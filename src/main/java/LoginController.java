import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    private Main main;
    private mySQLRequest sqlRequest;
    private String username, password;
    public void setMainApp(Main main) throws SQLException {
        this.main = main;
        sqlRequest = main.sqlRequest;
    }

    @FXML
    Button createAccountButton;
    @FXML
    Button loginButton;

    @FXML
    TextField usernameTextField;

    @FXML TextField passwordTextField;


    @FXML
    void createAccount() throws Exception {
//        createAccountButton.setDisable(true);
//        main.loadAccountCreationScene();
        main.showAccountCreationScene();
        System.out.println("Created account scene");
    }

    Integer accountValid() throws SQLException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        if(username.length() < 5 || username.length() > 20) {
            // username not proper length
            return -2;
        }
        else if(password.length() < 5 || password.length() > 20){
            return -3;
        }
        /*
        code -1: database is not connected
        code 0: account is not valid
        code 1: account is valid
         */
        this.username = username;
        this.password = password;
        return sqlRequest.validAccount(username,password);
    }

    private void codeInterpretator(Integer code){
        if(code == -1){
            usernameTextField.setText("Connection Error");
        }
        else if(code == -3){
            passwordTextField.setText("Length: 5 - 20");
        }
        else{
            usernameTextField.setText("Wrong Login");
            passwordTextField.clear();
        }
    }

    @FXML
    void login() throws Exception, SQLException {
        mySQLRequest.establishConnection();

        Integer code = accountValid();
        if(code == 1){
            main.accountUsername = username;
            main.accountPassword = password;
            sqlRequest.updateLastLoginAndLoginStreak(username);
            username = "";
            password = "";
            usernameTextField.clear();
            passwordTextField.clear();
            main.loadCreateJournalScene();
            main.showCreateJournalScene();
            return;
        }
        username = "";
        password = "";
        codeInterpretator(code);
    }
}
