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
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class SalesKegActivity extends AppCompatActivity {
		
	private static final String DB_NAME = "monolit.db";
	String salesplan;
	String salesfact;
	String orders;
	String ordersthisday;
	float newsalesfact;
	SharedPreferences sp;
	SQLiteDatabase db; 
	/** Called when the activity is first created. */
	
	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sales);
   
    int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
    TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
    Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
    if(actionBarTitleView != null){
        actionBarTitleView.setTypeface(fonts);
        actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
    }
    sp = PreferenceManager.getDefaultSharedPreferences(this);
    
    Calendar bCalendar = Calendar.getInstance();
    bCalendar.add(Calendar.MONTH,0);

    bCalendar.set(Calendar.HOUR_OF_DAY, 00);
    bCalendar.set(Calendar.MINUTE, 00);
    bCalendar.set(Calendar.MILLISECOND, 00);
    bCalendar.set(Calendar.SECOND,00);
    int day = bCalendar.get(Calendar.DAY_OF_MONTH);
    bCalendar.set(Calendar.DATE, day-1);
    
    String thisday = getFullTime(bCalendar.getTimeInMillis());
    
    bCalendar.set(Calendar.DATE, 1);
    
	String firstday =  getFullTime(bCalendar.getTimeInMillis());
    // set actual maximum date of previous month
    bCalendar.set(Calendar.DATE, bCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    //read it
    String todaydate = getFullTime(bCalendar.getTimeInMillis());
    
    Log.i("LOG_TAG", "������ ���� " + firstday +" ��������� ���� "+todaydate + "������� "+thisday);
    
    ExternalDbOpenHelper extdb = new ExternalDbOpenHelper(this, DB_NAME);
    db = extdb.openDataBase();
                            try {
                                    db.beginTransaction();
                                    String query = "SELECT Despatchs -" +
											"CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END AS Otgruz FROM (" +
											" (SELECT CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Despatchs FROM Unit,DespatchLine,Despatch " +
											" WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '" + firstday + "' AND DespatchLine.CustDate < date('" + todaydate + "' ,'+1 day') AND Unit.UnitId='dal' AND Unit.FactorValue <1 AND Despatch.CustNumber= DespatchLine.CustNumber AND DocumentTypeId ='Despatch') " +
											" LEFT JOIN " +
											" (SELECT CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Returnss FROM Unit,DespatchLine,Despatch " +
											" WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '" + firstday + "' AND DespatchLine.CustDate < date('" + todaydate + "' ,'+1 day') AND Unit.UnitId='dal'  AND Unit.FactorValue <1 AND Despatch.CustNumber= DespatchLine.CustNumber AND DocumentTypeId ='CustReturn' )) ";
                                    Cursor cursor = db.rawQuery(query, null);
                                   
                                    while (cursor.moveToNext()) {
                                			salesfact = cursor.getString(0);
                                		Log.i("LOG_TAG", "��������� ������� " + cursor.getString(0));
                                	}
                                    
                                	cursor.close();
                                	String query1="SELECT TargetValue FROM TaskReportView where LineNumber = 1";
                                	Cursor cursor1 = db.rawQuery(query1, null);
                                	while (cursor1.moveToNext()) {
                                		if (cursor1.getString(0) ==null){salesplan = "0";}
                                		else {salesplan = cursor1.getString(0);}
                            		Log.i("LOG_TAG", "��������� ������� " + cursor1.getString(0));
                                	}
                                	cursor1.close();
                                	
                                	String query2="SELECT CAST( SUM(CAST ((PPCOrderLine.Quantity)AS REAL)/Unit.FactorValue) AS REAL)AS REAL FROM Unit,PPCOrderLine WHERE Unit.WareId=PPCOrderLine.WareId AND Unit.FactorValue < 1 AND CreateId >= '"+thisday+"' AND CreateId < date('"+thisday+"' ,'+1 day') AND Unit.UnitId='dal'";
                                	Cursor cursor2 = db.rawQuery(query2, null);
                                	while (cursor2.moveToNext()) {
                            			orders = cursor2.getString(0);
                            		Log.i("LOG_TAG", "��������� ������� " + cursor2.getString(0));
                                	}
                                	cursor2.close();
                                	
                                	String query3="SELECT CAST( SUM(CAST ((PPCOrderLine.Quantity)AS REAL)/Unit.FactorValue) AS REAL)AS REAL FROM Unit,PPCOrderLine WHERE Unit.WareId=PPCOrderLine.WareId AND Unit.FactorValue < 1 AND CreateId >= '"+thisday+"' AND CreateId < date('"+thisday+"' ,'+2 day') AND Unit.UnitId='dal'";
                                	Cursor cursor3 = db.rawQuery(query3, null);
                                	while (cursor3.moveToNext()) {
                            			ordersthisday = cursor3.getString(0);
                            		Log.i("LOG_TAG", "��������� ������� " + cursor3.getString(0));
                                	}
                                	cursor3.close();
                                	
                                    db.setTransactionSuccessful();
                            } catch (SQLException e) {
                            } finally {
                                    db.endTransaction();
                                    db.close();
                            }
    /********************************���������� ������� ���� � ������***************************************/    	
    	
                            Button Button01 = (Button) findViewById(R.id.Button01);
                            Button01.setText(R.string.sales_daykeg);
                        	OnClickListener perescet = new OnClickListener() {
                     	       @Override
                     	       public void onClick(View v) {
                     	    	   /*��������� � �������� Prezentations*/ 
                     	    	   finish();
                     	    	 Intent intent = new Intent(SalesKegActivity.this, PlanPointKegActivity.class);
                    	    	   startActivity(intent);	     	 	    	   
                     	       }
                     	     };
                     	 
                     	     // �������� ���������� ������ OK (btnOk)
                     	    Button01.setOnClickListener(perescet);
                     	    
                     	   Button point = (Button) findViewById(R.id.point);
                     	   point.setText(R.string.kegsale);
                       	OnClickListener pere = new OnClickListener() {
                    	       @Override
                    	       public void onClick(View v) {
                    	    	   /*��������� � �������� Prezentations*/ 
                    	    	   finish();
                    	    	 Intent intent = new Intent(SalesKegActivity.this, SKegpointActivity.class);
                   	    	   startActivity(intent);	     	 	    	   
                    	       }
                    	     };
                    	 
                    	     // �������� ���������� ������ OK (btnOk)
                    	    point.setOnClickListener(pere);
    
    final Spinner spinner = (Spinner)findViewById(R.id.spinner1);
    
    ArrayAdapter<?> adapter = 
    		ArrayAdapter.createFromResource(this, R.array.spinner_sales, android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    	@Override
    	public void onItemSelected(AdapterView<?> parent,
    			View itemSelected, int selectedItemPosition, long selectedId) {
    		
    		if (selectedItemPosition ==0){
    			if(salesfact!=null){
    				int last =0;
    				int newd =0;
    				newsalesfact = Float.parseFloat(salesfact);
    				sales(last,newd);
    			}
    			else {
    				int last =0;
    				int newd =0;
    				newsalesfact = 0;
    				sales(last,newd);
    			}
    			
    		}
    		else if (selectedItemPosition ==1){
    			if(orders!=null){
    				if (salesfact ==null){salesfact="0";}
    				newsalesfact = Float.parseFloat(salesfact) + Float.parseFloat(orders);
    				int newd =0;
    				sales(lastday(),newd);
    			}
    			else {
    				int newd =0;
    				newsalesfact = Float.parseFloat(salesfact);
    				sales(lastday(),newd);
    			}
    		
    		}
    		
    		else if (selectedItemPosition ==2){
    			if(ordersthisday!=null){
    				newsalesfact = Float.parseFloat(salesfact) + Float.parseFloat(ordersthisday);
    				sales(lastday(),newday());
    			}
    			else {
    				newsalesfact = Float.parseFloat(salesfact);
    				sales(lastday(),newday());
    			}
        		
        		}
    		
    	}
    	public void onNothingSelected(AdapterView<?> parent) {
    	}
		
		
    });
    
   
    
	}
  
	public void sales(int lastday,int newday){
		
		 Calendar aCalendar = Calendar.getInstance();
		    
		    int today = aCalendar.get(Calendar.DAY_OF_MONTH);
		    // �������� ������� �����
		    aCalendar.add(Calendar.MONTH,0);
		    
		    int MaxDayInMonth =  aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		    int workdaysInMonth = getWorkDays(MaxDayInMonth);
		    int workdaysLast = getWorkDays(today)-1 + lastday+newday;
		    int workdaysLeft = workdaysInMonth-workdaysLast;
		    Log.i("LOG_TAG", "����� ������� ����= "+ workdaysInMonth+"������ ������� ����= "+workdaysLast+"�������� ������� ����= "+workdaysLeft);
		    /*******************************************************************************************************/
		    
		    
		    TextView WorkDays = (TextView) findViewById(R.id.TextView02);
		    WorkDays.setText(String.valueOf(workdaysInMonth));
		    
		    TextView WorkDaysLast = (TextView) findViewById(R.id.textView1);
		    WorkDaysLast.setText(String.valueOf(workdaysLast));
		    
		    TextView WorkDaysLeft = (TextView) findViewById(R.id.TextView01);
		    WorkDaysLeft.setText(String.valueOf(workdaysLeft));
		    /*����������� ����� ���� ������������*/
		    Boolean planuse = sp.getBoolean("planuse", false);
		    if (planuse ==true){
		    	String salespl = sp.getString("kegplan", "");
		    	 if (salespl.isEmpty()== true){
		    		 salesplan ="1";
		    	 }
		    	 else {salesplan = sp.getString("kegplan", "");}
		    }
		    TextView Plan = (TextView) findViewById(R.id.TextView14);
		    Plan.setText(salesplan);
		
		 TextView Fact = (TextView) findViewById(R.id.TextView15);
		    Fact.setText(String.valueOf(newsalesfact));
		    if (orders ==null){
		 		orders ="0.0";
		 	}
		 TextView Orders = (TextView) findViewById(R.id.TextView05);
		    Orders.setText(orders);
		    
		 TextView Ordersthisday = (TextView) findViewById(R.id.TextView23);
		 	if (ordersthisday ==null){
		 		ordersthisday ="0";
		 	}
		 	
		 	float ordershthis = Float.parseFloat(ordersthisday)- Float.parseFloat(orders);
		 	if ( ordershthis < 0){
		 		ordershthis =0;
		 	};
		    Ordersthisday.setText(String.valueOf(ordershthis));
		    
		    float salef = newsalesfact;
		    float salep = Float.parseFloat(salesplan);
		    double implement = BigDecimal.valueOf((salef/salep)*100).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();		    
		    TextView Implement = (TextView) findViewById(R.id.TextView16);
		    Implement.setText(String.valueOf(implement)+"%");
		    
		    float prognozk =Math.round((salef*workdaysInMonth/workdaysLast));
		    TextView Prognozk = (TextView) findViewById(R.id.TextView17);
		    if (prognozk < salep*0.8){
				Prognozk.setTextColor(getResources().getColor(R.color.carlred));
			}
			else if(prognozk < salep*0.9){
				Prognozk.setTextColor(getResources().getColor(R.color.carlorange));
			}
			else if(prognozk >= salep*0.9){
				Prognozk.setTextColor(getResources().getColor(R.color.carlgreen));
			}
		    Prognozk.setText(String.valueOf(prognozk));
		    
		   double prognozp =BigDecimal.valueOf((prognozk/salep)*100).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
		    TextView Prognozp = (TextView) findViewById(R.id.TextView19);
		    	if (prognozp < 80){
		    		Prognozp.setTextColor(getResources().getColor(R.color.carlred));
		    	}
		    	else if(prognozp < 90){
		    		Prognozp.setTextColor(getResources().getColor(R.color.carlorange));
		    	}
		    	else if(prognozp >= 90){
		    		Prognozp.setTextColor(getResources().getColor(R.color.carlgreen));
		    	}
		    Prognozp.setText(String.valueOf(prognozp)+"%");
		    
		   float avg =Math.round(salep/workdaysInMonth);
		    TextView Avg = (TextView) findViewById(R.id.TextView20);
		    Avg.setText(String.valueOf(avg));
		    
		    float avglast =Math.round((salep-salef)/workdaysLeft);
		    TextView Avglast = (TextView) findViewById(R.id.TextView21);
		    if (avglast > avg){
		    	Avglast.setTextColor(getResources().getColor(R.color.carlred));
		    }
		    else{
		    	Avglast.setTextColor(getResources().getColor(R.color.carlgreen));
		    }
		    Avglast.setText(String.valueOf(avglast));
		    
		    float trend =Math.round(salef-avg*workdaysLast);
		    TextView Trend = (TextView) findViewById(R.id.TextView22);
		    if (trend < 0){
		    	Trend.setTextColor(getResources().getColor(R.color.carlred));
		    }
		    else{		    
		    	Trend.setTextColor(getResources().getColor(R.color.carlgreen));
		    	
		    }
		    
		    Trend.setText(String.valueOf(trend));
		
	}
    
public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem mi = menu.add(0, 1, 0, R.string.action_settings );									
		mi.setIntent(new Intent(this, PrefActivity.class));
		
		MenuItem mie = menu.add(0, 1, 0, R.string.about );									
		mie.setIntent(new Intent(this, Info.class));
			
		//getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);		
		    
	}
	
	public int lastday(){
		
		 Calendar bCalendar = Calendar.getInstance();
		    bCalendar.add(Calendar.MONTH,0);
		    // �������� ������ ����� ������         
		    bCalendar.set(Calendar.HOUR_OF_DAY, 00);
		    bCalendar.set(Calendar.MINUTE, 00);
		    bCalendar.set(Calendar.MILLISECOND, 00);
		    bCalendar.set(Calendar.SECOND,00);
		    int day = bCalendar.get(Calendar.DAY_OF_MONTH);
		    bCalendar.set(Calendar.DATE, day-1);
		    int lastday;
		    if ( bCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
		    	lastday =0;
		    	 Log.i("LOG_TAG", "����� 1= "+ lastday);
		    }
		    else if(bCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
		    	lastday =0;
		    	Log.i("LOG_TAG", "����� 2= "+ lastday);
		    } else{lastday =1;
		    Log.i("LOG_TAG", "����� 3= "+ lastday);}		    		    
		    return lastday;
		
	}
	
	public int newday(){
		
		 Calendar bCalendar = Calendar.getInstance();
		    bCalendar.add(Calendar.MONTH,0);
		    // �������� ������ ����� ������         
		    bCalendar.set(Calendar.HOUR_OF_DAY, 00);
		    bCalendar.set(Calendar.MINUTE, 00);
		    bCalendar.set(Calendar.MILLISECOND, 00);
		    bCalendar.set(Calendar.SECOND,00);
		    int day = bCalendar.get(Calendar.DAY_OF_MONTH);
		    bCalendar.set(Calendar.DATE, day-1);
		    int newday;		    
		    
		    if ( bCalendar.get(Calendar.DAY_OF_WEEK)+1 == Calendar.SATURDAY){
		    	newday =0;
		    	 Log.i("LOG_TAG", "������� 1= "+ newday);
		    }
		    else if(bCalendar.get(Calendar.DAY_OF_WEEK)+1 == Calendar.SUNDAY){
		    	newday =0;
		    	Log.i("LOG_TAG", "������� 2= "+ newday);
		    } else {newday =1;
		    Log.i("LOG_TAG", "������� 3= "+ newday);}
		    
		    return newday;		
	}

	
	public static final int getWorkDays(int Days){
		int Workdays =0;
		int saturday=0;
		int sunday=0;
	    Calendar aCalendar = Calendar.getInstance();	    
	    // �������� ������ ����� ������         
	    aCalendar.set(Calendar.HOUR_OF_DAY, 00);
	    aCalendar.set(Calendar.MINUTE, 00);
	    aCalendar.set(Calendar.MILLISECOND, 00);
	    aCalendar.set(Calendar.SECOND,00);
		for(int i = 1 ; i < Days ; i++)
		 {   			 
			 aCalendar.set(Calendar.DAY_OF_MONTH, i);
			 if (aCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ) 
	    			saturday++; 
			 
			 aCalendar.set(Calendar.DAY_OF_MONTH, i);
	    		if (aCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ) 
	    			sunday++;	  			 
	    }
		Log.i("LOG_TAG", "�����������= "+ saturday);
	    Log.i("LOG_TAG", "�������= "+ sunday);
	    
	    Workdays = Days - (saturday + sunday);	   
	    
	    Log.i("LOG_TAG", "������� ���= "+ Workdays);    
	    
	    return Workdays;
		
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
