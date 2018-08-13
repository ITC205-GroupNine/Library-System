import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")
public class member implements Serializable {


    private String lastName;
    private String firstName;
    private String email;
    private int phoneNumber;
    private int id;
    private double fines;
    private Map<Integer, Loan> loanMap;


    public member(String lastName, String firstName, String email, int phoneNumber, int id) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.loanMap = new HashMap<>();
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("getMember:  ").append(id).append("\n")
          .append("  Name:  ").append(lastName).append(", ").append(firstName).append("\n")
          .append("  Email: ").append(email).append("\n")
          .append("  Phone: ").append(phoneNumber)
          .append("\n")
          .append(String.format("  Fines Owed :  $%.2f", fines))
          .append("\n");
        for (Loan loan : loanMap.values()) {
            sb.append(loan).append("\n");
        }
        return sb.toString();
    }


    public int getId() {
        return id;
    }


    public List<Loan> getLoans() {
        return new ArrayList<Loan>(loanMap.values());
    }


    public int getNumberOfCurrentLoans() {
        return loanMap.size();
    }


    public double getFinesOwed() {
        return fines;
    }


    public void takeOutLoan(Loan loan) {
        if (!loanMap.containsKey(loan.getId())) {
            loanMap.put(loan.getId(), loan);
        } else {
            throw new RuntimeException("Duplicate loan added to member");
        }
    }


    public String getLastName() {
        return lastName;
    }


    public String getFirstName() {
        return firstName;
    }


    public void addFine(double fine) {
        fines += fine;
    }


    public double payFine(double amount) {
        if (amount < 0) {
            throw new RuntimeException("getMember.payFine: amount must be positive");
        }
        double change = 0;
        if (amount > fines) {
            change = amount - fines;
            fines = 0;
        } else {
            fines -= amount;
        }
        return change;
    }


    public void dischargeLoan(Loan loan) {
        if (loanMap.containsKey(loan.getId())) {
            loanMap.remove(loan.getId());
        } else {
            throw new RuntimeException("No such loan held by member");
        }
    }
}
