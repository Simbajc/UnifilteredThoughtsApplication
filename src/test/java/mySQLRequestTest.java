import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class mySQLRequestTest {
    mySQLRequest sqlInteractor;
    @BeforeEach
    void before() throws SQLException {
        MySQLConnection.tryConnect();
        sqlInteractor = new mySQLRequest();
//        sqlInteractor.createJournalEntry("testUser", "example entry");
    }

    @Test
    void usernameExistTest() throws SQLException {

        // makes sure database can tell when there is not a user called
        assertEquals(0, sqlInteractor.usernameExists("noUser"));

        // checks if the database has the same username
        assertEquals(1, sqlInteractor.usernameExists("testUser"));

        // makes sure it can tell that the server connection is cut and return appropriate code
        MySQLConnection.closeConnection();
        assertEquals(102, sqlInteractor.usernameExists("testUser"));
    }

    @Test
    void validAccountTest() throws SQLException{
        // username should be in database
        assertEquals(1, sqlInteractor.validAccount("testUser", "test"));

        // username should not be in database
        assertEquals(0, sqlInteractor.validAccount("wrongUser", "wrongPass"));

        // username is correct, password is not
        assertEquals(0, sqlInteractor.validAccount("testUser", "wrongPass"));

        // password is correct, username is not
        assertEquals(0, sqlInteractor.validAccount("wrongUser", "test"));

        // makes sure it can tell that the server connection is cut and return appropriate code
        MySQLConnection.closeConnection();
        assertEquals(102, sqlInteractor.validAccount("testUser", "test"));
    }

    @Test
    void addAccountTest() throws SQLException{

        //check if add user exist
        assertEquals(1, sqlInteractor.addAccount("newUser", "test"));
        sqlInteractor.deleteAccount("newUser", "test", "test");

        // check if account is already there
        assertEquals(101, sqlInteractor.addAccount("testUser", "test"));

        // check if connection to database is cut
        MySQLConnection.closeConnection();
        assertEquals(102, sqlInteractor.addAccount("newUser", "test"));
    }

    @Test
    void updateAccountUsernameTest() throws SQLException {
        /*
        Assumptions:
        there exists an account that username is "testUser" and password is "test" and there is no username
        called updateTestUser1
        Test:
        username and passord is valid
        */
        assertEquals(1, sqlInteractor.updateAccountUsername("testUser", "updatedTestUser1", "test"));
        sqlInteractor.updateAccountUsername("updatedTestUser1", "testUser", "test");

        /*
        Assumptions:
        there exists an account that username is "testUser" and password is "test" and there is no username
        called updateTestUser
        Test:
        username is valid password is not valid
        */
        assertEquals(101, sqlInteractor.updateAccountUsername("testUser", "updatedTestUser", "wrongTest"));

        /*
        Assumptions:
        there exists an account that username is "testUser" and password is "test" and there is a username
        called updateTestUser2
        Test:
        updated username exists already
        */
        assertEquals(101, sqlInteractor.updateAccountUsername("testUser2", "updatedTestUser2", "test"));


        // makes sure it can tell that the server connection is cut and return appropriate code
        MySQLConnection.closeConnection();
        assertEquals(102, sqlInteractor.updateAccountUsername("testUser2", "updatedTestUser2", "test"));

    }


    @Test
    void updateAccountPasswordTest() throws SQLException{
        /*
        Assumptions:
        there exists an account that username is "testUser" and password is "test"
        Test:
        username and password is valid
        */
        assertEquals(1, sqlInteractor.updateAccountPassword("testUser", "test", "Updatetest"));
        sqlInteractor.updateAccountPassword("testUser", "Updatetest", "test");


        /*
        Assumptions:
        there exists an account that username is "testUser" and password is "test"
        Test:
        try putting the same password
        */
        assertEquals(101, sqlInteractor.updateAccountPassword("testUser", "test", "test"));


        /*
        Assumptions:
        there exists an account that username is "testUser" and password is "test" and there is no username
        called updateTestUser
        Test:
        try putting the valid username and wrong password
        */
        assertEquals(101, sqlInteractor.updateAccountPassword("testUser", "wrongPass", "test"));

        /*
        Assumptions:
        there exists an account that username is "testUser" and password is "test" and there is no username
        called updateTestUser
        Test:
        try putting the wrong username and valid password
        */
        assertEquals(101, sqlInteractor.updateAccountPassword("wrongUser", "test", "test1"));


        /*
        Assumptions:
        there exists an account that username is "testUser" and password is "test" and there is no username
        called updateTestUser
        Test:
        try putting the wrong username and wrong password
        */
        assertEquals(101, sqlInteractor.updateAccountPassword("wrongUser", "wrongPass", "test"));



        // makes sure it can tell that the server connection is cut and return appropriate code
        MySQLConnection.closeConnection();
        assertEquals(102, sqlInteractor.updateAccountPassword("testUser", "test", "updateTest"));
    }


    @Test
    void deleteAccountTest() throws SQLException {
        /*
        Assumptions:
        there exists an account that username is "testUser" and password is "test" and there is no username
        called updateTestUser
        Test:
        try putting the wrong username and wrong password
        */
        sqlInteractor.addAccount("testUser", "test");
        assertEquals(1, sqlInteractor.usernameExists("testUser"));
        assertEquals(1, sqlInteractor.deleteAccount("testUser", "test", "test"));
        if(sqlInteractor.usernameExists("testUser") == 0){
            System.out.println("User name is deleted from database");
        }
        assertEquals(0, sqlInteractor.usernameExists("testUser"));
//
        sqlInteractor.addAccount("testUser", "test");
    }


    @Test
    void journalEntryExistsTest() throws SQLException{
        sqlInteractor.createJournalEntry("testUser", "example entry");
        /*
        Assumptions: "testUser" valid username, 1 valid entry number
        Test:
        if journalEntryExists
         */
        assertEquals(1, sqlInteractor.journalEntryExists("testUser", 1));

        /*
        Assumptions: "wrongUser" invalid username, 1 valid entry number
        Test:
        if journalEntryExists
         */
        assertEquals(0, sqlInteractor.journalEntryExists("wrongUser", 1));


        /*
        Assumptions: "testUser" valid username, 1 invalid entry number
        Test:
        if journalEntryExists
         */
        assertEquals(0, sqlInteractor.journalEntryExists("testUser", 0));

        // makes sure it can tell that the server connection is cut and return appropriate code
        MySQLConnection.closeConnection();
        assertEquals(102, sqlInteractor.journalEntryExists("testUser", 1));



    }

    @Test
    void createJournalEntryTest() throws SQLException {
        /*
        Assumptions: "testUser" valid username, valid entry

         */
        assertEquals(1, sqlInteractor.createJournalEntry("testUser", "test for testUser"));

        /*
        Assumptions: "testUser" valid username, invalid entry

         */
        assertEquals(101, sqlInteractor.createJournalEntry("testUser", " "));

        /*
        Assumptions: "wrongUser" invalid username, invalid entry

         */
        assertEquals(101, sqlInteractor.createJournalEntry("wrongUser", "test"));

        // makes sure it can tell that the server connection is cut and return appropriate code
        MySQLConnection.closeConnection();
        assertEquals(102, sqlInteractor.createJournalEntry("testUser", "test"));


    }


    @Test
    void updateJournalEntryTest() throws SQLException{
        sqlInteractor.deleteAccount("testUser", "test", "test");
        sqlInteractor.addAccount("testUser", "test");
        sqlInteractor.createJournalEntry("testUser", "example entry");
        /*
        Assumptions: "testUser" valid username, 1 valid entry number, entry valid

         */
        assertEquals(1, sqlInteractor.updateJournalEntry("testUser", 1, "new journal entry"));


        /*
        Assumptions: "wrongUser" invalid username, 1 valid entry number, entry valid

         */
        assertEquals(101, sqlInteractor.updateJournalEntry("wrongUser", 1, "new journal entry"));


        /*
        Assumptions: "testUser" valid username, 1 invalid entry number, entry valid

         */
        assertEquals(101, sqlInteractor.updateJournalEntry("testUser", 0, "new journal entry"));
        sqlInteractor.deleteJournalEntry("testUser", 1);
        // makes sure it can tell that the server connection is cut and return appropriate code
        MySQLConnection.closeConnection();
        assertEquals(102, sqlInteractor.updateJournalEntry("testUser", 1, "new journal entry"));

    }


    @Test
    void deleteJournalEntryTest() throws SQLException{
        sqlInteractor.createJournalEntry("testUser", "example entry 1");
        sqlInteractor.createJournalEntry("testUser", "example entry 2");
        /*
        Assumptions: "testUser" valid username, 1 valid entry number

         */
        assertEquals(1, sqlInteractor.deleteJournalEntry("testUser", 1));

        /*
        Assumptions: "testUser" valid username,  invalid entry number

         */
        assertEquals(101, sqlInteractor.deleteJournalEntry("testUser", 0));

        /*
        Assumptions: "wrongUser" invalid username, 2 valid entry number

         */
        assertEquals(101, sqlInteractor.deleteJournalEntry("wrongUser", 2));
        sqlInteractor.deleteJournalEntry("testUser", 2);


        // makes sure it can tell that the server connection is cut and return appropriate code
        MySQLConnection.closeConnection();
        assertEquals(102, sqlInteractor.deleteJournalEntry("testUser", 1));

    }

    @Test
    void getListofEntryNumbersTest() throws SQLException{
        sqlInteractor.deleteAccount("testUser", "test", "test");
        sqlInteractor.addAccount("testUser", "test");
        sqlInteractor.createJournalEntry("testUser", "entry 1");
        sqlInteractor.createJournalEntry("testUser", "entry 2");

        ArrayList<Integer> test = new ArrayList<>();
        test.add(2);
        test.add(1);
        for(int i = 0; i < test.size(); i++){
            assertEquals(test.get(i), sqlInteractor.getListEntryNumbers("testUser").get(i));
        }

    }

    @Test
    void getEntryContentTest() throws SQLException{
        sqlInteractor.deleteAccount("testUser", "test", "test");
        sqlInteractor.addAccount("testUser", "test");
        sqlInteractor.createJournalEntry("testUser", "entry 1");
        sqlInteractor.createJournalEntry("testUser", "entry 2");

        /*
        Assumption: username "testUser" is valid, entry number 1 valid
         */
        assertEquals("entry 1", sqlInteractor.getEntryContent("testUser", 1).get(1));
        LocalDateTime after = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mma");

        String testTime = after.format(formatter);

        assertEquals(testTime.replace("T", " "), sqlInteractor.getEntryContent("testUser", 1).get(0));
        sqlInteractor.deleteJournalEntry("testUser", 1);

        /*
        Assumption: username "testUser" is valid entry number 1 not valid
         */
        assertEquals(new ArrayList<>(), sqlInteractor.getEntryContent("testUser", 1));
        sqlInteractor.deleteAccount("testUser", "test", "test");
        sqlInteractor.addAccount("testUser", "test");

    }

    @Test
    void getNumEntriesTest() throws SQLException {
        sqlInteractor.deleteAccount("testUser", "test", "test");
        sqlInteractor.addAccount("testUser", "test");
        /*
        Assumption: username "testUser" is valid
         */
        assertEquals(0, mySQLRequest.getNumOfEntries("testUser"));

        /*
        Assumption: username "testUser" is valid
         */
        sqlInteractor.createJournalEntry("testUser", "test entry");
        assertEquals(1, mySQLRequest.getNumOfEntries("testUser"));

        /*
        Assumption: username "testUser" is valid
         */
        sqlInteractor.createJournalEntry("testUser", "test entry");
        assertEquals(2, mySQLRequest.getNumOfEntries("testUser"));


        // makes sure it can tell that the server connection is cut and return appropriate code
        MySQLConnection.closeConnection();
        assertEquals(102, sqlInteractor.getNumOfEntries("testUser"));

    }




    @Test
    void getLastLoginTest() throws SQLException {
        sqlInteractor.deleteAccount("testUser", "test", "test");
        sqlInteractor.addAccount("testUser", "test");
        /*
        Assumption: last login is the date that user created account
         */
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Custom format
        String formattedDate = currentDate.format(formatter);
//        Integer intDate = Integer.parseInt(formattedDate);
        assertEquals(formattedDate + "", sqlInteractor.getLastLogin("testUser") + "");
    }


    @Test
    void getAccountCreationDateTest() throws SQLException {
        sqlInteractor.deleteAccount("testUser", "test", "test");
        sqlInteractor.addAccount("testUser", "test");
        /*
        Assumption: last login is the date that user created account
         */
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Custom format
        String formattedDate = currentDate.format(formatter);
//        Integer intDate = Integer.parseInt(formattedDate);
        assertEquals(formattedDate + "", sqlInteractor.getAccountCreationDate("testUser") + "");
    }




    @Test
    void getLoginStreakTest() throws SQLException{
        sqlInteractor.deleteAccount("testUser", "test", "test");
        sqlInteractor.addAccount("testUser", "test");
        assertEquals(0, mySQLRequest.getLoginStreak("testUser"));

        if(sqlInteractor.updateLastLoginAndLoginStreak("testUser") != 1){
            System.out.println("Issue with updating last login");
        }
//        assertEquals(1, mySQLRequest.getLoginStreak("testUser"));
    }


}
