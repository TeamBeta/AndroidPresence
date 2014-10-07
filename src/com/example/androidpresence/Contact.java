package com.example.androidpresence;

import java.util.ArrayList;

public class Contact {

	String contactName;
	ArrayList<String> emails;
	ArrayList<String> phoneNumbers;
	
	public Contact(String contactName, ArrayList<String> emails, ArrayList<String> phonenNumbers) {
		this.contactName = contactName;
		this.emails = emails;
		this.phoneNumbers = phoneNumbers;
	}
	
	public ArrayList<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(String phoneNumber, int index) {
		this.phoneNumbers.set(index, phoneNumber);
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
