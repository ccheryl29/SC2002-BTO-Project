package controllers;

import java.util.UUID;
import java.util.ArrayList;

public class ApplicantEnquiryController {
	private EnquiryRepository enquiryRepository;
	
	public List<Enquiry> getenquiries(User user){
		List<Enquiry> enquiriesByUser = new ArrayList<>();
		for (Enquiry enquiry: enquiryRepository.getAllEnquiries()) {
			if (enquiry.getApplicantNRIC().equals(user.getNRIC())) {
				enquiriesByUser.add(enquiry);
			}
		}
		return enquiriesByUser;
	}
	
	public boolean submitEquiry(User user, Project project, String message) {
		//Generate a unqiue ID for the enquiry
		String id;
		do {
			id = UUID.randomUUID().toString();
		} while (enquiryRepository.findEquiryByID(id) != null);
		
		Enquiry newEnquiry = new Enquiry(id, user, project, message);
		try {
			enquiryRepository.saveEnquiries("PLEASE ADD LATER!", newEnquiry);
			//System.out.println("Enquiry has been submitted.");
			return true;
		} catch (Exception e) {
			//System.out.println("Unable to save enquiry");
			return false;
		}
		
	}
	
	public boolean updateEnquiry(User user, String enquiryID, String newMessage) {
		Enquiry enquiryToEdit = enquiryRepository.findEnquiryByID(enquiryID);
		if (enquiryToEdit != null && enquiryToEdit.getApplicantNRIC().equals(user.getNRIC())) {
			enquiryToEdit.updateMessage(newMessage);
			//System.out.println("Enquiry has been updated.");
			return true;
		}
		//print enquiry does not exist or that they are not the person that wrote the enquiry
		return false;
	}
	
}
