package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("6: List users");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public int promptForUserSend(){
        System.out.println("Enter ID of user you are sending to (0 to cancel):");

        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public int promptForUserRequest() {
        System.out.println("Enter ID of user you are requesting to (0 to cancel):");

        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void currentBalance(){
        System.out.print("Your current balance is: " );
    }

    public void listAccounts() {
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println();
        System.out.println("ID      Name");
        System.out.println("-------------------------------------------");
    }

    public void transferHistory(){
        System.out.println("-------------------------------------------");
        System.out.println("Transfers                       ");
        System.out.println("ID              From/To             Amount");
        System.out.println("-------------------------------------------");
    }

    public void transferDetails(){
        System.out.println("-------------------------------------------");
        System.out.println("Transfer details");
        System.out.println("-------------------------------------------");
    }

    public void pendingTransfer(){
        System.out.println("-------------------------------------------");
        System.out.println("Pending Transfers");
        System.out.println("ID                  To             Amount");
        System.out.println("-------------------------------------------");
    }

    public void approveOrRejectTransfer(){
        System.out.println("-------------------------------------------");
        System.out.println("2: Approve");
        System.out.println("3: Reject");
        System.out.println("-------------------------------------------");
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
