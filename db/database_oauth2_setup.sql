-- ============================================
-- MySQL Database Schema for OAuth2 Support
-- Fitness Management System
-- ============================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS fitness_db 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

USE fitness_db;

-- ============================================
-- 1. Users Table (OAuth2 Support)
-- ============================================

-- Check if users table exists
SELECT 
  COLUMN_NAME, 
  DATA_TYPE, 
  IS_NULLABLE, 
  COLUMN_KEY,
  COLUMN_DEFAULT
FROM 
  INFORMATION_SCHEMA.COLUMNS
WHERE 
  TABLE_SCHEMA = 'fitness_db' 
  AND TABLE_NAME = 'users';

-- Add OAuth2 columns if they don't exist
-- Run these commands one by one if columns are missing:

-- Add provider column (google, github, local)
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS provider VARCHAR(50) 
DEFAULT 'local';

-- Add provider_id column (OAuth2 provider user ID)
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS provider_id VARCHAR(255);

-- Add profile_image_url column
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(500);

-- Make password nullable for OAuth2 users
ALTER TABLE users 
MODIFY COLUMN password VARCHAR(255) NULL;

-- Update role column to ensure it has default value
ALTER TABLE users 
MODIFY COLUMN role VARCHAR(50) NOT NULL DEFAULT 'MEMBER';

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_provider ON users(provider);
CREATE INDEX IF NOT EXISTS idx_users_provider_id ON users(provider_id);

-- ============================================
-- 2. Verify Table Structure
-- ============================================

DESCRIBE users;

-- ============================================
-- 3. Check Existing Data
-- ============================================

SELECT 
  id,
  email,
  first_name,
  last_name,
  role,
  provider,
  provider_id,
  created_at
FROM 
  users
LIMIT 10;

-- ============================================
-- 4. Update Existing Users (if needed)
-- ============================================

-- Update existing users to have 'local' provider
UPDATE users 
SET provider = 'local' 
WHERE provider IS NULL OR provider = '';

-- ============================================
-- 5. Constraints and Validations
-- ============================================

-- Ensure email is unique
-- Note: This should already exist from JPA, but verify:
SHOW INDEXES FROM users WHERE Column_name = 'email';

-- ============================================
-- 6. Test Queries
-- ============================================

-- Test finding user by email (used in OAuth2 flow)
SELECT * FROM users WHERE email = 'test@example.com';

-- Test finding user by provider and provider_id
SELECT * FROM users WHERE provider = 'google' AND provider_id = '123456789';

-- Count users by provider
SELECT provider, COUNT(*) as count 
FROM users 
GROUP BY provider;
