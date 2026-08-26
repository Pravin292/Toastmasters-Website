-- Flyway Migration V10: Create Achievements, Member Achievements, and Certificates Tables

CREATE TABLE achievement_definitions (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    icon VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    criteria_type VARCHAR(50) NOT NULL,
    criteria_threshold INTEGER,
    is_repeatable BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE UNIQUE INDEX idx_achievement_definitions_code_lower ON achievement_definitions (LOWER(code));
CREATE INDEX idx_achievement_definitions_category ON achievement_definitions (category);

CREATE TABLE member_achievements (
    id UUID PRIMARY KEY,
    member_id UUID NOT NULL REFERENCES members(id) ON DELETE RESTRICT,
    achievement_definition_id UUID NOT NULL REFERENCES achievement_definitions(id) ON DELETE RESTRICT,
    earned_at TIMESTAMPTZ NOT NULL,
    meeting_id UUID REFERENCES meetings(id) ON DELETE RESTRICT,
    reason TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE INDEX idx_member_achievements_member ON member_achievements (member_id);
CREATE INDEX idx_member_achievements_definition ON member_achievements (achievement_definition_id);
CREATE UNIQUE INDEX idx_member_achievements_unique_non_repeatable ON member_achievements (member_id, achievement_definition_id) WHERE meeting_id IS NULL;
CREATE UNIQUE INDEX idx_member_achievements_unique_meeting ON member_achievements (member_id, achievement_definition_id, meeting_id) WHERE meeting_id IS NOT NULL;

CREATE TABLE certificates (
    id UUID PRIMARY KEY,
    certificate_number VARCHAR(50) NOT NULL,
    member_id UUID NOT NULL REFERENCES members(id) ON DELETE RESTRICT,
    certificate_type VARCHAR(50) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    issued_date TIMESTAMPTZ NOT NULL,
    achievement_id UUID REFERENCES member_achievements(id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL DEFAULT 'ISSUED',
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE UNIQUE INDEX idx_certificates_number_lower ON certificates (LOWER(certificate_number));
CREATE INDEX idx_certificates_member ON certificates (member_id);
