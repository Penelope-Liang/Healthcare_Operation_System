-- One-off migration from the legacy `covidDB` schema into `healthops`.
-- Only needed if you have an existing covidDB database with real data.
--
--   mysql -u root -e "CREATE DATABASE IF NOT EXISTS healthops"
--   mysql -u root healthops < database/schema.sql
--   mysql -u root healthops < database/migrate-from-covidDB.sql
--
-- Dates like '01 JULY 2021' are normalised to a 3-letter month first, because STR_TO_DATE
-- raises an error (rather than returning NULL) for unparseable input in strict mode.
--
-- What it fixes on the way:
--   * text dates in mixed formats ('2021-12-21', '01 AUG 2022', '01 JULY 2021') become DATE values
--   * spouse OHIPs stored as the string 'NULL' are dropped; Patient.SpouseOHIP and Spouse are merged
--   * Nurse*/Doctor* tables are merged into staff, staff_credential and staff_assignment
--   * stray whitespace and trailing commas in addresses are trimmed
--   * the unused Practice table is not carried over

INSERT INTO company (name, street, city, province, postal_code)
SELECT TRIM(Name), TRIM(Street), TRIM(TRAILING ',' FROM TRIM(City)), TRIM(Prov), TRIM(PC)
FROM covidDB.Company;

INSERT INTO clinic (name, street, city, province, postal_code, operating_date)
SELECT TRIM(Name), TRIM(Street), TRIM(TRAILING ',' FROM TRIM(City)), TRIM(Prov), TRIM(PC),
       CASE WHEN TRIM(date) REGEXP '^[0-9]{4}-' THEN STR_TO_DATE(TRIM(date), '%Y-%m-%d')
            ELSE STR_TO_DATE(CONCAT(SUBSTRING_INDEX(TRIM(date), ' ', 1), ' ',
                                    LEFT(SUBSTRING_INDEX(SUBSTRING_INDEX(TRIM(date), ' ', 2), ' ', -1), 3), ' ',
                                    SUBSTRING_INDEX(TRIM(date), ' ', -1)), '%d %b %Y') END
FROM covidDB.VaxClinic;

INSERT INTO vaccine_lot (lot, manufacturer, production_date, expiry_date, doses)
SELECT TRIM(Lot), TRIM(CompanyName),
       CASE WHEN TRIM(Prodcution) REGEXP '^[0-9]{4}-' THEN STR_TO_DATE(TRIM(Prodcution), '%Y-%m-%d')
            ELSE STR_TO_DATE(CONCAT(SUBSTRING_INDEX(TRIM(Prodcution), ' ', 1), ' ',
                                    LEFT(SUBSTRING_INDEX(SUBSTRING_INDEX(TRIM(Prodcution), ' ', 2), ' ', -1), 3), ' ',
                                    SUBSTRING_INDEX(TRIM(Prodcution), ' ', -1)), '%d %b %Y') END,
       CASE WHEN TRIM(Expiry) REGEXP '^[0-9]{4}-' THEN STR_TO_DATE(TRIM(Expiry), '%Y-%m-%d')
            ELSE STR_TO_DATE(CONCAT(SUBSTRING_INDEX(TRIM(Expiry), ' ', 1), ' ',
                                    LEFT(SUBSTRING_INDEX(SUBSTRING_INDEX(TRIM(Expiry), ' ', 2), ' ', -1), 3), ' ',
                                    SUBSTRING_INDEX(TRIM(Expiry), ' ', -1)), '%d %b %Y') END,
       COALESCE(Doses, 0)
FROM covidDB.Vaccine;

INSERT INTO shipment (lot, clinic_name)
SELECT TRIM(Lots), TRIM(Clinic) FROM covidDB.ShipTo;

INSERT INTO patient (ohip, first_name, last_name, date_of_birth)
SELECT OHIP, TRIM(FirstName), TRIM(LastName), STR_TO_DATE(TRIM(DOB), '%Y-%m-%d')
FROM covidDB.Patient;

-- Spouses with full details first, then any spouse OHIP that only existed on Patient.
INSERT INTO spouse (patient_ohip, ohip, first_name, last_name, phone)
SELECT PatientOHIP, OHIP, TRIM(FirstName), TRIM(LastName), TRIM(Phone)
FROM covidDB.Spouse;

INSERT INTO spouse (patient_ohip, ohip)
SELECT p.OHIP, p.SpouseOHIP
FROM covidDB.Patient p
WHERE p.SpouseOHIP IS NOT NULL
  AND p.SpouseOHIP <> 'NULL'
  AND NOT EXISTS (SELECT 1 FROM covidDB.Spouse s WHERE s.PatientOHIP = p.OHIP);

INSERT INTO staff (id, role, first_name, last_name)
SELECT Id, 'nurse', TRIM(FirstName), TRIM(LastName) FROM covidDB.Nurse
UNION ALL
SELECT Id, 'doctor', TRIM(FirstName), TRIM(LastName) FROM covidDB.Doctor;

INSERT INTO staff_credential (staff_id, credential)
SELECT NurseId, TRIM(NurseCredials) FROM covidDB.NurseCreds
UNION
SELECT DoctorId, TRIM(DoctorCredials) FROM covidDB.DoctorCreds;

INSERT INTO staff_assignment (staff_id, clinic_name)
SELECT NurseId, TRIM(VaxClinicName) FROM covidDB.NurseWork
UNION
SELECT DoctorId, TRIM(VaxClinicName) FROM covidDB.DoctorWork;

INSERT INTO vaccination (patient_ohip, clinic_name, lot, given_on, given_at)
SELECT OHIP, TRIM(ClinicName), TRIM(Lots), STR_TO_DATE(TRIM(Date), '%Y-%m-%d'), STR_TO_DATE(TRIM(Time), '%H:%i')
FROM covidDB.Vaccination;
