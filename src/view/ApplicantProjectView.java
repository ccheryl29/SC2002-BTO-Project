package view;

import controller.ApplicantProjectController;
import model.Applicant;
import model.Application;
import model.Flat;
import model.Project;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

/**
 * View class for applicant project interactions
 */
public class ApplicantProjectView {
    
    private final Scanner scanner;
    private final ApplicantProjectController projectController;
    private Applicant currentApplicant;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Constructor for ApplicantProjectView
     * 
     * @param projectController The project controller
     */
    public ApplicantProjectView(ApplicantProjectController projectController) {
        this.scanner = new Scanner(System.in);
        this.projectController = projectController;
    }
    
    /**
     * Sets the current applicant
     * 
     * @param applicant The applicant
     */
    public void setCurrentApplicant(Applicant applicant) {
        this.currentApplicant = applicant;
    }
    
    /**
     * Displays the main menu
     */
    public void displayMenu() {
        if (currentApplicant == null) {
            System.out.println("Error: No applicant logged in.");
            return;
        }
        
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n===== PROJECT APPLICATION MENU =====");
            System.out.println("1. Browse Available Projects");
            System.out.println("2. Apply for a Project");
            System.out.println("3. Check Application Status");
            System.out.println("4. Withdraw Application");
            
            // Only show booking option if booking controller is available
            System.out.println("5. Request Flat Booking");
            System.out.println("6. Back to Main Menu");
            
            
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    browseProjects();
                    break;
                case 2:
                    applyForProject();
                    break;
                case 3:
                    checkApplicationStatus();
                    break;
                case 4:
                    withdrawApplication();
                    break;
                case 5:
                    requestFlatBooking();
                    break;
                case 6:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
    
    /**
     * Allows the applicant to browse available projects
     */
    protected void browseProjects() {
        List<Project> projects = projectController.getVisibleProjects(currentApplicant);
        
        if (projects.isEmpty()) {
            System.out.println("No projects available for your eligibility criteria.");
            return;
        }
        
        System.out.println("\n===== AVAILABLE PROJECTS =====");
        System.out.printf("%-5s %-25s %-20s %-15s %-15s\n", 
                "No.", "Project Name", "Neighborhood", "Opens", "Closes");
        System.out.println("-------------------------------------------------------------------------");
        
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.printf("%-5d %-25s %-20s %-15s %-15s\n", 
                    (i + 1), 
                    project.getName(), 
                    project.getNeighborhood(),
                    dateFormat.format(project.getApplicationOpenDate()),
                    dateFormat.format(project.getApplicationCloseDate()));
        }
        
        System.out.print("\nEnter a project number to view details (0 to go back): ");
        int choice = readIntInput();
        
        if (choice > 0 && choice <= projects.size()) {
            viewProjectDetails(projects.get(choice - 1));
        }
    }
    
    /**
     * Displays details of a selected project
     * 
     * @param project The project to display
     */
    private void viewProjectDetails(Project project) {
        System.out.println("\n===== PROJECT DETAILS =====");
        System.out.println("Project Name: " + project.getName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Period: " + 
                dateFormat.format(project.getApplicationOpenDate()) + " to " + 
                dateFormat.format(project.getApplicationCloseDate()));
        
        System.out.println("\nAvailable Flat Types:");
        System.out.printf("%-20s %-15s %-15s\n", "Flat Type", "Price", "Available Units");
        System.out.println("--------------------------------------------------");
        
        for (Flat flat : project.getFlats()) {
            if (isEligibleForFlatType(flat.getFlatType())) {
                System.out.printf("%-20s $%-14s %-15d\n", 
                        flat.getFlatType().getDisplayName(), 
                        flat.getSellingPrice(),
                        flat.getAvailableUnits());
            }
        }
        
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Checks if the current applicant is eligible for a flat type
     * 
     * @param flatType The flat type to check
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForFlatType(Flat.FlatType flatType) {
        // Singles, 35 years old and above, can ONLY apply for 2-Room
        if (currentApplicant.getMaritalStatus() == Applicant.MaritalStatus.SINGLE) {
            if (currentApplicant.getAge() < 35) {
                return false;
            }
            return flatType == Flat.FlatType.TWO_ROOM;
        } 
        // Married, 21 years old and above, can apply for any flat types
        else if (currentApplicant.getMaritalStatus() == Applicant.MaritalStatus.MARRIED) {
            if (currentApplicant.getAge() < 21) {
                return false;
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Allows the applicant to apply for a project
     */
    protected void applyForProject() {
        List<Project> projects = projectController.getVisibleProjects(currentApplicant);
        
        if (projects.isEmpty()) {
            System.out.println("No projects available for your eligibility criteria.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\n===== APPLY FOR PROJECT =====");
        System.out.printf("%-5s %-25s %-20s\n", 
                "No.", "Project Name", "Neighborhood");
        System.out.println("--------------------------------------------------");
        
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.printf("%-5d %-25s %-20s\n", 
                    (i + 1), 
                    project.getName(), 
                    project.getNeighborhood());
        }
        
        System.out.print("\nEnter a project number to apply (0 to cancel): ");
        int projectChoice = readIntInput();
        
        if (projectChoice <= 0 || projectChoice > projects.size()) {
            return;
        }
        
        Project selectedProject = projects.get(projectChoice - 1);
        
        // Show available flat types for the selected project
        System.out.println("\nAvailable Flat Types for " + selectedProject.getName() + ":");
        System.out.printf("%-5s %-20s %-15s %-15s\n", 
                "No.", "Flat Type", "Price Range", "Available Units");
        System.out.println("-----------------------------------------------------");
        
        List<Flat> eligibleFlats = selectedProject.getFlats().stream()
            .filter(flat -> isEligibleForFlatType(flat.getFlatType()) && flat.getAvailableUnits() > 0)
            .toList();
        
        if (eligibleFlats.isEmpty()) {
            System.out.println("No eligible flat types with available units for this project.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        for (int i = 0; i < eligibleFlats.size(); i++) {
            Flat flat = eligibleFlats.get(i);
            System.out.printf("%-5d %-20s $%-14s %-15d\n", 
                    (i + 1), 
                    flat.getFlatType().getDisplayName(), 
                    flat.getSellingPrice(),
                    flat.getAvailableUnits());
        }
        
        System.out.print("\nEnter a flat type number to apply (0 to cancel): ");
        int flatChoice = readIntInput();
        
        if (flatChoice <= 0 || flatChoice > eligibleFlats.size()) {
            return;
        }
        
        Flat selectedFlat = eligibleFlats.get(flatChoice - 1);
        
        // Confirm application
        System.out.println("\nYou are about to apply for:");
        System.out.println("Project: " + selectedProject.getName());
        System.out.println("Flat Type: " + selectedFlat.getFlatType().getDisplayName());
        System.out.println("Price: $" + selectedFlat.getSellingPrice());
        
        System.out.print("\nConfirm application? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
            String result = projectController.applyForProject(
                    currentApplicant, 
                    selectedProject.getName(), 
                    selectedFlat.getFlatType().name());
            
            System.out.println(result);
        } else {
            System.out.println("Application cancelled.");
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Checks the status of the applicant's application
     */
    protected void checkApplicationStatus() {
        String status = projectController.checkApplicationStatus(currentApplicant);
        System.out.println("\n===== APPLICATION STATUS =====");
        System.out.println(status);
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Allows the applicant to withdraw their application
     */
    protected void withdrawApplication() {
        System.out.println("\n===== WITHDRAW APPLICATION =====");
        
        // Check current application status first
        String status = projectController.checkApplicationStatus(currentApplicant);
        
        if (status.contains("don't have any active applications")) {
            System.out.println(status);
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Current Application:");
        System.out.println(status);
        
        System.out.print("\nAre you sure you want to withdraw your application? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
            String result = projectController.withdrawApplication(currentApplicant);
            System.out.println(result);
        } else {
            System.out.println("Withdrawal cancelled.");
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Allows the applicant to request a flat booking
     * (only available for successful applications)
     */
    protected void requestFlatBooking() {

        
        System.out.println("\n===== REQUEST FLAT BOOKING =====");
        
        // Check application status first
        String status = projectController.checkApplicationStatus(currentApplicant);
        
        if (status.contains("don't have any active applications")) {
            System.out.println("You don't have any active applications to book.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Check if application is successful
        if (!status.contains("SUCCESSFUL")) {
            System.out.println("Only SUCCESSFUL applications are eligible for booking.");
            System.out.println("Your current application status does not allow booking.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Parse project name from status (simple but might need improvement)
        String projectName = "";
        if (status.contains("Project:")) {
            int projectIndex = status.indexOf("Project:") + 9;
            int endIndex = status.indexOf("\n", projectIndex);
            if (endIndex != -1) {
                projectName = status.substring(projectIndex, endIndex).trim();
            } else {
                projectName = status.substring(projectIndex).trim();
            }
        }
        
        if (projectName.isEmpty()) {
            System.out.println("Unable to determine project name. Please contact support.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("You are about to request a booking for your successful application:");
        System.out.println(status);
        
        System.out.println("\nNOTE: A booking request requires an HDB Officer to complete the booking process.");
        System.out.println("After submitting your request, please contact an HDB Officer to assist with your booking.");
        
        System.out.print("\nConfirm booking request? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
            String result = projectController.initiateBooking(currentApplicant.getNRIC(), projectName);
            System.out.println(result);
        } else {
            System.out.println("Booking request cancelled.");
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Reads an integer input from the user
     * 
     * @return The integer input
     */
    protected int readIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Returns the scanner for use by subclasses
     * 
     * @return The scanner
     */
    protected Scanner getScanner() {
        return scanner;
    }
    
    /**
     * Returns the project controller for use by subclasses
     * 
     * @return The project controller
     */
    protected ApplicantProjectController getProjectController() {
        return projectController;
    }
} 