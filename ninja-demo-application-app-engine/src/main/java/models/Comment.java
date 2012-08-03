package models;

import javax.persistence.Id;

public class Comment {
	
    @Id Long id; // Can be Long, long, or String
    public String text;
    public String email;
    
	public String getText() {
		return text;
	}
	
	public String getEmail() {
		return email;
	}

}
