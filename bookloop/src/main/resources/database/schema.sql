-- BookLoop Database Schema
CREATE DATABASE IF NOT EXISTS bookloop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookloop_db;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
    profile_picture VARCHAR(500),
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY,
    student_id VARCHAR(50) UNIQUE NOT NULL,
    department VARCHAR(100),
    year_of_study INT,
    semester INT,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS credit_wallets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT UNIQUE NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS credit_transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    wallet_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_type ENUM('CREDIT','DEBIT') NOT NULL,
    reason VARCHAR(255),
    reference_id INT,
    reference_type VARCHAR(50),
    balance_after DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES credit_wallets(id)
);

CREATE TABLE IF NOT EXISTS departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    department_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50),
    year_of_study INT NOT NULL,
    semester INT NOT NULL,
    subject VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(300) NOT NULL,
    author VARCHAR(200),
    isbn VARCHAR(30),
    edition VARCHAR(50),
    subject VARCHAR(200),
    department_id INT,
    description TEXT,
    cover_image VARCHAR(500),
    course_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE IF NOT EXISTS book_listings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT NOT NULL,
    owner_id INT NOT NULL,
    listing_type ENUM('SELL','EXCHANGE','RENT','DONATE') NOT NULL,
    condition_type ENUM('NEW','EXCELLENT','GOOD','ACCEPTABLE') NOT NULL,
    original_price DECIMAL(10,2),
    asking_price DECIMAL(10,2),
    credit_value DECIMAL(10,2) DEFAULT 0,
    description TEXT,
    condition_details TEXT,
    has_highlights BOOLEAN DEFAULT FALSE,
    has_annotations BOOLEAN DEFAULT FALSE,
    has_missing_pages BOOLEAN DEFAULT FALSE,
    has_damaged_cover BOOLEAN DEFAULT FALSE,
    has_torn_pages BOOLEAN DEFAULT FALSE,
    has_water_damage BOOLEAN DEFAULT FALSE,
    rental_duration_days INT,
    rental_credits DECIMAL(10,2),
    image_path VARCHAR(500),
    status ENUM('AVAILABLE','RESERVED','REQUESTED','COMPLETED','SOLD','DONATED','RENTED','UNAVAILABLE','CANCELLED') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS wishlist_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    book_id INT,
    book_title VARCHAR(300),
    author VARCHAR(200),
    subject VARCHAR(200),
    department_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id),
    UNIQUE KEY uq_student_book (student_id, book_id)
);

CREATE TABLE IF NOT EXISTS exchange_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    requester_id INT NOT NULL,
    listing_id INT NOT NULL,
    offered_listing_id INT,
    message TEXT,
    status ENUM('PENDING','ACCEPTED','REJECTED','CANCELLED','EXPIRED') DEFAULT 'PENDING',
    request_type ENUM('BUY','EXCHANGE','RENT','DONATE') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(id),
    FOREIGN KEY (listing_id) REFERENCES book_listings(id),
    FOREIGN KEY (offered_listing_id) REFERENCES book_listings(id)
);

CREATE TABLE IF NOT EXISTS reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    request_id INT NOT NULL,
    requester_id INT NOT NULL,
    owner_id INT NOT NULL,
    listing_id INT NOT NULL,
    status ENUM('ACTIVE','REQUESTER_CONFIRMED','OWNER_CONFIRMED','COMPLETED','CANCELLED','EXPIRED') DEFAULT 'ACTIVE',
    expires_at TIMESTAMP NOT NULL,
    requester_confirmed_at TIMESTAMP,
    owner_confirmed_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES exchange_requests(id),
    FOREIGN KEY (requester_id) REFERENCES users(id),
    FOREIGN KEY (owner_id) REFERENCES users(id),
    FOREIGN KEY (listing_id) REFERENCES book_listings(id)
);

CREATE TABLE IF NOT EXISTS exchange_transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT,
    giver_id INT NOT NULL,
    receiver_id INT NOT NULL,
    listing_id INT NOT NULL,
    transaction_type ENUM('SELL','EXCHANGE','RENT','DONATE','CYCLE_EXCHANGE') NOT NULL,
    credits_transferred DECIMAL(10,2) DEFAULT 0,
    due_date TIMESTAMP,
    return_date TIMESTAMP,
    is_returned BOOLEAN DEFAULT FALSE,
    is_late BOOLEAN DEFAULT FALSE,
    status ENUM('PENDING','COMPLETED','CANCELLED','OVERDUE') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    FOREIGN KEY (giver_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (listing_id) REFERENCES book_listings(id)
);

CREATE TABLE IF NOT EXISTS circular_exchanges (
    id INT PRIMARY KEY AUTO_INCREMENT,
    cycle_hash VARCHAR(64) UNIQUE NOT NULL,
    participant_count INT NOT NULL,
    status ENUM('PROPOSED','PARTIAL_ACCEPT','ALL_ACCEPTED','COMPLETED','CANCELLED','EXPIRED') DEFAULT 'PROPOSED',
    initiated_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (initiated_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS circular_exchange_participants (
    id INT PRIMARY KEY AUTO_INCREMENT,
    circular_exchange_id INT NOT NULL,
    student_id INT NOT NULL,
    gives_listing_id INT NOT NULL,
    receives_listing_id INT NOT NULL,
    position_order INT NOT NULL,
    confirmed BOOLEAN DEFAULT FALSE,
    confirmed_at TIMESTAMP,
    FOREIGN KEY (circular_exchange_id) REFERENCES circular_exchanges(id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (gives_listing_id) REFERENCES book_listings(id),
    FOREIGN KEY (receives_listing_id) REFERENCES book_listings(id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reviewer_id INT NOT NULL,
    reviewed_user_id INT NOT NULL,
    transaction_id INT,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reviewer_id) REFERENCES users(id),
    FOREIGN KEY (reviewed_user_id) REFERENCES users(id),
    FOREIGN KEY (transaction_id) REFERENCES exchange_transactions(id),
    UNIQUE KEY uq_review (reviewer_id, transaction_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50),
    reference_id INT,
    reference_type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_listings_status ON book_listings(status);
CREATE INDEX idx_listings_type ON book_listings(listing_type);
CREATE INDEX idx_listings_owner ON book_listings(owner_id);
CREATE INDEX idx_requests_status ON exchange_requests(status);
CREATE INDEX idx_notifications_user ON notifications(user_id, is_read);
CREATE INDEX idx_transactions_type ON exchange_transactions(transaction_type);
