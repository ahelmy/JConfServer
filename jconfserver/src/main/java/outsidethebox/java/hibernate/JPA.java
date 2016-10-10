/**
 * 
 */
package outsidethebox.java.hibernate;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import outsidethebox.java.hibernate.models.DBConnection;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;
import outsidethebox.java.utils.ResourceUtils;

/**
 * @author AliHelmy
 *
 */
public class JPA {
	public static SessionFactory sessionFactory = null;

	public static SessionFactory createSession() {
		if (sessionFactory == null) {
			String db = "db/Configuration.db";
			Properties properties = new Properties();
			properties.put("javax.persistence.provider", "org.hibernate.ejb.HibernatePersistence");
			properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
			/* SQLite */
			properties.put("hibernate.connection.username", "");
			properties.put("hibernate.connection.password", "");
			properties.put("hibernate.connection.driver_class", "org.sqlite.JDBC");
			properties.put("hibernate.connection.url",
					"jdbc:sqlite:" + ResourceUtils.getFile(db).getAbsolutePath().replaceAll("%20", " ") + "");
			properties.put("hibernate.dialect", "outsidethebox.java.hibernate.dialect.SQLiteDialect");
			/* SQLite */

			/* MSSQL */
			// properties.put("hibernate.connection.username", "sa");
			// properties.put("hibernate.connection.password", "P@ssw0rd");
			// properties.put("hibernate.connection.driver_class",
			// "net.sourceforge.jtds.jdbc.Driver");
			// properties.put("hibernate.connection.url",
			// "jdbc:jtds:sqlserver://localhost/Configuration");
			/* MSSQL */
			// properties.put("hibernate.hbm2ddl.auto","create-drop"/*"update"*/);
			properties.put("hibernate.show_sql", "true");
			properties.put("hibernate.format_sql", "true");
			properties.put("use_sql_comments", "true");
			//
			Configuration cfg = new Configuration();
			cfg.addProperties(properties);
			cfg.addAnnotatedClass(Project.class);
			cfg.addAnnotatedClass(Property.class);
			cfg.addAnnotatedClass(DBConnection.class);
			// cfg.addPackage("outsidethebox.java.hibernate.models");
			//
			sessionFactory = cfg.buildSessionFactory();// .buildEntityManagerFactory();
		}
		return sessionFactory;
	}

	public void saveProject(Project project, boolean mergeID) {
		Session session = createSession().openSession();
		Transaction tx = session.beginTransaction();
		if (project.getId() != 0 && mergeID) {
			try {
				project = (Project) session.merge(project);
			} catch (Exception ex) {

			}
		}
		session.save(project);
		tx.commit();
		session.close();
	}

	public void saveProject(Project project) {
		saveProject(project, true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Project> getAllProjects() {
		List<Project> projects = new LinkedList<>();
		Session session = createSession().openSession();
		Query result = session.getNamedQuery("Project.findAll");// ,Project.class);
		projects = result.getResultList();
		session.close();
		return projects;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<DBConnection> getAllDBConnection() {
		Set<DBConnection> dbConns = new HashSet<>();
		Session session = createSession().openSession();
		Query result = session.getNamedQuery("DBConnection.findAll");// ,Project.class);
		dbConns.addAll(result.getResultList());
		session.close();
		return dbConns;
	}

	public Project getProject(String name) {
		Session session = null;
		Project project = null;
		try {
			session = createSession().openSession();

			String query = "FROM Project Where Name = ?";
			Query<?> result = session.createQuery(query, Project.class).setParameter(0, name);

			if (!result.getResultList().isEmpty()) {
				project = (Project) result.getSingleResult();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return project;
	}

	public List<Property> getPackageProperties(Project proj) {
		Session session = createSession().openSession();
		proj = (Project) session.createQuery("FROM Project WHERE id =?").setParameter(0, proj.getId())
				.getSingleResult();// .getResultList();
		session.close();
		return proj.getProperties();
	}

	public Property getProperty(Property property) {
		return getProperty(property.getProject().getName(), property.getName());
	}

	public Property getProperty(String project, String property) {
		Property prop = null;
		Session session = createSession().openSession();
		Query<?> query = session
				.createQuery(
						"FROM Property prop, Project proj WHERE prop.project.id = proj.id AND proj.name = ? AND prop.name = ?")
				.setParameter(0, project).setParameter(1, property);
		if (!query.getResultList().isEmpty()) {
			prop = (Property) ((Object[]) query.getResultList().get(0))[0];
		}
		session.close();
		return prop;
	}

	public void saveProperty(Property prop) {
		saveProperty(prop, true);
	}

	public void saveProperty(Property prop, boolean mergeID) {
		Session session = createSession().openSession();
		Transaction tx = session.beginTransaction();
		if (prop.getId() != 0 && mergeID) {
			try {
				prop = (Property) session.merge(prop);
			} catch (Exception ex) {

			}
		}
		session.save(prop);
		tx.commit();
		session.close();
	}

	public void saveDBConnection(DBConnection dbConn) {
		saveDBConnection(dbConn, true);
	}

	public void saveDBConnection(DBConnection dbConn, boolean mergeID) {
		Session session = createSession().openSession();
		Transaction tx = session.beginTransaction();
		if (dbConn.getId() != 0 && mergeID) {
			dbConn = (DBConnection) session.merge(dbConn);
		}
		session.save(dbConn);
		tx.commit();
		session.close();
	}

	public Property deleteProperty(Property prop) {
		Session session = createSession().openSession();
		Transaction tx = session.beginTransaction();
		session.delete(prop);
		tx.commit();
		session.close();
		return prop;

	}

	public Project deleteProject(Project proj) {
		Session session = createSession().openSession();
		Transaction tx = session.beginTransaction();
		session.delete(proj);
		tx.commit();
		session.close();
		return proj;
	}

	public DBConnection getDBConnection(String connID) {
		Session session = null;
		DBConnection dbConn = null;
		try {
			session = createSession().openSession();

			String query = "FROM DBConnection Where ConnectionID = ?";
			Query<?> result = session.createQuery(query, DBConnection.class).setParameter(0, connID);

			if (!result.getResultList().isEmpty()) {
				dbConn = (DBConnection) result.getSingleResult();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return dbConn;
	}

}
