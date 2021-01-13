package utils;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import models.BookBorrow;
import models.User;

public class LibraryUtil {
	
	DBUtil util = new DBUtil();
	private static final String MESSAGE_OK = "OK";
	private static final String MESSAGE_NOK = "NOK";
	public static final String GET_USER_API_URL = "http://localhost:8080/Library/libraryapi/user/";
	
	public JSONObject getUserMostLate() {
		JSONObject response = new JSONObject();
		
		String query = "SELECT MAX(bb.\"date_of_return\" - bb.\"borrow_end_date\") as days_late, us.\"firstname\", us.\"lastname\" "
				+ "from \"book_borrows\" bb inner join \"users\" us on us.\"id\" = bb.\"user_id\" "
				+ "group by us.\"firstname\", us.\"lastname\"";
		
		try (
			Connection conn = util.connectToDB();
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();) {
			
			if (resultSet.next()) {
				response.put("user", resultSet.getString("firstname") + " " + resultSet.getString("lastname"));
				response.put("days_late", resultSet.getInt("days_late"));
	    	}
		
		} catch (Exception e) {
			System.out.println("Error while executing select: " + e.getMessage());
		}
		
		return response;
	}

	public JSONObject getBorrowsForBook(int book_copy_id) {
		JSONObject response = new JSONObject();
		
		String query = "SELECT b.\"name\" , bb.\"borrow_start_date\", bb.\"date_of_return\", us.\"firstname\", us.\"lastname\""
				+ "from \"book_borrows\" bb inner join \"users\" us on us.\"id\" = bb.\"user_id\""
				+ "inner join \"book_copies\" bk on bb.\"book_copy_id\" = bk.\"id\""
				+ "inner join \"books\" b on bk.\"book_id\" = b.\"id\""
				+ "order by bb.\"borrow_start_date\" desc";
		
		try (
			Connection conn = util.connectToDB();
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();) {
			
			if (resultSet.next()) {
				response.put("user", resultSet.getString("firstname") + " " + resultSet.getString("lastname"));
				response.put("book", resultSet.getString("name"));
				response.put("borrow_start_date", resultSet.getDate("borrow_start_date"));
				Date returnDate = resultSet.getDate("date_of_return");
				if(returnDate != null) {
					response.put("return_date", resultSet.getDate("date_of_return"));
				}else {
					response.put("return_date", "Not returned");
				}
				
	    	}
		
		} catch (Exception e) {
			System.out.println("Error while executing select: " + e.getMessage());
		}
		
		return response;
		
	}
	
	public JSONObject borrowBook(BookBorrow borrow) {
		JSONObject response = new JSONObject();
		
		String sqlQuery = "INSERT INTO \"book_borrows\"(\"book_copy_id\", \"user_id\", \"borrow_start_date\", \"borrow_end_date\") VALUES (?, ?, ?, ?)";
        try (
                Connection conn = util.connectToDB();
                PreparedStatement statement = conn.prepareStatement(sqlQuery);) {
                 
        	if(!checIfkUserExists(borrow.getUser_id())) {
        		response.put("status", MESSAGE_NOK);
                response.put("description", "User with ID = " + borrow.getBook_copy_id() + " not in DB!");
                return response;
        	}
        	
        	if(!checkIfBookAvailable(borrow.getBook_copy_id(), conn)) {
        		response.put("status", MESSAGE_NOK);
                response.put("description", "Book copy with ID = " + borrow.getBook_copy_id() + " not available!");
                return response;
        	}
        	
        	statement.setInt(1, borrow.getBook_copy_id());
            statement.setInt(2, borrow.getUser_id());                    
            statement.setDate(3, new Date(borrow.getBorrow_start_date().getTime()));
            statement.setDate(4, new Date(borrow.getBorrow_end_date().getTime()));
            statement.addBatch();
            statement.executeBatch(); 

            updateCopyAsBorrowed(borrow.getBook_copy_id());
            
            response.put("status", MESSAGE_OK);
        	
        	return response;
        	
        } catch (SQLException | ClassNotFoundException | URISyntaxException ex) {
            System.out.println("Error while inserting borrow to DB: " + ex.toString());
            response.put("status", MESSAGE_NOK);
            response.put("description", "Error while inserting book borrow to DB");
            return response;
        }
	}
	
	private boolean checIfkUserExists(int user_id) {
		Client client = Client.create();	

		ClientResponse res = client.resource(GET_USER_API_URL)
        		.path(String.valueOf(user_id))
        		.type(MediaType.APPLICATION_JSON)
        		.get(ClientResponse.class);
          
		String out = res.getEntity(String.class);
		JSONObject user = new JSONObject(out);
		
        if (user.getInt("id") != 0) {
        	return true;
        }else {
        	return false;
        }
	}

	private boolean checkIfBookAvailable(int id, Connection conn) {
		boolean available = false;
    	String check = "SELECT * FROM \"book_copies\" where \"id\" = " + id;
    	
    	try (
			PreparedStatement statement = conn.prepareStatement(check);
    		ResultSet resultSet = statement.executeQuery();) {
    		
    		if (resultSet.next()) {
    			if(resultSet.getBoolean("borrowed") == false) {
    				available = true;
    			}else {
    				available = false;
    			}
    			
    		}else {
    			available = false;
    			System.out.println("Book with ID: " + id + " don't exists in DB");
    		}
    		

    	} catch (Exception e) {
            System.out.println("Error while executing select: " + e.getMessage());
        }
    	
    	return available;
	}

	private void updateCopyAsBorrowed(int book_copy_id) {
		String update = "UPDATE \"book_copies\" SET \"borrowed\" = ? WHERE \"id\" = ?";
		
		try (
	    		Connection conn = util.connectToDB();
	    		PreparedStatement statement = conn.prepareStatement(update);) {
				
				statement.setBoolean(1, Boolean.TRUE);
				statement.setInt(2, book_copy_id);
						
				statement.executeUpdate();				
				
	    	} catch (Exception e) {
	            System.out.println("Error while executing update: " + e.getMessage());
	        }
		
	}

}
