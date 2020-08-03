package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class VisitsActivity extends AppCompatActivity {
		
	private static final String DB_NAME = "monolit.db";
	double geovisits =0;
	SharedPreferences sp;
	SQLiteDatabase db;
	private ArrayList<HashMap<String, Object>> despatchList;
	private static final String TITLE = "Point Name";
	private static final String DESP1= "plan";
	private static final String DESP2 = "fact";
	private static final String DESP3= "geo";
	private static final String DESP4= "wait";
	private static final String DESP5= "cancel";
	private static final String DESP6= "date";
	String []geoviz;
	int month;
	float made,madeday;
	/** Called when the activity is first created. */
	
	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.visits);
   
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
    sp = PreferenceManager.getDefaultSharedPreferences(this);
    @SuppressWarnings("unused")
	final String LOG_TAG = null;
  
    Calendar bCalendar = Calendar.getInstance();
    bCalendar.add(Calendar.MONTH,0);
    // �������� ������ ����� ������         
    bCalendar.set(Calendar.HOUR_OF_DAY, 00);
    bCalendar.set(Calendar.MINUTE, 00);
    bCalendar.set(Calendar.MILLISECOND, 00);
    bCalendar.set(Calendar.SECOND,00);
    String thisday = getFullTime(bCalendar.getTimeInMillis());
    bCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    String monday = getFullTime(bCalendar.getTimeInMillis());
    String perem = sp.getString("lang", "default");
    Log.i("LOG_TAG", "���� " + perem);
    if (perem == "en"|| perem =="be"){month =6;} else{month =11;}
    bCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY + month);
    String sunday =  getFullTime(bCalendar.getTimeInMillis());   
    // set actual maximum date of previous month
    
    Log.i("LOG_TAG", "����������� " + monday +"����������� " + sunday +" ������� " + thisday);
    
    RotateAnimation ranim = (RotateAnimation)AnimationUtils.loadAnimation(this, R.anim.rotate);
    ranim.setFillAfter(true); //For the textview to remain at the same place after the rotation
    TextView planv = (TextView) findViewById(R.id.TextView02);
    TextView factv = (TextView) findViewById(R.id.TextView05);
    TextView geov = (TextView) findViewById(R.id.TextView04);
    TextView waitv = (TextView) findViewById(R.id.TextView11);
    TextView cancelv = (TextView) findViewById(R.id.TextView10);
    planv.setAnimation(ranim);
    factv.setAnimation(ranim);
    geov.setAnimation(ranim);
    waitv.setAnimation(ranim);
    cancelv.setAnimation(ranim);
    ExternalDbOpenHelper extdb = new ExternalDbOpenHelper(this, DB_NAME);
    db = extdb.openDataBase();
                            try {
                                    db.beginTransaction();
                                    String query =" SELECT DISTINCT CRMJobScheduleCompleteLocation.ClientAddressId,CRMJobScheduleCompleteLocation.PlanDate,CRMJobScheduleCompleteLocation.Latitude,CRMJobScheduleCompleteLocation.Longitude,ClientAddressGeoLocation.Latitude,ClientAddressGeoLocation.Longitude FROM "+
                                    	" CRMJobScheduleCompleteLocation,ClientAddressGeoLocation,CRMJobSchedule WHERE CRMJobScheduleCompleteLocation.PlanDate >= '"+monday+"'  AND CRMJobScheduleCompleteLocation.PlanDate < date('"+sunday+"'  ,'+1 day') AND CRMJobScheduleCompleteLocation.ClientAddressId = ClientAddressGeoLocation.ClientAddressId "+
                                    	" AND CRMJobScheduleCompleteLocation.Latitude IS NOT NULL AND CRMJobScheduleCompleteLocation.Longitude IS NOT NULL AND ClientAddressGeoLocation.Latitude IS NOT NULL AND ClientAddressGeoLocation.Longitude IS NOT NULL AND CRMJobSchedule.ClientAddressId = CRMJobScheduleCompleteLocation.ClientAddressId AND CRMJobSchedule.CompleteStatus='Completed'";
                                    
                                    Cursor cursor = db.rawQuery(query, null);
                                    geoviz = new String[cursor.getCount()];                                 	                                   		                         
                                    while (cursor.moveToNext()) {                                     		                                   		
                                    	Location locationA = new Location("point A");
                                        locationA.setLatitude(Double.valueOf(cursor.getString(2)));
                                        locationA.setLongitude(Double.valueOf(cursor.getString(3)));
                                        Location locationB = new Location("point B");
                                        locationB.setLatitude(Double.valueOf(cursor.getString(4)));
                                        locationB.setLongitude(Double.valueOf(cursor.getString(5)));
                                        float currentDistance = locationA.distanceTo(locationB);
                                        if( currentDistance<100){ geovisits = geovisits+1; }                     			                                			
                                        Log.i("LOG_TAG", "���������� �� ������ "+ currentDistance);
                                        Log.i("LOG_TAG", "���-�� ������������� ������� "+ geovisits);
                                	}
                                   
                                	cursor.close();
                    /*****************************************����� ������ �����***************************/
                                	String query1 ="SELECT DISTINCT t.LegalName,t.ClientAddressId, PlanVizit, RealVizit, waitVizit,CanceledVizit,lat1,lon1,lat2,lon2, t1.PlanDate FROM "+
                                			" (SELECT LegalName,CRMJobSchedule.ClientAddressId FROM ClientAddress LEFT JOIN CRMJobSchedule ON CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId) t "+
                                			" INNER JOIN "+
                                			" (SELECT COUNT(CRMJobSchedule.ClientAddressId) AS PlanVizit,CRMJobSchedule.ClientAddressId,PlanDate FROM CRMJobSchedule,ClientAddress WHERE PlanDate >= '"+monday+"'   AND PlanDate < date('"+sunday+"'  ,'+1 day') AND CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId GROUP BY CRMJobSchedule.ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
                                			" LEFT JOIN "+
                                			" (SELECT COUNT(CRMJobSchedule.ClientAddressId) AS RealVizit,CRMJobSchedule.ClientAddressId FROM CRMJobSchedule,ClientAddress WHERE PlanDate >= '"+monday+"'   AND PlanDate < date('"+sunday+"'  ,'+1 day') AND CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId AND CompleteStatus ='Completed' GROUP BY CRMJobSchedule.ClientAddressId ) t2 ON t.ClientAddressId = t2.ClientAddressId "+
                                			" LEFT JOIN "+
                                			" (SELECT COUNT(CRMJobSchedule.ClientAddressId) AS waitVizit,CRMJobSchedule.ClientAddressId FROM CRMJobSchedule,ClientAddress WHERE PlanDate >= '"+monday+"'   AND PlanDate < date('"+sunday+"'  ,'+1 day') AND CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId AND CompleteStatus ='wait' GROUP BY CRMJobSchedule.ClientAddressId ) t3 ON t.ClientAddressId = t3.ClientAddressId "+
                                			" LEFT JOIN "+
                                			" (SELECT COUNT(CRMJobSchedule.ClientAddressId) AS CanceledVizit,CRMJobSchedule.ClientAddressId FROM CRMJobSchedule,ClientAddress WHERE PlanDate >= '"+monday+"'  AND PlanDate < date('"+sunday+"'  ,'+1 day') AND CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId AND CompleteStatus ='Canceled' GROUP BY CRMJobSchedule.ClientAddressId ) t4 ON t.ClientAddressId = t4.ClientAddressId "+
                                			" LEFT JOIN "+
                                			" (SELECT DISTINCT CRMJobScheduleCompleteLocation.ClientAddressId,CRMJobScheduleCompleteLocation.PlanDate,CRMJobScheduleCompleteLocation.Latitude AS lat1,CRMJobScheduleCompleteLocation.Longitude AS lon1,ClientAddressGeoLocation.Latitude AS lat2,ClientAddressGeoLocation.Longitude AS lon2 FROM "+
                                			" CRMJobScheduleCompleteLocation,ClientAddressGeoLocation,CRMJobSchedule WHERE CRMJobScheduleCompleteLocation.PlanDate >= '"+monday+"'  AND CRMJobScheduleCompleteLocation.PlanDate < date('"+sunday+"'  ,'+1 day') AND CRMJobScheduleCompleteLocation.ClientAddressId = ClientAddressGeoLocation.ClientAddressId "+
                                    	    " AND CRMJobScheduleCompleteLocation.Latitude IS NOT NULL AND CRMJobScheduleCompleteLocation.Longitude IS NOT NULL AND ClientAddressGeoLocation.Latitude IS NOT NULL AND ClientAddressGeoLocation.Longitude IS NOT NULL AND CRMJobSchedule.ClientAddressId = CRMJobScheduleCompleteLocation.ClientAddressId AND CRMJobSchedule.CompleteStatus='Completed') t5 ON t.ClientAddressId = t5.ClientAddressId ORDER BY t1.PlanDate ASC ";
                                    
                                	
                                	
                                	Cursor cursor1 = db.rawQuery(query1, null);                                	
                                	 
                                	 while (cursor1.moveToNext()) { 
                                		 Location locationA = new Location("point A");
                                		 String lati1;
                                		 String lati2;
                                		 String loni1;
                                		 String loni2;
                                		 if (cursor1.getString(6) ==null) {lati1 ="100";} else{lati1 =cursor1.getString(6);}
                                         locationA.setLatitude(Double.valueOf(lati1));
                                         if (cursor1.getString(7) ==null) {loni1 ="100";} else{loni1 =cursor1.getString(7);}
                                         locationA.setLongitude(Double.valueOf(loni1));
                                         Location locationB = new Location("point B");
                                         if (cursor1.getString(8) ==null) {lati2 ="0";} else{lati2 =cursor1.getString(8);}
                                         locationB.setLatitude(Double.valueOf(lati2));
                                         if (cursor1.getString(9) ==null) {loni2 ="0";} else{loni2 =cursor1.getString(9);}
                                         locationB.setLongitude(Double.valueOf(loni2));
                                         float currentDistance = locationA.distanceTo(locationB);
                                         String realgeo;
                                         if( currentDistance<100){ realgeo ="1";}else{realgeo ="";}                     			                                			
                                         Log.i("LOG_TAG", "���������� �� ������ "+ currentDistance);
                                         Log.i("LOG_TAG", "���-�� ������������� ������� "+ geovisits);
                                		 
                                     	hm = new HashMap<String, Object>();
                                        hm.put(TITLE, cursor1.getString(0)); // ��������
                                 		hm.put(DESP1, cursor1.getString(2));
                                 		hm.put(DESP2, cursor1.getString(3));
                                 		hm.put(DESP3, realgeo);
                                 		hm.put(DESP4,cursor1.getString(4));
                                 		hm.put(DESP5,cursor1.getString(5));
                                 		hm.put(DESP6,cursor1.getString(10)); 
                                 		despatchList.add(hm);
                                 		
                             	}
                                	 cursor1.close();
                 SimpleAdapter adapterList = new SimpleAdapter(this, despatchList,
         		R.layout.list_item_visits, new String[] { TITLE, DESP1, DESP2, DESP3, DESP4, DESP5, DESP6},
         		new int[] { R.id.TextView07, R.id.TextView05, R.id.TextView06, R.id.TextView04,R.id.TextView02,R.id.TextView01,R.id.textView1 });
                              	
                 lv.setAdapter(adapterList); 
                 
                                	String query2="SELECT DISTINCT PlanVizit, RealVizit, waitVizit,CanceledVizit FROM " +
                                			"(SELECT LegalName,CRMJobSchedule.ClientAddressId FROM ClientAddress LEFT JOIN CRMJobSchedule ON CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId) t " +
                                			"LEFT JOIN" +
                                			"(SELECT COUNT(CRMJobSchedule.ClientAddressId) AS PlanVizit,CRMJobSchedule.ClientAddressId FROM CRMJobSchedule,ClientAddress WHERE PlanDate >= '"+monday+"' AND PlanDate < date('"+sunday+"' ,'+1 day') AND CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId) t1," +
                                			"(SELECT COUNT(CRMJobSchedule.ClientAddressId) AS RealVizit,CRMJobSchedule.ClientAddressId FROM CRMJobSchedule,ClientAddress WHERE PlanDate >= '"+monday+"' AND PlanDate < date('"+sunday+"' ,'+1 day') AND CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId AND CompleteStatus ='Completed') t2," +
                                			"(SELECT COUNT(CRMJobSchedule.ClientAddressId) AS waitVizit,CRMJobSchedule.ClientAddressId FROM CRMJobSchedule,ClientAddress WHERE PlanDate >= '"+monday+"' AND PlanDate < date('"+sunday+"' ,'+1 day') AND CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId AND CompleteStatus ='wait') t3," +
                                			"(SELECT COUNT(CRMJobSchedule.ClientAddressId) AS CanceledVizit,CRMJobSchedule.ClientAddressId FROM CRMJobSchedule,ClientAddress WHERE PlanDate >= '"+monday+"' AND PlanDate < date('"+sunday+"' ,'+1 day') AND CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId AND CompleteStatus ='Canceled') t4";
                 					Cursor cursor2 =db.rawQuery(query2, null);
                 					 while (cursor2.moveToNext()) {                                     		                                   		
                 					TextView plan = (TextView) findViewById(R.id.TextView01);
                            		plan.setText(cursor2.getString(0));
                            		TextView implement = (TextView) findViewById(R.id.TextView06);
                            		made=Integer.parseInt(cursor2.getString(1));
                            		implement.setText(cursor2.getString(1));
                            		TextView geo = (TextView) findViewById(R.id.TextView03);
                            		geo.setText(String.valueOf(geovisits));
                            		TextView wait = (TextView) findViewById(R.id.TextView13);
                            		wait.setText(cursor2.getString(2));
                            		TextView cancel = (TextView) findViewById(R.id.TextView08);
                            		cancel.setText(cursor2.getString(3));
                                         
                            		TextView fact = (TextView) findViewById(R.id.TextView12);
                            		float planvisits = Float.parseFloat(cursor2.getString(0));
                            		if (planvisits==0){planvisits=1;}
                            		float canceled = Float.parseFloat(cursor2.getString(3));
                            		double factvisits =BigDecimal.valueOf((geovisits/(planvisits-canceled))*100).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
                            		String plus=String.valueOf(factvisits)+"%";
                            		fact.setText(plus);	
                                  		
                              	}
                 
                 					cursor2.close(); 
                 					
                 					
                 					String query3="SELECT ClientAddressId FROM PPCOrder WHERE CreateId >= '"+monday+"' AND CreateId < date('"+sunday+"' ,'+1 day');";
                 					Cursor cursor3 =db.rawQuery(query3, null);			
                 					TextView orders = (TextView) findViewById(R.id.TextView17);                				
                 					orders.setText(String.valueOf(cursor3.getCount()));
                            		
                            		TextView strike = (TextView) findViewById(R.id.TextView16);
                 					float str = (Float.parseFloat(String.valueOf(cursor3.getCount()))/made) *100;
                            		strike.setText(String.valueOf(str)+"%");
                 					cursor3.close();
                 					
                 					String query5="SELECT COUNT(ClientAddressId) AS RealVizit FROM CRMJobSchedule WHERE PlanDate >= '"+thisday+"'   AND PlanDate < date('"+thisday+"'  ,'+1 day') AND CompleteStatus ='Completed' GROUP BY ClientAddressId";
                 					Cursor cursor5 =db.rawQuery(query5, null);			
                 					
                 					madeday = (Float.parseFloat(String.valueOf(cursor5.getCount())));
                 					 Log.i("LOG_TAG", " ��������� ������� �� �������"+ madeday);
                            		
                 					cursor5.close();
                 					
                 					String query4="SELECT ClientAddressId FROM PPCOrder WHERE CreateId >= '"+thisday+"' AND CreateId < date('"+thisday+"' ,'+1 day');";
                 					Cursor cursor4 =db.rawQuery(query4, null);			
                 					TextView ordersday = (TextView) findViewById(R.id.TextView20);                				
                 					ordersday.setText(String.valueOf(cursor4.getCount()));
                            		
                            		TextView strikeday = (TextView) findViewById(R.id.TextView21);
                 					float strday = (Float.parseFloat(String.valueOf(cursor4.getCount()))/madeday) *100;
                            		strikeday.setText(String.valueOf(strday)+"%");
                 					cursor4.close();
                 					
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
                     	    	 Intent intent = new Intent(VisitsActivity.this, MainActivity.class);
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
