package com.example.androidpresence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

public class EditFacebook extends Activity {

	EditText facebookUserAddress;
	EditText facebookPassword;
	Button facebookSaveButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_facebook);
		facebookUserAddress = (EditText) findViewById(R.id.facebookAddress);
		facebookPassword = (EditText) findViewById(R.id.facebookPassword);
		facebookSaveButton = (Button) findViewById(R.id.facebookSave);
		Global.sharedPreferences = getSharedPreferences(Global.myPreferences, Context.MODE_PRIVATE);
		
		if (Global.sharedPreferences.contains(Global.facebookAddressKey)) {
			facebookUserAddress.setText(Global.sharedPreferences.getString(Global.facebookAddressKey, ""));
		}
		if (Global.sharedPreferences.contains(Global.facebookPasswordKey)) {
			facebookPassword.setText(Global.sharedPreferences.getString(Global.facebookPasswordKey, ""));
		}
		
		facebookSaveButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				String username = facebookUserAddress.getText().toString();
				String password = facebookPassword.getText().toString();
				
				if (username.length() > 0 && password.length() > 0) {
					//SAVE
					Editor editor = Global.sharedPreferences.edit();
					editor.putString(Global.facebookAddressKey, username);
					editor.putString(Global.facebookPasswordKey, password);
					editor.commit();
				}
				return false; 
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_facebook, menu);
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
