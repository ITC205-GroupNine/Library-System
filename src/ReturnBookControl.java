class ReturnBookControl {
    
    private ReturnBookUI returnBookUI;
    
    private enum CONTROL_STATE {INITIALISED, READY, INSPECTING}
    
    private CONTROL_STATE controlState;
    
    private library library;
    private loan currentLoan;
    
    
    ReturnBookControl() {
        this.library = library.INSTANCE();
        controlState = CONTROL_STATE.INITIALISED;
    }
    
    
    void setUI(ReturnBookUI returnBookUI) {
        if (!controlState.equals(CONTROL_STATE.INITIALISED)) {
            throw new RuntimeException("ReturnBookControl: " +
                    "cannot call setUI except in INITIALISED controlState");
        }
        this.returnBookUI = returnBookUI;
        returnBookUI.setState(ReturnBookUI.UI_STATE.READY);
        controlState = CONTROL_STATE.READY;
    }
    
    
    void bookScanned(int bookId) {
        if (!controlState.equals(CONTROL_STATE.READY)) {
            throw new RuntimeException("ReturnBookControl: " +
                    "cannot call bookScanned except in READY controlState");
        }
        book currentBook = library.Book(bookId);
        
        if (currentBook == null) {
            returnBookUI.display("Invalid Book Id");
            return;
        }
        if (!currentBook.On_loan()) {
            returnBookUI.display("Book has not been borrowed");
            return;
        }
        currentLoan = library.getLoanByBookId(bookId);
        double overDueFine = 0.0;
        if (currentLoan.isOverDue()) {
            overDueFine = library.calculateOverDueFine(currentLoan);
        }
        returnBookUI.display("Inspecting");
        returnBookUI.display(currentBook.toString());
        returnBookUI.display(currentLoan.toString());
        
        if (currentLoan.isOverDue()) {
            returnBookUI.display(String.format("\nOverdue fine : $%.2f", overDueFine));
        }
        returnBookUI.setState(ReturnBookUI.UI_STATE.INSPECTING);
        controlState = CONTROL_STATE.INSPECTING;
    }
    
    
    void scanningComplete() {
        if (!controlState.equals(CONTROL_STATE.READY)) {
            throw new RuntimeException("ReturnBookControl: " +
                    "cannot call scanningComplete except in READY controlState");
        }
        returnBookUI.setState(ReturnBookUI.UI_STATE.COMPLETED);
    }
    
    void dischargeLoan(boolean isDamaged) {
        if (!controlState.equals(CONTROL_STATE.INSPECTING)) {
            throw new RuntimeException("ReturnBookControl: " +
                    "cannot call dischargeLoan except in INSPECTING controlState");
        }
        library.dischargeLoan(currentLoan, isDamaged);
        currentLoan = null;
        returnBookUI.setState(ReturnBookUI.UI_STATE.READY);
        controlState = CONTROL_STATE.READY;
    }
}
