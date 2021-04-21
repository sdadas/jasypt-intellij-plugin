package com.sdadas.jasypt.dialogs;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.FormBuilder;
import com.sdadas.jasypt.model.EncryptionEntry;
import com.sdadas.jasypt.model.EncryptionMode;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class EncryptionDialog extends DialogWrapper {

    private static final String[] MODES = {"Encrypt or decrypt", "Encrypt", "Decrypt"};

    private final EncryptionEntry defaultState;

    private JTextField password;

    private JComboBox<String> mode;

    public EncryptionDialog(EncryptionEntry state) {
        super(true);
        this.defaultState = state;
        init();
        setTitle("Encrypt / Decrypt Values");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        password = new JTextField();
        password.setPreferredSize(new Dimension(300, 30));
        password.setText(StringUtils.stripToEmpty(defaultState.getPassword()));
        mode = new ComboBox<>(MODES);
        if(defaultState.getMode() != null) {
            mode.setSelectedIndex(defaultState.getMode().ordinal());
        } else {
            mode.setSelectedIndex(0);
        }
        FormBuilder form = FormBuilder.createFormBuilder();
        form.addLabeledComponent("Password:", password);
        form.addLabeledComponent("Mode:", mode);
        return form.getPanel();
    }

    public String password() {
        return password.getText().trim();
    }

    public EncryptionMode mode() {
        int selected = mode.getSelectedIndex();
        return EncryptionMode.values()[selected];
    }
}
