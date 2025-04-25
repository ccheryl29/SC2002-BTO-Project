package model;

import java.io.Serializable;

/**
 * Represents an applicant who can apply for BTO flats.
 * Extends the abstract User class.
 */
public class Applicant extends User {
    private static final long serialVersionUID = 1L;
    
    protected Application currentApplication;
    
    /**
     * Constructor for Applicant
     * 
     * @param nric NRIC of the applicant
     * @param name Name of the applicant
     * @param password Password for login
     * @param age Age of the applicant
     * @param maritalStatus Marital status of the applicant
     */
    public Applicant(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        super(nric, name, password, age, maritalStatus);
        this.currentApplication = null;
    }
    
    /**
     * Gets the current application of the applicant
     * 
     * @return The current application
     */
    public Application getCurrentApplication() {
        return currentApplication;
    }
    
    /**
     * Sets the current application of the applicant
     * 
     * @param application The new current application
     */
    public void setCurrentApplication(Application application) {
        this.currentApplication = application;
    }
    

    
    @Override
    public String toString() {
        String applicantInfo = super.toString();
        
        if (currentApplication != null) {
            applicantInfo += "\nCurrent Application: " + currentApplication.getProject().getName();
        } else {
            applicantInfo += "\nNo Current Application";
        }
        
        return applicantInfo;
    }
} 