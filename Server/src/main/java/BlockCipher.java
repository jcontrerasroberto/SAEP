import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class BlockCipher {

    static void saveKey(String text) {
        String dirActual = "CipherThings";
        Path path = Paths.get("");
        String directoryName = path.toAbsolutePath().toString();

        directoryName+="\\"+dirActual+"\\"+"generated.key";

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

    static byte[] readKey() throws IOException {
        String dirActual = "CipherThings";
        Path path = Paths.get("");
        String directoryName = path.toAbsolutePath().toString();

        directoryName+="\\"+dirActual+"\\"+"generated.key";

        byte[] source = Files.readAllBytes(Path.of(directoryName));
        String message = new String(source);

        byte[] decodedBytes = Base64.getDecoder().decode(message);
        return decodedBytes;
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

    public static byte[] createIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);

        try {
            FileOutputStream stream = new FileOutputStream("IV1.txt");
            stream.write(Base64.getEncoder().encode(iv));
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return iv;
    }

    static byte[] encrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] cypherData;
        SecretKey key = new SecretKeySpec(readKey(), "AES" );
        IvParameterSpec iv = new IvParameterSpec(createIV());

        Cipher cipher= Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(ENCRYPT_MODE,key,iv);
        cypherData= cipher.doFinal(data);

        return cypherData;
    }

    static byte[] decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] originalData;
        SecretKey key = new SecretKeySpec(readKey(), "AES" );

        byte[] ivBytes = Base64.getDecoder().decode(Files.readAllBytes(Path.of("IV1.txt")));
        IvParameterSpec iv= new IvParameterSpec(ivBytes);

        Cipher cipher= Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(DECRYPT_MODE,key,iv);
        originalData= cipher.doFinal(data);

        return originalData;
    }

    public static byte[] loadFile(String sourcePath) throws IOException
    {
        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(sourcePath);
            return readFully(inputStream);
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
    }

    public static byte[] readFully(InputStream stream) throws IOException
    {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1)
        {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    public static void main (String[]args) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        createKey();
        Scanner in = new Scanner(System.in);
        System.out.println("absolute path");
        String namefile = in.nextLine();

        byte[] aux = encrypt(loadFile(namefile));

        System.out.println("Doc:" + Arrays.toString(loadFile(namefile)));
        System.out.println("Cipher: "+ Arrays.toString(aux));
        System.out.println("Decipher: "+ Arrays.toString(decrypt(aux)));
    }
}
