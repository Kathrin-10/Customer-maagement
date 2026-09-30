
CREATE DATABASE IF NOT EXISTS customer_management;

USE customer_management;

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    country_code VARCHAR(12) NOT NULL DEFAULT '+1',
    district VARCHAR(100) NOT NULL,
    address VARCHAR(255) NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS project_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    due_date DATE NOT NULL,
    completion_date DATE NULL,
    registered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    report TEXT NULL,
    amount DECIMAL(12,2) NULL,
    client_name VARCHAR(200) NOT NULL,
    client_email VARCHAR(255) NULL,
    client_phone VARCHAR(30) NULL,
    client_address VARCHAR(255) NULL,
    budget DECIMAL(12,2) NULL,
    advance_amount DECIMAL(12,2) NULL,
    last_payment_date DATE NULL,
    customer_id BIGINT NULL,
    CONSTRAINT fk_project_task_customers
        FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON DELETE SET NULL
	);

CREATE TABLE IF NOT EXISTS revenue_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(12,2) NOT NULL,
    record_date DATE NOT NULL,
    project_name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    project_info VARCHAR(500) NULL,
    project_id BIGINT NULL,
    CONSTRAINT fk_revenue_project
        FOREIGN KEY (project_id) REFERENCES project_tasks(id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS project_assignments (
    project_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    PRIMARY KEY (project_id, customer_id),
    CONSTRAINT fk_project_assignment_project
        FOREIGN KEY (project_id) REFERENCES project_tasks(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_project_assignment_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_customer_username ON customers(username);
CREATE INDEX idx_customer_email ON customers(email);
CREATE INDEX idx_project_category ON project_tasks(category);
CREATE INDEX idx_project_status ON project_tasks(status);
CREATE INDEX idx_project_due_date ON project_tasks(due_date);
CREATE INDEX idx_revenue_date ON revenue_records(record_date);

ALTER TABLE project_tasks
    ADD COLUMN IF NOT EXISTS last_payment_date DATE NULL;

ALTER TABLE revenue_records
    ADD COLUMN IF NOT EXISTS project_id BIGINT NULL;

CREATE INDEX idx_revenue_project ON revenue_records(project_id);

USE customer_management;

INSERT INTO customers
(username, full_name, email, password, phone_number, country_code, district, address, role)
VALUES

('arun.kumar', 'Arun Kumar', 'arun.kumar@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9876543210', '+91', 'Dindigul',
 '12 Anna Nagar, Dindigul, Tamil Nadu', 'CUSTOMER'),

('meena.joseph', 'Meena Joseph', 'meena.joseph@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9843217650', '+91', 'Madurai',
 '24 KK Nagar, Madurai, Tamil Nadu', 'CUSTOMER'),

('karthik.raj', 'Karthik Raj', 'karthik.raj@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9791234567', '+91', 'Coimbatore',
 '18 Saibaba Colony, Coimbatore, Tamil Nadu', 'CUSTOMER'),

('priya.nair', 'Priya Nair', 'priya.nair@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9887654321', '+91', 'Chennai',
 '41 Adyar, Chennai, Tamil Nadu', 'CUSTOMER'),

('daniel.thomas', 'Daniel Thomas', 'daniel.thomas@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9812345678', '+91', 'Bengaluru',
 '7 Indiranagar, Bengaluru, Karnataka', 'CUSTOMER'),

('divya.srinivasan', 'Divya Srinivasan', 'divya.srinivasan@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9965432108', '+91', 'Tiruchirappalli',
 '15 Cantonment Road, Tiruchirappalli, Tamil Nadu', 'CUSTOMER'),

('sanjay.prakash', 'Sanjay Prakash', 'sanjay.prakash@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9756784321', '+91', 'Salem',
 '32 Fairlands, Salem, Tamil Nadu', 'CUSTOMER'),

('anitha.mary', 'Anitha Mary', 'anitha.mary@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9898765432', '+91', 'Erode',
 '9 Perundurai Road, Erode, Tamil Nadu', 'CUSTOMER'),

('mohammed.irfan', 'Mohammed Irfan', 'mohammed.irfan@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9823456710', '+91', 'Tiruppur',
 '22 Avinashi Road, Tiruppur, Tamil Nadu', 'CUSTOMER'),

('lavanya.krishnan', 'Lavanya Krishnan', 'lavanya.krishnan@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9871236540', '+91', 'Thanjavur',
 '6 Medical College Road, Thanjavur, Tamil Nadu', 'CUSTOMER'),

('joseph.samuel', 'Joseph Samuel', 'joseph.samuel@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9945678123', '+91', 'Chennai',
 '28 Velachery Main Road, Chennai, Tamil Nadu', 'CUSTOMER'),

('nandhini.selvam', 'Nandhini Selvam', 'nandhini.selvam@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2JZKQ0J1Y7R8fYJ8JQJ7FQ8q6Vv7mK',
 '9867543219', '+91', 'Dindigul',
 '11 RM Colony, Dindigul, Tamil Nadu', 'CUSTOMER');
 
 INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'Corporate Website Redesign', 'Web Development', 'COMPLETED',
'2026-05-18', '2026-05-15', '2026-03-10 10:15:00',
'Corporate website redesign completed and delivered.',
65000.00, full_name, email, phone_number, address,
65000.00, 65000.00, id
FROM customers WHERE username = 'arun.kumar'

UNION ALL

SELECT
'Inventory Management Portal', 'Software Development', 'COMPLETED',
'2026-07-12', '2026-07-10', '2026-05-20 11:30:00',
'Inventory portal completed successfully.',
85000.00, full_name, email, phone_number, address,
85000.00, 85000.00, id
FROM customers WHERE username = 'arun.kumar'

UNION ALL

SELECT
'Customer Analytics Dashboard', 'Data Analytics', 'IN_PROGRESS',
'2026-10-15', NULL, '2026-08-02 09:45:00',
'Dashboard development is currently underway.',
120000.00, full_name, email, phone_number, address,
120000.00, 60000.00, id
FROM customers WHERE username = 'arun.kumar'

UNION ALL

SELECT
'Monthly Reporting Automation', 'Automation', 'PENDING',
'2026-11-05', NULL, '2026-09-10 14:20:00',
'Automation project awaiting implementation.',
45000.00, full_name, email, phone_number, address,
45000.00, 10000.00, id
FROM customers WHERE username = 'arun.kumar';

INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'Business Website', 'Web Development', 'COMPLETED',
'2026-02-15', '2026-02-12', '2026-01-05 10:00:00',
'Business website completed and launched.',
55000.00, full_name, email, phone_number, address,
55000.00, 55000.00, id
FROM customers WHERE username='meena.joseph'

UNION ALL

SELECT
'Online Appointment System', 'Software Development', 'COMPLETED',
'2026-04-20', '2026-04-18', '2026-03-01 09:30:00',
'Appointment system delivered.',
90000.00, full_name, email, phone_number, address,
90000.00, 90000.00, id
FROM customers WHERE username='meena.joseph'

UNION ALL

SELECT
'Digital Marketing Portal', 'Marketing Technology', 'COMPLETED',
'2026-06-25', '2026-06-22', '2026-05-05 11:15:00',
'Marketing portal completed.',
70000.00, full_name, email, phone_number, address,
70000.00, 70000.00, id
FROM customers WHERE username='meena.joseph'

UNION ALL

SELECT
'Sales Tracking System', 'Business Software', 'COMPLETED',
'2026-08-10', '2026-08-08', '2026-07-01 13:00:00',
'Sales tracking system completed.',
110000.00, full_name, email, phone_number, address,
110000.00, 75000.00, id
FROM customers WHERE username='meena.joseph'

UNION ALL

SELECT
'Customer Feedback Portal', 'Web Development', 'COMPLETED',
'2026-09-05', '2026-09-03', '2026-08-01 15:30:00',
'Feedback portal completed. Final payment pending.',
60000.00, full_name, email, phone_number, address,
60000.00, 30000.00, id
FROM customers WHERE username='meena.joseph';

INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'Restaurant Website', 'Web Development', 'COMPLETED',
'2026-03-15', '2026-03-12', '2026-02-01 10:00:00',
'Restaurant website completed.',
40000.00, full_name, email, phone_number, address,
40000.00, 40000.00, id
FROM customers WHERE username='karthik.raj'

UNION ALL

SELECT
'Billing Application', 'Software Development', 'COMPLETED',
'2026-05-20', '2026-05-18', '2026-04-01 11:00:00',
'Billing application delivered.',
75000.00, full_name, email, phone_number, address,
75000.00, 75000.00, id
FROM customers WHERE username='karthik.raj'

UNION ALL

SELECT
'Business Reports Dashboard', 'Data Analytics', 'COMPLETED',
'2026-07-30', '2026-07-28', '2026-06-01 12:00:00',
'Reporting dashboard completed.',
95000.00, full_name, email, phone_number, address,
95000.00, 95000.00, id
FROM customers WHERE username='karthik.raj';

INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'E-Commerce Website', 'E-Commerce', 'COMPLETED',
'2026-04-10', '2026-04-08', '2026-02-15 09:00:00',
'E-commerce platform completed.',
150000.00, full_name, email, phone_number, address,
150000.00, 150000.00, id
FROM customers WHERE username='priya.nair'

UNION ALL

SELECT
'Mobile Sales Application', 'Mobile Development', 'IN_PROGRESS',
'2026-10-30', NULL, '2026-08-10 10:30:00',
'Mobile application currently under development.',
180000.00, full_name, email, phone_number, address,
180000.00, 90000.00, id
FROM customers WHERE username='priya.nair'

UNION ALL

SELECT
'Customer Loyalty System', 'Business Software', 'PENDING',
'2026-11-20', NULL, '2026-09-05 14:00:00',
'Project scheduled for development.',
80000.00, full_name, email, phone_number, address,
80000.00, 20000.00, id
FROM customers WHERE username='priya.nair'

UNION ALL

SELECT
'Marketing Analytics Report', 'Data Analytics', 'COMPLETED',
'2026-08-15', '2026-08-13', '2026-07-01 11:45:00',
'Analytics report delivered.',
70000.00, full_name, email, phone_number, address,
70000.00, 70000.00, id
FROM customers WHERE username='priya.nair';

INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'Hotel Booking Website', 'Web Development', 'COMPLETED',
'2026-01-25', '2026-01-22', '2025-12-10 09:30:00',
'Hotel booking website completed.',
100000.00, full_name, email, phone_number, address,
100000.00, 100000.00, id
FROM customers WHERE username='daniel.thomas'

UNION ALL

SELECT
'Reservation Management System', 'Software Development', 'COMPLETED',
'2026-03-20', '2026-03-18', '2026-02-05 10:15:00',
'Reservation system completed.',
125000.00, full_name, email, phone_number, address,
125000.00, 125000.00, id
FROM customers WHERE username='daniel.thomas'

UNION ALL

SELECT
'Hotel Revenue Dashboard', 'Data Analytics', 'COMPLETED',
'2026-05-15', '2026-05-12', '2026-04-01 13:30:00',
'Revenue dashboard delivered.',
90000.00, full_name, email, phone_number, address,
90000.00, 90000.00, id
FROM customers WHERE username='daniel.thomas'

UNION ALL

SELECT
'Guest Feedback System', 'Web Development', 'COMPLETED',
'2026-07-05', '2026-07-02', '2026-06-01 15:00:00',
'Guest feedback system completed.',
60000.00, full_name, email, phone_number, address,
60000.00, 60000.00, id
FROM customers WHERE username='daniel.thomas'

UNION ALL

SELECT
'Hotel Marketing Portal', 'Marketing Technology', 'COMPLETED',
'2026-08-25', '2026-08-22', '2026-07-15 11:00:00',
'Marketing portal completed and delivered.',
75000.00, full_name, email, phone_number, address,
75000.00, 75000.00, id
FROM customers WHERE username='daniel.thomas';

INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'School Management Portal', 'Education Software', 'COMPLETED',
'2026-05-30', '2026-05-27', '2026-04-01 09:30:00',
'School management portal completed. Final payment pending.',
130000.00, full_name, email, phone_number, address,
130000.00, 90000.00, id
FROM customers WHERE username='divya.srinivasan'

UNION ALL

SELECT
'Student Attendance System', 'Software Development', 'COMPLETED',
'2026-07-10', '2026-07-08', '2026-06-01 10:00:00',
'Attendance system completed.',
65000.00, full_name, email, phone_number, address,
65000.00, 65000.00, id
FROM customers WHERE username='divya.srinivasan'

UNION ALL

SELECT
'Parent Communication Portal', 'Web Development', 'COMPLETED',
'2026-08-20', '2026-08-18', '2026-07-05 11:30:00',
'Parent portal delivered.',
85000.00, full_name, email, phone_number, address,
85000.00, 85000.00, id
FROM customers WHERE username='divya.srinivasan'

UNION ALL

SELECT
'School Analytics Dashboard', 'Data Analytics', 'IN_PROGRESS',
'2026-10-25', NULL, '2026-08-20 14:00:00',
'Analytics dashboard currently being developed.',
95000.00, full_name, email, phone_number, address,
95000.00, 45000.00, id
FROM customers WHERE username='divya.srinivasan';

INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'Manufacturing Website', 'Web Development', 'COMPLETED',
'2026-02-28', '2026-02-25', '2026-01-05 10:00:00',
'Manufacturing website completed.',
70000.00, full_name, email, phone_number, address,
70000.00, 70000.00, id
FROM customers WHERE username='sanjay.prakash'

UNION ALL

SELECT
'Production Tracking System', 'Software Development', 'COMPLETED',
'2026-05-10', '2026-05-07', '2026-03-10 11:00:00',
'Production tracking system delivered.',
140000.00, full_name, email, phone_number, address,
140000.00, 140000.00, id
FROM customers WHERE username='sanjay.prakash'

UNION ALL

SELECT
'Production Analytics Dashboard', 'Data Analytics', 'COMPLETED',
'2026-08-05', '2026-08-02', '2026-06-10 13:00:00',
'Analytics dashboard delivered.',
110000.00, full_name, email, phone_number, address,
110000.00, 110000.00, id
FROM customers WHERE username='sanjay.prakash';

INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'Clinic Website', 'Web Development', 'COMPLETED',
'2026-04-15', '2026-04-13', '2026-03-01 09:00:00',
'Clinic website completed.',
50000.00, full_name, email, phone_number, address,
50000.00, 50000.00, id
FROM customers WHERE username='anitha.mary'

UNION ALL

SELECT
'Appointment Booking Platform', 'Software Development', 'COMPLETED',
'2026-06-20', '2026-06-18', '2026-05-01 10:30:00',
'Appointment platform completed.',
100000.00, full_name, email, phone_number, address,
100000.00, 100000.00, id
FROM customers WHERE username='anitha.mary'

UNION ALL

SELECT
'Patient Communication Portal', 'Web Development', 'IN_PROGRESS',
'2026-10-12', NULL, '2026-08-05 11:00:00',
'Portal development underway.',
90000.00, full_name, email, phone_number, address,
90000.00, 45000.00, id
FROM customers WHERE username='anitha.mary'

UNION ALL

SELECT
'Clinic Performance Dashboard', 'Data Analytics', 'PENDING',
'2026-11-10', NULL, '2026-09-12 15:00:00',
'Dashboard awaiting development.',
85000.00, full_name, email, phone_number, address,
85000.00, 15000.00, id
FROM customers WHERE username='anitha.mary'

UNION ALL

SELECT
'Online Consultation Module', 'Healthcare Software', 'PENDING',
'2026-12-01', NULL, '2026-09-15 12:00:00',
'Online consultation module scheduled.',
120000.00, full_name, email, phone_number, address,
120000.00, 30000.00, id
FROM customers WHERE username='anitha.mary';

INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'Retail POS System', 'Retail Software', 'COMPLETED',
'2026-03-25', '2026-03-22', '2026-02-01 10:00:00',
'POS system completed. Final payment pending.',
125000.00, full_name, email, phone_number, address,
125000.00, 75000.00, id
FROM customers WHERE username='mohammed.irfan'

UNION ALL

SELECT
'Inventory Control System', 'Business Software', 'COMPLETED',
'2026-06-15', '2026-06-12', '2026-04-10 11:00:00',
'Inventory system delivered.',
100000.00, full_name, email, phone_number, address,
100000.00, 100000.00, id
FROM customers WHERE username='mohammed.irfan'

UNION ALL

SELECT
'Retail Sales Dashboard', 'Data Analytics', 'COMPLETED',
'2026-08-15', '2026-08-12', '2026-07-01 13:00:00',
'Sales dashboard completed.',
80000.00, full_name, email, phone_number, address,
80000.00, 50000.00, id
FROM customers WHERE username='mohammed.irfan'

UNION ALL

SELECT
'Customer Loyalty Application', 'Mobile Development', 'COMPLETED',
'2026-09-10', '2026-09-08', '2026-08-01 14:30:00',
'Loyalty application completed.',
95000.00, full_name, email, phone_number, address,
95000.00, 95000.00, id
FROM customers WHERE username='mohammed.irfan';

INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'Interior Design Portfolio', 'Web Development', 'COMPLETED',
'2026-03-05', '2026-03-02', '2026-01-15 09:30:00',
'Portfolio website completed.',
45000.00, full_name, email, phone_number, address,
45000.00, 45000.00, id
FROM customers WHERE username='lavanya.krishnan'

UNION ALL

SELECT
'Project Showcase Website', 'Web Development', 'COMPLETED',
'2026-05-25', '2026-05-22', '2026-04-01 10:30:00',
'Showcase website delivered.',
60000.00, full_name, email, phone_number, address,
60000.00, 60000.00, id
FROM customers WHERE username='lavanya.krishnan'

UNION ALL

SELECT
'Client Management Portal', 'Business Software', 'COMPLETED',
'2026-07-20', '2026-07-18', '2026-06-01 12:00:00',
'Client management portal completed.',
85000.00, full_name, email, phone_number, address,
85000.00, 85000.00, id
FROM customers WHERE username='lavanya.krishnan'

UNION ALL

SELECT
'Design Analytics Dashboard', 'Data Analytics', 'COMPLETED',
'2026-09-05', '2026-09-02', '2026-08-01 15:00:00',
'Analytics dashboard completed.',
70000.00, full_name, email, phone_number, address,
70000.00, 70000.00, id
FROM customers WHERE username='lavanya.krishnan';

INSERT INTO project_tasks
(title, category, status, due_date, completion_date, registered_at,
 report, amount, client_name, client_email, client_phone, client_address,
 budget, advance_amount, customer_id)

SELECT
'Logistics Management System', 'Software Development', 'COMPLETED',
'2026-03-15', '2026-03-12', '2026-01-10 10:00:00',
'Logistics system completed.',
135000.00, full_name, email, phone_number, address,
135000.00, 135000.00, id
FROM customers WHERE username='joseph.samuel'

UNION ALL

SELECT
'Fleet Tracking Dashboard', 'Data Analytics', 'COMPLETED',
'2026-06-10', '2026-06-08', '2026-04-05 11:00:00',
'Fleet dashboard completed.',
95000.00, full_name, email, phone_number, address,
95000.00, 95000.00, id
FROM customers WHERE username='joseph.samuel'

UNION ALL

SELECT
'Delivery Tracking Portal', 'Web Development', 'IN_PROGRESS',
'2026-10-18', NULL, '2026-08-01 09:30:00',
'Delivery portal currently under development.',
110000.00, full_name, email, phone_number, address,
110000.00, 55000.00, id
FROM customers WHERE username='joseph.samuel'

UNION ALL

SELECT
'Driver Mobile Application', 'Mobile Development', 'IN_PROGRESS',
'2026-11-15', NULL, '2026-08-20 13:00:00',
'Driver application currently under development.',
145000.00, full_name, email, phone_number, address,
145000.00, 70000.00, id
FROM customers WHERE username='joseph.samuel'

UNION ALL

SELECT
'Logistics Reporting System', 'Data Analytics', 'PENDING',
'2026-12-05', NULL, '2026-09-10 14:00:00',
'Reporting system scheduled for development.',
75000.00, full_name, email, phone_number, address,
75000.00, 15000.00, id
FROM customers WHERE username='joseph.samuel';

SELECT COUNT(*) AS total_customers
FROM customers
WHERE role = 'CUSTOMER';

SELECT COUNT(*) AS total_projects
FROM project_tasks;

SELECT
    c.full_name,
    COUNT(p.id) AS project_count
FROM customers c
LEFT JOIN project_tasks p
    ON p.customer_id = c.id
WHERE c.role = 'CUSTOMER'
GROUP BY c.id, c.full_name
ORDER BY c.full_name;

SELECT
    c.full_name,
    p.title,
    p.status,
    p.budget,
    p.advance_amount,
    (p.budget - p.advance_amount) AS remaining_payment
FROM project_tasks p
JOIN customers c
    ON p.customer_id = c.id
ORDER BY c.full_name, p.id;

SELECT
    c.id,
    c.full_name,
    CASE
        WHEN EXISTS (
            SELECT 1
            FROM project_tasks p
            WHERE p.customer_id = c.id
              AND (
                  UPPER(p.status) <> 'COMPLETED'
                  OR COALESCE(p.advance_amount, 0) < COALESCE(p.budget, 0)
              )
        )
        THEN 'ACTIVE'
        ELSE 'INACTIVE'
    END AS expected_status
FROM customers c
WHERE c.role = 'CUSTOMER'
ORDER BY c.full_name;

DELETE FROM revenue_records WHERE project_id IS NULL;

SELECT * FROM customers;
SELECT * FROM project_tasks;
SELECT * FROM revenue_records;