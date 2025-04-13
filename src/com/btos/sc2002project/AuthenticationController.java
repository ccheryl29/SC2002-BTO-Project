package controllers;

public class AuthenticationController {
	private UserRepository userRepository;
	
	public boolean login(String username, String password){
		//check if NRIC / user exists
		if (userRepository.findUserByNRIC(username) == null) {
			return false;
		}
		// Check if password matches
		if (userRepository.getUserPassword(username).equals(password)) {
			return true;
		}
		// If password does not match, print login unsuccessful
		return false;
	}
	
	public boolean changePassword(User user, String newPassword, String currentPassword){
		//Check if current password matches the password provided
		if (currentPassword.equals(getUserPassword(user.getNRIC()))) {
			userRepository.saveNewPassword(newPassword);
			return true;
		}
		return false;
	}
}
