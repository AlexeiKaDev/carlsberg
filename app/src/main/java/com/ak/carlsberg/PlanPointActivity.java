package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("ResourceAsColor")
public class PlanPointActivity extends AppCompatActivity {
		
	private static final String DB_NAME = "monolit.db";
	int geovisits;
	SharedPreferences sp;
	SQLiteDatabase db;
	String salesplan;
	float avgday;
	private ArrayList<HashMap<String, Object>> despatchList;	
	private static final String DESP1= "Name Point";
	private static final String DESP2 = "AVGplan";
	private static final String DESP3= "WeightPoint";
	private static final String DESP4= "Plan";
	private static final String DESP5= "Orders";
	/** Called when the activity is first created. */
	
	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.planpoint);
   
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
     bCalendar.set(Calendar.HOUR_OF_DAY, 00);
    bCalendar.set(Calendar.MINUTE, 00);
    bCalendar.set(Calendar.MILLISECOND, 00);
    bCalendar.set(Calendar.SECOND,00);
    int MaxDayInMonth =  bCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    final int  deninedeli = bCalendar.get(Calendar.DAY_OF_WEEK);

    String thisday = getFullTime(bCalendar.getTimeInMillis());     
    
    bCalendar.set(Calendar.DATE, 1);
    
	final String firstday =  getFullTime(bCalendar.getTimeInMillis());
    // set actual maximum date of previous month
    bCalendar.set(Calendar.DATE, bCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    //read it
    final String todaydate = getFullTime(bCalendar.getTimeInMillis());

    ExternalDbOpenHelper extdb = new ExternalDbOpenHelper(this, DB_NAME);
    db = extdb.openDataBase();
                            try {
                                    db.beginTransaction();
                  /******************************************************************************************/                  
                                    String query="SELECT TargetValue FROM TaskReportView where LineNumber = 1";
                                	Cursor cursor = db.rawQuery(query, null);
                                	while (cursor.moveToNext()) {
                            			salesplan = cursor.getString(0);
                            			}
                                	cursor.close();
                                	
                                	Boolean planuse = sp.getBoolean("planuse", false);
                        		    if (planuse ==true){
                        		    	String salespl = sp.getString("useplan", "");
                        		    	 if (salespl.isEmpty()== true){
                        		    		 salesplan ="1";
                        		    	 }
                        		    	 else {salesplan = sp.getString("useplan", "");}}                       		                          		                          		    
                        		    
                        		   int[] days = getWorkDays(MaxDayInMonth);
                        		   TextView Mo = (TextView) findViewById(R.id.TextView21);
                        		   Mo.setText(String.valueOf(days[0]));
                        		   TextView Tu = (TextView) findViewById(R.id.TextView19);
                        		   Tu.setText(String.valueOf(days[1]));
                        		   TextView We = (TextView) findViewById(R.id.TextView15);
                        		   We.setText(String.valueOf(days[2]));
                        		   TextView Th = (TextView) findViewById(R.id.TextView02);
                        		   Th.setText(String.valueOf(days[3]));
                        		   TextView Fr = (TextView) findViewById(R.id.TextView10);
                        		   Fr.setText(String.valueOf(days[4]));
                        		   
                        		   float pervii = (Float.parseFloat(sp.getString("monday", "20")))/100*Float.parseFloat(salesplan)/days[0];
                        		   TextView perv = (TextView) findViewById(R.id.TextView22);
                        		   perv.setText(sp.getString("monday", "20")+"%");
                        		   float vtoroi = (Float.parseFloat(sp.getString("tuesday", "20")))/100*Float.parseFloat(salesplan)/days[1];
                        		   TextView vtor = (TextView) findViewById(R.id.TextView26);
                        		   vtor.setText(sp.getString("tuesday", "20")+"%");
                        		   float tretii = (Float.parseFloat(sp.getString("wednesday", "20")))/100*Float.parseFloat(salesplan)/days[2];
                        		   TextView tret = (TextView) findViewById(R.id.TextView27);
                        		   tret.setText(sp.getString("wednesday", "20")+"%");
                        		   float cetvertii = (Float.parseFloat(sp.getString("thuesday", "20")))/100*Float.parseFloat(salesplan)/days[3];
                        		   TextView cet = (TextView) findViewById(R.id.TextView28);
                        		   cet.setText(sp.getString("thuesday", "20")+"%");
                        		   float piatii = (Float.parseFloat(sp.getString("friday", "20")))/100*Float.parseFloat(salesplan)/days[4];
                        		   TextView frid = (TextView) findViewById(R.id.TextView29);
                        		   frid.setText(sp.getString("friday", "20")+"%");
                        		   
                        		   TextView deni1 = (TextView) findViewById(R.id.TextView23);
                        		   deni1.setText(String.valueOf(pervii));
                        		   TextView deni2 = (TextView) findViewById(R.id.TextView20);
                        		   deni2.setText(String.valueOf(vtoroi));
                        		   TextView deni3 = (TextView) findViewById(R.id.TextView16);
                        		   deni3.setText(String.valueOf(tretii));
                        		   TextView deni4 = (TextView) findViewById(R.id.TextView05);
                        		   deni4.setText(String.valueOf(cetvertii));
                        		   TextView deni5 = (TextView) findViewById(R.id.TextView09);
                        		   deni5.setText(String.valueOf(piatii));
                        		   
                        		   
                        		   if (deninedeli == 2){ avgday =pervii; deni1.setBackgroundResource(R.color.carlgreen);deni1.setTextColor(Color.WHITE);
                        		   TextView d1= (TextView) findViewById(R.id.TextView25);
                        		   d1.setBackgroundResource(R.color.carlgreen);d1.setTextColor(Color.WHITE);} 
                        		   if (deninedeli == 3){ avgday =vtoroi; deni2.setBackgroundResource(R.color.carlgreen);deni2.setTextColor(Color.WHITE);
                        		   TextView d2= (TextView) findViewById(R.id.TextView18);
                        		   d2.setBackgroundResource(R.color.carlgreen);d2.setTextColor(Color.WHITE);} 
                        		   if (deninedeli == 4){ avgday =tretii; deni3.setBackgroundResource(R.color.carlgreen);deni3.setTextColor(Color.WHITE);
                        		   TextView d3= (TextView) findViewById(R.id.TextView14);
                        		   d3.setBackgroundResource(R.color.carlgreen);d3.setTextColor(Color.WHITE);} 
                        		   if (deninedeli == 5){ avgday =cetvertii; deni4.setBackgroundResource(R.color.carlgreen);deni4.setTextColor(Color.WHITE);
                        		   TextView d4= (TextView) findViewById(R.id.TextView04);
                        		   d4.setBackgroundResource(R.color.carlgreen);d4.setTextColor(Color.WHITE);} 
                        		   if (deninedeli == 6){ avgday =piatii; deni5.setBackgroundResource(R.color.carlgreen);deni5.setTextColor(Color.WHITE);
                        		   TextView d5= (TextView) findViewById(R.id.TextView13);
                        		   d5.setBackgroundResource(R.color.carlgreen);d5.setTextColor(Color.WHITE);} 
                        		   
                        	 /*****************************************����� ������ �����***************************/
                                	String query1 =" SELECT t.LegalName,t1.ORDERS,ROUND((t2.DESPATCH/t3.DESPATCHall),4) AS Weight,t3.DESPATCHall FROM "+
                               			" (SELECT LegalName,CRMJobSchedule.ClientAddressId  FROM CRMJobSchedule,ClientAddress WHERE PlanDate >='"+thisday+"' AND PlanDate < date('"+thisday+"' ,'+1 day') AND CRMJobSchedule.ClientAddressId = ClientAddress.ClientAddressId) t  "+
                               			" LEFT JOIN "+
                               			" (SELECT PPCOrder.ClientAddressId,CAST( ROUND( SUM(CAST ((PPCOrderLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS ORDERS FROM Unit,PPCOrderLine,ClientAddress,PPCOrder WHERE Unit.WareId=PPCOrderLine.WareId AND PPCOrderLine.CreateId >= '"+thisday+"' AND PPCOrderLine.CreateId < date('"+thisday+"' ,'+1 day') AND Unit.UnitId='dal' AND PPCOrder.ClientAddressId = ClientAddress.ClientAddressId AND PPCOrderLine.OrderNumber = PPCOrder.OrderNumber GROUP BY PPCOrder.ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
                               			" LEFT JOIN "+
                               			" (SELECT DespatchLine.ClientAddressId,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS DESPATCH FROM Unit,DespatchLine,ClientAddress WHERE Unit.WareId=DespatchLine.WareId AND Unit.UnitId='dal' AND DespatchLine.ClientAddressId = ClientAddress.ClientAddressId GROUP BY DespatchLine.ClientAddressId) t2 ON t.ClientAddressId = t2.ClientAddressId, "+
                               			" (SELECT DespatchLine.ClientAddressId,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS DESPATCHall FROM Unit,DespatchLine,ClientAddress WHERE Unit.WareId=DespatchLine.WareId AND Unit.UnitId='dal' AND DespatchLine.ClientAddressId = ClientAddress.ClientAddressId AND DespatchLine.ClientAddressId IN "+
                               			" (SELECT ClientAddressId  FROM CRMJobSchedule WHERE PlanDate >='"+thisday+"' AND PlanDate < date('"+thisday+"' ,'+1 day'))) t3";
                                	
                                	
                                	Cursor cursor1 = db.rawQuery(query1, null);
                                		double newc;
                                		if (cursor1.getCount()==0){newc=1;} else { newc = cursor1.getCount();}
                                	 	double avgpoint =BigDecimal.valueOf(avgday/newc).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue(); 
                                	 	double sumallorder = 0;
                                	 while (cursor1.moveToNext()) {                                     		                                   		
                                     	hm = new HashMap<String, Object>();
                                 		hm.put(DESP1, cursor1.getString(0));
                                 		hm.put(DESP2, avgpoint);
                                 		double sim,procent = 0;
                                 		if  (cursor1.getString(2)==null) {procent =0;} 
                                 		else {procent = BigDecimal.valueOf((Float.parseFloat(cursor1.getString(2)))*100).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();}
                                 		String procentfull = String.valueOf(procent)+"%";
                                 		hm.put(DESP3, procentfull);
                                 		double newplan =BigDecimal.valueOf((procent/100)*avgday).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
                                 		hm.put(DESP4,newplan); 
                                 		hm.put(DESP5, cursor1.getString(1));
                                 		despatchList.add(hm);
                                 		if  (cursor1.getString(1)==null) {sim =0;} 
                                 		else {sim = BigDecimal.valueOf(Float.parseFloat(cursor1.getString(1))).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();}
                                 		sumallorder = sumallorder + sim;
                             	}
                                	 double n = BigDecimal.valueOf(avgday-sumallorder).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
                                	 TextView all= (TextView) findViewById(R.id.textView1);
                                	 all.setText(String.valueOf(n));
                                	 
                                	 cursor1.close();
                                	 
                                	 
                 SimpleAdapter adapterList = new SimpleAdapter(this, despatchList,
         		R.layout.list_item_planpoint, new String[] { DESP1, DESP2, DESP3, DESP4, DESP5},
         		new int[] { R.id.TextView05, R.id.TextView01, R.id.TextView06, R.id.TextView04,R.id.TextView02 });
                              	
                 lv.setAdapter(adapterList);                                 
                                	
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

                     	    	      finish();
                     	    	 Intent intent = new Intent(PlanPointActivity.this, SalesTabActivity.class);
                    	    	   startActivity(intent);	     	 	    	   
                     	       }
                     	     };
                     	 
                     	      Button01.setOnClickListener(perescet);
 	  
 	        	
    
	}
		public static final int[] getWorkDays(int Days){
			
		int Monday =0;
		int Tuesday =0;
		int Wednesday =0;
		int Thuesday =0;
		int Friday=0;
		int[] Workdays = new int[5];
	    Calendar aCalendar = Calendar.getInstance();
	    aCalendar.set(Calendar.HOUR_OF_DAY, 00);
	    aCalendar.set(Calendar.MINUTE, 00);
	    aCalendar.set(Calendar.MILLISECOND, 00);
	    aCalendar.set(Calendar.SECOND,00);
	    
	    
	    Log.i("LOG_TAG", String.valueOf(aCalendar.getTime()));
		for(int i = 1 ; i <= Days; i++)
	    {        
	    	aCalendar.set(Calendar.DAY_OF_MONTH, i);
	    		if (aCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) 	    			
	    			{Monday++;Workdays[0] = Monday;}      	   
	    		else if (aCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) 
	    			{Tuesday++;Workdays[1] = Tuesday;}
	    		else if (aCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) 
	    			{Wednesday++;Workdays[2] = Wednesday;}
	    		else if (aCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) 
	    			{Thuesday++;Workdays[3] = Thuesday;}
	    		else if (aCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY ) 
	    			{Friday++;Workdays[4] = Friday;}
	    		
		}
		
		return Workdays;
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
