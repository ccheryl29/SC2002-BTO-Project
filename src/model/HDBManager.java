package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Represents an HDB Manager who manages overall system and project creation.
 * Extends the abstract User class.
 */
public class HDBManager extends User {
    private static final long serialVersionUID = 1L;
    
    private List<Project> createdProjects;
    
    /**
     * Constructor for HDBManager
     * 
     * @param nric NRIC of the manager
     * @param name Name of the manager
     * @param password Password for login
     * @param age Age of the manager
     * @param maritalStatus Marital status of the manager
     */
    public HDBManager(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        super(nric, name, password, age, maritalStatus);
        this.createdProjects = new ArrayList<>();
    }
    
    /**
     * Gets the list of projects created by this manager
     * 
     * @return The list of created projects
     */
    public List<Project> getCreatedProjects() {
        return createdProjects;
    }
    
    /**
     * Adds a created project to the manager's list
     * 
     * @param project The project to add
     */
    public void addCreatedProject(Project project) {
        if (!createdProjects.contains(project)) {
            createdProjects.add(project);
        }
    }
    
    
    @Override
    public String toString() {
        return super.toString() +
               "\nNumber of Created Projects: " + createdProjects.size();
    }

    public Project getHandlingProject() {
        if (createdProjects.isEmpty()) {
            return null;
        }
        if (createdProjects.get(0).getApplicationOpenDate().before(new Date()) && createdProjects.get(0).getApplicationCloseDate().after(new Date())) {
            return createdProjects.get(0);
        }
        return null;
    }
} 