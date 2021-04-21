package com.sdadas.jasypt.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class EncryptionEntry implements Serializable {

    private String password;

    private EncryptionMode mode;

    public EncryptionEntry(String password, EncryptionMode mode) {
        this.password = password;
        this.mode = mode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EncryptionMode getMode() {
        return mode;
    }

    public void setMode(EncryptionMode mode) {
        this.mode = mode;
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(password) && mode != null;
    }
}
