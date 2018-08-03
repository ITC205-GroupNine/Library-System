public class ReturnBookControl {

    private ReturnBookUi returnBookUi;

    private enum controlState {INITIALISED, READY, INSPECTING}

    private controlState state;
    private library library;
    private loan currentLoan;


    ReturnBookControl() {
        this.library = library.INSTANCE();
        state = controlState.INITIALISED;
    }


    void setUI(ReturnBookUi ui) {
        if (!state.equals(controlState.INITIALISED)) {
            throw new RuntimeException("ReturnBookControl: cannot call setUI except in INITIALISED state");
        }
        this.returnBookUi = ui;
        ui.setState(ReturnBookUi.uiState.READY);
        state = controlState.READY;
    }


    void bookScanned(int bookId) {
        if (!state.equals(controlState.READY)) {
            throw new RuntimeException("ReturnBookControl: cannot call bookScanned except in READY state");
        }
        book currentBook = library.Book(bookId);

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
        returnBookUi.setState(ReturnBookUi.uiState.INSPECTING);
        state = controlState.INSPECTING;
    }


    void scanningComplete() {
        if (!state.equals(controlState.READY)) {
            throw new RuntimeException("ReturnBookControl: cannot call scanningComplete except in READY state");
        }
        returnBookUi.setState(ReturnBookUi.uiState.COMPLETED);
    }


    void dischargeLoan(boolean isDamaged) {
        if (!state.equals(controlState.INSPECTING)) {
            throw new RuntimeException("ReturnBookControl: cannot call dischargeLoan except in INSPECTING state");
        }
        library.dischargeLoan(currentLoan, isDamaged);
        currentLoan = null;
        returnBookUi.setState(ReturnBookUi.uiState.READY);
        state = controlState.READY;
    }


}
