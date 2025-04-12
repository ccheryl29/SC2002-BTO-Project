
public class HDBOfficerProjectController {
	
    private HDBOfficerProjectService service;

    public HDBOfficerProjectController(HDBOfficerProjectService service) {
        this.service = service;
    }

    public void assignProject(HDBOfficer officer, BTOProject project) {
        service.assignProject(officer, project);
    }

    public void approveApplication(Application app) {
        service.approveApplication(app);
    }

    public void bookFlat(Application app, BTOProject project) {
        service.bookFlat(app, project);
    }
}