import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {


    private BorrowBookUi borrowBookUi;
    private Library library;
    private member member;
    private enum ControlState {INITIALISED, READY, SCANNING, FINALISING, COMPLETED, CANCELLED};
    private ControlState controlState;
    private List<Book> pendingBookList;
    private List<loan> completedLoanList;
    private Book book;



	public BorrowBookControl() {
		this.library = library.getInstance();
		controlState = ControlState.INITIALISED;
	}
	

	public void setUI(BorrowBookUi borrowBookUi) {
		if (!controlState.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED controlState");
        }
		this.borrowBookUi = borrowBookUi;
		borrowBookUi.setState(BorrowBookUi.UiState.READY);
		controlState = ControlState.READY;
	}

		
	public void swiped(int memberId) {
		if (!controlState.equals(ControlState.READY)) {
            throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY controlState");
        }
			
		member = library.getMember(memberId);
		if (member == null) {
			borrowBookUi.display("Invalid memberId");
			return;
		}
		if (library.memberCanBorrow(member)) {
			pendingBookList = new ArrayList<>();
			borrowBookUi.setState(BorrowBookUi.UiState.SCANNING);
			controlState = ControlState.SCANNING;
		}
		else{
			borrowBookUi.display("getMember cannot borrow at this time");
			borrowBookUi.setState(BorrowBookUi.UiState.RESTRICTED);
		}
	}
	
	
	public void scanned(int bookId) {
		book = null;
		if (!controlState.equals(ControlState.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING controlState");
		}	
		book = library.book(bookId);
		if (book == null) {
			borrowBookUi.display("Invalid bookId");
			return;
		}
		if (!book.available()) {
			borrowBookUi.display("book cannot be borrowed");
			return;
		}
		pendingBookList.add(book);
		for (Book book : pendingBookList) {
			borrowBookUi.display(book.toString());
		}
		if (library.loansRemainingForMember(member) - pendingBookList.size() == 0) {
			borrowBookUi.display("Loan limit reached");
			complete();
		}
	}
	
	
	public void complete() {
		if (pendingBookList.size() == 0) {
			cancel();
		}
		else {
			borrowBookUi.display("\nFinal Borrowing List");
			for (Book book : pendingBookList) {
				borrowBookUi.display(book.toString());
			}
			completedLoanList = new ArrayList<loan>();
			borrowBookUi.setState(BorrowBookUi.UiState.FINALISING);
			controlState = ControlState.FINALISING;
		}
	}


	public void commitLoans() {
		if (!controlState.equals(ControlState.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING controlState");
		}	
		for (Book book : pendingBookList) {
			loan loan = library.issueLoan(book, member);
			completedLoanList.add(loan);
		}
		borrowBookUi.display("Completed Loan Slip");
		for (loan loan : completedLoanList) {
			borrowBookUi.display(loan.toString());
		}
		borrowBookUi.setState(BorrowBookUi.UiState.COMPLETED);
		controlState = ControlState.COMPLETED;
	}

	
	public void cancel() {
		borrowBookUi.setState(BorrowBookUi.UiState.CANCELLED);
		controlState = ControlState.CANCELLED;
	}


}
