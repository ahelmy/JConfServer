package outsidethebox.java.hibernate.models;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import outsidethebox.java.hibernate.models.jsonadapter.DBConnectionAdapter;

/**
 * The persistent class for the DBConnection database table.
 * 
 */
@Entity
@NamedQuery(name = "DBConnection.findAll", query = "SELECT d FROM DBConnection d")
public class DBConnection implements Serializable, JsonSerializer<DBConnection> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "ConnectionID")
	private String connectionID;

	@Column(name = "Description")
	private String description;

	@Column(name = "Driver")
	private String driver;

	@Column(name = "Password")
	private String password;

	@Column(name = "URL")
	private String url;

	@Column(name = "Username")
	private String username;

	// bi-directional many-to-one association to Property
	@OneToMany(mappedBy = "dbconnection")
	private List<Property> properties;

	public DBConnection() {
	}

	public DBConnection(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConnectionID() {
		return this.connectionID;
	}

	public void setConnectionID(String connectionID) {
		this.connectionID = connectionID;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Property> getProperties() {
		return this.properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public Property addProperty(Property property) {
		getProperties().add(property);
		property.setDbconnection(this);

		return property;
	}

	public Property removeProperty(Property property) {
		getProperties().remove(property);
		property.setDbconnection(null);

		return property;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof DBConnection && ((DBConnection) obj).connectionID.equals(this.connectionID));
	}

	@Override
	public int hashCode() {
		return this.connectionID.hashCode();
	}

	@Override
	public JsonElement serialize(DBConnection src, Type typeOfSrc, JsonSerializationContext context) {
		return new DBConnectionAdapter().serialize(src, typeOfSrc, context);
	}

	@Override
	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(getClass(), new DBConnectionAdapter())
				.create();
		return gson.toJson(this);
	}

	public boolean validPOST() {
		return connectionID != null && connectionID.length() > 0 && driver != null && driver.length() > 0
				&& username != null && username.length() > 0 && password != null && password.length() > 0 && url != null
				&& url.length() > 0;
	}

}