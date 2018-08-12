import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class loan implements Serializable {
    public enum LoanState { CURRENT, OVER_DUE, DISCHARGED }
    private int loanedBookId;
    private Book book;
    private member memberId;
    private Date loanDate;
    private LoanState state;


    public loan(int loanId, Book book, member member, Date dueDate) {
        this.loanedBookId = loanId;
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
        return loanedBookId;
    }


    public Date getDueDate() {
        return loanDate;
    }


    public String toString() {
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder loanInfoDisplay = new StringBuilder();
        loanInfoDisplay.append("Loan:  ").append(loanedBookId).append("\n")
            .append("  Borrower ").append(memberId.getId()).append(" : ")
            .append(memberId.getLastName()).append(", ").append(memberId.getFirstName()).append("\n")
            .append("  Book ").append(book.id()).append(" : " )
            .append(book.title()).append("\n")
            .append("  DueDate: ").append(simpleDate.format(loanDate)).append("\n")
            .append("  State: ").append(state);
        return loanInfoDisplay.toString();
    }

	//this should be getMember
    public member Member() {
        return memberId;
    }

    //This should be getBook
    public Book Book() {
    return book;
    }

	//This should be getLoan
    public void Loan() {
        state = LoanState.DISCHARGED;
    }

}
