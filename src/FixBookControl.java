public class FixBookControl {

    private FixBookUi userInterface;
    private enum fixBookControlState { INITIALISED, READY, FIXING }

    private fixBookControlState state;
    private Library library;
    private Book currentBook;


    public FixBookControl() {
      //this should probably be a getInstance() per Jims notes. LIBRARY is not under my control, so request update?
      this.library = library.INSTANCE();
      state = fixBookControlState.INITIALISED;
    }


    public void setUI(FixBookUi newUserInterface) {
      if (!state.equals(fixBookControlState.INITIALISED)) {
        //Should we be throwing exceptions whne they can be avoided?
        throw new RuntimeException("FixBookControl: cannot call setUI except in INITIALISED state");
      }
      this.userInterface = newUserInterface;
      userInterface.setState(FixBookUi.fixBookUserInterfaceState.READY);
      state = fixBookControlState.READY;
    }


    public void bookScanned(int bookId) {
      if (!state.equals(fixBookControlState.READY)) {
        //Should we be throwing exceptions whne they can be avoided?
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
      userInterface.setState(FixBookUi.fixBookUserInterfaceState.FIXING);
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
      userInterface.setState(FixBookUi.fixBookUserInterfaceState.READY);
      state = fixBookControlState.READY;
    }


    public void scanningComplete() {
      if (!state.equals(fixBookControlState.READY)) {
        throw new RuntimeException("FixBookControl: cannot call scanningComplete except in READY state");
      }
      userInterface.setState(FixBookUi.fixBookUserInterfaceState.COMPLETED);
    }
}
