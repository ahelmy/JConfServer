/**
 * 
 */
package outsidethebox.java.rest.model;

import com.google.gson.GsonBuilder;

/**
 * @author AliHelmy
 *
 */
public class ConfResponse<T> {

	private String code;
	private String message;
	private T sample;

	public ConfResponse() {
	}

	public ConfResponse(String message) {
		this.code = "-1000";
		this.message = message;
	}
	
	public ConfResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public ConfResponse(String code, String message, T sample) {
		this.code = code;
		this.message = message;
		this.sample = sample;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getSample() {
		return sample;
	}

	public void setSample(T sample) {
		this.sample = sample;
	}

	@Override
	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}
}
