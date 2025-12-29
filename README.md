CREATE DATABASE govlash_laundry;
USE govlash_laundry;

-- Tabel Users
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    user_password VARCHAR(50) NOT NULL,
    user_email VARCHAR(100) NOT NULL,
    user_role VARCHAR(20) NOT NULL, -- 'Admin', 'Customer', 'Receptionist', 'Laundry Staff'
    user_address VARCHAR(255),
    user_phone VARCHAR(20),
    user_gender VARCHAR(10),
    user_dob DATE
);

-- Tabel Services
CREATE TABLE services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(50) NOT NULL,
    service_price INT NOT NULL,
    service_duration INT NOT NULL -- Hari
);

-- Tabel Transactions
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    service_id INT,
    staff_id INT,
    receptionist_id INT,
    transaction_date DATE,
    transaction_status VARCHAR(20), -- 'Pending', 'In Progress', 'Finished'
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id)
);

-- Tabel Notifications
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    message VARCHAR(255),
    created_at DATE,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Akun Dummy untuk Pengujian
INSERT INTO users (user_name, user_password, user_email, user_role) VALUES 
('admin', 'admin123', 'admin@govlash.com', 'Admin'),
('recep', 'recep123', 'recep@govlash.com', 'Receptionist'),
('staff', 'staff123', 'staff@govlash.com', 'Laundry Staff'),
('dodi', 'dodi123', 'dodi@email.com', 'Customer');

-- Layanan Awal
INSERT INTO services (service_name, service_price, service_duration) VALUES 
('Cuci Kering', 15000, 1), 
('Dry Clean Express', 35000, 2),
('Setrika Saja', 10000, 1);
