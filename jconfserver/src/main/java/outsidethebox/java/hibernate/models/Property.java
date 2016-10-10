package outsidethebox.java.hibernate.models;

import java.io.Serializable;
import java.lang.reflect.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import outsidethebox.java.hibernate.models.jsonadapter.ProjectAdapter;
import outsidethebox.java.hibernate.models.jsonadapter.PropertyAdapter;

/**
 * The persistent class for the Property database table.
 * 
 */
@Entity
@NamedQuery(name = "Property.findAll", query = "SELECT p FROM Property p")
public class Property extends Model implements Serializable, JsonSerializer<Property> {
	private static final long serialVersionUID = 1L;

	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "Description")
	private String description;

	@Column(name = "Name")
	private String name;

	@Column(name = "UpdateEveryMin")
	private Integer updateEveryMin;

	@Column(name = "Value")
	private String value;

	// bi-directional many-to-one association to DBConnection
	@ManyToOne
	@JoinColumn(name = "ConID")
	private DBConnection dbconnection;

	// bi-directional many-to-one association to Project
	@ManyToOne
	@JoinColumn(name = "ProjID")
	private Project project;

	public Property() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUpdateEveryMin() {
		return this.updateEveryMin;
	}

	public void setUpdateEveryMin(Integer updateEveryMin) {
		this.updateEveryMin = updateEveryMin;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DBConnection getDbconnection() {
		return this.dbconnection;
	}

	public void setDbconnection(DBConnection dbconnection) {
		this.dbconnection = dbconnection;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public boolean validPOST() {
		return name != null && name.length() > 0 && value != null && value.length() > 0 && project != null
				&& project.getName() != null && project.getName().length() > 0;
	}

	@Override
	public JsonElement serialize(Property src, Type typeOfSrc, JsonSerializationContext context) {
		return new PropertyAdapter().serialize(src, typeOfSrc, context);
	}

	public JsonObject toJSON() {
		return new GsonBuilder().setPrettyPrinting().registerTypeAdapter(getClass(), new PropertyAdapter()).create()
				.toJsonTree(this).getAsJsonObject();
	}

	
	@Override
	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(getClass(), new PropertyAdapter())
				.create();
		return gson.toJson(this);
	}

}