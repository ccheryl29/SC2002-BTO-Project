public class HDBOfficerRegisterService {
    private final ProjectRepo projectRepo;

    public HDBOfficerRegisterService(ProjectRepo projectRepo) {
        this.projectRepo = projectRepo;
    }

    public boolean isEligible(HDBOfficer officer, BTOProject project) {
        boolean alreadyApplied = officer.hasAppliedToProject(project);
        boolean handlingOtherProject = projectRepo.hasOfficerRegisteredDuring(officer, project.getOpenDate(), project.getCloseDate());
        return !alreadyApplied && !handlingOtherProject;
    }

    public boolean registerOfficerToProject(HDBOfficer officer, BTOProject project) {
        if (project.getAvailableOfficerSlots() <= 0) return false;
        project.addOfficerRegistration(officer);
        return true;
    }

    public void showOfficerRegistrationStatus(HDBOfficer officer) {
        System.out.println("Officer registered to: " + officer.getHandlingProject() + "project");
    }
}
