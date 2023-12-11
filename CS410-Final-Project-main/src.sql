CREATE DATABASE IF NOT EXISTS cs_410_final_project;
use cs_410_final_project;

-- Table to store information about classes
CREATE TABLE classes (
    class_id INT PRIMARY KEY,
    course_number VARCHAR(10) NOT NULL,
    term VARCHAR(5) NOT NULL,
    section_number INT NOT NULL,
    description VARCHAR(255),
    UNIQUE (course_number, term, section_number)
);

-- Table to store information about categories within a class
CREATE TABLE categories (
    category_id INT PRIMARY KEY,
    class_id INT REFERENCES classes(class_id),
    name VARCHAR(50) NOT NULL,
    weight DECIMAL(5, 2), 
    UNIQUE (class_id, name)
);

-- Table to store information about assignments within a category
CREATE TABLE assignments (
    assignment_id INT PRIMARY KEY,
    category_id INT REFERENCES categories(category_id),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    point_value INT,
    UNIQUE (category_id, name)
);

-- Table to store information about students
CREATE TABLE students (
    student_id INT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    student_id_number VARCHAR(20),
    name VARCHAR(100) NOT NULL,
    UNIQUE (username)
);

-- Table to store the enrollment of students in classes
CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY,
    student_id INT REFERENCES students(student_id),
    class_id INT REFERENCES classes(class_id),
    UNIQUE (student_id, class_id)
);

-- Table to store grades assigned to students for assignments
CREATE TABLE grades (
    grade_id INT PRIMARY KEY,
    student_id INT REFERENCES students(student_id),
    assignment_id INT REFERENCES assignments(assignment_id),
    grade INT,
    UNIQUE (student_id, assignment_id)
);
