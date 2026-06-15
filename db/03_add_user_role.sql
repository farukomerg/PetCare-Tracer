-- Add user_role column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS user_role VARCHAR(10) NOT NULL DEFAULT 'USER'
    CHECK (user_role IN ('USER', 'VET'));

-- Seed a test veterinarian
INSERT INTO users (full_name, email, password_hash, phone, user_role)
VALUES ('Dr. Ayse Kaya', 'vet@example.com', '$2a$10$VxjVj/DP2CPggQwOQKcR7.ORZospBtDWvXbmpRsaRwm4EmnA59OdC', '05551234567', 'VET')
ON CONFLICT (email) DO NOTHING;
