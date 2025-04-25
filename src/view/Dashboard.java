package view;

import model.User;

/**
 * Abstract base class for all dashboard views
 */
public abstract class Dashboard {
    
    protected final User currentUser;
    
    /**
     * Constructor for Dashboard
     * 
     * @param currentUser The authenticated user
     */
    public Dashboard(User currentUser) {
        this.currentUser = currentUser;
    }
    
    /**
     * Displays the dashboard and handles user interactions
     * This method should be implemented by all dashboard subclasses
     * 
     * @return true if a relogin is required, false otherwise
     */
    public abstract boolean displayDashboard();
} 