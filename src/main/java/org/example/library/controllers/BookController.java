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
import org.example.library.bus.BookBus;
import org.example.library.common.FilePath;
import org.example.library.models.Book;
import org.example.library.utility.ImageUtil;
import org.example.library.utility.Notification;

import java.io.File;


import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class BookController implements Initializable {
    @FXML
    private ImageView imgAvatar;
    @FXML
    private Label lbSelectedImage;
    @FXML
    private TableColumn colRemaining;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<Book> tbBooks;

    @FXML
    private TableColumn<Book, String> colBookId;
    @FXML
    private TableColumn<Book, String> colBookName;
    @FXML
    private TableColumn<Book, String> colPublisher;
    @FXML
    private TableColumn<Book, String> colCategory;
    @FXML
    private TableColumn<Book, String> colPublishDate;
    @FXML
    private TableColumn<Book, String> colAuthor;
    @FXML
    private TextField txtBookId;
    @FXML
    private TextField txtBookName;
    @FXML
    private TextField txtPublisher;
    @FXML
    private TextField txtCategory;
    @FXML
    private TextField txtAuthor;
    @FXML
    private DatePicker dpPublishDate;
    @FXML
    private TextField txtRemaining;

    private final BookBus bookBus;
    private String imagePath;

    public BookController() {
        bookBus = new BookBus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBooks(bookBus.getBooks());

        txtBookId.setText(bookBus.generateBookId());
    }

    private void loadBooks(List<Book> books) {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPublishDate.setCellValueFactory(new PropertyValueFactory<>("publishDate"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colRemaining.setCellValueFactory(new PropertyValueFactory<>("remaining"));

        tbBooks.setItems(FXCollections.observableArrayList(books));
    }

    public void onSelectImg(MouseEvent mouseEvent) {
        File file = ImageUtil.fileChooser();

        if (file != null) {
            imagePath = file.getAbsolutePath();
        }

        String id = txtBookId.getText();

        imagePath = ImageUtil.saveAndLoadImage(file, id, lbSelectedImage, imgAvatar);
    }

    public void onSelectedRow(MouseEvent mouseEvent) {
        Book book = tbBooks.getSelectionModel().getSelectedItem();

        if (book != null) {
            txtBookId.setText(book.getBookId());
            txtBookName.setText(book.getBookName());
            txtPublisher.setText(book.getPublisher());
            txtCategory.setText(book.getCategory());
            txtAuthor.setText(book.getAuthor());
            dpPublishDate.setValue(LocalDate.parse(book.getPublishDate()));
            txtRemaining.setText(String.valueOf(book.getRemaining()));

            imagePath = String.format("%s%s.jpg", FilePath.BASE_PATH, book.getBookId());
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

    public Book getFromForm() {
        String id = txtBookId.getText();
        String name = txtBookName.getText();
        String publisher = txtPublisher.getText();
        String category = txtCategory.getText();
        String author = txtAuthor.getText();
        String publishDate = dpPublishDate.getValue() == null ? "" : dpPublishDate.getValue().toString();
        int remaining;

        if (id.isEmpty() || name.isEmpty() || publisher.isEmpty() || category.isEmpty() || author.isEmpty() || publishDate.isEmpty()) {
            Notification.alter("Vui lòng nhập đầy đủ thông tin", Alert.AlertType.WARNING, "Cảnh báo", null);
            return null;
        }

        try {
            remaining = Integer.parseInt(txtRemaining.getText());
        } catch (Exception e) {
            Notification.alter("Vui lòng nhập số", Alert.AlertType.WARNING, "Cảnh báo", null);
            return null;
        }

        return Book.builder()
                .bookId(id)
                .bookName(name)
                .publisher(publisher)
                .category(category)
                .author(author)
                .publishDate(publishDate)
                .remaining(remaining)
                .image(imagePath)
                .build();
    }

    private void clear() {
        txtBookId.clear();
        txtBookName.clear();
        txtPublisher.clear();
        txtCategory.clear();
        txtAuthor.clear();
        dpPublishDate.setValue(null);
        txtRemaining.clear();
        lbSelectedImage.setVisible(true);
        imgAvatar.setImage(null);
        txtBookId.setText(bookBus.generateBookId());
    }

    public void onClickSave(ActionEvent actionEvent) {
        Book book = getFromForm();

        if (book != null) {
            bookBus.createNewBook(book);
            loadBooks(bookBus.getBooks());
            clear();

            Notification.alter("Lưu sách thành công", Alert.AlertType.INFORMATION, "Thông báo", null);
        }

    }

    public void onClickDelete(ActionEvent actionEvent) {
        Book book = tbBooks.getSelectionModel().getSelectedItem();

        if (!Notification.confirm("Bạn có chắc chắn muốn xóa sách này không?", "Xác nhận", null)) return;

        bookBus.deleteBook(book.getBookId());
        loadBooks(bookBus.getBooks());
        clear();

        Notification.alter("Xóa sách thành công", Alert.AlertType.INFORMATION, "Thông báo", null);
    }

    public void onClickRefresh(ActionEvent actionEvent) {
        clear();
    }

    public void onType(KeyEvent keyEvent) {
        String keyword = txtSearch.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            loadBooks(bookBus.getBooks());
            return;
        }

        List<Book> books = bookBus.search(keyword);
        loadBooks(books);
    }
}
