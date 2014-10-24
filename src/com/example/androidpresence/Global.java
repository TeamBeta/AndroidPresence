package com.example.androidpresence;

import java.util.ArrayList;

import org.jivesoftware.smack.XMPPConnection;

import android.content.SharedPreferences;
import android.os.AsyncTask;

public class Global {

	public static boolean contactsHaveBeenInit = false;
	public static boolean gmailPresenceHasBeenInit = false;
	public static boolean facebookPresenceHasBeenInit = false;
	public static boolean hipchatPresenceHasBeenInit = false;
	public static ArrayList<Contact> listOfGlobalContacts;
	public static final String myPreferences = "myPrefs";
	public static final String gmailAddressKey = "gmailAddressKey";
	public static final String gmailPasswordKey = "gmailPasswordKey";
	public static final String facebookAddressKey = "facebookAddressKey";
	public static final String facebookPasswordKey = "facebookPasswordKey";
	public static final String hipchatAddressKey = "hipchatAddressKey";
	public static final String hipchatPasswordKey = "hipchatPasswordKey";
	public static final String hipchatAPITokenKey = "hipchatAPITokenKey";
	public static final String hipchatCompanyJabberIdKey = "hipchatCompanyJabberIdKey";
	public static final String hipchatUserJabberIdKey = "hipchatUserJabberIdKey";
	public static SharedPreferences sharedPreferences;
	
	public static String lastActivity = "";
}
