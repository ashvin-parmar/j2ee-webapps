-- Smart Carbonix Database Schema
-- PostgreSQL Database Schema for Sustainability Platform

-- Users table for authentication and profile
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    profile_image_url VARCHAR(500),
    total_coins INTEGER DEFAULT 0,
    total_points INTEGER DEFAULT 0,
    level VARCHAR(50) DEFAULT 'Eco Beginner',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Session storage table
CREATE TABLE IF NOT EXISTS sessions (
    sid VARCHAR(255) PRIMARY KEY,
    sess JSONB NOT NULL,
    expire TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS IDX_session_expire ON sessions(expire);

-- Activities table for tracking sustainable actions
CREATE TABLE IF NOT EXISTS activities (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL, -- transport, food, energy, waste
    description TEXT NOT NULL,
    carbon_impact DECIMAL(10,2) DEFAULT 0, -- kg CO2 saved/produced
    coins_earned INTEGER DEFAULT 0,
    date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Achievements table for badges and milestones
CREATE TABLE IF NOT EXISTS achievements (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    badge_icon VARCHAR(100),
    coins_reward INTEGER DEFAULT 0,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Challenges table for community goals
CREATE TABLE IF NOT EXISTS challenges (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- daily, weekly, monthly
    target_value INTEGER NOT NULL,
    reward INTEGER DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User challenge progress
CREATE TABLE IF NOT EXISTS user_challenge_progress (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    challenge_id VARCHAR(36) NOT NULL REFERENCES challenges(id) ON DELETE CASCADE,
    current_value INTEGER DEFAULT 0,
    completed BOOLEAN DEFAULT false,
    completed_at TIMESTAMP,
    UNIQUE(user_id, challenge_id)
);

-- Waste bins for smart waste management
CREATE TABLE IF NOT EXISTS waste_bins (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    location VARCHAR(200) NOT NULL,
    type VARCHAR(50) NOT NULL, -- recycling, compost, electronic, general
    fill_level INTEGER DEFAULT 0, -- percentage 0-100
    status VARCHAR(50) DEFAULT 'online', -- online, offline, maintenance
    last_emptied TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Energy usage tracking
CREATE TABLE IF NOT EXISTS energy_usage (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    source VARCHAR(50) NOT NULL, -- solar, wind, grid
    usage DECIMAL(10,2) NOT NULL, -- kWh
    cost DECIMAL(10,2) DEFAULT 0,
    date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO challenges (name, description, type, target_value, reward, start_date, end_date, is_active) 
VALUES 
('Eco Transportation Week', 'Use eco-friendly transport 7 times this week', 'weekly', 7, 200, CURRENT_DATE, CURRENT_DATE + INTERVAL '7 days', true),
('Energy Saver Challenge', 'Reduce energy consumption by 10 activities', 'monthly', 10, 300, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', true),
('Waste Warrior', 'Properly sort 15 items this month', 'monthly', 15, 250, CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', true)
ON CONFLICT DO NOTHING;

INSERT INTO waste_bins (location, type, fill_level, status, last_emptied) 
VALUES 
('Main Campus - Building A', 'recycling', 45, 'online', CURRENT_TIMESTAMP - INTERVAL '2 days'),
('Main Campus - Building B', 'compost', 78, 'online', CURRENT_TIMESTAMP - INTERVAL '1 day'),
('Library Entrance', 'electronic', 23, 'online', CURRENT_TIMESTAMP - INTERVAL '3 days'),
('Cafeteria', 'general', 89, 'online', CURRENT_TIMESTAMP - INTERVAL '1 day'),
('Parking Lot A', 'recycling', 67, 'maintenance', CURRENT_TIMESTAMP - INTERVAL '4 days'),
('Student Center', 'compost', 34, 'online', CURRENT_TIMESTAMP - INTERVAL '2 days')
ON CONFLICT DO NOTHING;