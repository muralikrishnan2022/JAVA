Introduction:
This Java program implements a simple banking application that allows users to perform various banking operations such as checking balance, depositing and withdrawing cash, and applying for loans. The application supports two types of accounts: Savings Account and Current Account.

Features:

Account Creation: Users can create either a Savings Account or a Current Account by providing their username, password, and selecting the account type.
Authentication: Users need to provide their username and password to log in to their account.
Account Operations:
Display Balance: Users can view the balance of their account.
Deposit Cash: Users can deposit cash into their account.
Withdraw Cash: Users can withdraw cash from their account.
Apply for Loan: Users can apply for a loan, either small scale loan (for Current Account) or a savings loan (for Savings Account).
Apply for Mortgage: Users can apply for a mortgage, which is available only for Current Account holders.
Data Persistence: Account details and transaction history are saved to files to ensure data persistence between sessions.
Transaction History: The application records transaction history and saves it to a CSV file for easy tracking and auditing.

File Structure:
BankingApp.java: Main program file containing the application logic.
Transaction.java: Defines the Transaction class to record transaction details.
Customer.java: Defines the Customer class to store customer details.
Bank.java: Abstract class defining common functionalities for bank accounts.
SavingsAccount.java: Implements functionalities specific to Savings Accounts.
CurrentAccount.java: Implements functionalities specific to Current Accounts.
Loan.java: Defines the Loan class to handle loan-related operations.
Mortgage.java: Defines the Mortgage class to handle mortgage-related operations.

Usage:
Compile the BankingApp.java file.
Run the compiled file.
Follow the on-screen instructions to perform banking operations.
Dependencies:
This application utilizes Java's built-in libraries and does not require any external dependencies.

Note:

This application is a simplified version for demonstration purposes and may not include all features found in a real-world banking application.
Ensure to handle sensitive customer data securely and comply with relevant data protection regulations.
This README provides an overview of the application. For detailed understanding, refer to the comments within the source code.

