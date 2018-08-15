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
        return "book: " + bookId + "\n" +
                "  Title:  " + title + "\n" +
                "  Author: " + author + "\n" +
                "  CallNo: " + callNo + "\n" +
                "  State:  " + state;
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
            throw new RuntimeException(String.format("book: cannot borrow while book is in state: %s", state));
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
            throw new RuntimeException(String.format("book: cannot Return while book is in state: %s", state));
        }
    }


    void repair() {
        if (state.equals(State.DAMAGED)) {
            state = State.AVAILABLE;
        } else {
            throw new RuntimeException(String.format("book: cannot repair while book is in state: %s", state));
        }
    }
}
