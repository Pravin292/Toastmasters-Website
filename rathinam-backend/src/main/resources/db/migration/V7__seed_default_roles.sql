-- Rathinam Toastmasters Digital Platform - Meeting Roles Domain Schema
-- Migration V7: Seed Default Common Toastmasters Roles

INSERT INTO role_definitions (id, name, description, is_active, created_at, updated_at, created_by, updated_by)
VALUES
    (gen_random_uuid(), 'Toastmaster of the Day', 'Main host and master of ceremonies for the meeting', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'General Evaluator', 'Evaluates everything that takes place during the meeting', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'Table Topics Master', 'Prepares and conducts the impromptu speaking session', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'Timer', 'Keeps track of time for speeches and meeting segments', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'Ah-Counter', 'Notes crutch words, filler words, and pauses', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'Grammarian', 'Introduces Word of the Day and tracks language usage', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'Speaker', 'Delivers a prepared speech from the Pathways curriculum', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM'),
    (gen_random_uuid(), 'Speech Evaluator', 'Provides oral and written feedback to a prepared speaker', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'SYSTEM', 'SYSTEM');
