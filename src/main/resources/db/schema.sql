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
Prodcution      CHAR(15),
Expiry		CHAR(15),
Doses           INTEGER,
PRIMARY KEY(Lot),
FOREIGN KEY(CompanyName) REFERENCES Company(name));

CREATE TABLE VaxClinic(
Name	        VARCHAR(30)	NOT NULL,
Street          VARCHAR(30),
City            VARCHAR(30),
Prov            VARCHAR(30),
PC              VARCHAR(30),
date            CHAR(15),
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
Date	        CHAR(15)      NOT NULL,
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
Name       VARCHAR(40)  NOT NULL, 
Phone		 CHAR(10),
PRIMARY KEY(Name));

