package service;

import model.Applicant;
import model.HDBManager;
import model.HDBOfficer;
import model.User;
import repository.ApplicantRepository;
import repository.ManagerRepository;
import repository.OfficerRepository;

/**
 * Service for managing user account operations
 */
public class AccountManagementService {
    private final ApplicantRepository applicantRepository;
    private final OfficerRepository officerRepository;
    private final ManagerRepository managerRepository;
    
    /**
     * Constructor for AccountManagementService
     */
    public AccountManagementService(ApplicantRepository applicantRepository, OfficerRepository officerRepository, ManagerRepository managerRepository) {
        this.applicantRepository = applicantRepository;
        this.officerRepository = officerRepository;
        this.managerRepository = managerRepository;
    }
    
    /**
     * Updates a user's password
     * 
     * @param user User whose password needs to be updated
     * @param newPassword New password to set
     * @return true if password was updated successfully, false otherwise
     */
    public boolean updatePassword(User user, String newPassword) {
        if (user == null || newPassword == null || newPassword.isEmpty()) {
            return false;
        }
        
        user.changePassword(newPassword);
        
        return saveUser(user);
    }
    
    /**
     * Resets password for a user identified by NRIC
     * 
     * @param nric NRIC of the user
     * @param newPassword New password to set
     * @return true if password was reset successfully, false otherwise
     */
    public boolean resetPassword(String nric, String newPassword) {
        if (nric == null || newPassword == null || nric.isEmpty() || newPassword.isEmpty()) {
            return false;
        }
        
        // Check if user is an applicant
        Applicant applicant = applicantRepository.findById(nric);
        if (applicant != null) {
            applicant.changePassword(newPassword);
            applicantRepository.save(applicant);
            return true;
        }
        
        // Check if user is an officer
        HDBOfficer officer = officerRepository.findById(nric);
        if (officer != null) {
            officer.changePassword(newPassword);
            officerRepository.save(officer);
            return true;
        }
        
        // Check if user is a manager
        HDBManager manager = managerRepository.findById(nric);
        if (manager != null) {
            manager.changePassword(newPassword);
            managerRepository.save(manager);
            return true;
        }
        
        return false; // User not found
    }
    
    /**
     * Helper method to save the updated user to the appropriate repository
     * 
     * @param user User to save
     * @return true if saved successfully, false otherwise
     */
    private boolean saveUser(User user) {
        if (user instanceof Applicant) {
            applicantRepository.save((Applicant) user);
            return true;
        } else if (user instanceof HDBOfficer) {
            officerRepository.save((HDBOfficer) user);
            return true;
        } else if (user instanceof HDBManager) {
            managerRepository.save((HDBManager) user);
            return true;
        }
        return false;
    }
} 