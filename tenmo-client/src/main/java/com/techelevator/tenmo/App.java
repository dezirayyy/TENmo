package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String API_BASE_URL_TRANSFER = "http://localhost:8080/transfer/";

    private static final String API_BASE_URL_ACCOUNT = "http://localhost:8080/account/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private final TransferService transferService = new TransferService(API_BASE_URL_TRANSFER);

    private final AccountService accountService = new AccountService(API_BASE_URL_ACCOUNT);

    private AuthenticatedUser currentUser;


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            accountService.setCurrentUser(currentUser);
            transferService.setCurrentUser(currentUser);
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 6) {
                listUsers();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        consoleService.currentBalance();
        System.out.print(accountService.getAccountByUserId(currentUser.getUser().getId()).getBalance());
	}

    private void viewTransferHistory() {
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId());
        Transfer[] transferHistory = transferService.viewTransferHistory(account.getAccount_id());
        consoleService.transferHistory();
        for (Transfer transfer: transferHistory) {
            String fromOrTo = transfer.getAccount_from() == account.getAccount_id() ? "To: " : "From: ";
            String username = accountService.getUserByAccountId(fromOrTo.equals("To: ") ? transfer.getAccount_to() : transfer.getAccount_from()).getUsername();
            System.out.println(transfer.getTransfer_id() + "            " + fromOrTo + username + "              $" + transfer.getAmount());
        }
        System.out.println();
        int id = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        if (id == 0) {
            System.out.println("Action Cancelled");
        } else {
            Transfer transfer = transferService.viewTransferDetails(id);
            consoleService.transferDetails();
            String transferType = transfer.getTransferTypeString(transfer.getTransfer_type_id());
            String transferStatus = transfer.getTransferStatusString(transfer.getTransfer_status_id());
            System.out.println("ID: " + transfer.getTransfer_id());
            System.out.println("From: " + accountService.getUserByAccountId(transfer.getAccount_from()).getUsername());
            System.out.println("To: " + accountService.getUserByAccountId(transfer.getAccount_to()).getUsername());
            System.out.println("Type: " + transferType);
            System.out.println("Status: " + transferStatus);
            System.out.println("Amount: " + transfer.getAmount());
        }
    }


    private void viewPendingRequests() {
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId());
        Transfer[] transferHistory = transferService.viewPendingTransfer(account.getAccount_id());
        consoleService.pendingTransfer();
        for (Transfer transfer: transferHistory) {
            String fromOrTo = transfer.getAccount_from() == account.getAccount_id() ? "To: " : "From: ";
            String username = accountService.getUserByAccountId(fromOrTo.equals("To: ") ? transfer.getAccount_to() : transfer.getAccount_from()).getUsername();
            System.out.println(transfer.getTransfer_id() + "            " + fromOrTo + username + "              $" + transfer.getAmount());
        }
        System.out.println();
        int id = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
        if (id == 0) {
            System.out.println("Action Cancelled");
        } else {
            consoleService.approveOrRejectTransfer();
            int action = consoleService.promptForInt("Please choose an option: ");
            transferService.approveOrReject(id, action);
            if (action == 2) {
                System.out.println("You've approved the request. Amount has been sent");
            } else {
                System.out.println("You've rejected the request.");
            }
        }
    }

	private void sendBucks() {
        listUsers();
        int user = consoleService.promptForUserSend();
        if (user == 0) {
            System.out.println("Transfer Cancelled");
        } else if (user == currentUser.getUser().getId()) {
            System.out.println("Can't send bucks to yourself");
        } else {
            BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("Can't send negative or zero amount");
            } else {
                if (transferService.sendBucks(user, amount)) {
                    System.out.println("Transfer successful: " + amount + " bucks sent to: " + accountService.getUserByAccountId(accountService.getAccountByUserId(user).getAccount_id()).getUsername());
                } else {
                    System.out.println("Insufficient funds");
                }
            }
        }
    }

	private void requestBucks() {
		listUsers();
        System.out.println();
        int user = consoleService.promptForUserRequest();
        if (user == 0) {
            System.out.println("Transfer Cancelled");
        } else if (user == currentUser.getUser().getId()) {
            System.out.println("Can't request bucks from yourself");
        } else {
            BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("Can't send negative or zero amount");
            } else {
                transferService.requestBucks(user, amount);
                System.out.println("Requested " + amount + " bucks from: " + accountService.getUserByAccountId(accountService.getAccountByUserId(user).getAccount_id()).getUsername());
            }
        }
	}

    private void listUsers(){
        consoleService.listAccounts();
        User[] userList = accountService.listUsers(currentUser.getUser().getId());
        for (User user : userList) {
            System.out.println(user.getId() + "   " + user.getUsername());
        }
    }

}
