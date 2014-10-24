package com.example.androidpresence;

//import android.support.v7.app.ActionBarActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.androidpresence.sip.EditSIP;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Profile;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	// SIP Variables
	public String sipAddress = null;
    public SipManager manager = null;
    public SipProfile me = null;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String USERNAME = "usernameKey";
    public static final String DOMAIN = "domainKey";
    public static final String PASSWORD = "passwordKey";
    SharedPreferences sharedpreferences;
    private String SIPusername;
    private String SIPdomain;
    private String SIPpassword; 
	private AsyncTask<Void, Void, Void> googleTask;
	private AsyncTask<Void, Void, Void> facebookTask;
	private AsyncTask<Void, Void, Void> hipchatTask;
	public AsyncTask<Void, Void, Void> getFacebookUserNamesHttpTask;
	public AsyncTask<Void, Void, Void> getHipchatUserNamesHttpTask;
	private XMPPConnection googleConnection;
	private XMPPConnection facebookConnection;
	private XMPPConnection hipchatConnection;
	private static TextView gmailAddressTextView;
	private static TextView facebookAddressTextView;
	private static TextView hipchatAddressTextView;
    // SIP Variables END
	
	//USER INFORMATION
	public static String userName;
	public static Context context;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("CYCLE","CREATE");
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    context = this; 
	    
	    gmailAddressTextView = (TextView) findViewById(R.id.gmailPresence);
	    facebookAddressTextView = (TextView) findViewById(R.id.facebookPresence);
	    hipchatAddressTextView = (TextView) findViewById(R.id.hipchatPresence);
	    
	    //initiateSIP();
	    if (!Global.contactsHaveBeenInit) {
	        Global.listOfGlobalContacts = new ArrayList<Contact>();
	        getContacts(); 
			getOwnProfileInfo();  
			Global.contactsHaveBeenInit = true;
	    }
	    Global.sharedPreferences = getSharedPreferences(Global.myPreferences, Context.MODE_PRIVATE);
	    if (Global.sharedPreferences != null && Global.sharedPreferences.getString(Global.gmailAddressKey, "").length() > 0 && Global.sharedPreferences.getString(Global.gmailPasswordKey, "").length() > 0) {
	    	gmailAddressTextView.setText(Global.sharedPreferences.getString(Global.gmailAddressKey, ""));
	    	googleTask = new GoogleConnectionAction().execute(); //EXECUTE GOOGLE PRESENCE BACKGROUND THREAD 
	    }
	    if (Global.sharedPreferences != null && Global.sharedPreferences.getString(Global.facebookAddressKey, "").length() > 0 && Global.sharedPreferences.getString(Global.facebookPasswordKey, "").length() > 0) {
	    	facebookAddressTextView.setText(Global.sharedPreferences.getString(Global.facebookAddressKey, ""));
	    	facebookTask = new FacebookConnectionAction().execute(); //EXECUTE GOOGLE PRESENCE BACKGROUND THREAD 
	    }
	    if (Global.sharedPreferences != null && Global.sharedPreferences.getString(Global.hipchatAddressKey, "").length() > 0 && Global.sharedPreferences.getString(Global.hipchatPasswordKey, "").length() > 0 && Global.sharedPreferences.getString(Global.hipchatAPITokenKey, "").length() > 0) {
	    	hipchatAddressTextView.setText(Global.sharedPreferences.getString(Global.hipchatAddressKey, ""));
		    hipchatTask = new HipchatConnectionAction().execute(); //EXECUTE GOOGLE PRESENCE BACKGROUND THREAD 
	    }
	}
	
	private class GetFacebookUserNamesHTTPAction extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... params) {  
	    	for (int i = 0; i < Global.listOfGlobalContacts.size(); i++) {
	    		if (Global.listOfGlobalContacts.get(i).getFacebookUserName() == null) continue;  //if user doesn't have facebook address, skip
	    		StringBuilder builder = new StringBuilder();
		    	HttpClient client = new DefaultHttpClient();
	    		Log.d("AAAAH",Global.listOfGlobalContacts.get(i).getFacebookUserName());
	    		HttpGet httpGet = new HttpGet("http://graph.facebook.com/"+Global.listOfGlobalContacts.get(i).getFacebookUserName());
		    	try{
		    		HttpResponse response = client.execute(httpGet);
		    		StatusLine statusLine = response.getStatusLine();
		    		int statusCode = statusLine.getStatusCode();
		    		if(statusCode == 200){
		    			HttpEntity entity = response.getEntity();
		    			InputStream content = entity.getContent();
		    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		    			String line;
		    			while((line = reader.readLine()) != null){
		    				builder.append(line);
		    			}
		    		} else {
		    			Log.e(MainActivity.class.toString(),"Failedet JSON object");
		    		}
		    	}catch(ClientProtocolException e){
		    		e.printStackTrace();
		    	} catch (IOException e){
		    		e.printStackTrace();
		    	}
		    	String readJSON = builder.toString();
		    	try{
		        	JSONObject jsonObject = new JSONObject(readJSON);
		        	Log.d("TROROOR", jsonObject.getString("id"));
		        	Global.listOfGlobalContacts.get(i).setFacebookUserId(jsonObject.getString("id"));
		        } catch(Exception e){Log.d("Exception",""+e);}
			}
	    	this.cancel(true); //This terminates the async thread. Might have to be placed elsewhere..?
			return null;
		}
	} 
	
	private class GetHipchatUsernamesHTTPAction extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... params) {  
			StringBuilder builder = new StringBuilder();
	    	HttpClient client = new DefaultHttpClient();
    		HttpGet httpGet = new HttpGet("https://api.hipchat.com/v1/users/list?format=json&auth_token="+Global.sharedPreferences.getString(Global.hipchatAPITokenKey, ""));
	    	try{
	    		HttpResponse response = client.execute(httpGet);
	    		StatusLine statusLine = response.getStatusLine();
	    		int statusCode = statusLine.getStatusCode();
	    		if(statusCode == 200){
	    			HttpEntity entity = response.getEntity();
	    			InputStream content = entity.getContent();
	    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	    			String line;
	    			while((line = reader.readLine()) != null){
	    				builder.append(line);
	    			}
	    		} else {
	    			Log.e(MainActivity.class.toString(),"Failed JSON object");
	    		}
	    	}catch(ClientProtocolException e){
	    		e.printStackTrace();
	    	} catch (IOException e){
	    		e.printStackTrace();
	    	}
	    	String readJSONUsers = builder.toString();
	    	try{
	        	JSONObject jsonObject = new JSONObject(readJSONUsers);
	        	JSONArray jsonArray = jsonObject.getJSONArray("users");
	        	for (int y = 0; y < jsonArray.length(); y++) {
	        		JSONObject jsonObj = jsonArray.getJSONObject(y);
	        		String email = jsonObj.getString("email");
	        		for (int i = 0; i < Global.listOfGlobalContacts.size(); i++) {
	    	    		String hipchatUsernameOfContact = Global.listOfGlobalContacts.get(i).getHipchatUserName();
	    	    		if (hipchatUsernameOfContact == null) continue;  //if user doesn't have hipchat address, skip
	    	    		if (email.equals(hipchatUsernameOfContact)) {
		        			String contactHipchatJabberId = jsonObj.getString("user_id");
		        			if (contactHipchatJabberId != null && contactHipchatJabberId.length() > 0) {
		        				Log.d("Hipchat", "Contact, UserId: "+email+" = "+contactHipchatJabberId);
		        				Global.listOfGlobalContacts.get(i).setHipchatUserId(contactHipchatJabberId);
		        			}
		        			break;
		        		}
	    			}
				}
	        } catch(Exception e){Log.d("Exception",""+e);}
	    	this.cancel(true); //This terminates the async thread. Might have to be placed elsewhere..?
			return null;
		}
	}
	
	
	
	public void initiateSIP() {
		Log.d("sip", "initiate sip");
		if(manager == null) {
			manager = SipManager.newInstance(this);
	    }
		if (manager == null) {
            return;  
        }

        if (me != null) {
        	if (manager == null) {
                return;
            }
            try {
                if (me != null) {
                    manager.close(me.getUriString());
                }
            } catch (Exception ee) {
                Log.d("WalkieTalkieActivity/onDestroy", "Failed to close local profile.", ee);
            } 
        }
        
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        
        if (sharedpreferences.contains(USERNAME)){
	    	SIPusername = sharedpreferences.getString(USERNAME, "");
	    }
	    if (sharedpreferences.contains(DOMAIN)){
	    	SIPdomain = sharedpreferences.getString(DOMAIN, "");
	    }
	    if (sharedpreferences.contains(PASSWORD)){
	    	SIPpassword = sharedpreferences.getString(PASSWORD, "");
	    }
	    
	    try {
            SipProfile.Builder builder = new SipProfile.Builder(SIPusername, SIPdomain);
            builder.setPassword(SIPpassword);
            //builder.setProtocol("UDP");
            me = builder.build();

            Intent i = new Intent();
            i.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
            manager.open(me, pi, null);

            Log.d("sip", SIPusername);
            Log.d("sip", SIPdomain); 
            Log.d("sip", SIPpassword); 

            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.
 
            manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
                    public void onRegistering(String localProfileUri) {
                        Log.d("sip","Registering with SIP Server...");
                        
                    }

                    public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    	Log.d("sip","Registration Success");
                    }

                    public void onRegistrationFailed(String localProfileUri, int errorCode,
                            String errorMessage) {
                    	Log.e(""+errorCode,errorMessage);
                    	Log.e("yo",localProfileUri);
                    	Log.d("sip","Registering Failed");
                    }
                });
        } catch (ParseException pe) {
        	Log.d("sip","Connection error");
        } catch (SipException se) {
        	Log.d("sip","Connection error"); 
        }
		
	}





	private void getContacts() {
		String email;
		
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;		
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		
		Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;
				
		ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null); 
						
		Log.d("JADA", "So FAR"); 
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) { //for every contact
				String gmail = null;
				int gmailCounter = 0;
				String facebookUserName = null;
				String hipchatUserName = null;
				ArrayList<String> emails = new ArrayList<String>();
				ArrayList<String> phoneNumbers = new ArrayList<String>();
				String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
				String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));				
								
				Log.d("contactName", name);
				// Query and loop for every email of the contact
				Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,    null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
				while (emailCursor.moveToNext()) {
					email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
					Log.d("contact Email",email);
					emails.add(email);
					if ((email.contains("@gmail.com") || email.contains("@public.talk.google.com")) && gmailCounter == 0) { //should the contact have multiple gmail addresses
						gmail = email;										 //set the first one to be the contact's actual gmail address
						gmailCounter++;
					} 
					Log.d("email", email);
				} 
				emailCursor.close();
				String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
			 	String[] imWhereParams = new String[]{contact_id, 
			 	    ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE}; 
			 	Cursor imCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, 
			 			null, imWhere, imWhereParams, null); 
			 	while (imCursor.moveToNext()) { 
			 		String imName = imCursor.getString(
			 				imCursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
			 		String imType;
				 	imType = imCursor.getString(
				 			imCursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
				 	String serviceName = imCursor.getString(
				 			imCursor.getColumnIndex(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL));
				 	Log.d("IMCURSOR", "Name: "+imName+", Type: "+imType+", Service: "+serviceName); 
				 	if (serviceName != null && serviceName.toLowerCase().equals("facebook")) {
				 		facebookUserName = imName;
				 	}
				 	else if (serviceName != null && serviceName.toLowerCase().equals("hipchat")) {
				 		Log.d("Hipchat", "Has hipchat account: "+imName);
				 		hipchatUserName = imName;
				 	}
			 	} 
			 	imCursor.close(); 
				Contact contact = new Contact(name,emails, phoneNumbers, gmail, facebookUserName, hipchatUserName);
				Global.listOfGlobalContacts.add(contact);
			}
			cursor.close();
		}	
		getFacebookUserNamesHttpTask = new GetFacebookUserNamesHTTPAction().execute(); //run async thead that retrieves the contact's respective facebook ids
		getHipchatUserNamesHttpTask = new GetHipchatUsernamesHTTPAction().execute();
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
			}
			
			
	} 
	 
	
	private class GoogleConnectionAction extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			SmackAndroid.init(context);
			Log.d("OnCreate", "InsideAsyncThread");
			 ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222,"gmail.com");
		     googleConnection = new XMPPConnection(config); 
		        try   
		        {
		        	Log.d("OnCreate", "ConnectionAttempt"); 
		            googleConnection.connect();		 										//Establish connection
		            Log.d("SUCCSEESSSSS", "JADA");  
		            try {
			            googleConnection.login(Global.sharedPreferences.getString(Global.gmailAddressKey, ""), Global.sharedPreferences.getString(Global.gmailPasswordKey, ""));		//Login
			            Log.d("Login", "success");	
			            //ATTEMPT TO GET ROSTER OF USERS THAT YOU CHAT WITH AND THEIR STATUSES
			            Roster roster = googleConnection.getRoster();
			             
			            roster.addRosterListener(new RosterListener() { 
							
							@Override
							public void presenceChanged(Presence arg0) {
								String status = arg0.getType().name();
								String[] pregmailAddress = arg0.getFrom().split("/");
								String gmailAddress = pregmailAddress[0];
								//Presence pre = roster.getPresence(user);
								Log.d("CATO",gmailAddress+": "+status);
								
								//gå gjennom alle android contacts og sjekk om en gmail adresse stemmer
								//if so, oppdater denne kontaktens presence
								for (int i = 0; i < Global.listOfGlobalContacts.size(); i++) {
									if (gmailAddress.toLowerCase().equals(Global.listOfGlobalContacts.get(i).emails.get(0))) {
										Log.d("MATCHING!!!",gmailAddress+" = "+Global.listOfGlobalContacts.get(i).emails.get(0));
										Global.listOfGlobalContacts.get(i).setGmailPresence(status);
										Log.d("TEST", Global.listOfGlobalContacts.get(i).getGmail()+": "+Global.listOfGlobalContacts.get(i).getGmailPresence()+"-- Should be: "+status);
									}
								}
								 
								Log.d("HELLO","HELLO"); 
								// UPDATE CONTACTS FRAGMENT
								
								
								//Contacts.refreshList();
								 //prepareListData(); 
								// listAdapter.notifyDataSetChanged();
								
								Log.d("yoyo","yoyo");
						        /*
						         
						        // get the listview
								ExpandableListView view = Contacts.getExpView();
						        // preparing list data
						        Contacts.prepareListData(); 
						 
						        Contacts.listAdapter = new ExpandableListAdapter(context, Contacts.listContactNames, Contacts.listContactInformation);
						        
						 
						        // setting list adapter
						        view.setAdapter(Contacts.listAdapter);
						         
						        Contacts.getRootView();*/
								
								if (Global.gmailPresenceHasBeenInit == false) {
									runOnUiThread(new Runnable() {
					    				public void run() {
					    				    Toast.makeText(getApplicationContext(), "Google presence synchronized", Toast.LENGTH_LONG).show();
					    				    }
					    			});
								}
								Global.gmailPresenceHasBeenInit = true;
							} 
							
							@Override
							public void entriesUpdated(Collection<String> arg0) {
								// TODO Auto-generated method stub
								
							} 
							
							@Override
							public void entriesDeleted(Collection<String> arg0) {
								// TODO Auto-generated method stub
								
							}
							
							@Override 
							public void entriesAdded(Collection<String> arg0) {
								// TODO Auto-generated method stub
								
							} 
						});
			            /* GET CONTACTS PRESENCE INFORMATION ONCE:
			            Collection<RosterEntry> entries = roster.getEntries();
			            for (RosterEntry entry : entries) {
			            	//example: get presence, type, mode, status
			            	Presence entryPresence = roster.getPresence(entry.getUser());
			            	  //Presence.Type userType = entryPresence.getType();
			            	  //Presence.Mode mode = entryPresence.getMode();
			            	  //String status = entryPresence.getStatus(); 
			            	  Log.d("ID",""+entry.toString());			               	  
			            	  Log.d("User",""+entry.getUser());    
			            	  Log.d("USERNAME",""+entry.getName());
			            	  //Log.d("Name", entry.getName());                          //works in FB, not gmail
			            	  //Log.d("Status", ""+entry.getStatus());					//works in FB, not gmail
			            	  //Log.d("PRESENCESTATUS",""+entryPresence.getStatus()); 	//works in FB, not gmail
			            	  Log.d("PRESENCETYPE", ""+entryPresence.getType().name());		//works in FB, not in gmail??
			            	  Log.d("HOI", ""+entryPresence); 
			            	  //Mode m = entryPresence.getMode(); 
			            	  //Log.d("MODE",""+entryPresence.toString());
			            }  */
			           
			            
			            /*Presence presence = new Presence(Presence.Type.unavailable);		//Update your presence status
			            try {
				            connection.sendPacket(presence);
 
				            Log.d("PresenceUpdate","success"); 
						} catch (Exception e) {
							Log.d("PresenceUpdate", "failed");
						}*/
					} catch (Exception e) {
						Log.d("Login", "failed");
			            //gmailAddressTextView.setText(Global.sharedPreferences.getString(Global.gmailAddressKey, "") + " - failed");
						this.cancel(true); //attempt to terminate this async thread
					}
		        }
		        catch (Exception ex)
		        {
		        	Log.d("FAILURE", "XMPPConnection");
		        	this.cancel(true); //attempt to terminate this async thread 
		            googleConnection = null;
		        }
			return null;
		}
		
	}
	
	private class FacebookConnectionAction extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... params) {
			SmackAndroid.init(context);
			Log.d("OnCreate", "InsideAsyncThread");
			 ConnectionConfiguration config = new ConnectionConfiguration("chat.facebook.com", 5222);
		     facebookConnection = new XMPPConnection(config); 
		        try   
		        {
		        	Log.d("OnCreate", "ConnectionAttempt"); 
		            facebookConnection.connect();		 										//Establish connection
		            Log.d("SUCCSEESSSSS-FACEBOOK", "JADA"); 
		            try {
			            facebookConnection.login(Global.sharedPreferences.getString(Global.facebookAddressKey, ""), Global.sharedPreferences.getString(Global.facebookPasswordKey, ""));		//Login
			            Log.d("LoginFacebook", "success");			         
			            //ATTEMPT TO GET ROSTER OF USERS THAT YOU CHAT WITH AND THEIR STATUSES
			            Roster roster = facebookConnection.getRoster();
			            
			            roster.addRosterListener(new RosterListener() {
							
							@Override
							public void presenceChanged(Presence arg0) {
								String status = arg0.getType().name();
								String[] prefacebookAddress = arg0.getFrom().split("@");
								String facebookAddress = prefacebookAddress[0].substring(1); //remove the '-' sign placed in front of ID
								//Presence pre = roster.getPresence(user);
								Log.d("CATO",facebookAddress+": "+status);
								
								//gå gjennom alle android contacts og sjekk om en gmail adresse stemmer
								//if so, oppdater denne kontaktens presence
								for (int i = 0; i < Global.listOfGlobalContacts.size(); i++) {
									if (facebookAddress.equals(Global.listOfGlobalContacts.get(i).getFacebookUserId())) {
										Log.d("MATCHING!!!-face",facebookAddress+" = "+Global.listOfGlobalContacts.get(i).getFacebookUserId()+"--->"+Global.listOfGlobalContacts.get(i).getFacebookUserName());
										Global.listOfGlobalContacts.get(i).setFacebookPresence(status);
										Log.d("TEST_face", Global.listOfGlobalContacts.get(i).getFacebookUserName()+": "+Global.listOfGlobalContacts.get(i).getFacebookPresence()+"-- Should be: "+status);
									} 
								}
								
								if (Global.facebookPresenceHasBeenInit == false) {
									runOnUiThread(new Runnable() {
					    				public void run() {
					    				    Toast.makeText(getApplicationContext(), "Facebook presence synchronized", Toast.LENGTH_LONG).show();
					    				    }
					    			});
								}
								Global.facebookPresenceHasBeenInit = true;
							}  
							
							@Override
							public void entriesUpdated(Collection<String> arg0) {
								// TODO Auto-generated method stub
							} 
							
							@Override
							public void entriesDeleted(Collection<String> arg0) {
								// TODO Auto-generated method stub
							}
							
							@Override 
							public void entriesAdded(Collection<String> arg0) {
								// TODO Auto-generated method stub
							} 
						});
			            
					} catch (Exception e) {
						Log.d("LoginFacebook", "failed");
						this.cancel(true); //attempt to terminate this async thread
					}
		        }
		        catch (Exception ex)
		        {
		        	Log.d("FAILURE-FACEBOOK", "XMPPConnection");
		        	this.cancel(true); //attempt to terminate this async thread 
		            facebookConnection = null;
		        }
			return null;
		}
		
	}
	
	private class HipchatConnectionAction extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... params) {
			SmackAndroid.init(context);
			Log.d("Hipchat", "InsideAsyncThread");
			 ConnectionConfiguration config = new ConnectionConfiguration("chat.hipchat.com", 5222);
		     hipchatConnection = new XMPPConnection(config); 
		        try   
		        { 
		        	Log.d("Hipchat", "ConnectionAttempt"); 
		            hipchatConnection.connect();		 										//Establish connection
		            Log.d("Hipchat", "Success connection"); 
		            try {
		            	Log.d("Hipchat", "Try login"); 
		            	String hipchatUsername = Global.sharedPreferences.getString(Global.hipchatCompanyJabberIdKey, "")+"_"+Global.sharedPreferences.getString(Global.hipchatUserJabberIdKey, "")+"@chat.hipchat.com";
		            	Log.d("Hipchat", "Username = "+hipchatUsername); 
		            	hipchatConnection.login(hipchatUsername, Global.sharedPreferences.getString(Global.hipchatPasswordKey, ""));		//Login
			            Log.d("Hipchat", "Success login");			         
			            //ATTEMPT TO GET ROSTER OF USERS THAT YOU CHAT WITH AND THEIR STATUSES
			            Roster roster = hipchatConnection.getRoster();
			            
			            roster.addRosterListener(new RosterListener() { 
							 
							@Override
							public void presenceChanged(Presence arg0) {
								String status = arg0.getType().name();
								String from = arg0.getFrom();
								Log.d("Hipchat",from+": "+status);
								String contactId = from.split("_")[1].split("@")[0];
								
								for (int i = 0; i < Global.listOfGlobalContacts.size(); i++) {
									if (contactId.equals(Global.listOfGlobalContacts.get(i).getHipchatUserId())) {
										Log.d("Hipchat","Matching! "+contactId+" = "+Global.listOfGlobalContacts.get(i).getHipchatUserId()+"--->"+Global.listOfGlobalContacts.get(i).getHipchatUserName() + " is now: "+status);
										Global.listOfGlobalContacts.get(i).setHipchatPresence(status);
									} 
								}
								
								if (Global.hipchatPresenceHasBeenInit == false) {
									runOnUiThread(new Runnable() {
					    				public void run() {
					    				    Toast.makeText(getApplicationContext(), "HipChat presence synchronized", Toast.LENGTH_LONG).show();
					    				    }
					    			});
								}
								Global.hipchatPresenceHasBeenInit = true;
							}  
							
							@Override
							public void entriesUpdated(Collection<String> arg0) {
								// TODO Auto-generated method stub
							} 
							
							@Override
							public void entriesDeleted(Collection<String> arg0) {
								// TODO Auto-generated method stub
							}
							
							@Override 
							public void entriesAdded(Collection<String> arg0) {
								// TODO Auto-generated method stub
							} 
						});
			            
					} catch (Exception e) {
						Log.d("Hipchat", "Failed login");
						this.cancel(true); //attempt to terminate this async thread
					}
		        }
		        catch (Exception ex)
		        {
		        	Log.d("Hipchat", "Failed connection");
		        	this.cancel(true); //attempt to terminate this async thread 
		            hipchatConnection = null; 
		        }
			return null;
		}
	}
	
    
    public void editSIP(View view) {
    	Intent intent = new Intent(getApplicationContext(), EditSIP.class);
    	startActivity(intent);
    }
    
    public void editGmailAccount(View view) {
    	Intent intent = new Intent(getApplicationContext(), EditGmail.class);
    	Global.lastActivity = "editGmail";
    	startActivity(intent);
    }
    
    public void editFacebookAccount(View view) {
    	Intent intent = new Intent(getApplicationContext(), EditFacebook.class);
    	Global.lastActivity = "editFacebook";
    	startActivity(intent);
    }
    
    public void editHipchatAccount(View view) {
    	Intent intent = new Intent(getApplicationContext(), EditHipchat.class);
    	Global.lastActivity = "editHipchat";
    	startActivity(intent);
    }
    
    public void getContacts(View view) {
    	Intent intent = new Intent(getApplicationContext(), Contacts.class);
    	Global.lastActivity = "contacts";
    	startActivity(intent);
    }
 
    @Override 
    public void onStart() { 
    	Log.d("CYCLE","START");
        super.onStart();
    }
 
    @Override
    public void onResume() {   //Should the user have edited his/her gmail/facebook/hipchat connection info, terminate current connection and try new connection with potential new connection info
    	Log.d("CYCLE","RESUME");
        super.onResume();
        if (Global.lastActivity.equals("editGmail")) {
        	Global.lastActivity = "";
        	Global.gmailPresenceHasBeenInit = false;
        	try {
        		googleConnection.disconnect();
                googleTask.cancel(true);
                Log.d("onResume", "Terminated GoogleThread");
                if (Global.sharedPreferences != null && Global.sharedPreferences.getString(Global.gmailAddressKey, "").length() > 0 && Global.sharedPreferences.getString(Global.gmailPasswordKey, "").length() > 0) {
        	    	gmailAddressTextView.setText(Global.sharedPreferences.getString(Global.gmailAddressKey, ""));
        	    	googleTask = new GoogleConnectionAction().execute(); //EXECUTE GOOGLE PRESENCE BACKGROUND THREAD 
        	    }
    		} catch (Exception e) {
    			Log.d("onResume", "Could not terminate GoogleThread");  
    		}
        }
        else if (Global.lastActivity.equals("editFacebook")) {
        	Global.lastActivity = "";
        	Global.facebookPresenceHasBeenInit = false;
        	try {
        		facebookConnection.disconnect();  
                facebookTask.cancel(true);
                Log.d("onResume", "Terminated FacebookThread");
                if (Global.sharedPreferences != null && Global.sharedPreferences.getString(Global.facebookAddressKey, "").length() > 0 && Global.sharedPreferences.getString(Global.facebookPasswordKey, "").length() > 0) {
        	    	facebookAddressTextView.setText(Global.sharedPreferences.getString(Global.facebookAddressKey, ""));
        	    	facebookTask = new FacebookConnectionAction().execute(); //EXECUTE GOOGLE PRESENCE BACKGROUND THREAD 
        	    }
    		} catch (Exception e) {
    			Log.d("onResume", "Could not terminate FacebookThread");
    		}
        }
        else if (Global.lastActivity.equals("editHipchat")) {
        	Global.lastActivity = "";
        	Global.hipchatPresenceHasBeenInit = false;
        	try {
        		hipchatConnection.disconnect();  
                hipchatTask.cancel(true);
                Log.d("onResume", "Terminated HipchatThread");
                if (Global.sharedPreferences != null && Global.sharedPreferences.getString(Global.hipchatAddressKey, "").length() > 0 && Global.sharedPreferences.getString(Global.hipchatPasswordKey, "").length() > 0 && Global.sharedPreferences.getString(Global.hipchatAPITokenKey, "").length() > 0) {
        	    	hipchatAddressTextView.setText(Global.sharedPreferences.getString(Global.hipchatAddressKey, ""));
        	    	hipchatTask = new HipchatConnectionAction().execute(); //EXECUTE GOOGLE PRESENCE BACKGROUND THREAD 
        	    }
    		} catch (Exception e) {
    			Log.d("onResume", "Could not terminate HipchatkThread");
    		}
        }
        else if (Global.lastActivity.equals("contacts")) {
        	Global.lastActivity = "";
        	//do nothing	
        }
    }
    
    @Override
    public void onPause() {
    	Log.d("CYCLE","PAUSE");
        super.onPause();
    }
    
    @Override
    public void onDestroy() {
    	Log.d("CYCLE","DESTROY");
        super.onDestroy();
        try {
        	Log.d("TEST", "Clicking Disconnect");
            googleConnection.disconnect();
            googleTask.cancel(true);
            Log.d("onDestroy", "Google task canceled");
		} catch (Exception e) {
			Log.d("onDestroy", "Google task already canceled");
		}
        try {
            facebookConnection.disconnect();
            facebookTask.cancel(true);
            Log.d("onDestroy", "Facebook task canceled");
		} catch (Exception e) { 
			Log.d("onDestroy", "Facebook task already canceled");
		}
        try {
            hipchatConnection.disconnect();
            hipchatTask.cancel(true);
            Log.d("onDestroy", "Hipchat task canceled");
		} catch (Exception e) { 
			Log.d("onDestroy", "Hipchat task already canceled");
		}
        try {
            getFacebookUserNamesHttpTask.cancel(true);
            Log.d("onDestroy", "Facebook getUsernames HTTP task canceled");
		} catch (Exception e) { 
			Log.d("onDestroy", "Facebook getUsernames HTTP task already canceled");
		}
        try {
            getHipchatUserNamesHttpTask.cancel(true);
            Log.d("onDestroy", "Hipchat getUsernames HTTP task canceled");
		} catch (Exception e) { 
			Log.d("onDestroy", "Hipchat getUsernames HTTP task already canceled");
		}
    } 

}
