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
  skills text, -- Nullable concatenated string e.g. "dancing:cooking"
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
-- 5. BANNERS TABLE
-- ============================================
create table banners (
  id bigint generated always as identity primary key,
  description text not null default '',
  image_path text not null,
  event_id bigint references calendar_events(id) on delete set null,
  version integer not null default 1
);


-- ============================================
-- 6. SKILLS TABLE
-- ============================================
create table skills (
  id bigint generated always as identity primary key,
  name text not null unique
);


-- ============================================
-- 7. EVENT_SKILLS TABLE (Many-to-Many)
-- ============================================
create table event_skills (
  event_id bigint not null references calendar_events(id) on delete cascade,
  skill_id bigint not null references skills(id) on delete cascade,
  primary key (event_id, skill_id)
);


-- ============================================
-- 8. ENABLE ROW LEVEL SECURITY
-- ============================================
alter table users enable row level security;
alter table calendar_events enable row level security;
alter table event_assignments enable row level security;
alter table remote_config enable row level security;
alter table banners enable row level security;
alter table skills enable row level security;
alter table event_skills enable row level security;


-- ============================================
-- 9. GENERAL READ POLICIES
-- ============================================

-- Everyone can read config, events, banners, skills, and links
create policy "read all config" on remote_config for select to anon, authenticated using (true);
create policy "read all events" on calendar_events for select to anon, authenticated using (true);
create policy "read all banners" on banners for select to anon, authenticated using (true);
create policy "read all skills" on skills for select to anon, authenticated using (true);
create policy "read all event_skills" on event_skills for select to anon, authenticated using (true);

-- users: each user can read only their own profile
create policy "read own user row" on users for select to authenticated using (auth.uid() = id);

-- event_assignments: users can manage their own assignments
create policy "read own assignments" on event_assignments for select to authenticated using (auth.uid() = user_id);
create policy "insert own assignments" on event_assignments for insert to authenticated with check (auth.uid() = user_id);
create policy "update own assignments" on event_assignments for update to authenticated using (auth.uid() = user_id);
create policy "delete own assignments" on event_assignments for delete to authenticated using (auth.uid() = user_id);


-- ============================================
-- 10. ADMIN ROLES
-- ============================================
create table admin_roles (
  user_id uuid not null references users(id) on delete cascade,
  role text not null check (role in ('operator', 'maintainer')),
  primary key (user_id, role)
);

alter table admin_roles enable row level security;

create policy "read own admin roles" on admin_roles for select to authenticated using (auth.uid() = user_id);


-- ============================================
-- 11. OPERATOR POLICIES
-- ============================================

-- View all assignments & user profiles for review
create policy "operators read all assignments" on event_assignments for select to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'operator'));

create policy "operators can read all users" on users for select to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'operator'));

-- Manage assignments (Approve/Reject)
create policy "operators update any assignment" on event_assignments for update to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'operator'));

create policy "operators delete any assignment" on event_assignments for delete to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'operator'));

-- Manage events
create policy "operators insert events" on calendar_events for insert to authenticated
with check (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'operator'));

create policy "operators update events" on calendar_events for update to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'operator'));

create policy "operators delete events" on calendar_events for delete to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'operator'));

-- Manage banners
create policy "operators manage banners" on banners for all to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'operator'));

-- Manage skills & linking
create policy "operators manage skills" on skills for all to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'operator'));

create policy "operators manage event_skills" on event_skills for all to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'operator'));

-- Manage banner storage
create policy "public read banners" on storage.objects for select to anon, authenticated using (bucket_id = 'banners');
create policy "operators manage banners storage" on storage.objects for all to authenticated
using (bucket_id = 'banners' and exists (select 1 from public.admin_roles where user_id = auth.uid() and role = 'operator'));


-- ============================================
-- 12. MAINTAINER POLICIES
-- ============================================

-- Maintainers manage config & all user data
create policy "maintainers modify remote_config" on remote_config for all to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'maintainer'));

create policy "maintainers manage all users" on users for all to authenticated
using (exists (select 1 from admin_roles where user_id = auth.uid() and role = 'maintainer'));
