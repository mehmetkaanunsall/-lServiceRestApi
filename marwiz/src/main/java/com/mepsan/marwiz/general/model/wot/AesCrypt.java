/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 24.11.2017
 */
package com.mepsan.marwiz.general.model.wot;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * This Class for crypt or decrypt String with Advanced Encryption Standard with
 * static key and vector
 */
public class AesCrypt {

    private final byte[] key ; // 128 bit key
    private final byte[] initVector; // 16 bytes Inıt Vector

    /**
     *
     * @param key to crypt and decrypt
     * @param initVector to crypt decrypt
     */
    public AesCrypt(String key, String initVector) {
        this.key = key.getBytes();
        this.initVector = initVector.getBytes();
    }
    
     /**
     *
     * @param key to crypt and decrypt
     * @param initVector to crypt decrypt
     */
    public AesCrypt(byte[] key, byte[] initVector) {
        this.key = key;
        this.initVector = initVector;
    }

    /**
     *
     * @param string to encrypt
     * @return encrypted String from string
     * @throws Exception
     */
    public byte[] encrypt(String string) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(initVector);
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] encrypted = cipher.doFinal(string.getBytes());

        return encrypted;
    }

    /**
     *
     * @param string to encrypt and Base64 encode
     * @return encrypted Base64 String
     * @throws Exception
     */
    public String encryptBase64(String string) throws Exception {
        byte[] encrypted = encrypt(string);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     *
     * @param encrypted bytes to decrypt
     * @return decrypted bytes from encrypted
     * @throws Exception
     */
    public byte[] decrypt(byte[] encrypted) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(initVector);
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

        byte[] original = cipher.doFinal(encrypted);

        return original;
    }

    /**
     *
     * @param encrypted Base64 String to decrypt and Base64 decode
     * @return decrypted String from encrypted
     * @throws Exception
     */
    public String decryptBase64(String encrypted) throws Exception {
        byte[] encryptedByte = Base64.getDecoder().decode(encrypted);
        byte[] decrypted = decrypt(encryptedByte);
        return new String(decrypted);
    }

}
