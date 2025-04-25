package view;

import controller.HDBManagerProjectController;
import controller.AccountManagementController;
import controller.HDBManagerApplicationController;
import controller.HDBManagerRegistrationController;
import controller.HDBManagerEnquiryController;
import model.HDBManager;
import service.AccountManagementService;

import java.util.Scanner;

/**
 * Dashboard view class for HDB Manager interactions
 */
public class HDBManagerDashboard extends Dashboard {
    
    private final Scanner scanner;
    private final HDBManagerProjectView projectView;
    private final HDBManagerApplicationView applicationView;
    private final HDBManagerRegistrationView registrationView;
    private final HDBManagerEnquiryView enquiryView;
    private final HDBManager currentManager;
    private final AccountManagementView accountManagementView;
    
    /**
     * Constructor for HDBManagerDashboard
     * 
     * @param manager The authenticated HDB Manager
     * @param projectController The project controller
     * @param applicationController The application controller
     * @param registrationController The registration controller
     * @param enquiryController The enquiry controller
     */
    public HDBManagerDashboard(
            HDBManager manager,
            HDBManagerProjectController projectController,
            HDBManagerApplicationController applicationController,
            HDBManagerRegistrationController registrationController,
            HDBManagerEnquiryController enquiryController,
            AccountManagementController accountController) {
        super(manager);
        this.currentManager = manager;
        this.scanner = new Scanner(System.in);
        this.projectView = new HDBManagerProjectView(projectController);
        this.applicationView = new HDBManagerApplicationView(applicationController, projectController);
        this.registrationView = new HDBManagerRegistrationView(registrationController, projectController);
        this.enquiryView = new HDBManagerEnquiryView(enquiryController, projectController);
        
        // Initialize views with manager
        this.projectView.setCurrentManager(manager);
        this.applicationView.setCurrentManager(manager);
        this.registrationView.setCurrentManager(manager);
        this.enquiryView.setCurrentManager(manager);
        
        // Initialize account management

        this.accountManagementView = new AccountManagementView(accountController, manager);
    }
    
    /**
     * Displays the dashboard and handles user interactions
     * 
     * @return true if relogin is required, false otherwise
     */
    @Override
    public boolean displayDashboard() {
        if (currentManager == null) {
            System.out.println("Error: No manager logged in.");
            return false;
        }
        
        boolean exit = false;
        boolean reloginRequired = false;
        
        while (!exit) {
            System.out.println("\n===== HDB MANAGER DASHBOARD =====");
            System.out.println("Welcome, " + currentManager.getName() + "!");
            System.out.println("1. Project Management");
            System.out.println("2. Application Management");
            System.out.println("3. Officer Registration Management");
            System.out.println("4. Enquiry Management");
            System.out.println("5. System Reports");
            System.out.println("6. Account Management");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    projectView.displayMenu(currentManager);
                    break;
                case 2:
                    applicationView.displayMenu(currentManager);
                    break;
                case 3:
                    registrationView.displayMenu(currentManager);
                    break;
                case 4:
                    enquiryView.displayMenu(currentManager);
                    break;
                case 5:
                    displayReportsMenu();
                    break;
                case 6:
                    reloginRequired = displayAccountSettingsMenu();
                    if (reloginRequired) {
                        return true;
                    }
                    break;
                case 7:
                    exit = true;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
        
        return false;
    }
    
    /**
     * Displays the reports menu
     */
    private void displayReportsMenu() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n===== SYSTEM REPORTS =====");
            System.out.println("1. Project Statistics");
            System.out.println("2. Application Statistics");
            System.out.println("3. Back to Dashboard");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    System.out.println("Project Statistics - Not implemented yet");
                    break;
                case 2:
                    System.out.println("Application Statistics - Not implemented yet");
                    break;
                case 3:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
    
    /**
     * Displays the account settings menu
     * 
     * @return true if relogin is required, false otherwise
     */
    private boolean displayAccountSettingsMenu() {
        boolean back = false;
        boolean reloginRequired = false;
        
        while (!back) {
            System.out.println("\n===== ACCOUNT SETTINGS =====");
            System.out.println("1. View Profile");
            System.out.println("2. Account Management");
            System.out.println("3. Back to Dashboard");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    displayProfileInfo();
                    break;
                case 2:
                    reloginRequired = accountManagementView.displayMenu();
                    if (reloginRequired) {
                        return true;
                    }
                    break;
                case 3:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
        
        return false;
    }
    
    /**
     * Displays the manager's profile information
     */
    private void displayProfileInfo() {
        System.out.println("\n===== PROFILE INFORMATION =====");
        System.out.println("Name: " + currentManager.getName());
        System.out.println("NRIC: " + currentManager.getNRIC());
        System.out.println("Age: " + currentManager.getAge());
        System.out.println("Marital Status: " + currentManager.getMaritalStatus());
        System.out.println("Number of Projects Created: " + currentManager.getCreatedProjects().size());
        
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Read integer input from the user
     * 
     * @return The integer input
     */
    private int readIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
} 