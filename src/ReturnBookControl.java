class ReturnBookControl {

//File ready for static review
    
    private ReturnBookUi returnBookUi;
    private enum ControlState {INITIALISED, READY, INSPECTING}
    private ControlState state;
    private Library library;
    private Loan currentLoan;


    ReturnBookControl() {
        this.library = Library.getInstance();
        state = ControlState.INITIALISED;
    }


    void setUi(ReturnBookUi returnBookUi) {
        if (!state.equals(ControlState.INITIALISED)) {
            throw new RuntimeException("ReturnBookControl: cannot call setUserInterface except in INITIALISED state");
        }
        this.returnBookUi = returnBookUi;
        returnBookUi.setInterfaceState(ReturnBookUi.State.READY);
        state = ControlState.READY;
    }


    void bookScanned(int bookId) {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("ReturnBookControl: cannot call bookScanned except in READY state");
        }
        Book currentBook = library.book(bookId);
        if (currentBook == null) {
            returnBookUi.display("Invalid book Id");
            return;
        }
        if (!currentBook.onLoan()) {
            returnBookUi.display("book has not been borrowed");
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
        returnBookUi.setInterfaceState(ReturnBookUi.State.INSPECTING);
        state = ControlState.INSPECTING;
    }


    void scanningComplete() {
        if (!state.equals(ControlState.READY)) {
            throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
        }
        returnBookUi.setInterfaceState(ReturnBookUi.State.COMPLETED);
    }


    void dischargeLoan(boolean isDamaged) {
        if (!state.equals(ControlState.INSPECTING)) {
            throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
        }
        library.dischargeLoan(currentLoan, isDamaged);
        currentLoan = null;
        returnBookUi.setInterfaceState(ReturnBookUi.State.READY);
        state = ControlState.READY;
    }
}
