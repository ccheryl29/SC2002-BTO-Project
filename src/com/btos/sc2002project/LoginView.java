package com.btos.sc2002project;
import java.util.Scanner;

public class LoginView {
	private final ApplicantDashboard applicantDashboard;
	private final HDBOfficerDashboard officerDashboard;
	private final HDBManagerDashboard managerDashboard;
	private final AuthenticationController authenticationCtrl;
	public LoginView(AuthenticationController authenticationCtrl,
			ApplicantDashboard applicantDashboard,
			HDBOfficerDashboard officerDashboard,
			HDBManagerDashboard managerDashboard) {
		this.authenticationCtrl = authenticationCtrl;
		this.applicantDashboard = applicantDashboard;
		this.officerDashboard = officerDashboard;
		this.managerDashboard = managerDashboard;
	}
	
	public void displayLoginForm() {
		Scanner sc = new Scanner(System.in);
		System.out.println("=======Login to your account=======");
		System.out.println("User ID: ");
		String userID = sc.next();
		System.out.println("Password: ");
		String pw = sc.next();
		User user = authenticateUser(userID, pw); //verify if credentials are correct
		if (user != null) { //if user exists with correct id and pw
			displayDashboard(user); //send to corresponding dashboard
		} else { displayErrorMessage(); }
	}
	
	//send to controller to check id and pw match
	private User authenticateUser(String userID, String password) {
		return authCtrl.login(userID, password);
	}
	
	//create dashboard based on user type
	private void displayDashboard(User user) {
	    MainMenuView dashboard = null;
	    if (user.getUserType().equals("Applicant")) {
	        dashboard = appDashboard;
	    } else if (user.getUserType().equals("Officer")) {
	        dashboard = officerDashboard;
	    } else if (user.getUserType().equals("Manager")) {
	        dashboard = managerDashboard;
	    }

	    if (dashboard != null) {
	        dashboard.displayUserView(user);
	    } else {
	        System.out.println("No dashboard found for user type: " + user.getUserType());
	    }
	}
	
	public void displayErrorMessage() {
		System.out.println("UserID or password invalid. Please try again");
		displayLoginForm();
	}
}
