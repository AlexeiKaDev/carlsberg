package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class EmailActivity extends AppCompatActivity {
		
	private static final String DB_NAME = "monolit.db";
	int geovisits;
	SharedPreferences sp;
	SQLiteDatabase db;
	String cod;
	String territoria;
	String name;
	/** Called when the activity is first created. */
	
	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.email);
   
    int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
    TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
    Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
    if(actionBarTitleView != null){
        actionBarTitleView.setTypeface(fonts);
        actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
    }
    Typeface Days = Typeface.createFromAsset(getAssets(), "fonts/Days.ttf");
    final EditText editText1 = (EditText) findViewById(R.id.editText1);        
    editText1.setTypeface(Days);
    
    @SuppressWarnings("unused")
	final String LOG_TAG = null;
  
    Calendar bCalendar = Calendar.getInstance();
    bCalendar.add(Calendar.MONTH,0);

    bCalendar.set(Calendar.HOUR_OF_DAY, 00);
    bCalendar.set(Calendar.MINUTE, 00);
    bCalendar.set(Calendar.MILLISECOND, 00);
    bCalendar.set(Calendar.SECOND,00);   
    String thisday = getFullTime(bCalendar.getTimeInMillis());
    
    ExternalDbOpenHelper extdb = new ExternalDbOpenHelper(this, DB_NAME);
    db = extdb.openDataBase();
    try {
            db.beginTransaction();
            String query = "SELECT PersonId,FinManagerName,PersonName FROM Pars";
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
            	cod = cursor.getString(0);
            	territoria = cursor.getString(1);
            	name = cursor.getString(2);

        	}
        	cursor.close();                   

            db.setTransactionSuccessful();
    } catch (SQLException e) {
    } finally {
            db.endTransaction();
    }
                            Button Button01 = (Button) findViewById(R.id.Button01);
                        	OnClickListener perescet = new OnClickListener() {
                     	       @Override
                     	       public void onClick(View v) {                     	    	                        	    	   
                     	    	 
                     	    	   final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                     	    	    
                     	    	    emailIntent.setType("plain/text");

                     	    	    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                     	    	        new String[] { "kusocek@gmail.com" });

                     	    	    String subject = getText(R.string.theme).toString() +" "+ getText(R.string.app_name).toString();

                     	    	    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);

                     	    	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                     	    	    		editText1.getText().toString() +"\n\n"+
                     	    	    		getText(R.string.name).toString() +"    "+ name +"\n"+
                     	    	    		getText(R.string.cod).toString() +"    "+ cod +"\n"+
                     	    	    		getText(R.string.territoria).toString() +"    "+ territoria +"\n");                     	    	  

                     	    	    EmailActivity.this.startActivity(Intent.createChooser(emailIntent,
                     	    	        "Выберите приложение для оптравки сообщения..."));
                     	    	    
                     	       }
                     	     };

                     	    Button01.setOnClickListener(perescet);
 	                       	  
    
	}
		public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem mi = menu.add(0, 1, 0, R.string.action_settings );									
		mi.setIntent(new Intent(this, PrefActivity.class));
		
		MenuItem mie = menu.add(0, 1, 0, R.string.about );									
		mie.setIntent(new Intent(this, Info.class));
			
		//getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);		
		    
	}
		/*******************************************������ ����************************************************/
		 @SuppressLint("SimpleDateFormat")
			public static final String getFullTime(final long timeInMillis)
	    {
	        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        final Calendar c = Calendar.getInstance();
	        c.setTimeInMillis(timeInMillis);
	        c.setTimeZone(TimeZone.getDefault());
	        return format.format(c.getTime());
	    }
}
