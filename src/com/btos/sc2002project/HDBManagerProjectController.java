
public class HDBManagerProjectController {

    private HDBManagerProjectService service;

    public HDBManagerProjectController(HDBManagerProjectService service) {
        this.service = service;
    }

    // Manager creates a new BTO project
    public void createBTOProject(HDBManager manager, BTOProject project) {
        service.createProject(manager, project);
    }

    // Edit details of an existing project
    public void editBTOProject(HDBManager manager, BTOProject project) {
        service.editProject(manager, project);
    }

    // Delete an existing project
    public void deleteBTOProject(HDBManager manager, String projectName) {
        service.deleteProject(manager, projectName);
    }

    // Toggle visibility ON/OFF
    public void toggleProjectVisibility(HDBManager manager, String projectName) {
        service.toggleVisibility(manager, projectName);
    }

    // View all projects
    public void viewAllProjects() {
        service.viewAllProjects();
    }

    // View only projects created by this manager
    public void viewMyProjects(HDBManager manager) {
        service.viewManagerProjects(manager);
    }

    // Approve or reject officer registration
    public void processOfficerRegistration(String projectName, HDBOfficer officer, boolean approve) {
        service.processOfficerRegistration(projectName, officer, approve);
    }

    // Approve or reject applicant's BTO application
    public void processApplicantApplication(Application application, boolean approve) {
        service.processApplicantApplication(application, approve);
    }

    // Approve or reject withdrawal
    public void processWithdrawal(Application application, boolean approve) {
        service.processWithdrawal(application, approve);
    }

}

