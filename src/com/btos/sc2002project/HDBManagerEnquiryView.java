package com.btos.sc2002project;

import java.util.Scanner;
import java.util.List;

public class HDBManagerEnquiryView {
	private final HDBManagerEnquiryController enqCtrl;
	private final HDBManagerDashboard dashboard;
	private final Scanner sc;
	
	public HDBManagerEnquiryView(HDBManagerEnquiryController enqCtrl,
			HDBManagerDashboard dashboard) {
		this.enqCtrl = enqCtrl;
		this.dashboard = dashboard;
		this.sc = new Scanner(System.in);
	}
	
	public void displayEnquiryMenu(HDBManager user) {
		System.out.println("--------Enquiry Menu--------");
		System.out.println("1. View All Enquiries");
		System.out.println("2. Reply Project Enquiries");
		System.out.print("\nEnter your choice: ");
		int choice = sc.nextInt();
		if (choice == 1) {
			viewAllEnquiries();
		} else if (choice == 2) {
			replyEnquiries(user);
		} else { System.out.println("Invalid choice."); }
	}
	
	public void viewAllEnquiries(HDBManager user) {
		List<Enquiry> enquiries = enqCtrl.getAllEnquiries();
		if (!enquiries) {
			System.out.println("No enquiry found.");
		} else {
			System.out.println("ALL ENQUIRIES");
			for (Enquiry enquiry: enquiries) {
				System.out.println("#"+enquiry.getEnquiryID()+" "+enquiry.getProject());
				System.out.println("Manager-in-charge: "+enquiry.getProject().getManager().getName());
				System.out.println(enquiry.getMessage());
				System.out.println("----------------------------------");
			}
		}
		System.out.println("Press <ENTER> to return to home page  ");
		sc.nextLine();
		sc.nextLine();
		returnToDashboard(user);
	}
	
	public void replyEnquiries(HDBManager user) {
		List<Enquiry> enquiries = enqCtrl.getEnquiries(user);
		
		if (!enquiries) {
			System.out.println("No enquiry found.");
		} else {
			System.out.println("Your Project Enquiries:");
			for (Enquiry enquiry: enquiries) {
				System.out.println("#"+enquiry.getEnquiryID()+" "+enquiry.getProject());
				System.out.println(enquiry.getMessage());
				System.out.println("----------------------------------");
			}
			while (true) {
				System.out.print("\nEnter enquiry ID to reply: ");
				String enqID = sc.next();
				System.out.println("Type Reply: ");
				String reply = sc.next();
				boolean replyEnqSuccess = enqCtrl.replyToEnquiry(user, enqID, reply);
				if (replyEnqSuccess == true) {
					System.out.println("Reply sent! Returning to homepage...");
					returnToDashboard(user);
					break;
				} else {
					System.out.println("Failed to send reply. Please try again.");
				}
				displayEnquiryMenu(user);
			}
		}
	}
	
	private void returnToDashboard(HDBManager user) {
		dashboard.displayUserMenu(user);
	}
}
