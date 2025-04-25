package view;

import controller.ApplicantEnquiryController;
import controller.ApplicantProjectController;
import controller.HDBManagerApplicationController;
import controller.HDBManagerEnquiryController;
import controller.HDBManagerProjectController;
import controller.HDBManagerRegistrationController;
import controller.OfficerFlatBookingController;
import controller.OfficerRegistrationController;
import controller.OfficerEnquiryController;
import model.Applicant;
import model.HDBManager;
import model.HDBOfficer;
import model.User;
import service.OfficerEnquiryService;
import repository.ApplicationRepository;
import repository.ProjectRepository;
import repository.OfficerRepository;
import repository.EnquiryRepository;
import repository.ApplicantRepository;
import repository.ManagerRepository;
import controller.AccountManagementController;
import service.AccountManagementService;

import java.util.Scanner;

/**
 * Factory class for creating appropriate dashboard instances based on user type
 */
public class DashboardFactory {
    
    // Repositories
    private final ProjectRepository projectRepository;
    private final ApplicationRepository applicationRepository;
    private final OfficerRepository officerRepository;
    private final EnquiryRepository enquiryRepository;
    
    // Controllers needed for dashboards
    private final HDBManagerProjectController managerProjectController;
    private final HDBManagerApplicationController managerApplicationController;
    private final HDBManagerRegistrationController managerRegistrationController;
    private final HDBManagerEnquiryController managerEnquiryController;
    private final ApplicantProjectController applicantProjectController;
    private final ApplicantEnquiryController applicantEnquiryController;
    private final OfficerRegistrationController officerRegistrationController;
    private final OfficerFlatBookingController officerFlatBookingController;
    private final AccountManagementController accountManagementController;
    private final Scanner scanner;
    
    /**
     * Constructor for DashboardFactory
     * 
     * @param projectRepository Project repository
     * @param applicationRepository Application repository
     * @param officerRepository Officer repository
     * @param enquiryRepository Enquiry repository
     */
    public DashboardFactory(
            ProjectRepository projectRepository,
            ApplicationRepository applicationRepository,
            EnquiryRepository enquiryRepository,
            ApplicantRepository applicantRepository,
            OfficerRepository officerRepository,
            ManagerRepository managerRepository) {
        
        this.scanner = new Scanner(System.in);
        
        // Store repositories
        this.projectRepository = projectRepository;
        this.applicationRepository = applicationRepository;
        this.officerRepository = officerRepository;
        this.enquiryRepository = enquiryRepository;
        
        // Initialize services and controllers based on repositories
        // For HDB Manager
        this.managerProjectController = new HDBManagerProjectController(
                new service.HDBManagerProjectService(projectRepository, applicationRepository));
        
        this.managerApplicationController = new HDBManagerApplicationController(
                new service.HDBManagerApplicationService(applicationRepository, projectRepository));
        
        this.managerRegistrationController = new HDBManagerRegistrationController(
                new service.HDBManagerRegistrationService(officerRepository, projectRepository),
                new service.HDBManagerProjectService(projectRepository, applicationRepository));
        
        this.managerEnquiryController = new HDBManagerEnquiryController(
                new service.HDBManagerEnquiryService(enquiryRepository));
        
        // For Applicant
        this.applicantProjectController = new ApplicantProjectController(
                new service.ApplicantProjectService(projectRepository, applicationRepository));
        
        this.applicantEnquiryController = new ApplicantEnquiryController(
                new service.ApplicantEnquiryService(enquiryRepository, projectRepository), null); // Will set applicant later
        
        // For Officer
        this.officerRegistrationController = new OfficerRegistrationController(
                new service.OfficerRegistrationService(officerRepository, projectRepository, applicationRepository));
        
        this.officerFlatBookingController = new OfficerFlatBookingController(
                new service.OfficerFlatBookingService(applicationRepository, projectRepository));
        this.accountManagementController = new AccountManagementController(
                new AccountManagementService(applicantRepository, officerRepository, managerRepository), null);
    }
    
    /**
     * Creates the appropriate dashboard based on the user type
     * 
     * @param user The authenticated user
     * @return The appropriate dashboard for the user type, or null if the user type is unknown
     */
    public Dashboard createDashboard(User user) {
        if (user == null) {
            return null;
        }
        this.accountManagementController.setCurrentUser(user);
        if (user instanceof HDBManager) {
            HDBManagerDashboard dashboard = new HDBManagerDashboard(
                    (HDBManager) user,
                    managerProjectController,
                    managerApplicationController,
                    managerRegistrationController,
                    managerEnquiryController,
                    accountManagementController
            );
            return dashboard;
        } else if (user instanceof HDBOfficer) {
            // Create views needed by the dashboard
            OfficerRegistrationView registrationView = new OfficerRegistrationView(officerRegistrationController);
            OfficerApplicationView applicationView = new OfficerApplicationView(applicantProjectController);
            OfficerFlatBookingView flatBookingView = new OfficerFlatBookingView(officerFlatBookingController);
            
            // Create the officer enquiry controller and view
            OfficerEnquiryController officerEnquiryController = new OfficerEnquiryController(
                    new OfficerEnquiryService(enquiryRepository, projectRepository)
            );
            OfficerEnquiryView enquiryView = new OfficerEnquiryView(officerEnquiryController);
            AccountManagementView accountManagementView = new AccountManagementView(accountManagementController, (HDBOfficer) user);

            return new OfficerDashboard(
                    (HDBOfficer) user,
                    scanner,
                    registrationView,
                    applicationView,
                    flatBookingView,
                    enquiryView,
                    accountManagementView
            );
        } else if (user instanceof Applicant) {
            return new ApplicantDashboard(
                    (Applicant) user,
                    applicantProjectController,
                    applicantEnquiryController,
                    accountManagementController
            );
        }
        
        return null;
    }
} 