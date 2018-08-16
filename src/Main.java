//File ready for static review - Michael Coleman 11627449
import java.text.SimpleDateFormat;
import java.util.Scanner;


public class Main {


    private static Scanner scannerIn;
    private static Library library;
    private static String menu;
    private static Calendar calendar;
    private static SimpleDateFormat simpleDateFormat;


    private static String getMenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nLibrary Main Menu\n\n")
          .append("  M  : add Member\n")
          .append("  LM : list getMemberList\n")
          .append("\n")
          .append("  B  : add book\n")
          .append("  LB : list books\n")
          .append("  FB : fix books\n")
          .append("\n")
          .append("  L  : take out a loan\n")
          .append("  R  : return a loan\n")
          .append("  LL : list loans\n")
          .append("\n")
          .append("  P  : pay fine\n")
          .append("\n")
          .append("  T  : increment date\n")
          .append("  Q  : quit\n")
          .append("\n")
          .append("Choice : ");
        return sb.toString();
    }


    public static void main(String[] args) {
        try {
            scannerIn = new Scanner(System.in);
            library = library.getInstance();
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (Member m : library.getMemberList()) {
                output(m);
            }
            output(" ");
            for (Book b : library.getBookList()) {
                output(b);
            }
            menu = getMenu();
            boolean e = false;
            while (!e) {
                output("\n" + simpleDateFormat.format(calendar.date()));
                String c = input(menu);
                switch (c.toUpperCase()) {
                case "M":
                    addMember();
                    break;
                case "LM":
                    listMembers();
                    break;
                case "B":
                    addBook();
                    break;
                case "LB":
                    listBooks();
                    break;
                case "FB":
                    fixBooks();
                    break;
                case "L":
                    borrowBook();
                    break;
                case "R":
                    returnBook();
                    break;
                case "LL":
                    listCurrentLoans();
                    break;
                case "P":
                    payFine();
                    break;
                case "T":
                    incrementDate();
                    break;
                case "Q":
                    e = true;
                    break;
                default:
                    output("\nInvalid option\n");
                    break;
                }
                library.save();
            }
        } catch (RuntimeException e) {
            output(e);
        }
        output("\nEnded\n");
    }


    private static void payFine() {
        new PayFineUi(new PayFineControl()).run();
    }


    private static void listCurrentLoans() {
        output("");
        for (Loan loan : library.getCurrentLoansList()) {
            output(loan + "\n");
        }
    }


    private static void listBooks() {
        output("");
        for (Book book : library.getBookList()) {
            output(book + "\n");
        }
    }


    private static void listMembers() {
        output("");
        for (Member member : library.getMemberList()) {
            output(member + "\n");
        }
    }


    private static void borrowBook() {
        new BorrowBookUi(new BorrowBookControl()).run();
    }


    private static void returnBook() {
        new ReturnBookUi(new ReturnBookControl()).run();
    }


    private static void fixBooks() {
        new FixBookUi(new FixBookControl()).run();
    }


    private static void incrementDate() {
        try {
            int days = Integer.valueOf(input("Enter number of days: ")).intValue();
            calendar.incrementDate(days);
            library.checkCurrentLoans();
            output(simpleDateFormat.format(calendar.date()));
        } catch (NumberFormatException e) {
             output("\nInvalid number of days\n");
        }
    }


    private static void addBook() {
        String author = input("Enter author: ");
        String title  = input("Enter title: ");
        String callNo = input("Enter call number: ");
        Book book = library.addBook(author, title, callNo);
        output("\n" + book + "\n");
    }


    private static void addMember() {
        try {
            String lastName = input("Enter last name: ");
            String firstName  = input("Enter first name: ");
            String email = input("Enter email: ");
            int phoneNo = Integer.valueOf(input("Enter phone number: ")).intValue();
            Member member = library.addMember(lastName, firstName, email, phoneNo);
            output("\n" + member + "\n");
        } catch (NumberFormatException e) {
             output("\nInvalid phone number\n");
        }

    }


    private static String input(String prompt) {
        System.out.print(prompt);
        return scannerIn.nextLine();
    }


    private static void output(Object object) {
        System.out.println(object);
    }
}
