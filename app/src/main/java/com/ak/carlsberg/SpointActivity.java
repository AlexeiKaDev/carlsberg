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
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class SpointActivity extends AppCompatActivity {
		
	private static final String DB_NAME = "monolit.db";
	int geovisits;
	SharedPreferences sp;
	SQLiteDatabase db;
	private ArrayList<HashMap<String, Object>> despatchList;	
	private static final String DESP1= "File Name";
	private static final String DESP2 = "fact";
	private static final String DESP3= "geo";
	private static final String DESP4= "wait";
	/** Called when the activity is first created. */
	
	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.spoint);
   
    despatchList = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> hm;    
    final ListView lv = (ListView)findViewById(R.id.listViewDes);
    
    int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
    TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
    Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
    if(actionBarTitleView != null){
        actionBarTitleView.setTypeface(fonts);
        actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
    }
    
    @SuppressWarnings("unused")
	final String LOG_TAG = null;
  
    Calendar bCalendar = Calendar.getInstance();
    bCalendar.add(Calendar.MONTH,0);
    // �������� ������ ����� ������         
    bCalendar.set(Calendar.HOUR_OF_DAY, 00);
    bCalendar.set(Calendar.MINUTE, 00);
    bCalendar.set(Calendar.MILLISECOND, 00);
    bCalendar.set(Calendar.SECOND,00);
    int day = bCalendar.get(Calendar.DAY_OF_MONTH);
    bCalendar.set(Calendar.DATE, day-1);
    
    String thisday = getFullTime(bCalendar.getTimeInMillis());
    
    bCalendar.set(Calendar.DATE, 1);
    
	final String firstday =  getFullTime(bCalendar.getTimeInMillis());
    // set actual maximum date of previous month
    bCalendar.set(Calendar.DATE, bCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    //read it
    final String todaydate = getFullTime(bCalendar.getTimeInMillis());
    
    Log.i("LOG_TAG", "������ ���� " + firstday +" ��������� ���� "+todaydate + "������� "+thisday);
    
    ExternalDbOpenHelper extdb = new ExternalDbOpenHelper(this, DB_NAME);
    db = extdb.openDataBase();
                            try {
                                    db.beginTransaction();
                                    
                    /*****************************************����� ������ �����***************************/
                                	String query1 =" SELECT t.ClientAddressId,t.LegalName, t1.REAL, t2.REAL,ROUND (t2.REAL/t1.REAL) AS AVGDAL FROM "+                                			
                                				   " (SELECT ClientAddressId,LegalName FROM ClientAddress) t LEFT JOIN " +
                                				   " (SELECT DespatchLine.ClientAddressId,ClientAddress.LegalName,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS REAL FROM Unit,DespatchLine,ClientAddress " +
                                				   " WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND DespatchLine.ClientAddressId = ClientAddress.ClientAddressId GROUP BY DespatchLine.ClientAddressId) t1 " +
                                				   " ON t.ClientAddressId = t1.ClientAddressId  LEFT JOIN " +
                                				   " (SELECT Sum(Quantity*Price) AS REAL,ClientAddressId FROM DespatchLine,Unit " +
                                				   " WHERE CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.WareId=DespatchLine.WareId AND Unit.UnitId='dal' GROUP BY ClientAddressId) t2 " +
                                				   		" ON t.ClientAddressId = t2.ClientAddressId";
                                	
                                	
                                	Cursor cursor1 = db.rawQuery(query1, null);                                	
                                	 
                                	 while (cursor1.moveToNext()) {                                     		                                   		
                                     	hm = new HashMap<String, Object>();
                                 		hm.put(DESP1, cursor1.getString(1));
                                 		hm.put(DESP2, cursor1.getString(2));
                                 		hm.put(DESP3, cursor1.getString(3));
                                 		hm.put(DESP4,cursor1.getString(4));                                		                                 		
                                 		despatchList.add(hm);
                                 		
                             	}
                                	 TextView tt = (TextView) findViewById(R.id.textView1);
                          		   	 tt.setText(String.valueOf(cursor1.getCount()));
                          		   	 
                                	 cursor1.close();
                                	                                                          	                            	 
                 SimpleAdapter adapterList = new SimpleAdapter(this, despatchList,
         		R.layout.list_item_spoint, new String[] { DESP1, DESP2, DESP3, DESP4},
         		new int[] { R.id.TextView05, R.id.TextView06, R.id.TextView04,R.id.TextView02 });
                              	
                 lv.setAdapter(adapterList); 
                 
                 String query2 =" SELECT COUNT(ClientAddressId) FROM DespatchLine WHERE CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day')  GROUP BY ClientAddressId";
        	
        	
                 					Cursor cursor2 = db.rawQuery(query2, null);  
                 					TextView tt1 = (TextView) findViewById(R.id.TextView01);
                         		   	 tt1.setText(String.valueOf(cursor2.getCount()));
  		   	 
        	 						cursor2.close();
                                	
                                    db.setTransactionSuccessful();
                            } catch (SQLException e) {
                            } finally {
                                    db.endTransaction();
                                    db.close();
                            }
                                                 	                                                             	
 	 
                            Button Button01 = (Button) findViewById(R.id.Button01);
                        	OnClickListener perescet = new OnClickListener() {
                     	       @Override
                     	       public void onClick(View v) {
                     	    	   /*��������� � �������� Prezentations*/ 
                     	    	   finish();
                     	    	 Intent intent = new Intent(SpointActivity.this, SalesTabActivity.class);
                    	    	   startActivity(intent);	     	 	    	   
                     	       }
                     	     };
                     	 
                     	     // �������� ���������� ������ OK (btnOk)
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
