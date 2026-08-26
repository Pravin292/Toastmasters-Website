-- Rathinam Toastmasters Digital Platform - Meeting Roles Domain Schema
-- Migration V6: Create Role Definitions and Meeting Role Assignments Tables

CREATE TABLE role_definitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL
);

-- Case-insensitive unique constraint on role definition name
CREATE UNIQUE INDEX idx_role_definitions_name_lower ON role_definitions (LOWER(name));

CREATE TABLE meeting_role_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meeting_id UUID NOT NULL,
    role_definition_id UUID NOT NULL,
    member_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    CONSTRAINT fk_assignments_meeting FOREIGN KEY (meeting_id) REFERENCES meetings(id) ON DELETE RESTRICT,
    CONSTRAINT fk_assignments_role FOREIGN KEY (role_definition_id) REFERENCES role_definitions(id) ON DELETE RESTRICT,
    CONSTRAINT fk_assignments_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE RESTRICT
);

-- Rule #1: Member can perform only ONE role per meeting
CREATE UNIQUE INDEX idx_meeting_role_assignments_meeting_member_unique ON meeting_role_assignments (meeting_id, member_id);

-- Rule #2: Specific role can be assigned only ONCE per meeting
CREATE UNIQUE INDEX idx_meeting_role_assignments_meeting_role_unique ON meeting_role_assignments (meeting_id, role_definition_id);

-- Performance Indexes
CREATE INDEX idx_meeting_role_assignments_meeting_id ON meeting_role_assignments (meeting_id);
CREATE INDEX idx_meeting_role_assignments_member_id ON meeting_role_assignments (member_id);
CREATE INDEX idx_meeting_role_assignments_role_id ON meeting_role_assignments (role_definition_id);
