import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3305/unfiltered_thoughts";
    private static final String USER = "devuser";
    private static final String PASSWORD = "217956";
    private static volatile Connection connection = null;
    private static Boolean allowConnectionReestablishment = true;


    // Private constructor to prevent instantiation
    private MySQLConnection() {}


    // Get the active connection
    public static Connection getConnection() {
        if (isConnectionClosed()) {
            try {
                connect();
            } catch (SQLException e) {
                System.err.println("Error reconnecting to the database.");
                e.printStackTrace();
            }
        }
        else if(!allowConnectionReestablishment){
            return null;
        }
        return connection;
    }

    // Establish a connection
    public static void connect() throws SQLException {
        // Close existing connection if open
        closeConnection();

        try {
            // Load MySQL JDBC driver (optional in modern Java)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }

        // Create a new connection
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connection to MySQL database established successfully.");
    }

    // Check if connection is closed
    public static Boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    // Close the connection
    public static void closeConnection() {
        if (!isConnectionClosed()) {
            try {
                connection.close();
                connection = null;
                System.out.println("Connection to database server closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection to database is already closed.");
        }
    }

    // Test the connection
    public static synchronized boolean tryConnect() {
        try {
            if (isConnectionClosed() && allowConnectionReestablishment) {
                connect();
            }
            System.out.println("Connection to database is active.");
            return !isConnectionClosed();

        } catch (SQLException e) {
            System.err.println("Failed to establish database connection.");
            e.printStackTrace();
            return false;
        }
    }
}
