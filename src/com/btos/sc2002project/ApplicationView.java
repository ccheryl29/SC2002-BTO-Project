package com.btos.sc2002project;
import java.util.Scanner;

public class ApplicationView {
	private final ApplicationController applCtrl;
	private final MainMenuView dashboard;
	private final Scanner sc;
	public ApplicationView(ApplicationController applCtrl,
			MainMenuView dashboard) {
		this.applCtrl = applCtrl;
		this.dashboard = dashboard;
		this.sc = new Scanner(System.in);
	}
	
	public void initiateApplication(Applicant user) {
		System.out.print("Enter Project ID: ");
		String chosenProjID = sc.next();
		
		//check if applicant is eligible (marital status, age, no other project)
		boolean eligible = applCtrl.checkApplicantEligibility(user, chosenProjID);
		
		//return true if successfully applied
		if (eligible == true) {
			boolean success = applCtrl.applyForProject(user, chosenProjID);
			if (success == true) { //print success message
				System.out.println("Application submitted! Returning to home page...");
				break;			
			} else {
				System.out.println("Failed to apply. Invalid project name or eligibility issue.");
				System.out.println("Try again? (Y/N): ");
				String tryAgain = sc.nextLine().trim().toUpperCase();
				
				//choose if want to try again or exit
				if (tryAgain.toUpperCase() == "Y") {
					initiateApplication(user);
					break;
				} else if (tryAgain.toUpperCase() == "N") {
					System.out.println("Returning to home page...");
					break;
				}
			}
		} else { System.out.println("You are not eligible for this project. Please try again."); }
		//return to home page
		returnToDashboard(user);
	}
	
	public void displayApplicationStatus(Applicant user) {
		System.out.println("Status: "+applCtrl.getApplicationStatus(user));
		boolean book = false;
		while (book == false) {
			if (applCtrl.getApplicationStatus(user) == EnumHelper.APPROVED) {
				System.out.print("Proceed to book flat? (Y/N): ");
				String bookChoice = sc.next();
				if (bookChoice.toUpperCase() == "Y") {
					book = true;
					initiateBooking(user);
				} else if (bookChoice.toUpperCase() == "N") {
					System.out.println("Returning to home page...");
					book = true;
					returnToDashboard(user);
				} else { System.out.println("Invalid choice."); }
			}
		}
	}
	
	public void initiateWithdrawal(Applicant user) {
		System.out.println("You are about to withdraw application for "+applCtrl.getAppliedProject().getName());
		System.out.print("Confirm withdrawal? (Y/N): ");
		String wdChoice = sc.next();
		if (wdChoice.toUpperCase() == "Y") {
			boolean wdSuccess = applCtrl.withdrawApplication(user);
			if (wdSuccess == true) {
				System.out.println("Application successfully withdrawn.");
			} else { System.out.println("Application withdrawal unsuccessful. Please try again later.");
			returnToDashboard(user);
			}
		}
	}
	
	public void initiateBooking(Applicant user) { 
		//when application status is approved
		boolean booked = applCtrl.bookFlat(user);
		if (booked == true) {
			System.out.print("Booking in progress...");
			System.out.print("Status will be updated to 'BOOKED' in application menu when completed.");
			System.out.println("Returning you to home page...");
		} else { System.out.println("Booking failed. Please try again later."); }
		returnToDashboard(user);
	}
	
	private void returnToDashboard(User user) {
		dashboard.displayUserMenu(user);
	}
}
