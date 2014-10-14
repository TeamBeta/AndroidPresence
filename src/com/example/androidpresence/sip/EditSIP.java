package com.example.androidpresence.sip;

import com.example.androidpresence.MainActivity;
import com.example.androidpresence.R;
import com.example.androidpresence.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditSIP extends Activity{

	public String sipAddress = null;
    public SipManager manager = null;
    public SipProfile me = null;
    
    private EditText usernameView;
    private EditText domainView;
    private EditText passwordView;
    private Button save;
    private TextView status;
    
    private String username;
    private String domain;
    private String password;
    
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String USERNAME = "usernameKey";
    public static final String DOMAIN = "domainKey";
    public static final String PASSWORD = "passwordKey";
    
    SharedPreferences sharedpreferences;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.edit_sip); 
	    
	    usernameView = (EditText) findViewById(R.id.sipUsername);
	    domainView = (EditText) findViewById(R.id.sipDomain);
	    passwordView = (EditText) findViewById(R.id.sipPassword);
	    save = (Button) findViewById(R.id.sipSave);
	    status = (TextView) findViewById(R.id.sipLoading);
	    
	    sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
	    
	    if (sharedpreferences.contains(USERNAME)){
	    	usernameView.setText(sharedpreferences.getString(USERNAME, ""));
	    }
	    if (sharedpreferences.contains(DOMAIN)){
	    	domainView.setText(sharedpreferences.getString(DOMAIN, ""));
	    }
	    if (sharedpreferences.contains(PASSWORD)){
	    	passwordView.setText(sharedpreferences.getString(PASSWORD, ""));
	    }
	    
	    save.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				updateSipAccount();
				return false;
			}
	    });
	}
	
	
	public void updateSipAccount() {    
	    username = usernameView.getText().toString();
	    domain = domainView.getText().toString();
	    password = passwordView.getText().toString();
	    
	    if(username.length() == 0 || domain.length() == 0 || password.length() == 0){
	    	status.setText("Please fill in all fields");
	    }
	    else {
	    	status.setText("Trying to register...");
	    	// Register sip account
		    Editor editor = sharedpreferences.edit();
			editor.putString(USERNAME, usernameView.getText().toString());
		    editor.putString(DOMAIN, domainView.getText().toString());
		    editor.putString(PASSWORD, passwordView.getText().toString());
		    editor.commit(); 
		    
		    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
	    	startActivity(intent);
	    }
	    
	    
	}

	
	
}
