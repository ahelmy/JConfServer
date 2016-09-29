/**
 * 
 */
package outsidethebox.java.hibernate.models.jsonadapter;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import outsidethebox.java.hibernate.models.DBConnection;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;

/**
 * @author AliHelmy
 *
 */
public class PropertyAdapter implements JsonSerializer<Property> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(Property prop, Type typeOfSrc, JsonSerializationContext context) {
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", prop.getId());
		jsonObject.addProperty("name", prop.getName());
		jsonObject.addProperty("value", prop.getValue());
		jsonObject.addProperty("updateEveryMin", prop.getUpdateEveryMin());// updateEveryMin
		jsonObject.addProperty("description", prop.getDescription());
		if (prop.getDbconnection() != null) {
			//jsonObject.addProperty("dbconnection", prop.getDbconnection().toString());
			jsonObject.add("dbconnection", new DBConnectionAdapter().serialize(prop.getDbconnection(), DBConnection.class, context));
		}
		if (prop.getProject() != null) {
			jsonObject.add("project",new ProjectAdapter().serialize(prop.getProject(), Project.class, context));
		}
		return jsonObject;
	}

}
