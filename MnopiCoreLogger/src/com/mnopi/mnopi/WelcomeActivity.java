package com.mnopi.mnopi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mnopi.data.DataHandler;
import com.mnopi.data.DataHandlerRegistry;
import com.mnopi.data.PageVisitedDataHandler;
import com.mnopi.data.WebSearchDataHandler;
import com.mnopi.utils.Connectivity;

public class WelcomeActivity extends Activity{
	
	private Button btnSendImmediately;
	private Button btnPermissionConsole;
	private Button btnViewData;
	private Button action_settings;
	private ToggleButton butDataDelivery;
	private ToggleButton butDataCollector;
	private Context mContext;
	DataHandlerRegistry dataHandlers = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);  
        
        mContext = this;
        btnSendImmediately = (Button) findViewById(R.id.btnSendImmediately);
        btnPermissionConsole = (Button) findViewById(R.id.btnPermissionConsole);
        btnViewData = (Button) findViewById(R.id.btnViewData);
        action_settings = (Button) findViewById(R.id.action_settings);
        butDataCollector = (ToggleButton) findViewById(R.id.butDataCollector);
        butDataDelivery = (ToggleButton) findViewById(R.id.butDataDelivery);

        dataHandlers = new DataHandlerRegistry();
		
		WebSearchDataHandler webHandler = new WebSearchDataHandler(getApplicationContext());
		PageVisitedDataHandler pageHandler = new PageVisitedDataHandler(getApplicationContext());
		dataHandlers.bind(webHandler.getKey(), webHandler);
		dataHandlers.bind(pageHandler.getKey(), pageHandler);
        
        btnPermissionConsole.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent(WelcomeActivity.this,
						PermissionActivity.class);
				startActivity(intent);
        	}        	
        });
        
        btnViewData.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent(WelcomeActivity.this,
						ViewDataActivity.class);
				startActivity(intent);
        	}        	
        });
        
        btnSendImmediately.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		SharedPreferences prefs = getSharedPreferences("MisPreferencias",
        				Context.MODE_PRIVATE);
        		if (Connectivity.isOnline(mContext)){
        			DataHandler handler;
        			for (String handlerKey : dataHandlers.getHandlersKeys()){
        				handler = dataHandlers.lookup(handlerKey);
        				handler.sendData();
        			}
        		}
        		else{
					Toast toast = Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG);
					toast.show();
			    }
        	}
        });
        
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.action_logout:
	    	SharedPreferences settings = getSharedPreferences(
					"MisPreferencias", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("session_token", null);
			editor.commit();
			Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	

	@Override
	public void onStart(){
	    super.onStart();
	    SharedPreferences prefs = getSharedPreferences("MisPreferencias",
				Context.MODE_PRIVATE);
	    Boolean isCheckedDataCollector = prefs.getBoolean("butDataCollector", false);
	    butDataCollector.setChecked(isCheckedDataCollector);
	    Boolean isCheckedDataDelivery = prefs.getBoolean("butDataDelivery", false);
	    butDataDelivery.setChecked(isCheckedDataDelivery);
	}
	
	@Override
	public void onStop(){
	    super.onStop();
	    SharedPreferences prefs = getSharedPreferences("MisPreferencias",
				Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("butDataCollector", butDataCollector.isChecked());
		editor.putBoolean("butDataDelivery", butDataDelivery.isChecked());
		editor.commit();	
	}
	
}
