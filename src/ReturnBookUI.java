import java.util.Scanner;


class ReturnBookUI {
    
    public enum UI_STATE {INITIALISED, READY, INSPECTING, COMPLETED}
    
    private ReturnBookControl returnBookControl;
    private Scanner userInput;
    private UI_STATE uiState;
    
    
    ReturnBookUI(ReturnBookControl returnBookControl) {
        this.returnBookControl = returnBookControl;
        userInput = new Scanner(System.in);
        uiState = UI_STATE.INITIALISED;
        returnBookControl.setUI(this);
    }
    
    
    void run() {
        output("Return Book Use Case UI\n");
        
        while (true) {
            
            switch (uiState) {
                
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
                    output("Unhandled uiState");
                    throw new RuntimeException("ReturnBookUI : unhandled uiState :" + uiState);
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
    
    
    void display(Object object) {
        output(object);
    }
    
    
    void setState(UI_STATE uiState) {
        this.uiState = uiState;
    }
}
