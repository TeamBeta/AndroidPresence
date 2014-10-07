package com.example.androidpresence;

import java.util.ArrayList;

public class Contact {

	String contactName;
	ArrayList<String> emails;
	
	public Contact(String contactName, ArrayList<String> emails) {
		this.contactName = contactName;
		this.emails = emails;
	}
	
	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public ArrayList<String> getEmails() {
		return emails;
	}

	public void setEmail(String email, int index) {
		this.emails.set(index, email);
	}
	
	
	
}
