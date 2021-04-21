package com.sdadas.jasypt.components;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.sdadas.jasypt.model.EncryptionEntry;
import com.sdadas.jasypt.model.EncryptionMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@State(name = "encryptor-state", storages = @Storage("encryptor.xml"))
public class EncryptorStorage implements PersistentStateComponent<EncryptorStorage.State> {

    private State state = new State();

    @Override
    public @Nullable EncryptorStorage.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public EncryptionEntry getEntry(String path) {
        String password = PasswordSafe.getInstance().getPassword(ca(path));
        EncryptionMode mode = this.state.modes.get(path);
        return new EncryptionEntry(password, mode);
    }

    public void setEntry(String path, EncryptionEntry entry) {
        PasswordSafe.getInstance().setPassword(ca(path), entry.getPassword());
        this.state.modes.put(path, entry.getMode());
    }

    private CredentialAttributes ca(String path) {
        String serviceName = EncryptorStorage.class.getSimpleName();
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName(serviceName, path));
    }

    public static class State {
        public Map<String, EncryptionMode> modes = new HashMap<>();
    }
}
