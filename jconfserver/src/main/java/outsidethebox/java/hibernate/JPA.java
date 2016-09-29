/**
 * 
 */
package outsidethebox.java.hibernate;

import java.util.List;
import java.util.Properties;

import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.PropertyProjection;
import org.hibernate.query.Query;

import outsidethebox.java.hibernate.models.DBConnection;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;

/**
 * @author AliHelmy
 *
 */
public class JPA {
	public static SessionFactory sessionFactory = null;

	public static SessionFactory createSession() {
		if (sessionFactory == null) {
			Properties properties = new Properties();
			properties.put("javax.persistence.provider", "org.hibernate.ejb.HibernatePersistence");
			properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
			properties.put("hibernate.connection.username", "");
			properties.put("hibernate.connection.password", "");
			properties.put("hibernate.connection.driver_class", "org.sqlite.JDBC");
			String db = "./db/Configuration.db";
			properties.put("hibernate.connection.url", "jdbc:sqlite:"+db+"");
			properties.put("hibernate.dialect","outsidethebox.java.hibernate.dialect.SQLiteDialect");
//			properties.put("hibernate.hbm2ddl.auto","create-drop"/*"update"*/);
			properties.put("hibernate.show_sql", "true");
			properties.put("hibernate.format_sql", "true");
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

	public void addProject(Project project) {
		Session session = createSession().openSession();
		Transaction tx = session.beginTransaction();
		session.save(project);
		tx.commit();
		session.close();
	}

	public Project getAddProject(Project project) {
		Session session = createSession().openSession();
		String query = "FROM Project Where ";
		String param = "";
		if (project.getId() != 0) {
			query += " ID = ?";
			param = project.getId() + "";
		} else if (project.getName() != null && project.getName().length() != 0) {
			query += " Name = ?";
			param = project.getName();
		} else {
			throw new RuntimeException("No definition");
		}
		Query<?> result = session.createQuery(query, Project.class).setParameter(0, param);
		if (!result.getResultList().isEmpty()) {
			project = (Project) result.getSingleResult();
		} else {
			session.getTransaction().begin();
			session.save(project);
			session.getTransaction().commit();
		}
		session.close();
		return project;
	}

	public List<Property> getPackageProperties(Project proj) {
		Session session = createSession().openSession();
		proj = (Project) session.createQuery("FROM Project WHERE id =?").setParameter(0, proj.getId())
				.getSingleResult();// .getResultList();
		session.close();
		return proj.getProperties();
	}

	public Property getProperty(String project, String property) {
		Property prop = null;
		Session session = createSession().openSession();
		Query<?> query = session
				.createQuery(
						"FROM Property prop, Project proj WHERE prop.project.id = proj.id AND proj.name = ? AND prop.name = ?")
				.setParameter(0, project).setParameter(1, property);
		if (!query.getResultList().isEmpty()) {
			prop = (Property) ((Object[]) query.getSingleResult())[0];
		}
		session.close();
		return prop;
	}

	public void addProperty(Property prop) {
		Session session = createSession().openSession();
		Transaction tx = session.beginTransaction();
		session.save(prop);
		tx.commit();
		session.close();
	}

	public DBConnection getAddDBConnection(DBConnection dbConn) {
		Session session = createSession().openSession();
		String query = "FROM DBConnection Where ";
		String param = "";
		if (dbConn != null && dbConn.getId() != 0) {
			query += " ID = ?";
			param = dbConn.getId() + "";
		} else if (dbConn != null && dbConn.getConnectionID() != null && dbConn.getConnectionID().length() != 0) {
			query += " ConnectionID = ?";
			param = dbConn.getConnectionID();
		} else {
			// throw new RuntimeException("No definition");
			return dbConn;
		}
		Query<?> result = session.createQuery(query, DBConnection.class).setParameter(0, param);
		if (!result.getResultList().isEmpty()) {
			dbConn = (DBConnection) result.getSingleResult();
		} else {
			session.getTransaction().begin();
			session.save(dbConn);
			session.getTransaction().commit();
		}
		session.close();
		return dbConn;
	}

}
