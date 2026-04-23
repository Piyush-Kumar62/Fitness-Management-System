-- MySQL Database Setup for Fitness Management System
-- Run this script to ensure the database and tables are properly set up

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS fitness_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fitness_db;

-- Check if users table exists
SELECT COUNT(*) as table_exists 
FROM information_schema.tables 
WHERE table_schema = 'fitness_db' 
AND table_name = 'users';

-- View users table structure
DESCRIBE users;

-- Check existing OAuth2 users
SELECT id, email, first_name, last_name, provider, provider_id, profile_image_url, created_at
FROM users
WHERE provider IN ('google', 'github')
ORDER BY created_at DESC
LIMIT 10;

-- Check all users
SELECT id, email, first_name, last_name, role, provider, created_at
FROM users
ORDER BY created_at DESC
LIMIT 10;
