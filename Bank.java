import java.util.*;
import java.text.*;

// Interface defining basic savings account actions
interface SavingsAccount {
    final double INTEREST_RATE = 0.04;  // Interest rate for accounts with balance >= 10,000
    final double MIN_BALANCE = 200;     // Minimum balance to keep the account active
    void deposit(double amount, Date date);  // Method to deposit money
    void withdraw(double amount, Date date); // Method to withdraw money
}



// Bank class to manage customers and operations
class Bank {
    Map<String, Customer> customerMap;  // Stores customer data

    // Constructor initializing the Bank
    Bank() {
        customerMap = new HashMap<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Bank bank = new Bank();
        int choice;

        // Main loop for banking operations
        while (true) {
            System.out.println("\n-------------------");
            System.out.println("BANK OF JAVA");
            System.out.println("-------------------\n");
            System.out.println("1. Register account.");
            System.out.println("2. Login.");
            System.out.println("3. Update all accounts.");
            System.out.println("4. Exit.");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    // Register a new account
                    System.out.print("Enter your name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter your address: ");
                    String address = sc.nextLine();
                    System.out.print("Enter your phone number: ");
                    String phone = sc.nextLine();
                    System.out.print("Enter your email: ");
                    String email = sc.nextLine();

                    // Set up username and password
                    String username;
                    do {
                        System.out.print("Set username: ");
                        username = sc.nextLine();
                    } while (bank.customerMap.containsKey(username));

                    String password;
                    do {
                        System.out.print("Set a strong password (8+ chars, 1 digit, 1 lower, 1 upper, 1 special): ");
                        password = sc.nextLine();
                    } while (!password.matches(("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_]).{8,}")));

                    // Initial deposit
                    System.out.print("Enter initial deposit amount: ");
                    double initialDeposit = sc.nextDouble();
                    sc.nextLine();

                    // Create a new customer
                    Customer customer = new Customer(username, password, name, address, phone, email, initialDeposit, new Date());
                    bank.customerMap.put(username, customer);
                    System.out.println("Account registered successfully.");
                    break;

                case 2:
                    // Login to an existing account
                    System.out.print("Enter username: ");
                    String loginUsername = sc.nextLine();
                    System.out.print("Enter password: ");
                    String loginPassword = sc.nextLine();

                    if (bank.customerMap.containsKey(loginUsername) && bank.customerMap.get(loginUsername).password.equals(loginPassword)) {
                        Customer loggedInCustomer = bank.customerMap.get(loginUsername);
                        int userChoice;
                        do {
                            System.out.println("\n1. Deposit");
                            System.out.println("2. Withdraw");
                            System.out.println("3. View last 5 transactions");
                            System.out.println("4. Check balance");
                            System.out.println("5. Logout");
                            System.out.print("Enter your choice: ");
                            userChoice = sc.nextInt();
                            sc.nextLine();

                            switch (userChoice) {
                                case 1:
                                    System.out.print("Enter deposit amount: ");
                                    double depositAmount = sc.nextDouble();
                                    sc.nextLine();
                                    loggedInCustomer.deposit(depositAmount, new Date());
                                    break;

                                case 2:
                                    System.out.print("Enter withdrawal amount: ");
                                    double withdrawAmount = sc.nextDouble();
                                    sc.nextLine();
                                    loggedInCustomer.withdraw(withdrawAmount, new Date());
                                    break;

                                case 3:
                                    for (String transaction : loggedInCustomer.transactions) {
                                        System.out.println(transaction);
                                    }
                                    break;

                                case 4:
                                    System.out.println("Current balance: " + NumberFormat.getCurrencyInstance().format(loggedInCustomer.getBalance()));
                                    break;

                                case 5:
                                    System.out.println("Logged out.");
                                    break;

                                default:
                                    System.out.println("Invalid choice.");
                            }
                        } while (userChoice != 5);
                    } else {
                        System.out.println("Invalid username/password.");
                    }
                    break;

                case 3:
                    // Update all accounts (simulate interest and fees)
                    for (Customer customerToUpdate : bank.customerMap.values()) {
                        customerToUpdate.updateAccount(new Date());
                    }
                    System.out.println("All accounts updated.");
                    break;

                case 4:
                    // Exit the program
                    System.out.println("Thank you for using Bank Of Java.");
                    sc.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}

// Customer class implementing SavingsAccount interface
class Customer implements SavingsAccount {
    String username, password, name, address, phone, email;
    double balance;
    ArrayList<String> transactions;  // Store last 5 transactions

    // Constructor initializing a new customer
    Customer(String username, String password, String name, String address, String phone, String email, double balance, Date date) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.balance = balance;
        transactions = new ArrayList<>(5);

        // Add initial deposit to transactions
        addTransaction("Initial deposit - " + NumberFormat.getCurrencyInstance().format(balance) + " as on " + String.format("%1$tD", date));
    }

    // Update the account by applying interest or fees
    void updateAccount(Date date) {
        if (balance >= 10000) {
            balance += INTEREST_RATE * balance; // Add interest if balance >= 10000
        } else {
            balance -= (int)(balance / 100.0);  // Charge maintenance fee
        }
        addTransaction("Account updated. Balance - " + NumberFormat.getCurrencyInstance().format(balance) + " as on " + String.format("%1$tD", date));
    }

    // Deposit method implementation
    @Override
    public void deposit(double amount, Date date) {
        balance += amount;  // Increase balance
        addTransaction(NumberFormat.getCurrencyInstance().format(amount) + " credited. Balance: " + NumberFormat.getCurrencyInstance().format(balance) + " as on " + String.format("%1$tD", date));
    }

    // Withdraw method implementation
    @Override
    public void withdraw(double amount, Date date) {
        if (amount > (balance - MIN_BALANCE)) {
            System.out.println("Insufficient balance.");
            return;
        }
        balance -= amount;  // Deduct balance
        addTransaction(NumberFormat.getCurrencyInstance().format(amount) + " debited. Balance: " + NumberFormat.getCurrencyInstance().format(balance) + " as on " + String.format("%1$tD", date));
    }

    // Method to add a transaction to the list
    private void addTransaction(String message) {
        transactions.add(0, message);  // Add the latest transaction at the start
        if (transactions.size() > 5) {
            transactions.remove(5);  // Maintain only the last 5 transactions
        }
    }

    // Method to check the current balance
    public double getBalance() {
        return balance;
    }
}