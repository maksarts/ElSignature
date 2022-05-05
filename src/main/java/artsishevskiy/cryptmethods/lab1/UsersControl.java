package artsishevskiy.cryptmethods.lab1;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Objects;

public class UsersControl {
    private Stage mainStage;

    private final String algorithmHash = "EC";
    private final String algorithmSign = "RipeMD160WithECDSA";

    public UsersControl(Stage newMainStage) {
        mainStage = newMainStage;
    }

    public void setMainStage (Stage newMainStage) {
        mainStage = newMainStage;
    }

    public void exit() {
        System.exit(0);
    }

    /* signing doc */
    private PrivateKey prKey;
    private PublicKey puKey;

    public void keyGenEC(String username) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithmHash);
            keyPairGen.initialize(256, new SecureRandom(username.getBytes(StandardCharsets.UTF_8)));

            KeyPair pair = keyPairGen.genKeyPair();

            prKey = pair.getPrivate();
            puKey = pair.getPublic();

            // для подписи ключей
            createKeyDoc(puKey.getEncoded(), username, "pubEC", prKey);
            createKeyDoc(prKey.getEncoded(), username, "prkEC", prKey);

            // для подписи документа
            createKeyDoc(puKey.getEncoded(), username, "pub", prKey);
            createKeyDoc(prKey.getEncoded(), username, "prk", prKey);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public void keyGenRSA(String username) {
//        keyGenEC(username);
//        if (prKey == null || puKey == null) {
//            return;
//        }
//
//        try {
//            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithmHash);
//            keyPairGen.initialize(1024);
//
//            KeyPair pair = keyPairGen.genKeyPair();
//
//            createKeyDoc(pair.getPrivate().getEncoded(), username, "prk", prKey);
//            createKeyDoc(pair.getPublic().getEncoded(), username, "pub", prKey);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public void createKeyDoc(byte[] data, String username, String key, PrivateKey privateKey) {
        String path = "users/";
        path = path.concat(username).concat("/");
        path = path.concat(username);

        createKeyDoc(data, username, username, key, path, privateKey);
    }

    public void createKeyDoc(byte[] data, String user, String author, String key, String path, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(algorithmSign);
            signature.initSign(privateKey);

            signature.update(data);
            byte[] sign = signature.sign();
            byte[] authorB = author.getBytes(StandardCharsets.UTF_8);

            int lenAuthor = authorB.length;
            int lenSign = sign.length;
            int lenBlob = data.length;

            byte[] res = new byte[2 + lenAuthor + lenSign + lenBlob];
            res[0] = (byte) lenAuthor;
            res[1] = (byte) lenSign;

            System.arraycopy(authorB, 0, res, 2, lenAuthor);
            System.arraycopy(data, 0, res, 2+lenAuthor, lenBlob);
            System.arraycopy(sign, 0, res, 2+lenAuthor+lenBlob, lenSign);

            System.out.println("\n\nlenb: ");
            System.out.println(path.concat(key));
            System.out.println(lenBlob);
            System.out.println(privateKey.getEncoded().length);

            Files.write(Path.of(path.concat(".").concat(key)), res);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createPublic(byte[] data, String username, String path) {
        try {
            byte[] author = username.getBytes(StandardCharsets.UTF_8);

            int lenAuthor = author.length;
            int lenBlob = data.length;

            byte[] res = new byte[2 + lenAuthor + lenBlob];
            res[0] = (byte) lenAuthor;
            res[1] = (byte) 0;

            path = path.concat(username).concat(".public");

            System.arraycopy(author, 0, res, 2, lenAuthor);
            System.arraycopy(data, 0, res, 2+lenAuthor, lenBlob);

            System.out.println("\n\nlenb PUBLIC: ");
            System.out.println(path);
            System.out.println(lenBlob);

            Files.write(Path.of(path), res);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkKeys(String username, String path) {
        boolean res = false;

        try {
            PublicKey publicKey = getCheckPubKey(username, "users/"+username+"/"+username+".pubEC", "EC");
            if (publicKey == null) {
                return false;
            }

//            System.out.println(publicKey);

            byte[] data = Files.readAllBytes(Path.of(path));

            int lenAuthor = convertByte(data[0]);
            int lenSign = convertByte(data[1]);
            int lenBlob = data.length - lenAuthor - lenSign - 2;

            byte[] author = new byte[lenAuthor];
            byte[] sign = new byte[lenSign];
            byte[] textB = new byte[lenBlob];

            System.arraycopy(data, 2, author, 0, lenAuthor);
            System.arraycopy(data, 2+lenAuthor, textB, 0, lenBlob);
            System.arraycopy(data, 2+lenAuthor+lenBlob, sign, 0, lenSign);

            Signature signature = Signature.getInstance(algorithmSign);
            signature.initVerify(publicKey);

            signature.update(textB);
            res = signature.verify(sign);

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return res;
    }

    public PublicKey getCheckPubKey(String username, String path, String algorithm) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            String pathEC = "users/".concat(username).concat("/").concat(username).concat(".pubEC");

            PublicKey pk = KeyFactory.getInstance(algorithmHash).generatePublic(new X509EncodedKeySpec(getKey(pathEC)));
            PublicKey publicKey = null;

            if (algorithmHash.equals(algorithm)) {
                publicKey = KeyFactory.getInstance(algorithmHash).generatePublic(new X509EncodedKeySpec(getKey(path)));
            }
            if (publicKey == null) {
                return null;
            }

            byte[] data = Files.readAllBytes(Path.of(path));

            int lenAuthor = convertByte(data[0]);
            int lenSign = convertByte(data[1]);
            int lenBlob = data.length - lenAuthor - lenSign - 2;

            byte[] author = new byte[lenAuthor];
            byte[] sign = new byte[lenSign];
            byte[] textB = new byte[lenBlob];

            System.arraycopy(data, 2, author, 0, lenAuthor);
            System.arraycopy(data, 2+lenAuthor, textB, 0, lenBlob);
            System.arraycopy(data, 2+lenAuthor+lenBlob, sign, 0, lenSign);

            Signature signature = Signature.getInstance(algorithmSign);
            signature.initVerify(pk);

            signature.update(textB);
            if (!signature.verify(sign)) {
                System.out.println("not verified, if it wasn't .public: ".concat(path));
            }
            return publicKey;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public PublicKey getPubKey(String username, String path, String algorithm) {
        try {
            return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(getKey(path)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public PrivateKey getCheckPrkKey(String username, String path, String algorithm) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            String pathEC = "users/".concat(username).concat("/").concat(username).concat(".pubEC");

            PublicKey pk = KeyFactory.getInstance(algorithmHash).generatePublic(new X509EncodedKeySpec(getKey(pathEC)));
            PrivateKey privateKey = null;

            if (algorithmHash.equals(algorithm)) {
                privateKey = KeyFactory.getInstance(algorithmHash).generatePrivate(new PKCS8EncodedKeySpec(getKey(path)));
            }
            if (privateKey == null) {
                return null;
            }

            byte[] data = Files.readAllBytes(Path.of(path));

            int lenAuthor = convertByte(data[0]);
            int lenSign = convertByte(data[1]);
            int lenBlob = data.length - lenAuthor - lenSign - 2;

            byte[] author = new byte[lenAuthor];
            byte[] sign = new byte[lenSign];
            byte[] textB = new byte[lenBlob];

            System.arraycopy(data, 2, author, 0, lenAuthor);
            System.arraycopy(data, 2+lenAuthor, textB, 0, lenBlob);
            System.arraycopy(data, 2+lenAuthor+lenBlob, sign, 0, lenSign);

            Signature signature = Signature.getInstance(algorithmSign);
            signature.initVerify(pk);

            signature.update(textB);
            if (signature.verify(sign)) {
                return privateKey;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public byte[] getKey(String path) {
        try {
            byte[] data = Files.readAllBytes(Path.of(path));

            int lenAuthor = convertByte(data[0]);
            int lenSign = convertByte(data[1]);
            int lenBlob = data.length - lenAuthor - lenSign - 2;

            byte[] textB = new byte[lenBlob];

            System.arraycopy(data, 2+lenAuthor, textB, 0, lenBlob);

            return textB;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean signDoc(String username, String text, String filename) {
        try {
            String path = "users/".concat(username).concat("/").concat(username);

            if (!checkKeys(username, path.concat(".prk")) || !checkKeys(username, path.concat(".pub"))) {
                return false;
            }

            PrivateKey privateKey = KeyFactory.getInstance(algorithmHash).generatePrivate(new PKCS8EncodedKeySpec(getKey(path.concat(".prk"))));
            PublicKey publicKey= KeyFactory.getInstance(algorithmHash).generatePublic(new X509EncodedKeySpec(getKey(path.concat(".pub"))));

            String userPath = "users/".concat(username).concat("/");
            byte[] data = text.getBytes(StandardCharsets.UTF_8);

            Signature signature = Signature.getInstance(algorithmSign);
            signature.initSign(privateKey);
            signature.update(data);

            byte[] sign = signature.sign();
            byte[] author = username.getBytes(StandardCharsets.UTF_8);

            int lenAuthor = author.length;
            int lenSign = sign.length;
            int lenBlob = data.length;

            byte[] res = new byte[2 + lenAuthor + lenSign + lenBlob];
            res[0] = (byte) lenAuthor;
            res[1] = (byte) lenSign;

            System.arraycopy(author, 0, res, 2, lenAuthor);
            System.arraycopy(sign, 0, res, 2 + lenAuthor, lenSign);
            System.arraycopy(data, 0, res, 2 + lenAuthor + lenSign, lenBlob);

            Files.write(Path.of(filename), res);

            signature.initVerify(publicKey);
            signature.update(data);

            return signature.verify(sign);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checkSignedDoc(String path, PublicKey publicKey) {
        boolean res = false;

        try {
            byte[] data = Files.readAllBytes(Path.of(path));

            int lenAuthor = convertByte(data[0]);
            int lenSign = convertByte(data[1]);
            int lenBlob = data.length - lenAuthor - lenSign - 2;

            byte[] author = new byte[lenAuthor];
            byte[] sign = new byte[lenSign];
            byte[] textB = new byte[lenBlob];

            System.arraycopy(data, 2, author, 0, lenAuthor);
            System.arraycopy(data, 2+lenAuthor, sign, 0, lenSign);
            System.arraycopy(data, 2+lenAuthor+lenSign, textB, 0, lenBlob);

            Signature signature = Signature.getInstance(algorithmSign);
            signature.initVerify(publicKey);
            signature.update(textB);

            res = signature.verify(sign);

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return res;
    }

    public PublicKey findPubKey(String username, String author) {
        String pathUser = "users/".concat(username).concat("/");
        String pathPKEC = pathUser.concat(username).concat(".pubEC");
        String pathPKRSA = pathUser.concat("PK/");

        File dir = new File(pathPKRSA);
        if (!dir.exists()) {
            dir.mkdir();
            return null;
        }

        PublicKey publicKey = null;

        for (var file : Objects.requireNonNull(dir.listFiles())) {
            if (file.exists() && file.getName().contains(".pub")) {
                if (file.getName().contains(author)) {
//                    System.out.println(file.getPath());
                    publicKey = getCheckPubKey(username, file.getPath(), "RSA");
//                    System.out.println(publicKey);
                }
            }
        }

        return publicKey;
    }

    /* usefully function */
    public ArrayList<String> getUsers() {
        ArrayList<String> Users = new ArrayList<String>();

        File folder = new File(System.getProperty("user.dir").concat("/users"));
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                Users.add(fileEntry.getName());
            }
        }

        return Users;
    }

    public boolean isUser(String mbUser) {
        var Users = getUsers();
        for (var user : Users) {
            if (Objects.equals(mbUser, user)) {
                return true;
            }
        }
        return false;
    }

    public int convertByte(byte num) {
        if (num >= 0) {
            return num;
        }

        int buf1 = num * (-1);

        buf1 = 256 - buf1;
        return buf1;
    }
}

