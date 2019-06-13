package com.dronas.dronecore.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Arrays;

/**
 * Credit: https://gist.github.com/dmydlarz/32c58f537bb7e0ab9ebf
 */
public class RsaEncryptionHandler implements IEncryptionHandler {
    private final int KEY_SIZE = 4096;

    private KeyPair mKeyPair;
    private PublicKey mPublicKey;
    private PrivateKey mPrivateKey;

    public RsaEncryptionHandler() {
        try {
            mKeyPair = buildKeyPair();
            mPublicKey = mKeyPair.getPublic();
            mPrivateKey = mKeyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(getClass().getName() + ": Initialization error");
            e.printStackTrace();
        }
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(getAlgorithName());
            cipher.init(Cipher.ENCRYPT_MODE, mPrivateKey);

            byte[] encryptedBytes = new byte[bytes.length];

            // (KEY_SIZE / 8) - 11
            for (int i = 0; i < (bytes.length / ((KEY_SIZE / 8) - 11)); i ++) {
                System.arraycopy(cipher.doFinal(
                                Arrays.copyOfRange(bytes,
                                        i * ((KEY_SIZE / 8) - 11),
                                        (i+1) * ((KEY_SIZE / 8) - 11) <= bytes.length ? (i+1) * ((KEY_SIZE / 8) - 11) : bytes.length)
                                ),

                                0,
                                encryptedBytes,
                                i * ((KEY_SIZE / 8) - 11),
                                (i+1) * ((KEY_SIZE / 8) - 11) <= bytes.length ? ((KEY_SIZE / 8) - 11) : (i+1) * ((KEY_SIZE / 8) - 11) - bytes.length);
            }

            return encryptedBytes;
        } catch (NoSuchAlgorithmException e) {
            System.out.println(getClass().getName() + ": No such algorithm exception. ");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            System.out.println(getClass().getName() + ": No such padding exception");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println(getClass().getName() + ": Invalid key exception");
            e.printStackTrace();
        } catch (BadPaddingException e) {
            System.out.println(getClass().getName() + ": Bad padding exception");
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            System.out.println(getClass().getName() + ": Illegal block size exception");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(getAlgorithName());
            cipher.init(Cipher.DECRYPT_MODE, mPublicKey);

            return cipher.doFinal(bytes);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(getClass().getName() + ": No such algorithm exception. ");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            System.out.println(getClass().getName() + ": No such padding exception");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println(getClass().getName() + ": Invalid key exception");
            e.printStackTrace();
        } catch (BadPaddingException e) {
            System.out.println(getClass().getName() + ": Bad padding exception");
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            System.out.println(getClass().getName() + ": Illegal block size exception");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getAlgorithName() {
        return "RSA";
    }

    private KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = KEY_SIZE;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(getAlgorithName());
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }
}
