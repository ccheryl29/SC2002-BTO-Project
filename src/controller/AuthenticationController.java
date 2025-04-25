package controller;

import model.Applicant;
import model.HDBManager;
import model.HDBOfficer;
import model.User;
import repository.ApplicantRepository;
import repository.ManagerRepository;
import repository.OfficerRepository;

import java.util.regex.Pattern;

/**
 * Controller for handling user authentication across different user types
 */
public class AuthenticationController {
    private final ApplicantRepository applicantRepository;
    private final OfficerRepository officerRepository;
    private final ManagerRepository managerRepository;
    
    // Regular expression for NRIC validation: Starts with S or T, followed by 7 digits, and ends with a letter
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Za-z]$");
    
    /**
     * Constructor for AuthenticationController
     * 
     * @param applicantRepository Repository for applicant data
     * @param officerRepository Repository for officer data
     * @param managerRepository Repository for manager data
     */
    public AuthenticationController(
            ApplicantRepository applicantRepository,
            OfficerRepository officerRepository,
            ManagerRepository managerRepository) {
        this.applicantRepository = applicantRepository;
        this.officerRepository = officerRepository;
        this.managerRepository = managerRepository;
    }
    /**
     * Enum for user types
     */
    public enum UserType {
        APPLICANT,
        OFFICER,
        MANAGER
    }
    /**
     * Authenticates a user based on the provided credentials and user type
     * 
     * @param userType The type of user (APPLICANT, OFFICER, MANAGER)
     * @param nric The NRIC of the user
     * @param password The password of the user
     * @return The authenticated User object or null if authentication fails
     */
    public User authenticate(UserType userType, String nric, String password) {
        if (nric == null || password == null || nric.isEmpty() || password.isEmpty()) {
            return null;
        }
        
        // Validate NRIC format
        if (!isValidNRIC(nric)) {
            return null;
        }
        
        switch (userType) {
            case APPLICANT:
                return authenticateApplicant(nric, password);
            case OFFICER:
                return authenticateOfficer(nric, password);
            case MANAGER:
                return authenticateManager(nric, password);
            default:
                return null;
        }
    }
    
    /**
     * Validates if the given string is a valid NRIC
     * 
     * @param nric The NRIC string to validate
     * @return true if the NRIC is valid, false otherwise
     */
    private boolean isValidNRIC(String nric) {
        return NRIC_PATTERN.matcher(nric).matches();
    }
    
    /**
     * Authenticates an applicant
     * 
     * @param nric The NRIC of the applicant
     * @param password The password of the applicant
     * @return The authenticated Applicant or null if authentication fails
     */
    private Applicant authenticateApplicant(String nric, String password) {
        Applicant applicant = applicantRepository.findById(nric);
        
        if (applicant != null && applicant.validatePassword(password)) {
            return applicant;
        }
        return null;
    }
    
    /**
     * Authenticates an HDB officer
     * 
     * @param nric The NRIC of the officer
     * @param password The password of the officer
     * @return The authenticated HDBOfficer or null if authentication fails
     */
    private HDBOfficer authenticateOfficer(String nric, String password) {
        HDBOfficer officer = officerRepository.findById(nric);
        
        if (officer != null && officer.validatePassword(password)) {
            return officer;
        }
        return null;
    }
    
    /**
     * Authenticates an HDB manager
     * 
     * @param nric The NRIC of the manager
     * @param password The password of the manager
     * @return The authenticated HDBManager or null if authentication fails
     */
    private HDBManager authenticateManager(String nric, String password) {
        HDBManager manager = managerRepository.findById(nric);
        
        if (manager != null && manager.validatePassword(password)) {
            return manager;
        }
        return null;
    }
} 