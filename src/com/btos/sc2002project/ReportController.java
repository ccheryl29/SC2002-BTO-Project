public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    public void generateFlatBookingReport() {
        String report = reportService.generateAllFlatBookingReport();
        System.out.println(report);
    }

    public void generateFilteredReport(String maritalStatus, FlatType flatType) {
        String report = reportService.generateFilteredBookingReport(maritalStatus, flatType);
        System.out.println(report);
    }
}
