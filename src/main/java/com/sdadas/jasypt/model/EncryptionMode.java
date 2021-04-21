package com.sdadas.jasypt.model;

public enum EncryptionMode {
    ENCRYPT_AND_DECRYPT(true, true),
    ENCRYPT(true, false),
    DECRYPT(false, true);

    private final boolean encrypt;

    private final boolean decrypt;

    EncryptionMode(boolean encrypt, boolean decrypt) {
        this.encrypt = encrypt;
        this.decrypt = decrypt;
    }

    public boolean encrypt() {
        return this.encrypt;
    }

    public boolean decrypt() {
        return this.decrypt;
    }
}
