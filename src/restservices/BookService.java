package restservices;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import data.BookBorrow;
import utils.LibraryUtil;

@Path("/book")
public class BookService {

	@POST
	@Path("/borrow")
	@Produces(MediaType.APPLICATION_JSON)
	public Response borrowBook(BookBorrow borrow) {
 
		LibraryUtil util = new LibraryUtil();		
		JSONObject jsonResp = util.borrowBook(borrow);
		
		return Response.status(Response.Status.OK).entity(jsonResp.toString()).build();
		
	}
	
	@GET
	@Path("/borrows/late")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMostLateUser() {
 
		LibraryUtil libUtil = new LibraryUtil();
		JSONObject jsonResp = libUtil.getUserMostLate();
		
		return Response.status(Response.Status.OK).entity(jsonResp.toString()).build();
		
	}
	
	@GET
	@Path("/borrows/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBorrowingHistory(@PathParam("id") int bookCopyId) {
 
		LibraryUtil libUtil = new LibraryUtil();
		JSONObject jsonResp = libUtil.getBorrowsForBook(bookCopyId);
		
		return Response.status(Response.Status.OK).entity(jsonResp.toString()).build();
		
	}
}
