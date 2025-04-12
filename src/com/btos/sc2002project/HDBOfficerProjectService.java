
public class HDBOfficerProjectService {
    public void assignProject(HDBOfficer officer, BTOProject project) {
        officer.setAssignedProject(project);
    }

    public void approveApplication(Application app) {
        app.setStatus("Approved");
    }

    public void bookFlat(Application app, BTOProject project) {
        // Link flat to applicant/project
    }
}
