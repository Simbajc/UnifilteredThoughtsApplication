import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class AccountCreationController {
    /*
    CODE interpretation
    -2 - issue with length
    -3 - username exists already
    -4 - 2 things are not equal
     */
    Main main;
    int dissableAccountCreation = 0;
    public void setMainApp(Main main) {
        this.main = main;
    }

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField newPassTextField;

    @FXML
    private TextField repeatNewPassTextField;

    @FXML
    private Button createAccountButton;




    private Integer validUsername() throws SQLException {

        String username = usernameTextField.getText().trim();
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

    // check if the passwords are valid or not
    private Integer validPasswords() throws SQLException {
        String newPass = newPassTextField.getText().trim();
        String repeatPass = repeatNewPassTextField.getText().trim();

        if(newPass.length() < 5 || newPass.length() > 20){
            // password not the right length
            return -2;
        }
        else if(!newPass.equals(repeatPass)){
            // passwords are not the same
            return -4;
        }
        return 1;
    }

    @FXML
    /* Interprets the error code for the username and affects the gui */
    private void codeInterpretationUsername(Integer code) throws SQLException {
        // if the username is not proper length
        if(code == -2){
            usernameTextField.setText("Length: 5 - 20");
        }
        // If username is already existing
        else if(code == -3){
            usernameTextField.setText("Username exists");
        }
        // database connection issues
        else if(code == 102){
            dissableAccountCreation = 1;
            usernameTextField.clear();
            newPassTextField.clear();
            repeatNewPassTextField.clear();
            usernameTextField.setDisable(true);
            newPassTextField.setDisable(true);
            repeatNewPassTextField.setDisable(true);
            createAccountButton.setDisable(true);
            createAccountButton.setVisible(false);

        }
    }

    @FXML
    /* Interprets the error code for the password and affect the gui */
    private void codeInterpretationPassword(Integer code) throws SQLException {
        // not the proper length
        if(code == -2){
            newPassTextField.setText("Length: 5 - 20");
        }
        // The passwords are not the same
        else if(code == -4){
            repeatNewPassTextField.setText("Passwords not same");
        }
        // database is not connected
        else if(code == 102){
            dissableAccountCreation = 1;
            usernameTextField.clear();
            newPassTextField.clear();
            repeatNewPassTextField.clear();
            usernameTextField.setDisable(true);
            newPassTextField.setDisable(true);
            repeatNewPassTextField.setDisable(true);
            createAccountButton.setDisable(true);
            createAccountButton.setVisible(false);
        }
    }
    @FXML
        /* to create the user account */
    void createAccount() throws SQLException, IOException {

        mySQLRequest.establishConnection();
        // checks if username and password is valid
        Integer usernameCode = validUsername();
        Integer passwordCode = validPasswords();

        // if both the username and password is valid
        if(usernameCode == 1 && passwordCode == 1){
            int code = mySQLRequest.addAccount(usernameTextField.getText().trim(),newPassTextField.getText().trim());
            if(code == 1){
                usernameTextField.clear();
                newPassTextField.clear();
                repeatNewPassTextField.clear();
                main.loadLoginScene();
                return;
            }
            else if(code == 102){
                codeInterpretationUsername(code);
                System.err.println("Database connection failed");
            }
            else if(code == 101){
                System.out.println("Account could not be created");
            }
        }

        // Interprets why it's not going back to log in screen
        System.out.println("Username code: " + usernameCode);
        System.out.println("Password code: " + passwordCode);
        if(usernameCode != 1){
            codeInterpretationUsername(usernameCode);
        }
        else{
            codeInterpretationPassword(passwordCode);
        }
        dissableAccountCreation = 1;





    }


    @FXML
    void backToLogin() throws SQLException, IOException {
        System.out.println("Go back to log in");
        usernameTextField.clear();
        newPassTextField.clear();
        repeatNewPassTextField.clear();
        System.out.println("Dissable account: " + dissableAccountCreation);
        if(dissableAccountCreation == 1){
            createAccountButton.setVisible(true);
            usernameTextField.setDisable(false);
            newPassTextField.setDisable(false);
            repeatNewPassTextField.setDisable(false);
            createAccountButton.setDisable(false);
        }

        dissableAccountCreation = 0;
        System.out.println("Go back to log in");
        main.loadLoginScene();
    }


}
