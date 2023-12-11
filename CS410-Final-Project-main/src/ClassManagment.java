import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassManagment {
    private Connection connection;
    private int activeClassId;

    public ClassManagment() {
        try{
        this.connection = Database.getDatabaseConnection();
        this.activeClassId = -1;
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void showAssignments() {
        if(activeClassId != -1) {
            try{
                String sql = "SELECT a.assignment_name, c.category_name, a.description, a.point_value " +   
                             "FROM assignments a " + 
                             "JOIN categories c on a.category_id = c.category_id " +
                             "WHERE c.class_id = ?";
                try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, activeClassId);

                    try(ResultSet results = preparedStatement.executeQuery()) {
                        System.out.println("Assignments and Points");
                        while(results.next()) {
                            String assignmentName = results.getString("assignment_name");
                            String categoryName = results.getString("category_name");
                            String description = results.getString("description");
                            int points = results.getInt("point_value");

                            System.out.println("Assignments: " + assignmentName + " | Category: " + categoryName + " | Description: " + description + " | Points: " + points);
                        }
                    }
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No active class. Use 'select-class' to activate a class.");
        }
    }

    public void addAssignment(String assignmentName, String categoryName, String description, String points) {
        if (activeClassId != -1) {
            try (Connection connection = Database.getDatabaseConnection()) {
                int categoryId = getCategoryIDByName(connection, activeClassId, categoryName);

                if (categoryId != -1) {
                    String sql = "INSERT INTO assignments (assignment_name, category_id, description, point_value) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setString(1, assignmentName);
                        preparedStatement.setInt(2, categoryId);
                        preparedStatement.setString(3, description);
                        preparedStatement.setInt(4, Integer.parseInt(points));

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            System.out.printf("Assignment: %s added successfully.%n", assignmentName);
                        } else {
                            System.out.printf("Failed to add Assignment: %s.%n", assignmentName);
                        }
                    }
                } else {
                    System.out.println("Category not found. Please create the category first using 'add-category'.");
                }
            } catch (SQLException e) {
                e.printStackTrace(); 
            }
        } else {
            System.out.println("No active class. Use 'select-class' to activate a class.");
        }
    }

    private int getCategoryIDByName(Connection connection, int classId, String categoryName) {
        String sql = "SELECT category_id FROM categories WHERE class_id = ? AND name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, classId);
            preparedStatement.setString(2, categoryName);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("category_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
        return -1; // Return -1 if the category is not found
    }
    

    public void showCategories() {
        if(activeClassId != -1) {
            try {
                String sql = "SELECT category_name, weight from categories WHERE class_id = ?";
                try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, activeClassId);
                    
                    try(ResultSet results = preparedStatement.executeQuery()) {
                        System.out.println("Categories and Weights:");
                        while (results.next()) {
                            String categoryName = results.getString("category_name");
                            double weight = results.getDouble("weight");

                            System.out.println("Category: " + categoryName + " | Weight: " + weight);
                        }
                    }
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No active class. Use 'select-class' to activate a class.");
        }
    }

    public void addCategory(String categoryName, String weight) {
        if(activeClassId != -1) {
            try {
                String sql = "INSERT INTO categories (class_id, category_name, weight) VALUES (?, ?, ?)";

                try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, activeClassId);
                    preparedStatement.setString(2, categoryName);
                    preparedStatement.setDouble(3, Double.parseDouble(weight));
                    int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.printf("Category: %s inserted successfully.%n", categoryName);
                } else {
                    System.out.printf("Failed to insert Category: %s.%n", categoryName);
                }
                }
            } catch(SQLException e) {
                    e.printStackTrace();
                }
        } else {
            System.out.println("No active class. Use 'select-class' to activate a class.");
        }
    }

    public void showActiveClass() {
        try {
            String sql = "SELECT c.course_number, c.term, c.section_number, c.description, COUNT(e.student_id) AS num_students " +
                         "FROM classes c " +
                         "LEFT JOIN enrollments e ON c.class_id = e.class_id " +
                         "WHERE c.class_id = ? " +  
                         "GROUP BY c.course_number, c.term, c.section_number, c.description";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, activeClassId);
    
                try (ResultSet results = preparedStatement.executeQuery()) {
                    if (results.next()) {
                        String courseNum = results.getString("course_number");
                        String term = results.getString("term");
                        int sectionNum = results.getInt("section_number");
                        String description = results.getString("description");
                        int numStudents = results.getInt("num_students");
    
                        System.out.println("Course: " + courseNum + " | Term: " + term +
                                            " | Section: " + sectionNum + " | Description: " + description +
                                            " | Num Students: " + numStudents);
                    } else {
                        System.out.println("No active class found.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public void activateClassForMostRecentTerm(String courseNumber) {
        try {
            String sql = "SELECT class_id FROM classes WHERE course_number = ? AND term = (SELECT MAX(term) FROM classes WHERE course_number = ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, courseNumber);
                preparedStatement.setString(2, courseNumber);
    
                try (ResultSet results = preparedStatement.executeQuery()) {
                    if (results.next()) {
                        activeClassId = results.getInt("class_id");
    
                        // Check if there is another row
                        if (results.next()) {
                            System.out.println("Multiple sections found for course " + courseNumber + " in the most recent term. Activation failed.");
                            activeClassId = -1; // Reset activeClassId
                        } else {
                            System.out.println("Class activated: " + courseNumber);
                        }
                    } else {
                        System.out.println("No sections found for course " + courseNumber);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
    }
    

    public void activateClassForSpecificTerm(String courseNumber, String term) {
        try {
            String sql = "SELECT class_id from classes WHERE course_number = ? AND term = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, courseNumber);
                preparedStatement.setString(2, term);

                try(ResultSet results = preparedStatement.executeQuery()) {
                    if(results.next()) {
                        activeClassId = results.getInt("class_id");

                        if(results.next()) {
                            System.out.println("Multiple sections found for course " + courseNumber +" - Term: " + term + "Activation failed.");
                            activeClassId = -1;
                        } else {
                            System.out.println("Class activated: " + courseNumber + " - Term: " + term);
                        }
                    } else {
                        System.out.println("No sections found for course " + courseNumber + " - Term: " + term);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    public void activateSpecificSection(String courseNumber, String term, String sectionNumber) {
         try {
            String sql = "SELECT class_id from classes WHERE course_number = ? AND term = ? AND section_number = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, courseNumber);
                preparedStatement.setString(2, term);
                preparedStatement.setString(3, sectionNumber);

                try(ResultSet results = preparedStatement.executeQuery()) {
                    if(results.next()) {
                        activeClassId = results.getInt("class_id");

                        if(results.next()) {
                            System.out.println("Multiple sections found for course " + courseNumber +" - Term: " + term + " - Section: " + sectionNumber + "Activation failed.");
                            activeClassId = -1;
                        } else {
                            System.out.println("Class activated: " + courseNumber + " - Term: " + term + " - Section: " + sectionNumber);
                        }
                    } else {
                        System.out.println("No sections found for course " + courseNumber + " - Term: " + term + " - Section: " + sectionNumber);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void  listClasses() {
        try {
            String sql = "SELECT c.course_number, c.term, c.section_number, c.description, COUNT(e.student_id) AS num_students " +
                         "FROM classes c " +
                         "LEFT JOIN enrollments e ON c.class_id = e.class_id " +
                         "GROUP BY c.course_number, c.term, c.section_number, c.description";
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                ResultSet results = preparedStatement.executeQuery(); {
                    while (results.next()) {
                        String courseNum = results.getString("course_number");
                        String term = results.getString("term");
                        int sectionNum = results.getInt("section_number");
                        String description = results.getString("description");
                        int numStudents = results.getInt("num_students");

                        System.out.println("Course: " + courseNum + " | Term: " + term +
                                            " | Section: " + sectionNum + " | Description: " + description +
                                            " | Num Students: " + numStudents);  
                        
                    }

                }
            } 
 
        } catch(SQLException e) {
                e.printStackTrace();
            }
    }

    public void addClass(String classNumber, String classTerm, String sectionNum, String classDesc) {
        try {
        String sql = "INSERT INTO classes (course_number, term, section_number, description) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, classNumber);
            preparedStatement.setString(2, classTerm);
            preparedStatement.setInt(3, Integer.parseInt(sectionNum));
            preparedStatement.setString(4, classDesc);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Class inserted successfully.");
            } else {
                System.out.println("Failed to insert class.");
            }
        }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
    }

    public void processCommand(String command) {
        String[] tokens = command.split("\\s+");
        switch(tokens[0]){
                case "test":
                    Database.testConnection();
                    break;
                case "help":
                    System.out.println("-".repeat(38) + "Help" + "-".repeat(38));
                    System.out.println("Items in < > are required items and items in [ ] are optional");
                    System.out.println("test \n\tTests the database connection");

                    System.out.println("new-class <classNumber> <classTerm> <sectionNumber> <classDescription> \n\tAdds a new class");
                    System.out.println("list-classes \n\tLists all classes");
                    System.out.println("select-class <courseNumber> [term] [sectionNumber] \n\tActivates a class based on criteria passed into the system");
                    System.out.println("show-class \n\tShows current active class");
                    System.out.println("add-category <category-name> <weight> \n\tAdds a new category to active class");
                    System.out.println("show-categories \n\tShows categories for active class");
                    System.out.println("add-assignment <assignment-name> <category-name> <description> <point-value> \n\tAdds a new assignment to active class");
                    System.out.println("show-assignments \n\tShows assignments for active class");
                    System.out.println("help \n\tlists help information");
                    System.out.println("quit \n\tExits the program");
                    break;
                case "new-class":
                    if (tokens.length >= 5) {
                        addClass(tokens[1], tokens[2], tokens[3], tokens[4]);
                    } else {
                        System.out.println("Invalid input. Please use the format: new-class <classNumber> <classTerm> <sectionNumber> <classDescription>");
                    }
                    break;
                case "list-classes":
                    listClasses();
                    break;
                case "select-class":
                    if(tokens.length == 2) {
                        activateClassForMostRecentTerm(tokens[1]);
                    } else if (tokens.length == 3) {
                        activateClassForSpecificTerm(tokens[1], tokens[2]);
                    } else if (tokens.length == 4) {
                        activateSpecificSection(tokens[1], tokens[2], tokens[3]);
                    } else {
                        System.out.println("Invalid select-class command. Usage: select-class <courseNumber> [term] [sectionNumber]");
                    }
                    break;
                case "show-class":
                    if(activeClassId ==-1) {
                        System.out.println("No active class, please activate a class by using select-class <courseNumber> [term] [sectionNumber]");
                    } else {
                        showActiveClass();
                    }
                    break;
                case "add-category":
                    if(tokens.length == 3) {
                        addCategory(tokens[1], tokens[2]);
                    } else {
                        System.out.println("Invalid add-category command. Usage: add-category <Name> <Weight>");
                    }
                    break;
                case "show-categories":
                    showCategories();
                    break;
                case "add-assignment":
                    if(tokens.length == 5) {
                        addAssignment(tokens[1], tokens[2], tokens[3], tokens[4]);
                    } else {
                        System.out.println("Invalid add-assignment command. Usage: add-assignment <name> <category> <description> <points>");
                    }
                    break;
                case "show-assignments":
                    showAssignments();
                    break;
                case "quit":
                    break;
                default:
                    System.out.println(command);
                    System.out.println("Command not found. Enter 'help' for list of commands");
                    break;

            }
    }

    public void runShell() {
        System.out.println("Welcome to the School Management System");
        System.out.println("-".repeat(80));
        Scanner scan = new Scanner(System.in);
        String command;
        do {
            System.out.print("Command: ");
            command = scan.nextLine();
            processCommand(command);
            System.out.println("-".repeat(80));
        } while(!(command.equals("quit") || command.equals("exit")));
        
        System.out.println("Exiting Classroom Management System");
        scan.close();
    }

    

    public static void main(String[] args) {        
        ClassManagment cm = new ClassManagment();
        cm.runShell();
    }
}