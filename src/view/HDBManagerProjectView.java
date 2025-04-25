package view;

import controller.HDBManagerProjectController;
import exception.HDBException;
import model.Flat;
import model.HDBManager;
import model.Project;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * View class for HDB Manager's project management functionality
 */
public class HDBManagerProjectView {
    
    private final Scanner scanner;
    private final HDBManagerProjectController projectController;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private HDBManager currentManager;
    
    /**
     * Constructor for HDBManagerProjectView
     * 
     * @param projectController Controller for project management
     */
    public HDBManagerProjectView(HDBManagerProjectController projectController) {
        this.scanner = new Scanner(System.in);
        this.projectController = projectController;
    }
    
    /**
     * Sets the current manager
     * 
     * @param manager The current manager
     */
    public void setCurrentManager(HDBManager manager) {
        this.currentManager = manager;
    }
    
    /**
     * Displays the project management menu
     * 
     * @param manager The manager
     */
    public void displayMenu(HDBManager manager) {
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n===== HDB Project Management =====");
            System.out.println("1. Create a new project");
            System.out.println("2. View my projects");
            System.out.println("3. View all projects");
            System.out.println("4. View archived projects");
            System.out.println("5. Edit project details");
            System.out.println("6. Toggle project visibility");
            System.out.println("7. Delete/Archive project");
            System.out.println("8. Generate project applicants report");
            System.out.println("9. Generate filtered applicants report");
            System.out.println("10. Back to main menu");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    createProject(manager);
                    break;
                case 2:
                    viewMyProjects(manager);
                    break;
                case 3:
                    viewAllProjects();
                    break;
                case 4:
                    viewArchivedProjects(manager);
                    break;
                case 5:
                    editProjectDetails(manager);
                    break;
                case 6:
                    toggleProjectVisibility(manager);
                    break;
                case 7:
                    deleteProject(manager);
                    break;
                case 8:
                    generateProjectReport();
                    break;
                case 9:
                    generateFilteredReport();
                    break;
                case 10:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
    
    /**
     * Handles project creation
     */
    private void createProject(HDBManager manager) {
        System.out.println("\n----- CREATE NEW PROJECT -----");
        
        try {
            System.out.print("Enter Project Name: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Enter Neighborhood: ");
            String neighborhood = scanner.nextLine().trim();
            
            System.out.print("Enter Application Opening Date (dd/MM/yyyy): ");
            String openDate = scanner.nextLine().trim();
            
            System.out.print("Enter Application Closing Date (dd/MM/yyyy): ");
            String closeDate = scanner.nextLine().trim();
            
            System.out.print("Enter Available Officer Slots (max 10): ");
            String slots = scanner.nextLine().trim();
            
            String result = projectController.createProject(
                name, neighborhood, openDate, closeDate, slots, manager
            );
            
            if (result.startsWith("Error:")) {
                displayError(result);
                return; // Stop if there's an error in project creation
            }
            
            displaySuccess(result);
            
            // If project was created successfully, prompt for flat types
            System.out.println("\nNow let's add flat types to your project.");
            System.out.println("You can add at most one of each flat type (2-Room and 3-Room).");
            
            // Track which flat types have been added
            boolean twoRoomAdded = false;
            boolean threeRoomAdded = false;
            
            // First flat
            if (addFlatTypeToProject(name, twoRoomAdded, threeRoomAdded)) {
                // Update which type was added based on user's selection
                String lastAddedType = getLastAddedFlatType(name);
                if (lastAddedType.equals("2-Room")) {
                    twoRoomAdded = true;
                } else if (lastAddedType.equals("3-Room")) {
                    threeRoomAdded = true;
                }
                
                // Check if both types are already added
                if (twoRoomAdded && threeRoomAdded) {
                    System.out.println("\nAll flat types have been added to the project.");
                    return;
                }
                
                // Second flat (optional)
                System.out.print("\nAdd another flat type? (Y/N): ");
                String choice = scanner.nextLine().trim().toUpperCase();
                
                if (choice.equals("Y")) {
                    addFlatTypeToProject(name, twoRoomAdded, threeRoomAdded);
                }
            }
        } catch (Exception e) {
            displayError("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    /**
     * Handles adding a flat type to a project during creation
     * 
     * @param projectName The name of the project
     * @param twoRoomAdded Whether a 2-Room flat has already been added
     * @param threeRoomAdded Whether a 3-Room flat has already been added
     * @return true if flat was added successfully, false otherwise
     */
    private boolean addFlatTypeToProject(String projectName, boolean twoRoomAdded, boolean threeRoomAdded) {
        System.out.println("\n----- ADD FLAT TYPE -----");
        
        // Show available types
        if (!twoRoomAdded && !threeRoomAdded) {
            System.out.println("Available types: 2-Room, 3-Room");
        } else if (twoRoomAdded && !threeRoomAdded) {
            System.out.println("Available type: 3-Room");
        } else if (!twoRoomAdded && threeRoomAdded) {
            System.out.println("Available type: 2-Room");
        } else {
            System.out.println("No more flat types available to add.");
            return false;
        }
        
        try {
            System.out.print("Enter Flat Type: ");
            String flatType = scanner.nextLine().trim();
            
            // Verify it's one of the valid types
            if (Flat.FlatType.fromDisplayName(flatType) == null) {
                displayError("Invalid flat type. Only '2-Room' and '3-Room' are supported.");
                return false;
            }
            
            // Check if this type has already been added
            if ((flatType.equals("2-Room") && twoRoomAdded) || (flatType.equals("3-Room") && threeRoomAdded)) {
                displayError("This flat type has already been added to the project.");
                return false;
            }
            
            System.out.print("Enter Total Units: ");
            String totalUnits = scanner.nextLine().trim();
            
            System.out.print("Enter Selling Price: ");
            String sellingPrice = scanner.nextLine().trim();
            
            String result = projectController.addFlatToProject(
                projectName, flatType, totalUnits, sellingPrice
            );
            
            if (result.startsWith("Error:")) {
                displayError(result);
                return false;
            }
            
            displaySuccess(result);
            return true;
        } catch (Exception e) {
            displayError("An unexpected error occurred: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the flat type of the last added flat for a project
     * 
     * @param projectName The name of the project
     * @return The display name of the last added flat type
     */
    private String getLastAddedFlatType(String projectName) {
        Project project = projectController.getProjectByName(projectName);
        if (project != null && !project.getFlats().isEmpty()) {
            List<Flat> flats = project.getFlats();
            Flat lastFlat = flats.get(flats.size() - 1);
            return lastFlat.getFlatType().getDisplayName();
        }
        return "";
    }
    
    /**
     * View projects created by the manager
     * 
     * @param manager The manager
     */
    private void viewMyProjects(HDBManager manager) {
        List<Project> projects = projectController.getProjectsByManager(manager);
        
        if (projects.isEmpty()) {
            System.out.println("You haven't created any projects yet.");
            return;
        }
        
        System.out.println("\n===== My Projects =====");
        displayProjects(projects);
    }
    
    /**
     * View all projects in the system
     */
    private void viewAllProjects() {
        List<Project> projects = projectController.getAllProjects();
        
        if (projects.isEmpty()) {
            System.out.println("There are no projects in the system.");
            return;
        }
        
        System.out.println("\n===== All Projects =====");
        displayProjects(projects);
    }
    
    /**
     * Display a list of projects
     * 
     * @param projects List of projects to display
     */
    private void displayProjects(List<Project> projects) {
        System.out.printf("%-20s %-15s %-15s %-15s %-10s\n", 
                "Project Name", "Start Date", "End Date", "Location", "Creator");
        System.out.println("------------------------------------------------------------------------------");
        
        for (Project project : projects) {
            System.out.printf("%-20s %-15s %-15s %-15s %-10s\n",
                    project.getName(),
                    dateFormat.format(project.getApplicationOpenDate()),
                    dateFormat.format(project.getApplicationCloseDate()),
                    project.getNeighborhood(),
                    "Manager"); // Since there's no direct creator getter
        }
    }
    
    /**
     * Generate a report of applicants for a specific project
     */
    private void generateProjectReport() {
        System.out.println("\n===== Generate Project Applicants Report =====");
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();
        
        List<Map<String, Object>> report = projectController.generateProjectApplicantsReport(projectName);
        
        if (report.isEmpty()) {
            System.out.println("No applicants found for project: " + projectName);
            return;
        }
        
        displayApplicantsReport(report);
    }
    
    /**
     * Generate a filtered report of applicants
     */
    private void generateFilteredReport() {
        System.out.println("\n===== Generate Filtered Applicants Report =====");
        
        System.out.print("Project name (leave empty for all): ");
        String projectName = scanner.nextLine().trim();
        if (projectName.isEmpty()) projectName = null;
        
        // Present available flat types
        System.out.println("\nAvailable flat types: 2-Room, 3-Room");
        System.out.print("Flat type (leave empty for all): ");
        String flatType = scanner.nextLine().trim();
        
        // Validate flat type if one was entered
        if (!flatType.isEmpty() && Flat.FlatType.fromDisplayName(flatType) == null) {
            displayError("Invalid flat type. Only '2-Room' and '3-Room' are supported.");
            return;
        }
        
        if (flatType.isEmpty()) flatType = null;
        
        System.out.print("Marital status (married/single, leave empty for all): ");
        String maritalStatus = scanner.nextLine().trim();
        if (maritalStatus.isEmpty()) maritalStatus = null;
        
        System.out.print("Minimum age (leave empty for no minimum): ");
        String minAgeStr = scanner.nextLine().trim();
        if (minAgeStr.isEmpty()) minAgeStr = null;
        
        System.out.print("Maximum age (leave empty for no maximum): ");
        String maxAgeStr = scanner.nextLine().trim();
        if (maxAgeStr.isEmpty()) maxAgeStr = null;
        
        List<Map<String, Object>> report = projectController.generateFilteredApplicantsReport(
                projectName, flatType, maritalStatus, minAgeStr, maxAgeStr);
        
        if (report.isEmpty()) {
            System.out.println("No applicants found matching the specified criteria.");
            return;
        }
        
        displayApplicantsReport(report);
    }
    
    /**
     * Display the applicants report
     * 
     * @param report List of applicant report entries
     */
    private void displayApplicantsReport(List<Map<String, Object>> report) {
        System.out.printf("%-20s %-20s %-10s %-15s %-15s %-10s\n", 
                "Project", "Applicant Name", "Age", "Marital Status", "Flat Type", "Income");
        System.out.println("---------------------------------------------------------------------------------");
        
        for (Map<String, Object> entry : report) {
            System.out.printf("%-20s %-20s %-10s %-15s %-15s $%-10s\n",
                    entry.get("projectName"),
                    entry.get("applicantName"),
                    entry.get("age"),
                    (Boolean) entry.get("married") ? "Married" : "Single",
                    entry.get("flatType"),
                    String.format("%.2f", (double) entry.get("income")));
        }
    }
    
    /**
     * Displays a success message
     * 
     * @param message The success message
     */
    private void displaySuccess(String message) {
        System.out.println("\n✅ " + message);
    }
    
    /**
     * Displays an error message
     * 
     * @param message The error message
     */
    private void displayError(String message) {
        System.out.println("\n❌ " + message);
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
    
    /**
     * Handles editing project details
     * 
     * @param manager The manager editing the project
     */
    private void editProjectDetails(HDBManager manager) {
        System.out.println("\n----- EDIT PROJECT DETAILS -----");
        
        // Show manager's projects
        List<Project> managerProjects = projectController.getProjectsByManager(manager);
        
        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects to edit.");
            return;
        }
        
        // Display manager's projects
        System.out.println("\nYour Projects:");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            if (!project.isDeleted()) {
                System.out.println((i + 1) + ". " + project.getName() + " (" + project.getNeighborhood() + ")");
            }
        }
        
        // Select project to edit
        System.out.print("\nSelect project to edit (1-" + managerProjects.size() + ") or 0 to cancel: ");
        int selection = readIntInput();
        
        if (selection < 1 || selection > managerProjects.size()) {
            if (selection == 0) {
                System.out.println("Operation cancelled.");
            } else {
                System.out.println("Invalid selection.");
            }
            return;
        }
        
        Project selectedProject = managerProjects.get(selection - 1);
        
        // Check if project is deleted
        if (selectedProject.isDeleted()) {
            System.out.println("Error: Project '" + selectedProject.getName() + "' is archived and cannot be modified.");
            return;
        }
        
        System.out.println("\nEditing project: " + selectedProject.getName());
        
        try {
            // Get current values for reference
            System.out.println("Current Neighborhood: " + selectedProject.getNeighborhood());
            System.out.println("Current Opening Date: " + dateFormat.format(selectedProject.getApplicationOpenDate()));
            System.out.println("Current Closing Date: " + dateFormat.format(selectedProject.getApplicationCloseDate()));
            System.out.println("Current Officer Slots: " + selectedProject.getAvailableOfficerSlots());
            
            System.out.println("\nEnter new details (leave blank to keep current value):");
            
            // Get new values
            System.out.print("New Neighborhood: ");
            String neighborhood = scanner.nextLine().trim();
            if (neighborhood.isEmpty()) {
                neighborhood = selectedProject.getNeighborhood();
            }
            
            System.out.print("New Application Opening Date (dd/MM/yyyy): ");
            String openDateStr = scanner.nextLine().trim();
            Date openDate = openDateStr.isEmpty() ? 
                selectedProject.getApplicationOpenDate() : parseDateOrDefault(openDateStr, selectedProject.getApplicationOpenDate());
            
            System.out.print("New Application Closing Date (dd/MM/yyyy): ");
            String closeDateStr = scanner.nextLine().trim();
            Date closeDate = closeDateStr.isEmpty() ? 
                selectedProject.getApplicationCloseDate() : parseDateOrDefault(closeDateStr, selectedProject.getApplicationCloseDate());
            
            System.out.print("New Available Officer Slots (max 10): ");
            String slotsStr = scanner.nextLine().trim();
            int slots = slotsStr.isEmpty() ? 
                selectedProject.getAvailableOfficerSlots() : parseIntOrDefault(slotsStr, selectedProject.getAvailableOfficerSlots());
            
            // Update the project
            String result = projectController.updateProject(
                selectedProject.getName(), neighborhood, dateFormat.format(openDate), 
                dateFormat.format(closeDate), String.valueOf(slots), manager
            );
            
            if (result.startsWith("Error:")) {
                displayError(result);
            } else {
                displaySuccess(result);
            }
        } catch (Exception e) {
            displayError("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    /**
     * Handles toggling project visibility
     * 
     * @param manager The manager toggling visibility
     */
    private void toggleProjectVisibility(HDBManager manager) {
        System.out.println("\n----- TOGGLE PROJECT VISIBILITY -----");
        
        // Show manager's projects
        List<Project> managerProjects = projectController.getProjectsByManager(manager);
        
        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects to modify.");
            return;
        }
        
        // Display manager's projects with visibility status
        System.out.println("\nYour Projects:");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            if (!project.isDeleted()) {
                String visibilityStatus = project.isVisible() ? "Visible" : "Hidden";
                System.out.println((i + 1) + ". " + project.getName() + " (" + visibilityStatus + ")");
            }
        }
        
        // Select project to toggle visibility
        System.out.print("\nSelect project to toggle visibility (1-" + managerProjects.size() + ") or 0 to cancel: ");
        int selection = readIntInput();
        
        if (selection < 1 || selection > managerProjects.size()) {
            if (selection == 0) {
                System.out.println("Operation cancelled.");
            } else {
                System.out.println("Invalid selection.");
            }
            return;
        }
        
        Project selectedProject = managerProjects.get(selection - 1);
        
        // Check if project is deleted
        if (selectedProject.isDeleted()) {
            System.out.println("Error: Project '" + selectedProject.getName() + "' is archived and cannot be modified.");
            return;
        }
        
        boolean currentVisibility = selectedProject.isVisible();
        
        System.out.println("\nProject: " + selectedProject.getName());
        System.out.println("Current visibility: " + (currentVisibility ? "Visible" : "Hidden"));
        
        // Toggle to opposite state
        String newVisibility = currentVisibility ? "off" : "on";
        
        System.out.print("Toggle visibility to " + (currentVisibility ? "Hidden" : "Visible") + "? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
            String result = projectController.toggleProjectVisibility(
                selectedProject.getName(), newVisibility, manager
            );
            
            if (result.startsWith("Error:")) {
                displayError(result);
            } else {
                displaySuccess(result);
            }
        } else {
            System.out.println("Operation cancelled.");
        }
    }
    
    /**
     * Helper method to parse date or return default if format is invalid
     * 
     * @param dateStr Date string to parse
     * @param defaultDate Default date to return if parsing fails
     * @return Parsed date or default date
     */
    private Date parseDateOrDefault(String dateStr, Date defaultDate) {
        try {
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            System.out.println("Invalid date format. Using existing value.");
            return defaultDate;
        }
    }
    
    /**
     * Helper method to parse integer or return default if format is invalid
     * 
     * @param intStr Integer string to parse
     * @param defaultValue Default value to return if parsing fails
     * @return Parsed integer or default value
     */
    private int parseIntOrDefault(String intStr, int defaultValue) {
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Using existing value.");
            return defaultValue;
        }
    }
    
    /**
     * Handles deleting (archiving) a project
     * 
     * @param manager The manager deleting the project
     */
    private void deleteProject(HDBManager manager) {
        System.out.println("\n----- DELETE/ARCHIVE PROJECT -----");
        System.out.println("Note: Deleted projects will be archived and can no longer be modified.");
        System.out.println("They will remain in the system for reference purposes but will not be visible to applicants.");
        
        // Show manager's projects
        List<Project> managerProjects = projectController.getProjectsByManager(manager);
        
        if (managerProjects.isEmpty()) {
            System.out.println("You don't have any projects to delete.");
            return;
        }
        
        // Display manager's projects
        System.out.println("\nYour Projects:");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            System.out.println((i + 1) + ". " + project.getName() + " (" + project.getNeighborhood() + ")");
        }
        
        // Select project to delete
        System.out.print("\nSelect project to delete (1-" + managerProjects.size() + ") or 0 to cancel: ");
        int selection = readIntInput();
        
        if (selection < 1 || selection > managerProjects.size()) {
            if (selection == 0) {
                System.out.println("Operation cancelled.");
            } else {
                System.out.println("Invalid selection.");
            }
            return;
        }
        
        Project selectedProject = managerProjects.get(selection - 1);
        
        // Check if the project has applications
        if (!selectedProject.getApplications().isEmpty()) {
            System.out.println("Error: Project '" + selectedProject.getName() + "' has applications and cannot be deleted.");
            return;
        }
        
        System.out.println("\nYou are about to delete project: " + selectedProject.getName());
        System.out.println("This operation cannot be undone.");
        System.out.print("Are you sure you want to proceed? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
            String result = projectController.softDeleteProject(selectedProject.getName(), manager);
            
            if (result.startsWith("Error:")) {
                displayError(result);
            } else {
                displaySuccess(result);
            }
        } else {
            System.out.println("Operation cancelled.");
        }
    }
    
    /**
     * View archived (deleted) projects created by the manager or all archived projects
     * 
     * @param manager The manager
     */
    private void viewArchivedProjects(HDBManager manager) {
        System.out.println("\n===== Archived Projects =====");
        System.out.println("1. View my archived projects");
        System.out.println("2. View all archived projects");
        System.out.println("3. Back");
        System.out.print("Enter your choice: ");
        
        int choice = readIntInput();
        
        switch (choice) {
            case 1:
                viewMyArchivedProjects(manager);
                break;
            case 2:
                viewAllArchivedProjects();
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice, please try again.");
        }
    }
    
    /**
     * View archived projects created by the manager
     * 
     * @param manager The manager
     */
    private void viewMyArchivedProjects(HDBManager manager) {
        List<Project> archivedProjects = projectController.getDeletedProjectsByManager(manager);
        
        if (archivedProjects.isEmpty()) {
            System.out.println("You don't have any archived projects.");
            return;
        }
        
        System.out.println("\n===== My Archived Projects =====");
        displayArchivedProjects(archivedProjects);
    }
    
    /**
     * View all archived projects in the system
     */
    private void viewAllArchivedProjects() {
        List<Project> archivedProjects = projectController.getAllDeletedProjects();
        
        if (archivedProjects.isEmpty()) {
            System.out.println("There are no archived projects in the system.");
            return;
        }
        
        System.out.println("\n===== All Archived Projects =====");
        displayArchivedProjects(archivedProjects);
    }
    
    /**
     * Display a list of archived projects
     * 
     * @param projects List of projects to display
     */
    private void displayArchivedProjects(List<Project> projects) {
        System.out.printf("%-20s %-15s %-15s %-15s %-10s %-15s\n", 
                "Project Name", "Start Date", "End Date", "Location", "Creator", "Archive Date");
        System.out.println("-------------------------------------------------------------------------------------------");
        
        for (Project project : projects) {
            System.out.printf("%-20s %-15s %-15s %-15s %-10s %-15s\n",
                    project.getName(),
                    dateFormat.format(project.getApplicationOpenDate()),
                    dateFormat.format(project.getApplicationCloseDate()),
                    project.getNeighborhood(),
                    project.getManager() != null ? project.getManager().getName() : "Unknown",
                    "N/A" // Archive date is not tracked currently
            );
        }
        
        System.out.println("\nNote: Archived projects cannot be modified or made visible to applicants.");
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
} 