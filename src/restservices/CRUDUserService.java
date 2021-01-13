package restservices;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import data.User;
import utils.UserUtil;

@Path("/user")
public class CRUDUserService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers() {
 
		UserUtil util = new UserUtil();
		List<User> allUsers = util.getAllUsers();
		
		JSONObject jsonResp = new JSONObject();
		
		for(User u : allUsers) {
			JSONObject jsonUser = new JSONObject();
			jsonUser.put("id", u.getId());
			jsonUser.put("firstname", u.getFirstname());
			jsonUser.put("lastname", u.getLastname());
			jsonUser.put("date_of_birth", u.getDate_of_birth());
			jsonUser.put("verified", u.isVerified());
			jsonResp.append("Users", jsonUser);
		}
		
		return Response.status(Response.Status.OK).entity(jsonResp.toString()).build();
		
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserById(@PathParam("id") int id) {
 
		UserUtil util = new UserUtil();
		User u = util.getUserWithId(id);
		
		return Response.status(Response.Status.OK).entity(u).build();
		
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(User user) {
 
		UserUtil util = new UserUtil();
		JSONObject jsonObject = util.createUser(user);

		return Response.status(Response.Status.OK).entity(jsonObject.toString()).build();
		
	}
	
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("id") int id, User user) {
 
		UserUtil util = new UserUtil();
		JSONObject jsonObject = util.updateUserWithId(id, user);
		
		return Response.status(Response.Status.OK).entity(jsonObject.toString()).build();
		
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deteUser(@PathParam("id") int id) {
 
		UserUtil util = new UserUtil();
		JSONObject jsonObject = util.deleteUserWithId(id);
		
		return Response.status(Response.Status.OK).entity(jsonObject.toString()).build();
		
	}
}
