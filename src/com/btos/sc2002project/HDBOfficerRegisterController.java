
import java.util.List;

public class HDBOfficerRegisterController {
    private final List<BTOProject> projectList;

    public HDBOfficerRegisterController(List<BTOProject> projectList) {
        this.projectList = projectList;
    }

    public void registerForProject(HDBOfficer officer, BTOProject project) {
        if (!officer.isEligibleForProject(project)) {
            System.out.println("You are not eligible to register for this project.");
            return;
        }

        if (project.getAvailableOfficerSlots() > 0) {
            project.registerOfficer(officer);
            System.out.println("Registration request submitted.");
        } else {
            System.out.println("No available slots for officers.");
        }
    }
    
    public void validateEligibility(HDBOfficer officer, BTOProject projectList) {
    	
    }

    public void checkRegistrationStatus(HDBOfficer officer) {
        System.out.println("Current registered project: " + officer.getHandlingProject());
    }
}

