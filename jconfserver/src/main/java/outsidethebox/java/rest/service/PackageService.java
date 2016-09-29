/**
 * 
 */
package outsidethebox.java.rest.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import outsidethebox.java.backend.PropertyHandler;
import outsidethebox.java.hibernate.models.Property;
import outsidethebox.java.rest.pojo.GsonObject;

/**
 * @author AliHelmy
 *
 */
@Path("/Package")
public class PackageService {

	@PUT
	@Path("/{package}")
	public Response putPackage(@PathParam("package") String _package){
		return Response.ok().build();
	}
	
	@GET
	@Path("/{package}")
	public Response getPackageProperties(@PathParam("package") String _package) {
//		if (PropertyHandler.getInstance().containsKey(_package)) {
//			List<Property> packageProperties = PropertyHandler.getInstance().get(_package);
//			return Response.ok().entity(GsonObject.toJSON(packageProperties)).build();
//		} else {
			return Response.status(Status.NOT_FOUND).entity("KEY [" + _package + "] not found").build();
//		}
	}
}
