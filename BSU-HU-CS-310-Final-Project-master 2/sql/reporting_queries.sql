/* Put your final project reporting queries here */
USE cs_hu_310_final_project;

-- Reports
-- Calculate the GPA for student given a student_id (use student_id=1) NEED TO IMPLEMENT FUNCTION FOR GPA
SELECT students.first_name,
students.last_name,
COUNT(class_registration.student_id) as number_of_classes,
SUM(class_registration.grade_id)  as total_grade_points,
(total_grade_points/number_of_classes) as GPA
from class_registrations
INNER JOIN students on class_registrations.student_id = students.student_id
WHERE class_registrations.student_id = '1'
Group by class_registrations.student_id;

-- Calculate the GPA for each student (across all classes and all terms)
SELECT students.first_name,
students.last_name,
COUNT(class_registrations.student_id) as number_of_classes,
SUM( class_registrations.grade_id) as total_grade_points,
(SUM(class_registrations.grade_id)/COUNT(class_registrations.student_id) ) as GPA
from class_registrations
INNER JOIN students on class_registrations.student_id = students.student_id
Group by class_registrations.student_id;

-- Calculate the avg GPA for each class 
SELECT classes.code,
classes.name,
COUNT(class_registrations.class_section_id) as number_of_grades,
SUM( class_registrations.grade_id) as total_grade_points_earned,
(SUM(class_registrations.grade_id)/COUNT(class_registrations.class_section_id) ) as  GPA
from class_registrations
INNER JOIN class_sections on class_sections.class_section_id = class_registrations.class_section_id
INNER JOIN classes on class_sections.class_id = classes.class_id
Group by class_registrations.class_section_id;


-- Calculate the avg GPA for each class and term 
SELECT classes.code,
classes.name,
terms.name AS term,
COUNT(class_registrations.class_section_id) as number_of_grades,
SUM( class_registrations.grade_id) as total_grade_points_earned,
(SUM(class_registrations.grade_id)/COUNT(class_registrations.class_section_id) ) as  GPA
from class_registrations
INNER JOIN class_sections on class_sections.class_section_id = class_registrations.class_section_id
INNER JOIN classes on class_sections.class_id = classes.class_id
INNER JOIN terms on terms.term_id = class_sections.term_id
Group by class_sections.term_id, class_sections.class_section_id;

-- List all the classes being taught by an instructor (use instructor_id=1) WORKS
SELECT instructors.first_name,
		instructors.last_name,
        academic_titles.title,
        classes.code,
        classes.name as class_name,
        terms.name as term
	from class_sections
		INNER JOIN classes
			ON class_sections.class_id = classes.class_id
		INNER JOIN terms
			ON class_sections.term_id = terms.term_id
		INNER JOIN instructors
			ON instructors.instructor_id = class_sections.instructor_id
		INNER JOIN academic_titles
			ON instructors.academic_title_id = academic_titles.academic_title_id
WHERE class_sections.instructor_id = 1;
		
-- List all classes with terms & instructor WORKS
SELECT 
        classes.code,
        classes.name as name,
        terms.name as term,
        instructors.first_name,
		instructors.last_name
	from class_sections
		INNER JOIN classes
			ON class_sections.class_id = classes.class_id
		INNER JOIN terms
			ON class_sections.term_id = terms.term_id
		INNER JOIN instructors
			ON instructors.instructor_id = class_sections.instructor_id
		;


-- Calculate the remaining space left in a class
