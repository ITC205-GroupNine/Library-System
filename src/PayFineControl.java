public class PayFineControl {
    private PayFineUi userInterface;
    private enum PayFineControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED }
    private PayFineControlState state;
    private library library;
    private Member Member;


    public PayFineControl() {
        //this should become getInstance per code guidelines
        this.library = library.INSTANCE();
        state = PayFineControlState.INITIALISED;
    }


    public void setUserInterface(PayFineUi userInterface) {
        if (!state.equals(PayFineControlState.INITIALISED)) {
            throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
        }
        this.userInterface = userInterface;
        userInterface.setState(PayFineUi.PayFineUserInterfaceState.READY);
        state = PayFineControlState.READY;
    }


    public void cardSwiped(int memberId) {
        if (!state.equals(PayFineControlState.READY)) {
            throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
        }
        Member = library.getMember(memberId);
        if (Member == null) {
            userInterface.display("Invalid Member Id");
            return;
        }
        userInterface.display(Member.toString());
        userInterface.setState(PayFineUi.PayFineUserInterfaceState.PAYING);
        state = PayFineControlState.PAYING;
    }


    public void cancel() {
        userInterface.setState(PayFineUi.PayFineUserInterfaceState.CANCELLED);
        state = PayFineControlState.CANCELLED;
    }


    public double payFine(double amount) {
        if (!state.equals(PayFineControlState.PAYING)) {
            throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
        }
        double change = Member.payFine(amount);
        if (change > 0) {
            userInterface.display(String.format("Change: $%.2f", change));
        }
        userInterface.display(Member.toString());
        userInterface.setState(PayFineUi.PayFineUserInterfaceState.COMPLETED);
        state = PayFineControlState.COMPLETED;
        return change;
    }


}
