
public class HDBManagerProjectService {
	
    private List<BTOProject> projectList = new ArrayList<>();

    public void addProject(BTOProject project) {
        projectList.add(project);
    }

    public void updateProject(BTOProject project) {
        // Locate and update project
    }

    public void deleteProject(String projectId) {
        projectList.removeIf(p -> p.getId().equals(projectId));
    }
}