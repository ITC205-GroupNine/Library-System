//File ready for static review - John Galvin 11330960
import java.util.Scanner;


public class FixBookUi {
    public enum FixBookUserInterfaceState { INITIALISED, READY, FIXING, COMPLETED }
    private FixBookControl fixBookControl;
    private Scanner userInput;
    private FixBookUserInterfaceState state;


    public FixBookUi(FixBookControl control) {
        this.fixBookControl = control;
        userInput = new Scanner(System.in);
        state = FixBookUserInterfaceState.INITIALISED;
        control.setUi(this);
    }


    public void setState(FixBookUserInterfaceState state) {
        this.state = state;
    }


    public void run() {
        output("Fix book Interface\n");
        while (true) {
            switch (state) {
            case READY:
                String bookIdentifier = input("Scan book ( <enter> completes): ");
                if (bookIdentifier.length() == 0) {
                    fixBookControl.scanningComplete();
                } else {
                    try {
                        int bookId = Integer.valueOf(bookIdentifier);
                        fixBookControl.bookScanned(bookId);
                    } catch (NumberFormatException exception) {
                        output("This is not a valid bookId");
                    }
                }
                break;
            case FIXING:
                String fixBookAnswer = input("Would you like to fix book? (Y/N) : ");
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
                throw new RuntimeException("FixBookUi : unhandled state :" + state);
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


    public void display(Object object) {
        output(object);
    }


}
