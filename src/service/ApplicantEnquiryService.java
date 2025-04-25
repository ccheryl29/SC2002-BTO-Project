package service;

import model.Applicant;
import model.Enquiry;
import model.Project;
import repository.EnquiryRepository;
import repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service class for handling applicant enquiry operations
 */
public class ApplicantEnquiryService {
    
    private final EnquiryRepository enquiryRepository;
    private final ProjectRepository projectRepository;
    
    /**
     * Constructor for ApplicantEnquiryService
     */
    public ApplicantEnquiryService(EnquiryRepository enquiryRepository, ProjectRepository projectRepository) {
        this.enquiryRepository = enquiryRepository;
        this.projectRepository = projectRepository;
    }
    
    /**
     * Gets all enquiries submitted by a specific applicant
     * 
     * @param applicant The applicant
     * @return List of enquiries submitted by the applicant
     */
    public List<Enquiry> getEnquiriesByApplicant(Applicant applicant) {
        if (applicant == null) {
            return new ArrayList<>();
        }
        
        List<Enquiry> allEnquiries = enquiryRepository.findAll();
        List<Enquiry> applicantEnquiries = new ArrayList<>();
        
        for (Enquiry enquiry : allEnquiries) {
            if (enquiry.getApplicant().getNRIC().equals(applicant.getNRIC())) {
                applicantEnquiries.add(enquiry);
            }
        }
        
        return applicantEnquiries;
    }
    
    /**
     * Gets an enquiry by its ID, if it belongs to the specified applicant
     * 
     * @param enquiryId The ID of the enquiry
     * @param applicant The applicant
     * @return The enquiry, or null if not found or doesn't belong to the applicant
     */
    public Enquiry getEnquiryById(String enquiryId, Applicant applicant ) {
        if (enquiryId == null || applicant == null) {
            return null;
        }
        
        Enquiry enquiry = enquiryRepository.findById(enquiryId);
        
        if (enquiry != null && enquiry.getApplicant().getNRIC().equals(applicant.getNRIC())) {
            return enquiry;
        }
        
        return null;
    }
    
    /**
     * Submits a new enquiry
     * 
     * @param applicant The applicant submitting the enquiry
     * @param projectName The name of the project
     * @param question The enquiry question
     * @return The created enquiry, or null if project doesn't exist
     */
    public Enquiry submitEnquiry(Applicant applicant, String projectName, String question) {
        if (applicant == null || projectName == null || question == null) {
            return null;
        }
        
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return null;
        }
        
        String enquiryId = generateEnquiryId();
        Enquiry enquiry = new Enquiry(enquiryId, applicant, project, question.trim());
        
        enquiryRepository.save(enquiry);
        return enquiry;
    }
    
    /**
     * Updates an enquiry's question
     * 
     * @param enquiryId The ID of the enquiry
     * @param applicant The applicant
     * @param newQuestion The new question
     * @return true if updated successfully, false otherwise
     */
    public boolean updateEnquiry(String enquiryId, Applicant applicant, String newQuestion) {
        if (enquiryId == null || applicant == null || newQuestion == null) {
            return false;
        }
        
        Enquiry enquiry = getEnquiryById(enquiryId, applicant);
        
        if (enquiry == null) {
            return false;
        }
        
        // Only allow updates if the enquiry has not been responded to
        if (enquiry.getStatus() != Enquiry.EnquiryStatus.PENDING) {
            return false;
        }
        
        enquiry.setQuestion(newQuestion.trim());
        enquiryRepository.save(enquiry);
        
        return true;
    }
    
    /**
     * Deletes an enquiry
     * 
     * @param enquiryId The ID of the enquiry
     * @param applicant The applicant
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteEnquiry(String enquiryId, Applicant applicant) {
        if (enquiryId == null || applicant == null) {
            return false;
        }
        
        Enquiry enquiry = getEnquiryById(enquiryId, applicant);
        
        if (enquiry == null) {
            return false;
        }
        
        // Only allow deletion if the enquiry has not been responded to
        if (enquiry.getStatus() != Enquiry.EnquiryStatus.PENDING) {
            return false;
        }
        
        enquiryRepository.delete(enquiry);
        return true;
    }
    
    /**
     * Gets all projects that accept enquiries
     * 
     * @return List of all visible projects
     */
    public List<Project> getAvailableProjects() {
        List<Project> allProjects = projectRepository.findVisibleProjects();
        

        return allProjects;

    }
    
    /**
     * Generates a unique ID for a new enquiry
     * 
     * @return A unique enquiry ID
     */
    private String generateEnquiryId() {
        return "ENQ-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * Gets all enquiries submitted by a specific applicant for a specific project
     * 
     * @param applicant The applicant
     * @param projectName The name of the project
     * @return List of enquiries submitted by the applicant for the specified project
     */
    public List<Enquiry> getEnquiriesByApplicantAndProject(Applicant applicant, String projectName) {
        if (applicant == null || projectName == null) {
            return new ArrayList<>();
        }
        
        List<Enquiry> applicantEnquiries = getEnquiriesByApplicant(applicant);
        List<Enquiry> projectEnquiries = new ArrayList<>();
        
        for (Enquiry enquiry : applicantEnquiries) {
            if (enquiry.getProject().getName().equals(projectName)) {
                projectEnquiries.add(enquiry);
            }
        }
        
        return projectEnquiries;
    }
    
    /**
     * Gets all pending enquiries submitted by a specific applicant for a specific project
     * 
     * @param applicant The applicant
     * @param projectName The name of the project
     * @return List of pending enquiries submitted by the applicant for the specified project
     */
    public List<Enquiry> getPendingEnquiriesByApplicantAndProject(Applicant applicant, String projectName) {
        if (applicant == null || projectName == null) {
            return new ArrayList<>();
        }
        
        List<Enquiry> projectEnquiries = getEnquiriesByApplicantAndProject(applicant, projectName);
        List<Enquiry> pendingEnquiries = new ArrayList<>();
        
        for (Enquiry enquiry : projectEnquiries) {
            if (enquiry.getStatus() == Enquiry.EnquiryStatus.PENDING) {
                pendingEnquiries.add(enquiry);
            }
        }
        
        return pendingEnquiries;
    }
    
    /**
     * Counts the number of enquiries submitted by a specific applicant for a specific project
     * 
     * @param applicant The applicant
     * @param projectName The name of the project
     * @return The number of enquiries submitted by the applicant for the specified project
     */
    public int getEnquiryCountByApplicantAndProject(Applicant applicant, String projectName) {
        return getEnquiriesByApplicantAndProject(applicant, projectName).size();
    }
    
    /**
     * Counts the number of pending enquiries submitted by a specific applicant for a specific project
     * 
     * @param applicant The applicant
     * @param projectName The name of the project
     * @return The number of pending enquiries submitted by the applicant for the specified project
     */
    public int getPendingEnquiryCountByApplicantAndProject(Applicant applicant, String projectName) {
        return getPendingEnquiriesByApplicantAndProject(applicant, projectName).size();
    }
} 