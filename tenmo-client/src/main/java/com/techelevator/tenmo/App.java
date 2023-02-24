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
		// TODO Auto-generated method stub
        consoleService.currentBalance();
        System.out.print(accountService.getBalance(currentUser.getUser().getId()));
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Account account = transferService.getAccount(currentUser.getUser().getId());
        Transfer[] transferHistory = transferService.viewTransferHistory(account.getAccount_id());
        consoleService.transferHistory();

        for (Transfer transfer: transferHistory) {
            if (transfer.getAccount_from() == account.getAccount_id()){

                System.out.println(transfer.getTransfer_id()+"            To: "+transfer.getAccount_to()+"            $"+transfer.getAmount());
            } else {
                System.out.println(transfer.getTransfer_id()+"            From: "+ transfer.getAccount_from()+"          $"+transfer.getAmount());
            }
        }
        int id = consoleService.promptForInt("Please enter transfer ID to view details: ");
        Transfer transfer = transferService.viewTransferDetails(id);
        consoleService.transferDetails();
        System.out.println("ID: "+transfer.getTransfer_id());
        System.out.println("From: "+transfer.getAccount_from());
        System.out.println("To: "+transfer.getAccount_to());
        System.out.println("Type: "+transfer.getTransferType(transfer.getTransfer_type_id()));
        System.out.println("Status: "+transfer.getTransferStatus(transfer.getTransfer_status_id()));
        System.out.println("Amount: "+transfer.getAmount());

	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        listUsers();
        System.out.println();
        int user = consoleService.promptForUserInfo();
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        transferService.sendBucks(user, amount);
        System.out.println("You've successfully sent " + amount);

    }

	private void requestBucks() {
		// TODO Auto-generated method stub
		listUsers();
        System.out.println();
        int user = consoleService.promptForUserInfo();
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
	}

    private void listUsers(){
        consoleService.listAccounts();
        User[] userList = accountService.listUsers(currentUser.getUser().getId());
        for (User user : userList) {
            System.out.println(user.getId() + "   " + user.getUsername());
        }
    }

}
