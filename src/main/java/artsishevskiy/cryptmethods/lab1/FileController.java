package artsishevskiy.cryptmethods.lab1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;

public class FileController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuItem menuButtonMain;

    @FXML
    private MenuItem menuButtonKeys;

    @FXML
    private Button Button_Save;

    @FXML
    private MenuItem menuButtonExit;

    @FXML
    private TextField text_user_name;

    @FXML
    private MenuBar menu;

    @FXML
    private Button Button_Browse;

    @FXML
    private TextArea text_edit_field;

    @FXML
    private TextField text_author_name;

    @FXML
    private TextField text_path;

    @FXML
    private TextField text_document;

    @FXML
    private MenuItem menuButtonAbout;

    @FXML
    private Button Button_DownloadDoc;

    @FXML
    private Button Button_ChooseUser;

    @FXML
    private Label labelErrorUser;

    @FXML
    void OnAction_menuButtonMain(ActionEvent event) {

    }

    @FXML
    void OnAction_menuButtonExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void OnAction_menuButtonKeys(ActionEvent event) {

    }

    @FXML
    void OnAction_menuButtonAbout(ActionEvent event) {

    }

    @FXML
    void OnAction_Button_Browse(ActionEvent event) {

    }

    @FXML
    void OnAction_Button_Save(ActionEvent event) {

    }

    @FXML
    void OnAction_Button_DownloadDoc(ActionEvent event) {

    }

    @FXML
    void OnAction_Button_ChooseUser(ActionEvent event) {
        String username = text_user_name.getText();

        labelErrorUser.setText("");
        text_user_name.setText("");
        text_path.setText("");
        text_edit_field.setText("");

        if (username.equals("")) {
            text_edit_field.setDisable(true);
            text_path.setDisable(true);
            Button_Save.setDisable(true);
            Button_Browse.setDisable(true);
            text_document.setDisable(true);
            Button_DownloadDoc.setDisable(true);

            labelErrorUser.setText("Имя пользователя не может быть пустым!");
            labelErrorUser.setTextFill(Paint.valueOf("RED"));
            labelErrorUser.setAlignment(Pos.TOP_LEFT);
            return;
        }

        File usersFolder = new File("users");
        if (!usersFolder.exists()) {
            usersFolder.mkdir();
        }
    }

    @FXML
    void initialize() {
        text_author_name.setDisable(true);
        text_edit_field.setDisable(true);
        text_path.setDisable(true);
        Button_Save.setDisable(true);
        Button_Browse.setDisable(true);
        text_document.setDisable(true);
        Button_DownloadDoc.setDisable(true);

        assert menuButtonMain != null : "fx:id=\"menuButtonMain\" was not injected: check your FXML file 'file.fxml'.";
        assert menuButtonKeys != null : "fx:id=\"menuButtonKeys\" was not injected: check your FXML file 'file.fxml'.";
        assert Button_ChooseUser != null : "fx:id=\"chooseUserButton\" was not injected: check your FXML file 'file.fxml'.";
        assert Button_Save != null : "fx:id=\"save\" was not injected: check your FXML file 'file.fxml'.";
        assert menuButtonExit != null : "fx:id=\"menuButtonExit\" was not injected: check your FXML file 'file.fxml'.";
        assert text_user_name != null : "fx:id=\"userName\" was not injected: check your FXML file 'file.fxml'.";
        assert menu != null : "fx:id=\"menu\" was not injected: check your FXML file 'file.fxml'.";
        assert Button_Browse != null : "fx:id=\"browseDoc\" was not injected: check your FXML file 'file.fxml'.";
        assert text_edit_field != null : "fx:id=\"editField\" was not injected: check your FXML file 'file.fxml'.";
        assert text_author_name != null : "fx:id=\"authorName\" was not injected: check your FXML file 'file.fxml'.";
        assert text_path != null : "fx:id=\"pathToDoc\" was not injected: check your FXML file 'file.fxml'.";
        assert text_document != null : "fx:id=\"doc\" was not injected: check your FXML file 'file.fxml'.";
        assert menuButtonAbout != null : "fx:id=\"menuButtonAbout\" was not injected: check your FXML file 'file.fxml'.";
        assert labelErrorUser != null : "fx:id=\"labelErrorUser\" was not injected: check your FXML file 'file.fxml'.";
        assert Button_DownloadDoc != null : "fx:id=\"downloadDoc\" was not injected: check your FXML file 'file.fxml'.";

    }
}
