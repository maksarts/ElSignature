package artsishevskiy.cryptmethods.lab1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class KeyController {

    private UsersControl currentUsers;
    public void setUsers(UsersControl users) {
        currentUsers = users;
    }

    boolean delete;

    public static void deleteKeys(File file) {
        if (!file.exists())
            return;

        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                deleteKeys(f);
            }
        }

        file.delete();
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button Button_Close;

    @FXML
    private Button Button_BrowseKeyExport;

    @FXML
    private Label labelImport;

    @FXML
    private TextField text_author_name;

    @FXML
    private TextField text_keyExport;

    @FXML
    private Button Button_Export;

    @FXML
    private Label labelExport;

    @FXML
    private Label labelDelete;

    @FXML
    private TextField text_path;

    @FXML
    private TextField text_keyImport;

    @FXML
    private Button Button_DeleteKeys;

    @FXML
    private Button Button_Import;

    @FXML
    private Button Button_Confirm;

    @FXML
    private Button Button_BrowseKeyImport;

    @FXML
    private Label labelUser;

    @FXML
    private Button Button_ChooseUser;

    @FXML
    private Button Button_BrowsePath;

    @FXML
    private TextField text_user_name;

    @FXML
    void OnAction_Button_ChooseUser(ActionEvent event) {
        String username = text_user_name.getText();

        if (username.equals("")) {
            text_author_name.setDisable(true);
            text_keyExport.setDisable(true);
            text_keyImport.setDisable(true);
            text_path.setDisable(true);

            Button_BrowseKeyExport.setDisable(true);
            Button_BrowsePath.setDisable(true);
            Button_BrowseKeyImport.setDisable(true);
            Button_Export.setDisable(true);
            Button_Import.setDisable(true);
            Button_DeleteKeys.setDisable(true);

            labelDelete.setText("");
            Button_DeleteKeys.setText("Удалить пару ключей");
            Button_DeleteKeys.setDisable(true);
            //buttonConfirm.setDisable(true);
            delete = false;

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
        } else {

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

            return;
        }

        text_author_name.setDisable(false);
        text_keyExport.setDisable(false);
        text_keyImport.setDisable(false);
        text_path.setDisable(false);

        Button_BrowseKeyExport.setDisable(false);
        Button_BrowsePath.setDisable(false);
        Button_BrowseKeyImport.setDisable(false);
        Button_Export.setDisable(false);
        Button_Import.setDisable(false);
        Button_DeleteKeys.setDisable(false);

        labelDelete.setText("");
        delete = false;

    }

    @FXML
    void OnAction_Button_BrowseKeyImport(ActionEvent event) {
        String user = text_user_name.getText();

        //create default folder if not exist
        File file = new File("./publicKeys");
        if (!file.exists()) {
            file.mkdir();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите ваш публичный ключ");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Публичный ключ(.public)", "*.public"));
        File defaultDirectory = new File("./publicKeys");
        fileChooser.setInitialDirectory(defaultDirectory);
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile == null || !selectedFile.exists()) {
            return;
        }

        text_keyImport.setText(selectedFile.getPath());
    }

    @FXML
    void OnAction_Button_Import(ActionEvent event) {
        String user = text_user_name.getText();
        String pathFrom = text_keyImport.getText();
        String pathTo = "users/".concat(user).concat("/");

        labelImport.setText("");
        labelUser.setText("");
        labelExport.setText("");
        labelDelete.setText("");

        if (!currentUsers.isUser(user)) {
            File file = new File(pathTo);
            if (!file.exists()) {
                file.mkdir();
            }

            currentUsers.keyGenEC(user);
            labelUser.setText("Пользователь не найден, создана новая пара ключей");
            labelUser.setTextFill(Color.WHITE);
            labelUser.setAlignment(Pos.TOP_LEFT);
        }

        pathTo = pathTo.concat("PK").concat("/");
        File file = new File(pathTo);
        if (!file.exists()) {
            file.mkdir();
        }

        String userPathKeys = "users/".concat(user).concat("/").concat(user);
        PrivateKey prKey = currentUsers.getCheckPrkKey(user, userPathKeys.concat(".prkEC"), "EC");
        PublicKey puKey = currentUsers.getCheckPubKey(user, userPathKeys.concat(".pubEC"), "EC");

        if (prKey == null || puKey == null) {
            labelImport.setText("Что-то не так с EC ключами пользователя");
            labelImport.setAlignment(Pos.TOP_CENTER);
            labelImport.setTextFill(Paint.valueOf("RED"));
            return;
        }

        if (pathFrom.equals("")) {
            labelImport.setText("Неверный путь");
            labelImport.setAlignment(Pos.TOP_CENTER);
            labelImport.setTextFill(Color.RED);
            return;
        }

        if (pathTo.equals("")) {
            labelImport.setText("Проверьте путь");
            labelImport.setAlignment(Pos.TOP_CENTER);
            labelImport.setTextFill(Color.RED);
            return;
        }

        try {
            byte[] data = Files.readAllBytes(Path.of(pathFrom));
            int len = currentUsers.convertByte(data[0]);
            byte[] auth = new byte[len];
            System.arraycopy(data, 2, auth, 0, len);

            text_author_name.setText(new String(auth, Charset.defaultCharset()));
            pathTo = pathTo.concat(new String(auth, Charset.defaultCharset()));

            PublicKey publicKey = currentUsers.getPubKey(user, pathFrom, "EC");
            if (publicKey == null) {
                System.out.println("key ruined");

                labelImport.setText("Что-то не так с импортируемым ключом");
                labelImport.setAlignment(Pos.TOP_CENTER);
                labelImport.setTextFill(Color.RED);
                return;
            }
            currentUsers.createKeyDoc(publicKey.getEncoded(), user, new String(auth, Charset.defaultCharset()), "pub", pathTo, prKey);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        labelImport.setText("Ключ успешно импортирован");
        labelImport.setAlignment(Pos.TOP_CENTER);
        labelImport.setTextFill(Color.GREEN);

    }

    @FXML
    void OnAction_Button_Export(ActionEvent event) {
        String user = text_user_name.getText();
        String pathFrom = text_keyExport.getText();
        String pathTo = text_path.getText();

        labelImport.setText("");
        labelUser.setText("");
        labelExport.setText("");
        labelDelete.setText("");

        if (!currentUsers.isUser(user)) {
            File file = new File("users/".concat(user));
            if (!file.exists()) {
                file.mkdir();
            }

            currentUsers.keyGenEC(user);
            labelUser.setText("Пользователь не найден, создана новая пара ключей");
            labelUser.setAlignment(Pos.TOP_LEFT);
            labelUser.setTextFill(Color.WHITE);
        }

        String userPathKeys = "users/".concat(user).concat("/").concat(user);
        PrivateKey prKey = currentUsers.getCheckPrkKey(user, userPathKeys.concat(".prkEC"), "EC");
        PublicKey puKey = currentUsers.getCheckPubKey(user, userPathKeys.concat(".pubEC"), "EC");

        if (prKey == null || puKey == null) {
            System.out.println("keys are broken");

            labelExport.setText("Что-то пошло не так с ключами");
            labelExport.setAlignment(Pos.TOP_CENTER);
            labelExport.setTextFill(Color.RED);
            return;
        }

        if (pathFrom.equals("")) {
            System.out.println("pathFrom are broken");

            labelExport.setText("Неверный путь");
            labelExport.setAlignment(Pos.TOP_CENTER);
            labelExport.setTextFill(Color.RED);
            return;
        }

        if (pathTo.equals("")) {
            System.out.println("pathTo are broken");

            labelExport.setText("Неверный путь");
            labelExport.setAlignment(Pos.TOP_CENTER);
            labelExport.setTextFill(Color.RED);
            return;
        }

        File filePubKey = new File(pathFrom);

        try {
            byte[] data = Files.readAllBytes(Path.of(filePubKey.getPath()));

            int lenA = currentUsers.convertByte(data[0]);
            int lenK = data.length - 2 - lenA;
            byte[] author = new byte[lenA];
            byte[] key = new byte[lenK];

            System.arraycopy(data, 2, author, 0, lenA);
            System.arraycopy(data, 2+lenA, key, 0, lenK);

            String nameAuthor = new String(author, StandardCharsets.UTF_8);
            text_author_name.setText(nameAuthor);

            if (!user.equals(nameAuthor)) {
                labelImport.setText("Ключ могут экспортировать только владельцы");
                labelImport.setAlignment(Pos.TOP_CENTER);
                labelImport.setTextFill(Paint.valueOf("RED"));
                return;
            }

            if (Character.compare(pathTo.charAt(pathTo.length()-1), '\\') != 0) {
                pathTo = pathTo.concat("\\");
            }
            currentUsers.createPublic(key, nameAuthor, pathTo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        labelExport.setText("Ключ успешно экспортирован");
        labelExport.setTextFill(Color.GREEN);
        labelExport.setAlignment(Pos.TOP_CENTER);

    }

    @FXML
    void OnAction_Button_BrowseKeyExport(ActionEvent event) {
        String user = text_user_name.getText();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите подписанный документ");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Публичный ключ(.pub)", "*.pub"));
        File defaultDirectory = new File("./users/".concat(user));
        fileChooser.setInitialDirectory(defaultDirectory);
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile == null || !selectedFile.exists()) {
            return;
        }

        text_keyExport.setText(selectedFile.getPath());
    }

    @FXML
    void OnAction_Button_BrowsePath(ActionEvent event) {
        String user = text_user_name.getText();

        //create default folder if not exist
        File file = new File("./publicKeys");
        if (!file.exists()) {
            file.mkdir();
        }

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Выберите папку для хранения");
        File defaultDirectory = new File("./publicKeys");

        if (!defaultDirectory.exists()) {
            defaultDirectory.mkdir();
        }

        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(new Stage());

        if (selectedDirectory != null) {
            text_path.setText(selectedDirectory.getPath());
        }

        if (selectedDirectory == null || !selectedDirectory.exists()) {
            return;
        }

        text_path.setText(selectedDirectory.getPath());
    }

    @FXML
    void OnAction_Button_DeleteKeys(ActionEvent event) {
        labelDelete.setText("");
        if (!delete) {
            //buttonConfirm.setDisable(false);
            Button_Confirm.setVisible(true);
            Button_DeleteKeys.setText("Отменить");

            labelDelete.setText("Вы уверены?");
            labelDelete.setTextFill(Color.RED);
            labelDelete.setAlignment(Pos.TOP_RIGHT);
            delete = true;
        } else {
            //Button_Confirm.setDisable(true);
            Button_Confirm.setVisible(false);
            Button_DeleteKeys.setText("Удалить пару ключей");
            delete = false;
        }
    }

    @FXML
    void OnAction_Button_Confirm(ActionEvent event) {
        //Button_Confirm.setDisable(true);
        Button_Confirm.setVisible(false);
        Button_DeleteKeys.setText("Удалить пару ключей");

        labelDelete.setText("Ключи удалены");
        labelDelete.setTextFill(Color.GREEN);
        labelDelete.setAlignment(Pos.TOP_CENTER);

        deleteKeys(new File("users/".concat(text_user_name.getText())));
        delete = false;

        text_author_name.setDisable(true);
        text_keyExport.setDisable(true);
        text_keyImport.setDisable(true);
        text_path.setDisable(true);

        Button_BrowseKeyExport.setDisable(true);
        Button_BrowsePath.setDisable(true);
        Button_BrowseKeyImport.setDisable(true);
        Button_Export.setDisable(true);
        Button_Import.setDisable(true);
        Button_DeleteKeys.setDisable(true);

        Button_Confirm.setVisible(false);

        text_user_name.setText("");
        text_path.setText("");
        text_author_name.setText("");
        text_keyExport.setText("");
        text_keyImport.setText("");

        labelImport.setText("");
        labelExport.setText("");
        labelUser.setText("");

    }

    @FXML
    void OnAction_Button_Close(ActionEvent event) {
        Stage st = (Stage) Button_Close.getScene().getWindow();
        st.close();
    }

    @FXML
    void initialize() {

        text_author_name.setDisable(true);
        text_keyExport.setDisable(true);
        text_keyImport.setDisable(true);
        text_path.setDisable(true);

        Button_BrowseKeyExport.setDisable(true);
        Button_BrowsePath.setDisable(true);
        Button_BrowseKeyImport.setDisable(true);
        Button_Export.setDisable(true);
        Button_Import.setDisable(true);
        Button_DeleteKeys.setDisable(true);

        Button_Confirm.setVisible(false);


        assert Button_Close != null : "fx:id=\"Button_Close\" was not injected: check your FXML file 'key.fxml'.";
        assert Button_BrowseKeyExport != null : "fx:id=\"Button_BrowseKeyExport\" was not injected: check your FXML file 'key.fxml'.";
        assert labelImport != null : "fx:id=\"labelImport\" was not injected: check your FXML file 'key.fxml'.";
        assert text_author_name != null : "fx:id=\"text_author_name\" was not injected: check your FXML file 'key.fxml'.";
        assert text_keyExport != null : "fx:id=\"text_keyExport\" was not injected: check your FXML file 'key.fxml'.";
        assert Button_Export != null : "fx:id=\"Button_Export\" was not injected: check your FXML file 'key.fxml'.";
        assert labelExport != null : "fx:id=\"labelExport\" was not injected: check your FXML file 'key.fxml'.";
        assert text_path != null : "fx:id=\"text_path\" was not injected: check your FXML file 'key.fxml'.";
        assert text_keyImport != null : "fx:id=\"text_keyImport\" was not injected: check your FXML file 'key.fxml'.";
        assert Button_DeleteKeys != null : "fx:id=\"Button_DeleteKeys\" was not injected: check your FXML file 'key.fxml'.";
        assert Button_Import != null : "fx:id=\"Button_Import\" was not injected: check your FXML file 'key.fxml'.";
        assert Button_BrowseKeyImport != null : "fx:id=\"Button_BrowseKeyImport\" was not injected: check your FXML file 'key.fxml'.";
        assert labelUser != null : "fx:id=\"labelUser\" was not injected: check your FXML file 'key.fxml'.";
        assert Button_ChooseUser != null : "fx:id=\"Button_ChooseUser\" was not injected: check your FXML file 'key.fxml'.";
        assert Button_BrowsePath != null : "fx:id=\"Button_BrowsePath\" was not injected: check your FXML file 'key.fxml'.";
        assert labelDelete != null : "fx:id=\"labelDelete\" was not injected: check your FXML file 'key.fxml'.";
        assert Button_Confirm != null : "fx:id=\"Button_Confirm\" was not injected: check your FXML file 'key.fxml'.";
        assert text_user_name != null : "fx:id=\"text_user_name\" was not injected: check your FXML file 'key.fxml'.";


    }
}