import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This application will keep track of things like what classes are offered by
 * the school, and which students are registered for those classes and provide
 * basic reporting. This application interacts with a database to store and
 * retrieve data.
 */
public class SchoolManagementSystem {

    public static void getAllClassesByInstructor(String first_name, String last_name) {
        Connection connection = null;
        Statement sqlStatement = null;
        	 ResultSet results = null;
             try {
             	connection = Database.getDatabaseConnection();
             	sqlStatement =connection.createStatement();
             	String query = "SELECT instructors.first_name, instructors.last_name, academic_titles.title, classes.code,"
             			+ " classes.name, terms.name FROM class_sections\n"
             			+ "INNER JOIN instructors on instructors.instructor_id = class_sections.instructor_id\n"+
             			"INNER JOIN classes on classes.class_id = class_sections.class_id\n"+
             			"INNER JOIN academic_titles on academic_titles.academic_title_id = instructors.academic_title_id\n"+
             			"INNER JOIN terms ON class_sections.term_id = terms.term_id\n" +
             			"WHERE instructors.first_name = '" + first_name + "' AND instructors.last_name = '" + last_name +  "'";
             			
             	
             	results = sqlStatement.executeQuery(query);
             	System.out.println("First Name | Last Name | Title | Code | Name | Term");
             	System.out.println("-------------------------------------------------------------------------------");

                  while (results.next()) {
                 
                      String firstName= results.getString(1);
                      String lastName = results.getString(2);
                      String code = results.getString(3);
                      String className = results.getString(4);
                      String term = results.getString(5);
                      String grade = results.getString(6);
                      

                     System.out.println( firstName + " | " + lastName + " | " + code +
                     		" | "+ className + " | " + term + " | " + grade);
                  }
      
            
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void submitGrade(String studentId, String classSectionID, String grade) {
        Connection connection = null;
        Statement sqlStatement = null;
        ResultSet results = null;
        
        try {
        	connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            
            String query = ("UPDATE class_registrations \n"
            		+ "SET grade_id = convert_to_grade_point('"
  
            + grade
            +"')\n"
            
            +"WHERE student_id = '"
            +studentId
            + "' AND class_section_id = '"
            + classSectionID
            +"'"
            );
            sqlStatement.executeUpdate(query);
        	
        } catch (SQLException sqlException) {
            System.out.println("Failed to submit grade");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void registerStudent(String studentId, String classSectionID) {
        Connection connection = null;
        Statement sqlStatement = null;
        ResultSet results = null;

        try {

            connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            
            String register = ("INSERT INTO class_registrations (student_id, class_section_id )\n" +
            "VALUES ( '"
  
            + studentId
            +"' ,'"
            +classSectionID
            + "')"
            );
            sqlStatement.executeUpdate(register);
            
            String query = ("SELECT * From class_registrations\n"+
            		"WHERE student_id = " + studentId + 
            		" AND class_section_id = " + classSectionID );

            results = sqlStatement.executeQuery(query);
            
            System.out.println("Class Registration ID | Student ID | Class Section ID");
        	System.out.println("-------------------------------------------------------------------------------");

             while (results.next()) {
            	 int id = results.getInt(1);
                 String studId = results.getString(2);
                 String classId = results.getString(3);
                 

                System.out.println(id + " | " + studId + " | " + classId);
             }
        	
        } catch (SQLException sqlException) {
            System.out.println("Failed to register student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void deleteStudent(String studentId) {
        Connection connection = null;
        Statement sqlStatement = null;
        ResultSet results = null;
        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement = connection.createStatement();
        	String deleteStudent = "DELETE FROM students WHERE student_id = '"+
        	studentId + "'";
        	sqlStatement.executeUpdate(deleteStudent);
        	System.out.println("Student with id: " + studentId + " was deleted");
             
        } catch (SQLException sqlException) {
            System.out.println("Failed to delete student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void createNewStudent(String firstName, String lastName, String birthdate) {
        Connection connection = null;
        Statement sqlStatement = null;
        ResultSet results = null;
        
        try {

            connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            
            String add = ("INSERT INTO students (first_name, last_name, birthdate)\n" +
            "VALUES ('"
            +  firstName 
            + "', '"
            + lastName
            +"' ,'"
            +birthdate
            + "')"
            );
            sqlStatement.executeUpdate(add);
            
            String query = ("SELECT * from students\n"+
            		"WHERE students.first_name = '" + firstName + 
            		"' AND students.last_name = '" + lastName +  "'");

            results = sqlStatement.executeQuery(query);
            
            System.out.println("Student ID | First Name | Last Name | Birthdate ");
        	System.out.println("-------------------------------------------------------------------------------");

             while (results.next()) {
            	 int id = results.getInt(1);
                 String first = results.getString(2);
                 String last = results.getString(3);
                 String birthDate = results.getString(4);

                System.out.println(id + " | " + first + " | " + last + " | " + birthDate);
             }
            
            
        } catch (SQLException sqlException) {
            System.out.println("Failed to create student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void listAllClassRegistrations() {
        Connection connection = null;
        Statement sqlStatement = null;

        ResultSet results = null;
        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement =connection.createStatement();
        	String query = "SELECT students.student_id, class_sections.class_section_id, students.first_name, students.last_name, classes.code, "
        			+ "classes.name, terms.name, convert_to_letter_grade(grade_id) FROM class_registrations\n"
        			+ "INNER JOIN students on students.student_id = class_registrations.student_id\n"+
        			"INNER JOIN class_sections on class_sections.class_section_id = class_registrations.class_section_id\n"+
        			"INNER JOIN classes on classes.class_id = class_sections.class_id\n"+
        			"INNER JOIN terms ON class_sections.term_id = terms.term_id";
        	
        	results = sqlStatement.executeQuery(query);
        	System.out.println("Student ID | Class Section ID | First Name | Last Name | Code | Name | Term | Letter Grade ");
        	System.out.println("-------------------------------------------------------------------------------");

             while (results.next()) {
            	 int studentId= results.getInt(1);
            	 int classId= results.getInt(2);
                 String firstName= results.getString(3);
                 String lastName = results.getString(4);
                 String code = results.getString(5);
                 String className = results.getString(6);
                 String term = results.getString(7);
                 String grade = results.getString(8);
                 

                System.out.println(studentId + " | " + classId + " | "+ firstName + " | " + lastName + " | " + code +
                		" | "+ className + " | " + term + " | " + grade);
             }
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void listAllClassSections() {
        Connection connection = null;
        Statement sqlStatement = null;

        ResultSet results = null;
        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement =connection.createStatement();
        	String query = "SELECT class_sections.class_section_id, classes.code, classes.name, terms.name FROM class_sections\n"
        			+ "INNER JOIN classes on classes.class_id = class_sections.class_id\n"+
        			"INNER JOIN terms ON class_sections.term_id = terms.term_id";
        	results = sqlStatement.executeQuery(query);
        	System.out.println("Class Section ID | Code | Name | Term ");
        	System.out.println("-------------------------------------------------------------------------------");

             while (results.next()) {
            	 int id = results.getInt(1);
                 String code = results.getString(2);
                 String name = results.getString(3);
                 String term = results.getString(4);

                System.out.println(id + " | " + code + " | " + name + " | " + term);
             }
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void listAllClasses() {
        Connection connection = null;
        Statement sqlStatement = null;
        ResultSet results = null;
        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement =connection.createStatement();
        	String query = "SELECT * FROM classes";
        	results = sqlStatement.executeQuery(query);
        	System.out.println("Class ID | Code | Name | Description ");
        	System.out.println("-------------------------------------------------------------------------------");

             while (results.next()) {
            	 int id = results.getInt(1);
                 String name = results.getString(2);
                 String description = results.getString(3);
                 String code = results.getString(4);

                System.out.println(id + " | " + code + " | " + name + " | " + description);
             } 
        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void listAllStudents() {
        Connection connection = null;
        Statement sqlStatement = null;
        ResultSet results = null;
        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement =connection.createStatement();
        	String query = "SELECT * FROM students";
        	results = sqlStatement.executeQuery(query);
        	System.out.println("Student ID | First Name | Last Name | Birthdate ");
        	System.out.println("-------------------------------------------------------------------------------");

             while (results.next()) {
            	 int id = results.getInt(1);
                 String firstName = results.getString(2);
                 String lastName = results.getString(3);
                 String birthDate = results.getString(4);

                System.out.println(id + " | " + firstName + " | " + lastName + " | " + birthDate);
             }
            
        
        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

   

	/***
     * Splits a string up by spaces. Spaces are ignored when wrapped in quotes.
     *
     * @param command - School Management System cli command
     * @return splits a string by spaces.
     */
    public static List<String> parseArguments(String command) {
        List<String> commandArguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
        while (m.find()) commandArguments.add(m.group(1).replace("\"", ""));
        return commandArguments;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the School Management System");
        System.out.println("-".repeat(80));

        Scanner scan = new Scanner(System.in);
        String command = "";

        do {
            System.out.print("Command: ");
            command = scan.nextLine();
            ;
            List<String> commandArguments = parseArguments(command);
            command = commandArguments.get(0);
            commandArguments.remove(0);

            if (command.equals("help")) {
                System.out.println("-".repeat(38) + "Help" + "-".repeat(38));
                System.out.println("test connection \n\tTests the database connection");

                System.out.println("list students \n\tlists all the students");
                System.out.println("list classes \n\tlists all the classes");
                System.out.println("list class_sections \n\tlists all the class_sections");
                System.out.println("list class_registrations \n\tlists all the class_registrations");
                System.out.println("list instructor <first_name> <last_name>\n\tlists all the classes taught by that instructor");


                System.out.println("delete student <studentId> \n\tdeletes the student");
                System.out.println("create student <first_name> <last_name> <birthdate> \n\tcreates a student");
                System.out.println("register student <student_id> <class_section_id>\n\tregisters the student to the class section");

                System.out.println("submit grade <studentId> <class_section_id> <letter_grade> \n\tcreates a student");
                System.out.println("help \n\tlists help information");
                System.out.println("quit \n\tExits the program");
            } else if (command.equals("test") && commandArguments.get(0).equals("connection")) {
                Database.testConnection();
            } else if (command.equals("list")) {
                if (commandArguments.get(0).equals("students")) listAllStudents();
                if (commandArguments.get(0).equals("classes")) listAllClasses();
                if (commandArguments.get(0).equals("class_sections")) listAllClassSections();
                if (commandArguments.get(0).equals("class_registrations")) listAllClassRegistrations();

                if (commandArguments.get(0).equals("instructor")) {
                    getAllClassesByInstructor(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("create")) {
                if (commandArguments.get(0).equals("student")) {
                    createNewStudent(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("register")) {
                if (commandArguments.get(0).equals("student")) {
                    registerStudent(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("submit")) {
                if (commandArguments.get(0).equals("grade")) {
                    submitGrade(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("delete")) {
                if (commandArguments.get(0).equals("student")) {
                    deleteStudent(commandArguments.get(1));
                }
            } else if (!(command.equals("quit") || command.equals("exit"))) {
                System.out.println(command);
                System.out.println("Command not found. Enter 'help' for list of commands");
            }
            System.out.println("-".repeat(80));
        } while (!(command.equals("quit") || command.equals("exit")));
        System.out.println("Bye!");
    }
}

