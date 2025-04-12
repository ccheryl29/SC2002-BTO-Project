package com.btos.views;
import java.util.Scanner;
import java.util.List;

public class ApplicantProjectView {
	private final ApplicantProjectController appProjCtrl;
	private final ApplicationView applView;
	private final ApplicantDashboard dashboard;
	private final Scanner sc;
	
	public ApplicantProjectView(ApplicantProjectController appProjCtrl,
			ApplicationView applView,
			ApplicantDashboard dashboard) {
		this.appProjCtrl = appProjCtrl;
		this.dashboard = dashboard;
		this.applView = applView;
		this.sc = new Scanner(System.in);
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
	
	public void displayProjects(Applicant user, FilterSettings filterSetting) { //only DISPLAY projects
		//controller retrieve projects with visibility : ON
		List<Project> projects = appProjCtrl.getVisibleProjects(user, filterSetting);
		if (projects.isEmpty()) {
			System.out.println("No projects available.");
			return;
		} else { System.out.println("--------Available Projects--------"); }
		
		//display each project
		for (Project project : projects) {
			System.out.println("#"+project.getProjectID());
			System.out.println("Name: "+project.getName());
			System.out.println("Location: "+project.getLocation());
			System.out.println("Type: "+project.getFlatType());
			System.out.println("Price: "+project.getPrice());
			System.out.println("----------------------------------");
		}
		
		promptApplication(user);
	}
	
	private void promptApplication(Applicant user) { 
		//get input of project user want to apply
		//send to application view to complete process
		String boolApply = readYesNo("Apply for Project? (Y/N): ");
		if (boolApply.equals("Y")) {
		    applView.initiateApplication(user);
		} else {
		    System.out.println("Returning to home page...");
		    returnToDashboard(user);
		}
	}
	
	public void displayFilterSetting(Applicant user) {
		FilterSettings currentFilter = user.getFilterSettings(); //NEED NEW FILTERSETTINGS CLASS
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
	
	private void returnToDashboard(Applicant user) {
		dashboard.displayUserMenu(user);
	}
}
