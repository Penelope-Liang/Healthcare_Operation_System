-- Healthcare Operations Dashboard schema (MySQL 8+).
-- Usage:
--   mysql -u root -e "CREATE DATABASE IF NOT EXISTS healthops"
--   mysql -u root healthops < database/schema.sql
--   mysql -u root healthops < database/seed.sql
-- Re-running this file drops and recreates every table.

DROP TABLE IF EXISTS vaccination;
DROP TABLE IF EXISTS staff_assignment;
DROP TABLE IF EXISTS staff_credential;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS shipment;
DROP TABLE IF EXISTS spouse;
DROP TABLE IF EXISTS patient;
DROP TABLE IF EXISTS vaccine_lot;
DROP TABLE IF EXISTS clinic;
DROP TABLE IF EXISTS company;

-- Vaccine manufacturers.
CREATE TABLE company (
    name        VARCHAR(50) NOT NULL,
    street      VARCHAR(60),
    city        VARCHAR(40),
    province    VARCHAR(30),
    postal_code VARCHAR(10),
    PRIMARY KEY (name)
);

-- Vaccination sites.
CREATE TABLE clinic (
    name           VARCHAR(50) NOT NULL,
    street         VARCHAR(60) NOT NULL,
    city           VARCHAR(40) NOT NULL,
    province       VARCHAR(30) NOT NULL,
    postal_code    VARCHAR(10) NOT NULL,
    operating_date DATE        NOT NULL,
    PRIMARY KEY (name)
);

-- A batch of doses from one manufacturer.
CREATE TABLE vaccine_lot (
    lot             VARCHAR(12) NOT NULL,
    manufacturer    VARCHAR(50) NOT NULL,
    production_date DATE        NOT NULL,
    expiry_date     DATE        NOT NULL,
    doses           INT         NOT NULL,
    PRIMARY KEY (lot),
    CONSTRAINT fk_lot_company FOREIGN KEY (manufacturer) REFERENCES company (name),
    CONSTRAINT ck_lot_doses CHECK (doses >= 0),
    CONSTRAINT ck_lot_dates CHECK (expiry_date > production_date)
);

-- Which lots have been delivered to which clinics.
CREATE TABLE shipment (
    lot         VARCHAR(12) NOT NULL,
    clinic_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (lot, clinic_name),
    CONSTRAINT fk_shipment_lot FOREIGN KEY (lot) REFERENCES vaccine_lot (lot) ON DELETE CASCADE,
    CONSTRAINT fk_shipment_clinic FOREIGN KEY (clinic_name) REFERENCES clinic (name) ON DELETE CASCADE
);

-- Patients are identified by their OHIP number (0000-000-000).
CREATE TABLE patient (
    ohip          CHAR(12)    NOT NULL,
    first_name    VARCHAR(30) NOT NULL,
    last_name     VARCHAR(30) NOT NULL,
    date_of_birth DATE        NOT NULL,
    PRIMARY KEY (ohip)
);

-- At most one spouse per patient; the only place spouse data is stored.
CREATE TABLE spouse (
    patient_ohip CHAR(12)    NOT NULL,
    ohip         CHAR(12)    NOT NULL,
    first_name   VARCHAR(30),
    last_name    VARCHAR(30),
    phone        VARCHAR(15),
    PRIMARY KEY (patient_ohip),
    CONSTRAINT fk_spouse_patient FOREIGN KEY (patient_ohip) REFERENCES patient (ohip) ON DELETE CASCADE
);

-- Nurses and doctors share one table; role tells them apart.
CREATE TABLE staff (
    id         VARCHAR(5)              NOT NULL,
    role       ENUM ('nurse', 'doctor') NOT NULL,
    first_name VARCHAR(30)             NOT NULL,
    last_name  VARCHAR(30)             NOT NULL,
    PRIMARY KEY (id)
);

-- A staff member can hold several credentials (RN, BSN, MD, ...).
CREATE TABLE staff_credential (
    staff_id   VARCHAR(5)  NOT NULL,
    credential VARCHAR(20) NOT NULL,
    PRIMARY KEY (staff_id, credential),
    CONSTRAINT fk_credential_staff FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE CASCADE
);

-- A staff member can work at several clinics.
CREATE TABLE staff_assignment (
    staff_id    VARCHAR(5)  NOT NULL,
    clinic_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (staff_id, clinic_name),
    CONSTRAINT fk_assignment_staff FOREIGN KEY (staff_id) REFERENCES staff (id) ON DELETE CASCADE,
    CONSTRAINT fk_assignment_clinic FOREIGN KEY (clinic_name) REFERENCES clinic (name) ON DELETE CASCADE
);

-- One row per dose given. A patient can have several doses (boosters).
-- Medical history is protected: patients, clinics and lots with doses cannot be deleted.
CREATE TABLE vaccination (
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    patient_ohip CHAR(12)    NOT NULL,
    clinic_name  VARCHAR(50) NOT NULL,
    lot          VARCHAR(12) NOT NULL,
    given_on     DATE        NOT NULL,
    given_at     TIME        NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_vaccination_patient FOREIGN KEY (patient_ohip) REFERENCES patient (ohip) ON DELETE RESTRICT,
    CONSTRAINT fk_vaccination_clinic FOREIGN KEY (clinic_name) REFERENCES clinic (name) ON DELETE RESTRICT,
    CONSTRAINT fk_vaccination_lot FOREIGN KEY (lot) REFERENCES vaccine_lot (lot) ON DELETE RESTRICT
);

CREATE INDEX ix_vaccination_patient ON vaccination (patient_ohip, given_on);
CREATE INDEX ix_vaccination_date ON vaccination (given_on, given_at);
