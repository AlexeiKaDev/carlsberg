package com.ak.carlsberg;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Info extends AppCompatActivity {
		
		
	/** Called when the activity is first created. */
	@SuppressWarnings("ResourceType")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.xml.info); 
	   
	    int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
	    TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
	    Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
	    if(actionBarTitleView != null){
	        actionBarTitleView.setTypeface(fonts);
	        actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
	    }
	    
	    // TODO Auto-generated method stub
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		
		
		
				
		MenuItem mi = menu.add(0, 1, 0, getText(R.string.action_settings));
		MenuItem mispravka = menu.add(0, 2, 0, getText(R.string.about));
					
		
		mi.setIntent(new Intent(this, PrefActivity.class));				
		mispravka.setIntent(new Intent(this, Info.class));	
		
		
	//	getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);		
		    
	}
	
}
