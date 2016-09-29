/**
 * 
 */
package outsidethebox.java.web.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;

/**
 * @author AliHelmy
 *
 */
@ManagedBean(name="homeBean")
public class HomeBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<String> packages = Arrays.asList("java.util","com.test","kol.ok");
	
	public HomeBean(){
		
	}

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}
	

	
}
