import java.io.Serializable;

public class Book implements Serializable {

    private String title;
    private String author;
    private String callNo;
    private int bookId;
    private enum State {AVAILABLE, ON_LOAN, DAMAGED, RESERVED}
    private State state;


    public Book(String author, String title, String callNo, int id) {
        this.author = author;
        this.title = title;
        this.callNo = callNo;
        this.bookId = id;
        this.state = State.AVAILABLE;
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Book: ").append(bookId).append("\n")
                .append("  Title:  ").append(title).append("\n")
                .append("  Author: ").append(author).append("\n")
                .append("  CallNo: ").append(callNo).append("\n")
                .append("  State:  ").append(state);
        return stringBuilder.toString();
    }


    Integer id() {
        return bookId;
    }


    String title() {
        return title;
    }


    boolean available() {
        return state == State.AVAILABLE;
    }


    boolean onLoan() {
        return state == State.ON_LOAN;
    }


    boolean damaged() {
        return state == State.DAMAGED;
    }


    void borrow() {
        if (state.equals(State.AVAILABLE)) {
            state = State.ON_LOAN;
        } else {
            throw new RuntimeException(String.format("Book: cannot borrow while book is in state: %s", state));
        }
    }


    void bookReturn(boolean damaged) {
        if (state.equals(State.ON_LOAN)) {
            if (damaged) {
                state = State.DAMAGED;
            } else {
                state = State.AVAILABLE;
            }
        } else {
            throw new RuntimeException(String.format("Book: cannot Return while book is in state: %s", state));
        }
    }


    void repair() {
        if (state.equals(State.DAMAGED)) {
            state = State.AVAILABLE;
        } else {
            throw new RuntimeException(String.format("Book: cannot repair while book is in state: %s", state));
        }
    }
}
