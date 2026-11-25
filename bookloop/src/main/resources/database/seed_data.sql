-- BookLoop Seed Data
USE bookloop_db;

-- Admin user (password: Admin@123)
INSERT INTO users (name, email, password_hash, role, is_active) VALUES
('Admin User', 'admin@bookloop.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjTBq0dcg0C', 'ADMIN', TRUE);

-- Student users (password: Student@123)
INSERT INTO users (name, email, password_hash, role, is_active) VALUES
('Alice Johnson', 'student1@bookloop.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOeOjMHMuIpBNWAbVDrh.JX1P1dFMJK5a', 'STUDENT', TRUE),
('Bob Smith', 'student2@bookloop.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOeOjMHMuIpBNWAbVDrh.JX1P1dFMJK5a', 'STUDENT', TRUE),
('Carol White', 'student3@bookloop.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOeOjMHMuIpBNWAbVDrh.JX1P1dFMJK5a', 'STUDENT', TRUE),
('David Brown', 'student4@bookloop.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOeOjMHMuIpBNWAbVDrh.JX1P1dFMJK5a', 'STUDENT', TRUE),
('Eva Green', 'student5@bookloop.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOeOjMHMuIpBNWAbVDrh.JX1P1dFMJK5a', 'STUDENT', TRUE);

-- Student profiles (IDs 2-6 correspond to student users)
INSERT INTO students (id, student_id, department, year_of_study, semester) VALUES
(2, 'CSE2021001', 'Computer Science', 3, 5),
(3, 'ECE2021002', 'Electronics', 2, 3),
(4, 'ME2022001', 'Mechanical Engineering', 1, 2),
(5, 'CE2020001', 'Civil Engineering', 4, 7),
(6, 'MBA2022001', 'Business Administration', 2, 3);

-- Credit wallets
INSERT INTO credit_wallets (student_id, balance) VALUES
(2, 150.00),
(3, 75.50),
(4, 200.00),
(5, 50.00),
(6, 120.00);

-- Departments
INSERT INTO departments (name, code, description) VALUES
('Computer Science Engineering', 'CSE', 'Department of Computer Science and Engineering'),
('Electronics and Communication Engineering', 'ECE', 'Department of Electronics and Communication'),
('Mechanical Engineering', 'ME', 'Department of Mechanical Engineering'),
('Civil Engineering', 'CE', 'Department of Civil Engineering'),
('Master of Business Administration', 'MBA', 'Department of Business Administration');

-- Courses
INSERT INTO courses (department_id, name, code, year_of_study, semester, subject) VALUES
(1, 'Data Structures and Algorithms', 'CSE301', 2, 3, 'Computer Science'),
(1, 'Database Management Systems', 'CSE401', 2, 4, 'Computer Science'),
(1, 'Operating Systems', 'CSE501', 3, 5, 'Computer Science'),
(2, 'Digital Electronics', 'ECE201', 1, 2, 'Electronics'),
(2, 'Signals and Systems', 'ECE301', 2, 3, 'Electronics'),
(3, 'Engineering Mechanics', 'ME101', 1, 1, 'Mechanical'),
(3, 'Thermodynamics', 'ME201', 2, 3, 'Mechanical'),
(4, 'Structural Analysis', 'CE401', 3, 5, 'Civil'),
(5, 'Financial Management', 'MBA201', 1, 2, 'Business'),
(5, 'Marketing Management', 'MBA301', 2, 3, 'Business');

-- Books
INSERT INTO books (title, author, isbn, edition, subject, department_id, description, course_id) VALUES
('Introduction to Algorithms', 'Cormen, Leiserson, Rivest, Stein', '978-0262033848', '4th', 'Computer Science', 1, 'The definitive guide to algorithms', 1),
('Database System Concepts', 'Silberschatz, Korth, Sudarshan', '978-0073523323', '7th', 'Computer Science', 1, 'Comprehensive database textbook', 2),
('Operating System Concepts', 'Silberschatz, Galvin, Gagne', '978-1118063330', '10th', 'Computer Science', 1, 'Classic OS textbook', 3),
('Digital Electronics: Principles and Applications', 'Roger Tokheim', '978-0073373775', '8th', 'Electronics', 2, 'Fundamental digital electronics', 4),
('Signals and Systems', 'Alan V. Oppenheim', '978-0138147570', '2nd', 'Electronics', 2, 'Signal processing fundamentals', 5),
('Engineering Mechanics: Statics', 'Hibbeler', '978-0133918922', '14th', 'Mechanical', 3, 'Statics and dynamics', 6),
('Engineering Thermodynamics', 'P.K. Nag', '978-9353165697', '6th', 'Mechanical', 3, 'Thermodynamics principles', 7),
('Structural Analysis', 'R.C. Hibbeler', '978-0134610672', '10th', 'Civil', 4, 'Civil structural analysis', 8),
('Financial Management', 'I.M. Pandey', '978-8183711807', '11th', 'Business', 5, 'Corporate finance fundamentals', 9),
('Marketing Management', 'Philip Kotler', '978-0133856460', '15th', 'Business', 5, 'Marketing principles', 10),
('Computer Networks', 'Andrew Tanenbaum', '978-0132126953', '5th', 'Computer Science', 1, 'Networking fundamentals', NULL),
('Software Engineering', 'Ian Sommerville', '978-0133943030', '10th', 'Computer Science', 1, 'SE principles and practices', NULL),
('Discrete Mathematics', 'Kenneth Rosen', '978-0073383095', '7th', 'Computer Science', 1, 'Discrete math for CS', NULL),
('Microprocessors and Microcontrollers', 'N. Senthil Kumar', '978-8120340220', '1st', 'Electronics', 2, 'Embedded systems intro', NULL),
('Fluid Mechanics', 'Frank White', '978-0073398273', '8th', 'Mechanical', 3, 'Fluid dynamics fundamentals', NULL);

-- Book Listings
INSERT INTO book_listings (book_id, owner_id, listing_type, condition_type, original_price, asking_price, credit_value, description, status) VALUES
(1, 2, 'SELL', 'GOOD', 850.00, 500.00, 50.00, 'Slightly used, good condition, no missing pages', 'AVAILABLE'),
(2, 3, 'EXCHANGE', 'EXCELLENT', 750.00, NULL, 75.00, 'Almost new, used for one semester', 'AVAILABLE'),
(3, 4, 'SELL', 'ACCEPTABLE', 700.00, 350.00, 30.00, 'Some highlights but all pages intact', 'AVAILABLE'),
(4, 5, 'DONATE', 'GOOD', 450.00, NULL, 0.00, 'Donating as I no longer need it', 'AVAILABLE'),
(5, 6, 'RENT', 'EXCELLENT', 600.00, NULL, NULL, 'Available for rent', 'AVAILABLE'),
(6, 2, 'SELL', 'NEW', 950.00, 800.00, 90.00, 'Never used, bought extra copy', 'AVAILABLE'),
(7, 3, 'EXCHANGE', 'GOOD', 500.00, NULL, 45.00, 'Good condition, want to exchange for DSA book', 'AVAILABLE'),
(8, 4, 'SELL', 'GOOD', 650.00, 400.00, 40.00, 'Used two semesters, still in good shape', 'AVAILABLE'),
(9, 5, 'SELL', 'EXCELLENT', 550.00, 450.00, 55.00, 'Excellent condition, minimal use', 'AVAILABLE'),
(10, 6, 'DONATE', 'ACCEPTABLE', 700.00, NULL, 0.00, 'Old edition but still useful', 'AVAILABLE');

-- Update rental listing
UPDATE book_listings SET rental_duration_days = 30, rental_credits = 20.00 WHERE id = 5;

-- Wishlist items
INSERT INTO wishlist_items (student_id, book_id, book_title, author, subject) VALUES
(2, 4, 'Digital Electronics: Principles and Applications', 'Roger Tokheim', 'Electronics'),
(3, 1, 'Introduction to Algorithms', 'Cormen et al', 'Computer Science'),
(4, 2, 'Database System Concepts', 'Silberschatz', 'Computer Science'),
(5, 11, 'Computer Networks', 'Andrew Tanenbaum', 'Computer Science'),
(6, 3, 'Operating System Concepts', 'Silberschatz', 'Computer Science');

-- Notifications
INSERT INTO notifications (user_id, title, message, notification_type, is_read) VALUES
(2, 'Welcome to BookLoop!', 'Welcome Alice! Start by listing your books or browsing what others have shared.', 'WELCOME', FALSE),
(3, 'Welcome to BookLoop!', 'Welcome Bob! Start by listing your books or browsing what others have shared.', 'WELCOME', FALSE),
(4, 'Welcome to BookLoop!', 'Welcome Carol! Start by listing your books or browsing what others have shared.', 'WELCOME', FALSE),
(5, 'Welcome to BookLoop!', 'Welcome David! Start by listing your books or browsing what others have shared.', 'WELCOME', FALSE),
(6, 'Welcome to BookLoop!', 'Welcome Eva! Start by listing your books or browsing what others have shared.', 'WELCOME', FALSE),
(2, 'Credits Added', 'You have received 150 credits in your wallet. Happy exchanging!', 'CREDIT', FALSE),
(3, 'Book Listed Successfully', 'Your listing for "Database System Concepts" is now live!', 'LISTING', TRUE);
