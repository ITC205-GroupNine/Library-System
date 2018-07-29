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
	
	private static final String LIBRARY_FILE = "Library.obj";
	private static final int LOAN_LIMIT = 2;
	private static final int LOAN_PERIOD = 2;
	private static final double FINE_PER_DAY = 1.0;
	private static final double MAX_FINES_OWED = 5.0;
	private static final double DAMAGE_FEE = 2.0;
	
	private static Library self;
	private int BID;
	private int MID;
	private int LID;
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
		BID = 1;
		MID = 1;		
		LID = 1;		
	}

	
	public static synchronized Library INSTANCE() {
		if (self == null) {
			Path path = Paths.get(LIBRARY_FILE);			
			if (Files.exists(path)) {	
				try (ObjectInputStream lof = new ObjectInputStream(new FileInputStream(LIBRARY_FILE));) {
			    
					self = (Library) lof.readObject();
					Calendar.getInstance().setDate(self.loadDate);
					lof.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else self = new Library();
		}
		return self;
	}

	
	public static synchronized void SAVE() {
		if (self != null) {
			self.loadDate = Calendar.getInstance().Date();
			try (ObjectOutputStream lof = new ObjectOutputStream(new FileOutputStream(LIBRARY_FILE));) {
				lof.writeObject(self);
				lof.flush();
				lof.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	public int BookID() {
		return BID;
	}
	
	
	public int MemberID() {
		return MID;
	}
	
	
	private int nextBID() {
		return BID++;
	}

	
	private int nextMID() {
		return MID++;
	}

	
	private int nextLID() {
		return LID++;
	}

	
	public List<Member> Members() {
		return new ArrayList<Member>(members.values());
	}


	public List<Book> Books() {
		return new ArrayList<Book>(catalog.values());
	}


	public List<Loan> CurrentLoans() {
		return new ArrayList<Loan>(currentLoans.values());
	}


	public Member Add_mem(String lastName, String firstName, String email, int phoneNo) {
		Member Member = new Member(lastName, firstName, email, phoneNo, nextMID());
		members.put(Member.getId(), Member);
		return Member;
	}

	
	public Book Add_book(String a, String t, String c) {
		Book b = new Book(a, t, c, nextBID());
		catalog.put(b.ID(), b);		
		return b;
	}

	
	public Member getMember(int memberId) {
		if (members.containsKey(memberId)) 
			return members.get(memberId);
		return null;
	}

	
	public Book Book(int bookId) {
		if (catalog.containsKey(bookId)) 
			return catalog.get(bookId);		
		return null;
	}

	
	public int loanLimit() {
		return LOAN_LIMIT;
	}

	
	public boolean memberCanBorrow(Member Member) {
		if (Member.getNumberOfCurrentLoans() == LOAN_LIMIT )
			return false;
				
		if (Member.getFinesOwed() >= MAX_FINES_OWED)
			return false;
				
		for (Loan Loan : Member.getLoans())
			if (Loan.isOverDue())
				return false;
			
		return true;
	}

	
	public int loansRemainingForMember(Member Member) {
		return LOAN_LIMIT - Member.getNumberOfCurrentLoans();
	}

	
	public Loan issueLoan(Book book, Member Member) {
		Date dueDate = Calendar.getInstance().getDueDate(LOAN_PERIOD);
		Loan Loan = new Loan(nextLID(), book, Member, dueDate);
		Member.takeOutLoan(Loan);
		book.Borrow();
		loans.put(Loan.getId(), Loan);
		currentLoans.put(book.ID(), Loan);
		return Loan;
	}
	
	
	public Loan getLoanByBookId(int bookId) {
		if (currentLoans.containsKey(bookId)) {
			return currentLoans.get(bookId);
		}
		return null;
	}

	
	public double calculateOverDueFine(Loan Loan) {
		if (Loan.isOverDue()) {
			long daysOverDue = Calendar.getInstance().getDaysDifference(Loan.getDueDate());
			double fine = daysOverDue * FINE_PER_DAY;
			return fine;
		}
		return 0.0;		
	}


	public void dischargeLoan(Loan currentLoan, boolean isDamaged) {
		Member Member = currentLoan.Member();
		Book book  = currentLoan.Book();
		
		double overDueFine = calculateOverDueFine(currentLoan);
		Member.addFine(overDueFine);
		
		Member.dischargeLoan(currentLoan);
		book.Return(isDamaged);
		if (isDamaged) {
			Member.addFine(DAMAGE_FEE);
			damagedBooks.put(book.ID(), book);
		}
		currentLoan.Loan();
		currentLoans.remove(book.ID());
	}


	public void checkCurrentLoans() {
		for (Loan Loan : currentLoans.values()) {
			Loan.checkOverDue();
		}		
	}


	public void repairBook(Book currentBook) {
		if (damagedBooks.containsKey(currentBook.ID())) {
			currentBook.Repair();
			damagedBooks.remove(currentBook.ID());
		}
		else {
			throw new RuntimeException("Library: repairBook: Book is not damaged");
		}
		
	}
	
	
}
