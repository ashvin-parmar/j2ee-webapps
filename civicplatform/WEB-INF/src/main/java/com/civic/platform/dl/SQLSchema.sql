CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    location VARCHAR(100),
    join_date DATE NOT NULL,
    issues_reported INT DEFAULT 0,
    issues_resolved INT DEFAULT 0,
    community_validations INT DEFAULT 0,
    comments_posted INT DEFAULT 0,
    total_views INT DEFAULT 0,
    upvotes_received INT DEFAULT 0,
    account_level VARCHAR(50) DEFAULT 'Beginner',
    experience_points INT DEFAULT 0,
    next_level_points INT DEFAULT 1000,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO users (name, email, phone, location, join_date, issues_reported, issues_resolved, 
                  community_validations, comments_posted, total_views, upvotes_received,
                  account_level, experience_points, next_level_points)
VALUES 
('John Doe', 'john.doe@email.com', '+1234567890', 'Downtown District', '2024-01-15', 12, 8, 34, 67, 1543, 89, 'Community Champion', 2840, 3500),
('Jane Smith', 'jane.smith@email.com', '+0987654321', 'Uptown Area', '2024-02-20', 5, 3, 15, 23, 567, 34, 'Active Contributor', 1200, 2000); 
