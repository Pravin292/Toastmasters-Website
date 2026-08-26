-- Rathinam Toastmasters Digital Platform - Points Engine Domain Schema
-- Migration V8: Create Point Rules and Point Events Tables

CREATE TABLE point_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    points INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    category VARCHAR(30) NOT NULL,
    role_definition_id UUID NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    CONSTRAINT fk_point_rules_role FOREIGN KEY (role_definition_id) REFERENCES role_definitions(id) ON DELETE RESTRICT
);

-- Case-insensitive unique constraint on point rule code
CREATE UNIQUE INDEX idx_point_rules_code_lower ON point_rules (LOWER(code));

CREATE TABLE point_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id UUID NOT NULL,
    meeting_id UUID NULL,
    point_rule_id UUID NULL,
    points INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_id UUID NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    CONSTRAINT fk_point_events_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE RESTRICT,
    CONSTRAINT fk_point_events_meeting FOREIGN KEY (meeting_id) REFERENCES meetings(id) ON DELETE RESTRICT,
    CONSTRAINT fk_point_events_rule FOREIGN KEY (point_rule_id) REFERENCES point_rules(id) ON DELETE RESTRICT
);

-- Idempotency constraint: Prevent duplicate automatic awards for the same source
CREATE UNIQUE INDEX idx_point_events_source_unique ON point_events (source_type, source_id) WHERE source_id IS NOT NULL;

-- Performance Indexes
CREATE INDEX idx_point_events_member_id ON point_events (member_id);
CREATE INDEX idx_point_events_meeting_id ON point_events (meeting_id);
CREATE INDEX idx_point_events_created_at ON point_events (created_at);
CREATE INDEX idx_point_events_member_created ON point_events (member_id, created_at);
