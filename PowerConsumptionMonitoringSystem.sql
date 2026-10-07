------------- CREATE DATABASE ----------
CREATE DATABASE PRJ301_NILM;
GO
USE PRJ301_NILM;
GO
------------- 14 TABLE ------------

-- 1. USER
CREATE TABLE Users (	
	user_id INT IDENTITY PRIMARY KEY, 
	username NVARCHAR(50) UNIQUE NOT NULL,
    password_hash CHAR(60) NOT NULL, 
    full_name NVARCHAR(80), 
    status NVARCHAR(20) DEFAULT 'ACTIVE',
    created_at DATETIME2 DEFAULT SYSUTCDATETIME());

-- 2. ROLE
CREATE TABLE Roles (
    role_id INT IDENTITY PRIMARY KEY, 
    role_name NVARCHAR(30) UNIQUE NOT NULL);

-- 3. USER ROLE
CREATE TABLE UserRole (
	user_id INT REFERENCES Users, role_id INT REFERENCES Roles, PRIMARY KEY (user_id, role_id));

-- 4. PERMISSIONS
CREATE TABLE Permissions (
    perm_id INT IDENTITY PRIMARY KEY, 
    resource NVARCHAR(40), 
    action NVARCHAR(10),
    UNIQUE (resource, action));

-- 5. ROLE PERM
CREATE TABLE RolePerm (
    role_id INT REFERENCES Roles, 
    perm_id INT REFERENCES Permissions, 
    PRIMARY KEY (role_id, perm_id));

-- 6. DEVICE
CREATE TABLE Device (
    device_id INT IDENTITY PRIMARY KEY, 
    device_code NVARCHAR(30) UNIQUE, 
    location NVARCHAR(80),
    firmware NVARCHAR(20), 
    status NVARCHAR(20) DEFAULT 'ACTIVE', 
    control_owner INT NULL REFERENCES Users,
    last_seen DATETIME2 NULL, 
    api_key_hash CHAR(64) NOT NULL);

-- 7. SENSOR TYPE
CREATE TABLE SensorType (
    sensor_type_id INT IDENTITY PRIMARY KEY, 
    name NVARCHAR(40), 
    unit NVARCHAR(20),
    min_valid FLOAT, max_valid FLOAT);

-- 8. CALIBRATION
CREATE TABLE Calibration (
    calib_id INT IDENTITY PRIMARY KEY, 
    device_id INT REFERENCES Device,
    sensor_type_id INT REFERENCES SensorType, 
    offset_val FLOAT, 
    scale_val FLOAT, 
    version INT,
    valid_until DATE, created_by INT REFERENCES Users);

-- 9. OPERATING PROFILE
CREATE TABLE OperatingProfile (
    profile_id INT IDENTITY PRIMARY KEY, 
    device_id INT REFERENCES Device,
    params_json NVARCHAR(MAX), 
    updated_by INT REFERENCES Users, 
    updated_at DATETIME2 DEFAULT SYSUTCDATETIME());

-- 10. ALERT
CREATE TABLE Alert (
    alert_id INT IDENTITY PRIMARY KEY, 
    device_id INT REFERENCES Device, 
    session_id BIGINT,
    source NVARCHAR(10), 
    label NVARCHAR(40), 
    risk_score FLOAT NULL, 
    status NVARCHAR(20) DEFAULT 'OPEN',
    created_at DATETIME2 DEFAULT SYSUTCDATETIME());

-- 11. ALERT EVIDENCE
CREATE TABLE AlertEvidence (
    evidence_id INT IDENTITY PRIMARY KEY, 
    alert_id INT REFERENCES Alert,
    item NVARCHAR(60), 
    value_text NVARCHAR(200));

-- 12. MAINTENANCE TICKET
CREATE TABLE MaintenanceTicket (
    ticket_id INT IDENTITY PRIMARY KEY, 
    alert_id INT NULL REFERENCES Alert,
    device_id INT REFERENCES Device, 
    assigned_to INT REFERENCES Users, 
    note NVARCHAR(400),
    status NVARCHAR(20) DEFAULT 'OPEN', 
    opened_at DATETIME2 DEFAULT SYSUTCDATETIME(), 
    closed_at DATETIME2 NULL);

-- 13. AUDITLOG
CREATE TABLE AuditLog (
    audit_id BIGINT IDENTITY PRIMARY KEY, 
    actor_user_id INT, action NVARCHAR(50),
    object_type NVARCHAR(50), 
    object_id BIGINT, 
    diff_json NVARCHAR(MAX),
    ts DATETIME2(7) DEFAULT SYSUTCDATETIME(), 
    row_hash CHAR(64) NULL);

-- 14. HASHCHAIN
CREATE TABLE HashChain (
    anchor_id BIGINT IDENTITY PRIMARY KEY, 
    audit_id BIGINT REFERENCES AuditLog,
    prev_hash CHAR(64), 
    chain_hash CHAR(64));

------------- 4 TABLE ------------

-- 1. APPLIANCE
CREATE TABLE Appliance (
  appliance_id INT IDENTITY PRIMARY KEY,
  appliance_name NVARCHAR(50),
  household_id INT,
  rated_w FLOAT,
  room NVARCHAR(30),
  created_at DATETIME2 DEFAULT SYSUTCDATETIME());

-- 2. APPLIANCE CYCLE 
CREATE TABLE ApplianceCycle (
  session_id BIGINT IDENTITY PRIMARY KEY,
  device_id INT NOT NULL REFERENCES Device,
  device_seq INT NOT NULL,                    -- sequence number on the device, prevents duplicates
  measured_at DATETIME2 NOT NULL,             -- time measured on the device
  ingested_at DATETIME2 DEFAULT SYSUTCDATETIME(),   -- time received by the server
  appliance_id INT,
  duration_min FLOAT,
  mean_power_w FLOAT,
  energy_wh FLOAT,
  standby_w FLOAT,
  UNIQUE (device_id, device_seq));


-- 3. APPLIANCE CYCLE LABEL 
CREATE TABLE ApplianceCycleLabel (
  session_id BIGINT PRIMARY KEY REFERENCES ApplianceCycle,
  label NVARCHAR(40) NOT NULL,                -- one of: NORMAL, OVERLOAD, STANDBY_WASTE, MISIDENTIFIED, SENSOR_FAULT
  labeled_by INT REFERENCES Users,
  labeled_at DATETIME2 DEFAULT SYSUTCDATETIME());

-- 4. APPLIANCE CYCLE READING 
CREATE TABLE ApplianceCycleReading (
  reading_id BIGINT IDENTITY PRIMARY KEY,
  session_id BIGINT REFERENCES ApplianceCycle,
  sensor_type_id INT REFERENCES SensorType,
  measured_at DATETIME2, value FLOAT);
GO

-- sessions are append-only: block every update and delete
CREATE TRIGGER trg_ApplianceCycle_AppendOnly ON ApplianceCycle INSTEAD OF UPDATE, DELETE AS
BEGIN
  RAISERROR(N'Session records are append-only', 16, 1);
END;
GO

-- feature view consumed by the machine learning step
CREATE VIEW v_ApplianceCycle_Features AS
SELECT s.session_id, s.device_id, s.appliance_id, s.duration_min, s.mean_power_w, s.energy_wh, s.standby_w, l.label
FROM ApplianceCycle s JOIN ApplianceCycleLabel l ON l.session_id = s.session_id;
GO

INSERT INTO Roles (role_name) VALUES (N'ADMIN'), (N'OPERATOR'), (N'TECHNICIAN'), (N'AUDITOR'), (N'END_USER');
-- every resource gets a READ and a WRITE permission

INSERT INTO Permissions (resource, action)
SELECT r.res, a.act
FROM (VALUES (N'USER'), (N'ROLE'), (N'DEVICE'), (N'SENSOR_TYPE'), (N'CALIBRATION'), (N'PROFILE'),
             (N'APPLIANCECYCLE'), (N'APPLIANCE'), (N'ALERT'), (N'MAINTENANCE'), (N'AUDIT')) r(res)
CROSS JOIN (VALUES (N'READ'), (N'WRITE')) a(act);

-- example: auditors are read-only
INSERT INTO RolePerm (role_id, perm_id)
SELECT (SELECT role_id FROM Roles WHERE role_name = N'AUDITOR'), perm_id
FROM Permissions WHERE action = N'READ';


-- 1. Chèn người dùng(Mật khẩu chung của 3 người: 123456)
INSERT INTO Users (username, password_hash, full_name) VALUES
(N'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', N'Trần Tiến Khanh'),
(N'user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', N'Lê Thành Công'),
(N'tech', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', N'Hồ Anh Huy');
GO

-- 2. Gán vai trò cho từng người
INSERT INTO UserRole (user_id, role_id) VALUES
(1,1),
(2,5),
(3,3);
GO

SELECT u.user_id, u.username, u.full_name, r.role_name, u.status, u.created_at
FROM Users u
LEFT JOIN UserRole ur ON u.user_id = ur.user_id
LEFT JOIN Roles r ON ur.role_id = r.role_id;
GO

INSERT INTO RolePerm (role_id, perm_id)
SELECT r.role_id, p.perm_id
FROM Roles r CROSS JOIN Permissions p
WHERE r.role_name = N'ADMIN';

SELECT * FROM sys.procedures WHERE name = 'Audit_Append';      -- phải ra 1 dòng
SELECT * FROM RolePerm rp JOIN Roles r ON r.role_id = rp.role_id WHERE r.role_name = 'ADMIN'; -- phải có nhiều dòng
SELECT username, password_hash FROM Users;

INSERT INTO Appliance (appliance_name, household_id, rated_w, room) VALUES
(N'Tủ lạnh',        1, 150,  N'Bếp'),
(N'Máy lạnh',        1, 1200, N'Phòng ngủ'),
(N'Máy giặt',        1, 500,  N'Nhà vệ sinh'),
(N'Lò vi sóng',      1, 1000, N'Bếp'),
(N'Bếp từ',          1, 2000, N'Bếp'),
(N'Tivi',            2, 120,  N'Phòng khách'),
(N'Quạt điện',       2, 60,   N'Phòng ngủ'),
(N'Máy nước nóng',   2, 2500, N'Nhà vệ sinh'),
(N'Máy sấy tóc',     2, 1800, N'Phòng tắm'),
(N'Bình đun nước',   2, 1500, N'Bếp'),
(N'Máy lạnh',        3, 1100, N'Phòng khách'),
(N'Tủ đông',         3, 200,  N'Bếp');

SELECT * FROM Appliance ORDER BY appliance_id;