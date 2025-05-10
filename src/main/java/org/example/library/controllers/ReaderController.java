package org.example.library.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.example.library.bus.ReaderBus;
import org.example.library.common.FilePath;
import org.example.library.common.Validate;
import org.example.library.models.Reader;
import org.example.library.utility.ImageUtil;
import org.example.library.utility.Notification;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ReaderController implements Initializable {
    @FXML
    private TableView<Reader> tbReaders;
    @FXML
    private TableColumn colReaderId;
    @FXML
    private TableColumn colReaderName;
    @FXML
    private TableColumn colAddress;
    @FXML
    private TableColumn colPhone;
    @FXML
    private TableColumn colDob;
    @FXML
    private TextField txtReaderId;
    @FXML
    private TextField txtReaderName;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtPhone;
    @FXML
    private DatePicker dpDob;
    @FXML
    private ImageView imgAvatar;
    @FXML
    private Label lbSelectedImage;
    @FXML
    private TextField txtSearch;

    private final ReaderBus readerBus;
    private String imagePath;

    public ReaderController() {
        this.readerBus = new ReaderBus();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadReaders(readerBus.getAllReaders());

        txtReaderId.setText(readerBus.generateReaderId());
    }

    private void loadReaders(List<Reader> readers) {
        colReaderId.setCellValueFactory(new PropertyValueFactory<>("readerId"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("readerAddress"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));

        tbReaders.setItems(FXCollections.observableArrayList(readers));
    }

    private Reader getReaderFromForm() {
        String readerId = txtReaderId.getText();
        String readerName = txtReaderName.getText();
        String address = txtAddress.getText();
        String phone = txtPhone.getText();
        String dob = dpDob.getValue() == null ? "" : dpDob.getValue().toString();


        if (readerId.isEmpty() || readerName.isEmpty() || address.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
            Notification.alter("Vui lòng nhập đầy đủ thông tin", Alert.AlertType.WARNING, "Cảnh báo", null);
            return null;
        }

        if (!Validate.isPhone(phone)) {
            Notification.alter("Số điện thoại không hợp lệ", Alert.AlertType.WARNING, "Cảnh báo", null);
            return null;
        }

        return Reader.builder()
                .readerId(readerId)
                .readerName(readerName)
                .readerAddress(address)
                .phoneNumber(phone)
                .dob(dob)
                .image(imagePath)
                .build();
    }

    private void clearForm() {
        txtReaderId.setText(readerBus.generateReaderId());
        txtReaderName.clear();
        txtAddress.clear();
        txtPhone.clear();
        dpDob.setValue(null);
        lbSelectedImage.setVisible(true);
        imgAvatar.setImage(null);

        tbReaders.getSelectionModel().clearSelection();
    }

    public void onSelectImg(MouseEvent mouseEvent) {
        File file = ImageUtil.fileChooser();

        if (file != null) {
            imagePath = file.getAbsolutePath();
        }

        String id = txtReaderId.getText();

        imagePath = ImageUtil.saveAndLoadImage(file, id, lbSelectedImage, imgAvatar);
    }

    public void onClickSave(ActionEvent actionEvent) {
        Reader readerFromForm = getReaderFromForm();

        if (readerFromForm != null) {
            try {
                readerBus.saveInfo(readerFromForm);
            } catch (Exception e) {
                Notification.alter(e.getMessage(), Alert.AlertType.WARNING, "Cảnh báo", null);
            }
            Notification.alter("Lưu độc giả thành công", Alert.AlertType.INFORMATION, "Thông báo", null);
            loadReaders(readerBus.getAllReaders());
            clearForm();
        }
    }

    public void onClickDelete(ActionEvent actionEvent) {
        Reader reader = tbReaders.getSelectionModel().getSelectedItem();

        if (reader == null) {
            Notification.alter("Vui lòng chọn độc giả", Alert.AlertType.WARNING, "Cảnh báo", null);
            return;
        }

        readerBus.deleteReader(reader.getReaderId());
        Notification.alter("Xóa độc giả thành công", Alert.AlertType.INFORMATION, "Thông báo", null);
        loadReaders(readerBus.getAllReaders());
        clearForm();
    }

    public void onClickRefresh(ActionEvent actionEvent) {
        loadReaders(readerBus.getAllReaders());
        clearForm();
    }

    public void onType(KeyEvent keyEvent) {
        String keyword = txtSearch.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            loadReaders(readerBus.getAllReaders());
            return;
        }

        loadReaders(readerBus.search(keyword));
    }

    public void onSelectedRow(MouseEvent mouseEvent) {
        Reader reader = tbReaders.getSelectionModel().getSelectedItem();

        if (reader == null) {
            return;
        }

        txtReaderId.setText(reader.getReaderId());
        txtReaderName.setText(reader.getReaderName());
        txtAddress.setText(reader.getReaderAddress());
        txtPhone.setText(reader.getPhoneNumber());
        dpDob.setValue(LocalDate.parse(reader.getDob()));

        imagePath = String.format("%s%s.jpg", FilePath.BASE_PATH, reader.getReaderId());
        File file = new File(imagePath);

        if (file.exists()) {
            lbSelectedImage.setVisible(false);
            imgAvatar.setImage(new javafx.scene.image.Image(file.toURI().toString()));
        } else {
            imgAvatar.setImage(null);
            lbSelectedImage.setText("Không có ảnh");
            lbSelectedImage.setVisible(true);
        }
    }
}
