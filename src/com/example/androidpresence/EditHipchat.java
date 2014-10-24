package com.example.androidpresence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditHipchat extends Activity {
	public AsyncTask<Void, Void, Void> httpTask;
	public AsyncTask<Void, Void, Void> validateTokenHttpTask;
	EditText hipchatAPIToken;
	EditText hipchatUserAddress;
	EditText hipchatPassword;
	Button hipchatValidateButton;
	Button hipchatSaveButton;
	String enteredUsername;
	String enteredToken;
	public Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_hipchat);
		
		hipchatAPIToken = (EditText) findViewById(R.id.hipchatApiToken);
		hipchatValidateButton = (Button) findViewById(R.id.hipchatValidateToken);
		hipchatUserAddress = (EditText) findViewById(R.id.hipchatAddress);
		hipchatPassword = (EditText) findViewById(R.id.hipchatPassword);
		hipchatSaveButton = (Button) findViewById(R.id.hipchatSave);
		Global.sharedPreferences = getSharedPreferences(Global.myPreferences, Context.MODE_PRIVATE);
		hipchatAPIToken.setText("df95b2d32d0baa1b289011626e7949");  //TESTING
		if (Global.sharedPreferences.contains(Global.hipchatAPITokenKey)) {
			hipchatAPIToken.setText(Global.sharedPreferences.getString(Global.hipchatAPITokenKey, ""));
		}
		if (Global.sharedPreferences.contains(Global.hipchatAddressKey)) {
			hipchatUserAddress.setText(Global.sharedPreferences.getString(Global.hipchatAddressKey, ""));
		}
		if (Global.sharedPreferences.contains(Global.hipchatPasswordKey)) {
			hipchatPassword.setText(Global.sharedPreferences.getString(Global.hipchatPasswordKey, ""));
		}
		
		hipchatSaveButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d("Hipchat", "clicked save");
				enteredUsername = hipchatUserAddress.getText().toString();
				enteredToken = hipchatAPIToken.getText().toString();
				String password = hipchatPassword.getText().toString();
				httpTask = new HipchatAPIAction().execute(); //GET COMPANY JABBER ID AND USER JABBER ID => SAVE THESE
				if (enteredUsername.length() > 0 && password.length() > 0 && enteredToken.length() > 0) {
					//SAVE
					Editor editor = Global.sharedPreferences.edit();
					editor.putString(Global.hipchatAddressKey, enteredUsername); //FIX
					editor.putString(Global.hipchatPasswordKey, password);
					editor.putString(Global.hipchatAPITokenKey, enteredToken);
					editor.commit();
				}
				return false;  
			}
		});
	}
	
	private class HipchatAPIAction extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... params) {  
			Log.d("Hipchat", "Started HTTPTask");
			if (enteredUsername != null && enteredUsername.length() > 0) {
				Log.d("Hipchat", "working..");
				StringBuilder builder1 = new StringBuilder();
		    	HttpClient client1 = new DefaultHttpClient();
		    	HttpGet httpGet1 = new HttpGet("https://api.hipchat.com/v1/rooms/list?auth_token="+enteredToken);
		    	try{
		    		HttpResponse response = client1.execute(httpGet1);
		    		StatusLine statusLine = response.getStatusLine();
		    		int statusCode = statusLine.getStatusCode();
		    		Log.d("Hipchat", "statusCode= "+statusCode);
		    		if(statusCode == 200){
		    			HttpEntity entity = response.getEntity();
		    			InputStream content = entity.getContent();
		    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		    			String line;
		    			while((line = reader.readLine()) != null){
		    				builder1.append(line);
		    			}
		    		} else {
		    			Log.e("Hipchat","Failed to get JSON");
		    		}
		    	}catch(ClientProtocolException e){
		    		Log.d("Hipchat", "ClientProtocolExc");
		    		e.printStackTrace();
		    	} catch (IOException e){
		    		Log.d("Hipchat", "IOExcept");
		    		e.printStackTrace();
		    	}
		    	Log.d("Hipchat", "readJSON");
		    	String readJSON = builder1.toString();
		    	try{
		    		Log.d("Hipchat", "trying JSONOBJECT");
		        	JSONObject jsonObject1 = new JSONObject(readJSON);
		        	JSONArray jsonArray = jsonObject1.getJSONArray("rooms");
		        	JSONObject jsonObj = jsonArray.getJSONObject(0);
		        	Log.d("Hipchat", "POLO --> "+jsonObj.getString("xmpp_jid"));
		        	String hipchatCompanyJabberId = jsonObj.getString("xmpp_jid").split("_")[0];
		        	if (hipchatCompanyJabberId != null && hipchatCompanyJabberId.length() > 0) {
        				Log.d("Hipchat", "CompanyId: "+hipchatCompanyJabberId);
		        		Editor editor = Global.sharedPreferences.edit();
						editor.putString(Global.hipchatCompanyJabberIdKey, hipchatCompanyJabberId); //FIX
						editor.commit();
		        	}
		        } catch(Exception e){Log.d("Exception",""+e);}
		    	
		    	//FIND USER'S JABBER ID
		    	StringBuilder builder2 = new StringBuilder();
		    	HttpClient client2 = new DefaultHttpClient();
		    	HttpGet httpGet2 = new HttpGet("https://api.hipchat.com/v1/users/list?format=json&auth_token="+enteredToken);
		    	try{
		    		HttpResponse response = client2.execute(httpGet2);
		    		StatusLine statusLine = response.getStatusLine();
		    		int statusCode = statusLine.getStatusCode();
		    		Log.d("Hipchat", "Username, trying");
		    		if(statusCode == 200){
		    			Log.d("Hipchat", "Username, 200 ok");
		    			HttpEntity entity = response.getEntity();
		    			InputStream content = entity.getContent();
		    			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		    			String line;
		    			while((line = reader.readLine()) != null){
		    				builder2.append(line);
		    			}
		    		} else {
		    			Log.e("Hipchat","Username, Failed to get JSON");
		    		}
		    	}catch(ClientProtocolException e){
		    		Log.d("Hipchat", "Username, ClientProtocolExc");
		    		e.printStackTrace();
		    	} catch (IOException e){
		    		Log.d("Hipchat", "Username, IOExcept");
		    		e.printStackTrace();
		    	}
		    	Log.d("Hipchat", "Username, readJSON");
		    	String readJSONUsers = builder2.toString();
		    	try{
		    		Log.d("Hipchat", "Username, trying JSONOBJECT");
		        	JSONObject jsonObject = new JSONObject(readJSONUsers);
		        	JSONArray jsonArray = jsonObject.getJSONArray("users");
		        	Log.d("Hipchat", "Username, "+jsonObject);
		        	for (int i = 0; i < jsonArray.length(); i++) {
		        		JSONObject jsonObj = jsonArray.getJSONObject(i);
		        		String email = jsonObj.getString("email");
		        		Log.d("Hipchat", "Username, email= "+email);
		        		if (email.equals(enteredUsername)) {
		        			String userHipchatJabberId = jsonObj.getString("user_id");
		        			if (userHipchatJabberId != null && userHipchatJabberId.length() > 0) {
		        				Log.d("Hipchat", "Username, UserId: "+userHipchatJabberId);
		        				Editor editor = Global.sharedPreferences.edit();
								editor.putString(Global.hipchatUserJabberIdKey, userHipchatJabberId);
								editor.commit();
		        			}
		        			break;
		        		}
					}
		        } catch(Exception e){Log.d("Exception",""+e);}
		    	
	    	}
	    	this.cancel(true); //This terminates the async thread. Might have to be placed elsewhere..?
			return null;
		}
	} 
	
	public void validateHipchatToken(View view) {
		Log.d("Hipchat","PUSHED BUTTON");
		enteredToken = hipchatAPIToken.getText().toString();
		validateTokenHttpTask = new HipchatValidateTokenAction().execute();
	}
	
	private class HipchatValidateTokenAction extends AsyncTask<Void,Void,Void> {
		@Override
		protected Void doInBackground(Void... params) {   
			Log.d("Hipchat", "Started HTTPTask");
				Log.d("Hipchat", "working..");
				StringBuilder builder = new StringBuilder();
		    	HttpClient client = new DefaultHttpClient();
		    	HttpGet httpGet = new HttpGet("https://api.hipchat.com/v1/rooms/list?auth_token="+enteredToken+"&auth_test=true");
		    	try{
		    		HttpResponse response = client.execute(httpGet);
		    		StatusLine statusLine = response.getStatusLine();
		    		int statusCode = statusLine.getStatusCode();
		    		Log.d("Hipchat", "statusCode= "+statusCode);
		    		if(statusCode == 202){
		    			Log.d("Hipchat", "SUCCESS");
		    			runOnUiThread(new Runnable() {
		    				public void run() {
		    				    Toast.makeText(getApplicationContext(), "HipChat API Token is valid!", Toast.LENGTH_LONG).show();
		    				    }
		    			});
		    		} else if (statusCode == 401) {
		    			runOnUiThread(new Runnable() {
		    				public void run() {
		    				    Toast.makeText(getApplicationContext(), "HipChat API Token is not valid. Contact your company's HipChat admin, or go to hipchat.com/admin/api", Toast.LENGTH_LONG).show();
		    				    }
		    			});
		    		}
		    	}catch(ClientProtocolException e){
		    		Log.d("Hipchat", "ClientProtocolExc");
		    		e.printStackTrace();
		    	} catch (IOException e){
		    		Log.d("Hipchat", "IOExcept");
		    		e.printStackTrace();
		    	}	    	
	    	this.cancel(true); //This terminates the async thread. Might have to be placed elsewhere..?
			return null;
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_hipchat, menu);
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
	
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        try {
            httpTask.cancel(true);
            Log.d("onDestroy", "Hipchat API HTTP task canceled");
		} catch (Exception e) { 
			Log.d("onDestroy", "Hipchat API HTTP task already canceled");
		}
        try {
        	validateTokenHttpTask.cancel(true);
            Log.d("onDestroy", "Hipchat API Validate HTTP task canceled");
		} catch (Exception e) { 
			Log.d("onDestroy", "Hipchat API Validate HTTP task already canceled");
		}
    } 
}
