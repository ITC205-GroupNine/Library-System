public class FixBookControl {

    private FixBookUi userInterface;
    private enum FixBookControlState { INITIALISED, READY, FIXING }
    private FixBookControlState state;
    private library library;
    private Book currentBook;


    public FixBookControl() {
        //this should probably be a getInstance() per Jim's notes. LIBRARY is not under my control, so request update?
        this.library = library.getInstance();
        state = FixBookControlState.INITIALISED;
    }


    public void setUi(FixBookUi newUserInterface) {
        if (!state.equals(FixBookControlState.INITIALISED)) {
            throw new RuntimeException("FixBookControl: cannot call setUi except in INITIALISED state");
        }
        this.userInterface = newUserInterface;
        userInterface.setState(FixBookUi.FixBookUserInterfaceState.READY);
        state = FixBookControlState.READY;
    }


    public void bookScanned(int bookId) {
        if (!state.equals(FixBookControlState.READY)) {
            //Should we be throwing exceptions when they can be avoided?
            throw new RuntimeException("FixBookControl: cannot call bookScanned except in READY state");
        }
        currentBook = library.book(bookId);

        if (currentBook == null) {
            userInterface.display("Invalid bookId");
            return;
        }
        if (!currentBook.damaged()) {
            userInterface.display("book has not been damaged");
            return;
        }
        userInterface.display(currentBook.toString());
        userInterface.setState(FixBookUi.FixBookUserInterfaceState.FIXING);
        state = FixBookControlState.FIXING;
    }


    public void fixBook(boolean fix) {
        if (!state.equals(FixBookControlState.FIXING)) {
            throw new RuntimeException("FixBookControl: cannot call fixBook except in Fixing state");
        }
        if (fix) {
            library.repairBook(currentBook);
        }
        currentBook = null;
        userInterface.setState(FixBookUi.FixBookUserInterfaceState.READY);
        state = FixBookControlState.READY;
    }


    public void scanningComplete() {
        if (!state.equals(FixBookControlState.READY)) {
            throw new RuntimeException("FixBookControl: cannot call scanningComplete except in READY state");
        }
        userInterface.setState(FixBookUi.FixBookUserInterfaceState.COMPLETED);
    }
}
