-- Rathinam Toastmasters Digital Platform - Member Domain Schema
-- Migration V2: Create Members Table and Indexes

CREATE TABLE members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    display_name VARCHAR(200) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NULL,
    profile_picture_url VARCHAR(500) NULL,
    join_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    bio TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NULL,
    updated_by VARCHAR(100) NULL
);

-- Case-insensitive unique constraint on email
CREATE UNIQUE INDEX idx_members_email_lower ON members (LOWER(email));

-- Index on member status for filtering active/inactive members
CREATE INDEX idx_members_status ON members (status);
