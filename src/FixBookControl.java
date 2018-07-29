public class FixBookControl {

    private FixBookUI userInterface;
    private enum fixBookControlState { INITIALISED, READY, FIXING }

    private fixBookControlState state;
    private library library;
    private book currentBook;


    public FixBookControl() {
        this.library = library.INSTANCE();
        state = fixBookControlState.INITIALISED;
    }


    public void setUI(FixBookUI userInterface) {
        if (!state.equals(fixBookControlState.INITIALISED)) {
            throw new RuntimeException("FixBookControl: cannot call setUI except in INITIALISED state");
        }
        this.userInterface = userInterface;
        userInterface.setState(FixBookUI.UI_STATE.READY);
        state = fixBookControlState.READY;
    }


    public void bookScanned(int bookId) {
        if (!state.equals(fixBookControlState.READY)) {
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
        state = fixBookControlState.FIXING;
    }


    public void fixBook(boolean fix) {
        if (!state.equals(fixBookControlState.FIXING)) {
            throw new RuntimeException("FixBookControl: cannot call fixBook except in FIXING state");
        }
        if (fix) {
            library.repairBook(currentBook);
        }
        currentBook = null;
        userInterface.setState(FixBookUI.UI_STATE.READY);
        state = fixBookControlState.READY;
    }


    public void scanningComplete() {
        if (!state.equals(fixBookControlState.READY)) {
            throw new RuntimeException("FixBookControl: cannot call scanningComplete except in READY state");
        }
        userInterface.setState(FixBookUI.UI_STATE.COMPLETED);
    }
}
