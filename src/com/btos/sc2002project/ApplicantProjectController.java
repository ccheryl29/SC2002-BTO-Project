import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class ApplicantProjectController {

    private ApplicantProjectService projectService;
    private ApplicationController applicationController; //Should use Application Controller to apply for project

    public ApplicantProjectController(ApplicantProjectService projectService, ApplicationController applicationController) {
        this.projectService = projectService;
        this.applicationController = applicationController;
    }

    public void displayBTOProjects(Applicant applicant) {
        List<BTOProject> visibleProjects = projectService.getVisibleProjects(applicant);
        
        for (BTOProject project : visibleProjects) {
            System.out.println(project.getName());
        }
    }
    
    // As applicant and HDB officer share a application view, when applicant wants to apply for BTO 
    // it should show the application view and use ApplicationController to apply
    public void applyForProject(Applicant applicant, String projectID) {
        BTOProject project = projectService.getProjectByID(projectID); //getting the ID of the project
        applicationController.applyForProject(applicant, project); //should validate the project and eligibility in the applicationController
    }

    public void withdrawApplication(Applicant applicant) {
        projectService.withdrawApplication(applicant);
    }

    public void bookFlat(Applicant applicant, String flatType) {
        projectService.bookFlat(applicant, flatType);
    }
}



