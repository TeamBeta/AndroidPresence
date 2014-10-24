package com.example.androidpresence;

import java.util.ArrayList;

public class Contact {

	String contactName;
	ArrayList<String> emails;
	String gmail;
	ArrayList<String> phoneNumbers;
	String gmailPresence = "unavailable";
	String facebookPresence = "unavailable";
	String hipchatPresence = "unavailable";
	String facebookUserName;
	String facebookUserId;
	String hipchatUserName;
	String hipchatUserId;

	public Contact(String contactName, ArrayList<String> emails, ArrayList<String> phoneNumbers, String gmail, String facebookUserName, String hipchatUserName) {
		this.contactName = contactName;
		this.emails = emails;
		this.phoneNumbers = phoneNumbers;
		this.gmail = gmail;
		this.facebookUserName = facebookUserName;
		this.hipchatUserName = hipchatUserName;
	}
	
	
	public String getFacebookUserName() {
		return facebookUserName;
	}


	public void setFacebookUserName(String facebookUserName) {
		this.facebookUserName = facebookUserName;
	}


	public String getGmail() {
		return gmail;
	}


	public void setGmail(String gmail) {
		this.gmail = gmail;
	}


	public String getGmailPresence() {
		return gmailPresence;
	}

	public void setGmailPresence(String gmailPresence) {
		this.gmailPresence = gmailPresence;
	}

	public String getFacebookPresence() {
		return facebookPresence;
	}

	public void setFacebookPresence(String facebookPresence) {
		this.facebookPresence = facebookPresence;
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


	public void setFacebookUserId(String facebookUserId) {
		this.facebookUserId = facebookUserId;
		
	}
	
	public String getFacebookUserId() {
		return this.facebookUserId;
	}


	public String getHipchatUserName() {
		return hipchatUserName;
	}


	public void setHipchatUserName(String hipchatUserName) {
		this.hipchatUserName = hipchatUserName;
	}


	public String getHipchatUserId() {
		return hipchatUserId;
	}


	public void setHipchatUserId(String hipchatUserId) {
		this.hipchatUserId = hipchatUserId;
	}


	public String getHipchatPresence() {
		return hipchatPresence;
	}


	public void setHipchatPresence(String hipchatPresence) {
		this.hipchatPresence = hipchatPresence;
	}
	
	
	
	
}
