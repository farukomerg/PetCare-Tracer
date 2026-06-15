UPDATE users SET password_hash = '$2a$10$lEUvS7oQv4YXOW1.gNl04OlNIt8AY65rwAqvaa6D1/htU/Z82bS6S' WHERE email IN ('ali@example.com', 'vet@example.com', 'ahmet@example.com');
UPDATE users SET user_role = 'VET' WHERE email IN ('drtest@vet.com', 'vet@example.com');
