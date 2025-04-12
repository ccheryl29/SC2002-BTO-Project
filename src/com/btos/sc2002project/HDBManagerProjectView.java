package com.btos.sc2002project;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HDBManagerProjectView {
	private final HDBManagerProjectController mgrProjCtrl;
	private final ApplicationController currAppCtrl;
	private final HDBOfficerRegisterController offRegCtrl;
	private final HDBManagerDashboard dashboard;
	private final Scanner sc;
	
	public HDBManagerProjectView(HDBManagerProjectController mgrProjCtrl,
			ApplicationController currAppCtrl,
			HDBOfficerRegisterController offRegCtrl,
			HDBManagerDashboard dashboard) {
		this.mgrProjCtrl = mgrProjCtrl;
		this.currAppCtrl = currAppCtrl;
		this.offRegCtrl = offRegCtrl;
		this.dashboard = dashboard;
		this.sc = new Scanner(System.in);
	}
	
	public void displayProjectMenu(HDBManager user) {
		boolean viewProjectMenu = true;
		while (viewProjectMenu == true) {
			System.out.println("-------Project Menu-------");
			System.out.println("1. Create New Project");
			System.out.println("2. Edit Existing Project");
			System.out.println("3. Delete Project");
			System.out.println("4. View Own Projects");
			System.out.println("5. Toggle Visibility");
			System.out.println("6. Exit");
			System.out.println("--------------------------");
			int choice = readInt("\nSelect option: ");
            
			switch(choice) {
				case 1:
					createProjectView(user);
					break;
				case 2:
					editProjectView(user);
					break;
				case 3:
					deleteProjectView(user);
					break;
				case 4:
					showOwnProjects(user);
					break;
				case 5:
					promptChangeVisibility(user);
					break;
				case 6:
					viewProjectMenu = false;
					returnToDashboard(user);
					break;
				default:
					System.out.println("Invalid choice.");
					break;
			}	
		}
	}
	
	public void createProjectView(HDBManager user) {
	    String projName = readString("\nEnter Project Name: ");
	    String location = readString("\nEnter Location: ");
	    LocalDate openDate = readDate("\nEnter Opening Date (yyyy-mm-dd): ");
	    LocalDate closeDate = readDate("\nEnter Closing Date (yyyy-mm-dd): ");
	    int numOfTypes = readInt("\nEnter number of Types of Flats: ");

	    int[] rooms = new int[numOfTypes];
	    double[] price = new double[numOfTypes];
	    for (int i = 0; i < numOfTypes; i++) {
	        rooms[i] = readInt("\nEnter number of rooms for Type " + (i + 1) + ": ");
	        price[i] = readDouble("Enter selling price for Type " + (i + 1) + " rooms: ");
	    }

	    int numOfSlots = readInt("\nEnter number of officer slots: ");

	    boolean created = mgrProjCtrl.createProject(user, projName, location, openDate, closeDate, 
	    		rooms, price, numOfSlots);
	    if (created) {
	        System.out.println("Project successfully created!");
	    } else {
	        System.out.println("Failed to create new project. Please try again.");
	    }
	}

	
	public void editProjectView(HDBManager user) {
		List<Project> projects = mgrProjCtrl.getProjects(user);
		if (projects.isEmpty()) {
			System.out.println("No projects created.");
			return;
		} else { System.out.println("---------List of Projects---------"); }
		for(Project proj : projects) {
			System.out.println("#"+proj.getProjID()+" - "+proj.getName());
		}
		System.out.print("\nEnter ID of project to edit: ");
		String editProjID = sc.next();
		Project project = mgrProjCtrl.getProjectByID(editProjID);
		//give manager choice of which feature of project to be edited individually
		System.out.println("Aspects of Project that can be edited are");
		System.out.println("1. Name, 2. Location, 3. Opening Date, 4. Closing Date, 5. Room Type, 6. Price");
		int choice = readInt("\nEnter your choice: ");
        
		switch(choice) {
			case 1:
				System.out.print("\nEnter new project name: ");
				String newName = sc.next();
				mgrProjCtrl.updateProjectName(project, newName);
				break;
			case 2:
				System.out.print("\nEnter new location: ");
				String newLoc = sc.next();
				mgrProjCtrl.updateLocation(project, newLoc);
				break;
			case 3:
				DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				System.out.print("\nEnter new opening date (yyyy-mm-dd): ");
				String newOpenDateStr = sc.next();
				LocalDate newOpenDate = LocalDate.parse(newOpenDateStr, formatter3);
				mgrProjCtrl.updateOpeningDate(project, newOpenDate);
				break;
			case 4:
				DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				System.out.print("\nEnter new closing date (yyyy-mm-dd): ");
				String newCloseDateStr = sc.next();
				LocalDate newCloseDate = LocalDate.parse(newCloseDateStr, formatter4);
				mgrProjCtrl.updateClosingDate(project, newCloseDate);
				break;					
			case 5:
				System.out.println("Current Room Types:");
	               List<Integer> roomTypes = project.getRoomTypes();
	               for (int i=0; i<roomTypes.size(); i++) {
	                   System.out.println((i+1)+". "+roomTypes.get(i)+" rooms");
	               }
	               System.out.print("\na. Add or b. Edit room types? (A/B): ");
	               String roomChoice = sc.next();
	               
	               //adding new room type
	               if (roomChoice.toUpperCase() == "A") {
	               	System.out.print("\nEnter number of rooms: ");
	               	int newRooms = sc.nextInt();
	               	System.out.print("\nEnter price: ");
	               	double newRoomPrice = sc.nextDouble();
	               	mgrProjCtrl.addRoomType(project, newRooms, newRoomPrice);
	               } 
	               //editing existing room type (changing number of rooms)
	               else if (roomChoice.toUpperCase() == "B") {
		               	System.out.print("\nEnter index of room type to edit: ");
		               	//IMPLEMENT TRY CATCH 
		               	int idx = sc.nextInt()-1;
		               	System.out.print("\nEnter new number of rooms: ");
		               	int newRoomType = sc.nextInt();
		               	mgrProjCtrl.changeRoomType(project, idx, newRoomType);
		           }
	            break;
			case 6:
				System.out.println("Current Rooms & Prices:");
				List<Integer> rooms = project.getRoomTypes();
				List<Double> prices = project.getPrices();
	            for (int i = 0; i < prices.size(); i++) {
	               System.out.println((i+1)+". "+roomTypes.get(i)+" rooms: $"+prices.get(i));
	            }
	            System.out.print("Enter index of room price to edit: ");
	            int priceIdx = sc.nextInt()-1;
	            System.out.print("Enter new price: ");
	            double newPrice = sc.nextDouble();
	            mgrProjCtrl.updateRoomPrice(project, priceIdx, newPrice);
	            break;
			default:
				System.out.println("Invalid choice.");
		}
	}
	
	public void deleteProjectView(HDBManager user) {
		List<Project> projects = mgrProjCtrl.getProjects(user);
		if (projects.isEmpty()) {
			System.out.println("No projects created.");
			return;
		} else { System.out.println("---------List of Projects---------"); }
		for(Project proj : projects) {
			System.out.println("#"+proj.getProjID()+" - "+proj.getName());
		}
		System.out.print("\nEnter ID of project to delete: ");
		String delProjID = sc.next();
		
		//confirmation to delete project
		System.out.print("Confirm deletion of project #"+delProjID+"? (Y/N): ");
		String boolDel = sc.next().trim();
		if (boolDel.equalsIgnoreCase("Y")) {
			boolean deleted = mgrProjCtrl.deleteProject(user, delProjID);
			if (deleted == true) {
				System.out.println("Project successfully deleted.");
			} else { System.out.println("Failed to delete project. Please try again."); }
		} else if (boolDel.equalsIgnoreCase("N")) {
			System.out.println("Returning to project menu...");
		}
	}
	
	public void showAllProjects(HDBManager user) {
		List<Project> allProjects = mgrProjCtrl.getAllProjects();
		if (allProjects.isEmpty()) {
			System.out.println("No projects created.");
			break;
		} else { System.out.println("--------Available Projects--------"); }	
			//display each eligible project
		for (Project project : allProjects) {
				System.out.println("Name: "+project.getName());
				System.out.println("Location: "+project.getLocation());
				System.out.println("Manager-in-charge: "+project.getManager());
				System.out.println("----------------------------------");
		}
		System.out.println("Press <ENTER> to return to home page  ");
		sc.nextLine();
		sc.nextLine();
		returnToDashboard(user);
	}
	
	public void showOwnProjects(HDBManager user, FilterSettings filterSettings) {
		List<Project> ownProjects = mgrProjCtrl.getOwnFilteredProjects(user, filterSettings);
		if (ownProjects.isEmpty()) {
			System.out.println("No projects created.");
			break;
		} else { System.out.println("---------Created Projects--------"); }	
			//display each project manager created
		for (Project project : ownProjects) {
				System.out.println("Name: "+project.getName());
				System.out.println("Location: "+project.getLocation());
				System.out.println("Manager-in-charge: "+project.getManager());
				System.out.println("----------------------------------");
		}
		System.out.println("Press <ENTER> to return to home page  ");
		sc.nextLine();
		sc.nextLine();
		returnToDashboard(user);
	}
	
	private void promptChangeVisibility(HDBManager user) {
		List<Project> ownProjects = mgrProjCtrl.getOwnProjects(user);
		if (ownProjects.isEmpty()) {
			System.out.println("No projects created.");
			break;
		} else { 
			System.out.print("Enter project ID to edit visibility: ");
			String visID = sc.nextLine();
			Project changeVisibility = mgrProjCtrl.getProject(visID);
			System.out.println("Current project visibility is "+changeVisibility.getVisibility());
			System.out.print("Confirm to switch? (Y/N): ");
			String chgVis = sc.nextLine();
			if (chgVis.trim().toUpperCase() == "Y") {
				mgrProjCtrl.toggleVisibility(changeVisibility);
				System.out.println("Project visibility changed to "+changeVisibility.getVisibility());
				System.out.println("Returning to project menu...");
			} else { System.out.println("Failed to change visibility. Please try again."); }
		}
	}
	
	public void displayFilterSetting(HDBManager user) {
		FilterSettings currentFilter = user.getFilterSettings(); //NEW FILTERSETTINGS CLASS
		boolean setting = true;
		while (setting) {
			System.out.println("-----Filter Settings-----");
			System.out.println("1. Location");
			System.out.println("2. Flat Type");
			System.out.println("3. Price (Low to High)");
			System.out.println("4. Price (High to Low)");
			System.out.println("5. Reset to default");
			System.out.println("6. Return to home page");
			System.out.print("Enter your choice: ");
			
			int filterChoice = -1;

	        try {
	            filterChoice = Integer.parseInt(sc.nextLine());
	        } catch (NumberFormatException e) {
	            System.out.println("Invalid input. Please enter a number.");
	            continue;
	        }
			
			switch (filterChoice) {
				case 1:
					System.out.print("Enter desired location: ");
					String location = sc.nextLine();
					currentFilter.setLocation(location);
					System.out.println("Filter setting applied.");
					System.out.println("Projects at "+location.toLowerCase()+" will be shown.");
					break;
				case 2:
					System.out.print("Enter number of rooms: ");
					int numRooms = sc.nextInt();
					sc.nextLine();
					currentFilter.setFlatType(numRooms);
					System.out.println("Filter setting applied.");
					System.out.println("Projects with "+numRooms+"-room flats will be shown.");
					break;
				case 3:
					currentFilter.setPriceLowToHigh();
					System.out.println("Filter setting applied.");
					System.out.println("Projects will be sorted in increasing price order.");
					break;
				case 4:
					currentFilter.setPriceHighToLow();
					System.out.println("Filter setting applied.");
					System.out.println("Projects will be sorted in decreasing price ordern.");
					break;
				case 5:
					currentFilter.setDefault();
					System.out.println("All filters have been cleared and reset to default.");
					break;
				case 6:
					setting = false;
					break;
				default:
					System.out.println("Invalid option.");
			}
		}
		returnToDashboard(user);
	}
	
	public void manageOfficers(HDBManager user) {
		List<Registration> registrations = offRegCtrl.getPendingRegistrations(user);
		for (Registration reg : registrations) {
			System.out.println("Project: "+reg.getProjectName());
			System.out.println("Request from "+reg.getOfficerName());
			while(true) {
				System.out.print("\nAccept Officer Registration? (Y/N): ");
				String regAccept = sc.next();
				if (regAccept.trim().toUpperCase() == "Y") {
					offRegCtrl.updateRegistrationStatus(reg, true);
					System.out.println("Officer registration accepted.");
					break;
				}
				else if (regAccept.trim().toUpperCase() == "N") {
					offRegCtrl.updateRegistrationStatus(reg, false);
					System.out.println("Officer registration rejected.");
					break;
				} else { System.out.println("Invalid input."); }
			}
		}
		List<Project> projects = mgrProjCtrl.getProjects(user);
		System.out.println("Current number of Officers by Project");
		for(Project proj : projects) {
			System.out.println(proj.getName()+": "+proj.getNumOfOfficers()+"/10 officers.");
		}
		returnToDashboard(user);
	}
	
	public void manageApplications(HDBManager user) {
		List<Application> applications = currAppCtrl.getPendingApplications(user);
		for(Application app : applications) {
			System.out.println("Project: "+app.getProjectName());
			System.out.println("Request from User #"+app.getApplicantUserID());
			while(true) {
				System.out.print("\nAccept Flat Application? (Y/N): ");
				String appAccept = sc.next();
				if (appAccept.trim().toUpperCase() == "Y") {
					currAppCtrl.updateApplicationStatus(app, true);
					System.out.println("Flat application accepted.");
					break;
				}
				else if (appAccept.trim().toUpperCase() == "N") {
					currAppCtrl.updateApplicationStatus(app, false);
					System.out.println("Flat application rejected.");
					break;
				} else { System.out.println("Invalid input."); }
			}
		}
		returnToDashboard(user);
	}
	
	public void manageWithdrawals(HDBManager user) {
		List<Application> withdrawals = currAppCtrl.getPendingWithdrawals(user);
		for(Application wd : withdrawals) {
			System.out.println("Project: "+wd.getProjectName());
			System.out.println("Request from User #"+wd.getApplicantUserID());
			while(true) {
				System.out.print("\nAccept Flat Withdrawal? (Y/N): ");
				String wdAccept = sc.next();
				if (wdAccept.trim().toUpperCase() == "Y") {
					currAppCtrl.withdrawAppStatus(wd, true);
					System.out.println("Withdrawal accepted.");
					break;
				}
				else if (wdAccept.trim().toUpperCase() == "N") {
					currAppCtrl.withdrawAppStatus(wd, false);
					System.out.println("Withdrawal rejected.");
					break;
				} else { System.out.println("Invalid input."); }
			}
		}
		returnToDashboard(user);
	}
	
	private void returnToDashboard(HDBOfficer user) {
		dashboard.displayUserMenu(user);
	}
	
	//INPUT VALIDATION HELPER METHODS
	private int readInt(String prompt) {
	    while (true) {
	        try {
	            System.out.print(prompt);
	            return Integer.parseInt(sc.nextLine());
	        } catch (NumberFormatException e) {
	            System.out.println("Please enter a number.");
	        }
	    }
	}

	private double readDouble(String prompt) {
	    while (true) {
	        try {
	            System.out.print(prompt);
	            return Double.parseDouble(sc.nextLine());
	        } catch (NumberFormatException e) {
	            System.out.println("Please enter a number.");
	        }
	    }
	}

	private LocalDate readDate(String prompt) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    while (true) {
	        try {
	            System.out.print(prompt);
	            String input = sc.nextLine();
	            return LocalDate.parse(input, formatter);
	        } catch (Exception e) {
	            System.out.println("Invalid date format. Use yyyy-mm-dd.");
	        }
	    }
	}
	
	private String readString(String prompt) {
	    System.out.print(prompt);
	    return sc.nextLine().trim();
	}
}
