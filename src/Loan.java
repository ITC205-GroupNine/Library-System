//File ready for static review - John Galvin 11330960
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressWarnings("serial")
public class Loan implements Serializable {
    public enum LoanState { CURRENT, OVER_DUE, DISCHARGED }
    private int loanBookId;
    private Book book;
    private Member memberId;
    private Date loanDate;
    private LoanState state;


    public Loan(int loanId, Book book, Member member, Date dueDate) {
        this.loanBookId = loanId;
        this.book = book;
        this.memberId = member;
        this.loanDate = dueDate;
        this.state = LoanState.CURRENT;
    }


    public void checkOverDue() {
        if (state == LoanState.CURRENT &&
            Calendar.getInstance().date().after(loanDate)) {
            this.state = LoanState.OVER_DUE;
        }
    }


    public boolean isOverDue() {
        return state == LoanState.OVER_DUE;
    }


    public Integer getId() {
        return loanBookId;
    }


    public Date getDueDate() {
        return loanDate;
    }


    public String toString() {
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder loanInfoDisplay = new StringBuilder();
        loanInfoDisplay.append("Loan:  ").append(loanBookId).append("\n")
            .append("  Borrower ").append(memberId.getId()).append(" : ")
            .append(memberId.getLastName()).append(", ").append(memberId.getFirstName()).append("\n")
            .append("  book ").append(book.id()).append(" : " )
            .append(book.title()).append("\n")
            .append("  DueDate: ").append(simpleDate.format(loanDate)).append("\n")
            .append("  State: ").append(state);
        return loanInfoDisplay.toString();
    }


    public Member getMember() {
        return memberId;
    }


    public Book getBook() {
        return book;
    }


    public void getLoan() {
        state = LoanState.DISCHARGED;
    }
}
