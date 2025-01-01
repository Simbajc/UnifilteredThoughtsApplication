import java.sql.Date;
import java.util.ArrayList;

interface DatabaseInteractor {
    /*
    CODE MEANING
    100 - Working Correctly
    101 - Server is connected but could not execute the function (Functions that modify database)
    102 - Server is not connected
    103 - Error for getLastLogin
     */


    // check that username exists
    Integer usernameExists(String username);

    // add account to database
    Integer addAccount(String username, String password);

    // check that account is a valid one
    Integer validAccount(String username, String password);

    // updates the username in database
    Integer updateAccountUsername(String username, String newUsername, String password);

    // update the password in database
    Integer updateAccountPassword(String username, String password, String newPassword);

    // delete account in database
    Integer deleteAccount(String username, String password, String repeatPassword);

    // make sure journal entry exists
    Integer journalEntryExists(String username, Integer entryNum);

    // create journal entry
    Integer createJournalEntry(String username, String entry);

    // update journal entry
    Integer updateJournalEntry(String username, Integer entryNum, String entry);

    // delete journal entry
    Integer deleteJournalEntry(String username, Integer entryNum);

    // return a list of entry IDs
    ArrayList<Integer> getListEntryNumbers(String username);

    // get the entry content
    ArrayList<String> getEntryContent(String username, Integer entryNum);

    // format the dates : [MM]/[DD]/[yyyy] [HH]:[mm][AM/PM]
    String dateFormatter(String Date);

    // get the number of journal entries for specific account
    Integer getNumOfEntries(String username);

    // get last login date
    Date getLastLogin(String username);

    // get login streak
    Integer getLoginStreak(String username);

    // get account creation date
    Date getAccountCreationDate(String username);

    // update the last login date and update login streak
    Integer updateLastLoginAndLoginStreak(String username);
}
