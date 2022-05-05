package artsishevskiy.cryptmethods.lab1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileController {

    Stage keyStage;
    Stage aboutStage;

    private UsersControl currentUsers;
    public void setUsers(UsersControl users) {
        currentUsers = users;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    private Label labelErrorSave;

    @FXML
    private Label labelErrorLoad;

    @FXML
    void OnAction_menuButtonExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void OnAction_menuButtonKeys(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("key.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 642, 601);
        keyStage = new Stage();
        keyStage.setTitle("Работа с ключами");
        keyStage.setScene(scene);
        keyStage.show();

        UsersControl curUsers = new UsersControl(keyStage);
        KeyController keyController = fxmlLoader.getController();
        keyController.setUsers(curUsers);
    }

    @FXML
    void OnAction_menuButtonAbout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("about.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 600, 446);
        keyStage = new Stage();
        keyStage.setTitle("О программе");
        keyStage.setScene(scene);
        keyStage.show();
    }

    @FXML
    void OnAction_Button_Browse(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Выберите папку для сохранения документов");
        File defaultDirectory = new File("./");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(new Stage());

        if (selectedDirectory != null) {
            text_path.setText(selectedDirectory.getPath());
        }
    }

    @FXML
    void OnAction_Button_Save(ActionEvent event) {
        String path = text_path.getText();
        String filename = text_document.getText();
        String username = text_user_name.getText();
        String userKeys = "users/".concat(username).concat("/").concat(username);

        labelErrorSave.setText("");
        labelErrorLoad.setText("");

        if (username.equals("")) {
            labelErrorSave.setText("Введите имя пользователя!");
            labelErrorSave.setTextFill(Color.RED);
            labelErrorSave.setAlignment(Pos.CENTER);

            return;
        }

        if (filename.equals("")) {
            labelErrorSave.setText("Наименование файла не может быть пустым!");
            labelErrorSave.setTextFill(Color.RED);
            labelErrorSave.setAlignment(Pos.CENTER);

            return;
        }
        if (!filename.contains(".sd")) {
            filename = filename.concat(".sd");
        }

        String finalPath = "";
        if (!path.equals("")) {
            finalPath = finalPath.concat(path);
            if (finalPath.charAt(finalPath.length() - 1) != '/') {
                finalPath = finalPath.concat("/");
            }
        }

        finalPath = finalPath.concat(filename);

        // creating file
        File file = new File(finalPath);
        if (file.exists()) {
            String au = text_author_name.getText();
            if (!au.equals("") && !au.equals(username)) {
                labelErrorSave.setText("Невозможно изменить подписанный документ.");
                labelErrorLoad.setText("Пользователь не является автором!");

                labelErrorSave.setTextFill(Color.RED);
                labelErrorSave.setAlignment(Pos.CENTER);

                labelErrorLoad.setTextFill(Color.RED);
                labelErrorLoad.setAlignment(Pos.CENTER);
                return;
            }
        }

        // signing document
        if (!currentUsers.signDoc(text_user_name.getText(), text_edit_field.getText(), finalPath)) {
            System.out.println("not signed");

            labelErrorSave.setText("Файл не подписан, возможно изменен приватный ключ");
            labelErrorSave.setTextFill(Color.RED);
            labelErrorSave.setAlignment(Pos.CENTER);

            return;
        } else {
            System.out.println("signed");
        }

        // checking signature
        if (!currentUsers.checkSignedDoc(finalPath, currentUsers.getCheckPubKey(username, userKeys.concat(".pub"), "EC"))) {
            System.out.println("not verified");

            labelErrorSave.setText("Файл не верифицирован, возможно изменена пара ключей");
            labelErrorSave.setTextFill(Color.RED);
            labelErrorSave.setAlignment(Pos.CENTER);

            return;
        } else {
            System.out.println("verified");
            labelErrorSave.setText("Файл успешно подписан");
            labelErrorSave.setTextFill(Color.GREEN);
            labelErrorSave.setAlignment(Pos.CENTER);

            text_document.setText("");
            text_path.setText("");
            text_edit_field.setText("");
        }

    }

    @FXML
    void OnAction_Button_DownloadDoc(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите подписанный документ");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Подписанный документ(.sd)", "*.sd"));
        File defaultDirectory = new File("./");
        fileChooser.setInitialDirectory(defaultDirectory);
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        text_edit_field.setText("");
        text_path.setText("");
        text_author_name.setText("");
        text_document.setText("");
        labelErrorLoad.setText("");
        labelErrorSave.setText("");
        labelErrorUser.setText("");

        if (selectedFile == null) {
            return;
        }

        String pathTF = selectedFile.getPath();
        pathTF = pathTF.substring(0, pathTF.lastIndexOf('\\'));
        text_path.setText(pathTF);
        text_document.setText(selectedFile.getName());

        try {
            byte[] data = Files.readAllBytes(Path.of(selectedFile.getPath()));
            int lenAuthor = currentUsers.convertByte(data[0]);
            int lenSign = currentUsers.convertByte(data[1]);
            int lenText = data.length - 2 - lenAuthor - lenSign;

            byte[] author = new byte[lenAuthor];
            byte[] text = new byte[lenText];

            System.arraycopy(data, 2, author, 0, lenAuthor);
            System.arraycopy(data, 2+lenAuthor+lenSign, text, 0, lenText);

            String usernameA = new String(author, StandardCharsets.UTF_8);
            String username = text_user_name.getText();
            text_author_name.setText(usernameA);

            String path = "users/".concat(username).concat("/").concat(usernameA).concat(".pub");

            if (!usernameA.equals(username)) {
                PublicKey publicKey = currentUsers.findPubKey(username, usernameA);
                if (publicKey == null) {
                    labelErrorLoad.setText("Ключ не найден или поврежден");
                    labelErrorLoad.setAlignment(Pos.CENTER);
                    labelErrorLoad.setTextFill(Color.RED);
                    return;
                }
                if (currentUsers.checkSignedDoc(selectedFile.getPath(), publicKey)) {
                    text_edit_field.setText(new String(text, StandardCharsets.UTF_8));
                } else {
                    text_edit_field.setText("");
                    text_path.setText("");
                    text_document.setText("");

                    labelErrorLoad.setText("Файл изменен или поврежден!");
                    labelErrorLoad.setAlignment(Pos.TOP_CENTER);
                    labelErrorLoad.setTextFill(Color.RED);
                }
            } else {
                if (currentUsers.checkSignedDoc(selectedFile.getPath(), currentUsers.getPubKey(username, path, "EC"))) {
                    text_edit_field.setText(new String(text, StandardCharsets.UTF_8));
                } else {
                    text_edit_field.setText("");
                    text_path.setText("");
                    text_document.setText("");

                    labelErrorLoad.setText("Файл изменен или поврежден!");
                    labelErrorLoad.setAlignment(Pos.TOP_CENTER);
                    labelErrorLoad.setTextFill(Color.RED);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    void OnAction_Button_ChooseUser(ActionEvent event) {
        String username = text_user_name.getText();

        labelErrorUser.setText("");
        labelErrorLoad.setText("");
        labelErrorSave.setText("");
        //text_user_name.setText("");
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
            labelErrorUser.setTextFill(Color.RED);
            labelErrorUser.setAlignment(Pos.TOP_LEFT);
            return;
        }

        File usersFolder = new File("users");
        if (!usersFolder.exists()) {
            usersFolder.mkdir();
        }

        ArrayList<String> Users = currentUsers.getUsers();
        String userKeys = "users/".concat(username).concat("/").concat(username);

        if (Users.contains(username)) {
            if (!currentUsers.checkKeys(username, userKeys.concat(".pub")) ||
                    !currentUsers.checkKeys(username, userKeys.concat(".prk"))) {
                currentUsers.keyGenEC(username);
                if (!currentUsers.checkKeys(username, userKeys.concat(".pub")) ||
                        !currentUsers.checkKeys(username, userKeys.concat(".prk"))) {
                    return;
                }
            }
            labelErrorUser.setText("Пользователь выбран");
            labelErrorUser.setTextFill(Color.WHITE);
            labelErrorUser.setAlignment(Pos.TOP_LEFT);
        } else {
            labelErrorUser.setText("Пользователь не найден, создана новая пара ключей");
            labelErrorUser.setTextFill(Color.WHITE);
            labelErrorUser.setAlignment(Pos.TOP_LEFT);

            File folder = new File(usersFolder.getName().concat("/").concat(username));
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    System.out.println("impossible...");
                } else {
                    currentUsers.keyGenEC(username);
                }
            }
        }

        if (!currentUsers.checkKeys(username, userKeys.concat(".pub"))
                || !currentUsers.checkKeys(username, userKeys.concat(".prk"))) {
            System.out.println("keys not created!");

            labelErrorUser.setText("Отсутвуют ключи, попробуйте пересоздать пользователя");
            labelErrorUser.setTextFill(Color.RED);
            labelErrorUser.setAlignment(Pos.TOP_LEFT);
            return;
        }

        text_author_name.setDisable(false);
        text_document.setDisable(false);
        text_path.setDisable(false);
        text_edit_field.setDisable(false);
        Button_Browse.setDisable(false);
        Button_Save.setDisable(false);
        Button_DownloadDoc.setDisable(false);

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

        assert menuButtonKeys != null : "fx:id=\"menuButtonKeys\" was not injected: check your FXML file 'file.fxml'.";
        assert text_author_name != null : "fx:id=\"text_author_name\" was not injected: check your FXML file 'file.fxml'.";
        assert menuButtonExit != null : "fx:id=\"menuButtonExit\" was not injected: check your FXML file 'file.fxml'.";
        assert menu != null : "fx:id=\"menu\" was not injected: check your FXML file 'file.fxml'.";
        assert Button_Browse != null : "fx:id=\"Button_Browse\" was not injected: check your FXML file 'file.fxml'.";
        assert text_path != null : "fx:id=\"text_path\" was not injected: check your FXML file 'file.fxml'.";
        assert labelErrorSave != null : "fx:id=\"labelErrorSave\" was not injected: check your FXML file 'file.fxml'.";
        assert text_edit_field != null : "fx:id=\"text_edit_field\" was not injected: check your FXML file 'file.fxml'.";
        assert Button_Save != null : "fx:id=\"Button_Save\" was not injected: check your FXML file 'file.fxml'.";
        assert menuButtonAbout != null : "fx:id=\"menuButtonAbout\" was not injected: check your FXML file 'file.fxml'.";
        assert Button_ChooseUser != null : "fx:id=\"Button_ChooseUser\" was not injected: check your FXML file 'file.fxml'.";
        assert labelErrorUser != null : "fx:id=\"labelErrorUser\" was not injected: check your FXML file 'file.fxml'.";
        assert labelErrorLoad != null : "fx:id=\"labelErrorLoad\" was not injected: check your FXML file 'file.fxml'.";
        assert Button_DownloadDoc != null : "fx:id=\"Button_DownloadDoc\" was not injected: check your FXML file 'file.fxml'.";
        assert text_user_name != null : "fx:id=\"text_user_name\" was not injected: check your FXML file 'file.fxml'.";
        assert text_document != null : "fx:id=\"text_document\" was not injected: check your FXML file 'file.fxml'.";

    }
}
