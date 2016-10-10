/**
 * 
 */
package outsidethebox.java.utils;

import java.io.File;


/**
 * @author AliHelmy
 *
 */
public class ResourceUtils {
	public static File getFile(String fileName) {
		ClassLoader classLoader = new ResourceUtils().getClass().getClassLoader();
		return new File(classLoader.getResource(fileName).getFile());
	}
}
