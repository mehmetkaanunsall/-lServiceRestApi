/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 22.06.2017 12:30:56
 */
package com.mepsan.marwiz.general.httpclient.business;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AESEncryptor {

    private static final byte[] IV = {
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0
    };

    private static final byte[] keys = {
        1, 1, 1, 1, 1, 1, 1, 1,
        1, 1, 1, 1, 1, 1, 1, 1
    };

    public String encrypt(String value) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(keys, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(IV));

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(AESEncryptor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String decrypt(String encrypted) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(keys, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(IV));

            System.out.println(  "---"  + encrypted);
            System.out.println(Base64.decodeBase64(encrypted));
                byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original,Charset.forName("UTF-8"));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
        }

        return null;
    }

}
