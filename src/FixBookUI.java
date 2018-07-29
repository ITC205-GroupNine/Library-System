import java.util.Scanner;


public class FixBookUI {

    public enum fixBookUserInterfaceState { INITIALISED, READY, FIXING, COMPLETED }

    private FixBookControl fixBookControl;
    private Scanner userInput;
    private fixBookUserInterfaceState state;


    public FixBookUI(FixBookControl control) {
      this.fixBookControl = control;
      userInput = new Scanner(System.in);
      state = fixBookUserInterfaceState.INITIALISED;
      control.setUI(this);
    }


    public void setState(fixBookUserInterfaceState state) {
        this.state = state;
    }


    public void run() {
      output("Fix Book Interface\n");
      while (true) {
        switch (state) {
          case READY:
            String bookIdentifier = input("Scan Book ( <enter> completes): ");
            if (bookIdentifier.length() == 0) {
              fixBookControl.scanningComplete();
            }
            else {
              try {
                int bookId = Integer.valueOf(bookIdentifier);
                fixBookControl.bookScanned(bookId);
              }
              catch (NumberFormatException exception) {
                output("This is not a valid bookId");
              }
            }
            break;

          case FIXING:
            String fixBookAnswer = input("Would you like to fix Book? (Y/N) : ");
            boolean fix = false;
            if (fixBookAnswer.toUpperCase().equals("Y")) {
              fix = true;
            }
            fixBookControl.fixBook(fix);
            break;

          case COMPLETED:
            output("The book/s have been fixed.");
            return;

          default:
            output("Unhandled state");
            throw new RuntimeException("FixBookUI : unhandled state :" + state);
        }
      }
    }

	
    private String input(String prompt) {
      System.out.print(prompt);
      return userInput.nextLine();
    }


    private void output(Object object) {
      System.out.println(object);
    }

    //Is this necessary or should I remove it?
    public void display(Object object) {
        output(object);
    }


}
