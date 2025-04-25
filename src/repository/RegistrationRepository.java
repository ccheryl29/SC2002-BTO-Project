package repository;

import model.HDBOfficer;
import model.Project;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository for managing the registration relationship between officers and projects
 */
public class RegistrationRepository extends AbstractRepository<RegistrationRepository.Registration, String> {
    
    /**
     * Constructor for RegistrationRepository
     * 
     * @param filePath Path to the file where registrations are stored
     */
    public RegistrationRepository(String filePath) {
        super(filePath);
    }
    
    /**
     * Creates a new registration between an officer and a project
     * 
     * @param officer The officer to register
     * @param project The project to register for
     * @return The created registration
     */
    public Registration createRegistration(HDBOfficer officer, Project project) {
        Registration registration = new Registration(
                generateRegistrationId(),
                officer,
                project,
                new Date(),
                HDBOfficer.RegistrationStatus.PENDING
        );
        return save(registration);
    }
    
    /**
     * Generates a unique registration ID
     * 
     * @return A new unique registration ID
     */
    public String generateRegistrationId() {
        return "REG-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public Registration findById(String id) {
        return entities.stream()
                      .filter(registration -> registration.getRegistrationId().equals(id))
                      .findFirst()
                      .orElse(null);
    }
    
    @Override
    protected String getEntityId(Registration registration) {
        return registration.getRegistrationId();
    }
    
    /**
     * Finds registrations by officer
     * 
     * @param officer The officer to search for
     * @return A list of registrations for the specified officer
     */
    public List<Registration> findByOfficer(HDBOfficer officer) {
        return entities.stream()
                      .filter(registration -> registration.getOfficer().getNRIC().equals(officer.getNRIC()))
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds registrations for a specific project
     * 
     * @param project The project to search for
     * @return A list of registrations for the specified project
     */
    public List<Registration> findByProject(Project project) {
        return entities.stream()
                      .filter(registration -> registration.getProject().getName().equals(project.getName()))
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds registrations by status
     * 
     * @param status The status to search for
     * @return A list of registrations with the specified status
     */
    public List<Registration> findByStatus(HDBOfficer.RegistrationStatus status) {
        return entities.stream()
                      .filter(registration -> registration.getStatus() == status)
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds a registration for a specific officer and project
     * 
     * @param officer The officer
     * @param project The project
     * @return The matching registration, or null if not found
     */
    public Registration findByOfficerAndProject(HDBOfficer officer, Project project) {
        return entities.stream()
                      .filter(registration -> 
                          registration.getOfficer().getNRIC().equals(officer.getNRIC()) &&
                          registration.getProject().getName().equals(project.getName())
                      )
                      .findFirst()
                      .orElse(null);
    }
    
    /**
     * Class representing a registration between an officer and a project
     */
    public static class Registration implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String registrationId;
        private HDBOfficer officer;
        private Project project;
        private Date registrationDate;
        private HDBOfficer.RegistrationStatus status;
        
        /**
         * Constructor for Registration
         * 
         * @param registrationId The registration ID
         * @param officer The officer being registered
         * @param project The project being registered for
         * @param registrationDate The date of registration
         * @param status The status of the registration
         */
        public Registration(String registrationId, HDBOfficer officer, Project project, 
                           Date registrationDate, HDBOfficer.RegistrationStatus status) {
            this.registrationId = registrationId;
            this.officer = officer;
            this.project = project;
            this.registrationDate = registrationDate;
            this.status = status;
        }
        
        public String getRegistrationId() {
            return registrationId;
        }
        
        public HDBOfficer getOfficer() {
            return officer;
        }
        
        public Project getProject() {
            return project;
        }
        
        public Date getRegistrationDate() {
            return registrationDate;
        }
        
        public HDBOfficer.RegistrationStatus getStatus() {
            return status;
        }
        
        public void setStatus(HDBOfficer.RegistrationStatus status) {
            this.status = status;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            
            Registration that = (Registration) o;
            return registrationId.equals(that.registrationId);
        }
        
        @Override
        public int hashCode() {
            return registrationId.hashCode();
        }
        
        @Override
        public String toString() {
            return "Registration ID: " + registrationId +
                   "\nOfficer: " + officer.getName() +
                   "\nProject: " + project.getName() +
                   "\nRegistration Date: " + registrationDate +
                   "\nStatus: " + status;
        }
    }
} 