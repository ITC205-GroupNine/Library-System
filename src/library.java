import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")
public class library implements Serializable {


    private static final String libraryFile = "library.obj";
    private static final int loanLimit = 2;
    private static final int loanPeriod = 2;
    private static final double finePerDay = 1.0;
    private static final double maxFineOwed = 5.0;
    private static final double damageFee = 2.0;
    private static library library;
    private int bookId;
    private int memberId;
    private int libraryId;
    private Date loadDate;
    private Map<Integer, Book> catalog;
    private Map<Integer, member> members;
    private Map<Integer, loan> loans;
    private Map<Integer, loan> currentLoans;
    private Map<Integer, Book> damagedBooks;


    private library() {
        catalog = new HashMap<>();
        members = new HashMap<>();
        loans = new HashMap<>();
        currentLoans = new HashMap<>();
        damagedBooks = new HashMap<>();
        bookId = 1;
        memberId = 1;
        libraryId = 1;
    }


    public static synchronized library INSTANCE() {
        if (library == null) {
            Path path = Paths.get(libraryFile);
            if (Files.exists(path)) {
                try (ObjectInputStream lof = new ObjectInputStream(new FileInputStream(libraryFile))) {
                    library = (library) lof.readObject();
                    Calendar.getInstance().setDate(library.loadDate);
                    lof.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else library = new library();
        }
        return library;
    }


    public static synchronized void SAVE() {
        if (library != null) {
            library.loadDate = Calendar.getInstance().date();
            try (ObjectOutputStream libraryFileOutputStream = new ObjectOutputStream(new FileOutputStream(libraryFile))) {
                libraryFileOutputStream.writeObject(library);
                libraryFileOutputStream.flush();
                libraryFileOutputStream.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public int getBookId() {
        return bookId;
    }


    public int getMemberID() {
        return memberId;
    }


    private int nextBookId() {
        return bookId++;
    }


    private int nextMemberId() {
        return memberId++;
    }


    private int nextLibraryId() {
        return libraryId++;
    }


    public List<member> getMemberList() {
        return new ArrayList<member>(members.values());
    }


    public List<Book> getBookList() {
        return new ArrayList<Book>(catalog.values());
    }


    public List<loan> getCurrentLoansList() {
        return new ArrayList<loan>(currentLoans.values());
    }


    public member addMember(String lastName, String firstName, String email, int phoneNo) {
        member member = new member(lastName, firstName, email, phoneNo, nextMemberId());
        members.put(member.getId(), member);
        return member;
    }


    public Book addBook(String a, String t, String c) {
        Book b = new Book(a, t, c, nextBookId());
        catalog.put(b.id(), b);
        return b;
    }


    public member getMember(int memberId) {
        if (members.containsKey(memberId)) {
            return members.get(memberId);
        }
        return null;
    }


    public Book book(int bookId) {
        if (catalog.containsKey(bookId)) {
            return catalog.get(bookId);
        }
        return null;
    }


    public int getLoanLimit() {
        return loanLimit;
    }


    public boolean memberCanBorrow(member member) {
        if (member.getNumberOfCurrentLoans() == loanLimit) {
            return false;
        }
        if (member.getFinesOwed() >= maxFineOwed) {
            return false;
        }
        for (loan loan : member.getLoans()) {
            if (loan.isOverDue()) {
                return false;
            }
        }
        return true;
    }


    public int loansRemainingForMember(member member) {
        return loanLimit - member.getNumberOfCurrentLoans();
    }


    public loan issueLoan(Book book, member member) {
        Date dueDate = Calendar.getInstance().getDueDate(loanPeriod);
        loan loan = new loan(nextLibraryId(), book, member, dueDate);
        member.takeOutLoan(loan);
        book.borrow();
        loans.put(loan.getId(), loan);
        currentLoans.put(book.id(), loan);
        return loan;
    }


    public loan getLoanByBookId(int bookId) {
        if (currentLoans.containsKey(bookId)) {
            return currentLoans.get(bookId);
        }
        return null;
    }


    public double calculateOverDueFine(loan loan) {
        if (loan.isOverDue()) {
            long daysOverDue = Calendar.getInstance().getDaysDifference(loan.getDueDate());
            double fine = daysOverDue * finePerDay;
            return fine;
        }
        return 0.0;
    }


    public void dischargeLoan(loan currentLoan, boolean isDamaged) {
        member member = currentLoan.Member();
        Book book  = currentLoan.Book();
        double overDueFine = calculateOverDueFine(currentLoan);
        member.addFine(overDueFine);
        member.dischargeLoan(currentLoan);
        book.bookReturn(isDamaged);
        if (isDamaged) {
            member.addFine(damageFee);
            damagedBooks.put(book.id(), book);
        }
        currentLoan.Loan();
        currentLoans.remove(book.id());
    }


    public void checkCurrentLoans() {
        for (loan loan : currentLoans.values()) {
            loan.checkOverDue();
        }
    }


    public void repairBook(Book currentBook) {
        if (damagedBooks.containsKey(currentBook.id())) {
            currentBook.repair();
            damagedBooks.remove(currentBook.id());
        } else {
            throw new RuntimeException("Library: repairBook: book is not damaged");
        }
    }
}
