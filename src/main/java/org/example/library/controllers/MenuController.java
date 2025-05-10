package org.example.library.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.example.library.App;
import org.example.library.utility.Notification;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public ListView<String> optionMenu;
    public AnchorPane pane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        optionMenu.getItems().addAll("Quản lý sách", "Quản lý độc giả", "Quản lý mượn", "Quản lý trả", "Đổi mật khẩu");
        optionMenu.getSelectionModel().selectFirst();

        loadView("BookView");
    }

    private void loadView(String fxmlName) {
        try {
            AnchorPane childPane = FXMLLoader.load(getClass().getResource("/org/example/library/" + fxmlName + ".fxml"));
            pane.getChildren().setAll(childPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onChooseMenu(MouseEvent mouseEvent) {
        String selectedMenu = optionMenu.getSelectionModel().getSelectedItem();
        switch (selectedMenu) {
            case "Quản lý sách":
                loadView("BookView");
                break;
            case "Quản lý độc giả":
                loadView("ReaderView");
                break;
            case "Quản lý mượn":
                loadView("BorrowView");
                break;
            case "Quản lý trả":
                loadView("ReturnView");
                break;

            case "Đổi mật khẩu":
                loadView("ChangePasswordView");
                break;
        }
    }

    public void onClickLogout(ActionEvent actionEvent) throws IOException {
        if(!Notification.confirm("Bạn có chắc chắn muốn đăng xuất?", "Đăng xuất", null)){
            return;
        }


        App.setRoot("LoginView", "Đăng nhập");
    }
}
