package data;

import java.util.Date;

public class User {

	private int id;
	private String firstname;
    private String lastname;
    private Date date_of_birth;
    private boolean verified;
    
    public User(String firstname, String lastname, Date dateOfBirth) {
    	this.firstname = firstname;
    	this.lastname = lastname;
    	this.date_of_birth = dateOfBirth;
    }
    
    public User() {
    	
    }
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Date getDate_of_birth() {
		return date_of_birth;
	}

	public void setDate_of_birth(Date date_of_birth) {
		this.date_of_birth = date_of_birth;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	
    
}
