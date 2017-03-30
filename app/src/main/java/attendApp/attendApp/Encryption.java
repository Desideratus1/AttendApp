package attendApp.attendApp;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Encryption
{
    /*public static void main(String[] argv) {
        try {
        	SecureRandom i = new SecureRandom();
        	i.setSeed(100);
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            keygenerator.init(56, i);
            SecretKey myDesKey = keygenerator.generateKey();
            System.out.print(decrypt(encrypt("34534543",myDesKey),myDesKey));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }*/

    public static String decrypt(byte[] textEncrypted, SecretKey myDesKey) {
        String finalText = "";
        try {
            Cipher desCipher;
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
            byte[] textDecrypted = desCipher.doFinal(textEncrypted);
            finalText =  "" + new String(textDecrypted);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return finalText;
    }

    public static byte[] encrypt(String string, SecretKey myDesKey) {
        byte [] textEncrypted = {0};
        try {
            Cipher desCipher;
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
            byte[] text = string.getBytes();
            textEncrypted = desCipher.doFinal(text);
            return textEncrypted;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return textEncrypted;
    }
}