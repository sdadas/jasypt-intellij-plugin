package com.sdadas.jasypt.helper;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.salt.RandomSaltGenerator;

import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncryptionHelper {

    private static final Pattern VALUE_PATTERN = Pattern.compile("(ENC|DEC)\\(([^)]+)\\)");

    private final String body;

    private final StringEncryptor encryptor;

    public EncryptionHelper(String password, String body) {
        this.body = body;
        this.encryptor = stringEncryptor(password);
    }

    public StringEncryptor stringEncryptor(String password) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGenerator(new RandomSaltGenerator());
        config.setIvGenerator(new RandomIvGenerator());
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    public String process(boolean encrypt, boolean decrypt) {
        return processBody(val -> processMatch(val, encrypt, decrypt));
    }

    public String encrypt() {
        return processBody(val -> processMatch(val, true, false));
    }

    public String decrypt() {
        return processBody(val -> processMatch(val, false, true));
    }

    public String encryptAndDecrypt() {
        return processBody(val -> processMatch(val, true, true));
    }

    private String processMatch(MatchResult result, boolean encrypt, boolean decrypt) throws EncryptionHelperException {
        String qual = result.group(1);
        String value = result.group(2);
        if(qual.equals("DEC") && encrypt) {
            String encrypted = encryptor.encrypt(value);
            return String.format("ENC(%s)", encrypted);
        } else if(qual.equals("ENC") && decrypt) {
            try {
                String decrypted = encryptor.decrypt(value);
                return String.format("DEC(%s)", decrypted);
            } catch (EncryptionOperationNotPossibleException ex) {
                String valueString = StringUtils.abbreviate(value, 100);
                throw new EncryptionHelperException("Decryption failed for value:\n" + valueString, ex);
            }
        } else {
            return result.group(0);
        }
    }

    private String processBody(Function<MatchResult, String> replace) {
        Matcher matcher = VALUE_PATTERN.matcher(body);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()) {
            String replacement = replace.apply(matcher);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
