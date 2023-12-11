
CREATE DATABASE IF NOT EXISTS cs_hu_310_final_project; 
USE cs_hu_310_final_project; 
DROP TABLE IF EXISTS class_registrations; 
DROP TABLE IF EXISTS grades; 
DROP TABLE IF EXISTS class_sections; 
DROP TABLE IF EXISTS instructors; 
DROP TABLE IF EXISTS academic_titles; 
DROP TABLE IF EXISTS students; 
DROP TABLE IF EXISTS classes ;
DROP FUNCTION IF EXISTS convert_to_grade_point; 
DROP FUNCTION IF EXISTS convert_to_letter_grade;
 
CREATE TABLE IF NOT EXISTS classes( 
    class_id INT AUTO_INCREMENT, 
    name VARCHAR(50) NOT NULL, 
    description VARCHAR(1000), 
    code VARCHAR(10) UNIQUE, 
    maximum_students INT DEFAULT 10, 
    PRIMARY KEY(class_id) 
); 
 
CREATE TABLE IF NOT EXISTS students( 
    student_id INT AUTO_INCREMENT, 
    first_name VARCHAR(30) NOT NULL, 
    last_name VARCHAR(50) NOT NULL, 
    birthdate DATE, 
    PRIMARY KEY (student_id) 
); 

CREATE TABLE IF NOT EXISTS academic_titles( 
    academic_title_id INT NOT NULL AUTO_INCREMENT, 
    title VARCHAR(255) NOT NULL, 
  
    PRIMARY KEY (academic_title_id) 
); 
CREATE TABLE IF NOT EXISTS instructors( 
    instructor_id INT NOT NULL AUTO_INCREMENT, 
    first_name VARCHAR(80) NOT NULL, 
    last_name VARCHAR(80) NOT NULL, 
    academic_title_id INT NOT NULL,
    PRIMARY KEY (instructor_id),
    foreign key (academic_title_id) REFERENCES academic_titles (academic_title_id)
); 

CREATE TABLE IF NOT EXISTS terms( 
    term_id INT NOT NULL AUTO_INCREMENT, 
    name VARCHAR(80) NOT NULL, 
  
    PRIMARY KEY (term_id) 
); 
CREATE TABLE IF NOT EXISTS class_sections( 
    class_section_id INT NOT NULL AUTO_INCREMENT, 
    class_id INT NOT NULL,
    instructor_id INT NOT NULL,
    term_id INT NOT NULL,
    PRIMARY KEY (class_section_id),
    foreign key (class_id) REFERENCES classes (class_id),
    foreign key (instructor_id) REFERENCES instructors (instructor_id),
    foreign key (term_id) REFERENCES terms (term_id)
); 



CREATE TABLE IF NOT EXISTS grades( 
    grade_id INT NOT NULL AUTO_INCREMENT, 
    letter_grade CHAR(2) NOT NULL, 
    PRIMARY KEY (grade_id)
); 

CREATE TABLE IF NOT EXISTS class_registrations( 
    class_registration_id INT NOT NULL auto_increment,
    class_section_id INT NOT NULL, 
    student_id  INT NOT NULL,
    grade_id INT,
    signup_timestamp DATETIME DEFAULT current_timestamp,
    PRIMARY KEY (class_registration_id),
    foreign key (class_section_id) REFERENCES class_sections (class_section_id),
    foreign key (student_id) REFERENCES students (student_id),
    foreign key (grade_id) REFERENCES grades (grade_id),
    CONSTRAINT unique_student UNIQUE (student_id, class_section_id)
); 


DELIMITER $$
CREATE FUNCTION convert_to_grade_point(letter_grade char(2))
	RETURNS INT
	DETERMINISTIC
BEGIN
	DECLARE gpa int;
    
	IF (letter_grade = 'A') THEN 
    SET gpa = 4;
    ELSEIF (letter_grade = 'B') THEN
    SET gpa = 3;
	ELSEIF (letter_grade = 'C') THEN
    SET gpa = 2;
    ELSEIF (letter_grade = 'D') THEN
    SET gpa = 1;
	ELSEIF (letter_grade = 'F') THEN 
    SET gpa = 0;
	ELSEIF (letter_grade = NULL) THEN 
    SET gpa = NULL;
    END IF;
    RETURN gpa;
   END $$

DELIMITER $$
CREATE FUNCTION convert_to_letter_grade(grade_point int)
	RETURNS CHAR(2)
	DETERMINISTIC
BEGIN
	DECLARE letter CHAR(2);
	IF (grade_point = 4) THEN 
    SET letter = 'A';
    ELSEIF (grade_point = 3) THEN 
    SET letter = 'B';
	ELSEIF (grade_point = 2) THEN 
     SET letter = 'C';
    ELSEIF (grade_point = 1) THEN 
    SET letter = 'D';
	ELSEIF (grade_point = 0) THEN 
    SET letter = 'F';
	ELSEIF (grade_point = NULL) THEN 
    SET letter = 'NULL';
    END IF;
    RETURN letter;
    END $$

    
    
