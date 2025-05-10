package org.example.library.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.library.App;
import org.example.library.daos.AccountDao;
import org.example.library.utility.Notification;
import org.example.library.utility.SecurityContextHolder;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtPassword.setText("admin");
        txtUsername.setText("admin");
    }

    private final AccountDao accountDao = new AccountDao();

    public void btnSubmit(ActionEvent actionEvent) throws IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Notification.alter("Vui lòng nhập tên đăng nhập và mật khẩu", Alert.AlertType.ERROR, "Đăng nhập thất bại",
                    null);
            return;
        }

        if (accountDao.authenticate(username, password)) {
            SecurityContextHolder.getInstance().setPassword(password);
            SecurityContextHolder.getInstance().setUsername(username);

            Notification.alter("Đăng nhập thành công", Alert.AlertType.INFORMATION, "Đăng nhập thành công", null);
            App.setRoot("MenuView", "Menu");
        } else {
            Notification.alter("Sai tên đăng nhập hoặc mật khẩu", Alert.AlertType.ERROR, "Đăng nhập thất bại", null);
        }
    }
}
