package com.dronas.dronecore.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Credit: https://www.javainterviewpoint.com/aes-256-encryption-and-decryption/
 */
public class AESEncryptionHandler implements IEncryptionHandler {
    private final int KEY_SIZE = 256;

    private SecretKey mSecretKey;
    private byte[] mIV;

    public AESEncryptionHandler() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(getAlgorithName());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }
        keyGenerator.init(KEY_SIZE);

        // Generate Key
        mSecretKey = keyGenerator.generateKey();

        // Generating IV.
        mIV = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(mIV);
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(getFullAlgorithmName());
            SecretKeySpec keySpec = new SecretKeySpec(mSecretKey.getEncoded(), getAlgorithName());
            IvParameterSpec ivSpec = new IvParameterSpec(mIV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        try {
            Cipher cipher = Cipher.getInstance(getAlgorithName());
            SecretKeySpec keySpec = new SecretKeySpec(mSecretKey.getEncoded(), getAlgorithName());
            IvParameterSpec ivSpec = new IvParameterSpec(mIV);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getAlgorithName() {
        return "AES";
    }

    public String getFullAlgorithmName() {
        return "AES/CBC/PKCS5Padding";
    }
}