/**
 * 
 */
package outsidethebox.java.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import outsidethebox.java.hibernate.JPA;
import outsidethebox.java.hibernate.models.DBConnection;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;

/**
 * @author AliHelmy
 *
 */
public class PropertyHandler {
	private Map<Project, List<Property>> projectProperties;
	private Map<String, Property> properties;
	private static PropertyHandler INSTANCE;
	private JPA jpa;

	private PropertyHandler() {
		projectProperties = new ConcurrentHashMap<>();
		properties = new ConcurrentHashMap<>();
		jpa = new JPA();
	}

	public static PropertyHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PropertyHandler();
		}
		return INSTANCE;
	}

	public Property put(Property property) throws IOException {
		synchronized (property) {
			Project p = jpa.getAddProject(property.getProject());
			DBConnection dbConn = jpa.getAddDBConnection(property.getDbconnection());
			property.setProject(p);
			property.setDbconnection(dbConn);
			jpa.addProperty(property);
			projectProperties.put(p, p.getProperties());
			projectProperties.get(p).add(property);
			properties.put(p.getName() + "." + property.getName(), property);
			return property;
		}
	}

	public Property get(String project, String property) throws IOException {
		if (properties.containsKey(project + "." + property)) {
			Property prop = properties.get(project + "." + property);
			if (prop != null && (prop.getUpdateEveryMin() == null || prop.getUpdateEveryMin() == 0)) {
				prop = jpa.getProperty(project, property);
				return prop;
			}
		} else {
			Property prop = jpa.getProperty(project, property);
			if (prop != null) {
				projectProperties.put(prop.getProject(), prop.getProject().getProperties());
				projectProperties.get(prop.getProject()).add(prop);
				properties.put(project + "." + property, prop);
				return prop;
			}
		}
		return null;
	}

}
