import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {

    private BorrowBookUI userInterface;
    private library library;
    private member member;

    private enum ControlState {INITIALISED, READY, SCANNING, FINALISING, COMPLETED, CANCELLED};
    private ControlState state;
    private List<book> pendingBookList;
    private List<loan> completedLoanList;
    private book book;


    public BorrowBookControl() {
        this.library = library.INSTANCE();
        state = ControlState.INITIALISED;
    }


    public void setUI(BorrowBookUI ui) {
        if (!state.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");
        }
        this.userInterface = ui;
        ui.setState(BorrowBookUI.UI_STATE.READY);
        state = ControlState.READY;
    }


    public void swiped(int memberId) {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
        }

        member = library.getMember(memberId);
        if (member == null) {
            userInterface.display("Invalid memberId");
            return;
        }
        if (library.memberCanBorrow(member)) {
            pendingBookList = new ArrayList<>();
            userInterface.setState(BorrowBookUI.UI_STATE.SCANNING);
            state = ControlState.SCANNING;
        } else {
            userInterface.display("Member cannot borrow at this time");
            userInterface.setState(BorrowBookUI.UI_STATE.RESTRICTED);
        }
    }


    public void scanned(int bookId) {
        book = null;
        if (!state.equals(ControlState.SCANNING)) {
            throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
        }
        book = library.Book(bookId);
        if (book == null) {
            userInterface.display("Invalid bookId");
            return;
        }
        if (!book.Available()) {
            userInterface.display("Book cannot be borrowed");
            return;
        }
        pendingBookList.add(book);
        for (book book : pendingBookList) {
            userInterface.display(book.toString());
        }
        if (library.loansRemainingForMember(member) - pendingBookList.size() == 0) {
            userInterface.display("Loan limit reached");
            complete();
        }
    }


    public void complete() {
        if (pendingBookList.size() == 0) {
            cancel();
        } else {
            userInterface.display("\nFinal Borrowing List");
            for (book book : pendingBookList) {
                userInterface.display(book.toString());
            }
            completedLoanList = new ArrayList<loan>();
            userInterface.setState(BorrowBookUI.UI_STATE.FINALISING);
            state = ControlState.FINALISING;
        }
    }


    public void commitLoans() {
        if (!state.equals(ControlState.FINALISING)) {
            throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
        }
        for (book book : pendingBookList) {
            loan loan = library.issueLoan(book, member);
            completedLoanList.add(loan);
        }
        userInterface.display("Completed Loan Slip");
        for (loan loan : completedLoanList) {
            userInterface.display(loan.toString());
        }
        userInterface.setState(BorrowBookUI.UI_STATE.COMPLETED);
        state = ControlState.COMPLETED;
    }


    public void cancel() {
        userInterface.setState(BorrowBookUI.UI_STATE.CANCELLED);
        state = ControlState.CANCELLED;
    }


}
