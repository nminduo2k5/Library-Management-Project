package org.example.library.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.example.library.bus.BookBus;
import org.example.library.bus.BorrowBus;
import org.example.library.bus.ReaderBus;
import org.example.library.models.Book;
import org.example.library.models.Borrow;
import org.example.library.models.Reader;
import org.example.library.utility.Notification;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class BorrowController implements Initializable {
    public TextField txtBookName;
    public TextField txtPublisher;
    public TextField txtRemaining;
    public TextField txtReaderName;
    public TextField txtPhone;
    public TextField txtAddress;
    public TableColumn colId;
    public TableColumn colReaderId;
    public TableColumn colBookId;
    public TableColumn colBorrowDate;
    public TableColumn colDuedate;
    public TableColumn colReturnDate;
    public TableView<Borrow> tbBorrows;
    public ComboBox<String> cbFilter;
    public TextField txtKeyword;
    @FXML
    private ComboBox<String> cbBookId;
    @FXML
    private ComboBox<String> cbReaderId;

    private final BookBus bookBus;
    private final ReaderBus readerBus;
    private final BorrowBus borrowBus;

    public BorrowController() {
        bookBus = new BookBus();
        readerBus = new ReaderBus();
        borrowBus = new BorrowBus();


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbBookId.getItems().addAll(bookBus.getBookIds());
        cbReaderId.getItems().addAll(readerBus.getReaderIds());

        loadBorrow(borrowBus.getAllBorrowsNotReturned());
        cbFilter.getItems().addAll("Tất cả", "Muộn");
        cbFilter.setValue("Tất cả");
    }

    public void loadBorrow(List<Borrow> borrows) {
        ObservableList<Borrow> observableList = FXCollections.observableArrayList(borrows);

        colId.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        colReaderId.setCellValueFactory(new PropertyValueFactory<>("readerId"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colDuedate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        tbBorrows.setItems(observableList);
    }

    public void onSelectReader(ActionEvent actionEvent) {
        Reader reader = readerBus.findReaderBy(cbReaderId.getValue());

        txtReaderName.setText(reader.getReaderName());
        txtPhone.setText(reader.getPhoneNumber());
        txtAddress.setText(reader.getReaderAddress());
    }

    public Borrow getBorrowFromForm() {
        String borrowId = borrowBus.generateBorrowId();
        String readerId = cbReaderId.getValue() == null ? null : cbReaderId.getValue();
        String bookId = cbBookId.getValue() == null ? null : cbBookId.getValue();
        String borrowDate = LocalDate.now().toString();
        String dueDate = LocalDate.now().plusDays(7).toString();
        String returnDate = null;
        String readerName = txtReaderName.getText();
        String bookName = txtBookName.getText();
        String status = "Chưa trả";

        if (bookId == null || readerId == null) {
            Notification.alter("Thông báo", Alert.AlertType.WARNING, "Vui lòng chọn độc giả và sách!", null);
            return null;
        }

        return Borrow.builder()
                .borrowId(borrowId)
                .readerId(readerId)
                .bookId(bookId)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .returnDate(returnDate)
                .readerName(readerName)
                .bookName(bookName)
                .status(status)
                .build();

    }

    public void onSelectBookId(ActionEvent actionEvent) {
        Book book = bookBus.findBookBy(cbBookId.getValue());

        txtBookName.setText(book.getBookName());
        txtPublisher.setText(book.getPublisher());
        txtRemaining.setText(String.valueOf(book.getRemaining()));
    }


    public void onClickBorrow(ActionEvent actionEvent) {
        Borrow borrow = getBorrowFromForm();

        if (borrow != null) {

            if (borrowBus.isBorrowed(borrow.getBookId(), borrow.getReaderId())) {
                Notification.alter("Sách đã được mượn bởi người này và chưa trả !", Alert.AlertType.WARNING, "Thông báo", null);
                return;
            }

            if (bookBus.getRemaining(borrow.getBookId()) == 0) {
                Notification.alter("Sách đã hết!", Alert.AlertType.WARNING, "Thông báo", null);
                return;
            }

            if (borrowBus.getQuantityNotReturnedByReaderId(borrow.getReaderId()) >= 3) {
                Notification.alter("Độc giả đã mượn quá số lượng sách quy định! (>=3)", Alert.AlertType.WARNING, "Thông báo", null);
                return;
            }

            borrowBus.saveBorrow(borrow);
            loadBorrow(borrowBus.getAllBorrowsNotReturned());

            Notification.alter("Mượn sách thành công!", Alert.AlertType.INFORMATION, "Thông báo", null);
            bookBus.decreaseRemaining(borrow.getBookId());
        }
    }

    public void onClickReturn(ActionEvent actionEvent) {
        Borrow borrow = tbBorrows.getSelectionModel().getSelectedItem();

        if (borrow == null) {
            Notification.alter("Vui lòng chọn phiếu mượn!", Alert.AlertType.WARNING, "Thông báo", null);
            return;
        }


        if (borrow.getReturnDate().equals("null")) {
            borrow.setReturnDate(LocalDate.now().toString());
            borrow.setStatus("Đã trả");

            borrowBus.updateBorrow(borrow);

            loadBorrow(borrowBus.getAllBorrowsNotReturned());

            Notification.alter("Trả sách thành công!", Alert.AlertType.INFORMATION, "Thông báo", null);
            bookBus.increaseRemaining(borrow.getBookId());
        }

    }

    public void onClickRenew(ActionEvent actionEvent) {
        Borrow borrow = tbBorrows.getSelectionModel().getSelectedItem();

        if (borrow == null) {
            Notification.alter("Vui lòng chọn phiếu mượn!", Alert.AlertType.WARNING, "Thông báo", null);
            return;
        }

        if (borrow.getReturnDate().equals("null")) {
            borrow.setDueDate(LocalDate.now().plusDays(7).toString());

            System.out.println(borrow);

            borrowBus.updateBorrow(borrow);

            loadBorrow(borrowBus.getAllBorrowsNotReturned());

            Notification.alter("Gia hạn thành công!", Alert.AlertType.INFORMATION, "Thông báo", null);
        }

    }

    public void onChooseFilter(ActionEvent actionEvent) {
        if (cbFilter.getValue().equals("Tất cả")) {
            loadBorrow(borrowBus.getAllBorrowsNotReturned());
        } else {
            loadBorrow(borrowBus.getBorrowsLate());
        }
    }

    public void onSelectedRow(MouseEvent mouseEvent) {
        Borrow borrow = tbBorrows.getSelectionModel().getSelectedItem();

        if (borrow != null) {
            cbReaderId.setValue(borrow.getReaderId());
            cbBookId.setValue(borrow.getBookId());
        }
    }

    public void onType(KeyEvent keyEvent) {
        String keyword = txtKeyword.getText().toLowerCase();

        if (keyword.isEmpty()) {
            loadBorrow(borrowBus.getAllBorrowsNotReturned());
        } else {
            loadBorrow(borrowBus.search(keyword));
        }
    }
}
