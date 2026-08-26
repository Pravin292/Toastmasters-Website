-- Rathinam Toastmasters Digital Platform - Attendance Domain Schema
-- Migration V5: Create Attendance Table and Indexes

CREATE TABLE attendance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meeting_id UUID NOT NULL,
    member_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    check_in_time TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL,
    CONSTRAINT fk_attendance_meeting FOREIGN KEY (meeting_id) REFERENCES meetings(id) ON DELETE RESTRICT,
    CONSTRAINT fk_attendance_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE RESTRICT
);

-- Unique constraint preventing duplicate attendance per member per meeting
CREATE UNIQUE INDEX idx_attendance_meeting_member_unique ON attendance (meeting_id, member_id);

-- Indexes for meeting and member attendance queries
CREATE INDEX idx_attendance_meeting_id ON attendance (meeting_id);
CREATE INDEX idx_attendance_member_id ON attendance (member_id);
CREATE INDEX idx_attendance_status ON attendance (status);
