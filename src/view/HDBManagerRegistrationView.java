package view;

import controller.HDBManagerRegistrationController;
import controller.HDBManagerProjectController;
import model.HDBManager;
import model.HDBOfficer;
import model.Project;

import java.util.List;
import java.util.Scanner;

/**
 * View class for handling HDB Manager officer registration operations
 */
public class HDBManagerRegistrationView {
    
    private final Scanner scanner;
    private final HDBManagerRegistrationController registrationController;
    private final HDBManagerProjectController projectController;
    private HDBManager currentManager;
    
    /**
     * Constructor for HDBManagerRegistrationView
     * 
     * @param registrationController Controller for officer registration operations
     * @param projectController Controller for project operations
     */
    public HDBManagerRegistrationView(HDBManagerRegistrationController registrationController, 
                               HDBManagerProjectController projectController) {
        this.scanner = new Scanner(System.in);
        this.registrationController = registrationController;
        this.projectController = projectController;
    }
    
    /**
     * Sets the current manager
     * 
     * @param manager The manager using the view
     */
    public void setCurrentManager(HDBManager manager) {
        this.currentManager = manager;
    }
    
    /**
     * Displays the registration menu
     * 
     * @param manager The manager using the menu
     */
    public void displayMenu(HDBManager manager) {
        if (manager == null) {
            System.out.println("Error: No manager logged in.");
            return;
        }
        
        this.currentManager = manager;
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n===== Officer Registration Management =====");
            System.out.println("1. View and Manage Officer Registrations");
            System.out.println("2. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    manageOfficerRegistrations();
                    break;
                case 2:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
    
    /**
     * Manages officer registrations
     */
    private void manageOfficerRegistrations() {
        // First, select a project
        Project project = selectProject();
        if (project == null) {
            return;
        }
        
        boolean exit = false;
        while (!exit) {
            System.out.println("\n===== Officer Registrations for " + project.getName() + " =====");
            System.out.println("1. View Pending Registrations");
            System.out.println("2. View Approved Registrations");
            System.out.println("3. Back to Previous Menu");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    handlePendingOfficerRegistrations(project.getName());
                    break;
                case 2:
                    displayOfficerRegistrations(
                        registrationController.getApprovedOfficerRegistrations(project.getName()),
                        "Approved"
                    );
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
    
    /**
     * Handles pending officer registrations
     * 
     * @param projectName The name of the project
     */
    private void handlePendingOfficerRegistrations(String projectName) {
        List<HDBOfficer> pendingOfficers = registrationController.getPendingOfficerRegistrations(projectName);
        
        if (pendingOfficers.isEmpty()) {
            System.out.println("No pending officer registrations found for this project.");
            return;
        }
        
        displayOfficerRegistrations(pendingOfficers, "Pending");
        
        System.out.print("\nWould you like to approve/reject an officer? (Y/N): ");
        String choice = scanner.nextLine().trim().toUpperCase();
        
        if (!choice.equals("Y")) {
            return;
        }
        
        System.out.print("Enter the NRIC of the officer: ");
        String officerNRIC = scanner.nextLine().trim();
        
        System.out.print("Do you want to (A)pprove or (R)eject this registration? ");
        String action = scanner.nextLine().trim().toUpperCase();
        
        if (action.equals("A")) {
            String result = registrationController.approveOfficerRegistration(
                officerNRIC, projectName, currentManager
            );
            System.out.println(result);
        } else if (action.equals("R")) {
            String result = registrationController.rejectOfficerRegistration(
                officerNRIC, projectName, currentManager
            );
            System.out.println(result);
        } else {
            System.out.println("Invalid action. Operation cancelled.");
        }
    }
    
    /**
     * Displays officer registrations
     * 
     * @param officers The list of officers to display
     * @param status The status of the officers (for display purposes)
     */
    private void displayOfficerRegistrations(List<HDBOfficer> officers, String status) {
        System.out.println("\n===== " + status + " Officer Registrations =====");
        
        if (officers.isEmpty()) {
            System.out.println("No " + status.toLowerCase() + " officer registrations found.");
            return;
        }
        
        System.out.printf("%-15s %-20s %-5s %-15s %-20s\n", 
                "NRIC", "Name", "Age", "Marital Status", "Registration Status");
        System.out.println("--------------------------------------------------------------------------");
        
        for (HDBOfficer officer : officers) {
            System.out.printf("%-15s %-20s %-5d %-15s %-20s\n",
                    officer.getNRIC(),
                    officer.getName(),
                    officer.getAge(),
                    officer.getMaritalStatus(),
                    officer.getRegistrationStatus());
        }
    }
    
    /**
     * Helper method to select a project
     * 
     * @return The selected project, or null if cancelled
     */
    private Project selectProject() {
        List<Project> projects = projectController.getProjectsByManager(currentManager);
        
        if (projects.isEmpty()) {
            System.out.println("You have no projects to manage.");
            return null;
        }
        
        System.out.println("\n===== Select a Project =====");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getName());
        }
        System.out.println((projects.size() + 1) + ". Cancel");
        
        System.out.print("Enter your choice: ");
        int choice = readIntInput();
        
        if (choice < 1 || choice > projects.size() + 1) {
            System.out.println("Invalid choice.");
            return null;
        }
        
        if (choice == projects.size() + 1) {
            return null;
        }
        
        return projects.get(choice - 1);
    }
    
    /**
     * Reads an integer input from the user
     * 
     * @return The integer input
     */
    private int readIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}