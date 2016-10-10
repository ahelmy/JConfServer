/**
 * 
 */
package outsidethebox.java.hibernate.models.jsonadapter;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import outsidethebox.java.hibernate.models.Project;

/**
 * @author AliHelmy
 *
 */
public class ProjectAdapter implements JsonSerializer<Project> {

	private boolean logProperties;

	public ProjectAdapter() {
		this.logProperties = false;
	}

	public ProjectAdapter(boolean logProperties) {
		this.logProperties = logProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(Project proj, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", proj.getId());
		jsonObject.addProperty("name", proj.getName());
		jsonObject.addProperty("description", proj.getDescription());
		if (logProperties) {
			JsonArray array = new JsonArray();
			proj.getProperties().stream().forEach(prop -> {
				array.add(new PropertyAdapter().serialize(prop, PropertyAdapter.class, context));
			});
			jsonObject.add("properties", array);
		}
		return jsonObject;
	}

}
