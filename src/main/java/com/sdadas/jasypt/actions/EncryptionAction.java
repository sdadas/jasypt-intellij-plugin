package com.sdadas.jasypt.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.sdadas.jasypt.components.EncryptorStorage;
import com.sdadas.jasypt.dialogs.EncryptionDialog;
import com.sdadas.jasypt.helper.EncryptionHelper;
import com.sdadas.jasypt.model.EncryptionEntry;
import com.sdadas.jasypt.model.EncryptionMode;
import org.jetbrains.annotations.NotNull;

public class EncryptionAction extends AnAction {

    public boolean alwaysShowDialog() {
        return true;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        event.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;
        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if (file == null || editor == null) return;
        EncryptorStorage storage = project.getComponent(EncryptorStorage.class);
        EncryptionEntry state = getEncryptionEntry(file, storage);
        if (!state.isValid() || alwaysShowDialog()) {
            state = showDialog(state);
        }
        if (state != null && state.isValid()) {
            rewriteFile(project, editor, state);
            setEncryptionEntry(file, storage, state);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    private EncryptionEntry showDialog(EncryptionEntry state) {
        EncryptionDialog dialog = new EncryptionDialog(state);
        boolean ok = dialog.showAndGet();
        if(!ok) return null;
        String password = dialog.password();
        EncryptionMode mode = dialog.mode();
        return new EncryptionEntry(password, mode);
    }

    private EncryptionEntry getEncryptionEntry(VirtualFile file, EncryptorStorage storage) {
        String path = file.getCanonicalPath();
        return storage.getEntry(path);
    }

    private void setEncryptionEntry(VirtualFile file, EncryptorStorage storage, EncryptionEntry entry) {
        String path = file.getCanonicalPath();
        storage.setEntry(path, entry);
    }

    private void rewriteFile(Project project, Editor editor, EncryptionEntry state) {
        Document document = editor.getDocument();
        try {
            String encrypted = encrypt(state.getPassword(), state.getMode(), document.getText());
            Runnable replace = () -> {
                document.setReadOnly(false);
                document.setText(encrypted);
            };
            WriteCommandAction.runWriteCommandAction(project, replace);
        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), "Operation Failed");
        }
    }

    private String encrypt(@NotNull String password, @NotNull EncryptionMode mode, @NotNull String body) {
        EncryptionHelper helper = new EncryptionHelper(password, body);
        return helper.process(mode.encrypt(), mode.decrypt());
    }
 }
