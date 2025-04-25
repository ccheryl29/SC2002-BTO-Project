package view;

import controller.HDBManagerEnquiryController;
import controller.HDBManagerProjectController;
import model.Enquiry;
import model.HDBManager;

import java.util.List;
import java.util.Scanner;

/**
 * View class for handling enquiry operations for HDB Managers
 */
public class HDBManagerEnquiryView {
    
    private final HDBManagerEnquiryController enquiryController;
    private final HDBManagerProjectController projectController;
    private final Scanner scanner;
    private HDBManager currentManager;
    
    /**
     * Constructor for HDBManagerEnquiryView
     * 
     * @param enquiryController The enquiry controller
     * @param projectController The project controller for project related operations
     */
    public HDBManagerEnquiryView(HDBManagerEnquiryController enquiryController, 
                                 HDBManagerProjectController projectController) {
        this.enquiryController = enquiryController;
        this.projectController = projectController;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Sets the current manager
     * 
     * @param manager The manager
     */
    public void setCurrentManager(HDBManager manager) {
        this.currentManager = manager;
    }
    
    /**
     * Displays the main menu for enquiry management and handles user selection
     * 
     * @param manager The current HDB manager
     */
    public void displayMenu(HDBManager manager) {
        if (manager == null) {
            System.out.println("Error: No manager logged in.");
            return;
        }
        
        setCurrentManager(manager);
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
     * Displays all enquiries for the manager's project
     */
    private void viewAllEnquiries() {
        if (currentManager.getHandlingProject() == null) {
            System.out.println("You are not currently assigned to any project.");
            return;
        }
        
        String projectName = currentManager.getHandlingProject().getName();
        System.out.println("\n===== ALL ENQUIRIES FOR " + projectName.toUpperCase() + " =====");
        
        List<Enquiry> enquiries = enquiryController.getEnquiriesByProject(projectName);
        
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found for this project.");
            return;
        }
        
        displayEnquiries(enquiries);
    }
    
    /**
     * Displays pending enquiries for the manager's project
     */
    private void viewPendingEnquiries() {
        if (currentManager.getHandlingProject() == null) {
            System.out.println("You are not currently assigned to any project.");
            return;
        }
        
        String projectName = currentManager.getHandlingProject().getName();
        System.out.println("\n===== PENDING ENQUIRIES FOR " + projectName.toUpperCase() + " =====");
        
        List<Enquiry> pendingEnquiries = enquiryController.getPendingEnquiries(projectName);
        
        if (pendingEnquiries.isEmpty()) {
            System.out.println("No pending enquiries found for this project.");
            return;
        }
        
        displayEnquiries(pendingEnquiries);
    }
    
    /**
     * Displays a list of enquiries
     * 
     * @param enquiries The list of enquiries to display
     */
    private void displayEnquiries(List<Enquiry> enquiries) {
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println("\nEnquiry #" + (i + 1));
            System.out.println("ID: " + enquiry.getId());
            System.out.println("Applicant: " + enquiry.getApplicant().getName());
            System.out.println("Date Submitted: " + enquiry.getDateSubmitted());
            System.out.println("Status: " + enquiry.getStatus());
            System.out.println("Question: " + enquiry.getQuestion());
            
            if (enquiry.isResponded()) {
                System.out.println("\nReplies:");
                List<Enquiry.Reply> replies = enquiry.getReplies();
                for (int j = 0; j < replies.size(); j++) {
                    Enquiry.Reply reply = replies.get(j);
                    System.out.println("  " + (j + 1) + ". " + reply.getContent());
                    System.out.println("     By: " + reply.getRespondedBy().getName() + " on " + reply.getDateResponded());
                }
            } else {
                System.out.println("\nStatus: Awaiting Reply");
            }
            System.out.println("------------------------------");
        }
        
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Handles replying to an enquiry
     */
    private void replyToEnquiry() {
        if (currentManager.getHandlingProject() == null) {
            System.out.println("You are not currently assigned to any project.");
            return;
        }
        
        String projectName = currentManager.getHandlingProject().getName();
        System.out.println("\n===== REPLY TO ENQUIRY FOR " + projectName.toUpperCase() + " =====");
        
        List<Enquiry> pendingEnquiries = enquiryController.getPendingEnquiries(projectName);
        
        if (pendingEnquiries.isEmpty()) {
            System.out.println("No pending enquiries found for this project.");
            return;
        }
        
        System.out.println("Pending Enquiries:");
        for (int i = 0; i < pendingEnquiries.size(); i++) {
            Enquiry enquiry = pendingEnquiries.get(i);
            System.out.println((i + 1) + ". [" + enquiry.getId() + "] From: " + 
                    enquiry.getApplicant().getName() + " - " + truncateText(enquiry.getQuestion(), 50));
        }
        
        System.out.print("\nSelect an enquiry to reply to (1-" + pendingEnquiries.size() + ") or 0 to cancel: ");
        int choice = readIntInput();
        
        if (choice <= 0 || choice > pendingEnquiries.size()) {
            System.out.println("Operation cancelled or invalid selection.");
            return;
        }
        
        Enquiry selectedEnquiry = pendingEnquiries.get(choice - 1);
        
        System.out.println("\nEnquiry ID: " + selectedEnquiry.getId());
        System.out.println("From: " + selectedEnquiry.getApplicant().getName());
        System.out.println("Question: " + selectedEnquiry.getQuestion());
        
        System.out.println("\nEnter your reply:");
        String reply = scanner.nextLine();
        
        if (reply.trim().isEmpty()) {
            System.out.println("Reply cannot be empty. Operation cancelled.");
            return;
        }
        
        boolean success = enquiryController.addReply(selectedEnquiry.getId(), currentManager, reply);
        
        if (success) {
            System.out.println("Reply sent successfully!");
        } else {
            System.out.println("Failed to send reply. Please try again later.");
        }
    }
    
    /**
     * Displays statistics about enquiries for the manager's project
     */
    private void viewEnquiryStatistics() {
        if (currentManager.getHandlingProject() == null) {
            System.out.println("You are not currently assigned to any project.");
            return;
        }
        
        String projectName = currentManager.getHandlingProject().getName();
        System.out.println("\n===== ENQUIRY STATISTICS FOR " + projectName.toUpperCase() + " =====");
        
        int totalEnquiries = enquiryController.getEnquiryCount(projectName);
        int pendingEnquiries = enquiryController.getPendingEnquiryCount(projectName);
        int answeredEnquiries = totalEnquiries - pendingEnquiries;
        
        System.out.println("Total Enquiries: " + totalEnquiries);
        System.out.println("Pending Enquiries: " + pendingEnquiries);
        System.out.println("Answered Enquiries: " + answeredEnquiries);
        
        if (totalEnquiries > 0) {
            double responseRate = ((double) answeredEnquiries / totalEnquiries) * 100;
            System.out.printf("Response Rate: %.1f%%\n", responseRate);
        }
        
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
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