package artsishevskiy.cryptmethods.lab1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button Button_Close;

    @FXML
    void OnAction_Button_Close(ActionEvent event) {
        Stage stage = (Stage) Button_Close.getScene().getWindow();
        stage.close();
    }

    @FXML
    void initialize() {
        assert Button_Close != null : "fx:id=\"Button_Close\" was not injected: check your FXML file 'about.fxml'.";

    }
}