package view;

import controller.AuthenticationController;
import controller.AuthenticationController.UserType;
import model.User;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * View class for handling user login
 */
public class LoginView {
    
    private final Scanner scanner;
    private final AuthenticationController authController;
    // Regular expression for NRIC validation: Starts with S or T, followed by 7 digits, and ends with a letter
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Za-z]$");
    
    /**
     * Constructor for LoginView
     * 
     * @param authController Authentication controller for validating credentials
     */
    public LoginView(AuthenticationController authController) {
        this.scanner = new Scanner(System.in);
        this.authController = authController;
    }
    
    /**
     * Displays the login screen and handles user authentication
     * 
     * @return Authenticated user if login successful, null otherwise
     */
    public User displayLoginScreen() {
        System.out.println("\n==============================");
        System.out.println("      HDB HOUSING SYSTEM      ");
        System.out.println("==============================");
        System.out.println("Welcome! Please log in to continue.");
        
        User authenticatedUser = null;
        boolean exitLogin = false;
        
        while (!exitLogin) {
            // Get user type selection
            UserType userType = promptUserType();
            
            if (userType == null) {
                System.out.println("Exiting system. Goodbye!");
                return null;
            }
            
            // Get and validate NRIC
            String nric = null;
            boolean validNRIC = false;
            
            while (!validNRIC) {
                nric = promptInput("Enter your NRIC (format: Sxxxxxxxa or Txxxxxxxa): ");
                if (isValidNRIC(nric)) {
                    validNRIC = true;
                } else {
                    System.out.println("Invalid NRIC format. NRIC must start with 'S' or 'T', followed by 7 digits, and end with a letter.");
                }
            }
            
            String password = promptInput("Enter your password: ");
            
            // Attempt to authenticate
            authenticatedUser = authController.authenticate(userType, nric, password);
            
            if (authenticatedUser != null) {
                System.out.println("\nLogin successful! Welcome, " + authenticatedUser.getName() + ".");
                return authenticatedUser;
            } else {
                System.out.println("\nInvalid credentials or account not approved. Please try again or select a different user type.");
                System.out.println("Enter 'Y' to try again, or any other key to exit: ");
                String retry = scanner.nextLine().trim().toUpperCase();
                if (!retry.equals("Y")) {
                    exitLogin = true;
                }
            }
        }
        
        return null;
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
     * Prompts the user to select a user type
     * 
     * @return Selected UserType, or null if exit is chosen
     */
    private UserType promptUserType() {
        while (true) {
            System.out.println("\nSelect user type:");
            System.out.println("1. Applicant");
            System.out.println("2. HDB Officer");
            System.out.println("3. HDB Manager");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            
            String input = scanner.nextLine().trim();
            
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 0:
                        return null;
                    case 1:
                        return UserType.APPLICANT;
                    case 2:
                        return UserType.OFFICER;
                    case 3:
                        return UserType.MANAGER;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    /**
     * Prompts the user for input with a given message
     * 
     * @param message The prompt message
     * @return User input as string
     */
    private String promptInput(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
} 