package repository;

import model.Project;
import model.Flat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing Project entities
 */
public class ProjectRepository extends AbstractRepository<Project, String> {
    
    /**
     * Constructor for ProjectRepository
     * 
     * @param filePath Path to the file where projects are stored
     */
    public ProjectRepository(String filePath) {
        super(filePath);
    }
    
    @Override
    public Project findById(String name) {
        return entities.stream()
                      .filter(project -> project.getName().equals(name))
                      .findFirst()
                      .orElse(null);
    }
    
    @Override
    protected String getEntityId(Project project) {
        return project.getName();
    }
    
    /**
     * Finds projects by neighborhood
     * 
     * @param neighborhood The neighborhood to search for
     * @return A list of projects in the specified neighborhood
     */
    public List<Project> findByNeighborhood(String neighborhood) {
        return entities.stream()
                      .filter(project -> project.getNeighborhood().equalsIgnoreCase(neighborhood))
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds projects that are currently open for application
     * 
     * @return A list of open projects
     */
    public List<Project> findOpenProjects() {
        Date now = new Date();
        return entities.stream()
                      .filter(project -> project.isVisible() &&
                                        now.after(project.getApplicationOpenDate()) &&
                                        now.before(project.getApplicationCloseDate()))
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds projects that have a specific flat type
     * 
     * @param flatType The flat type to search for
     * @return A list of projects with the specified flat type
     */
    public List<Project> findByFlatType(String flatType) {
        return entities.stream()
                      .filter(project -> 
                          project.getFlats().stream()
                                .anyMatch(flat -> flat.getFlatType().equals(Flat.FlatType.fromDisplayName(flatType)))
                      )
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds projects that have available units
     * 
     * @return A list of projects with available units
     */
    public List<Project> findWithAvailableUnits() {
        return entities.stream()
                      .filter(project -> 
                          project.getFlats().stream()
                                .anyMatch(Flat::isAvailable)
                      )
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds visible projects
     * 
     * @return A list of visible projects
     */
    public List<Project> findVisibleProjects() {
        return entities.stream()
                      .filter(Project::isVisible)
                      .collect(Collectors.toList());
    }
    public List<Project> findOpeningProjects() {
        return entities.stream()
                      .filter(Project::isVisible)
                      .filter(project -> project.getApplicationOpenDate().before(new Date()) &&
                                        project.getApplicationCloseDate().after(new Date()))
                      .collect(Collectors.toList());
    }
} 