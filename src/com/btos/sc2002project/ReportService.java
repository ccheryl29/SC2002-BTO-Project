import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    private final ApplicationRepo applicationRepo;

    public ReportService(ApplicationRepo applicationRepo) {
        this.applicationRepo = applicationRepo;
    }

    public String generateAllFlatBookingReport() {
        List<Application> bookedApps = applicationRepo.getAllApplications().stream()
            .filter(app -> app.getApplicationStatus() == ApplicationStatus.BOOKED)
            .collect(Collectors.toList());

        return generateReportString(bookedApps);
    }

    public String generateFilteredBookingReport(String maritalStatus, FlatType flatType) {
        List<Application> filtered = applicationRepo.getAllApplications().stream()
            .filter(app -> app.getApplicationStatus() == ApplicationStatus.BOOKED)
            .filter(app -> app.getApplicant().getMaritalStatus().equalsIgnoreCase(maritalStatus))
            .filter(app -> app.getBookedFlatType() == flatType)
            .collect(Collectors.toList());

        return generateReportString(filtered);
    }

    private String generateReportString(List<Application> applications) {
        if (applications.isEmpty()) return "No matching flat bookings found.";

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Flat Booking Report ===\n");
        for (Application app : applications) {
            Applicant a = app.getApplicant();
            sb.append(String.format("""
                --------------------------
                Name: %s
                NRIC: %s
                Age: %d
                Marital Status: %s
                Flat Type: %s
                Project: %s
                --------------------------\n""",
                a.getName(), a.getNRIC(), a.getAge(), a.getMaritalStatus(),
                app.getBookedFlatType(), app.getProject().getName()));
        }
        return sb.toString();
    }
}
