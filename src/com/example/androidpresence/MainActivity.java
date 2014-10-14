package com.example.androidpresence;

//import android.support.v7.app.ActionBarActivity;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.androidpresence.adapter.ExpandableListAdapter;
import com.example.androidpresence.adapter.TabsPagerAdapter;
import com.example.androidpresence.sip.EditSIP;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.sip.SipProfile;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Profile;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener  {
	private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Accounts", "Contacts" };
	
    private ArrayList<String> parentItems = new ArrayList<String>();
	private ArrayList<Object> childItems = new ArrayList<Object>();

	private Button login;
	
    //CONTACT INFORMATION
	public static ArrayList<Contact> listOfContacts;
	
	//USER INFORMATION
	public static String userName;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main); 
	    
	    listOfContacts = new ArrayList<Contact>();
        getContacts(); 
		getOwnProfileInfo();
	    	    
	    viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
	    
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
         
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
         
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        
	    //username = (EditText)findViewById(R.id.editText1);
	    //password = (EditText)findViewById(R.id.editText2);
	    //login = (Button)findViewById(R.id.button1);
        
        
	}
	
	
	
	
	
	private void getContacts() { 
		String phoneNumber;
		String email;
		 
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
		
		String contact_status = ContactsContract.Contacts.CONTACT_STATUS;
		String contact_presence = ContactsContract.Contacts.CONTACT_PRESENCE;
		Uri presenceUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, Uri.encode("kristoffer.oseth@gmail.com"));
		
		
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null); 
						
		Log.d("JADA", "So FAR"); 
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) { //for every contact
				ArrayList<String> emails = new ArrayList<String>();
				ArrayList<String> phoneNumbers = new ArrayList<String>();
				String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
				String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));				
								
				Log.d("contactName", name);
				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER ))); //get the boolean value of HasPhone and parse to int (0 == false, 1 == true)
				if (hasPhoneNumber > 0) { //if user has phone number
					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
						Log.d("contact Phone",phoneNumber);
						phoneNumbers.add(phoneNumber);
					}
					phoneCursor.close();
					// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,    null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
					while (emailCursor.moveToNext()) {
						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
						Log.d("contact Email",email);
						emails.add(email);
					} 
					emailCursor.close();
				}
				Contact contact = new Contact(name,emails, phoneNumbers);
				listOfContacts.add(contact);
				Log.d("PRESENCE", ""+cursor.getInt(cursor.getColumnIndex(contact_presence)));
				//Log.d("STATUS",cursor.getString(cursor.getColumnIndex(contact_status)));
			}
			cursor.close();
		}	
	}
	
	public void getOwnProfileInfo() {
		String USER_ID;
		
		String[] mProjection = new String[]  //projection are the columns to retrieve => makes the queries more efficient
			    {
			        Profile._ID,
			        Profile.DISPLAY_NAME_PRIMARY,
			        Profile.LOOKUP_KEY,
			        Profile.PHOTO_THUMBNAIL_URI,
			        Profile.CONTACT_PRESENCE,
			        //CommonDataKinds.SipAddress.CONTACT_PRESENCE
			    };
		
			//THE COLUMNS IN THE PROFILE TABLE
			String _ID = ContactsContract.Profile._ID;
			String DISPLAY_NAME = ContactsContract.Profile.DISPLAY_NAME_PRIMARY;
			String LOOKUP_KEY = ContactsContract.Profile.LOOKUP_KEY;
			String PHOTO_THUMBNAIL_URI = ContactsContract.Profile.PHOTO_THUMBNAIL_URI;
			String CONTACT_PRESENCE = ContactsContract.Profile.CONTACT_PRESENCE;
			
			//CONTENT URI FOR EMAIL
			Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
			String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
			String EMAIL_DATA = ContactsContract.CommonDataKinds.Email.DATA;
			//CONTENT URI FOR SIP
			String SIP_ADDRESS = ContactsContract.CommonDataKinds.SipAddress.CONTACT_PRESENCE;
			String SIP_CONTACT_ID = ContactsContract.CommonDataKinds.SipAddress.CONTACT_ID;
			String DATA = ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS;
			
			// Retrieves the profile from the Contacts Provider
			Cursor mProfileCursor =
			        getContentResolver().query(
			                Profile.CONTENT_URI,
			                null,         
			                null,
			                null,
			                null);
			if (mProfileCursor.moveToFirst()) // data?
				Log.d("SO FAR","Sofar");	
			    Log.d("User Name",mProfileCursor.getString(mProfileCursor.getColumnIndex(DISPLAY_NAME)));	
				userName = mProfileCursor.getString(mProfileCursor.getColumnIndex(DISPLAY_NAME));
			    USER_ID = mProfileCursor.getString(mProfileCursor.getColumnIndex(_ID)); 
				//String sipa = mProfileCursor.getString(mProfileCursor.getColumnIndex(DATA));
				Log.d("SIPPP",DATA);
			    Cursor emailCursor = getContentResolver().query(EMAIL_CONTENT_URI, null, null, null, null); //translated to SQL => where EmailContact_ID = 'USER_ID';
				while (emailCursor.moveToNext()) { //for every email that uses has
					String email = emailCursor.getString(emailCursor.getColumnIndex(EMAIL_DATA)); 
					Log.d("USER EMAIL",email);
				}
				emailCursor.close(); 
				/*Cursor sipCursor = getContentResolver().query(Profile.CONTENT_URI, null, null, null, null); //translated to SQL => where EmailContact_ID = 'USER_ID';
				while (sipCursor.moveToNext()) { //for every email that uses has
					String sip = sipCursor.getString(sipCursor.getColumnIndex(DATA)); 
					Log.d("USER SIP",sip);
				}
				sipCursor.close(); */ 
			mProfileCursor.close();  
			
			Uri uri = ContactsContract.Data.CONTENT_URI;
			String[] projection = new String[] {
			    ContactsContract.Data._ID,
			    ContactsContract.Data.DISPLAY_NAME,
			    ContactsContract.CommonDataKinds.SipAddress.SIP_ADDRESS,
			    ContactsContract.CommonDataKinds.SipAddress.TYPE,
			};
			String selection = 
			    ContactsContract.Data.MIMETYPE+" ='" 
			    +ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE+"'";
			String[] selectionArgs = null;
			String sortOrder = ContactsContract.Contacts.DISPLAY_NAME+ " COLLATE LOCALIZED ASC";
			Cursor cursor2 = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
			if (cursor2.moveToFirst()) {
				String s = cursor2.getString(2);
				Log.d("MONGO",s);
			}
			
			
	} 
	 
	
	
	
	
	@Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) { 
    }
 
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }
 
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
    
    
	
    /*
	public void login(View view){
		if(username.getText().toString().equals("") && password.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(getApplicationContext(), Contacts.class); //setup change view/activity
			i.putExtra("username", username.getText().toString()); //send values into new intent
			startActivity(i); //execute change view
		}	
		else{
			Toast.makeText(getApplicationContext(), "Wrong Password/Username",Toast.LENGTH_SHORT).show();
		}
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

   */
    
    public void editSIP(View view) {
    	Intent intent = new Intent(getApplicationContext(), EditSIP.class);
    	startActivity(intent);
    }
 
    @Override
    public void onStart() {
        super.onStart();
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

	

}
