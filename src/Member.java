import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Member implements Serializable {

	private String LN;
	private String FN;
	private String EM;
	private int PN;
	private int ID;
	private double FINES;
	
	private Map<Integer, Loan> LNS;

	
	public Member(String lastName, String firstName, String email, int phoneNo, int id) {
		this.LN = lastName;
		this.FN = firstName;
		this.EM = email;
		this.PN = phoneNo;
		this.ID = id;
		
		this.LNS = new HashMap<>();
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Member:  ").append(ID).append("\n")
		  .append("  Name:  ").append(LN).append(", ").append(FN).append("\n")
		  .append("  Email: ").append(EM).append("\n")
		  .append("  Phone: ").append(PN)
		  .append("\n")
		  .append(String.format("  Fines Owed :  $%.2f", FINES))
		  .append("\n");
		
		for (Loan Loan : LNS.values()) {
			sb.append(Loan).append("\n");
		}		  
		return sb.toString();
	}

	
	public int getId() {
		return ID;
	}

	
	public List<Loan> getLoans() {
		return new ArrayList<Loan>(LNS.values());
	}

	
	public int getNumberOfCurrentLoans() {
		return LNS.size();
	}

	
	public double getFinesOwed() {
		return FINES;
	}

	
	public void takeOutLoan(Loan Loan) {
		if (!LNS.containsKey(Loan.getId())) {
			LNS.put(Loan.getId(), Loan);
		}
		else {
			throw new RuntimeException("Duplicate Loan added to Member");
		}		
	}

	
	public String getLastName() {
		return LN;
	}

	
	public String getFirstName() {
		return FN;
	}


	public void addFine(double fine) {
		FINES += fine;
	}
	
	public double payFine(double amount) {
		if (amount < 0) {
			throw new RuntimeException("Member.payFine: amount must be positive");
		}
		double change = 0;
		if (amount > FINES) {
			change = amount - FINES;
			FINES = 0;
		}
		else {
			FINES -= amount;
		}
		return change;
	}


	public void dischargeLoan(Loan Loan) {
		if (LNS.containsKey(Loan.getId())) {
			LNS.remove(Loan.getId());
		}
		else {
			throw new RuntimeException("No such Loan held by Member");
		}		
	}

}
