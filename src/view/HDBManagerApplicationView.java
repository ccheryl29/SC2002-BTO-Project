package view;

import controller.HDBManagerApplicationController;
import controller.HDBManagerProjectController;
import model.Application;
import model.HDBManager;
import model.Project;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

/**
 * View class for handling HDB Manager application approval operations
 */
public class HDBManagerApplicationView {
    
    private final Scanner scanner;
    private final HDBManagerApplicationController applicationController;
    private final HDBManagerProjectController projectController;
    private HDBManager currentManager;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Constructor for HDBManagerApplicationView
     * 
     * @param applicationController Controller for application operations
     * @param projectController Controller for project operations
     */
    public HDBManagerApplicationView(HDBManagerApplicationController applicationController, 
                               HDBManagerProjectController projectController) {
        this.scanner = new Scanner(System.in);
        this.applicationController = applicationController;
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
     * Displays the applications menu
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
            System.out.println("\n===== Application Management =====");
            System.out.println("1. Manage Pending Applications");
            System.out.println("2. Manage Withdrawal Requests");
            System.out.println("3. View Applications by Status");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    managePendingApplications();
                    break;
                case 2:
                    manageWithdrawals();
                    break;
                case 3:
                    viewApplicationStatus();
                    break;
                case 4:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
    
    /**
     * Manages pending applications
     */
    private void managePendingApplications() {
        // First, select a project
        Project project = selectProject();
        if (project == null) {
            return;
        }
        
        handlePendingApplications(project.getName());
    }
    
    /**
     * Handles pending applications
     * 
     * @param projectName The name of the project
     */
    private void handlePendingApplications(String projectName) {
        List<Application> pendingApplications = applicationController.getPendingApplications(projectName);
        
        if (pendingApplications.isEmpty()) {
            System.out.println("No pending applications found for this project.");
            return;
        }
        
        displayApplications(pendingApplications, "Pending");
        
        System.out.print("\nWould you like to approve/reject an application? (Y/N): ");
        String choice = scanner.nextLine().trim().toUpperCase();
        
        if (!choice.equals("Y")) {
            return;
        }
        
        System.out.print("Enter the NRIC of the applicant: ");
        String applicantNRIC = scanner.nextLine().trim();
        
        System.out.print("Do you want to (A)pprove or (R)eject this application? ");
        String action = scanner.nextLine().trim().toUpperCase();
        
        if (action.equals("A")) {
            String result = applicationController.approveApplication(
                applicantNRIC, projectName, currentManager
            );
            System.out.println(result);
        } else if (action.equals("R")) {
            String result = applicationController.rejectApplication(
                applicantNRIC, projectName, currentManager
            );
            System.out.println(result);
        } else {
            System.out.println("Invalid action. Operation cancelled.");
        }
    }
    
    /**
     * Manages withdrawal requests
     */
    private void manageWithdrawals() {
        // First, select a project
        Project project = selectProject();
        if (project == null) {
            return;
        }
        
        // View applications that have a withdrawal request
        List<Application> applications = applicationController.getWithdrawalRequests(project.getName());
        
        if (applications.isEmpty()) {
            System.out.println("No withdrawal requests found for this project.");
            return;
        }
        
        System.out.println("\n===== Withdrawal Requests for " + project.getName() + " =====");
        
        displayApplications(applications, "Withdrawal Requests");
        
        System.out.print("\nWould you like to approve/reject a withdrawal? (Y/N): ");
        String choice = scanner.nextLine().trim().toUpperCase();
        
        if (!choice.equals("Y")) {
            return;
        }
        
        System.out.print("Enter the NRIC of the applicant: ");
        String applicantNRIC = scanner.nextLine().trim();
        
        System.out.print("Do you want to (A)pprove or (R)eject this withdrawal? ");
        String action = scanner.nextLine().trim().toUpperCase();
        
        if (action.equals("A")) {
            String result = applicationController.approveWithdrawal(
                applicantNRIC, project.getName(), currentManager
            );
            System.out.println(result);
        } else if (action.equals("R")) {
            String result = applicationController.rejectWithdrawal(
                applicantNRIC, project.getName(), currentManager
            );
            System.out.println(result);
        } else {
            System.out.println("Invalid action. Operation cancelled.");
        }
    }
    
    /**
     * Views application status
     */
    private void viewApplicationStatus() {
        // First, select a project
        Project project = selectProject();
        if (project == null) {
            return;
        }
        
        System.out.println("\n===== Application Status =====");
        System.out.println("1. View Pending Applications");
        System.out.println("2. View Successful Applications");
        System.out.println("3. View Unsuccessful Applications");
        System.out.println("4. View Booked Applications");
        System.out.println("5. Back to Previous Menu");
        System.out.print("Enter your choice: ");
        
        int choice = readIntInput();
        String status;
        
        switch (choice) {
            case 1:
                status = "PENDING";
                break;
            case 2:
                status = "SUCCESSFUL";
                break;
            case 3:
                status = "UNSUCCESSFUL";
                break;
            case 4:
                status = "BOOKED";
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        List<Application> applications = applicationController.getApplicationsByStatus(project.getName(), status);
        displayApplications(applications, status);
    }
    
    /**
     * Displays applications
     * 
     * @param applications The list of applications to display
     * @param status The status of the applications (for display purposes)
     */
    private void displayApplications(List<Application> applications, String status) {
        System.out.println("\n===== " + status + " Applications =====");
        
        if (applications.isEmpty()) {
            System.out.println("No " + status.toLowerCase() + " applications found.");
            return;
        }
        
        System.out.printf("%-15s %-20s %-15s %-12s %-15s\n", 
                "Applicant NRIC", "Applicant Name", "Flat Type", "Status", "Application Date");
        System.out.println("------------------------------------------------------------------------------");
        
        for (Application app : applications) {
            System.out.printf("%-15s %-20s %-15s %-12s %-15s\n",
                    app.getApplicant().getNRIC(),
                    app.getApplicant().getName(),
                    app.getFlatType().getDisplayName(),
                    app.getStatus(),
                    dateFormat.format(app.getApplicationDate()));
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