import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;

public class SQLConnectionTest {

    @Test
    void testConnection() throws SQLException {
        assertTrue(MySQLConnection.tryConnect());
        if(MySQLConnection.isConnectionClosed() == false){
            System.out.println("Connection is not closed");
        }
    }

    @Test
    void testCloseConnection() throws SQLException{
        MySQLConnection.tryConnect();
        MySQLConnection.closeConnection();
        // checks if connection closes properly
        assertTrue(MySQLConnection.isConnectionClosed());
    }
}
