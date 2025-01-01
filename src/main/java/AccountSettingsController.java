import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/*
CODE:
-1 - database connection issues
-2 - not proper length
-3 - username exists
-4 - same as previous passwords
 */
public class AccountSettingsController {
    Main main;
    String buttonPressed;
    mySQLRequest sqlRequest;
    String username;
    String password;

    @FXML
    private Label repeatPassword;

    @FXML
    private Label usernameTitle;


    // TextFields
    @FXML
    private TextField txtFieldUsername;

    @FXML
    private TextField txtFieldPassword;

    @FXML
    private TextField txtFieldRePassword;

    // Buttons
    @FXML
    private Button updateUsernameButton;

    @FXML
    private Button updatePasswordButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private Button backButton;
    public void setMainApp(Main main) {
    this.main = main;
    username = main.accountUsername;
    password = main.accountPassword;
    System.out.println("Account Settings");
    System.out.println("Username: " + username);
    System.out.println("Password: " + password);
    txtFieldUsername.setDisable(true);
    txtFieldPassword.setDisable(true);
    txtFieldRePassword.setDisable(true);
    confirmButton.setDisable(true);
    sqlRequest = main.sqlRequest;
    }


    @FXML
    private void updateUsernameConfig() {
        // configuration for update username button
        usernameTitle.setText("New Username");
        updateUsernameButton.setDisable(false);
        updatePasswordButton.setDisable(true);
        deleteAccountButton.setDisable(true);
        confirmButton.setDisable(false);
        txtFieldUsername.setDisable(false);
        txtFieldPassword.setDisable(false);
        txtFieldRePassword.setDisable(false);
        buttonPressed = "username";
        System.out.println("Update Username");
    }


    @FXML
    private void updatePasswordConfig() {
        // configuration for update password button
        updateUsernameButton.setDisable(true);
        updatePasswordButton.setDisable(false);
        deleteAccountButton.setDisable(true);
        confirmButton.setDisable(false);
        txtFieldUsername.setDisable(false);
        txtFieldPassword.setDisable(false);
        txtFieldRePassword.setDisable(false);
        repeatPassword.setText("New Password");
        System.out.println("Update Password");


        buttonPressed = "password";
    }


    @FXML
    private void deleteAccountConfig() {
        // configuration for delete account button
        updateUsernameButton.setDisable(true);
        updatePasswordButton.setDisable(true);
        deleteAccountButton.setDisable(false);
        confirmButton.setDisable(false);
        txtFieldUsername.setDisable(false);
        txtFieldPassword.setDisable(false);
        txtFieldRePassword.setDisable(false);
        buttonPressed = "delete";
    }

    @FXML
    private void reset(){
        txtFieldPassword.clear();
        txtFieldRePassword.clear();
        txtFieldUsername.clear();
        repeatPassword.setText("Repeat Password");
        usernameTitle.setText("Username");
        txtFieldUsername.setDisable(true);
        txtFieldPassword.setDisable(true);
        txtFieldRePassword.setDisable(true);
        updatePasswordButton.setDisable(false);
        updateUsernameButton.setDisable(false);
        deleteAccountButton.setDisable(false);
        confirmButton.setDisable(true);
    }


    @FXML
    private void confirm() throws SQLException, IOException {
        Integer code = 0;
        System.out.println("Button Pressed: '" + buttonPressed + "'");
        if(Objects.equals(buttonPressed, "password") && validPasswordChange() == 1){
            code = sqlRequest.updateAccountPassword(username, password, txtFieldRePassword.getText().trim());
            if(code == 1){
                password = txtFieldRePassword.getText().trim();
                main.accountPassword = password;
                back();
                return;
            }
        }
        // makes sure that what password was given is a valid password, and it's the same password
        else if(Objects.equals(buttonPressed, "username") && validPassword() == 1 && validChangeUsername() == 1) {
            code = sqlRequest.updateAccountUsername(username, txtFieldUsername.getText().trim(), txtFieldPassword.getText().trim());
            System.out.println("change username button confirm works");
            if(code == 1){
                username = txtFieldUsername.getText().trim();
                main.accountUsername = username;
                back();
                return;
            }
        }
        else if(Objects.equals(buttonPressed, "delete") && validPassword() == 1) {
            code = sqlRequest.deleteAccount(txtFieldUsername.getText().trim(), txtFieldPassword.getText().trim(), txtFieldRePassword.getText().trim());
            if(code == 1){
                main.accountPassword = "";
                main.accountUsername = "";
                main.editJournal = -1;
                main.entryID = -1;
                main.loadLoginScene();
                main.showLoginScene();
                return;
            }
        }
        codeInterpretationUsername(code);
    }


    private Integer validChangeUsername() throws SQLException {
        String username = txtFieldUsername.getText().trim();
        if(mySQLRequest.usernameExists(username) == 102){
            // database is not connected
            return 102;
        }
        //check size of username
        else if(username.length() < 5 || username.length() > 20){
            // username not proper length
            return -2;
        }
        // check if username has been used before
        else if(mySQLRequest.usernameExists(username) == 1){
            // username already exists
            return -3;
        }

        return 1;
    }


    private Integer validPassword() throws SQLException {
        String newPass = txtFieldPassword.getText().trim();
        String repeatPass = txtFieldRePassword.getText().trim();

        if(newPass.length() < 5 || newPass.length() > 20 || repeatPass.length() < 5 || repeatPass.length() > 20){
            // password not the right length
            return -2;
        }
        else if(!newPass.equals(password) || !newPass.equals(repeatPass)){
            if(!newPass.equals(password)){
                System.out.println("Password do not match current password");
                System.out.println("'" + newPass + "' != '" + password + "'");
            }
            else{
                System.out.println("Password do not match");
            }
            System.out.println("Error with validating password");
            return -3;
        }

        return 1;
    }

    // check if the passwords are valid or not
    private Integer validPasswordChange() throws SQLException {
        String newPass = txtFieldPassword.getText().trim();
        String repeatPass = txtFieldRePassword.getText().trim();

        if(newPass.length() < 5 || newPass.length() > 20 || repeatPass.length() < 5 || repeatPass.length() > 20){
            // password not the right length
            return -2;
        }
        // if the password is the same as the previous password
        else if(!Objects.equals(password, newPass) || Objects.equals(newPass, repeatPass)){
            System.out.println("Password error");
            return -3;
        }
        return 1;
    }

    @FXML
    /* Interprets the error code for the username and affects the gui */
    private void codeInterpretationUsername(Integer code) throws SQLException {
        // if the username is not proper length
        if(code == -2){
            txtFieldUsername.setText("Length: 5 - 20");
        }
        // If username is already existing
        else if(code == -3){
            txtFieldUsername.setText("Username exists");
        }
        // database connection issues
        else if(code == 102){
            repeatPassword.setText("Repeat Password");
            usernameTitle.setText("Username");
            txtFieldUsername.clear();
            txtFieldPassword.clear();
            txtFieldRePassword.clear();
            txtFieldUsername.setDisable(true);
            txtFieldPassword.setDisable(true);
            txtFieldRePassword.setDisable(true);


        }
    }

    @FXML
    public void back() throws SQLException, IOException {
        repeatPassword.setText("Repeat Password");
        usernameTitle.setText("Username");
        txtFieldUsername.clear();
        txtFieldPassword.clear();
        txtFieldRePassword.clear();
        updateUsernameButton.setDisable(false);
        updatePasswordButton.setDisable(false);
        confirmButton.setDisable(false);
        deleteAccountButton.setDisable(false);
        if(Objects.equals(main.backButtonToScene, "create")){
            main.loadCreateJournalScene();
            main.backButtonToScene = "";
            main.showCreateJournalScene();
        }
        else if(Objects.equals(main.backButtonToScene, "view")){
            main.loadViewJournalScene();
            main.backButtonToScene = "";
            main.showViewJournalScene();
        }
    }
}
