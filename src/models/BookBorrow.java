package models;

import java.util.Date;

public class BookBorrow {

	private int id;
	private int book_copy_id;
    private int user_id;
    private Date borrow_start_date;
    private Date borrow_end_date;
    private Date date_of_return;
    
    public BookBorrow() {
    	
    }
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBook_copy_id() {
		return book_copy_id;
	}
	public void setBook_copy_id(int book_copy_id) {
		this.book_copy_id = book_copy_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public Date getBorrow_start_date() {
		return borrow_start_date;
	}
	public void setBorrow_start_date(Date borrow_start_date) {
		this.borrow_start_date = borrow_start_date;
	}
	public Date getBorrow_end_date() {
		return borrow_end_date;
	}
	public void setBorrow_end_date(Date borrow_end_date) {
		this.borrow_end_date = borrow_end_date;
	}

	public Date getDate_of_return() {
		return date_of_return;
	}

	public void setDate_of_return(Date date_of_return) {
		this.date_of_return = date_of_return;
	}
	
	
    
}
