import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/*
CODE MEANING
100 - Working Correctly
101 - Server is connected but could not execute the function (Functions that modify database)
102 - Server is not connected
103 - Error for getLastLogin
 */
public class  mySQLRequest {
    static Connection connection = null;
    mySQLRequest() throws SQLException {
    /*
    get instance of cconection
     */
        connection = MySQLConnection.getConnection();
    }



    private static boolean validConnection(){
    /*
    check if connection to database is secure and is not severed
     */
        return !MySQLConnection.isConnectionClosed();
    }

    public static Boolean establishConnection() throws SQLException {
    /*
    establishes connection to the database
     */
        if(MySQLConnection.isConnectionClosed()){
            if(MySQLConnection.tryConnect()){
                connection = MySQLConnection.getConnection();
                return true;
            }
            return false;
        }

        return true;
    }


    public static Boolean closeConnection() throws SQLException {
    /*
    close connection the database
     */
        MySQLConnection.closeConnection();
        return MySQLConnection.isConnectionClosed();
    }


    public static Integer usernameExists(String username) throws SQLException {
    /*
    check if username exists in database
     */
        if(!validConnection()){
            return 102;
        }

        String sqlQuery = "SELECT COUNT(username) AS user_count FROM accounts WHERE username = ?";

        // prevent SQL injections
        try(PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)){
            preparedStatement.setString(1, username);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // if result of user is 1 it's not unique so return 0
                    if(resultSet.getInt("user_count") >= 1){
                        return 1;
                    }
                    // if result is 0 return 1 as it is unique
                    else{
                        return 0;
                    }
                }
            }
        }
        return 102;
    }



    public static Integer addAccount(String username, String password) throws SQLException {
    /*
    insert account username and password in the database
     */
        if(!validConnection()){
            return 102;
        }
        // makes sure username is unique and there is a valid connection
        int code = usernameExists(username);
        if(code == 1){
            return 101;
        }

        String sqlQuery = "INSERT INTO accounts (username, password) VALUES(?, ?)";

        // prevents SQL injections
        try(PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)){
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the query
            int rowAffected = preparedStatement.executeUpdate();
            code = usernameExists(username);
            // check if query executed or not
            if(rowAffected == 1 && code == 1){
                return 1;
            }
            //account was not added properly
            else if(code == 0){
                return 101;
            }
            // database connection cut
            else{
                return code;
            }
        }
    }



    public static Integer validAccount(String username, String password) throws SQLException{
    /*
    check if the account is a valid one or not
     */
        if(!validConnection()){
            return 102;
        }

        String sqlQuery = "SELECT COUNT(username) AS user_count FROM accounts WHERE username = ? AND password = ?";
        // prevent SQL injections
        try(PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // is result is 1 user exists
                    if (resultSet.getInt("user_count") == 1) {
                        return 1;
                    }
                    // if result is 0 it does not exist
                    else{
                        return 0;
                    }
                }
            }

        }
        return 102;
    }



    public Integer updateAccountUsername(String username, String newUsername, String password) throws SQLException {
    /*
    update account username
     */
        if (!validConnection()) {
            return 102;
        }
        // check is the new username is unique
        int code = usernameExists(newUsername);
        if (code == 1) {
            return 101;
        }

        // make sure that this is a valid account
        code = validAccount(username, password);
        if (code != 1) {
            return 101;
        }

        //assumption is the newUsername is a valid username

        String sqlQuery = "UPDATE accounts SET username = ? WHERE username = ?";


        // prevent SQL injections
        try (PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, newUsername);
            preparedStatement.setString(2, username);


            // Execute the query
            int rowAffected = preparedStatement.executeUpdate();
            code = usernameExists(newUsername);
            // check if query executed or not
            if (rowAffected == 1 && code == 1) {
                return 1;
            }
            // connection to database got cut or did not update properly
            else {
                return 101;
            }


        }
    }



    public Integer updateAccountPassword(String username, String password, String newPassword) throws SQLException {
    /*
    update account password
     */
        if (!validConnection()) {
            return 102;
        }

        // make sure that this is a valid account
        int code = validAccount(username, password);
        if (code != 1) {
            return 101;
        }

        // make sure old and new password are not the same
        if(Objects.equals(password, newPassword)) {
            return 101;
        }

        //assumption is the newPassword is a valid password
        String sqlQuery = "UPDATE accounts SET password = ? WHERE username = ?";


        // prevent SQL injections
        try (PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);


            // Execute the query
            int rowAffected = preparedStatement.executeUpdate();
            // check if query executed or not
            if (rowAffected == 1) {
                return 1;
            }
            // connection to database got cut or did not update properly
            else {
                return 101;
            }


        }

    }



    public Integer deleteAccount(String username, String password, String repeatPassword) throws SQLException {
    /*
    delete account
     */
        if (!validConnection()) {
            return 102;
        }

        // make sure that this is a valid account
        int code = validAccount(username, password);
        if (code != 1) {
            return 101;
        }

        // make sure old and new password are not the same
        if(!Objects.equals(password, repeatPassword)) {
            return 101;
        }

        //assumption is the newPassword is a valid password
        String sqlQuery = "DELETE FROM accounts WHERE username = ?";


        // prevent SQL injections
        try (PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);


            // Execute the query
            int rowAffected = preparedStatement.executeUpdate();
            // check if query executed or not
            if (rowAffected == 1 && usernameExists(username) == 0) {
                return 1;
            }
            // connection to database got cut or did not update properly
            else {
                return 101;
            }


        }

    }




    public  Integer journalEntryExists(String username, Integer entryNum) throws SQLException{
    /*
    check if journal entry exists
     */
        if(!validConnection()){
            return 102;
        }
        else if(usernameExists(username) == 0){
            return 0;
        }

        String sqlQuery = "SELECT COUNT(*) AS entry_count FROM journal_entries " +
                "WHERE account_username = ? AND entry_number = ?";


        // prevent SQL injections
        try(PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, entryNum);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // is result is 1 user exists
                    if (resultSet.getInt("entry_count") == 1) {
                        return 1;
                    }
                    // if result is 0 it does not exist
                    else{
                        return 0;
                    }
                }
            }

        }
        return 102;
    }



    public  Integer createJournalEntry(String username, String entry) throws SQLException{
    /*
    add journal entry
     */
        if(!validConnection()){
            return 102;
        }
        else if(usernameExists(username) == 0){
            return 101;
        }
        // make sure entry is within the limit
        int entryLen = entry.trim().length();
        if(entryLen > 2000 || entryLen == 0){
            return 101;
        }

        //assumption is the entry is valid
        String sqlQuery = "INSERT INTO journal_entries (account_username, content)" +
                " VALUES( ? , ? )";

        // prevent SQL injections
        try (PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, entry);


            // Execute the query
            int rowAffected = preparedStatement.executeUpdate();
            // check if query executed or not
            if (rowAffected == 1) {
                updateNumEntries(username);
                return 1;
            }
            // connection to database got cut or did not update properly
            else {
                return 101;
            }


        }

    }



    public  Integer updateJournalEntry(String username, Integer entryNum, String entry) throws SQLException {
    /*
    update journal entry
     */
        if(!validConnection()){
            return 102;
        }
        if(journalEntryExists(username, entryNum) != 1){
            System.out.println("update entry: journal does not exist");
            return 101;
        }
        // make sure entry is within the limit
        int entryLen = entry.trim().length();
        if(entryLen > 2000 || entryLen == 0){
            return 101;
        }

        String sqlQuery = "UPDATE journal_entries SET content = ? WHERE account_username = ? AND entry_number = ?";

        // prevent SQL injections
        try (PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, entry);
            preparedStatement.setString(2, username);
            preparedStatement.setInt(3, entryNum);

            // Execute the query
            int rowAffected = preparedStatement.executeUpdate();
            // check if query executed or not
            if (rowAffected == 1) {
                return 1;
            }
            // connection to database got cut or did not update properly
            else {
                return 101;
            }


        }


    }



    public  Integer deleteJournalEntry(String username, Integer entryNum) throws SQLException{
    /*
    Delete journal entry using a specific id
     */
        if(!validConnection()){
            return 102;
        }
        // check username and entry exists
        else if (usernameExists(username) == 0 && journalEntryExists(username, entryNum) == 0){
            return 101;
        }



        String sqlQuery = "DELETE FROM journal_entries WHERE account_username = ? AND entry_number = ?";

        // prevent SQL injections
        try (PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, entryNum);

            // Execute the query
            int rowAffected = preparedStatement.executeUpdate();
            // check if query executed or not
            if (rowAffected >= 1) {
                updateNumEntries(username);
                return 1;
            }
            // connection to database got cut or did not update properly
            else {
                return 101;
            }


        }


    }


    public  ArrayList<Integer> getListEntryNumbers(String username) throws SQLException{
    /*
    get a list of the entry ids
     */
        if(!validConnection()){
            return new ArrayList<>();
        }
        else if(journalEntryExists(username, 1) == 0){
            return new ArrayList<>();
        }

        ArrayList<Integer> entryNumbers = new ArrayList<>();

        String sqlQuery = "SELECT entry_number FROM journal_entries WHERE account_username = ? ORDER BY entry_number DESC";
        // prevent SQL injections
        try(PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    entryNumbers.add(resultSet.getInt("entry_number"));
                }
                return entryNumbers;
            }

        }
    }



    public  ArrayList<String> getEntryContent(String username, Integer entryNum) throws SQLException {
        /*
        get the entry content of specific journal entry
         */
        if(!validConnection()){
            return new ArrayList<>();
        }
        else if(journalEntryExists(username, entryNum) != 1){
            return new ArrayList<>();
        }
        // the length of sql time stamp so can remove the seconds
        Integer timeStampLen = 19;
        String sqlQuery = "SELECT creation_date, content, FORMAT(creation_date, 'tt') AS am_pm FROM journal_entries WHERE account_username = ? AND entry_number = ?";
        // prevent SQL injections
        try(PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, entryNum);
            ArrayList<String> dateAndConetent = new ArrayList<>();
            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    String dateTime = resultSet.getString("creation_date").substring(0 , timeStampLen -3);
                    dateTime = dateFormatter(dateTime);
                    dateAndConetent.add(dateTime);
                    dateAndConetent.add(resultSet.getString("content"));
                }
                return dateAndConetent;
            }

        }
    }


    private String dateFormatter(String Date){
        /*
        formats the date given by database
        format the date : [MM]/[DD]/[yyyy] [HH]:[mm][AM/PM]
         */
        String[] dateAndTime = Date.split(" ");
        String[] time = dateAndTime[1].split(":");
        String[] dateSplit = dateAndTime[0].split("-");
        String hour = time[0];
        if(Integer.parseInt(hour) > 12){
            hour = Integer.toString(Integer.parseInt(hour) - 12);
            time[1] = time[1] + "PM";
        }
        else{
            time[1] = time[1] + "AM";
        }
        String newDate = dateSplit[1] + "/" + dateSplit[2] + "/" + dateSplit[0] + " " + hour + ":" + time[1];
        return newDate;
    }


    public static Integer getNumOfEntries(String username) throws SQLException {
        /*
        function get the number of journal entries an account has
         */
        // check if valid connection
        if(!validConnection()){
            return 102;
        }
        // check username and entry exists
        else if (usernameExists(username) == 0){
            return 101;
        }


        String sqlQuery = "SELECT COUNT(entry_number) AS num_entries FROM journal_entries WHERE account_username = ?";
        // prevent SQL injections
        try(PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                int entryNumbers = 0;
                if(resultSet.next()) {
                    entryNumbers = resultSet.getInt("num_entries");
                }
                return entryNumbers;
            }

        }






    }


    public Integer updateNumEntries(String username) throws SQLException {
        /*
        Function sets the number of entries in account
         */
        if(!validConnection()){
            return 102;
        }
        else if (usernameExists(username) == 0){
            return 101;
        }

        int numEntries = getNumOfEntries(username);
        if(numEntries == 101 || numEntries == 102){
            return numEntries;
        }


        String sqlQuery = "UPDATE accounts SET number_of_entries = ?  WHERE username = ?";

        // prevent SQL injections
        try (PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, numEntries);
            preparedStatement.setString(2, username);

            // Execute the query
            int rowAffected = preparedStatement.executeUpdate();
            // check if query executed or not
            if (rowAffected >= 1) {
                return 1;
            }
            // connection to database got cut or did not update properly
            else {
                return 101;
            }


        }


    }


    public Date getLastLogin(String username) throws SQLException {
        /*
        function gets last login date
         */
        // valid connection
        if(!validConnection()){
            return null;
        }
        // username does not exist
        else if (usernameExists(username) == 0){
            return null;
        }

        String sqlQuery = "SELECT last_login_date AS login_date FROM accounts WHERE username = ?";
        // prevent SQL injections
        try(PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Date login_date = null;
                if(resultSet.next()) {
                    // gets date of login
                     login_date = resultSet.getDate("login_date");
                }
                return login_date;
            }

        }


    }


    public static Integer getLoginStreak(String username) throws SQLException {
        /*
        function gets the login streak of the account
         */
        if(!validConnection()){
            return 102;
        }
        // username does not exist
        else if (usernameExists(username) == 0){
            return 101;
        }

        String sqlQuery = "SELECT login_streak FROM accounts WHERE username = ?";
        // prevent SQL injections
        try(PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Integer streak = 0;
                if(resultSet.next()) {
                    // gets login streak
                    streak = resultSet.getInt("login_streak");

                }
                return streak;
            }

        }

    }

    public Date getAccountCreationDate(String username) throws SQLException{
        /*
        Function returns the date of creation from sql database
         */
        // valid connection
        if(!validConnection()){
            return null;
        }
        // username does not exist
        else if (usernameExists(username) == 0){
            return null;
        }

        String sqlQuery = "SELECT creation_date FROM accounts WHERE username = ?";
        // prevent SQL injections
        try(PreparedStatement preparedStatement = MySQLConnection.getConnection().prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, username);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Date creation_date = null;
                if(resultSet.next()) {
                    // gets date of login
                    creation_date = resultSet.getDate("creation_date");
                }
                return creation_date;
            }

        }
    }


    public Integer updateLastLoginAndLoginStreak(String username) throws SQLException {
        /*
        Function checks if the current date to last login date has a difference more than a day.
        If it does streak goes to 0
        If its one steak increases by 1
        If its 0 do nothing with the streak;
        It then updates the lastLoginDate to current date
         */
        if(!validConnection()){
            return 102;
        }
        // function could not execute
        else if (usernameExists(username) == 0){
            return 101;
        }
        // checks if no issue with the streak function
        Integer streak = getLoginStreak(username);
        if(streak == 101 || streak == 102){
            return streak;
        }
        // make sure that getLastLogin function is working
        Date lastLogin = getLastLogin(username);
        if(lastLogin == null){
            return 103;
        }

        // converts last login to a date
        LocalDate lastLoginDate = lastLogin.toLocalDate();
        LocalDate now = LocalDate.now();

        long dayDifference = ChronoUnit.DAYS.between(lastLoginDate, now);
        if(Math.abs(dayDifference) == 1){
            if(streak < 1000){
                streak += 1;
            }
        }
        // login difference is more than a day
        else if(Math.abs(dayDifference) > 1){
            streak = 0;
        }





        String sqlQuery1 = "UPDATE accounts SET last_login_date = ? WHERE username = ?;";
        String sqlQuery2 = "UPDATE accounts SET login_streak = ? WHERE username = ?;";

        try (PreparedStatement preparedStatement1 = MySQLConnection.getConnection().prepareStatement(sqlQuery1);
             PreparedStatement preparedStatement2 = MySQLConnection.getConnection().prepareStatement(sqlQuery2)) {

            // First query
            preparedStatement1.setDate(1, Date.valueOf(now));
            preparedStatement1.setString(2, username);
            int rowAffected1 = preparedStatement1.executeUpdate();

            // Second query
            preparedStatement2.setInt(1, streak);
            preparedStatement2.setString(2, username);
            int rowAffected2 = preparedStatement2.executeUpdate();


            // check if query executed or not
            if (rowAffected1 == 1 && rowAffected2 == 1) {
                return 1;
            }
            // connection to database got cut or did not update properly
            else {
                return 101;
            }
        }


    }
}
