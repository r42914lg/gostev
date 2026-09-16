INSERT INTO calendar_events (date, name, is_available) VALUES
  ('2026-09-10', 'Team meeting', true),
  ('2026-09-12', 'On-call shift', true),
  ('2026-09-15', 'Holiday', false);

INSERT INTO calendar_events (date, name, is_available) VALUES
  ('2026-09-12', 'System Maintenance', true),
  ('2026-09-12', 'DB Backup', true),
  ('2026-09-12', 'Security Audit', true);


-- ============================================
-- TEST DATA: admin_roles (Operator / Maintainer)
-- ============================================
-- admin_roles.user_id has a foreign key into auth.users, so these
-- inserts will fail with a foreign key violation until the UUIDs
-- below are real user IDs. Before running this section:
--   1. Supabase dashboard → Authentication → Users → Add user,
--      once per test account (e.g. test-operator@example.com,
--      test-maintainer@example.com, test-both@example.com).
--   2. Copy each UID from that screen and replace the matching
--      placeholder UUID below.
-- The trigger in init.sql auto-creates a matching public.users row
-- for each one, so no separate insert into users is needed.

INSERT INTO admin_roles (user_id, role) VALUES
  ('831f2515-5b46-4f77-afbc-0d3d308f13d0', 'operator'),   -- REPLACE: test-operator
  ('51044cb8-9032-4249-858e-9139cbe7f4c5', 'maintainer'), -- REPLACE: test-maintainer
  ('c413dd16-fa26-454a-a8b7-26ec861c18e2', 'operator'),   -- REPLACE: test-both
  ('c413dd16-fa26-454a-a8b7-26ec861c18e2', 'maintainer'); -- REPLACE: test-both (same user, second role)


-- ============================================
-- TEST DATA: remote_config
-- ============================================
INSERT INTO remote_config (key, value) VALUES
  ('min_app_version', '1');