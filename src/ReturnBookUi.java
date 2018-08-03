import java.util.Scanner;


public class ReturnBookUi {

    public enum uiState {INITIALISED, READY, INSPECTING, COMPLETED}

    private ReturnBookControl returnBookControl;
    private Scanner input;
    private uiState state;


    ReturnBookUi(ReturnBookControl control) {
        this.returnBookControl = control;
        input = new Scanner(System.in);
        state = uiState.INITIALISED;
        control.setUI(this);
    }


    void run() {
        output("Return Book Use Case UI\n");
        while (true) {
            switch (state) {
                case INITIALISED:
                    break;
                case READY:
                    String bookIdString = input("Scan Book (<enter> completes): ");
                    if (bookIdString.length() == 0) {
                        returnBookControl.scanningComplete();
                    } else {
                        try {
                            int bookId = Integer.valueOf(bookIdString).intValue();
                            returnBookControl.bookScanned(bookId);
                        } catch (NumberFormatException e) {
                            output("Invalid bookId");
                        }
                    }
                    break;
                case INSPECTING:
                    String ans = input("Is book damaged? (Y/N): ");
                    boolean isDamaged = false;
                    if (ans.toUpperCase().equals("Y")) {
                        isDamaged = true;
                    }
                    returnBookControl.dischargeLoan(isDamaged);
                case COMPLETED:
                    output("Return processing complete");
                    return;
                default:
                    output("Unhandled state");
                    throw new RuntimeException("ReturnBookUI : unhandled state :" + state);
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


    void display(Object object) {
        output(object);
    }


    public void setState(uiState state) {
        this.state = state;
    }


}
