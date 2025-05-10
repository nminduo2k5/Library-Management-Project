package org.example.library.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import org.example.library.bus.AccountBus;
import org.example.library.utility.Notification;
import org.example.library.utility.SecurityContextHolder;

public class ChangePasswordController {
    public PasswordField pwCur;
    public PasswordField pwNew;
    public PasswordField pwReEnter;

    private final AccountBus accountBus;

    public ChangePasswordController() {
        accountBus = new AccountBus();
    }

    public void onClickOk(ActionEvent actionEvent) {
        String cur = pwCur.getText();
        String newPw = pwNew.getText();
        String reEnter = pwReEnter.getText();

        if (cur.isEmpty() || newPw.isEmpty() || reEnter.isEmpty()) {
            Notification.alter("Vui lòng nhập đầy đủ thông tin", Alert.AlertType.WARNING, "Cảnh báo", null);
            return;
        }

        if (!newPw.equals(reEnter)) {
            Notification.alter("Mật khẩu mới không khớp", Alert.AlertType.WARNING, "Cảnh báo", null);
            return;
        }

        if (!SecurityContextHolder.getInstance().getPassword().equals(cur)) {
            Notification.alter("Mật khẩu hiện tại không đúng", Alert.AlertType.WARNING, "Cảnh báo", null);
            return;
        }

        accountBus.changePassword(SecurityContextHolder.getInstance().getUsername(), newPw);
        Notification.alter("Đổi mật khẩu thành công", Alert.AlertType.INFORMATION, "Thông báo", null);
    }
}
