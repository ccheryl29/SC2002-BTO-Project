
public class HDBOfficerProjectController {
	
    private final HDBOfficerProjectService projectService;

    public HDBOfficerProjectController(HDBOfficerProjectService projectService) {
        this.projectService = projectService;
    }
    
    public void getDetailsOfProject(HDBOfficer officer) {}
    
    

    public void viewHandlingProject(HDBOfficer officer) {
        BTOProject project = projectService.getHandlingProject(officer);
        System.out.println(project);
    }

    public void bookFlat(HDBOfficer officer, String applicantNRIC, FlatType flatType) {
        BTOProject project = projectService.getHandlingProject(officer); //getting the project handled by the officer
        Application app = projectService.retrieveApplication(applicantNRIC, project); //getting the application from the applicant
        if (app != null && app.getApplicationStatus() == "SUCCESSFUL") { //check if they match
            projectService.updateNumberOfFlats(project, flatType, -1); //update flats no.
            projectService.updateApplicantBooking(app, flatType); //update applicant flat booking
            System.out.println("Flat booking confirmed.");
        } else {
            System.out.println("Flat not eligible for booking.");
        }
    }

    public void printReceipt(HDBOfficer officer, String applicantNRIC) {
        BTOProject project = projectService.getHandlingProject(officer);
        Application app = projectService.retrieveApplication(applicantNRIC, project);
        if (app != null && app.getApplicationStatus() == "BOOKED") {
            Applicant applicant = app.getApplicant();
            String receipt = projectService.generateReceipt(applicant, app);
            System.out.println(receipt);
        } else {
            System.out.println("No booked application found.");
        }
    }
}
