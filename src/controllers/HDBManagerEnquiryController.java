package controllers;

import java.util.ArrayList;

public class HDBManagerEnquiryController {
	private EnquiryRepository enquiryRepository;
	
	public List<Enquiry> getAllEnquiries(){
		//print all enquiries on screen
		return enquiryRepository.getAllEnquiries();
	}
	
	public List<Enquiry> getManagingEnquiries(HDBManager HDBManager){
		List<Enquiry> managingEnquiries = new ArrayList<>();
		
		for(Enquiry enquiry: enquiryRepository.getAllEnquiries()) {
			if (enquiry.getProject().equals(HDBManager.getCreatedProject())) {
				managingEnquiries.add(enquiry);
			}
		}
		return managingEnquiries;
	}
	
	public boolean replyToEnquiry(HDBManager HDBManager, String enquiryID, String reply) {
		Enquiry enquiry = enquiryRepository.findEnquiryByID(enquiryID);
		
		if (enquiry == null) {
			// print enquiry does not exists
			return false;
		}
		// Ensure that HDBOfficer can only reply to projects he is in charge of
		if (enquiry.getProject().equals(HDBManager.getCreatedProject())) {
			enquiry.addReply(reply);
			enquiryRepository("PLEASE ADD FILEPATH LATER", enquiry);
			return true;
		}
		
		return false;
	}
}
