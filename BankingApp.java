import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;
import java.time.temporal.ChronoUnit;

class Transaction {
    LocalDateTime timestamp;
    String formattedTimestamp;
    double amount;
    String type;

    Transaction(double amount, String type) {
        this.timestamp = LocalDateTime.now();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        this.formattedTimestamp = timestamp.format(formatter);
        this.amount = amount;
        this.type = type;
    }
}

class Customer {
    String name;
    String password;
    int acc_no;
    int acc_type;

    Customer(String name, String password, int acc_no, int acc_type) {
        this.name = name;
        this.password = password;
        this.acc_no = acc_no;
        this.acc_type = acc_type;
    }
}

abstract class Bank {
    String cust_name;
    int acc_no;
    int acc_type;
    double savings_balance = 0;
    double current_balance = 0;
    Transaction[] transactionHistory = new Transaction[100];
    int transactionCount = 0;

    protected String password = "";

    Bank(String password) {
        this.password = password;
    }

    void setPassword(String newPassword) {
        this.password = newPassword;
    }

    private String securePassword() {
        char[] passwordChars = password.toCharArray();
        char[] securePassword = new char[passwordChars.length];
        Arrays.fill(securePassword, '*');
        return new String(securePassword);
    }

    abstract void displayBalance();

    abstract void withdrawalCash(int count);

    abstract void depositCash(int count);

    abstract void applyForLoan();

    abstract void applyForMortgage();

   void recordTransaction(double amount, String type) {
    if (transactionCount < transactionHistory.length) {
        Transaction transaction = new Transaction(amount, type);
        // Initialize the formattedTimestamp here
        transaction.formattedTimestamp = transaction.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        transactionHistory[transactionCount] = transaction;
        transactionCount++;
    } else {
        System.out.println("Transaction history is full. Cannot record more transactions.");
    }
}

    void writeTransactionsToCSV() {
        try {
            String directoryPath = "customer_data";
            File directory = new File(directoryPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String customerFileName = "acc_" + acc_no + "_password" + password.hashCode() + ".csv";
            String transactionHistoryFileName = directoryPath + File.separator + customerFileName;
            File transactionHistoryFile = new File(transactionHistoryFileName);

            if (!transactionHistoryFile.exists()) {
                transactionHistoryFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(transactionHistoryFile, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);

           for (int i = 0; i < transactionCount; i++) {
            Transaction transaction = transactionHistory[i];
            if (transaction != null) {
                String transactionData = transaction.formattedTimestamp + ","
                        + transaction.type + ","
                        + String.format("%.2f", transaction.amount);

                writer.write(transactionData);
                writer.newLine();
            }
        }

            writer.close();
            System.out.println("Transaction history saved to " + transactionHistoryFileName);
        } catch (IOException e) {
            System.out.println("Error writing transaction history to file: " + e.getMessage());
        }
    }

    void readTransactionsFromCSV() {
        try {
            String customerFileName = "acc_" + acc_no + "_password" + password.hashCode() + ".csv";
            String transactionHistoryFileName = "customer_data" + File.separator + customerFileName;

            File transactionHistoryFile = new File(transactionHistoryFileName);

            if (transactionHistoryFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(transactionHistoryFileName));
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    String timestamp = parts[0];
                    String type = parts[1];
                    double amount = Double.parseDouble(parts[2]);

                    Transaction transaction = new Transaction(amount, type);
                    transaction.formattedTimestamp = timestamp;
                    transactionHistory[transactionCount] = transaction;
                    transactionCount++;
                }

                reader.close();
                System.out.println("Transaction history loaded from " + transactionHistoryFileName);
            }
        } catch (IOException e) {
            System.out.println("Error reading transaction history from file: " + e.getMessage());
        }
    }

    abstract boolean loadAccountDetailsFromFile();

    abstract void saveAccountDetailsToFile();
}

class SavingsAccount extends Bank {
    SavingsAccount(String password) {
        super(password);
    }

    @Override
    void displayBalance() {
        System.out.println("Savings Account Balance: ₹" + String.format("%.2f", savings_balance));
    }

    @Override
    void depositCash(int count) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the amount you want to deposit in the savings account: ");
        double deposit = sc.nextDouble();
        double interest, penalty;

        savings_balance += deposit;
        System.out.println("The amount deposited successfully = ₹" + String.format("%.2f", deposit));
        recordTransaction(deposit, "Deposit");

        if (savings_balance >= 1000) {
            if (count % 3 == 0) {
                interest = 0.009 * savings_balance;
                savings_balance += interest;
                System.out.println("The total balance in the savings account is: ₹" + String.format("%.2f", savings_balance));
                System.out.println("The interest for the deposit is: ₹" + String.format("%.2f", interest));
            } else {
                System.out.println("The total balance in the savings account is: ₹" + String.format("%.2f", savings_balance));
            }
        } else if (savings_balance <= 0) {
            System.out.println("INSUFFICIENT BALANCE");
        } else {
            penalty = 0.005 * (1000 - savings_balance);
            savings_balance -= penalty;
            System.out.println("The total balance in the savings account is: ₹" + String.format("%.2f", savings_balance));
            System.out.println("The penalty charged: ₹" + String.format("%.2f", penalty));
        }
    }

    @Override
    void withdrawalCash(int count) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the amount you want to withdraw from the savings account: ");
        double withdrawal = sc.nextDouble();
        double service, penalty;

        if (savings_balance >= withdrawal) {
            if (count % 3 == 0) {
                savings_balance -= withdrawal;
                service = 0.025 * savings_balance;
                savings_balance -= service;
                System.out.println("The amount withdrawn successfully = ₹" + String.format("%.2f", withdrawal));
                System.out.println("The service charge for the transaction is: ₹" + String.format("%.2f", service));
            } else {
                savings_balance -= withdrawal;
                System.out.println("The amount withdrawn successfully = ₹" + String.format("%.2f", withdrawal));
            }
            recordTransaction(withdrawal, "Withdraw");
            displayBalance();
        } else {
            System.out.println("INSUFFICIENT BALANCE TO PERFORM WITHDRAWAL");
        }

        if (savings_balance <= 0) {
            System.out.println("MINIMUM BALANCE NOT MAINTAINED");
        }
    }

    @Override
    void applyForLoan() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the loan amount you want to apply for: ");
        double requestedLoanAmount = sc.nextDouble();

        if (requestedLoanAmount <= 0) {
            System.out.println("Invalid loan amount. Please enter a valid amount.");
        } else if (requestedLoanAmount > 100000) {
            System.out.println("Loan amount exceeds the maximum allowed limit.");
        } else if (requestedLoanAmount > 0.1 * savings_balance) {
            System.out.println("You are not eligible for the requested loan amount.");
        } else {
            Loan loanAccount = new Loan(password);
            loanAccount.acc_no = this.acc_no;
            loanAccount.cust_name = this.cust_name;
            loanAccount.loanAmount = requestedLoanAmount;
            loanAccount.interestRate = 0.08; // Set your specific interest rate
            loanAccount.loanBalance = requestedLoanAmount;
            System.out.println("Loan application approved. Loan amount: ₹" + requestedLoanAmount);
            loanAccount.saveLoanDetailsToFile();
        }
    }

    @Override
    void applyForMortgage() {
        System.out.println("Big scale loans are not available for Savings Accounts.");
    }

    @Override
    boolean loadAccountDetailsFromFile() {
        // Implement loading account details from file
        try {
            String directoryPath = "customer_data";
            String customerFileName = "acc_" + acc_no + "_password" + password.hashCode() + ".txt";
            String accountDetailsFileName = directoryPath + File.separator + customerFileName;
            File accountDetailsFile = new File(accountDetailsFileName);

            if (accountDetailsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(accountDetailsFileName));

                this.savings_balance = Double.parseDouble(reader.readLine());
                this.current_balance = Double.parseDouble(reader.readLine());
                this.transactionCount = Integer.parseInt(reader.readLine());

                reader.close();

                return true;
            }
        } catch (IOException e) {
            System.out.println("Error loading account details from file: " + e.getMessage());
        }
        return false;
    }

    @Override
    void saveAccountDetailsToFile() {
        // Implement saving account details to file
        try {
            String directoryPath = "customer_data";
            File directory = new File(directoryPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String customerFileName = "acc_" + acc_no + "_password" + password.hashCode() + ".txt";
            String accountDetailsFileName = directoryPath + File.separator + customerFileName;
            File accountDetailsFile = new File(accountDetailsFileName);

            if (!accountDetailsFile.exists()) {
                accountDetailsFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(accountDetailsFile, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(String.format("%.2f", savings_balance));
            writer.newLine();
            writer.write(String.format("%.2f", current_balance));
            writer.newLine();
            writer.write(Integer.toString(transactionCount));
            writer.newLine();

            writer.close();
            System.out.println("Account details saved to " + accountDetailsFileName);
        } catch (IOException e) {
            System.out.println("Error writing account details to file: " + e.getMessage());
        }
    }
}

class CurrentAccount extends Bank {
    CurrentAccount(String password) {
        super(password);
    }

    @Override
    void displayBalance() {
        System.out.println("Current Account Balance: ₹" + String.format("%.2f", current_balance));
    }

    @Override
    void depositCash(int count) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the amount you want to deposit in the current account: ");
        double deposit = sc.nextDouble();
        double interest, penalty;

        current_balance += deposit;
        System.out.println("The amount deposited successfully = ₹" + String.format("%.2f", deposit));

        if (current_balance >= 5000) {
            if (count % 3 == 0) {
                interest = 0.009 * current_balance;
                current_balance += interest;
                System.out.println("The total balance in the current account is: ₹" + String.format("%.2f", current_balance));
                System.out.println("The interest for the deposit is: ₹" + String.format("%.2f", interest));
            } else {
                System.out.println("The total balance in the current account is: ₹" + String.format("%.2f", current_balance));
            }
        } else {
            penalty = 0.005 * (5000 - current_balance);
            current_balance -= penalty;
            System.out.println("The total balance in the current account is: ₹" + String.format("%.2f", current_balance));
            System.out.println("The penalty charged: ₹" + String.format("%.2f", penalty));
        }
    }

    @Override
    void withdrawalCash(int count) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the amount you want to withdraw from the current account: ");
        double withdrawal = sc.nextDouble();
        double service, penalty;

        if (current_balance >= withdrawal) {
            if (count % 3 == 0) {
                current_balance -= withdrawal;
                service = 0.025 * current_balance;
                current_balance -= service;
                System.out.println("The amount withdrawn successfully = ₹" + String.format("%.2f", withdrawal));
                System.out.println("The service charge for the transaction is: ₹" + String.format("%.2f", service));
            } else {
                current_balance -= withdrawal;
                System.out.println("The amount withdrawn successfully = ₹" + String.format("%.2f", withdrawal));
            }
            displayBalance();
        } else {
            System.out.println("INSUFFICIENT BALANCE TO PERFORM WITHDRAWAL");
        }

        if (current_balance <= 0) {
            System.out.println("MINIMUM BALANCE NOT MAINTAINED");
        }
    }

    @Override
    void applyForLoan() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the Small Scale loan amount you want to apply for: ");
        double requestedLoanAmount = sc.nextDouble();

        if (requestedLoanAmount <= 0) {
            System.out.println("Invalid loan amount. Please enter a valid amount.");
        } else if (requestedLoanAmount > 200000) {
            System.out.println("Loan amount exceeds the maximum allowed limit.");
        } else if (requestedLoanAmount > 0.2 * current_balance) {
            System.out.println("You are not eligible for the requested  loan amount.");
        } else {
            Loan loanAccount = new Loan(password);
            loanAccount.acc_no = this.acc_no;
            loanAccount.cust_name = this.cust_name;
            loanAccount.loanAmount = requestedLoanAmount;
            loanAccount.interestRate = 0.07; // Set your specific interest rate
            loanAccount.loanBalance = requestedLoanAmount;
            System.out.println(" Small Scale Loan application approved. Loan amount: ₹" + requestedLoanAmount);
            loanAccount.saveLoanDetailsToFile();
        }
    }

    @Override
    void applyForMortgage() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the Big scale loan amount you want to apply for: ");
        double requestedMortgageAmount = sc.nextDouble();

        if (requestedMortgageAmount <= 0) {
            System.out.println("Invalid Big scale loan amount. Please enter a valid amount.");
        } else if (requestedMortgageAmount > 500000) {
            System.out.println("Big scale loan amount exceeds the maximum allowed limit.");
        } else {
            Mortgage mortgageAccount = new Mortgage(password);
            mortgageAccount.acc_no = this.acc_no;
            mortgageAccount.cust_name = this.cust_name;
            mortgageAccount.mortgageAmount = requestedMortgageAmount;
            mortgageAccount.interestRate = 0.05; // Set your specific interest rate
            mortgageAccount.mortgageBalance = requestedMortgageAmount;
            System.out.println("Big scale loan application approved. The requested amount: ₹" + requestedMortgageAmount);
            mortgageAccount.saveMortgageDetailsToFile();
        }
    }

    @Override
    boolean loadAccountDetailsFromFile() {
        // Implement loading account details from file
        try {
            String directoryPath = "customer_data";
            String customerFileName = "acc_" + acc_no + "_password" + password.hashCode() + ".txt";
            String accountDetailsFileName = directoryPath + File.separator + customerFileName;
            File accountDetailsFile = new File(accountDetailsFileName);

            if (accountDetailsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(accountDetailsFileName));

                this.savings_balance = Double.parseDouble(reader.readLine());
                this.current_balance = Double.parseDouble(reader.readLine());
                this.transactionCount = Integer.parseInt(reader.readLine());

                reader.close();

                return true;
            }
        } catch (IOException e) {
            System.out.println("Error loading account details from file: " + e.getMessage());
        }
        return false;
    }

    @Override
    void saveAccountDetailsToFile() {
        // Implement saving account details to file
        try {
            String directoryPath = "customer_data";
            File directory = new File(directoryPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String customerFileName = "acc_" + acc_no + "_password" + password.hashCode() + ".txt";
            String accountDetailsFileName = directoryPath + File.separator + customerFileName;
            File accountDetailsFile = new File(accountDetailsFileName);

            if (!accountDetailsFile.exists()) {
                accountDetailsFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(accountDetailsFile, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(String.format("%.2f", savings_balance));
            writer.newLine();
            writer.write(String.format("%.2f", current_balance));
            writer.newLine();
            writer.write(Integer.toString(transactionCount));
            writer.newLine();

            writer.close();
            System.out.println("Account details saved to " + accountDetailsFileName);
        } catch (IOException e) {
            System.out.println("Error writing account details to file: " + e.getMessage());
        }
    }
}

class Loan {
    String cust_name;
    int acc_no;
    double loanAmount;
    double interestRate;
    double loanBalance;

    private String password = "";

    Loan(String password) {
        this.password = password;
    }

    void saveLoanDetailsToFile() {
        try {
            String directoryPath = "customer_data";
            String customerFileName = "acc_" + acc_no + "_password" + password.hashCode() + "_loan.txt";
            String loanDetailsFileName = directoryPath + File.separator + customerFileName;
            File loanDetailsFile = new File(loanDetailsFileName);

            if (!loanDetailsFile.exists()) {
                loanDetailsFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(loanDetailsFile, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(String.format("%.2f", loanAmount));
            writer.newLine();
            writer.write(String.format("%.2f", interestRate));
            writer.newLine();
            writer.write(String.format("%.2f", loanBalance));
            writer.newLine();

            writer.close();
            System.out.println("Loan details saved to " + loanDetailsFileName);
        } catch (IOException e) {
            System.out.println("Error writing loan details to file: " + e.getMessage());
        }
    }
}

class Mortgage {
    String cust_name;
    int acc_no;
    double mortgageAmount;
    double interestRate;
    double mortgageBalance;

    private String password = "";

    Mortgage(String password) {
        this.password = password;
    }

    void saveMortgageDetailsToFile() {
        try {
            String directoryPath = "customer_data";
            String customerFileName = "acc_" + acc_no + "_password" + password.hashCode() + "_mortgage.txt";
            String mortgageDetailsFileName = directoryPath + File.separator + customerFileName;
            File mortgageDetailsFile = new File(mortgageDetailsFileName);

            if (!mortgageDetailsFile.exists()) {
                mortgageDetailsFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(mortgageDetailsFile, false);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(String.format("%.2f", mortgageAmount));
            writer.newLine();
            writer.write(String.format("%.2f", interestRate));
            writer.newLine();
            writer.write(String.format("%.2f", mortgageBalance));
            writer.newLine();

            writer.close();
            System.out.println("Big scale loan details saved to " + mortgageDetailsFileName);
        } catch (IOException e) {
            System.out.println("Error writing Big scale loan details to file: " + e.getMessage());
        }
    }
}

public class BankingApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String name = sc.nextLine();

        System.out.println("Enter the password: ");
        String password = sc.nextLine();

        System.out.println("Enter your account type (1 for Savings, 2 for Current): ");
        int acc_type = sc.nextInt();

        Bank account = null;

        if (acc_type == 1) {
            account = new SavingsAccount(password);
        } else if (acc_type == 2) {
            account = new CurrentAccount(password);
        } else {
            System.out.println("Invalid account type.");
            return;
        }

        account.cust_name = name;
        account.acc_type = acc_type;
        account.acc_no = account.cust_name.hashCode();

        // Load account details from a file, if available
        if (account.loadAccountDetailsFromFile()) {
            System.out.println("Account details loaded successfully.");
        } else {
            System.out.println("New account created successfully.");
        }

        int count = 0;
         LocalDateTime loginTime = LocalDateTime.now();
        String loginTimePattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter loginTimeFormatter = DateTimeFormatter.ofPattern(loginTimePattern);
        String formattedLoginTime = loginTime.format(loginTimeFormatter);
        System.out.println("Logged in at: " + formattedLoginTime);

        while (true) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Display Balance");
            System.out.println("2. Deposit Cash");
            System.out.println("3. Withdraw Cash");
            System.out.println("4. Apply for  Small scale Loan");
            System.out.println("5. Apply for Big scale loan");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    account.displayBalance();
                    break;
                case 2:
                    account.depositCash(count);
                    break;
                case 3:
                    account.withdrawalCash(count);
                    break;
                case 4:
                    account.applyForLoan();
                    break;
                case 5:
                    account.applyForMortgage();
                    break;
                case 6:
                    // Save account details and transaction history to files before exiting
                    account.writeTransactionsToCSV();
                    account.saveAccountDetailsToFile();
                    System.out.println("Exiting the application. Account details saved successfully.");
                    // Adding a logout time record
                    LocalDateTime logoutTime = LocalDateTime.now();
                    String logoutTimePattern = "yyyy-MM-dd HH:mm:ss";
                    DateTimeFormatter logoutTimeFormatter = DateTimeFormatter.ofPattern(logoutTimePattern);
                    String formattedLogoutTime = logoutTime.format(logoutTimeFormatter);
                    System.out.println("Logged out at: " + formattedLogoutTime);

                    // Calculate and display time elapsed
                    long minutesElapsed = loginTime.until(logoutTime, ChronoUnit.MINUTES);
                    System.out.println("Time elapsed: " + minutesElapsed + " minutes");
                    return;
                default:
                    System.out.println("Invalid choice. Please choose a valid option.");
                    break;
            }

            count++;
        }
    }
}