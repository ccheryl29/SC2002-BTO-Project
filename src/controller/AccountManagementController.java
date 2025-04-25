package controller;

import model.User;
import service.AccountManagementService;

import java.util.regex.Pattern;

/**
 * Controller for managing user account operations
 */
public class AccountManagementController {
    private final AccountManagementService accountService;
    private User currentUser;
    
    // Regular expression for NRIC validation: Starts with S or T, followed by 7 digits, and ends with a letter
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Za-z]$");
    
    /**
     * Constructor for AccountManagementController
     * 
     * @param accountService Service for account management operations
     * @param currentUser Currently logged-in user
     */
    public AccountManagementController(AccountManagementService accountService, User currentUser) {
        this.accountService = accountService;
        this.currentUser = currentUser;
    }
    
    /**
     * Changes the current user's password
     * 
     * @param currentPassword Current password for verification
     * @param newPassword New password to set
     * @return true if password change was successful, false otherwise
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        if (currentPassword == null || newPassword == null || 
            currentPassword.isEmpty() || newPassword.isEmpty()) {
            return false;
        }
        
        // Verify current password
        if (!currentUser.validatePassword(currentPassword)) {
            return false;
        }
        
        // Validate the new password
        if (!isValidPassword(newPassword)) {
            return false;
        }
        
        return accountService.updatePassword(currentUser, newPassword);
    }
    
    /**
     * Resets password to a new one
     * 
     * @param nric NRIC of the user to reset password for (admin use)
     * @param newPassword New password to set
     * @return true if password reset was successful, false otherwise
     */
    public boolean resetPassword(String nric, String newPassword) {
        if (nric == null || newPassword == null || nric.isEmpty() || newPassword.isEmpty()) {
            return false;
        }
        
        // Validate NRIC format
        if (!isValidNRIC(nric)) {
            return false;
        }
        
        // Validate the new password
        if (!isValidPassword(newPassword)) {
            return false;
        }
        
        return accountService.resetPassword(nric, newPassword);
    }
    public void setCurrentUser(User user) {
        this.currentUser = user;
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
     * Validates if a password meets security requirements
     * 
     * @param password Password to validate
     * @return true if password is valid, false otherwise
     */
    private boolean isValidPassword(String password) {
        // Password should be at least 8 characters long
        if (password.length() < 8) {
            return false;
        }
        
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        
        // Password should have at least one uppercase letter, one lowercase letter, and one digit
        return hasUpperCase && hasLowerCase && hasDigit;
    }
} 