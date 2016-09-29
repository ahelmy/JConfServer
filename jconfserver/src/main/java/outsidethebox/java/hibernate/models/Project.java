package outsidethebox.java.hibernate.models;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import outsidethebox.java.hibernate.models.jsonadapter.DBConnectionAdapter;
import outsidethebox.java.hibernate.models.jsonadapter.ProjectAdapter;

/**
 * The persistent class for the Project database table.
 * 
 */
@Entity
@NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p")
public class Project implements Serializable, JsonSerializer<Project> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private int id;

	@Column(name = "Description")
	private String description;

	@Column(name = "Name")
	private String name;

	// bi-directional many-to-one association to Property
	@OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
	private List<Property> properties;

	public Project() {
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

	public List<Property> getProperties() {
		return this.properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public Property addProperty(Property property) {
		getProperties().add(property);
		property.setProject(this);

		return property;
	}

	public Property removeProperty(Property property) {
		getProperties().remove(property);
		property.setProject(null);

		return property;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Project && ((Project) obj).name.equals(this.name));
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public JsonElement serialize(Project src, Type typeOfSrc, JsonSerializationContext context) {
		return new ProjectAdapter().serialize(src, typeOfSrc, context);
	}

	@Override
	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(getClass(), new ProjectAdapter())
				.create();
		return gson.toJson(this);
	}

}