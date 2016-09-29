/**
 * 
 */
package outsidethebox.java.rest.pojo;


import java.io.IOException;
import java.io.StringReader;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import outsidethebox.java.hibernate.models.Property;
import outsidethebox.java.hibernate.models.jsonadapter.PropertyAdapter;

/**
 * @author AliHelmy
 *
 */
public class GsonObject{
	@Override
	public String toString() {
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(this);
		} catch (Exception ex) {
			return super.toString();
		}
	}

	@SuppressWarnings( "unchecked")
	public <T extends GsonObject> T from(String json) {
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new StringReader(json));
		reader.setLenient(true);
		return (T) gson.fromJson(reader, this.getClass());
	}

	public static String toJSON(Object obj) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(obj);
	}

}
