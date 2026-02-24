package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            // Load the MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
           
            // Removed backticks from the database name
            String url = "jdbc:mysql://localhost:3306/oceanviewresort";
            String user = "root";
            String pass = "Naveen#2002";
            
            // Establish Connection
            connection = DriverManager.getConnection(url, user, pass);
            
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        } catch (SQLException ex) {
            System.out.println("SQL Exception: " + ex.getMessage());
            System.out.println("SQL State: " + ex.getSQLState());
            System.out.println("Vendor Error: " + ex.getErrorCode());
        }
        
        return connection;
    }
}