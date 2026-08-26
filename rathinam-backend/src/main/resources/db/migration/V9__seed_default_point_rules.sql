-- Rathinam Toastmasters Digital Platform - Points Engine Domain Schema
-- Migration V9: Seed Default Point Rules

INSERT INTO point_rules (id, code, name, description, points, is_active, category, role_definition_id, created_at, updated_at, created_by, updated_by)
VALUES
    -- Attendance Rules
    (gen_random_uuid(), 'ATTENDANCE_PRESENT', 'Attendance Present', 'Awarded when a member attends a scheduled meeting', 5, true, 'ATTENDANCE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'ATTENDANCE_EXCUSED', 'Attendance Excused', 'Recorded when a member provides prior notice for missing a meeting', 0, true, 'ATTENDANCE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),

    -- Meeting Role Rules linked to role_definitions
    (gen_random_uuid(), 'ROLE_TOASTMASTER', 'Role: Toastmaster of the Day', 'Points for serving as Toastmaster of the Day', 10, true, 'ROLE', (SELECT id FROM role_definitions WHERE LOWER(name) = 'toastmaster of the day' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'ROLE_GENERAL_EVALUATOR', 'Role: General Evaluator', 'Points for serving as General Evaluator', 8, true, 'ROLE', (SELECT id FROM role_definitions WHERE LOWER(name) = 'general evaluator' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'ROLE_TABLE_TOPICS_MASTER', 'Role: Table Topics Master', 'Points for serving as Table Topics Master', 8, true, 'ROLE', (SELECT id FROM role_definitions WHERE LOWER(name) = 'table topics master' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'ROLE_TIMER', 'Role: Timer', 'Points for serving as Timer', 3, true, 'ROLE', (SELECT id FROM role_definitions WHERE LOWER(name) = 'timer' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'ROLE_AH_COUNTER', 'Role: Ah-Counter', 'Points for serving as Ah-Counter', 3, true, 'ROLE', (SELECT id FROM role_definitions WHERE LOWER(name) = 'ah-counter' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'ROLE_GRAMMARIAN', 'Role: Grammarian', 'Points for serving as Grammarian', 3, true, 'ROLE', (SELECT id FROM role_definitions WHERE LOWER(name) = 'grammarian' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'ROLE_SPEAKER', 'Role: Speaker', 'Points for delivering a prepared speech', 10, true, 'ROLE', (SELECT id FROM role_definitions WHERE LOWER(name) = 'speaker' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'ROLE_SPEECH_EVALUATOR', 'Role: Speech Evaluator', 'Points for providing speech evaluation', 8, true, 'ROLE', (SELECT id FROM role_definitions WHERE LOWER(name) = 'speech evaluator' LIMIT 1), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),

    -- Manual Bonus Rule
    (gen_random_uuid(), 'MANUAL_BONUS', 'Manual Adjustment / Bonus', 'Awarded or deducted manually by club officers for special contributions or corrections', 1, true, 'MANUAL', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM');
