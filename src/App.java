import controller.AuthenticationController;
import controller.AuthenticationController.UserType;
import controller.HDBManagerProjectController;
import controller.HDBManagerApplicationController;
import controller.HDBManagerRegistrationController;
import controller.HDBManagerEnquiryController;
import controller.OfficerRegistrationController;
import controller.ApplicantProjectController;
import controller.ApplicantEnquiryController;
import controller.OfficerFlatBookingController;
import model.HDBManager;
import model.HDBOfficer;
import model.HDBOfficer.RegistrationStatus;
import model.Applicant;
import model.User;
import model.Project;
import model.Flat;
import model.Flat.FlatType;
import repository.ProjectRepository;
import repository.ApplicationRepository;
import repository.OfficerRepository;
import repository.EnquiryRepository;
import repository.ApplicantRepository;
import repository.ManagerRepository;
import view.LoginView;
import view.DashboardFactory;
import view.Dashboard;

import java.util.Scanner;
import java.util.List;
import java.util.Date;
import java.io.File;

/**
 * Main application entry point with mock data for testing
 */
public class App {
    
    // Repositories
    private static ProjectRepository projectRepository;
    private static ApplicationRepository applicationRepository;
    private static OfficerRepository officerRepository;
    private static EnquiryRepository enquiryRepository;
    private static ApplicantRepository applicantRepository;
    private static ManagerRepository managerRepository;
    
    private static DashboardFactory dashboardFactory;
    
    public static void main(String[] args) {
        System.out.println("Starting HDB Housing System...");
        
        // Create data directories if they don't exist
        createDataDirectories();
        
        // Initialize repositories
        initializeRepositories();
        
        // Create mock data
        createMockData();
        
        // Initialize controllers for login
        AuthenticationController authController = new AuthenticationController(
            applicantRepository, 
            officerRepository, 
            managerRepository
        );
        
        boolean systemRunning = true;
        
        while (systemRunning) {
            // Start with login view
            LoginView loginView = new LoginView(authController);
            User authenticatedUser = loginView.displayLoginScreen();
            
            if (authenticatedUser != null) {
                // Use the dashboard factory to create the appropriate dashboard
                Dashboard dashboard = dashboardFactory.createDashboard(authenticatedUser);
                
                if (dashboard != null) {
                    // Display the dashboard
                    dashboard.displayDashboard();
                } else {
                    System.out.println("Error: Unknown user type.");
                }
                
                // After returning from the dashboard, prompt if the user wants to log out or exit
                Scanner scanner = new Scanner(System.in);
                System.out.println("\n===== LOGOUT OPTIONS =====");
                System.out.println("1. Return to Login Screen");
                System.out.println("2. Exit the system");
                System.out.print("Enter your choice: ");
                
                String choice = scanner.nextLine().trim();
                
                if (choice.equals("2")) {
                    systemRunning = false;
                    System.out.println("Exiting system...");
                } else {
                    System.out.println("Logging out...");
                }
            } else {
                // If login returned null, exit the system
                systemRunning = false;
            }
        }
        
        System.out.println("Thank you for using the HDB Housing System. Goodbye!");
    }
    
    /**
     * Create necessary data directories
     */
    private static void createDataDirectories() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
            System.out.println("Created data directory");
        }
    }
    
    /**
     * Initialize all repositories
     */
    private static void initializeRepositories() {
        projectRepository = new ProjectRepository("data/projects.ser");
        applicationRepository = new ApplicationRepository("data/applications.ser");
        officerRepository = new OfficerRepository("data/officers.ser");
        enquiryRepository = new EnquiryRepository("data/enquiries.ser");
        applicantRepository = new ApplicantRepository("data/applicants.ser");
        managerRepository = new ManagerRepository("data/managers.ser");
        
        // Load existing data if any
        // projectRepository.loadData();
        // applicationRepository.loadData();
        // officerRepository.loadData();
        // enquiryRepository.loadData();
        // applicantRepository.loadData();
        // managerRepository.loadData();
        
        System.out.println("Repositories initialized");
        
        // Initialize the dashboard factory
        dashboardFactory = new DashboardFactory(
            projectRepository,
            applicationRepository,
            officerRepository,
            enquiryRepository
        );
    }
    
    /**
     * Create mock data for testing
     */
    private static void createMockData() {
        // Create mock projects if none exist
        if (projectRepository.findAll().isEmpty()) {
            createMockProjects();
        }
        
        // Create mock users if none exist
        if (applicantRepository.findAll().isEmpty()) {
            createMockApplicants();
        }
        
        if (officerRepository.findAll().isEmpty()) {
            createMockOfficers();
        }
        
        if (managerRepository.findAll().isEmpty()) {
            createMockManagers();
        }
        
        System.out.println("Mock data created successfully");
    }
    
    /**
     * Create mock projects
     */
    private static void createMockProjects() {
        // Create a few projects
        Project project1 = new Project(
            "Sunset Gardens",
            "Beautiful housing project in the west",
            new Date(),
            new Date(System.currentTimeMillis() + 90 * 24 * 60 * 60 * 1000L), // 90 days from now
            5 // 5 slots for officers
        );
        
        // Set project as visible and open
        project1.toggleVisibility(true);
        
        // Add flats to the project
        Flat flat1_2Room = new Flat(FlatType.TWO_ROOM, 50, 250000);
        Flat flat1_3Room = new Flat(FlatType.THREE_ROOM, 75, 350000);
        project1.addFlat(flat1_2Room);
        project1.addFlat(flat1_3Room);
        
        Project project2 = new Project(
            "Parkview Residences",
            "Modern apartments near central park",
            new Date(),
            new Date(System.currentTimeMillis() + 60 * 24 * 60 * 60 * 1000L), // 60 days from now
            3 // 3 slots for officers
        );
        
        // Set project as visible and open
        project2.toggleVisibility(true);
        
        // Add flats to the project
        Flat flat2_2Room = new Flat(FlatType.TWO_ROOM, 40, 280000);
        Flat flat2_3Room = new Flat(FlatType.THREE_ROOM, 80, 380000);
        project2.addFlat(flat2_2Room);
        project2.addFlat(flat2_3Room);
        
        Project project3 = new Project(
            "Riverside Heights",
            "Luxurious apartments with river view",
            new Date(),
            new Date(System.currentTimeMillis() + 45 * 24 * 60 * 60 * 1000L), // 45 days from now
            4 // 4 slots for officers
        );
        
        // Set project as visible and open
        project3.toggleVisibility(true);
        
        // Add flats to the project
        Flat flat3_2Room = new Flat(FlatType.TWO_ROOM, 60, 320000);
        Flat flat3_3Room = new Flat(FlatType.THREE_ROOM, 40, 420000);
        project3.addFlat(flat3_2Room);
        project3.addFlat(flat3_3Room);
        
        // Save projects
        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);
        
        System.out.println("Created 3 mock projects");
    }
    
    /**
     * Create mock applicants
     */
    private static void createMockApplicants() {
        // Create applicants
        Applicant applicant1 = new Applicant("T1234567A", "John Applicant", "password", 32, User.MaritalStatus.MARRIED);
        Applicant applicant2 = new Applicant("S2345678A", "Jane Doe", "password", 28, User.MaritalStatus.SINGLE);
        Applicant applicant3 = new Applicant("S3456789A", "Robert Smith", "password", 35, User.MaritalStatus.MARRIED);
        
        // Save applicants
        applicantRepository.save(applicant1);
        applicantRepository.save(applicant2);
        applicantRepository.save(applicant3);
        
        System.out.println("Created 3 mock applicants");
    }
    
    /**
     * Create mock officers
     */
    private static void createMockOfficers() {
        // Create officers
        HDBOfficer officer1 = new HDBOfficer("S1234567T", "Alice Officer", "password", 30, User.MaritalStatus.MARRIED);
        officer1.setRegistrationStatus(RegistrationStatus.APPROVED);
        
        HDBOfficer officer2 = new HDBOfficer("T2345678S", "Bob Lee", "password", 35, User.MaritalStatus.SINGLE);
        officer2.setRegistrationStatus(RegistrationStatus.PENDING);
        
        HDBOfficer officer3 = new HDBOfficer("S3456789T", "Carol Chen", "password", 28, User.MaritalStatus.MARRIED);
        officer3.setRegistrationStatus(RegistrationStatus.APPROVED);
        
        // Save officers
        officerRepository.save(officer1);
        officerRepository.save(officer2);
        officerRepository.save(officer3);
        
        System.out.println("Created 3 mock officers");
    }
    
    /**
     * Create mock managers
     */
    private static void createMockManagers() {
        // Create managers
        HDBManager manager1 = new HDBManager("S1234567M", "David Manager", "password", 40, User.MaritalStatus.MARRIED);
        HDBManager manager2 = new HDBManager("T2345678M", "Emily Wong", "password", 45, User.MaritalStatus.SINGLE);
        
        // Assign projects to managers (assuming we have projects)
        List<Project> projects = projectRepository.findAll();
        if (!projects.isEmpty()) {
            if (projects.size() >= 1) {
                Project project1 = projects.get(0);
                manager1.addCreatedProject(project1);
                projectRepository.save(project1);
            }
            
            if (projects.size() >= 3) {
                Project project2 = projects.get(1);
                Project project3 = projects.get(2);
                manager2.addCreatedProject(project2);
                manager2.addCreatedProject(project3);
                projectRepository.save(project2);
                projectRepository.save(project3);
            }
        }
        
        // Save managers
        managerRepository.save(manager1);
        managerRepository.save(manager2);
        
        System.out.println("Created 2 mock managers and assigned projects");
    }
}
