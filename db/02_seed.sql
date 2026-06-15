INSERT INTO users (full_name, email, password_hash, phone, user_role)
VALUES 
('Ahmet Yilmaz', 'ahmet@example.com', '$2a$10$lEUvS7oQv4YXOW1.gNl04OlNIt8AY65rwAqvaa6D1/htU/Z82bS6S', '05550000000', 'USER'),
('Dr. Ayse Kaya', 'vet@example.com', '$2a$10$lEUvS7oQv4YXOW1.gNl04OlNIt8AY65rwAqvaa6D1/htU/Z82bS6S', '05551234567', 'VET');

INSERT INTO pets (user_id, pet_name, species, breed, gender, birth_date, current_weight, notes)
VALUES (1, 'Boncuk', 'Kedi', 'British Shorthair', 'FEMALE', '2022-03-10', 4.20, 'Sakin yapili');

INSERT INTO vaccines (vaccine_name, description, repeat_days)
VALUES
('Kuduz', 'Kuduz asisi', 365),
('Karma', 'Karma asisi', 365);

INSERT INTO medications (medication_name, form, description)
VALUES
('Antibiyotik A', 'Tablet', 'Genel kullanim'),
('Vitamin Surubu', 'Syrup', 'Destek takviyesi');

INSERT INTO vaccine_records (pet_id, vaccine_id, application_date, next_due_date, note)
VALUES (1, 1, '2026-05-10', '2027-05-10', 'Sorunsuz uygulandi');

INSERT INTO feeding_plans (pet_id, food_name, amount, amount_unit, meals_per_day, note)
VALUES (1, 'Tavuklu mama', 80, 'gram', 2, 'Sabah ve aksam');

INSERT INTO appointments (pet_id, vet_id, vet_name, clinic_name, appointment_time, status, note)
VALUES (1, 2, 'Dr. Ayse Kaya', 'Patiler Klinigi', '2026-05-20 14:00:00', 'PLANNED', 'Genel kontrol');

INSERT INTO reminders (pet_id, reminder_type, title, remind_at, status)
VALUES (1, 'VACCINE', 'Kuduz asisi kontrolu', '2027-05-01 09:00:00', 'PENDING');
