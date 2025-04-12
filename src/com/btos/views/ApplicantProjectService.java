import java.util.List;
import java.util.ArrayList;


public class ApplicantProjectService {
	
    public List<BTOProject> getVisibleProjects(Applicant applicant) {
        return BTOProjectRepository.getVisibleTo(applicant);
    }

    public void apply(Applicant applicant, BTOProject project) {
    	//Make sure only got one application
        if (applicant.getApplication() == null) {
            applicant.applyBTO(project);
        } else {
        	System.out.println("Cannot apply more than 1 project");
        }
    }

    public void withdraw(Applicant applicant) {
        applicant.withdrawBTO(applicant.getApplication().getProject());
    }

    public void book(Applicant applicant, BTOProject project, String flatType) {
        applicant.bookBTO(project, flatType);
    }
    
}