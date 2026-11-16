-- Sample data for the Healthcare Operations Dashboard. Load after schema.sql.

INSERT INTO company (name, street, city, province, postal_code) VALUES
    ('Astrazeneca', '1004 Middlegate Rd', 'Mississauga', 'ON', 'L4Y 1M4'),
    ('Johnson & Johnson', '88 McNabb St', 'Markham', 'ON', 'L3R 5L2'),
    ('Moderna', 'One Upland Rd', 'Norwood', 'MA', '02062'),
    ('Pfizer', '400 Webro Rd', 'Parsippany', 'NJ', '07054');

INSERT INTO clinic (name, street, city, province, postal_code, operating_date) VALUES
    ('Pharmasave Division Pharmacy', '472 Division St', 'Kingston', 'ON', 'K7K 4B1', '2021-07-01'),
    ('Rexall', '240 Montreal St', 'Kingston', 'ON', 'K7K 3G8', '2021-06-17'),
    ('Shoppers Drug Mart', '445 Princess St', 'Kingston', 'ON', 'K7L 1C3', '2021-12-30'),
    ('CATP Kingston', '7 Hickson Ave', 'Kingston', 'ON', 'K7K 2N4', '2022-01-03'),
    ('Frontenac Medical Pharmacy', '789 Princess St', 'Kingston', 'ON', 'K7L 1E9', '2021-11-23'),
    ('Quarry Medical Pharmacy', '100 Princess St', 'Kingston', 'ON', 'K7L 1A7', '2022-02-05');

INSERT INTO vaccine_lot (lot, manufacturer, production_date, expiry_date, doses) VALUES
    ('AB1234', 'Astrazeneca', '2021-02-01', '2022-08-01', 1),
    ('CD2345', 'Johnson & Johnson', '2021-02-02', '2022-08-02', 2),
    ('EF3456', 'Moderna', '2021-02-03', '2022-08-03', 3),
    ('GH4567', 'Astrazeneca', '2021-02-04', '2022-08-04', 1),
    ('IJ5678', 'Johnson & Johnson', '2021-02-05', '2022-08-05', 2),
    ('KL6789', 'Moderna', '2021-02-06', '2022-08-06', 3);

INSERT INTO shipment (lot, clinic_name) VALUES
    ('AB1234', 'Pharmasave Division Pharmacy'),
    ('CD2345', 'Rexall'),
    ('EF3456', 'Shoppers Drug Mart'),
    ('GH4567', 'CATP Kingston'),
    ('IJ5678', 'Frontenac Medical Pharmacy'),
    ('KL6789', 'Quarry Medical Pharmacy');

INSERT INTO patient (ohip, first_name, last_name, date_of_birth) VALUES
    ('1029-293-345', 'Amy', 'White', '1990-01-23'),
    ('3424-091-654', 'John', 'Smith', '1991-02-24'),
    ('0903-434-120', 'Peter', 'Lee', '1992-03-25'),
    ('4523-123-787', 'Bob', 'Hill', '2000-04-21'),
    ('4323-898-432', 'Kitty', 'Wood', '2012-11-11'),
    ('4523-098-343', 'Lucy', 'Cooper', '1980-05-07');

INSERT INTO spouse (patient_ohip, ohip, first_name, last_name, phone) VALUES
    ('1029-293-345', '2342-543-798', 'Lily', 'Li', '613888123'),
    ('4523-098-343', '8234-923-444', 'Teddy', 'James', '613543675');

INSERT INTO staff (id, role, first_name, last_name) VALUES
    ('QW102', 'nurse', 'Jim', 'King'),
    ('ER345', 'nurse', 'Alex', 'Green'),
    ('TY143', 'nurse', 'Paul', 'Taylor'),
    ('UI089', 'nurse', 'Emma', 'Carter'),
    ('OP678', 'nurse', 'Helen', 'Roy'),
    ('AS234', 'nurse', 'Tim', 'Wang'),
    ('DF324', 'doctor', 'Kate', 'Gates'),
    ('GH090', 'doctor', 'Kiki', 'Johnson'),
    ('JK456', 'doctor', 'Whiskey', 'Brown'),
    ('LZ604', 'doctor', 'Alicia', 'Williams'),
    ('XC546', 'doctor', 'Cindy', 'Young'),
    ('VB980', 'doctor', 'Sherry', 'Fox');

INSERT INTO staff_credential (staff_id, credential) VALUES
    ('QW102', 'RN'), ('ER345', 'BSN'), ('TY143', 'CCRN'), ('UI089', 'CCRN'), ('OP678', 'BSN'), ('AS234', 'RN'),
    ('DF324', 'MD'), ('GH090', 'DO'), ('JK456', 'DNP'), ('LZ604', 'PA'), ('XC546', 'CNNP'), ('VB980', 'MBBS');

INSERT INTO staff_assignment (staff_id, clinic_name) VALUES
    ('QW102', 'Pharmasave Division Pharmacy'),
    ('ER345', 'Rexall'),
    ('TY143', 'Shoppers Drug Mart'),
    ('UI089', 'CATP Kingston'),
    ('OP678', 'Frontenac Medical Pharmacy'),
    ('AS234', 'Quarry Medical Pharmacy'),
    ('DF324', 'Pharmasave Division Pharmacy'),
    ('GH090', 'Rexall'),
    ('JK456', 'Shoppers Drug Mart'),
    ('LZ604', 'CATP Kingston'),
    ('XC546', 'Frontenac Medical Pharmacy'),
    ('XC546', 'Quarry Medical Pharmacy');

INSERT INTO vaccination (patient_ohip, clinic_name, lot, given_on, given_at) VALUES
    ('1029-293-345', 'Pharmasave Division Pharmacy', 'AB1234', '2021-12-21', '10:00'),
    ('3424-091-654', 'Rexall', 'CD2345', '2021-11-19', '13:00'),
    ('0903-434-120', 'Shoppers Drug Mart', 'EF3456', '2021-11-04', '14:23'),
    ('4523-123-787', 'CATP Kingston', 'GH4567', '2022-01-01', '09:25'),
    ('4323-898-432', 'Frontenac Medical Pharmacy', 'IJ5678', '2022-01-23', '13:35'),
    ('4523-098-343', 'Quarry Medical Pharmacy', 'KL6789', '2021-09-15', '16:50');
