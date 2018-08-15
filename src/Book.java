import java.io.Serializable;

//File ready for static review

public class Book implements Serializable {

    private String title;
    private String author;
    private String callNumber;
    private int bookId;
    private enum State {AVAILABLE, ON_LOAN, DAMAGED, RESERVED}
    private State bookState;


    public Book(String author, String title, String callNo, int id) {
        this.author = author;
        this.title = title;
        this.callNumber = callNo;
        this.bookId = id;
        this.bookState = State.AVAILABLE;
    }


    public String toString() {
        return "book: " + bookId + "\n" +
                "  Title:  " + title + "\n" +
                "  Author: " + author + "\n" +
                "  CallNo: " + callNumber + "\n" +
                "  State:  " + bookState;
    }


    Integer id() {
        return bookId;
    }


    String title() {
        return title;
    }


    boolean available() {
        return bookState == State.AVAILABLE;
    }


    boolean onLoan() {
        return bookState == State.ON_LOAN;
    }


    boolean damaged() {
        return bookState == State.DAMAGED;
    }


    void borrow() {
        if (bookState.equals(State.AVAILABLE)) {
            bookState = State.ON_LOAN;
        } else {
            throw new RuntimeException(String.format("book: cannot borrow while book is in bookState: %s", bookState));
        }
    }


    void bookReturn(boolean damaged) {
        if (bookState.equals(State.ON_LOAN)) {
            if (damaged) {
                bookState = State.DAMAGED;
            } else {
                bookState = State.AVAILABLE;
            }
        } else {
            throw new RuntimeException(String.format("book: cannot Return while book is in bookState: %s", bookState));
        }
    }


    void repair() {
        if (bookState.equals(State.DAMAGED)) {
            bookState = State.AVAILABLE;
        } else {
            throw new RuntimeException(String.format("book: cannot repair while book is in bookState: %s", bookState));
        }
    }
}
