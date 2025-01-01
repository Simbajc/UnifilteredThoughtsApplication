
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;


public class Main extends Application {
    Stage window;
    Scene loginScene;
    Scene accountCreationScene;
    Scene accountSettingScene;
    Scene createJournalScene;
    Scene viewJournalScene;


    String accountUsername = "";
    String accountPassword = "";
    Integer entryID = -1;
    Integer editJournal = -1;
    mySQLRequest sqlRequest;
    String backButtonToScene = "";

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Unfiltered Thoughts");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application shutting down...");
            MySQLConnection.closeConnection();
        }));
        sqlRequest = new mySQLRequest();
        loadLoginScene();
        loadAccountCreationScene();
        loadAccountSettingsScene();
        loadViewJournalScene();
        loadCreateJournalScene();

        showLoginScene();
        window.show();
    }

    // Loader for the login window
    public void loadLoginScene() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/login.fxml"));

        Parent root = fxmlLoader.load();
        LoginController controller = fxmlLoader.getController();
        controller.setMainApp(this);

        loginScene = new Scene(root, 1500, 700);
        loginScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style/login.css")).toExternalForm());
        window.setScene(loginScene);
        window.show();

    }

    // Loader for the account creation window
    public void loadAccountCreationScene() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/accountCreation.fxml"));

        Parent root = fxmlLoader.load();
        AccountCreationController controller = fxmlLoader.getController();
        controller.setMainApp(this);

        accountCreationScene = new Scene(root, 1500, 700);
        accountCreationScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style/accountCreation.css")).toExternalForm());
        window.setScene(accountCreationScene);
        //window.show();
    }

    // Loader for the account setting window
    public void loadAccountSettingsScene() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/accountSettings.fxml"));
        System.out.println("Passed");
        Parent root = fxmlLoader.load();
        AccountSettingsController controller = fxmlLoader.getController();
        controller.setMainApp(this);

        accountSettingScene = new Scene(root, 1500, 700);
        accountSettingScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style/accountSettings.css")).toExternalForm());
        window.setScene(accountSettingScene);
        //window.show();
    }

    // Loader for viewing individual journal entries
    public void loadViewJournalScene() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/viewJournal.fxml"));

        Parent root = fxmlLoader.load();
        ViewJournalController controller = fxmlLoader.getController();
        controller.setMainApp(this, entryID, editJournal);

        viewJournalScene = new Scene(root, 1500, 700);
        viewJournalScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style/viewJournal.css")).toExternalForm());
        window.setScene(viewJournalScene);
        //window.show();
    }


    // Loader for the creation journal entries. This is going to be home
    public void loadCreateJournalScene() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/createJournal.fxml"));

        Parent root = fxmlLoader.load();
        CreateJournalController controller = fxmlLoader.getController();
        controller.setMainApp(this, accountUsername, accountPassword);

        createJournalScene = new Scene(root, 1500, 700);
        createJournalScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style/createJournal.css")).toExternalForm());
        window.setScene(createJournalScene);
        window.show();
    }


    // show login scene
    public void showLoginScene(){
        window.setScene(loginScene);
    }

    // show account creation scene
    public void showAccountCreationScene() throws Exception{
        window.setScene(accountCreationScene);
    }


    // show account settings scene
    public void showAccountSettingsScene(){
        window.setScene(accountSettingScene);
    }

    // show specific journal entry
    public void showViewJournalScene(){
        window.setScene(viewJournalScene);
    }

    // show "Hone screen"
    public void showCreateJournalScene(){
        window.setScene(createJournalScene);
    }



}