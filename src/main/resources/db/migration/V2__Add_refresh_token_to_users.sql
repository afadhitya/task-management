-- Add refresh_token column to users table for logout functionality
ALTER TABLE users ADD COLUMN refresh_token VARCHAR(500);
