import java.util.ArrayList;
import java.util.List;

public class BorrowBookControl {
	
	private BorrowBookUi ui;
	
	private Library L;
	private Member M;
	private enum CONTROL_STATE { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private CONTROL_STATE state;
	
	private List<Book> PENDING;
	private List<Loan> COMPLETED;
	private Book B;
	
	
	public BorrowBookControl() {
		this.L = L.INSTANCE();
		state = CONTROL_STATE.INITIALISED;
	}
	

	public void setUI(BorrowBookUi ui) {
		if (!state.equals(CONTROL_STATE.INITIALISED)) 
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");
			
		this.ui = ui;
		ui.setState(BorrowBookUi.UI_STATE.READY);
		state = CONTROL_STATE.READY;		
	}

		
	public void Swiped(int memberId) {
		if (!state.equals(CONTROL_STATE.READY)) 
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
			
		M = L.getMember(memberId);
		if (M == null) {
			ui.display("Invalid memberId");
			return;
		}
		if (L.memberCanBorrow(M)) {
			PENDING = new ArrayList<>();
			ui.setState(BorrowBookUi.UI_STATE.SCANNING);
			state = CONTROL_STATE.SCANNING; }
		else 
		{
			ui.display("Member cannot borrow at this time");
			ui.setState(BorrowBookUi.UI_STATE.RESTRICTED); }}
	
	
	public void Scanned(int bookId) {
		B = null;
		if (!state.equals(CONTROL_STATE.SCANNING)) {
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
		}	
		B = L.Book(bookId);
		if (B == null) {
			ui.display("Invalid bookId");
			return;
		}
		if (!B.Available()) {
			ui.display("Book cannot be borrowed");
			return;
		}
		PENDING.add(B);
		for (Book B : PENDING) {
			ui.display(B.toString());
		}
		if (L.loansRemainingForMember(M) - PENDING.size() == 0) {
			ui.display("Loan limit reached");
			Complete();
		}
	}
	
	
	public void Complete() {
		if (PENDING.size() == 0) {
			cancel();
		}
		else {
			ui.display("\nFinal Borrowing List");
			for (Book b : PENDING) {
				ui.display(b.toString());
			}
			COMPLETED = new ArrayList<Loan>();
			ui.setState(BorrowBookUi.UI_STATE.FINALISING);
			state = CONTROL_STATE.FINALISING;
		}
	}


	public void commitLoans() {
		if (!state.equals(CONTROL_STATE.FINALISING)) {
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
		}	
		for (Book b : PENDING) {
			Loan Loan = L.issueLoan(b, M);
			COMPLETED.add(Loan);
		}
		ui.display("Completed Loan Slip");
		for (Loan Loan : COMPLETED) {
			ui.display(Loan.toString());
		}
		ui.setState(BorrowBookUi.UI_STATE.COMPLETED);
		state = CONTROL_STATE.COMPLETED;
	}

	
	public void cancel() {
		ui.setState(BorrowBookUi.UI_STATE.CANCELLED);
		state = CONTROL_STATE.CANCELLED;
	}
	
	
}
