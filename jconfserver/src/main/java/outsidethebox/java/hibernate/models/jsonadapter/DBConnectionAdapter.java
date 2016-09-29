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

/**
 * @author AliHelmy
 *
 */
public class DBConnectionAdapter implements JsonSerializer<DBConnection> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(DBConnection db, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", db.getId());
		jsonObject.addProperty("connectionID", db.getConnectionID());
		jsonObject.addProperty("username", db.getUsername());
		jsonObject.addProperty("password", db.getPassword());
		jsonObject.addProperty("url", db.getUrl());
		jsonObject.addProperty("driver", db.getDriver());
		jsonObject.addProperty("description", db.getDescription());
		return jsonObject;
	}

}
