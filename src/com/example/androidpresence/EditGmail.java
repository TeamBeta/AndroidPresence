package com.example.androidpresence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

public class EditGmail extends Activity {

	EditText gmailUserAddress;
	EditText gmailPassword;
	Button gmailSaveButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_gmail);
		gmailUserAddress = (EditText) findViewById(R.id.gmailAddress);
		gmailPassword = (EditText) findViewById(R.id.gmailPassword);
		gmailSaveButton = (Button) findViewById(R.id.gmailSave);
		Global.sharedPreferences = getSharedPreferences(Global.myPreferences, Context.MODE_PRIVATE);
		
		if (Global.sharedPreferences.contains(Global.gmailAddressKey)) {
			gmailUserAddress.setText(Global.sharedPreferences.getString(Global.gmailAddressKey, ""));
		}
		if (Global.sharedPreferences.contains(Global.gmailPasswordKey)) {
			gmailPassword.setText(Global.sharedPreferences.getString(Global.gmailPasswordKey, ""));
		}
		
		gmailSaveButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				String username = gmailUserAddress.getText().toString();
				String password = gmailPassword.getText().toString();
				
				if (username.length() > 0 && password.length() > 0) {
					//SAVE
					Editor editor = Global.sharedPreferences.edit();
					editor.putString(Global.gmailAddressKey, username);
					editor.putString(Global.gmailPasswordKey, password);
					editor.commit();
				}
				return false; 
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_gmail, menu);
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
