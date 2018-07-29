public class FixBookControl {
	
	private FixBookUI userInterface;
	private enum CONTROL_STATE { INITIALISED, READY, FIXING }

	private CONTROL_STATE state;
	
	private library library;
	private book currentBook;


	public FixBookControl() {
	    this.library = library.INSTANCE();
	    state = CONTROL_STATE.INITIALISED;
	}
	
	
	public void setUI(FixBookUI ui) {
	    if (!state.equals(CONTROL_STATE.INITIALISED)) {
	        throw new RuntimeException("FixBookControl: cannot call setUI except in INITIALISED state");
		}
	    this.userInterface = ui;
	    ui.setState(FixBookUI.UI_STATE.READY);
	    state = CONTROL_STATE.READY;
	}


	public void bookScanned(int bookId) {
		if (!state.equals(CONTROL_STATE.READY)) {
		    throw new RuntimeException("FixBookControl: cannot call bookScanned except in READY state");
		}	
		currentBook = library.Book(bookId);
		
		if (currentBook == null) {
            userInterface.display("Invalid bookId");
		    return;
		}
		if (!currentBook.Damaged()) {
            userInterface.display("Book has not been damaged");
		    return;
		}
        userInterface.display(currentBook.toString());
        userInterface.setState(FixBookUI.UI_STATE.FIXING);
		state = CONTROL_STATE.FIXING;		
	}


	public void fixBook(boolean fix) {
		if (!state.equals(CONTROL_STATE.FIXING)) {
		    throw new RuntimeException("FixBookControl: cannot call fixBook except in FIXING state");
		}	
		if (fix) {
		    library.repairBook(currentBook);
		}
		currentBook = null;
        userInterface.setState(FixBookUI.UI_STATE.READY);
		state = CONTROL_STATE.READY;		
	}

	
	public void scanningComplete() {
		if (!state.equals(CONTROL_STATE.READY)) {
		    throw new RuntimeException("FixBookControl: cannot call scanningComplete except in READY state");
		}
        userInterface.setState(FixBookUI.UI_STATE.COMPLETED);
	}
}
