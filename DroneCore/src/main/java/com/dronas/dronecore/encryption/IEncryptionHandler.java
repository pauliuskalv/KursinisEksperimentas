package com.dronas.dronecore.encryption;

public interface IEncryptionHandler {
    byte[] encrypt(byte[] bytes);
    byte[] decrypt(byte[] bytes);

    String getAlgorithName();
}
