import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class ViewJournalController {
    Integer entryId;
    Integer canEdit;
    mySQLRequest sqlRequest;
    String username, password;
    Main main;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Label entryIdNum;

    @FXML
    private Label Date;

    @FXML
    private TextArea journalContent;

    @FXML
    private ListView<String> journalEntryList;


    @FXML
    private Label entryNum;

    @FXML
    private Label loginStreakNum;

    public void setMainApp(Main main, Integer entryID, Integer editJournal) throws SQLException {
        this.entryId = entryID;
        canEdit = editJournal;
        username = main.accountUsername;
        password = main.accountPassword;
        sqlRequest = main.sqlRequest;
        this.main = main;
        if (entryID != -1 && editJournal != -1) {
            setup();
        }

    }


    @FXML
    // Set up the Entry List so that it can be displayed
    private void setEntryList() throws SQLException {
        ArrayList<String> entry = new ArrayList<>();
        ArrayList<Integer> numbers = sqlRequest.getListEntryNumbers(username);
        for(Integer number : numbers){
            entry.add("Journal ID: " + number + " - " + sqlRequest.getEntryContent(username, number).get(0));
        }
        journalEntryList.getItems().setAll(entry);


    }

    @FXML
    // If user clicks on journal entry
    private void viewJournalEntry() throws SQLException {
        // check if what is clicked is an entry number
        if (!journalEntryList.getItems().isEmpty()) {
            journalEntryList.setOnMouseClicked(event -> {
                String selectedItem = journalEntryList.getSelectionModel().getSelectedItem();
                // if there was no selected item
                if (selectedItem != null) {
                    System.out.println("Clicked: " + selectedItem);
                    // get specific entry id
                    Integer entryId = Integer.parseInt((selectedItem.split("-")[0]).split(" ")[2]);

                    // send entry id up
                    main.entryID = entryId;
                    this.entryId = entryId;
                    main.editJournal = 0; // signal that this is a new journal entry;

                    try {
                        setEntryList();
                        canEdit = 0;
                        setup();

                    }catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }


    }

    @FXML
    // allows editing for a journal entry
    void editEntryContent() throws SQLException {
        // configuration that allows the editing of entries for setup
        journalContent.setDisable(false);
        confirmButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @FXML
    // Delete entry content
    void deleteEntryContent() throws SQLException, IOException {
        Integer code = sqlRequest.deleteJournalEntry(username, entryId);
        if(code != 1){
            main.loadCreateJournalScene();
        }

    }



    @FXML
    // makes sure the entry is a valid entry
    void confirmEntryContent() throws SQLException {
        Integer contentLength = journalContent.getText().trim().length();
        Integer Code = sqlRequest.journalEntryExists(username, entryId);
        if (contentLength < 5 || contentLength > 2000) {
            return;
        }
        // check if entry exist function is working
        if(Code > 99){
            codeInterpretator(Code);
            return;
        }

        // if entry exists and prev entry is not equal to current update the entry
        if(sqlRequest.journalEntryExists(username, entryId) == 1){
            if(!Objects.equals(sqlRequest.getEntryContent(username, entryId).get(1), journalContent.getText().trim())){
                sqlRequest.updateJournalEntry(username, entryId, journalContent.getText().trim());
                canEdit = 0;
                setup();
            }
        }
        // creates an entry
        else{
            sqlRequest.createJournalEntry(username, journalContent.getText().trim());
            canEdit = 0;
            setup();
        }







    }


    @FXML
    void goToHomeScreen() throws SQLException, IOException {
        main.entryID = -1;
        main.editJournal = -1;
        main.loadCreateJournalScene();
        main.showCreateJournalScene();
    }


    @FXML
    // Goes to account setting
    void goToAccountSettings() throws SQLException, IOException {
//        main.entryID = -1;
//        main.editJournal = -1;
        main.backButtonToScene = "view";
        main.loadAccountSettingsScene();
        main.showAccountSettingsScene();
    }


    // Makes sure the configuration for buttons and textfield is set based on how the scene was initiallized
    private void setup() throws SQLException {
        // this is a new journal Entry;
        System.out.println("Entry ID: " + entryId);
        if(canEdit == 1){
            entryId =  sqlRequest.getListEntryNumbers(username).get(0) + 1;
            deleteButton.setDisable(true);
            editButton.setDisable(true);
            journalContent.setDisable(false);

            LocalDate date = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); // Custom format
            String formattedDate = date.format(formatter);
            Date.setText("Date: " + formattedDate);
            System.out.println("New Entry");
            journalContent.clear();



        }
        else{
            confirmButton.setDisable(true);

            deleteButton.setDisable(false);
            editButton.setDisable(false);
            journalContent.setDisable(true);

            Date.setText("Date: " + sqlRequest.getEntryContent(username, entryId).get(0).split(" ")[0]);
            journalContent.setText(sqlRequest.getEntryContent(username, entryId).get(1));

            System.out.println("Old Entry");

        }

        entryIdNum.setText("Journal ID: " + entryId);
        setJournalEntries();
        setEntryList();
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

}
