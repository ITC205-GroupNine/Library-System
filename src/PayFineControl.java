public class PayFineControl {
    private PayFineUI userInterface;
    private enum PayFineControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED }
    private PayFineControlState state;
    private library library;
    private member member;


    public PayFineControl() {
        //this should become getInstance per code guidelines
        this.library = library.INSTANCE();
        state = PayFineControlState.INITIALISED;
    }


    public void setUI(PayFineUI userInterface) {
        if (!state.equals(PayFineControlState.INITIALISED)) {
            throw new RuntimeException("PayFineControl: cannot call setUI except in INITIALISED state");
        }
        this.userInterface = userInterface;
        userInterface.setState(PayFineUI.UI_STATE.READY);
        state = PayFineControlState.READY;
    }


    public void cardSwiped(int memberId) {
        if (!state.equals(PayFineControlState.READY)) {
            throw new RuntimeException("PayFineControl: cannot call cardSwiped except in READY state");
        }
        member = library.getMember(memberId);
        if (member == null) {
            userInterface.display("Invalid Member Id");
            return;
        }
        userInterface.display(member.toString());
        userInterface.setState(PayFineUI.UI_STATE.PAYING);
        state = PayFineControlState.PAYING;
    }


    public void cancel() {
        userInterface.setState(PayFineUI.UI_STATE.CANCELLED);
        state = PayFineControlState.CANCELLED;
    }


    public double payFine(double amount) {
        if (!state.equals(PayFineControlState.PAYING)) {
            throw new RuntimeException("PayFineControl: cannot call payFine except in PAYING state");
        }
        double change = member.payFine(amount);
        if (change > 0) {
            userInterface.display(String.format("Change: $%.2f", change));
        }
        userInterface.display(member.toString());
        userInterface.setState(PayFineUI.UI_STATE.COMPLETED);
        state = PayFineControlState.COMPLETED;
        return change;
    }


}
