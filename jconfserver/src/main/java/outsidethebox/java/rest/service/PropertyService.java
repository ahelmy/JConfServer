/**
 * 
 */
package outsidethebox.java.rest.service;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import outsidethebox.java.backend.PropertyHandler;
import outsidethebox.java.hibernate.models.DBConnection;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;

/**
 * @author AliHelmy
 *
 */
@Path("/Property")
public class PropertyService {

	@GET
	@Path("/{project}/{property}")
	public Response getPropery(@PathParam("project") String project, @PathParam("property") String property,
			@QueryParam("details") String details) {
		Property prop;
		try {
			prop = PropertyHandler.getInstance().get(project, property);
			if (prop != null) {
				if (details != null && details.equals("true")) {
					return Response.ok().entity(prop.toString()).build();
				} else {
					return Response.ok().entity(prop.getValue()).build();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Response.status(Status.NOT_FOUND).entity("KEY [" + project + "." + property + "] not found").build();
	}

	@GET
	@Path("/sample.json")
	public Response getPropertyJSON() {
		Property prop = new Property();
		prop.setId(0);
		prop.setDescription("Property Description");
		prop.setName("Property1");
		prop.setUpdateEveryMin(0);
		prop.setValue("SELECT val FROM table");
		Project proj = new Project();
		proj.setId(0);
		proj.setName("Project1");
		proj.setDescription("Project1 Description");
		prop.setProject(proj);
		DBConnection db = new DBConnection();
		db.setId(0);
		db.setConnectionID("Unique connection ID");
		db.setDescription("DBConnection Description");
		db.setUrl("JDBC URL");
		db.setPassword("DB Password");
		db.setUsername("DB Uername");
		db.setDriver("JDBC Driver");
		prop.setDbconnection(db);
		prop.setProject(proj);
		return Response.ok().entity(prop.toString()).build();
	}

	/**
	 * Add new property
	 * 
	 * @param prop
	 * @return
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPropery(Property prop) {
		try {
			prop = PropertyHandler.getInstance().put(prop);
			return Response.status(Status.CREATED).entity(prop.toString()).build();
		} catch (IOException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}

	}

	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addOrUpdatePropery(Property prop) {
		try {
			prop = PropertyHandler.getInstance().put(prop);
			return Response.ok().entity(prop.toString()).build();
		} catch (IOException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}

	}
}
