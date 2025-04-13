package controllers;

public class ApplicationController {
	private ApplicantRepository applicantRepository;
	private ProjectRepository projectRepository;
	
	
	public boolean (checkApplicantEligibility(Applicant applicant, Flat flat){
		if (applicant.getCurrentApplication() != null) {
			// print the applicant had already applied for another flat
			return false;
		}
		
		if (applicant.getMaritalStatus() == "SINGLE") {
			if (flat.getNumberOfRooms() == 2) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}
	}
	
	public boolean applyForProject(Applicant applicant, String projectID) {
		Project project = projectRepository.findProjectById(projectID);
        if (project == null || !checkApplicantEligibility(applicant, project)) {
            return false;
        }
        
        Application application = new Application(applicant);
        return applicantRepository.saveApplicantApplication(Application application);
        
	}
	
	public boolean bookFlat(Applicant applicant) {
		Application application = applicantRepository.getApplication(applicant);
		if (application.getApplicationStatus() == "BOOKED") {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean updateApplicationStatus(Application applicantion, ApplicationStatus status) {
		application.updateStatus(status);
		return applicationRepository.saveApplicantApplication(application);
	}
	
	public boolean withdrawApplication(Application application) {
		application.updateStatus("WITHDRAWN");
		return applicationRepository.saveApplicantApplication(application);
	}
}
