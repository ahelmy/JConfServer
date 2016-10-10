/**
 * 
 */
package outsidethebox.java.rest.model;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import outsidethebox.java.hibernate.models.DBConnection;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;
import outsidethebox.java.hibernate.models.jsonadapter.DBConnectionAdapter;
import outsidethebox.java.hibernate.models.jsonadapter.ProjectAdapter;
import outsidethebox.java.hibernate.models.jsonadapter.PropertyAdapter;

/**
 * @author AliHelmy
 *
 */
public class ConfResponseAdapter implements JsonSerializer<ConfResponse<?>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(ConfResponse<?> response, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("code", response.getCode());
		jsonObject.addProperty("message", response.getMessage());
		if (response.getSample() != null) {
			if (response.getSample() instanceof Property) {
				jsonObject.add("property",
						new PropertyAdapter().serialize((Property) response.getSample(), Property.class, context));
			} else if (response.getSample() instanceof DBConnection) {
				jsonObject.add("dbconnection", new DBConnectionAdapter().serialize((DBConnection) response.getSample(),
						DBConnection.class, context));
			} else if (response.getSample() instanceof Project) {
				jsonObject.add("project",
						new ProjectAdapter().serialize((Project) response.getSample(), Project.class, context));
			}
		}
		return jsonObject;
	}

}
