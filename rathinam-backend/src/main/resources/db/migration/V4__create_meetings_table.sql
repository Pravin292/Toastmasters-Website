-- Rathinam Toastmasters Digital Platform - Meeting Domain Schema
-- Migration V4: Create Meetings Table and Indexes

CREATE TABLE meetings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meeting_number INTEGER NOT NULL,
    meeting_start TIMESTAMPTZ NOT NULL,
    meeting_end TIMESTAMPTZ NULL,
    theme VARCHAR(255) NULL,
    meeting_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    location VARCHAR(255) NULL,
    meeting_url VARCHAR(500) NULL,
    description TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL
);

-- Unique constraint on meeting_number
CREATE UNIQUE INDEX idx_meetings_number_unique ON meetings (meeting_number);

-- Index on status for filtering meetings
CREATE INDEX idx_meetings_status ON meetings (status);

-- Index on meeting_start for timeline queries
CREATE INDEX idx_meetings_start ON meetings (meeting_start);
