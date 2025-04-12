package com.btos.sc2002project;

import java.util.Scanner;
import java.util.List;
import java.util.Map;

public class HDBOfficerProjectView {
	private final HDBOfficerProjectController offProjCtrl;
	private final HDBOfficerRegistrationController offRegCtrl;
	private final HDBOfficerDashboard dashboard;
	private final Scanner sc;
	
	public HDBOfficerProjectView(HDBOfficerProjectController offProjCtrl,
			HDBOfficerRegistrationController offRegCtrl,
			HDBOfficerDashboard dashboard) {
		this.offProjCtrl = offProjCtrl;
		this.offRegCtrl = offRegCtrl;
		this.dashboard = dashboard;
		this.sc = new Scanner(System.in);
	}
	
	public void displayProjects(HDBOfficer user) {
		List<Project> projects = offProjCtrl.getVisibleProjects(user); //retrieve visible projects
		if (projects.isEmpty()) {
			System.out.println("No eligible projects available.");
			return;
		} else { System.out.println("--------Available Projects--------"); }
		
		//display each project
		for (Project project : projects) {
			System.out.println("Name: "+project.getName());
			System.out.println("Location: "+project.getLocation());
			System.out.println("----------------------------------");
		}
	}
	
	public void displayFilterSetting(HDBOfficer user) {
		FilterSettings currentFilter = user.getFilterSettings(); //NEW FILTERSETTINGS CLASS
		boolean setting = true;
		while (setting) {
			System.out.println("-----Filter Settings-----");
			System.out.println("1. Location");
			System.out.println("2. Flat Type");
			System.out.println("3. Price (Low to High)");
			System.out.println("4. Price (High to Low)");
			System.out.println("5. Reset to default");
			System.out.println("6. Return to home page");
			System.out.print("Enter your choice: ");

			int filterChoice = -1;

	        try {
	            filterChoice = Integer.parseInt(sc.nextLine());
	        } catch (NumberFormatException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            continue;
	        }
			
			switch (filterChoice) {
				case 1:
					System.out.print("Enter desired location: ");
					String location = sc.nextLine();
					currentFilter.setLocation(location);
					System.out.println("Filter setting applied.");
					System.out.println("Projects at "+location.toLowerCase()+" will be shown.");
					break;
				case 2:
					System.out.print("Enter number of rooms: ");
					int numRooms = sc.nextInt();
					sc.nextLine();
					currentFilter.setFlatType(numRooms);
					System.out.println("Filter setting applied.");
					System.out.println("Projects with "+numRooms+"-room flats will be shown.");
					break;
				case 3:
					currentFilter.setPriceLowToHigh();
					System.out.println("Filter setting applied.");
					System.out.println("Projects will be sorted in increasing price order.");
					break;
				case 4:
					currentFilter.setPriceHighToLow();
					System.out.println("Filter setting applied.");
					System.out.println("Projects will be sorted in decreasing price ordern.");
					break;
				case 5:
					currentFilter.setDefault();
					System.out.println("All filters have been cleared and reset to default.");
					break;
				case 6:
					setting = false;
					break;
				default:
					System.out.println("Invalid option.");
			}
		}
		returnToDashboard(user);
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
	
	private void promptRegistration(HDBOfficer user) { 
		//get input of project officer want to register
		//send to controller to complete registration process
		String boolRegister = readYesNo("Register for Project? (Y/N): ");
		if (boolRegister.equals("Y")) {
			initiateRegistration(user);
		} else {
			System.out.println("Returning to home page...");
			returnToDashboard(user);
		}
	}
	
	public void initiateRegistration(HDBOfficer user) {
		System.out.print("Enter Project ID: ");
		String chosenProjID = sc.next();
		
		//check if applicant is eligible (marital status, age, no other project)
		boolean eligible = offRegCtrl.validateEligibility(user, chosenProjID);
		
		//return true if successfully applied
		if (eligible == true) {
			boolean success = offRegCtrl.registerForProject(user, chosenProjID);
			if (success == true) { //print success message
				System.out.println("Registration submitted! Returning to home page...");
				break;			
			} else {
				System.out.println("Failed to register. Invalid project or eligibility issue.");
				String tryAgain = readYesNo("Try again? (Y/N): ");
				
				//choose if want to try again or exit
				if (tryAgain.equalsIgnoreCase("Y")) {
					initiateRegistration(user);
					break;
				} else {
					System.out.println("Returning to home page...");
					break;
				}
			}
		} else { System.out.println("You are not eligible for this project. Please try again."); }
		//return to home page
		returnToDashboard(user);
	}
	
	public void displayRegistrationStatus(HDBOfficer user) {
		System.out.println("Status: "+offProjCtrl.getRegistrationStatus(user));
		System.out.println("Press <ENTER> to return to home page  ");
		sc.nextLine();
		sc.nextLine();
		returnToDashboard(user);
	}
	
	public void displayBookings(HDBOfficer user) {
		//get list of applications of relevant project with 'APPROVED' status
		List<Application> approvedApplications = offProjCtrl.getApprovedApplications(user);
		if (approvedApplications.isEmpty()) {
			System.out.println("No bookings to process.");
			return;
		} else { System.out.println("---------Pending Bookings---------"); }
		
		//display each project
		for (Application application : approvedApplications) {
			System.out.println("Application No. #"+application.getApplicationID());
			System.out.println("Applicant: "+application.getApplicant().getName());
			System.out.println("Applicant: "+application.getApplicant().getNRIC());
			System.out.println("Date applied: "+application.getApplicationDate());
			System.out.println("----------------------------------");
		}
		System.out.print("Enter Application ID to proceed with booking: ");
		String applicationID = sc.next();
		//HDB Officer's responsibilities:
		//1. update number of flat for each flat type that are remaining
		//2. retrieve applicant's BTO application with their NRIC
		//3. update applicant's project status to 'BOOKED'
		//4. update applicant's profile with type of flat for corresponding project
		Map<Integer, Integer> remainingFlats = offProjCtrl.getRemainingFlats();
		System.out.println("Remaining flats and types:");
		for (Map.Entry<Integer, Integer> flat : remainingFlats.entrySet()) {
			System.out.println(flat.getKey()+"-room: "+flat.getValue()+" available units remaining.");
		}
		
		//manually enter number of flats remaining
		boolean validInput = false;
		int chosenType = 0;
		int newUnits = 0;

		while (!validInput) {
		    try {
		        System.out.print("Enter flat type (number of rooms) to update: ");
		        chosenType = Integer.parseInt(sc.nextLine());
		        System.out.print("\nNewly updated number of units: ");
		        newUnits = Integer.parseInt(sc.nextLine());
		        validInput = true; //only reach here if both inputs are valid
		    } catch (NumberFormatException e) {
		        System.out.println("Invalid input. Please enter a number.");
		    }
		}
		boolean updated1 = offProjCtrl.updateRemainingFlats(applicationID, chosenType, newUnits);
		
		//prompt for details to update applicant's profile and status
		System.out.print("\nEnter applicant's NRIC to update details: ");
		String applicantID = sc.next();
		boolean updated2 = offProjCtrl.updateApplicantDetails(applicationID, applicantID);
		
		//prompt if want to generate receipt of booking
		if (updated1 && updated2) {
			String receiptChoice = readYesNo("\nBooking success. Generate receipt of booking? (Y/N): ");
			if (receiptChoice.equalsIgnoreCase("Y")) {
				generateBookingReceipt(applicationID);
			} else {
				System.out.println("Returning to homepage...");
				returnToDashboard(user);
			}
		} else { System.out.println("Booking failed. Please try again."); }
	}
	
	private void generateBookingReceipt(String applicationID) {
		//controller method to retrieve application object, to access and print details
		Application applBooked = offProjCtrl.getApplication(applicationID);
		System.out.println("---------BOOKING RECEIPT---------");
		System.out.println("Name: "+applBooked.getApplicant().getName());
		System.out.println("NRIC: "+applBooked.getApplicant.getNRIC());
		System.out.println("Age: "+applBooked.getApplicant().getAge());
		System.out.println("Marital Status: "+applBooked.getApplicant().getMaritalStatus());
		System.out.println("Flat Type: "+applBooked.getFlatType());
		System.out.println("Project Name: "+applBooked.getFlatType());
		System.out.println("Project Location: "+applBooked.getFlatType());
		System.out.println("---------------------------------");
	}
	
	private void returnToDashboard(HDBOfficer user) {
		dashboard.displayUserMenu(user);
	}
}
