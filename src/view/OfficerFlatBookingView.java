package view;

import controller.OfficerFlatBookingController;
import model.Application;
import model.HDBOfficer;
import model.Project;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * View class for HDB Officer flat booking interactions
 */
public class OfficerFlatBookingView {
    
    private final Scanner scanner;
    private final OfficerFlatBookingController bookingController;
    private HDBOfficer currentOfficer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Constructor for OfficerFlatBookingView
     * 
     * @param bookingController The booking controller
     */
    public OfficerFlatBookingView(OfficerFlatBookingController bookingController) {
        this.scanner = new Scanner(System.in);
        this.bookingController = bookingController;
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
     * Displays the flat booking menu for officers
     */
    public void displayMenu() {
        if (currentOfficer == null) {
            System.out.println("Error: No officer logged in.");
            return;
        }
        
        // Check if officer is handling a project
        if (currentOfficer.getHandlingProject() == null) {
            System.out.println("You are not currently handling any project.");
            System.out.println("You need to be assigned to a project before you can manage flat bookings.");
            System.out.println("Press Enter to return to the dashboard...");
            scanner.nextLine();
            return;
        }
        
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n===== FLAT BOOKING MANAGEMENT =====");
            System.out.println("Project: " + currentOfficer.getHandlingProject().getName());
            System.out.println("1. View Pending Booking Requests");
            System.out.println("2. Complete a Booking");
            System.out.println("3. Generate Booking Receipt");
            System.out.println("4. Back to Dashboard");
            
            System.out.print("Enter your choice: ");
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    viewPendingBookings();
                    break;
                case 2:
                    completeBooking();
                    break;
                case 3:
                    generateBookingReceipt();
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
     * View pending booking requests for the officer's project
     */
    private void viewPendingBookings() {
        System.out.println("\n===== PENDING BOOKING REQUESTS =====");
        
        String projectName = currentOfficer.getHandlingProject().getName();
        List<Application> pendingBookings = bookingController.getPendingBookingRequests(projectName);
        
        if (pendingBookings.isEmpty()) {
            System.out.println("No pending booking requests for your project.");
        } else {
            System.out.println("Pending Booking Requests:");
            System.out.printf("%-20s %-15s %-20s\n", "Applicant", "NRIC", "Flat Type");
            System.out.println("-----------------------------------------------------");
            
            for (Application app : pendingBookings) {
                System.out.printf("%-20s %-15s %-20s\n", 
                    app.getApplicant().getName(),
                    app.getApplicant().getNRIC(),
                    app.getFlatType().getDisplayName());
            }
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Complete a flat booking request
     */
    private void completeBooking() {
        System.out.println("\n===== COMPLETE BOOKING =====");
        
        // Get the project name
        String projectName = currentOfficer.getHandlingProject().getName();
        
        // Get applicant NRIC
        System.out.print("Enter applicant NRIC: ");
        String nric = scanner.nextLine().trim();
        
        if (nric.isEmpty()) {
            System.out.println("NRIC cannot be empty.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Confirm the action
        System.out.println("\nYou are about to complete the booking for:");
        System.out.println("Applicant NRIC: " + nric);
        System.out.println("Project: " + projectName);
        
        System.out.print("\nConfirm? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (!confirm.equals("Y")) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        // Process the booking
        String result = bookingController.completeBooking(nric, projectName, currentOfficer);
        System.out.println(result);
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Generate a receipt for a completed booking
     */
    private void generateBookingReceipt() {
        System.out.println("\n===== GENERATE BOOKING RECEIPT =====");
        
        // Get the project name
        String projectName = currentOfficer.getHandlingProject().getName();
        
        // Get applicant NRIC
        System.out.print("Enter applicant NRIC: ");
        String nric = scanner.nextLine().trim();
        
        if (nric.isEmpty()) {
            System.out.println("NRIC cannot be empty.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Get the receipt
        Map<String, Object> receipt = bookingController.generateBookingReceipt(nric, projectName);
        
        if (receipt.containsKey("error")) {
            System.out.println("Error: " + receipt.get("error"));
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Display the receipt
        System.out.println("\n========== BOOKING RECEIPT ==========");
        System.out.println("Applicant Name: " + receipt.get("applicantName"));
        System.out.println("NRIC: " + receipt.get("nric"));
        System.out.println("Age: " + receipt.get("age"));
        System.out.println("Marital Status: " + receipt.get("maritalStatus"));
        System.out.println("-------------------------------------");
        System.out.println("Project: " + receipt.get("projectName"));
        System.out.println("Neighborhood: " + receipt.get("neighborhood"));
        System.out.println("Flat Type: " + receipt.get("flatType"));
        System.out.println("Price: $" + receipt.get("price"));
        System.out.println("Booking Date: " + dateFormat.format(receipt.get("bookingDate")));
        System.out.println("======================================");
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
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