import java.util.List;


public class HDBOfficerProjectService {
    private final ProjectRepository projectRepo;
    private final ApplicantRepository applicationRepo;

    public HDBOfficerProjectService(ProjectRepository projectRepo, ApplicantRepository applicationRepo) {
        this.projectRepo = projectRepo;
        this.applicationRepo = applicationRepo;
    }

    public BTOProject getHandlingProject(HDBOfficer officer) {
        return officer.getHandlingProject();
    }

    public void updateNumberOfFlats(BTOProject project, FlatType type, int bookedUnits) {
        project.updateFlatAvailability(type, bookedUnits); // assumed method exists
    }

    public Application retrieveApplication(String applicantNRIC, BTOProject project) {
        return applicationRepo.getApplication(applicantNRIC, project);
    }

    public void updateApplicantBooking(Application application, FlatType bookedType) {
        application.setApplicationStatus(ApplicationStatus.BOOKED);
        application.setBookedFlatType(bookedType);
    }

    public String generateReceipt(Applicant applicant, Application application) {
        // returns a string receipt with details based on requirements
        return String.format("Receipt:\nName: %s\nNRIC: %s\nAge: %d\nStatus: %s\nFlat: %s\nProject: %s",
                applicant.getName(), applicant.getNRIC(), applicant.getAge(),
                applicant.getMaritalStatus(), application.getBookedFlatType(),
                application.getProject().getName());
    }
}
