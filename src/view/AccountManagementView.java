package view;

import controller.AccountManagementController;
import model.HDBManager;
import model.User;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * View class for account management operations
 */
public class AccountManagementView {
    private final AccountManagementController accountController;
    private final Scanner scanner;
    private final User currentUser;
    
    // Regular expression for NRIC validation: Starts with S or T, followed by 7 digits, and ends with a letter
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Za-z]$");
    
    /**
     * Constructor for AccountManagementView
     * 
     * @param accountController The account management controller
     * @param currentUser The currently logged-in user
     */
    public AccountManagementView(AccountManagementController accountController, User currentUser) {
        this.accountController = accountController;
        this.currentUser = currentUser;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Displays the account management menu and handles user selections
     * 
     * @return true if the user needs to relogin, false otherwise
     */
    public boolean displayMenu() {
        boolean exit = false;
        boolean reloginRequired = false;
        
        while (!exit) {
            System.out.println("\n===== ACCOUNT MANAGEMENT =====");
            System.out.println("1. Change Password");
            
            // Only show reset password option to HDB Managers
            if (currentUser instanceof HDBManager) {
                System.out.println("2. Reset User Password (Admin)");
            }
            
            System.out.println("0. Back to Dashboard");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    reloginRequired = changePassword();
                    if (reloginRequired) {
                        return true;
                    }
                    break;
                case 2:
                    if (currentUser instanceof HDBManager) {
                        resetUserPassword();
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        return false;
    }
    
    /**
     * Handles the password change operation
     * 
     * @return true if password was changed and user needs to relogin, false otherwise
     */
    private boolean changePassword() {
        System.out.println("\n----- Change Password -----");
        
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        
        if (currentPassword.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return false;
        }
        
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();
        
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return false;
        }
        
        if (newPassword.length() < 8) {
            System.out.println("Password must be at least 8 characters long.");
            return false;
        }
        
        boolean success = accountController.changePassword(currentPassword, newPassword);
        
        if (success) {
            System.out.println("Password changed successfully.");
            System.out.println("For security reasons, you need to log in again with your new password.");
            System.out.println("Press Enter to continue to login screen...");
            scanner.nextLine();
            return true;
        } else {
            System.out.println("Failed to change password. Make sure your current password is correct");
            System.out.println("and the new password meets security requirements:");
            System.out.println("- At least 8 characters");
            System.out.println("- At least one uppercase letter");
            System.out.println("- At least one lowercase letter");
            System.out.println("- At least one digit");
            return false;
        }
    }
    
    /**
     * Handles resetting a user's password (admin function)
     */
    private void resetUserPassword() {
        System.out.println("\n----- Reset User Password (Admin) -----");
        
        String nric = null;
        boolean validNRIC = false;
        
        while (!validNRIC) {
            System.out.print("Enter user's NRIC (format: Sxxxxxxxa or Txxxxxxxa): ");
            nric = scanner.nextLine().trim();
            
            if (isValidNRIC(nric)) {
                validNRIC = true;
            } else {
                System.out.println("Invalid NRIC format. NRIC must start with 'S' or 'T', followed by 7 digits, and end with a letter.");
            }
        }
        
        System.out.print("Enter new password for user: ");
        String newPassword = scanner.nextLine();
        
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }
        
        if (newPassword.length() < 8) {
            System.out.println("Password must be at least 8 characters long.");
            return;
        }
        
        boolean success = accountController.resetPassword(nric, newPassword);
        
        if (success) {
            System.out.println("Password reset successfully for user with NRIC: " + nric);
        } else {
            System.out.println("Failed to reset password. Make sure the user exists and the new password meets security requirements:");
            System.out.println("- At least 8 characters");
            System.out.println("- At least one uppercase letter");
            System.out.println("- At least one lowercase letter");
            System.out.println("- At least one digit");
        }
    }
    
    /**
     * Validates if the given string is a valid NRIC
     * 
     * @param nric The NRIC string to validate
     * @return true if the NRIC is valid, false otherwise
     */
    private boolean isValidNRIC(String nric) {
        return nric != null && NRIC_PATTERN.matcher(nric).matches();
    }
    
    /**
     * Helper method to get integer input from the user
     * 
     * @return The integer input
     */
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
} 