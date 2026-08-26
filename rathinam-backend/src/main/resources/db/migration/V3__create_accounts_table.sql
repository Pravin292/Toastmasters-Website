-- V3__create_accounts_table.sql

CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Alter members table to add foreign key to accounts
ALTER TABLE members
    ADD CONSTRAINT fk_members_account_id
    FOREIGN KEY (account_id)
    REFERENCES accounts(id)
    ON DELETE SET NULL;
