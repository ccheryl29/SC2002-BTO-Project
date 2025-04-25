package view;

import controller.OfficerRegistrationController;
import model.HDBOfficer;
import model.Project;

import java.util.List;
import java.util.Scanner;
import java.util.Date;

/**
 * View class for handling HDB Officer registration operations
 */
public class OfficerRegistrationView {
    
    private final Scanner scanner;
    private final OfficerRegistrationController registrationController;
    private HDBOfficer currentOfficer;
    
    /**
     * Constructor for OfficerRegistrationView
     * 
     * @param registrationController Controller for registration operations
     */
    public OfficerRegistrationView(OfficerRegistrationController registrationController) {
        this.scanner = new Scanner(System.in);
        this.registrationController = registrationController;
    }
    
    /**
     * Sets the current officer
     * 
     * @param officer The officer using the view
     */
    public void setCurrentOfficer(HDBOfficer officer) {
        this.currentOfficer = officer;
    }
    
    /**
     * Displays the registration menu
     * 
     * @param officer The officer using the menu
     */
    public void displayMenu(HDBOfficer officer) {
        if (officer == null) {
            System.out.println("Error: No officer logged in.");
            return;
        }
        
        this.currentOfficer = officer;
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n===== Project Registration =====");
            System.out.println("1. View Available Projects");
            System.out.println("2. Register for a Project");
            System.out.println("3. View My Registration Status");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    viewAvailableProjects();
                    break;
                case 2:
                    registerForProject();
                    break;
                case 3:
                    viewRegistrationStatus();
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
     * Displays available projects for registration
     */
    private void viewAvailableProjects() {
        System.out.println("\n===== Available Projects =====");
        
        List<Project> availableProjects = registrationController.getAvailableProjects();
        
        if (availableProjects.isEmpty()) {
            System.out.println("No projects are currently available for registration.");
        } else {
            for (int i = 0; i < availableProjects.size(); i++) {
                Project project = availableProjects.get(i);
                System.out.println((i + 1) + ". " + project.getName());
                System.out.println("   Neighborhood: " + project.getNeighborhood());
                System.out.println("   Application Period: " + project.getApplicationOpenDate() + 
                                   " to " + project.getApplicationCloseDate());
                System.out.println("   Available Officer Slots: " + 
                                   (project.getAvailableOfficerSlots() - project.getRegisteredOfficers().size()) + 
                                   "/" + project.getAvailableOfficerSlots());
                System.out.println();
            }
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Registers the officer for a project
     */
    private void registerForProject() {
        System.out.println("\n===== Register for a Project =====");
        
        // Check current status
        if (currentOfficer.getHandlingProject() != null && 
            currentOfficer.getRegistrationStatus() == HDBOfficer.RegistrationStatus.APPROVED) {
            
            Project handlingProject = currentOfficer.getHandlingProject();
            Date currentDate = new Date();
            
            // Check if the current date is within the application period (inclusive)
            boolean isWithinApplicationPeriod = 
                (currentDate.compareTo(handlingProject.getApplicationOpenDate()) >= 0 && 
                 currentDate.compareTo(handlingProject.getApplicationCloseDate()) <= 0);
            
            if (isWithinApplicationPeriod) {
                System.out.println("You are already handling a project: " + handlingProject.getName());
                System.out.println("The application period is active. You cannot register for another project during this period.");
                System.out.println("Application Period: " + handlingProject.getApplicationOpenDate() + 
                                  " to " + handlingProject.getApplicationCloseDate());
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                return;
            } else {
                System.out.println("You are currently assigned to: " + handlingProject.getName());
                System.out.println("However, the application period has ended, so you can register for a new project.");
            }
        }
        
        if (currentOfficer.getRegistrationStatus() == HDBOfficer.RegistrationStatus.PENDING) {
            System.out.println("You already have a pending registration. Please wait for approval.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display available projects
        List<Project> availableProjects = registrationController.getAvailableProjects();
        
        if (availableProjects.isEmpty()) {
            System.out.println("No projects are currently available for registration.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Available Projects:");
        for (int i = 0; i < availableProjects.size(); i++) {
            Project project = availableProjects.get(i);
            System.out.println((i + 1) + ". " + project.getName() + " (" + project.getNeighborhood() + ")");
        }
        
        System.out.print("\nEnter the number of the project you wish to register for (0 to cancel): ");
        int projectIndex = readIntInput() - 1;
        
        if (projectIndex == -1) {
            System.out.println("Registration cancelled.");
            return;
        }
        
        if (projectIndex < 0 || projectIndex >= availableProjects.size()) {
            System.out.println("Invalid project selection.");
            return;
        }
        
        Project selectedProject = availableProjects.get(projectIndex);
        
        // Additional check to see if the officer has applied for this specific project
        if (currentOfficer.hasAppliedForProject(selectedProject)) {
            System.out.println("You have already applied for this project as an applicant.");
            System.out.println("You cannot register to handle a project that you have applied for.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\nYou are about to register for project: " + selectedProject.getName());
        System.out.println("This will set your registration status to PENDING.");
        System.out.print("Confirm registration? (Y/N): ");
        
        String confirmation = scanner.nextLine().trim().toUpperCase();
        
        if (!confirmation.equals("Y")) {
            System.out.println("Registration cancelled.");
            return;
        }
        
        String result = registrationController.registerForProject(currentOfficer, selectedProject.getName());
        System.out.println(result);
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Displays the officer's registration status
     */
    private void viewRegistrationStatus() {
        System.out.println("\n===== My Registration Status =====");
        
        String status = registrationController.getRegistrationStatus(currentOfficer);
        System.out.println(status);
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Helper method to read integer input
     * 
     * @return The integer input
     */
    private int readIntInput() {
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            return value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
} 