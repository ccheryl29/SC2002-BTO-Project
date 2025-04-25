package view;

import controller.OfficerEnquiryController;
import model.Enquiry;
import model.HDBOfficer;
import model.Project;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

/**
 * View class for handling enquiry operations for HDB Officers
 */
public class OfficerEnquiryView {
    
    private final OfficerEnquiryController enquiryController;
    private final Scanner scanner;
    private HDBOfficer currentOfficer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    /**
     * Constructor for OfficerEnquiryView
     * 
     * @param enquiryController The enquiry controller
     */
    public OfficerEnquiryView(OfficerEnquiryController enquiryController) {
        this.enquiryController = enquiryController;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Sets the current officer
     * 
     * @param officer The officer
     */
    public void setCurrentOfficer(HDBOfficer officer) {
        this.currentOfficer = officer;
    }
    
    /**
     * Displays the main menu for enquiry management and handles user selection
     */
    public void displayMenu() {
        if (currentOfficer == null) {
            System.out.println("Error: No officer logged in.");
            return;
        }
        
        if (currentOfficer.getHandlingProject() == null) {
            System.out.println("You are not currently handling any project.");
            System.out.println("Please register for a project first.");
            return;
        }
        
        boolean back = false;
        
        while (!back) {
            System.out.println("\n===== ENQUIRY MANAGEMENT =====");
            System.out.println("1. View All Enquiries for My Project");
            System.out.println("2. View Pending Enquiries");
            System.out.println("3. Reply to an Enquiry");
            System.out.println("4. View Enquiry Statistics");
            System.out.println("5. Back to Dashboard");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    viewAllEnquiries();
                    break;
                case 2:
                    viewPendingEnquiries();
                    break;
                case 3:
                    replyToEnquiry();
                    break;
                case 4:
                    viewEnquiryStatistics();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
    
    /**
     * Displays all enquiries for the officer's handling project
     */
    private void viewAllEnquiries() {
        Project handlingProject = currentOfficer.getHandlingProject();
        System.out.println("\n===== ALL ENQUIRIES FOR " + handlingProject.getName().toUpperCase() + " =====");
        
        try {
            List<Enquiry> enquiries = enquiryController.getEnquiriesForHandlingProject(currentOfficer);
            
            if (enquiries.isEmpty()) {
                System.out.println("No enquiries found for this project.");
                return;
            }
            
            displayEnquiries(enquiries);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Displays pending enquiries for the officer's handling project
     */
    private void viewPendingEnquiries() {
        Project handlingProject = currentOfficer.getHandlingProject();
        System.out.println("\n===== PENDING ENQUIRIES FOR " + handlingProject.getName().toUpperCase() + " =====");
        
        try {
            List<Enquiry> pendingEnquiries = enquiryController.getPendingEnquiriesForHandlingProject(currentOfficer);
            
            if (pendingEnquiries.isEmpty()) {
                System.out.println("No pending enquiries found for this project.");
                return;
            }
            
            displayEnquiries(pendingEnquiries);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Displays a list of enquiries
     * 
     * @param enquiries The list of enquiries to display
     */
    private void displayEnquiries(List<Enquiry> enquiries) {
        System.out.println("\n-----------------------------------------------------------------");
        System.out.printf("%-10s %-15s %-25s %-15s\n", "ID", "Applicant", "Date", "Status");
        System.out.println("-----------------------------------------------------------------");
        
        for (Enquiry enquiry : enquiries) {
            System.out.printf("%-10s %-15s %-25s %-15s\n",
                    enquiry.getId().substring(0, Math.min(8, enquiry.getId().length())),
                    enquiry.getApplicant().getName(),
                    dateFormat.format(enquiry.getDateSubmitted()),
                    enquiry.isResponded() ? "Responded" : "Pending");
        }
        
        System.out.println("-----------------------------------------------------------------");
        System.out.println("\nEnter enquiry ID to view details (or 'back' to return): ");
        String input = scanner.nextLine().trim();
        
        if (!input.equalsIgnoreCase("back")) {
            viewEnquiryDetails(input);
        }
    }
    
    /**
     * Displays details of a specific enquiry
     * 
     * @param enquiryId The ID of the enquiry
     */
    private void viewEnquiryDetails(String enquiryId) {
        try {
            Enquiry enquiry = enquiryController.getEnquiryById(enquiryId);
            
            if (enquiry == null) {
                System.out.println("Enquiry not found with ID: " + enquiryId);
                return;
            }
            
            // Check if this enquiry is for the officer's handling project
            if (!enquiry.getProject().getName().equals(currentOfficer.getHandlingProject().getName())) {
                System.out.println("This enquiry is not related to your handling project.");
                return;
            }
            
            System.out.println("\n===== ENQUIRY DETAILS =====");
            System.out.println("ID: " + enquiry.getId());
            System.out.println("Applicant: " + enquiry.getApplicant().getName());
            System.out.println("Date Submitted: " + dateFormat.format(enquiry.getDateSubmitted()));
            System.out.println("Project: " + enquiry.getProject().getName());
            System.out.println("Status: " + (enquiry.isResponded() ? "Responded" : "Pending"));
            System.out.println("\nQuestion:");
            System.out.println(enquiry.getQuestion());
            
            if (enquiry.isResponded()) {
                Enquiry.Reply latestReply = enquiry.getLatestReply();
                System.out.println("\nLatest Response:");
                System.out.println(latestReply.getContent());
                System.out.println("Responded By: " + latestReply.getRespondedBy().getName());
                System.out.println("Response Date: " + dateFormat.format(latestReply.getDateResponded()));
                
                if (enquiry.getReplies().size() > 1) {
                    System.out.println("\nThis enquiry has " + enquiry.getReplies().size() + " replies in total.");
                    System.out.println("Enter 'all' to view all replies, or any other key to continue: ");
                    String input = scanner.nextLine().trim();
                    
                    if (input.equalsIgnoreCase("all")) {
                        displayAllReplies(enquiry);
                    }
                }
            } else {
                System.out.println("\nThis enquiry has not been responded to yet.");
                System.out.println("Would you like to respond now? (Y/N): ");
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("Y")) {
                    respondToEnquiry(enquiry);
                }
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Displays all replies for an enquiry
     * 
     * @param enquiry The enquiry
     */
    private void displayAllReplies(Enquiry enquiry) {
        List<Enquiry.Reply> replies = enquiry.getReplies();
        System.out.println("\n===== ALL REPLIES =====");
        
        for (int i = 0; i < replies.size(); i++) {
            Enquiry.Reply reply = replies.get(i);
            System.out.println("Reply #" + (i + 1) + " - " + dateFormat.format(reply.getDateResponded()));
            System.out.println("From: " + reply.getRespondedBy().getName());
            System.out.println("Content: " + reply.getContent());
            System.out.println("-------------------");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Interface for replying to an enquiry
     */
    private void replyToEnquiry() {
        System.out.println("\n===== REPLY TO ENQUIRY =====");
        System.out.print("Enter enquiry ID: ");
        String enquiryId = scanner.nextLine().trim();
        
        try {
            Enquiry enquiry = enquiryController.getEnquiryById(enquiryId);
            
            if (enquiry == null) {
                System.out.println("Enquiry not found with ID: " + enquiryId);
                return;
            }
            
            // Check if this enquiry is for the officer's handling project
            if (!enquiry.getProject().getName().equals(currentOfficer.getHandlingProject().getName())) {
                System.out.println("This enquiry is not related to your handling project.");
                return;
            }
            
            if (enquiry.isResponded()) {
                Enquiry.Reply latestReply = enquiry.getLatestReply();
                System.out.println("This enquiry has already been responded to by " + 
                        latestReply.getRespondedBy().getName() + " on " + 
                        dateFormat.format(latestReply.getDateResponded()));
                
                System.out.println("Current response: " + latestReply.getContent());
                
                System.out.println("Would you like to add another reply? (Y/N): ");
                String input = scanner.nextLine().trim();
                
                if (!input.equalsIgnoreCase("Y")) {
                    return;
                }
            }
            
            respondToEnquiry(enquiry);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Adds a response to an enquiry
     * 
     * @param enquiry The enquiry to respond to
     */
    private void respondToEnquiry(Enquiry enquiry) {
        System.out.println("\nEnquiry Question:");
        System.out.println(enquiry.getQuestion());
        
        System.out.println("\nEnter your response:");
        String response = scanner.nextLine().trim();
        
        if (response.isEmpty()) {
            System.out.println("Response cannot be empty. Operation cancelled.");
            return;
        }
        
        try {
            boolean success = enquiryController.addReply(enquiry.getId(), currentOfficer, response);
            
            if (success) {
                System.out.println("Response added successfully!");
            } else {
                System.out.println("Failed to add response. Please try again.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Displays enquiry statistics for the officer's handling project
     */
    private void viewEnquiryStatistics() {
        if (currentOfficer.getHandlingProject() == null) {
            System.out.println("You are not currently handling any project.");
            return;
        }
        
        System.out.println("\n===== ENQUIRY STATISTICS =====");
        System.out.println("Project: " + currentOfficer.getHandlingProject().getName());
        
        try {
            int totalEnquiries = enquiryController.getEnquiryCount(currentOfficer);
            int pendingEnquiries = enquiryController.getPendingEnquiryCount(currentOfficer);
            int respondedEnquiries = totalEnquiries - pendingEnquiries;
            
            System.out.println("Total Enquiries: " + totalEnquiries);
            System.out.println("Pending Enquiries: " + pendingEnquiries);
            System.out.println("Responded Enquiries: " + respondedEnquiries);
            
            if (totalEnquiries > 0) {
                double respondedPercentage = (double) respondedEnquiries / totalEnquiries * 100;
                System.out.printf("Response Rate: %.1f%%\n", respondedPercentage);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
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
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // Invalid input
        }
    }
} 