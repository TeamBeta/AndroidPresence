package com.example.androidpresence;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity  {
	private EditText username = null;
	private EditText password = null;
	private Button login;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main); 
	    
	    username = (EditText)findViewById(R.id.editText1);
	    password = (EditText)findViewById(R.id.editText2);
	    login = (Button)findViewById(R.id.button1);
	}
	
	public void login(View view){
		if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
			Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(getApplicationContext(), Contacts.class);
			i.putExtra("username", username.getText().toString());
			startActivity(i);
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

   
 
    @Override
    public void onStart() {
        super.onStart();
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
 
 
	

}
