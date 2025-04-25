package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a BTO housing project.
 */
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String neighborhood;
    private Date applicationOpenDate;
    private Date applicationCloseDate;
    private boolean visible;
    private boolean eligibleForSingles;
    private boolean eligibleForMarried;
    private boolean deleted; // Flag to indicate if project has been soft deleted
    private List<Flat> flats;
    private List<Application> applications;
    private List<HDBOfficer> registeredOfficers;
    private int availableOfficerSlots;
    private HDBManager manager;
    
    /**
     * Constructor for Project
     * 
     * @param name Name of the project
     * @param neighborhood Neighborhood/location of the project
     * @param applicationOpenDate Date when applications open
     * @param applicationCloseDate Date when applications close
     * @param availableOfficerSlots Number of available slots for officers
     */
    public Project(String name, String neighborhood, Date applicationOpenDate, 
                   Date applicationCloseDate, int availableOfficerSlots) {
        this.name = name;
        this.neighborhood = neighborhood;
        this.applicationOpenDate = applicationOpenDate;
        this.applicationCloseDate = applicationCloseDate;
        this.visible = false; // New projects are hidden by default
        this.eligibleForSingles = true; // By default, projects are eligible for all
        this.eligibleForMarried = true;
        this.deleted = false; // New projects are not deleted by default
        this.flats = new ArrayList<>();
        this.applications = new ArrayList<>();
        this.registeredOfficers = new ArrayList<>();
        this.availableOfficerSlots = availableOfficerSlots;
    }
    
    /**
     * Gets the name of the project
     * 
     * @return The project name
     */
    public String getName() {
        return name;
    }
    public HDBManager getManager() {
        return manager;
    }
    public void setManager(HDBManager manager) {
        this.manager = manager;
    }
    
    /**
     * Sets the name of the project
     * 
     * @param name The new project name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the neighborhood of the project
     * 
     * @return The neighborhood
     */
    public String getNeighborhood() {
        return neighborhood;
    }
    
    /**
     * Sets the neighborhood of the project
     * 
     * @param neighborhood The new neighborhood
     */
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
    
    /**
     * Gets the application open date
     * 
     * @return The application open date
     */
    public Date getApplicationOpenDate() {
        return applicationOpenDate;
    }
    
    /**
     * Sets the application open date
     * 
     * @param applicationOpenDate The new application open date
     */
    public void setApplicationOpenDate(Date applicationOpenDate) {
        this.applicationOpenDate = applicationOpenDate;
    }
    
    /**
     * Gets the application close date
     * 
     * @return The application close date
     */
    public Date getApplicationCloseDate() {
        return applicationCloseDate;
    }
    
    /**
     * Sets the application close date
     * 
     * @param applicationCloseDate The new application close date
     */
    public void setApplicationCloseDate(Date applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }
    
    /**
     * Checks if the project is visible
     * 
     * @return true if the project is visible, false otherwise
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Toggles the visibility of the project
     * 
     * @param visible The new visibility status
     */
    public void toggleVisibility(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Checks if the project is eligible for single applicants
     * 
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForSingles() {
        return eligibleForSingles;
    }
    
    /**
     * Sets eligibility for single applicants
     * 
     * @param eligibleForSingles The new eligibility status
     */
    public void setEligibleForSingles(boolean eligibleForSingles) {
        this.eligibleForSingles = eligibleForSingles;
    }
    
    /**
     * Checks if the project is eligible for married applicants
     * 
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForMarried() {
        return eligibleForMarried;
    }
    
    /**
     * Sets eligibility for married applicants
     * 
     * @param eligibleForMarried The new eligibility status
     */
    public void setEligibleForMarried(boolean eligibleForMarried) {
        this.eligibleForMarried = eligibleForMarried;
    }
    
    /**
     * Gets the list of flats in the project
     * 
     * @return The list of flats
     */
    public List<Flat> getFlats() {
        return flats;
    }
    
    /**
     * Adds a flat to the project
     * 
     * @param flat The flat to add
     */
    public void addFlat(Flat flat) {
        flats.add(flat);
    }
    
    /**
     * Gets the list of applications for the project
     * 
     * @return The list of applications
     */
    public List<Application> getApplications() {
        return applications;
    }
    
    /**
     * Adds an application to the project
     * 
     * @param application The application to add
     */
    public void addApplication(Application application) {
        applications.add(application);
    }
    
    /**
     * Gets the list of registered officers for the project
     * 
     * @return The list of registered officers
     */
    public List<HDBOfficer> getRegisteredOfficers() {
        return registeredOfficers;
    }
    
    /**
     * Gets the number of available officer slots
     * 
     * @return The number of available officer slots
     */
    public int getAvailableOfficerSlots() {
        return availableOfficerSlots;
    }
    
    /**
     * Sets the number of available officer slots
     * 
     * @param availableOfficerSlots The new number of available officer slots
     */
    public void setAvailableOfficerSlots(int availableOfficerSlots) {
        this.availableOfficerSlots = availableOfficerSlots;
    }
    
    /**
     * Checks if the project is full (no more officer slots available)
     * 
     * @return true if the project is full, false otherwise
     */
    public boolean isFull() {
        return registeredOfficers.size() >= availableOfficerSlots;
    }
    
    /**
     * Registers an officer for the project
     * 
     * @param officer The officer to register
     * @return true if registration succeeded, false otherwise
     */
    public boolean registerOfficer(HDBOfficer officer) {
        if (isFull()) {
            return false;
        }
        if (!registeredOfficers.contains(officer)) {
            registeredOfficers.add(officer);
            return true;
        }
        return false;
    }
    
    /**
     * Gets a flat by its type
     * 
     * @param flatType The type of flat to find
     * @return The flat of the specified type, or null if not found
     */
    public Flat getFlatByType(Flat.FlatType flatType) {
        for (Flat flat : flats) {
            if (flat.getFlatType() == flatType) {
                return flat;
            }
        }
        return null;
    }
    
    /**
     * Checks if the project has been deleted
     * 
     * @return true if the project has been deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }
    
    /**
     * Sets the deleted status of the project
     * 
     * @param deleted The new deleted status
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    @Override
    public String toString() {
        return "Project Name: " + name +
               "\nNeighborhood: " + neighborhood +
               "\nApplication Period: " + applicationOpenDate + " to " + applicationCloseDate +
               "\nVisible: " + (visible ? "Yes" : "No") +
               "\nEligible for Singles: " + (eligibleForSingles ? "Yes" : "No") +
               "\nEligible for Married: " + (eligibleForMarried ? "Yes" : "No") +
               "\nFlats: " + flats.size() +
               "\nApplications: " + applications.size() +
               "\nRegistered Officers: " + registeredOfficers.size() + "/" + availableOfficerSlots;
    }
} 