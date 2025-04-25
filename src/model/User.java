package model;

import java.io.Serializable;

/**
 * Abstract base class for all users in the BTO Housing System.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nric;
    private String name;
    private String password;
    private int age;
    private MaritalStatus maritalStatus;
    
    /**
     * Enum representing the marital status of a user
     */
    public enum MaritalStatus {
        SINGLE, 
        MARRIED, 
    }
    
    /**
     * Constructor for User
     * 
     * @param nric User's NRIC/identification number
     * @param name Full name of the user
     * @param password Password for login
     * @param age Age of the user
     * @param maritalStatus Marital status of the user
     */
    public User(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        this.nric = nric;
        this.name = name;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }
    
    /**
     * Gets the NRIC of the user
     * 
     * @return The NRIC of the user
     */
    public String getNRIC() {
        return nric;
    }
    
    /**
     * Gets the name of the user
     * 
     * @return The name of the user
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the user
     * 
     * @param name The new name of the user
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the age of the user
     * 
     * @return The age of the user
     */
    public int getAge() {
        return age;
    }
    
    /**
     * Sets the age of the user
     * 
     * @param age The new age of the user
     */
    public void setAge(int age) {
        this.age = age;
    }
    
    /**
     * Gets the marital status of the user
     * 
     * @return The marital status of the user
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }
    
    /**
     * Sets the marital status of the user
     * 
     * @param maritalStatus The new marital status of the user
     */
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    
    /**
     * Validates the provided password against the user's password
     * 
     * @param password The password to validate
     * @return true if the password matches, false otherwise
     */
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
    
    /**
     * Changes the user's password
     * 
     * @param newPassword The new password
     */
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    
    @Override
    public String toString() {
        return "Name: " + name + 
               "\nNRIC: " + nric +
               "\nAge: " + age +
               "\nMarital Status: " + maritalStatus;
    }
} 