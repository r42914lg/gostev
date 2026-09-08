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
-- 4. ENABLE ROW LEVEL SECURITY
-- ============================================
alter table users enable row level security;
alter table calendar_events enable row level security;
alter table event_assignments enable row level security;


-- ============================================
-- 5. POLICIES
-- ============================================

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