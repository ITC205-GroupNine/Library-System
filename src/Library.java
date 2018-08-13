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
public class Library implements Serializable {


    private static final String libraryFile = "Library.obj";
    private static final int loanLimit = 2;
    private static final int loanPeriod = 2;
    private static final double finePerDay = 1.0;
    private static final double maxFineOwed = 5.0;
    private static final double damageFee = 2.0;
    private static Library library;
    private int bookId;
    private int memberId;
    private int libraryId;
    private Date loadDate;
    private Map<Integer, Book> catalog;
    private Map<Integer, Member> members;
    private Map<Integer, Loan> loans;
    private Map<Integer, Loan> currentLoans;
    private Map<Integer, Book> damagedBooks;


    private Library() {
        catalog = new HashMap<>();
        members = new HashMap<>();
        loans = new HashMap<>();
        currentLoans = new HashMap<>();
        damagedBooks = new HashMap<>();
        bookId = 1;
        memberId = 1;
        libraryId = 1;
    }


    public static synchronized Library getInstance() {
        if (library == null) {
            Path path = Paths.get(libraryFile);
            if (Files.exists(path)) {
                try (ObjectInputStream libraryFileOutputStream = new ObjectInputStream(new FileInputStream(libraryFile))) {
                    library = (Library) libraryFileOutputStream.readObject();
                    Calendar.getInstance().setDate(library.loadDate);
                    libraryFileOutputStream.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else library = new Library();
        }
        return library;
    }


    public static synchronized void save() {
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


    public List<Member> getMemberList() {
        return new ArrayList<Member>(members.values());
    }


    public List<Book> getBookList() {
        return new ArrayList<Book>(catalog.values());
    }


    public List<Loan> getCurrentLoansList() {
        return new ArrayList<Loan>(currentLoans.values());
    }


    public Member addMember(String lastName, String firstName, String email, int phoneNo) {
        Member member = new Member(lastName, firstName, email, phoneNo, nextMemberId());
        members.put(member.getId(), member);
        return member;
    }


    public Book addBook(String a, String t, String c) {
        Book b = new Book(a, t, c, nextBookId());
        catalog.put(b.id(), b);
        return b;
    }


    public Member getMember(int memberId) {
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


    public boolean memberCanBorrow(Member member) {
        if (member.getNumberOfCurrentLoans() == loanLimit) {
            return false;
        }
        if (member.getFinesOwed() >= maxFineOwed) {
            return false;
        }
        for (Loan loan : member.getLoans()) {
            if (loan.isOverDue()) {
                return false;
            }
        }
        return true;
    }


    public int loansRemainingForMember(Member member) {
        return loanLimit - member.getNumberOfCurrentLoans();
    }


    public Loan issueLoan(Book book, Member member) {
        Date dueDate = Calendar.getInstance().getDueDate(loanPeriod);
        Loan loan = new Loan(nextLibraryId(), book, member, dueDate);
        member.takeOutLoan(loan);
        book.borrow();
        loans.put(loan.getId(), loan);
        currentLoans.put(book.id(), loan);
        return loan;
    }


    public Loan getLoanByBookId(int bookId) {
        if (currentLoans.containsKey(bookId)) {
            return currentLoans.get(bookId);
        }
        return null;
    }


    public double calculateOverDueFine(Loan loan) {
        if (loan.isOverDue()) {
            long daysOverDue = Calendar.getInstance().getDaysDifference(loan.getDueDate());
            double fine = daysOverDue * finePerDay;
            return fine;
        }
        return 0.0;
    }


    public void dischargeLoan(Loan currentLoan, boolean isDamaged) {
        Member member = currentLoan.getMember();
        Book book  = currentLoan.getBook();
        double overDueFine = calculateOverDueFine(currentLoan);
        member.addFine(overDueFine);
        member.dischargeLoan(currentLoan);
        book.bookReturn(isDamaged);
        if (isDamaged) {
            member.addFine(damageFee);
            damagedBooks.put(book.id(), book);
        }
        currentLoan.getLoan();
        currentLoans.remove(book.id());
    }


    public void checkCurrentLoans() {
        for (Loan loan : currentLoans.values()) {
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
