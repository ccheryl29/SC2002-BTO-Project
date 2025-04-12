package com.btos.sc2002project;
import java.util.Scanner;

public class ApplicantDashboard implements MainMenuView {
	private final ApplicantProjectView appProjView;
	private final ApplicantEnquiryView appEnqView;
	private final ApplicationView currAppView;
	private final LoginView logIn;
	private final FilterSettings filterSettings;
	private final Scanner sc;
	
	public ApplicantDashboard(ApplicantProjectView appProjView, 
            ApplicantEnquiryView appEnqView, 
            ApplicationView currAppView,
            LoginView logIn,
            FilterSettings filterSettings) {
		this.appProjView = appProjView;
		this.appEnqView = appEnqView;
		this.currAppView = currAppView;
		this.logIn = logIn;
		this.filterSettings = filterSettings;
		this.sc = new Scanner(System.in);
	}
	
	public void displayUserMenu(Applicant user) {
		boolean viewMenu = true;
		while (viewMenu == true) {
			System.out.println("=========APPLICANT HOME PAGE=========");
			System.out.println("|| Services:                       ||");
			System.out.println("|| 1. View/Apply Project           ||"); //ApplicantProjectView
			System.out.println("|| 2. Search Filter Settings       ||"); //ApplicantProjectView
			System.out.println("|| 3. Check Application Status     ||"); //ApplicationView
			System.out.println("|| 4. Withdraw Application         ||"); //ApplicationView
			System.out.println("|| 5. Submit Enquiry               ||"); //ApplicantEnquiryView
	        System.out.println("||---------------------------------||");
	        System.out.println("|| 6. Logout                       ||"); //LoginView
			System.out.println("=====================================");
			System.out.println("Enter your choice: ");
			
			int choice = -1; //default invalid choice

	        try {
	            choice = Integer.parseInt(sc.nextLine());
	        } catch (NumberFormatException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            continue; //skip to next loop iteration
	        }
	        
			switch(choice) {
				case 1:
					appProjView.displayProjects(user, filterSettings);
					break;
				case 2:
					appProjView.displayFilterSetting(user);
					break;
				case 3:
					currAppView.displayApplicationStatus(user);
					break;
				case 4:
					currAppView.initiateWithdrawal(user);
					break;
				case 5:
					appEnqView.displayEnquiryMenu(user);
					break;
				case 6:
					viewMenu = false;
					System.out.println("Session terminated.");
					logIn.displayLoginForm();
					break;
				default:
					System.out.println("Invalid input.");
					break;
			}
		}
	}
}
