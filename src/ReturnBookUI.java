import java.util.Scanner;


class ReturnBookUi {

    public enum UiState {INITIALISED, READY, INSPECTING, COMPLETED}

    private ReturnBookControl returnBookControl;
    private Scanner input;
    private UiState state;


    ReturnBookUi(ReturnBookControl control) {
        this.returnBookControl = control;
        input = new Scanner(System.in);
        state = UiState.INITIALISED;
        control.setUi(this);
    }


    void run() {
        output("Return book Use Case UI\n");
        while (true) {
            switch (state) {
                case INITIALISED:
                    break;
                case READY:
                    String bookString = input("Scan book (<enter> completes): ");
                    if (bookString.length() == 0) {
                        returnBookControl.scanningComplete();
                    } else {
                        try {
                            int bookId = Integer.valueOf(bookString);
                            returnBookControl.bookScanned(bookId);
                        } catch (NumberFormatException e) {
                            output("Invalid bookId");
                        }
                    }
                    break;
                case INSPECTING:
                    String answer = input("Is book damaged? (Y/N): ");
                    boolean isDamaged = false;
                    if (answer.toUpperCase().equals("Y")) {
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


    void setState(UiState state) {
        this.state = state;
    }
}
