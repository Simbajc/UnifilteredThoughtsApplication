import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CreateJournalController {
    String username, password;
    Main main;
    mySQLRequest sqlRequest = null;

    @FXML
    private Label entryNum;

    @FXML
    private Label loginStreakNum;

    @FXML
    private Label currentDate;

    @FXML
    private ListView<String> journalEntryList;



    public void setMainApp(Main main, String username, String password) throws SQLException {
        this.main = main;
        this.username = username;
        this.password = password;
        main.entryID = -1;
        main.editJournal = 1;
        try{
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            sqlRequest = main.sqlRequest;
            setJournalEntries();
            setDate();
            setEntryList();
        }
        catch (SQLException e) {
            System.err.println("Error initializing CreateJournalController: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void codeInterpretator(Integer code){
        /*
        Function interprets the code given to it
         */
        if(code == 101){
            System.out.println("Create Journal Scene: Function could not execute");
        }
        else if(code == 102){
            System.out.println("Database connection error");
        }
        else if(code == 103){
            System.out.println("getting date error error");
        }
    }


    private void setJournalEntries() throws SQLException {
        /*
        Function shows the user journal stats
         */
        Integer numEntries = mySQLRequest.getNumOfEntries(username);
        Integer loginStreak = mySQLRequest.getLoginStreak(username);
        if(numEntries == 101 || numEntries == 102){
            System.out.println("Entry Number error");
            codeInterpretator(numEntries);
            return;
        }
        else if(loginStreak == 101 || loginStreak == 102){
            System.out.println("Login streak error");
            codeInterpretator(loginStreak);
            return;
        }


        entryNum.setText(numEntries.toString());
        loginStreakNum.setText(loginStreak.toString());

    }


    private void setDate() throws SQLException {
        /*
        Function for showing the user current date on the UI
         */
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // Custom format
        String formattedDate = date.format(formatter);


        currentDate.setText("Date: " + formattedDate);
    }


    @FXML
    private void goToAccountSettings() throws SQLException, IOException {
        main.backButtonToScene = "create";
        main.loadAccountSettingsScene();
        main.showAccountSettingsScene();
    }

    @FXML
    private void setEntryList() throws SQLException {
        ArrayList<String> entry = new ArrayList<>();
        ArrayList<Integer> numbers = sqlRequest.getListEntryNumbers(username);
        for(Integer number : numbers){
            entry.add("Journal ID: " + number + " - " + sqlRequest.getEntryContent(username, number).get(0));
        }
        journalEntryList.getItems().setAll(entry);


    }

    @FXML
    private void createJournalEntry() throws SQLException, IOException {
        System.out.println("Go to view Journal");
        main.entryID = 0;
        main.editJournal = 1;
        main.loadViewJournalScene();
        main.showViewJournalScene();
    }


    @FXML
    // If user clicks on journal entry
    private void viewJournalEntry() throws SQLException {
        // check if what is clicked is an entry number
        if(!journalEntryList.getItems().isEmpty()){
            journalEntryList.setOnMouseClicked(event -> {
                String selectedItem = journalEntryList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    System.out.println("Clicked: " + selectedItem);
                    Integer entryId = Integer.parseInt((selectedItem.split("-")[0]).split(" ")[2]);
                    // send entry id up
                    main.entryID = entryId;
                    main.editJournal = 0; // signal that this is a new journal entry;

                    try {
                        main.loadViewJournalScene();
                        main.showViewJournalScene();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
