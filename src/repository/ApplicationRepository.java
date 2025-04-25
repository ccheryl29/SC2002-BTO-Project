package repository;

import model.Application;
import model.Applicant;
import model.Project;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository for managing Application entities
 */
public class ApplicationRepository extends AbstractRepository<Application, String> {
    
    /**
     * Constructor for ApplicationRepository
     * 
     * @param filePath Path to the file where applications are stored
     */
    public ApplicationRepository(String filePath) {
        super(filePath);
    }
    
    /**
     * Creates a new application ID
     * 
     * @return A unique application ID
     */
    public String generateApplicationId() {
        return "APP-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public Application findById(String id) {
        return entities.stream()
                      .filter(application -> getEntityId(application).equals(id))
                      .findFirst()
                      .orElse(null);
    }
    
    @Override
    protected String getEntityId(Application application) {
        // Since Application doesn't have an ID field in its current form,
        // we're creating an ID based on applicant's NRIC and project's name
        return application.getApplicant().getNRIC() + "-" + application.getProject().getName();
    }
    
    /**
     * Finds applications by applicant
     * 
     * @param applicant The applicant to search for
     * @return A list of applications from the specified applicant
     */
    public List<Application> findByApplicant(Applicant applicant) {
        return entities.stream()
                      .filter(application -> application.getApplicant().getNRIC().equals(applicant.getNRIC()))
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds applications for a specific project
     * 
     * @param project The project to search for
     * @return A list of applications for the specified project
     */
    public List<Application> findByProject(Project project) {
        return entities.stream()
                      .filter(application -> application.getProject().getName().equals(project.getName()))
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds applications by status
     * 
     * @param status The status to search for
     * @return A list of applications with the specified status
     */
    public List<Application> findByStatus(Application.ApplicationStatus status) {
        return entities.stream()
                      .filter(application -> application.getStatus() == status)
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds applications submitted within a date range
     * 
     * @param startDate The start of the date range
     * @param endDate The end of the date range
     * @return A list of applications submitted within the date range
     */
    public List<Application> findByDateRange(Date startDate, Date endDate) {
        return entities.stream()
                      .filter(application -> {
                          Date applicationDate = application.getApplicationDate();
                          return applicationDate.after(startDate) && applicationDate.before(endDate);
                      })
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds an application for a specific applicant and project
     * 
     * @param applicant The applicant
     * @param project The project
     * @return The matching application, or null if not found
     */
    public Application findByApplicantAndProject(Applicant applicant, Project project) {
        return entities.stream()
                      .filter(application -> 
                          application.getApplicant().getNRIC().equals(applicant.getNRIC()) &&
                          application.getProject().getName().equals(project.getName())
                      )
                      .findFirst()
                      .orElse(null);
    }
} 