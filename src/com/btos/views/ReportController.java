
public class ReportController {
    private ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    public void generateEnquiryReport(BTOProject project) {
        service.generateEnquiryReport(project);
    }

    public void generateApplicationReport(BTOProject project) {
        service.generateApplicationReport(project);
    }
}