CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE pets (
    pet_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pet_name VARCHAR(80) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(80),
    gender VARCHAR(20) CHECK (gender IN ('MALE', 'FEMALE', 'UNKNOWN')),
    birth_date DATE,
    current_weight NUMERIC(5,2),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pets_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
);

CREATE TABLE health_records (
    health_record_id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    record_type VARCHAR(50) NOT NULL CHECK (record_type IN ('CHECKUP', 'DISEASE', 'ALLERGY', 'SURGERY', 'OTHER')),
    record_date DATE NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_health_pet
        FOREIGN KEY (pet_id) REFERENCES pets(pet_id)
        ON DELETE CASCADE
);

CREATE TABLE vaccines (
    vaccine_id BIGSERIAL PRIMARY KEY,
    vaccine_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    repeat_days INT CHECK (repeat_days >= 0)
);

CREATE TABLE vaccine_records (
    vaccine_record_id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    vaccine_id BIGINT NOT NULL,
    application_date DATE NOT NULL,
    next_due_date DATE,
    note TEXT,
    CONSTRAINT fk_vaccine_record_pet
        FOREIGN KEY (pet_id) REFERENCES pets(pet_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_vaccine_record_vaccine
        FOREIGN KEY (vaccine_id) REFERENCES vaccines(vaccine_id)
        ON DELETE RESTRICT
);

CREATE TABLE medications (
    medication_id BIGSERIAL PRIMARY KEY,
    medication_name VARCHAR(100) NOT NULL UNIQUE,
    form VARCHAR(50),
    description TEXT
);

CREATE TABLE medication_schedules (
    medication_schedule_id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    medication_id BIGINT NOT NULL,
    dosage_amount NUMERIC(6,2) NOT NULL CHECK (dosage_amount > 0),
    dosage_unit VARCHAR(20) NOT NULL,
    frequency_per_day INT NOT NULL CHECK (frequency_per_day > 0),
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    note TEXT,
    CONSTRAINT fk_med_schedule_pet
        FOREIGN KEY (pet_id) REFERENCES pets(pet_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_med_schedule_medication
        FOREIGN KEY (medication_id) REFERENCES medications(medication_id)
        ON DELETE RESTRICT
);

CREATE TABLE feeding_plans (
    feeding_plan_id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    food_name VARCHAR(100) NOT NULL,
    amount NUMERIC(6,2) NOT NULL CHECK (amount > 0),
    amount_unit VARCHAR(20) NOT NULL,
    meals_per_day INT NOT NULL CHECK (meals_per_day > 0),
    note TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_feeding_pet
        FOREIGN KEY (pet_id) REFERENCES pets(pet_id)
        ON DELETE CASCADE
);

CREATE TABLE appointments (
    appointment_id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    vet_name VARCHAR(100) NOT NULL,
    clinic_name VARCHAR(120),
    appointment_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED'
        CHECK (status IN ('PLANNED', 'COMPLETED', 'CANCELLED')),
    note TEXT,
    CONSTRAINT fk_appointment_pet
        FOREIGN KEY (pet_id) REFERENCES pets(pet_id)
        ON DELETE CASCADE
);

CREATE TABLE reminders (
    reminder_id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    reminder_type VARCHAR(30) NOT NULL
        CHECK (reminder_type IN ('VACCINE', 'MEDICATION', 'FEEDING', 'APPOINTMENT', 'GENERAL')),
    title VARCHAR(120) NOT NULL,
    remind_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'DONE', 'CANCELLED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reminder_pet
        FOREIGN KEY (pet_id) REFERENCES pets(pet_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_pets_user_id ON pets(user_id);
CREATE INDEX idx_health_records_pet_id ON health_records(pet_id);
CREATE INDEX idx_vaccine_records_pet_id ON vaccine_records(pet_id);
CREATE INDEX idx_medication_schedules_pet_id ON medication_schedules(pet_id);
CREATE INDEX idx_feeding_plans_pet_id ON feeding_plans(pet_id);
CREATE INDEX idx_appointments_pet_id ON appointments(pet_id);
CREATE INDEX idx_appointments_time ON appointments(appointment_time);
CREATE INDEX idx_reminders_pet_id ON reminders(pet_id);
CREATE INDEX idx_reminders_remind_at ON reminders(remind_at);
