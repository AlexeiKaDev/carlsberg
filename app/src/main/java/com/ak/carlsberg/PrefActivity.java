package com.ak.carlsberg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class PrefActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.pref);
	    SharedPreferences sp;
	    sp = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    onSharedPreferenceChangedList(sp,"list");
	    onSharedPreferenceChangedEdit(sp,"serverurl");
	    onSharedPreferenceChangedEdit(sp,"useplan");
	    onSharedPreferenceChangedEdit(sp,"kegplan");
	    onSharedPreferenceChangedEdit(sp,"packingplan");
	    onSharedPreferenceChangedEdit(sp,"monday");
	    onSharedPreferenceChangedEdit(sp,"tuesday");
	    onSharedPreferenceChangedEdit(sp,"wednesday");
	    onSharedPreferenceChangedEdit(sp,"thuesday");
	    onSharedPreferenceChangedEdit(sp,"friday");
	    
	    int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
	    TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
	    Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
	    if(actionBarTitleView != null){
	        actionBarTitleView.setTypeface(fonts);
	        actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
	    }
	   
  }
		 	
	@SuppressWarnings("deprecation")
	public void onSharedPreferenceChangedList(SharedPreferences sharedPreferences, String key) {
	    Preference pref = findPreference(key);

	    if (pref instanceof ListPreference) {
	        ListPreference listPref = (ListPreference) pref;
	        pref.setSummary(listPref.getEntry());
	    }
	}
	
	@SuppressWarnings("deprecation")
	public void onSharedPreferenceChangedEdit(SharedPreferences sharedPreferences, String key) {
	    Preference pref = findPreference(key);

	    if (pref instanceof EditTextPreference) {
	    	EditTextPreference edit = (EditTextPreference) pref;
	        pref.setSummary(edit.getText());
	    }
	}
		
	
public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem mi = menu.add(0, 1, 0, R.string.action_settings );									
		mi.setIntent(new Intent(this, PrefActivity.class));
		
		MenuItem mie = menu.add(0, 1, 0, R.string.about );									
		mie.setIntent(new Intent(this, Info.class));
			
		//getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);		
		    
	}



}
