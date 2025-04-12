package com.btos.views;
import java.util.Scanner;
import java.util.List;

public class ApplicantEnquiryView {
	private final ApplicantEnquiryController enqCtrl;
	private final ApplicantDashboard dashboard;
	private final Scanner sc;
	
	public ApplicantEnquiryView(ApplicantEnquiryController enqCtrl) {
		this.enqCtrl = enqCtrl;
		this.dashboard = dashboard;
		this.sc = new Scanner(System.in);
	}
	
	public void displayEnquiryMenu(Applicant user) {
		System.out.println("-----------Enquiry Menu-----------");
		List<Enquiry> enquiries = enqCtrl.getEnquiries(user);
		
		if (!enquiries) {
			System.out.println("No enquiry found.");
		} else {
			for (Enquiry enquiry: enquiries) {
				System.out.println("#"+enquiry.getEnquiryID()+" "+enquiry.getProject());
				System.out.println(enquiry.getMessage());
				System.out.println("----------------------------------");
			}
			
			System.out.println("1. Reply to enquiry");
			System.out.println("2. Edit enquiry");
			System.out.println("3. Submit new enquiry");
			System.out.println("4. Exit");
			System.out.print("Enter your choice: ");
			
			int choice = -1;

	        try {
	            choice = Integer.parseInt(sc.nextLine());
	        } catch (NumberFormatException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            continue;
	        }
			
			switch(choice) {
			case 1:
				promptReplyDetails(user);
				break;
			case 2:
				promptEditDetails(user);
				break;
			case 3:
				displayEnquiryForm(user);
				break;
			case 4:
				returnToDashboard(user);
				break;
			default:
				System.out.println("Invalid choice. Enter a number from 1-6.");
				break;
			}
		}
	}
	
	public String promptReplyDetails(Applicant user) { //1. Reply to enquiry
		System.out.print("\nEnter enquiry ID: ");
		String enqID = sc.nextLine();
		System.out.println("Enter reply: ");
		String reply = sc.next();
		boolean replySuccess = enqCtrl.replyToEnquiry(user, enqID, reply);
		if (replySuccess == true) { //print success message
			System.out.println("Reply sent!");
			break;			
		} else {
			System.out.println("Failed to send reply. Please try again.");
		}
		displayEnquiryMenu(user);
	}
	
	public String promptEditDetails(Applicant user) {
		System.out.print("\nEnter enquiry ID: ");
		String enqID = sc.nextLine();
		System.out.println("Enter edited message: ");
		String newMessage = sc.next();
		boolean editSuccess = enqCtrl.updateEnquiry(user, enqID, newMessage);
		if (editSuccess == true) { //print success message
			System.out.println("Reply edited!");
			break;			
		} else {
			System.out.println("Failed to edit reply. Please try again.");
		}
		displayEnquiryMenu(user);
	}
	
	public void displayEnquiryForm(Applicant user) {
		System.out.print("\nEnter Project Name: ");
		String projName = sc.nextLine();
		System.out.println("Enter new message: ");
		String message = sc.next();
		boolean newEnqSuccess = enqCtrl.submitEnquiry(user, projName, message);
		if (newEnqSuccess == true) { //print success message
			System.out.println("Enquiry submitted!");
			break;			
		} else {
			System.out.println("Failed to submit enquiry. Please try again.");
		}
		displayEnquiryMenu(user);
	}
	
	private void returnToDashboard(Applicant user) {
		dashboard.displayUserMenu(user);
	}
}
