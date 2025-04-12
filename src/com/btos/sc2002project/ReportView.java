package com.btos.sc2002project;

import java.util.List;
import java.util.Scanner;

public class ReportView {
	private final ReportController repCtrl;
	private final Scanner sc;
	public ReportView(ReportController repCtrl) {
		this.repCtrl = repCtrl;
		this.sc = new Scanner(System.in);
	}
	
	//ADD FILTERING
	public void generateReport(HDBManager user) {
		FilterSettings filter = promptReportFilter(user);
		
		List<Application> bookings = repCtrl.getBookedApplications(user, filter); //PASS FILTERSETTINGS AS PARAMETER???
		if (bookings.isEmpty()) {
	        System.out.println("No bookings found with selected filters.");
	    } else {
	        for (Application booking: bookings) {
	            System.out.println("#" + booking.getBookingID() + " " + booking.getProjectName());
	            System.out.println("User ID: " + booking.getUserID());
	            System.out.println("Marital Status: " + booking.getMaritalStatus());
	            System.out.println("Age: " + booking.getAge());
	            System.out.println("Flat Type: " + booking.getFlatType());
	            System.out.println("----------------------------");
	        }
	    }
	}
	
	private void promptReportFilter(HDBManager user) {
		FilterSettings reportFilter = user.getReportFilter();
	    boolean setting = true;

	    while (setting) {
	        System.out.println("---- Report Filter Settings ----");
	        System.out.println("1. Flat Type (number of rooms)");
	        System.out.println("2. Project Name");
	        System.out.println("3. Age");
	        System.out.println("4. Marital Status");
	        System.out.println("5. Reset All Filters");
	        System.out.println("6. Done");
	        System.out.print("Select filter option: ");
	        
	        int choice = -1;

	        try {
	            choice = Integer.parseInt(sc.nextLine());
	        } catch (NumberFormatException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            continue;
	        }

	        switch (choice) {
	            case 1:
	                System.out.print("Enter number of rooms: ");
	                reportFilter.setFlatType(sc.nextInt());
	                sc.nextLine();
	                break;
	            case 2:
	                System.out.print("Enter project name: ");
	                reportFilter.setProjectName(sc.nextLine());
	                break;
	            case 3:
	                System.out.print("Enter age: ");
	                reportFilter.setMinAge(sc.nextInt());
	                sc.nextLine();
	                break;
	            case 4:
	                System.out.print("Enter marital status (e.g. Single, Married): ");
	                reportFilter.setMaritalStatus(sc.nextLine());
	                break;
	            case 5:
	            	reportFilter.resetToDefault();
	                System.out.println("Filters cleared.");
	                break;
	            case 6:
	                setting = false;
	                System.out.println("Returning to report menu...");
	                break;
	            default:
	                System.out.println("Invalid option.");
	        }
	    }
	    return reportFilter;
	}
}
