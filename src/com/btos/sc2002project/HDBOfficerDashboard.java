package com.btos.sc2002project;
import java.util.Scanner;

public class HDBOfficerDashboard implements MainMenuView {
	private final HDBOfficerEnquiryView offEnqView;
	private final HDBOfficerProjectView offProjView;
	private final ApplicantProjectView appProjView;
	private final ApplicantEnquiryView appEnqView;
	private final ApplicationView currAppView;
	private final FilterSettings filterSettings;
	private final LoginView logIn;
	private final Scanner sc;
	
	public HDBOfficerDashboard(HDBOfficerEnquiryView offEnqView,
			HDBOfficerProjectView offProjView,
			ApplicantProjectView appProjView,
			ApplicantEnquiryView appEnqView,
			ApplicationView currAppView,
			LoginView logIn,
			FilterSettings filterSettings) {
		this.offEnqView = offEnqView;
		this.offProjView = offProjView;
		this.appProjView = appProjView;
		this.appEnqView = appEnqView;
		this.currAppView = currAppView;
		this.logIn = logIn;
		this.filterSettings = filterSettings;
		this.sc = new Scanner(System.in);
	}
	
	public void displayUserMenu(HDBOfficer user) {
		boolean viewMenu = true;
		while (viewMenu == true) {
            System.out.println("======= HDB OFFICER DASHBOARD =======");
            System.out.println("|| Officer Services:               ||");
            System.out.println("|| 1. Register a Project           ||"); //HDBOfficerProjectView
            System.out.println("|| 2. Registration Status          ||");
            System.out.println("|| 3. Handle Enquiries             ||"); //HDBOfficerEnquiryView
            System.out.println("|| 4. Handle Flat Bookings         ||"); //can put in proj view, they see list of pending bookings to do?
            System.out.println("||---------------------------------||");
            System.out.println("|| Applicant Services:             ||");
            System.out.println("|| 5. View/Apply Project           ||"); //ApplicantProjectView
            System.out.println("|| 6. Application Status           ||"); //ApplicationView
            System.out.println("|| 7. Submit Enquiry               ||"); //ApplicantEnquiryView
            System.out.println("||---------------------------------||");
            System.out.println("|| 8. Filter Settings              ||");
            System.out.println("|| 9. Logout                       ||");
            System.out.println("=====================================");
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
					offProjView.displayProjects(user, user.getFilterSettings());
					break;
				case 2:
					offProjView.displayRegistrationStatus(user);
					break;
				case 3:
					offEnqView.displayEnquiries(user);
					break;
				case 4:
					offProjView.displayBookings(user);
				case 5:
					appProjView.displayProjects(user, user.getFilterSettings());
					break;
				case 6:
					currAppView.displayApplicationStatus(user);
					break;
				case 7:
					appEnqView.displayEnquiryMenu(user);
					break;
				case 8:
					offProjView.displayFilterSetting(user);
				case 9:
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
