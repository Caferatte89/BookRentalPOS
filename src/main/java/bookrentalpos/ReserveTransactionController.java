package bookrentalpos;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import my.edu.tarc.dco.bookrentalpos.Book;
import my.edu.tarc.dco.bookrentalpos.Member;
import my.edu.tarc.dco.bookrentalpos.Transaction;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ReserveTransactionController implements Initializable, TableInterface {
    public Label dateTime;
    public TextField bookIDField;
    public TextArea bookDetailTextArea;
    public TextField memberIDField;
    public TextArea memberDetailTextArea;
    public Button backButton;
    public TableView reserveTransactionTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Clock.display(dateTime);
        Main.tm.updateBookReservationStatus();
    }

    @Override
    public void reloadTableView() {
        ObservableList ol = reserveTransactionTable.getItems();
        ol.remove(0, ol.size());
        String memID_str = memberIDField.getText();
        if (memID_str.trim().isEmpty()) {
            ol.remove(0, ol.size());
            return;
        }

        int memID;
        try {
            memID = Integer.parseInt(memID_str);
        } catch (NumberFormatException e) {
            ol.remove(0, ol.size());
            return;
        }

        Member mem = Main.mm.getMember(memID);
        if (mem == null) {
            ol.remove(0, ol.size());
            return;
        }

        ArrayList<Book> reservedBooks = Main.tm.getMemberActiveReservations(memID);
        for (int a = 0; a < reservedBooks.size(); a++) {
            ReserveTransactionTableData rttd = new ReserveTransactionTableData(
                    Main.tm.getBookLastReservedTransaction(reservedBooks.get(a).getId()).getDateCreated(),
                    reservedBooks.get(a).getId() + "",
                    reservedBooks.get(a).getName(),
                    reservedBooks.get(a).getAuthor()
            );
            ol.add(rttd);
        }

    }

    @Override
    public void tableOnClick(Event event) {

    }

    @Override
    public void tableOnKeyPressed(Event event) {

    }


    public void backToTransChoose(MouseEvent event) throws IOException {
        Parent mainMenuParent = FXMLLoader.load(getClass().getResource("/FXML/TransactionManager/transactionChoose.fxml"));
        Scene mainMenuScene = new Scene(mainMenuParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainMenuScene);
    }

    // =================================================================================================================
    // Event functions
    // =================================================================================================================
    public void bookIDFieldOnReleased(Event event) {
        reloadBookDetailTextArea();
    }

    public void memberIDFieldOnReleased(Event event) {
        reloadMemberDetailTextArea();
    }

    public void bookIDFieldOnPressed(Event event) {
        if (((KeyEvent) event).getCode() == KeyCode.ENTER)
            reloadBookDetailTextArea();
    }

    public void memberIDFieldOnPressed(Event event) {
        if (((KeyEvent) event).getCode() == KeyCode.ENTER)
            reloadMemberDetailTextArea();
    }

    public void checkReservationOnPressed(Event event) {
        int memID;
        try {
            memID = Integer.parseInt(memberIDField.getText());
        } catch (NumberFormatException e) {
            Dialog.alertBox("Invalid member ID");
            return;
        }
        Member mem = Main.mm.getMember(memID);
        if (mem == null) {
            Dialog.alertBox("Member ID not found");
            return;
        }
        reloadTableView();
    }

    public void addReservationOnPressed(Event event) {
        int bookID;
        int memID;
        try {
            bookID = Integer.parseInt(bookIDField.getText());
            memID = Integer.parseInt(memberIDField.getText());
        } catch (NumberFormatException e) {
            Dialog.alertBox("Invalid bookID or memID");
            return;
        }
        Book bk = Main.bm.getBookById(bookID);
        Member mem = Main.mm.getMember(memID);
        if (bk == null) {
            Dialog.alertBox("BookID do not exist");
            return;
        }

        if (mem == null) {
            Dialog.alertBox("BookID do not exist");
            return;
        }

        if (!bk.isRented()) {
            Dialog.alertBox("Only books that is currently rented can be reserved.");
            return;
        }

        if(bk.getLastRentedBy() == mem.getId()) {
            Dialog.alertBox("Uhh, but you are the one renting this book... (._.)");
            return;
        }

        Transaction t = new Transaction(Main.sm.getLogOnStaff().getId(), mem.getId(), bk.getId());
        if (Main.tm.addTransaction(t)) {
            Dialog.alertBox("Book reserved successfully;\nNote: book reservation will be cancelled if the book is not rented 7 days after its returned.");
        } else Dialog.alertBox("Something went wrong when trying to reserve the book");
        reloadTableView();

    }
    // =================================================================================================================
    // =================================================================================================================

    public void reloadMemberDetailTextArea() {
        String memID_str = memberIDField.getText();
        int memID;
        try {
            memID = Integer.parseInt(memID_str);
        } catch (NumberFormatException e) {
            return;
        }
        Member mem;
        if ((mem = Main.mm.getMember(memID)) != null) {
            memberDetailTextArea.setText(mem.toString());
        } else {
            memberDetailTextArea.setText("");
        }
    }

    public void reloadBookDetailTextArea() {
        String bookID_str = bookIDField.getText();
        int bookID;
        try {
            bookID = Integer.parseInt(bookID_str);
        } catch (NumberFormatException e) {
            return;
        }
        Book bk;
        if ((bk = Main.bm.getBookById(bookID)) != null) {
            bookDetailTextArea.setText(bk.toString());
        } else {
            bookDetailTextArea.setText("");
        }
    }
}


