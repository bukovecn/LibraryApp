package utils;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import models.User;

public class UserUtil {

	DBUtil util = new DBUtil();
	private static final String MESSAGE_OK = "OK";
	private static final String MESSAGE_NOK = "NOK";
	
	public JSONObject createUser(User user) {
		JSONObject response = new JSONObject();
		
		String sqlQuery = "INSERT INTO \"users\"(\"firstname\", \"lastname\", \"date_of_birth\", \"verified\") VALUES (?, ?, ?, ?)";
		       
		try (
                Connection conn = util.connectToDB();
                PreparedStatement statement = conn.prepareStatement(sqlQuery);) {
             
            	if(checkIfExists(user, conn) == false) {
            		statement.setString(1, user.getFirstname());
                    statement.setString(2, user.getLastname());                    
                    statement.setDate(3, new Date(user.getDate_of_birth().getTime()));  
                    statement.setBoolean(4, user.isVerified());
                    statement.addBatch();
                    statement.executeBatch(); 

                    response.put("status", MESSAGE_OK);
            	
            	}else {
            		response.put("status", MESSAGE_NOK);            		
            		response.put("description", "User " + user.getFirstname() + " " + user.getLastname() + ", " + user.getDate_of_birth() + " already exists in DB!");                               		
            	}
            	
            	return response;
         
        } catch (SQLException | ClassNotFoundException | URISyntaxException ex) {
            System.out.println("Error while inserting in DB: " + ex.toString());
            response.put("status", MESSAGE_NOK);
            response.put("description", "Error while inserting in DB");
            return response;
        }
        
        
	}
	
	private boolean checkIfExists(User user, Connection conn) {
    	boolean exists = false;
    	String check = "SELECT * FROM \"users\" where (\"firstname\" = '" + user.getFirstname() + "' and \"lastname\" = '" + user.getLastname() 
    				+ "' and \"date_of_birth\" = '" + user.getDate_of_birth() + "')";
    	try (
			PreparedStatement statement = conn.prepareStatement(check);
    		ResultSet resultSet = statement.executeQuery();) {
    		
    		if (resultSet.next()) {
    			exists = true;
    		}
    		

    	} catch (Exception e) {
            System.out.println("Error while executing select: " + e.getMessage());
        }
    	return exists;
    }
	
	
	public List<User> getAllUsers() {
		List<User> allUsers = new ArrayList<User>();
		
    	String query = "SELECT * FROM \"users\"";
    	
    	try (
    		Connection conn = util.connectToDB();
    		PreparedStatement statement = conn.prepareStatement(query);
    		ResultSet resultSet = statement.executeQuery();) {

    	    while (resultSet.next()) {
    	      User user = new User();
    	      user.setId(resultSet.getInt("id"));
    	      user.setFirstname(resultSet.getString("firstname"));
    	      user.setLastname(resultSet.getString("lastname"));
    	      user.setDate_of_birth(resultSet.getDate("date_of_birth"));
    	      user.setVerified(resultSet.getBoolean("verified"));
    	      allUsers.add(user);
    	    }


    	} catch (Exception e) {
            System.out.println("Error while executing select: " + e.getMessage());
        }
    	
    	return allUsers;
    }
	
	public User getUserWithId(int id) {
		User user = new User();
		
		String query = "SELECT * FROM \"users\" where \"id\" = " + id;
    	
    	try (
    		Connection conn = util.connectToDB();
    		PreparedStatement statement = conn.prepareStatement(query);
    		ResultSet resultSet = statement.executeQuery();) {

    	    if (resultSet.next()) {
    	      user = new User();
    	      user.setId(resultSet.getInt("id"));
    	      user.setFirstname(resultSet.getString("firstname"));
    	      user.setLastname(resultSet.getString("lastname"));
    	      user.setDate_of_birth(resultSet.getDate("date_of_birth"));
    	      user.setVerified(resultSet.getBoolean("verified"));
    	    }
    	    

    	} catch (Exception e) {
            System.out.println("Error while executing select: " + e.getMessage());
        }
    	
    	return user;
    }

	public JSONObject updateUserWithId(int id, User user) {
		JSONObject response = new JSONObject();
		
		String update = "UPDATE \"users\" SET \"firstname\" = ?, \"lastname\" = ?, \"date_of_birth\" = ? WHERE \"id\" = ?";
		
		try (
	    		Connection conn = util.connectToDB();
	    		PreparedStatement statement = conn.prepareStatement(update);) {
				
				statement.setString(1, user.getFirstname());
				statement.setString(2, user.getLastname());
				statement.setDate(3, new Date(user.getDate_of_birth().getTime()));
				statement.setInt(4, id);
				
				int updated = statement.executeUpdate();
				
				if(updated == 0) {
					response.put("status", MESSAGE_NOK);
            		response.put("description", "Update user with ID = " + id + " failed");                               		
				}else {
					response.put("status", MESSAGE_OK);            		
				}
								
				return response;
				
	    	} catch (Exception e) {
	            System.out.println("Error while executing update: " + e.getMessage());
	            response.put("status", MESSAGE_NOK);
	            response.put("description", "Error while updating user with ID = " + id);
	            return response;
	        }
				    
	}

	public JSONObject deleteUserWithId(int id) {
		JSONObject response = new JSONObject();
		
		String delete = "DELETE FROM \"users\" WHERE \"id\" = ?";
		
		try (
	    		Connection conn = util.connectToDB();
	    		PreparedStatement statement = conn.prepareStatement(delete);) {				
				statement.setInt(1, id);
				
				int deleted = statement.executeUpdate();
				
				if(deleted == 0) {
					response.put("status", MESSAGE_NOK);
            		response.put("description", "Deleting user with ID = " + id + " failed");                               		
				}else {
					response.put("status", MESSAGE_OK);            		
				}
								
				return response;
				
	    	} catch (Exception e) {
	            System.out.println("Error while deleting: " + e.getMessage());
	            response.put("status", MESSAGE_NOK);
	            response.put("description", "Error while deleting user with ID = " + id);
	            return response;
	        }
	}

	
}
