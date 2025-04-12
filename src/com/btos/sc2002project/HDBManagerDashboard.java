package com.btos.sc2002project;
import java.util.Scanner;

public class HDBManagerDashboard implements MainMenuView {
	private final HDBManagerEnquiryView mgrEnqView;
	private final HDBManagerProjectView mgrProjView;
	private final ReportView reportView;
	private final LoginView logIn;
	private final Scanner sc;
	
	public HDBManagerDashboard(HDBManagerEnquiryView mgrEnqView,
			HDBManagerProjectView mgrProjView,
			ReportView reportView,
			LoginView logIn) {
		this.mgrEnqView = mgrEnqView;
		this.mgrProjView = mgrProjView;
		this.reportView = reportView;
		this.logIn = logIn;
		this.sc = new Scanner(System.in);
	}
	
	public void displayUserMenu(HDBManager user) {
		boolean viewMenu = true;
		while (viewMenu == true) {
            System.out.println("======= HDB MANAGER DASHBOARD =======");
            System.out.println("|| Manager Services:               ||");
            System.out.println("|| 1. Manage Own Projects          ||"); //create edit delete, view list of own projs, toggle visibility
            System.out.println("|| 2. View All Projects            ||"); //view all manager projs
            System.out.println("|| 3. Project Filter Setting       ||"); //filter when viewing own projs
            System.out.println("|| 4. Manage Officers              ||"); //approve pending bookings
            System.out.println("|| 5. Manage Flat Application      ||"); //approve pending applicant application
            System.out.println("|| 6. Manage Flat Withdrawal       ||"); //approve from list of pending withdrawals
            System.out.println("|| 7. Enquiry Menu                 ||"); //view all enquiry, view own enquiry and reply
            System.out.println("|| 8. Generate Report              ||"); //list of applicant with booking (with filter)
            System.out.println("||---------------------------------||");
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
            		mgrProjView.displayProjectMenu(user);
            		break;
            	case 2:
            		mgrProjView.showAllProjects(user);
            		break;
            	case 3:
					mgrProjView.displayFilterSetting(user);
					break;
            	case 4:
            		mgrProjView.manageOfficers(user);
            		break;
            	case 5:
            		mgrProjView.manageApplications(user);
            		break;
            	case 6:
            		mgrProjView.manageWithdrawals(user);
            		break;
            	case 7:
            		mgrEnqView.displayEnquiryMenu(user);
            		break;
            	case 8:
            		reportView.generateReport(user);
            		break;
            	case 9:
            		viewMenu = false;
					System.out.println("Session terminated.");
					logIn.displayLoginForm();
					break;
            	default:
            		System.out.println("Invalid choice.");
            }
		}
	}
}
