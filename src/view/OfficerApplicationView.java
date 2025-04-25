package view;

import controller.ApplicantProjectController;
import model.HDBOfficer;
import model.Project;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import model.Flat;

/**
 * View class for HDB Officer application interactions.
 * This class extends ApplicantProjectView with officer-specific messages.
 */
public class OfficerApplicationView extends ApplicantProjectView {
    
    private HDBOfficer currentOfficer;
    private final Scanner scanner;
    private final ApplicantProjectController projectController;
    
    /**
     * Constructor for OfficerApplicationView
     * 
     * @param projectController The project controller
     */
    public OfficerApplicationView(ApplicantProjectController projectController) {
        super(projectController);
        this.projectController = projectController;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Sets the current officer
     * 
     * @param officer The officer using the view
     */
    public void setCurrentOfficer(HDBOfficer officer) {
        this.currentOfficer = officer;
        super.setCurrentApplicant(officer); // HDBOfficer extends Applicant
    }
    
    /**
     * Checks if the officer is handling this project
     * 
     * @param project The project to check
     * @return true if handling, false otherwise
     */
    private boolean isOfficerHandlingProject(Project project) {
        if (currentOfficer == null || project == null) {
            return false;
        }
        
        Project handlingProject = currentOfficer.getHandlingProject();
        return handlingProject != null && handlingProject.getName().equals(project.getName());
    }
    
    /**
     * Override displayMenu to show officer-specific message
     */

    
    /**
     * Overrides the applyForProject method to add officer-specific validation
     * to prevent officers from applying for projects they are handling
     */
    @Override
    protected void applyForProject() {
        if (currentOfficer == null) {
            System.out.println("Error: No officer logged in.");
            return;
        }
        
        // Get all visible projects
        List<Project> allProjects = projectController.getVisibleProjects(currentOfficer);
        
        if (allProjects.isEmpty()) {
            System.out.println("No projects available for your eligibility criteria.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Filter out projects the officer is handling
        List<Project> eligibleProjects = new ArrayList<>();
        for (Project project : allProjects) {
            if (!isOfficerHandlingProject(project)) {
                eligibleProjects.add(project);
            }
        }
        
        if (eligibleProjects.isEmpty()) {
            System.out.println("You are currently handling all available projects.");
            System.out.println("You cannot apply for projects you are handling as an officer.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Now delegate to the parent class method
        List<Project> projects = projectController.getVisibleProjects(currentOfficer);
        
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
        if (isOfficerHandlingProject(selectedProject)) {
            System.out.println("You are currently handling this project.");
            System.out.println("You cannot apply for projects you are handling as an officer.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
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
                    currentOfficer, 
                    selectedProject.getName(), 
                    selectedFlat.getFlatType().name());
            
            System.out.println(result);
        } else {
            System.out.println("Application cancelled.");
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
}
    
