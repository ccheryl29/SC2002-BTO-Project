package com.btos.views;
import java.util.Scanner;
import java.util.List;

public class HDBOfficerEnquiryView {
	private final HDBOfficerEnquiryController offEnqCtrl;
	private final HDBOfficerDashboard dashboard;
	private final Scanner sc;
	
	public HDBOfficerEnquiryView(HDBOfficerEnquiryController offEnqCtrl,
			HDBOfficerDashboard dashboard) {
		this.offEnqCtrl = offEnqCtrl;
		this.dashboard = dashboard;
		this.sc = new Scanner(System.in);
	}
	
	public void displayEnquiries(HDBOfficer user) {
		System.out.println("-----------Enquiry Menu-----------");
		List<Enquiry> enquiries = offEnqCtrl.getEnquiries(user);
		
		if (!enquiries) {
			System.out.println("No enquiry found.");
		} else {
			for (Enquiry enquiry: enquiries) {
				System.out.println("#"+enquiry.getEnquiryID()+" "+enquiry.getProject());
				System.out.println(enquiry.getMessage());
				System.out.println("----------------------------------");
			}
			
			String boolReply = readYesNo("Reply to enquiry? (Y/N): ");
			if (boolReply.equalsIgnoreCase("Y")) {
				displayEnquiryForm(user);
			} else {
				System.out.println("Returning to home page...");
				returnToDashboard(user);
			}
		}
	}
	
	//input validation utility methods (reusable)
	public String readYesNo(String prompt) {
	    while (true) {
	        System.out.print(prompt);
	        String input = sc.nextLine().trim();
	        if (input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("N")) {
	            return input.toUpperCase();
	        } else {
	            System.out.println("Please enter Y or N.");
	        }
	    }
	}
	
	public void displayEnquiryForm(HDBOfficer user) {
		System.out.print("\nEnter enquiry ID to reply: ");
		String enqID = sc.next();
		System.out.println("Type Reply: ");
		String reply = sc.next();
		boolean replyEnqSuccess = offEnqCtrl.replyToEnquiry(user, enqID, reply);
		if (replyEnqSuccess == true) { //print success message
			System.out.println("Reply sent! Returning to homepage...");
			returnToDashboard(user);			
		} else {
			System.out.println("Failed to send reply. Please try again.");
		}
		displayEnquiries(user);
	}
	
	private void returnToDashboard(HDBOfficer user) {
		dashboard.displayUserMenu(user);
	}
}
