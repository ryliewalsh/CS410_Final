import java.sql.*;
public class Database {

    public static Connection getDatabaseConnection() throws SQLException {
            int databasePort = Integer.parseInt(System.getenv("CS410_PORT"));
            String databaseHost = System.getenv("CS410_HOST");
            String databaseUsername = System.getenv("CS410_USERNAME");
            String databasePassword = System.getenv("CS410_PASSWORD");
            String databaseName = System.getenv("CS410_DATABASE");
            String databaseURL = String.format(
                    "jdbc:mysql://%s:%s/%s",
                    databaseHost,
                    databasePort,
                    databaseName);

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                return DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
            } catch (SQLException sqlException) {
                System.out.printf("SQLException was thrown while trying to connection to database: %s%n", databaseURL);
                System.out.println(sqlException.getMessage());
                throw sqlException;
            }
            catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }

        }



        public static void testConnection() {
            System.out.println("Attempting to connect to MySQL database using:");
            System.out.printf("CS410_HOST: %s%n", System.getenv("CS410_HOST"));
            System.out.printf("CS410_PORT: %s%n", System.getenv("CS410_PORT"));
            System.out.printf("CS410_USERNAME: %s%n", System.getenv("CS410_USERNAME"));
            System.out.printf("CS410_PASSWORD: %s%n", System.getenv("CS410_PASSWORD"));
            System.out.printf("CS410_DATABASE: %s%n", System.getenv("CS410_DATABASE"));
    
            Connection connection = null;
            ResultSet resultSet = null;
    
            try{
                connection = getDatabaseConnection();
                Statement sqlStatement = connection.createStatement();
                String sql = "SELECT VERSION();";
                resultSet = sqlStatement.executeQuery(sql);
                resultSet.next();
                System.out.printf("Connected SUCCESS! Database Version: %s%n", resultSet.getString(1));
            } catch (SQLException sql){
                System.out.println("Failed to connect to database! Please make sure your Environment variables are set!");
            } finally {
                try { resultSet.close(); } catch (Exception e) { /* ignored */ }
                try { connection.close(); } catch (Exception e) { /* ignored */ }
            }
        }
}
