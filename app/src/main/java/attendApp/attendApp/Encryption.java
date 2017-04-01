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
import javax.crypto.spec.SecretKeySpec;

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

	byte[] encoded = {0,0,0,0,0,0,0,0};
	SecretKey k;
	Encryption() {
		SecureRandom i = new SecureRandom();
		i.setSeed(100);
		KeyGenerator keygenerator = null;
		try {
			keygenerator = KeyGenerator.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keygenerator.init(56, i);
		k = new SecretKeySpec(encoded, "DES");
	}
    public String decrypt(byte[] textEncrypted) {
		String finalText = "";
        try {
            Cipher desCipher;
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.DECRYPT_MODE, k);
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

    public byte[] encrypt(String string) {
        byte[] textEncrypted = {0};
        try {
            Cipher desCipher;
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            desCipher.init(Cipher.ENCRYPT_MODE, k);
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