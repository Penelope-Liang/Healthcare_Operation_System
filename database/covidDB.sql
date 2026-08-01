create DATABASE covidDB;
USE covidDB;

CREATE TABLE Company(
Name		VARCHAR(30)	NOT NULL,
Street          VARCHAR(30),
City            VARCHAR(30),
Prov            VARCHAR(30),
PC              VARCHAR(30),
PRIMARY KEY(Name));


CREATE TABLE Vaccine(
Lot             VARCHAR(30)	NOT NULL,
CompanyName     VARCHAR(30),
Prodcution      CHAR(11),
Expiry		CHAR(11),
Doses           INTEGER,
PRIMARY KEY(Lot),
FOREIGN KEY(CompanyName) REFERENCES Company(name));


CREATE TABLE VaxClinic(
Name	        VARCHAR(30)	NOT NULL,
Street          VARCHAR(30),
City            VARCHAR(30),
Prov            VARCHAR(30),
PC              VARCHAR(30),
date            CHAR(11),
PRIMARY KEY(Name));


CREATE TABLE ShipTo(
Lots	     VARCHAR(6)	   NOT NULL,
Clinic        VARCHAR(30)    NOT NULL,
PRIMARY KEY(Lots, Clinic),
FOREIGN KEY(Lots) REFERENCES Vaccine(Lot) on delete cascade,
FOREIGN KEY(Clinic) REFERENCES VaxClinic(Name) on delete cascade);


CREATE TABLE Patient(
OHIP	       CHAR(12)   NOT NULL,
SpouseOHIP      CHAR(12),
FirstName       VARCHAR(30),
LastName        VARCHAR(30),
DOB             CHAR(10),
PRIMARY KEY(OHIP));

CREATE TABLE Spouse(
OHIP            CHAR(12)   NOT NULL,
PatientOHIP     CHAR(12)	 NOT NULL,
Phone           CHAR(10),
FirstName       VARCHAR(15),
LastName        VARCHAR(15),
PRIMARY KEY(OHIP),
FOREIGN KEY(PatientOHIP) REFERENCES Patient(OHIP));

CREATE TABLE Vaccination(
OHIP            CHAR(12)     NOT NULL,
ClinicName      VARCHAR(30)  NOT NULL,
Lots            CHAR(6)      NOT NULL,
Date	        CHAR(11)      NOT NULL,
Time            CHAR(5)      NOT NULL,
PRIMARY KEY(OHIP),
FOREIGN KEY(OHIP) REFERENCES Patient(OHIP),
FOREIGN KEY(ClinicName) REFERENCES VaxClinic(Name),
FOREIGN KEY(Lots) REFERENCES Vaccine(Lot));


CREATE TABLE Nurse(
Id              CHAR(5)       NOT NULL, 
FirstName       VARCHAR(15),
LastName        VARCHAR(15),
PRIMARY KEY(Id));


CREATE TABLE NurseCreds(
NurseId           CHAR(5)      NOT NULL, 
NurseCredials	  VARCHAR(20),
PRIMARY KEY(NurseId, NurseCredials),
FOREIGN KEY(NurseId) REFERENCES Nurse(Id));


CREATE TABLE NurseWork(
VaxClinicName       VARCHAR(30)   NOT NULL, 
NurseId		    CHAR(5)      NOT NULL,
PRIMARY KEY(VaxClinicName, NurseId),
FOREIGN KEY(VaxClinicName) REFERENCES VaxClinic(Name) on delete cascade,
FOREIGN KEY(NurseId) REFERENCES Nurse(Id) on delete cascade);


CREATE TABLE Doctor(
Id                CHAR(5)  NOT NULL, 
FirstName         VARCHAR(15),
LastName          VARCHAR(15),
PRIMARY KEY(Id));


CREATE TABLE DoctorCreds(
DoctorId            CHAR(5)  NOT NULL, 
DoctorCredials	     VARCHAR(20) NOT NULL,
PRIMARY KEY(DoctorId),
FOREIGN KEY(DoctorId) REFERENCES Doctor(Id));


CREATE TABLE DoctorWork(
VaxClinicName     VARCHAR(30), 
DoctorId		  CHAR(5),
PRIMARY KEY(VaxClinicName, DoctorId),
FOREIGN KEY(VaxClinicName) REFERENCES VaxClinic(Name) on delete cascade,
FOREIGN KEY(DoctorId) REFERENCES Doctor(Id) on delete cascade);


CREATE TABLE Practice(
Name       VARCHAR(20)  NOT NULL, 
Phone		 CHAR(10),
PRIMARY KEY(Name));


INSERT INTO Company VALUES
("Astrazeneca","1004 Middlegate Rd", "Mississauga,", "ON" ,"L4Y 1M4"),
("Johnson & Johnson", "88 McNabb St", "Markham", "ON", "L3R 5L2"),
("Moderna", "One Upland Rd", "Norwood", "MA", "02062"),
("Pfizer", "400 Webro Rd", "Parsippany", "NJ", "07054");


INSERT INTO Vaccine VALUES  
("AB1234", "Astrazeneca", "01 FEB 2021", "01 AUG 2022", 1),
("CD2345", "Johnson & Johnson", "02 FEB 2021", "02 AUG 2022", 2),
("EF3456", "Moderna", "03 FEB 2021", "03 AUG 2022", 3),
("GH4567", "Astrazeneca", "04 FEB 2021", "04 AUG 2022", 1),
("IJ5678", "Johnson & Johnson", "05 FEB 2021", "05 AUG 2022", 2),
("KL6789", "Moderna", "06 FEB 2021", "06 AUG 2022", 3);


INSERT INTO VaxClinic VALUES
("Pharmasave Division Pharmacy", "472 Division St", "Kingston", "ON", "K7K 4B1", "01 JULY 2021"),
("Rexall", " 240 Montreal St", "Kingston", "ON", "K7K 3G8", "17 JUNE 2021"),
("Shoppers Drug Mart", "445 Princess St", "Kingston", "ON", "K7L 1C3", "30 DEC 2021"),
("CATP Kingston", "7 Hickson Ave", "Kingston", "ON", "K7K 2N4", "03 JAN 2022"),
("Frontenac Medical Pharmacy", "789 Princess St", "Kingston", "ON", "K7L 1E9", "23 NOV 2021"),
("Quarry Medical Pharmacy", "100 Princess St", "Kingston", "ON", "K7L 1A7", "05 FEB 2022");


INSERT INTO ShipTo VALUES
("AB1234", "Pharmasave Division Pharmacy"),
("CD2345", "Rexall"),
("EF3456", "Shoppers Drug Mart"),
("GH4567", "CATP Kingston"),
("IJ5678", "Frontenac Medical Pharmacy"),
("KL6789", "Quarry Medical Pharmacy");


INSERT INTO Patient VALUES
("1029-293-345", "2342-543-798", "Amy", "White", "1990-01-23"),
("3424-091-654", "NULL", "John", "Smith", "1991-02-24"),
("0903-434-120", "NULL", "Peter", "Lee", "1992-03-25"),
("4523-123-787", "NULL", "Bob", "Hill", "2000-04-21"),
("4323-898-432", "NULL", "Kitty", "Wood", "2012-11-11"),
("4523-098-343", "8234-923-444", "Lucy", "Cooper", "1980-05-07");


INSERT INTO Spouse VALUES
("2342-543-798", "1029-293-345", "613888123", "Lily", "Li"),
("8234-923-444", "4523-098-343", "613543675", "Teddy", "James");


INSERT INTO Vaccination VALUES
("1029-293-345", "Pharmasave Division Pharmacy", "AB1234", "2021-12-21", "10:00"),
("3424-091-654", "Rexall", "CD2345", "2021-11-19", "13:00"),
("0903-434-120", "Shoppers Drug Mart", "EF3456", "2021-11-04", "14:23"),
("4523-123-787", "CATP Kingston", "GH4567", "2022-01-01", "09:25"),
("4323-898-432", "Frontenac Medical Pharmacy", "IJ5678", "2022-01-23", "13:35"),
("4523-098-343", "Quarry Medical Pharmacy", "KL6789", "2021-09-15", "16:50");


INSERT INTO Nurse VALUES
("QW102", "Jim", "King"),
("ER345", "Alex", "Green"),
("TY143", "Paul", "Taylor"),
("UI089", "Emma", "Carter"),
("OP678", "Helen", "Roy"),
("AS234", "Tim", "Wang");


INSERT INTO NurseCreds VALUES
("QW102", "RN"),
("ER345", "BSN"),
("TY143", "CCRN"),
("UI089", "CCRN"),
("OP678", "BSN"),
("AS234", "RN");


INSERT INTO NurseWork VALUES
("Pharmasave Division Pharmacy", "QW102"),
("Rexall", "ER345"),
("Shoppers Drug Mart", "TY143"),
("CATP Kingston", "UI089"),
("Frontenac Medical Pharmacy", "OP678"),
("Quarry Medical Pharmacy", "AS234");

INSERT INTO Doctor VALUES
("DF324", "Kate", "Gates"),
("GH090", "Kiki", "Johnson"),
("JK456", "Whiskey", "Brown"),
("LZ604", "Alicia", "Williams"),
("XC546", "Cindy", "Young"),
("VB980", "Sherry", "Fox");


INSERT INTO DoctorCreds VALUES
("DF324", "MD"),
("GH090", "DO"),
("JK456", "DNP"),
("LZ604", "PA"),
("XC546", "CNNP"),
("VB980", "MBBS");


INSERT INTO DoctorWork VALUES
("Pharmasave Division Pharmacy", "DF324"),
("Rexall", "GH090"),
("Shoppers Drug Mart", "JK456"),
("CATP Kingston", "LZ604"),
("Frontenac Medical Pharmacy", "XC546"),
("Quarry Medical Pharmacy", "XC546");


INSERT INTO Practice VALUES
("Private Practice", 6132345423),
("Group Practice", 6130984323),
("Large HMOs", 6134126443),
("Hospital Based", 6132224434),
("Locum Tenens", 6132345423),
("Associations and Partnerships", 6132345423);

