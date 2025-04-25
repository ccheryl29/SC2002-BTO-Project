package view;

import controller.ApplicantEnquiryController;
import controller.ApplicantProjectController;
import model.Applicant;
import model.Enquiry;
import model.Project;

import java.util.List;
import java.util.Scanner;

/**
 * View class for handling enquiry operations for applicants
 */
public class ApplicantEnquiryView {
    private final ApplicantEnquiryController enquiryController;
    private final ApplicantProjectController projectController;
    private final Scanner scanner;
    private Applicant currentApplicant;

    /**
     * Constructor for ApplicantEnquiryView
     * 
     * @param enquiryController The enquiry controller
     * @param projectController The project controller for project related operations
     */
    public ApplicantEnquiryView(ApplicantEnquiryController enquiryController, ApplicantProjectController projectController) {
        this.enquiryController = enquiryController;
        this.projectController = projectController;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Sets the current applicant
     * 
     * @param applicant The currently logged-in applicant
     */
    public void setCurrentApplicant(Applicant applicant) {
        this.currentApplicant = applicant;
    }

    /**
     * Displays the main enquiry menu and handles user selection
     */
    public void displayEnquiryMenu() {
        if (currentApplicant == null) {
            System.out.println("Error: No applicant logged in.");
            return;
        }
        
        int choice;
        do {
            System.out.println("\n==== Enquiry Management ====");
            System.out.println("1. Submit new enquiry");
            System.out.println("2. View my enquiries");
            System.out.println("3. Update an enquiry");
            System.out.println("4. Delete an enquiry");
            System.out.println("0. Return to main menu");
            System.out.print("Enter your choice: ");
            
            choice = getIntInput();
            
            switch (choice) {
                case 1:
                    submitEnquiry();
                    break;
                case 2:
                    viewMyEnquiries();
                    break;
                case 3:
                    updateEnquiry();
                    break;
                case 4:
                    deleteEnquiry();
                    break;
                case 0:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    /**
     * Handles submission of a new enquiry
     */
    private void submitEnquiry() {
        System.out.println("\n==== Submit New Enquiry ====");
        
        // Get available projects
        List<Project> availableProjects = enquiryController.getAvailableProjects();
        if (availableProjects.isEmpty()) {
            System.out.println("No projects are available for enquiries at this time.");
            return;
        }
        
        // Display available projects
        System.out.println("Available Projects:");
        for (int i = 0; i < availableProjects.size(); i++) {
            System.out.println((i + 1) + ". " + availableProjects.get(i).getName());
        }
        
        // Get project selection
        System.out.print("Select a project (1-" + availableProjects.size() + "): ");
        int projectChoice = getIntInput();
        
        if (projectChoice < 1 || projectChoice > availableProjects.size()) {
            System.out.println("Invalid project selection.");
            return;
        }
        
        Project selectedProject = availableProjects.get(projectChoice - 1);
        
        // Get enquiry question
        System.out.println("Enter your enquiry about " + selectedProject.getName() + ":");
        scanner.nextLine(); // Clear buffer
        String question = scanner.nextLine();
        
        if (question.trim().isEmpty()) {
            System.out.println("Enquiry cannot be empty.");
            return;
        }
        
        // Submit the enquiry
        Enquiry enquiry = enquiryController.submitEnquiry(currentApplicant,selectedProject.getName(), question);
        
        if (enquiry != null) {
            System.out.println("Enquiry submitted successfully. Enquiry ID: " + enquiry.getId());
        } else {
            System.out.println("Failed to submit enquiry. Please try again later.");
        }
    }

    /**
     * Displays all enquiries submitted by the current applicant
     */
    private void viewMyEnquiries() {
        System.out.println("\n==== My Enquiries ====");
        
        List<Enquiry> myEnquiries = enquiryController.getMyEnquiries(currentApplicant);
        
        if (myEnquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries yet.");
            return;
        }
        
        for (int i = 0; i < myEnquiries.size(); i++) {
            Enquiry enquiry = myEnquiries.get(i);
            System.out.println("\nEnquiry #" + (i + 1));
            System.out.println("ID: " + enquiry.getId());
            System.out.println("Project: " + enquiry.getProject().getName());
            System.out.println("Date Submitted: " + enquiry.getDateSubmitted());
            System.out.println("Question: " + enquiry.getQuestion());
            System.out.println("Status: " + enquiry.getStatus());
            
            if (enquiry.isResponded()) {
                System.out.println("Replies (" + enquiry.getReplies().size() + "):");
                List<Enquiry.Reply> replies = enquiry.getReplies();
                for (int j = 0; j < replies.size(); j++) {
                    Enquiry.Reply reply = replies.get(j);
                    System.out.println("  " + (j + 1) + ". " + reply.getContent());
                    System.out.println("     By: " + reply.getRespondedBy().getName() + " on " + reply.getDateResponded());
                }
            } else {
                System.out.println("No replies yet.");
            }
            System.out.println("---------------------");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Handles updating an existing enquiry
     */
    private void updateEnquiry() {
        System.out.println("\n==== Update Enquiry ====");
        
        List<Enquiry> myEnquiries = enquiryController.getMyEnquiries(currentApplicant);
        
        if (myEnquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries yet.");
            return;
        }
        
        // Only show pending enquiries (those without replies) as they can be updated
        List<Enquiry> pendingEnquiries = myEnquiries.stream()
                .filter(e -> !e.isResponded())
                .toList();
        
        if (pendingEnquiries.isEmpty()) {
            System.out.println("You don't have any pending enquiries that can be updated.");
            return;
        }
        
        // Display pending enquiries
        System.out.println("Your pending enquiries that can be updated:");
        for (int i = 0; i < pendingEnquiries.size(); i++) {
            Enquiry enquiry = pendingEnquiries.get(i);
            System.out.println((i + 1) + ". [" + enquiry.getId() + "] " + enquiry.getProject().getName() + 
                    " - " + truncateText(enquiry.getQuestion(), 50));
        }
        
        // Get enquiry selection
        System.out.print("Select an enquiry to update (1-" + pendingEnquiries.size() + ") or 0 to cancel: ");
        int enquiryChoice = getIntInput();
        
        if (enquiryChoice == 0) {
            return;
        }
        
        if (enquiryChoice < 1 || enquiryChoice > pendingEnquiries.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Enquiry selectedEnquiry = pendingEnquiries.get(enquiryChoice - 1);
        
        // Get new question
        System.out.println("Current question: " + selectedEnquiry.getQuestion());
        System.out.println("Enter new question:");
        scanner.nextLine(); // Clear buffer
        String newQuestion = scanner.nextLine();
        
        if (newQuestion.trim().isEmpty()) {
            System.out.println("New question cannot be empty.");
            return;
        }
        
        // Update the enquiry
        boolean success = enquiryController.updateEnquiry(selectedEnquiry.getId(), newQuestion);
        
        if (success) {
            System.out.println("Enquiry updated successfully.");
        } else {
            System.out.println("Failed to update enquiry. It may have been responded to already.");
        }
    }

    /**
     * Handles deletion of an existing enquiry
     */
    private void deleteEnquiry() {
        System.out.println("\n==== Delete Enquiry ====");
        
        List<Enquiry> myEnquiries = enquiryController.getMyEnquiries(currentApplicant);
        
        if (myEnquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries yet.");
            return;
        }
        
        // Only show pending enquiries (those without replies) as they can be deleted
        List<Enquiry> pendingEnquiries = myEnquiries.stream()
                .filter(e -> !e.isResponded())
                .toList();
        
        if (pendingEnquiries.isEmpty()) {
            System.out.println("You don't have any pending enquiries that can be deleted.");
            return;
        }
        
        // Display pending enquiries
        System.out.println("Your pending enquiries that can be deleted:");
        for (int i = 0; i < pendingEnquiries.size(); i++) {
            Enquiry enquiry = pendingEnquiries.get(i);
            System.out.println((i + 1) + ". [" + enquiry.getId() + "] " + enquiry.getProject().getName() + 
                    " - " + truncateText(enquiry.getQuestion(), 50));
        }
        
        // Get enquiry selection
        System.out.print("Select an enquiry to delete (1-" + pendingEnquiries.size() + ") or 0 to cancel: ");
        int enquiryChoice = getIntInput();
        
        if (enquiryChoice == 0) {
            return;
        }
        
        if (enquiryChoice < 1 || enquiryChoice > pendingEnquiries.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Enquiry selectedEnquiry = pendingEnquiries.get(enquiryChoice - 1);
        
        // Confirm deletion
        System.out.print("Are you sure you want to delete this enquiry? (y/n): ");
        String confirm = scanner.next();
        
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }
        
        // Delete the enquiry
        boolean success = enquiryController.deleteEnquiry(selectedEnquiry.getId());
        
        if (success) {
            System.out.println("Enquiry deleted successfully.");
        } else {
            System.out.println("Failed to delete enquiry. It may have been responded to already.");
        }
    }

    /**
     * Helper method to truncate text for display
     */
    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Helper method to get integer input from the user
     */
    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next(); // Consume invalid input
        }
        return scanner.nextInt();
    }
} 