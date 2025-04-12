import java.util.*;

public class HDBManagerProjectService {

    private List<BTOProject> allProjects;

    public HDBManagerProjectService() {
        this.allProjects = new ArrayList<>();
    }

    // Adds a new project if manager is eligible
    public void createProject(HDBManager manager, BTOProject project) {
        if (!hasDateConflict(manager, project)) {
            project.setManager(manager);
            allProjects.add(project);
            System.out.println("Project created: " + project.getName());
        } else {
            System.out.println("Manager already handles a project in this period.");
        }
    }

    public void editProject(HDBManager manager, BTOProject updatedProject) {
        for (int i = 0; i < allProjects.size(); i++) {
            BTOProject existing = allProjects.get(i);
            if (existing.getName().equals(updatedProject.getName()) &&
                existing.getManager().equals(manager)) {
                allProjects.set(i, updatedProject);
                System.out.println("Project updated.");
                return;
            }
        }
        System.out.println("Edit failed. Project not found or permission denied.");
    }

    public void deleteProject(HDBManager manager, String projectName) {
        allProjects.removeIf(p -> p.getName().equals(projectName) && p.getManager().equals(manager));
        System.out.println("Project deleted if it existed and was yours.");
    }

    public void toggleVisibility(HDBManager manager, String projectName) {
        for (BTOProject p : allProjects) {
            if (p.getName().equals(projectName) && p.getManager().equals(manager)) {
                p.setVisible(!p.isVisible());
                System.out.println("Project visibility toggled.");
                return;
            }
        }
        System.out.println("Toggle failed. Project not found or permission denied.");
    }

    public void viewAllProjects() {
        for (BTOProject p : allProjects) {
            System.out.println(p);
        }
    }

    public void viewManagerProjects(HDBManager manager) {
        for (BTOProject p : allProjects) {
            if (p.getManager().equals(manager)) {
                System.out.println(p);
            }
        }
    }

    public void processOfficerRegistration(String projectName, HDBOfficer officer, boolean approve) {
        for (BTOProject project : allProjects) {
            if (project.getName().equals(projectName)) {
                if (approve && project.getOfficerSlots() > 0) {
                    project.addOfficer(officer);
                    officer.setAssignedProject(project);
                    System.out.println("Officer approved and assigned.");
                } else if (!approve) {
                    System.out.println("Officer registration rejected.");
                } else {
                    System.out.println("No officer slots available.");
                }
                return;
            }
        }
    }

    public void processApplicantApplication(Application app, boolean approve) {
        if (approve && app.getProject().hasAvailableUnits(app.getFlatType())) {
            app.setStatus(ApplicationStatus.SUCCESSFUL);
            app.getProject().decreaseFlatCount(app.getFlatType());
            System.out.println("Application approved.");
        } else {
            app.setStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application rejected or no flats available.");
        }
    }

    public void processWithdrawal(Application app, boolean approve) {
        if (approve) {
            app.setStatus(ApplicationStatus.WITHDRAWN);
            System.out.println("Withdrawal approved.");
        } else {
            System.out.println("Withdrawal denied.");
        }
    }

 
}
