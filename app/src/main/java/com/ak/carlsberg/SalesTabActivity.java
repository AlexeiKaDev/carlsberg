package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

		
	public class SalesTabActivity extends TabActivity {
	    /** Called when the activity is first created. */
		@SuppressLint("ResourceAsColor")
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.salestab);
	        
	        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
	        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
	        Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
	        if(actionBarTitleView != null){
	            actionBarTitleView.setTypeface(fonts);
	            actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
	        }

	        TabHost tabHost = getTabHost();
	        
	        // ������������� ���� ��������� � getTabHost
	        // ����� setup �������� �� �����
	        
	        TabHost.TabSpec tabSpec;
	        
	        tabSpec = tabHost.newTabSpec("tag1");
	        tabSpec.setIndicator(getText(R.string.alldal));
	        tabSpec.setContent(new Intent(this, SalesActivity.class));
	        tabHost.addTab(tabSpec);
	        
	        tabSpec = tabHost.newTabSpec("tag2");
	        tabSpec.setIndicator(getText(R.string.keg));
	        tabSpec.setContent(new Intent(this, SalesKegActivity.class));
	        tabHost.addTab(tabSpec);
	        
	        tabSpec = tabHost.newTabSpec("tag3");
	        tabSpec.setIndicator(getText(R.string.packing));
	        tabSpec.setContent(new Intent(this, SalesPackActivity.class));
	        tabHost.addTab(tabSpec);
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
