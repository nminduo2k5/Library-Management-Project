package org.example.library.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.library.bus.BorrowBus;
import org.example.library.bus.ReaderBus;
import org.example.library.models.Borrow;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReturnController implements Initializable {
    public TableView tbBorrows;
    public TableColumn colId;
    public TableColumn colReaderId;
    public TableColumn colBookId;
    public TableColumn colBorrowDate;
    public TableColumn colReturnDate;
    public ComboBox<String> cbReaderId;

    private final BorrowBus borrowBus;
    private final ReaderBus readerBus;

    public ReturnController() {
        borrowBus = new BorrowBus();
        readerBus = new ReaderBus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbReaderId.getItems().addAll(readerBus.getReaderIds());
        cbReaderId.getItems().add("Tất cả");

        cbReaderId.getSelectionModel().selectLast();

        loadBorrows(borrowBus.getBorrowsReturned());
    }

    private void loadBorrows(List<Borrow> list) {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colReaderId.setCellValueFactory(new PropertyValueFactory<>("readerId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colId.setCellValueFactory(new PropertyValueFactory<>("borrowId"));

        tbBorrows.setItems(FXCollections.observableArrayList(list));

    }


    public void onChooseReader(ActionEvent actionEvent) {
        String readerId = cbReaderId.getValue() == null ? "" : cbReaderId.getValue();

        System.out.println(readerId);

        if (readerId.equals("Tất cả")) {
            loadBorrows(borrowBus.getBorrowsReturned());
            return;
        }

        loadBorrows(borrowBus.filterBorrowReturnedByReaderId(readerId));
    }
}
