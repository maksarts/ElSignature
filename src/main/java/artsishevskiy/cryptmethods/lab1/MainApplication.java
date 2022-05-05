package artsishevskiy.cryptmethods.lab1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("file.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 878, 609);
        stage.setTitle("Работа с подписанными документами");
        stage.setScene(scene);
        stage.show();

        UsersControl curUsers = new UsersControl(stage);
        FileController fileController = fxmlLoader.getController();
        fileController.setUsers(curUsers);
    }

    public static void main(String[] args) {
        launch();
    }
}