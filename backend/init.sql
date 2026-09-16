-- ============================================
-- 1. USERS TABLE (mirrors auth.users)
-- ============================================
create table users (
  id uuid primary key references auth.users(id) on delete cascade,
  name text not null default ''
);

-- Auto-create a row in public.users whenever someone signs up
create function public.handle_new_user()
returns trigger as $$
begin
  insert into public.users (id, name)
  values (new.id, coalesce(new.raw_user_meta_data->>'name', ''));
  return new;
end;
$$ language plpgsql security definer;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();


-- ============================================
-- 2. CALENDAR_EVENTS TABLE
-- ============================================
create table calendar_events (
  id bigint generated always as identity primary key,
  date date not null,
  name text not null default '',
  is_available boolean not null default false
);


-- ============================================
-- 3. EVENT_ASSIGNMENTS TABLE
-- ============================================
create table event_assignments (
  user_id uuid not null references users(id) on delete cascade,
  event_id bigint not null references calendar_events(id) on delete cascade,
  status text not null default 'APPLIED' check (status in ('APPLIED', 'CONFIRMED')),
  primary key (user_id, event_id)
);


-- ============================================
-- 4. REMOTE_CONFIG TABLE
-- ============================================
create table remote_config (
  key text primary key,
  value text not null
);


-- ============================================
-- 5. ENABLE ROW LEVEL SECURITY
-- ============================================
alter table users enable row level security;
alter table calendar_events enable row level security;
alter table event_assignments enable row level security;
alter table remote_config enable row level security;


-- ============================================
-- 6. POLICIES
-- ============================================

-- remote_config: everyone can read, including logged-out (anon) users
create policy "read all config"
on remote_config for select
to anon, authenticated
using (true);

-- calendar_events: everyone can read, including logged-out (anon) users
create policy "read all events"
on calendar_events for select
to anon, authenticated
using (true);

-- users: each user can read only their own name/row
create policy "read own user row"
on users for select
to authenticated
using (auth.uid() = id);

-- event_assignments: users can read only their own assignments
create policy "read own assignments"
on event_assignments for select
to authenticated
using (auth.uid() = user_id);

-- event_assignments: users can insert only their own assignments
create policy "insert own assignments"
on event_assignments for insert
to authenticated
with check (auth.uid() = user_id);

-- event_assignments: users can update only their own assignments
create policy "update own assignments"
on event_assignments for update
to authenticated
using (auth.uid() = user_id);

-- event_assignments: users can delete only their own assignments (optional, add if needed)
create policy "delete own assignments"
on event_assignments for delete
to authenticated
using (auth.uid() = user_id);


-- ============================================
-- 7. ADMIN ROLES (Operator / Maintainer)
-- ============================================
-- Two independent roles for the web admin panel. A user can hold
-- 'operator', 'maintainer', both, or neither. Roles are assigned
-- manually (see bottom of this file), never by self-service — this
-- is separate from, and has no effect on, the normal Android app
-- sign-up flow in section 1 above.

create table admin_roles (
  user_id uuid not null references auth.users(id) on delete cascade,
  role text not null check (role in ('operator', 'maintainer')),
  primary key (user_id, role)
);

alter table admin_roles enable row level security;

-- A signed-in user can see only their own role rows — enough for
-- the admin panel to ask "what am I allowed to do?" without
-- exposing the full allowlist to every logged-in user.
create policy "read own admin roles"
on admin_roles for select
to authenticated
using (auth.uid() = user_id);


-- ============================================
-- 8. OPERATOR POLICIES
-- ============================================
-- "Requests" are rows in event_assignments with status = 'APPLIED'.
-- Approve = update status to 'CONFIRMED'. Reject = delete the row,
-- since the status check constraint above has no 'REJECTED' value —
-- a rejected request simply isn't stored. If you'd rather keep a
-- record of rejections, add 'REJECTED' to the check constraint in
-- section 3 and change the admin panel to update instead of delete.

-- Operators need to see every user's assignments, not just their own,
-- to review requests across the whole app.
create policy "operators read all assignments"
on event_assignments for select
to authenticated
using (
  exists (
    select 1 from admin_roles
    where admin_roles.user_id = auth.uid() and admin_roles.role = 'operator'
  )
);

-- Approve a request.
create policy "operators update any assignment"
on event_assignments for update
to authenticated
using (
  exists (
    select 1 from admin_roles
    where admin_roles.user_id = auth.uid() and admin_roles.role = 'operator'
  )
);

-- Reject a request (delete it).
create policy "operators delete any assignment"
on event_assignments for delete
to authenticated
using (
  exists (
    select 1 from admin_roles
    where admin_roles.user_id = auth.uid() and admin_roles.role = 'operator'
  )
);

-- Add a new event. calendar_events currently has no insert policy at
-- all, so this is the first thing that grants write access to it.
create policy "operators insert events"
on calendar_events for insert
to authenticated
with check (
  exists (
    select 1 from admin_roles
    where admin_roles.user_id = auth.uid() and admin_roles.role = 'operator'
  )
);

-- Toggle is_available on an existing event.
create policy "operators update events"
on calendar_events for update
to authenticated
using (
  exists (
    select 1 from admin_roles
    where admin_roles.user_id = auth.uid() and admin_roles.role = 'operator'
  )
);

-- Delete an event (cascades to its event_assignments rows via the
-- foreign key in section 3).
create policy "operators delete events"
on calendar_events for delete
to authenticated
using (
  exists (
    select 1 from admin_roles
    where admin_roles.user_id = auth.uid() and admin_roles.role = 'operator'
  )
);


-- ============================================
-- 9. MAINTAINER POLICIES
-- ============================================
-- remote_config currently has no write policy either — this grants
-- insert/update/delete to Maintainers only.
create policy "maintainers modify remote_config"
on remote_config for all
to authenticated
using (
  exists (
    select 1 from admin_roles
    where admin_roles.user_id = auth.uid() and admin_roles.role = 'maintainer'
  )
)
with check (
  exists (
    select 1 from admin_roles
    where admin_roles.user_id = auth.uid() and admin_roles.role = 'maintainer'
  )
);

-- ============================================
-- 10. OPERATOR USERS SHOULD read all user's registration requests
-- ============================================
create policy "operators can read all users"
on users for select
to authenticated
using (
  exists (
    select 1 from admin_roles
    where admin_roles.user_id = auth.uid() and admin_roles.role = 'operator'
  )
);


-- ============================================
-- 11. ASSIGN ROLES (run manually per admin, after
--     this script — needs real user UUIDs to exist)
-- ============================================
-- 1. Get the user's UID from Authentication → Users in the Supabase
--    dashboard (they sign up normally through the Android app, or
--    you create an account from that screen — no separate admin
--    login is created).
-- 2. Run one insert per role you want to grant, e.g.:
--
--    insert into admin_roles (user_id, role) values ('paste-uid-here', 'operator');
--    insert into admin_roles (user_id, role) values ('paste-uid-here', 'maintainer');
--
--    (insert both rows for someone who should have both roles)
