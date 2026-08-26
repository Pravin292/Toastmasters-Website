-- Flyway Migration V11: Seed Default Achievements

INSERT INTO achievement_definitions (id, code, name, description, icon, category, criteria_type, criteria_threshold, is_repeatable, is_active, created_at, updated_at, created_by, updated_by)
VALUES
  ('a1111111-1111-1111-1111-111111111101', 'FIRST_MEETING', 'First Step', 'Attended your first Toastmasters meeting', 'footsteps', 'ATTENDANCE', 'ATTENDANCE_COUNT', 1, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
  ('a1111111-1111-1111-1111-111111111102', 'MEETINGS_5', 'Dedicated Attendee', 'Attended 5 Toastmasters meetings', 'calendar-check', 'ATTENDANCE', 'ATTENDANCE_COUNT', 5, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
  ('a1111111-1111-1111-1111-111111111103', 'MEETINGS_10', 'Club Pillar', 'Attended 10 Toastmasters meetings', 'building-columns', 'ATTENDANCE', 'ATTENDANCE_COUNT', 10, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
  ('a1111111-1111-1111-1111-111111111104', 'MEETINGS_25', 'Toastmasters Veteran', 'Attended 25 Toastmasters meetings', 'award-star', 'ATTENDANCE', 'ATTENDANCE_COUNT', 25, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
  ('a1111111-1111-1111-1111-111111111105', 'FIRST_ROLE', 'Stepping Up', 'Took on your first meeting role', 'user-check', 'MEETING_ROLES', 'ROLE_COUNT', 1, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
  ('a1111111-1111-1111-1111-111111111106', 'ROLES_10', 'Versatile Leader', 'Performed 10 meeting roles', 'users-gear', 'MEETING_ROLES', 'ROLE_COUNT', 10, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
  ('a1111111-1111-1111-1111-111111111107', 'POINTS_100', 'Century Achiever', 'Earned 100 total club points', 'coin-gold', 'POINTS', 'TOTAL_POINTS', 100, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
  ('a1111111-1111-1111-1111-111111111108', 'POINTS_250', 'High Performer', 'Earned 250 total club points', 'fire', 'POINTS', 'TOTAL_POINTS', 250, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
  ('a1111111-1111-1111-1111-111111111109', 'POINTS_500', 'Club Legend', 'Earned 500 total club points', 'crown-gold', 'POINTS', 'TOTAL_POINTS', 500, false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
  ('a1111111-1111-1111-1111-111111111110', 'MONTHLY_CHAMPION', 'Monthly Champion', 'Awarded Monthly Champion of Rathinam Toastmasters', 'trophy-champion', 'RANKING', 'MONTHLY_CHAMPION', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM');
