package com.example.androidpresence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.androidpresence.adapter.ExpandableListAdapter;



public class Contacts extends Activity {
	
	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private ArrayList<String> listContactNames;
	private HashMap<String, ArrayList<String>> listContactInformation;
	//private ArrayList<Contact> listOfContacts;
	private Context context;
	private static boolean contactsHaveBeenInitialized = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.contacts);
	    context = this;
	    // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableList);
 
        //if (!contactsHaveBeenInitialized) {
            //listOfContacts = MainActivity.listOfContacts;
        //    contactsHaveBeenInitialized = true;
        //}

        Log.d("OnCreate", "hasFinishedInit");
        // preparing list data

        Log.d("OnCreate", "Established a new google thread");
        
        
        
        
        
         prepareListData();
	     listAdapter = new ExpandableListAdapter(context, listContactNames, listContactInformation);
	     // setting list adapter
	     expListView.setAdapter(listAdapter);  
	     Log.d("OnCreate", "hasRunAsynThread");
	     
	     /*Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	        	@Override
	        	  public void run() {
	        	     //prepareListData();
	        	      
	        	     Log.d("UPDATE", "Should update");
	        	     listAdapter.update(listContactNames, listContactInformation);
	        	     listAdapter.notifyDataSetChanged();
	        	  }
	        }, 5000);  */
	}
	  
	public void refresh(View view) {
		onRestart();
	} 
	
	protected void onRestart() {
		super.onRestart();
		Intent i = new Intent(this, Contacts.class);
		startActivity(i);
		finish();
	}
	private void prepareListData() {
		Log.d("OnCreate", "InsidePrepareListData");
		listContactNames = new ArrayList<String>();
		listContactInformation = new HashMap<String, ArrayList<String>>();
		
		//ArrayList<Contact> contacts = MainActivity.listOfContacts;
		for (int i = 0; i < Global.listOfGlobalContacts.size(); i++) {
			Contact contact = Global.listOfGlobalContacts.get(i);
			listContactNames.add(contact.contactName);
			
			String gmail = contact.getGmail() +" - "+ contact.getGmailPresence();
			String facebook = contact.getFacebookUserName() +" - "+ contact.getFacebookPresence();
			Log.d("DEBUG",""+ contact.getGmailPresence());
			ArrayList<String> phoneNumbers = contact.getPhoneNumbers();
			
			ArrayList<String> finalList = new ArrayList<String>();
			
			finalList.add(gmail);
			finalList.add(facebook);
			//finalList.addAll(phoneNumbers);
			
			String s = ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS;
			Log.d("sip", s);
			
			listContactInformation.put(listContactNames.get(i), finalList);
		}
	}
	
	
	
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }

	
}
/*
public class Contacts extends Fragment {
	
	
		// Expandable list
		public static ExpandableListAdapter listAdapter;
	    public static ExpandableListView expListView;
	    public static ArrayList<String> listContactNames;
	    public static HashMap<String, ArrayList<String>> listContactInformation;
	    public static View rootview;
	    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        rootview = inflater.inflate(R.layout.contacts, container, false);
         
        // get the listview
        expListView = (ExpandableListView) rootview.findViewById(R.id.expandableList);
 
        
        // preparing list data
        prepareListData(); 
 
        listAdapter = new ExpandableListAdapter(this.getActivity(), listContactNames, listContactInformation);
        
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
        
        return rootview;
    }
	
	public static ExpandableListView getExpView () {
		return expListView;
	}
	
	public static View getRootView() {
		return rootview;
	}
	
	public static void refreshList() {
		prepareListData();
		listAdapter.notifyDataSetChanged();
	}
	
	public static void prepareListData() {
		
		listContactNames = new ArrayList<String>();
		listContactInformation = new HashMap<String, ArrayList<String>>();
		
		ArrayList<Contact> contacts = MainActivity.listOfContacts;
		for (int i = 0; i < contacts.size(); i++) {
			Contact contact = contacts.get(i);
			listContactNames.add(contact.contactName);
			
			String gmail = contact.getGmail() +", Status: "+ contact.getGmailPresence();
			Log.d("DEBUG",""+ contact.getGmailPresence());
			ArrayList<String> phoneNumbers = contact.getPhoneNumbers();
			
			ArrayList<String> finalList = new ArrayList<String>();
			
			finalList.add(gmail);
			finalList.addAll(phoneNumbers);
			
			String s = ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS;
			Log.d("sip", s);
			
			listContactInformation.put(listContactNames.get(i), finalList);
		}
		
		
		
        
 /*
        // Adding child data
		listContactNames.add("Top 250");
		listContactNames.add("Now Showing");
		listContactNames.add("Coming Soon..");
 
        // Adding child data 
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");
 
        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");
 
        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");
 
        listContactInformation.put(listContactNames.get(0), top250); // Header, Child data
        listContactInformation.put(listContactNames.get(1), nowShowing);
        listContactInformation.put(listContactNames.get(2), comingSoon);*/
    
	
	/*
	private TextView username = null;
	private TextView outputText = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.contacts); //decide what layout to use
	    
	    // Get username from previous activity
	    headlineLabel = (TextView) findViewById(R.id.textView1);
	    Intent i = getIntent();
        String _username = i.getStringExtra("username");
        headlineLabel.setText("Logged in as "+_username);
        

        //ContentValues personValues = new ContentValues();
        //personValues.put(Contacts.People.NAME, "John F. Doe");
        fetchContacts();

	}
	
	private void fetchContacts() {
		String phoneNumber = null;
		String email = null;
		 
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
		Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;
		StringBuffer output = new StringBuffer();
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null); 
		
		Log.d("JADA", "So FAR");
		// Loop for every contact in the phone
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
				String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));				
				Log.d("contactName", name);
				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER ))); //get the boolean value of HasPhone and parse to int (0 == false, 1 == true)
				if (hasPhoneNumber > 0) { //if user has phone number
					output.append("\n First Name:" + name);
					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
						output.append("\n Phone number:" + phoneNumber);
					}
					phoneCursor.close();
					// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,    null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
					while (emailCursor.moveToNext()) {
						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
						output.append("\nEmail:" + email);
					}
					emailCursor.close();
				}
				output.append("\n");
			}
			outputText.setText(output);
		}	
	}

	public void sip(View view){
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	
}
*/