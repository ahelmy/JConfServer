/**
 * 
 */
package outsidethebox.java.backend;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import outsidethebox.java.hibernate.JPA;
import outsidethebox.java.hibernate.models.DBConnection;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;

/**
 * @author AliHelmy
 *
 */
public class JPAHandler {
	private Map<Project, List<Property>> projectProperties;
	private Set<DBConnection> dbConnections;
	private static JPAHandler INSTANCE;
	private JPA jpa;

	private JPAHandler() {
		projectProperties = new ConcurrentHashMap<>();
		dbConnections = new HashSet<>();
		jpa = new JPA();
	}

	public static JPAHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new JPAHandler();
		}
		return INSTANCE;
	}

	public void loadProperties() {
		jpa.getAllProjects().stream().forEach(proj -> {
			projectProperties.put(proj, proj.getProperties());
			System.out.println(proj.getName());
		});
	}

	public List<Project> getAllProjects() {
		return jpa.getAllProjects();
	}

	public void loadDBConnections() {
		jpa.getAllDBConnection().stream().forEach(db -> {
			dbConnections.add(db);
			System.out.println(db.getConnectionID());
		});
	}

	public void postProject(Project project) {
		final Project exists = jpa.getProject(project.getName());
		if (exists != null) {
			project.setId(exists.getId());
		}
		jpa.saveProject(project);
		projectProperties.put(project, project.getProperties());
	}

	public void postProperty(final Property property) {
		final Project project = jpa.getProject(property.getProject().getName());
		if (project == null) {
			jpa.saveProject(property.getProject());
			property.getProject().setProperties(new LinkedList<>());
		} else {
			property.setProject(project);
		}
		if (property.getDbconnection() != null) {
			final DBConnection dbConn = jpa.getDBConnection(property.getDbconnection().getConnectionID());
			if (dbConn != null) {
				property.getDbconnection().setId(dbConn.getId());
			}
			jpa.saveDBConnection(property.getDbconnection());
		}

		Property propFilter = property.getProject().getProperties().stream()
				.filter(prop -> prop.getName().equals(property.getName())).findAny().orElse(null);
		if (propFilter != null) {
			property.setId(propFilter.getId());
		} else {
			property.getProject().addProperty(property);
		}
		jpa.saveProperty(property);
		projectProperties.put(property.getProject(), property.getProject().getProperties());
	}

	public Property getProperty(String project, String property) {
		Property $prop = projectProperties.entrySet().stream().filter(entry -> entry.getKey().getName().equals(project))
				.map(Map.Entry::getValue).findAny().orElse(new LinkedList<>()).stream()
				.filter(prop -> prop.getName().equals(property)).findAny().orElse(null);
		if ($prop == null) {
			$prop = jpa.getProperty(project, property);
			if ($prop != null) {
				projectProperties.put($prop.getProject(), $prop.getProject().getProperties());
			}
		}
		return $prop;
	}

	public Project getProject(String name) {
		Project proj = new Project();
		proj.setName(name);
		Project result = null;
		if (projectProperties.containsKey(proj)) {
			result = projectProperties.keySet().stream().filter(entry -> entry.getName().equals(name)).findAny()
					.orElse(null);
		} else {
			result = jpa.getProject(name);
			if (result != null)
				projectProperties.put(result, result.getProperties());
		}
		return result;
	}

	public Property deleteProperty(String project, String property) {
		Property prop = getProperty(project, property);
		if (prop != null) {
			prop = jpa.deleteProperty(prop);
			projectProperties.get(prop.getProject()).remove(prop);
		}
		return prop;
	}

	public Project deleteProject(String project) {
		Project proj = getProject(project);
		if (proj != null) {
			proj = jpa.deleteProject(proj);
			projectProperties.remove(proj);
		}
		return proj;
	}

	public DBConnection getDBConnection(String connID) {
		DBConnection dbConn = dbConnections.stream().filter(db -> db.getConnectionID().equals(connID)).findAny()
				.orElse(null);
		if (dbConn == null) {
			dbConn = jpa.getDBConnection(connID);
		}
		return dbConn;
	}

	public void postDBConnection(DBConnection dbConn) {
		final DBConnection exists = jpa.getDBConnection(dbConn.getConnectionID());
		if (exists != null) {
			dbConn.setId(exists.getId());
		}
		jpa.saveDBConnection(dbConn);
		dbConnections.add(dbConn);
	}
}
