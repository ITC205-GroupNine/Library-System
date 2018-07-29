class ReturnBookControl {
    
    private ReturnBookUi returnBookUi;
    
    private enum CONTROL_STATE {INITIALISED, READY, INSPECTING}
    
    private CONTROL_STATE controlState;
    
    private Library library;
    private Loan currentLoan;
    
    
    ReturnBookControl() {
        this.library = library.INSTANCE();
        controlState = CONTROL_STATE.INITIALISED;
    }
    
    
    void setUI(ReturnBookUi returnBookUi) {
        if (!controlState.equals(CONTROL_STATE.INITIALISED)) {
            throw new RuntimeException("ReturnBookControl: " +
                    "cannot call setUI except in INITIALISED controlState");
        }
        this.returnBookUi = returnBookUi;
        returnBookUi.setState(ReturnBookUi.UI_STATE.READY);
        controlState = CONTROL_STATE.READY;
    }
    
    
    void bookScanned(int bookId) {
        if (!controlState.equals(CONTROL_STATE.READY)) {
            throw new RuntimeException("ReturnBookControl: " +
                    "cannot call bookScanned except in READY controlState");
        }
        Book currentBook = library.Book(bookId);
        
        if (currentBook == null) {
            returnBookUi.display("Invalid Book Id");
            return;
        }
        if (!currentBook.On_loan()) {
            returnBookUi.display("Book has not been borrowed");
            return;
        }
        currentLoan = library.getLoanByBookId(bookId);
        double overDueFine = 0.0;
        if (currentLoan.isOverDue()) {
            overDueFine = library.calculateOverDueFine(currentLoan);
        }
        returnBookUi.display("Inspecting");
        returnBookUi.display(currentBook.toString());
        returnBookUi.display(currentLoan.toString());
        
        if (currentLoan.isOverDue()) {
            returnBookUi.display(String.format("\nOverdue fine : $%.2f", overDueFine));
        }
        returnBookUi.setState(ReturnBookUi.UI_STATE.INSPECTING);
        controlState = CONTROL_STATE.INSPECTING;
    }
    
    
    void scanningComplete() {
        if (!controlState.equals(CONTROL_STATE.READY)) {
            throw new RuntimeException("ReturnBookControl: " +
                    "cannot call scanningComplete except in READY controlState");
        }
        returnBookUi.setState(ReturnBookUi.UI_STATE.COMPLETED);
    }
    
    void dischargeLoan(boolean isDamaged) {
        if (!controlState.equals(CONTROL_STATE.INSPECTING)) {
            throw new RuntimeException("ReturnBookControl: " +
                    "cannot call dischargeLoan except in INSPECTING controlState");
        }
        library.dischargeLoan(currentLoan, isDamaged);
        currentLoan = null;
        returnBookUi.setState(ReturnBookUi.UI_STATE.READY);
        controlState = CONTROL_STATE.READY;
    }
}
