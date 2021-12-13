import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class BlockCipher {

    static void saveKey(String text) {
        String dirActual = "CipherThings";
        Path path = Paths.get("");
        String directoryName = path.toAbsolutePath().toString();

        directoryName+="\\"+dirActual+"\\"+"generatedKey.key";

        try {
            File fileText = new File(directoryName);
            FileWriter pw = new FileWriter(fileText, true);

            pw.write(text);
            System.out.println("text stored correctly");
            pw.close();
        }
        catch (Exception e) {
            System.out.println("Error writing text");
        }
    }

    static String readKey() throws IOException {
        String dirActual = "CipheredThings";
        Path path = Paths.get("");
        String directoryName = path.toAbsolutePath().toString();

        directoryName+="\\"+dirActual+"\\"+"generatedKey.key";

        byte[] source = Files.readAllBytes(Path.of(directoryName));
        String message = new String(source);

        byte[] decodedBytes = Base64.getDecoder().decode(message);
        String decodedString = new String(decodedBytes);
        return decodedString;

    }

    static void createKey () {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey key = keyGen.generateKey();
            saveKey(Base64.getEncoder().encodeToString(key.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR");
        }
    }

    static IvParameterSpec createIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    static byte[] encrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] cypherData;
        SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(readKey()), "AES" );
        IvParameterSpec iv = createIv();

        Cipher cipher= Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(ENCRYPT_MODE,key,iv);
        cypherData= cipher.doFinal(data);

        return cypherData;
    }

    public static void main (String[]args) {
        createKey();

    }
}
