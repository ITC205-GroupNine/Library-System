import java.util.Scanner;


public class FixBookUI {

    public enum fixBookUserInterfaceState { INITIALISED, READY, FIXING, COMPLETED }

    private FixBookControl fixBookControl;
    private Scanner input;
    private fixBookUserInterfaceState state;


    public FixBookUI(FixBookControl control) {
        this.fixBookControl = control;
        input = new Scanner(System.in);
        state = fixBookUserInterfaceState.INITIALISED;
        control.setUI(this);
    }


    public void setState(fixBookUserInterfaceState state) {
        this.state = state;
    }


    public void run() {
        output("Fix Book Use Case UI\n");

        while (true) {
            switch (state) {
              case READY:
                String bookStr = input("Scan Book (<enter> completes): ");
                if (bookStr.length() == 0) {
                    fixBookControl.scanningComplete();
                }
                else {
                    try {
                        int bookId = Integer.valueOf(bookStr).intValue();
                    fixBookControl.bookScanned(bookId);
                    }
                    catch (NumberFormatException e) {
                        output("Invalid bookId");
                    }
                }
                break;

              case FIXING:
                String ans = input("Fix Book? (Y/N) : ");
                boolean fix = false;
                if (ans.toUpperCase().equals("Y")) {
                    fix = true;
                }
                fixBookControl.fixBook(fix);
                break;

              case COMPLETED:
                output("Fixing process complete");
                return;

              default:
                output("Unhandled state");
                throw new RuntimeException("FixBookUI : unhandled state :" + state);
            }
        }
    }

	
    private String input(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }


    private void output(Object object) {
        System.out.println(object);
    }


    public void display(Object object) {
        output(object);
    }


}
