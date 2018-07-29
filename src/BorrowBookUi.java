import java.util.Scanner;


public class BorrowBookUi {

    public enum UiState {INITIALISED, READY, RESTRICTED, SCANNING, FINALISING, COMPLETED, CANCELLED}
    private BorrowBookControl borrowBookControl;
    private Scanner input;
    private UiState uiState;


    public BorrowBookUi(BorrowBookControl control) {
        this.borrowBookControl = control;
        input = new Scanner(System.in);
        uiState = UiState.INITIALISED;
        control.setUI(this);
    }


    private String input(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }


    private void output(Object object) {
        System.out.println(object);
    }


    public void setState(UiState state) {
        this.uiState = state;
    }


    public void run() {
        output("Borrow Book Use Case UI\n");
        while (true) {
            switch (uiState) {
                case CANCELLED:
                    output("Borrowing Cancelled");
                    return;
                case READY:
                    String memberString = input("Swipe member card (press <enter> to cancel): ");
                    if (memberString.length() == 0) {
                        borrowBookControl.cancel();
                        break;
                    }
                    try {
                        int memberId = Integer.valueOf(memberString).intValue();
                        borrowBookControl.swiped(memberId);
                    } catch (NumberFormatException e) {
                        output("Invalid Member Id");
                    }
                    break;
                case RESTRICTED:
                    input("Press <any key> to cancel");
                    borrowBookControl.cancel();
                    break;
                case SCANNING:
                    String bookString = input("Scan Book (<enter> completes): ");
                    if (bookString.length() == 0) {
                        borrowBookControl.complete();
                        break;
                    }
                    try {
                        int bookId = Integer.valueOf(bookString).intValue();
                        borrowBookControl.scanned(bookId);
                    } catch (NumberFormatException e) {
                        output("Invalid Book Id");
                    }
                    break;
                case FINALISING:
                    String answer = input("Commit loans? (Y/N): ");
                    if (answer.toUpperCase().equals("N")) {
                        borrowBookControl.cancel();
                    } else {
                        borrowBookControl.commitLoans();
                        input("Press <any key> to complete ");
                    }
                    break;
                case COMPLETED:
                    output("Borrowing Completed");
                    return;
                default:
                    output("Unhandled state");
                    throw new RuntimeException("BorrowBookUi : unhandled state :" + uiState);
            }
        }
    }


    public void display(Object object) {
        output(object);
    }


}
