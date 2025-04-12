package controllers;

import java.util.ArrayList;

public class HDBOfficerEnquiryController {
	private EnquiryRepository enquiryRepository;
	
	public List<Enquiry> getEnquires(HDBOfficer HDBOfficer){
		List<Enquiry> enquiriesForOfficer = new ArrayList<>();
		for (Enquiry enquiry: enquiryRepository.getAllEnquiries()) {
			if (enquiry.getProject().equals(HDBOfficer.getHandlingProject())){
				enquiriesForOfficer.add(enquiry);
			}
		}
		return enquiriesForOfficer;
	}
	
	public boolean replyToEnquiry(HDBOfficer HDBOfficer, String enquiryID, String reply) {
		Enquiry enquiry = enquiryRepository.findEnquiryByID(enquiryID);
		
		if (enquiry == null) {
			// print enquiry does not exists
			return false;
		}
		// Ensure that HDBOfficer can onlny reply to projects he is in charge of
		if (enquiry.getProject().equals(HDBOfficer.getHandlingProject())) {
			enquiry.addReply(reply);
			enquiryRepository("PLEASE ADD FILEPATH LATER", enquiry);
			return true;
		}
		
		return false;
	}
}
