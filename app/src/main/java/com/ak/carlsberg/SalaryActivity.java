package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SalaryActivity extends AppCompatActivity {

	public static String DB_PATH,crmcode,response;
	private static final String DB_NAME = "monolit.db";
	private static final String DB_Carlsberg = "carlsberg.db";
	final String DIR_SD_CARLSBERG = "Carlsberg/Database";
    final String lei =" лей";
    double sumsalary =0.00;
	SharedPreferences sp;
	SQLiteDatabase db;
    ArrayList<HashMap<String, Object>> despatchList,despatchList1,despatchList2;
    HashMap<String, Object> hm,hm1,hm2;
    ListView lv;
    Button button106;
    String firstday,thisday;
    SimpleAdapter adapterList;
    int mhlKoff;
	/** Called when the activity is first created. */

	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salary);
        MobileAds.initialize(this, "ca-app-pub-5841832298128104~5926147671");
        despatchList = new ArrayList<HashMap<String, Object>>();
        despatchList1 = new ArrayList<HashMap<String, Object>>();
        despatchList2 = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm1,hm2;

        NativeExpressAdView adView = (NativeExpressAdView)findViewById(R.id.adView);


        AdRequest request = new AdRequest.Builder()
                .build();
        adView.loadAd(request);

		int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
		Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
		if (actionBarTitleView != null) {
			actionBarTitleView.setTypeface(fonts);
			actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		}
		sp = PreferenceManager.getDefaultSharedPreferences(this);

		Calendar bCalendar = Calendar.getInstance();
		bCalendar.add(Calendar.MONTH, 0);

		bCalendar.set(Calendar.HOUR_OF_DAY, 00);
		bCalendar.set(Calendar.MINUTE, 00);
		bCalendar.set(Calendar.MILLISECOND, 00);
		bCalendar.set(Calendar.SECOND, 00);
		int day = bCalendar.get(Calendar.DAY_OF_MONTH);
		bCalendar.set(Calendar.DATE, day);

		thisday = getFullTime(bCalendar.getTimeInMillis());
		bCalendar.set(Calendar.DATE, 1);
		firstday = getFullTime(bCalendar.getTimeInMillis());
		bCalendar.set(Calendar.DATE, bCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        int max = bCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		String todaydate = getFullTime(bCalendar.getTimeInMillis());
        Log.i("LOG_TAG", "Даты: сегодняшний день " + thisday + "  первый день месяца " + firstday);
        /***********************************кнопка на общий экран***************************/
        Button button01 =(Button) findViewById(R.id.point);
        OnClickListener butt01 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalaryActivity.this, RouteActivity.class);
                startActivity(intent);
            }
        };
        button01.setOnClickListener(butt01);
        button106 = (Button) findViewById(R.id.button106);
        OnClickListener obmen = new OnClickListener() {
            @Override
            public void onClick(View v) {
		      /*��������� � �������� Prezentations*/
                Intent intent = new Intent(SalaryActivity.this, SinhrActivity.class);
                startActivity(intent);

            }
        };

        // �������� ���������� ������ OK (btnOk)
        button106.setOnClickListener(obmen);
        deleteOldRows();


        mhlKoff = seasonKoffMhl();
        Log.i("LOG_TAG", "Коэффициент mhl " + mhlKoff);
		/*******************************считаем визиты*********************************/
		File sdPath = Environment.getExternalStorageDirectory();
		DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
		ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SalaryActivity.this, DB_NAME);
		db = extdbc.openDataBase();
		db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
		try {
			db.beginTransaction();
            /*******заполняем таблицу план****/
            db.execSQL("DELETE FROM calendar");
        for (int i=1;i<=max;i++){
            bCalendar.set(Calendar.DATE, i);
            db.execSQL("REPLACE INTO calendar(id,date) VALUES('"+i+"',strftime('%w','"+getFullTime(bCalendar.getTimeInMillis())+"' ))");
        }

            db.execSQL("INSERT INTO VisitsPlan (ClientAddressId,PlanDate,Latitude,Longitude) " +
                    " SELECT i.ClientAddressId, i.PlanDate, i.Latitude,i.Longitude FROM (SELECT  CRMJobSchedule.ClientAddressId, CRMJobSchedule.PlanDate, Latitude, Longitude " +
                    " FROM CRMJobSchedule,ClientAddressGeoLocation " +
                    " WHERE " +
                    " CRMJobSchedule.ClientAddressId = ClientAddressGeoLocation.ClientAddressId " +
                    " AND CRMJobSchedule.PlanDate >='" + firstday + "' AND CRMJobSchedule.PlanDate < date('" + thisday + "' ,'+1 day') ) i " +
                    " WHERE " +
                    " NOT EXISTS ( " +
                    "   SELECT t.ClientAddressId,t.PlanDate,t.Latitude,t.Longitude " +
                    "   FROM VisitsPlan t " +
                    "   WHERE t.ClientAddressId = i.ClientAddressId AND t.PlanDate = i.PlanDate AND t.Latitude = i.Latitude AND t.Longitude=i.Longitude )");
            /*************************************заполняем таблицу выполнение*********************************************/
            db.execSQL("INSERT INTO VisitsComplete (ClientAddressId,PlanDate,Latitude,Longitude) " +
                    " SELECT i.ClientAddressId, i.PlanDate, i.Latitude,i.Longitude FROM (SELECT  ClientAddressId, PlanDate, Latitude, Longitude  " +
                    " FROM CRMJobScheduleCompleteLocation " +
                    " WHERE " +
                    " PlanDate >='" + firstday + "' AND PlanDate < date('" + thisday + "' ,'+1 day') ) i " +
                    " WHERE " +
                    " NOT EXISTS ( " +
                    "   SELECT t.ClientAddressId,t.PlanDate,t.Latitude,t.Longitude " +
                    "   FROM VisitsComplete t " +
                    "   WHERE t.ClientAddressId = i.ClientAddressId AND t.PlanDate = i.PlanDate AND t.Latitude = i.Latitude AND t.Longitude=i.Longitude)");

           String visitsFact =" SELECT t.ClientAddressId, t.PlanDate, CASE WHEN t.Latitude IS NULL THEN 0 ELSE t.Latitude END, CASE WHEN t.Longitude IS NULL THEN 0 ELSE t.Longitude END ,t1.LatitudeComplete,t1.LongitudeComplete FROM " +
                    " (SELECT ClientAddressId, PlanDate,  Latitude, Longitude FROM VisitsPlan ) t " +
                    " LEFT JOIN  " +
                    "(SELECT ClientAddressId,Latitude AS LatitudeComplete, Longitude AS LongitudeComplete , PlanDate FROM VisitsComplete) t1 " +
                    " ON t.ClientAddressId =t1.ClientAddressId AND t.PlanDate =t1.PlanDate AND t.PlanDate >= '" + firstday + "' AND t.PlanDate < date('" + thisday + "' ,'+1 day') ";
			Cursor cursor = db.rawQuery(visitsFact, null);

            while (cursor.moveToNext()) {
                String clientaddressid = cursor.getString(0);
                String plandate = cursor.getString(1);
                Location locationA = new Location("point A");
                locationA.setLatitude(Double.valueOf(cursor.getString(2)));
                locationA.setLongitude(Double.valueOf(cursor.getString(3)));
                Location locationB = new Location("point B");
                String latc,lonc,confirmation="0",distance="0";
                if (cursor.getString(4)==null||cursor.getString(5)==null){latc="0.00";lonc="0.00";}
                 else {
                    latc = cursor.getString(4);
                    lonc = cursor.getString(5);
                    locationB.setLatitude(Double.valueOf(latc));
                    locationB.setLongitude(Double.valueOf(lonc));
                    float currentDistance = locationA.distanceTo(locationB);
                    distance = String.valueOf(currentDistance);
                    if (currentDistance < 100) {
                        confirmation = "1";
                    } else {
                        confirmation = "0";
                    }
                }
                String check =" SELECT ClientAddressId,PlanDate,Latitude,Longitude,LatitudeComplete,LongitudeComplete,Distance,Confirmation FROM VisitsSalary" +
                        " WHERE ClientAddressId = '"+clientaddressid+"'AND PlanDate = '"+plandate+"'AND Latitude ='"+cursor.getString(2)+"'AND Longitude ='"+cursor.getString(3)+"'AND LatitudeComplete ='"+latc+"'AND LongitudeComplete='"+lonc+"'AND Distance='"+distance+"'AND Confirmation='"+confirmation+"' ";
                Cursor cursor1 = db.rawQuery(check, null);
                if (cursor1.getCount()==0) {
                    db.execSQL("REPLACE INTO VisitsSalary (id,ClientAddressId,PlanDate,Latitude,Longitude,LatitudeComplete,LongitudeComplete,Distance,Confirmation) " +
                            " VALUES ('" +clientaddressid+plandate+ "','" + clientaddressid + "','" + plandate + "','" + cursor.getString(2) + "','" + cursor.getString(3) + "','" + latc + "','" + lonc + "','" + distance + "','" + confirmation + "') ");
                    db.execSQL("UPDATE VisitsSalary SET Equipment = '50' WHERE  ClientAddressId =( SELECT ClientAddressId FROM ClientAddressEquip, Equipment WHERE WareId = equipment AND ClientAddressId = '"+clientaddressid+"') AND PlanDate = '"+plandate+"'AND Latitude ='"+cursor.getString(2)+"'AND Longitude ='"+cursor.getString(3)+"'AND LatitudeComplete ='"+latc+"'AND LongitudeComplete='"+lonc+"'AND Distance='"+distance+"'AND Confirmation='"+confirmation+"'");
                }
                else
                {

                }
                cursor1.close();
            }

			Log.i("LOG_TAG", "кол-во записей курсор " + cursor.getCount());
			cursor.close();

            /****************************выводим точки на экран*********************/

            String XoCount ="SELECT COUNT(ClientAddressId) FROM " +
                " (SELECT ClientAddressId,Equipment FROM VisitsSalary WHERE Equipment='25' GROUP BY ClientAddressId,Equipment ) " +
                " UNION ALL " +
                " SELECT COUNT(ClientAddressId) FROM " +
                " (SELECT ClientAddressId,Equipment FROM VisitsSalary WHERE Equipment='50' GROUP BY ClientAddressId,Equipment )";
            Cursor cursor2 = db.rawQuery(XoCount, null);
            String St[] = new String[2];
            int i=0;
            while (cursor2.moveToNext()) {
               St[i] = cursor2.getString(0);
                i++;
            }
            TextView NetXo = (TextView) findViewById(R.id.textView432);
            NetXo.setText(St[0] + " тт");
            TextView EstiXo = (TextView) findViewById(R.id.textView435);
            EstiXo.setText(St[1]+" тт");
            cursor2.close();
            /**************************выводим деньги по визитам на экран****************/
            String visitSalary =" SELECT SUM(SUMMA)*1.00 FROM ( SELECT t.ClientAddressId,FULLPLAN ,PLAN,FACT, CONF, t.Equipment, "+
                    " CASE WHEN FULLPLAN <4 THEN 5*CONF*1.00 ELSE t.Equipment*1.00/FULLPLAN*CONF*1.00 END AS SUMMA "+
                    "  FROM ( (SELECT ClientAddressId,COUNT(ClientAddressId) AS FULLPLAN FROM VisitsSalary " +
                    "  GROUP BY ClientAddressId) i " +
                    " LEFT JOIN (SELECT ClientAddressId,COUNT(ClientAddressId) AS PLAN,Equipment FROM VisitsSalary "+
                    " GROUP BY ClientAddressId,Equipment) t "+
                    " ON i.ClientAddressId = t.ClientAddressId "+
                    " LEFT JOIN (SELECT ClientAddressId,COUNT(LatitudeComplete)  AS FACT,Equipment  FROM VisitsSalary "+
                    " WHERE LatitudeComplete >'0.00' "+
                    " GROUP BY ClientAddressId,Equipment) t1 "+
                    " ON t.ClientAddressId =t1.ClientAddressId AND t.Equipment = t1.Equipment "+
                    " LEFT JOIN (SELECT ClientAddressId,COUNT(Confirmation) AS CONF,Equipment  FROM VisitsSalary "+
                    " WHERE Confirmation>0 GROUP BY ClientAddressId,Equipment) t2 "+
                    " ON t.ClientAddressId =t2.ClientAddressId AND t.Equipment = t2.Equipment) ) WHERE Equipment ='25' "+
            " UNION ALL "+
                            "  SELECT SUM(SUMMA)*1.00 FROM ( SELECT t.ClientAddressId,FULLPLAN ,PLAN,FACT, CONF, t.Equipment, " +
                    " CASE WHEN FULLPLAN <4 THEN 12.50*CONF*1.00 ELSE t.Equipment*1.00/FULLPLAN*CONF*1.00 END AS SUMMA "+
                    "  FROM ( (SELECT ClientAddressId,COUNT(ClientAddressId) AS FULLPLAN FROM VisitsSalary " +
                    " GROUP BY ClientAddressId) i "+
                    "  LEFT JOIN (SELECT ClientAddressId,COUNT(ClientAddressId) AS PLAN,Equipment FROM VisitsSalary "+
                    " GROUP BY ClientAddressId,Equipment) t "+
                    " ON i.ClientAddressId = t.ClientAddressId "+
                    " LEFT JOIN (SELECT ClientAddressId,COUNT(LatitudeComplete)  AS FACT,Equipment  FROM VisitsSalary "+
                    " WHERE LatitudeComplete >'0.00' GROUP BY ClientAddressId,Equipment) t1"+
                    " ON t.ClientAddressId =t1.ClientAddressId AND t.Equipment = t1.Equipment "+
                    " LEFT JOIN (SELECT ClientAddressId,COUNT(Confirmation) AS CONF,Equipment  FROM VisitsSalary "+
                    " WHERE Confirmation>0 GROUP BY ClientAddressId,Equipment) t2 "+
                    " ON t.ClientAddressId =t2.ClientAddressId AND t.Equipment = t2.Equipment) ) WHERE Equipment ='50' ";
            Cursor cursor3 = db.rawQuery(visitSalary, null);
           i=0;
            while (cursor3.moveToNext()) {
                if (cursor3.getString(0)==null) {St[i]="0";}
                else {
                    St[i] = cursor3.getString(0);
                }
                i++;
            }
            TextView NetXoS = (TextView) findViewById(R.id.textView433);
            NetXoS.setText(St[0] + lei);
            TextView EstiXoS = (TextView) findViewById(R.id.textView436);
            EstiXoS.setText(St[1] + lei);
            cursor3.close();
            float co = Float.valueOf(St[0]) + Float.valueOf(St[1]);
            if (co <2400) {
                sumsalary = 2400;
                TextView minim = (TextView) findViewById(R.id.textView442);
                minim.setText("Минимум");
                TextView minims = (TextView) findViewById(R.id.textView441);
                minims.setText("2400"+ lei);
            }
            else {
                sumsalary = Float.valueOf(St[0]) + Float.valueOf(St[1]);
                TextView minim = (TextView) findViewById(R.id.textView442);
                minim.setText("Сумма");
                sumsalary = new BigDecimal(sumsalary).setScale(2, RoundingMode.UP).doubleValue();
                TextView minims = (TextView) findViewById(R.id.textView441);
                minims.setText(sumsalary+ lei);
            }

/*******************************************Остатки и рек заказ****************************************************/
            String RekStock ="SELECT ClientAddressId, PlanDate, Confirmation FROM VisitsSalary WHERE PlanDate >= date('" + thisday + "','-3 day') AND PlanDate < date('" + thisday + "','+1 day') ";
            Cursor CursorRekStock = db.rawQuery(RekStock,null);
            while (CursorRekStock.moveToNext()) {
                 //db.execSQL("REPLACE INTO");
                String crm = CursorRekStock.getString(0);
                String planDate = CursorRekStock.getString(1);
                String conf = CursorRekStock.getString(2);
                String rek ="SELECT " +
                        " COUNT(Ostatkitek) AS Plan,SUM( CASE WHEN Ostatkitek >= 0 THEN '1' ELSE '0' END) AS Fact, " +
                        "  CASE WHEN SUM(Rekzakaz) IS NULL THEN '0.00' ELSE SUM(Rekzakaz) END AS Rekzakaz,SUM(Zakaztek) AS Zakaztek " +
                        " FROM ( " +
                        " SELECT UnionGrup,WareId,SUM(Ostatkipr) AS Ostatkipr,SUM(Otgruzpr) AS Otgruzpr,SUM(Ostatkitek) AS Ostatkitek,dniprodaj,sledVizitdnei, " +
                        " CASE  " +
                        " WHEN ROUND((ROUND(sledVizitdnei*((SUM(Ostatkipr)+SUM(Otgruzpr)-SUM(Ostatkitek))*1.00/dniprodaj) -SUM(Ostatkitek))/FactorValue),2) <0 THEN '0.00'  " +
                        " ELSE ROUND((ROUND(sledVizitdnei*((SUM(Ostatkipr)+SUM(Otgruzpr)-SUM(Ostatkitek))*1.00/dniprodaj) -SUM(Ostatkitek))/FactorValue),2) " +
                        " END AS Rekzakaz,ROUND((Zakaztek*1.00/FactorValue),2) AS Zakaztek FROM ( " +
                        " SELECT t.WareId, CASE WHEN Ostatkipr IS NULL THEN '0' ELSE Ostatkipr END AS Ostatkipr,CASE WHEN SUM(Otgruzpr) IS NULL THEN '0' ELSE SUM(Otgruzpr) END AS Otgruzpr,CASE WHEN Ostatkitek IS NULL THEN '-1' ELSE Ostatkitek END AS Ostatkitek,CASE WHEN SUM(Zakaztek) IS NULL THEN '0' ELSE SUM(Zakaztek) END AS Zakaztek,UnionGrup,FactorValue FROM (( " +
                        " SELECT WareId FROM WorkReportInfo  " +
                        " WHERE FormNumber IN ('21','74') AND ClientAddressId ='"+crm+"'  AND PlanDate ='"+planDate+"' AND WareId NOT LIKE '%CRMT%' AND WareId NOT LIKE '%CRMP%' AND WareId NOT LIKE '%CRMP%AS' AND WareId NOT LIKE '%CRM%AS' AND WareId NOT LIKE '%CRM%A%' AND WareId NOT LIKE '%CRM%А' " +
                        " UNION  " +
                        " SELECT WareId FROM DespatchLine WHERE ClientAddressId ='"+crm+"' AND  WareId NOT LIKE '%CRMP%' AND WareId NOT LIKE '%CRMP%AS' AND WareId NOT LIKE '%CRM%AS' AND WareId NOT LIKE '%CRM%A%' AND WareId NOT LIKE '%CRM%А' " +
                        " UNION" +
                        " SELECT WareId FROM PPCOrderLine,PPCOrder WHERE PPCOrderLine.OrderNumber = PPCOrder.OrderNumber AND ClientAddressId = '"+crm+"'  AND date(ActionDate) < date('"+planDate+"','+1 day') AND WareId NOT LIKE '%CRMT%' AND WareId NOT LIKE '%CRMP%' AND WareId NOT LIKE '%CRMP%AS' AND WareId NOT LIKE '%CRM%AS' AND WareId NOT LIKE '%CRM%A%' AND WareId NOT LIKE '%CRM%А' "+
                        " ) t " +
                        " LEFT JOIN " +
                        " (SELECT WareId,CASE WHEN InfoValue IS NULL THEN '0' ELSE InfoValue END AS Ostatkipr FROM WorkReportInfo  " +
                        " WHERE FormNumber IN ('21','74') AND ClientAddressId ='"+crm+"'  AND PlanDate =(SELECT PlanDate FROM CRMJobSchedule WHERE ClientAddressId ='"+crm+"'  AND PlanDate < date('"+planDate+"') ORDER BY  PlanDate DESC LIMIT 1 ) " +
                        " ) t1  " +
                        " ON t.WareId =t1.WareId " +
                        " LEFT JOIN " +
                        " (SELECT WareId,Quantity AS Otgruzpr FROM DespatchLine WHERE ClientAddressId ='"+crm+"'  AND date(CustDate) = date((SELECT PlanDate FROM CRMJobSchedule WHERE ClientAddressId ='"+crm+"'  AND PlanDate < date('"+planDate+"') ORDER BY  PlanDate DESC LIMIT 1 ),'+1 day')) t2 " +
                        " ON t.WareId =t2.WareId " +
                        " LEFT JOIN " +
                        " (SELECT WareId,CASE WHEN InfoValue IS NULL THEN '-1' ELSE InfoValue END AS Ostatkitek FROM WorkReportInfo  " +
                        " WHERE FormNumber IN ('21','74') AND ClientAddressId ='"+crm+"'  AND PlanDate =(SELECT PlanDate FROM CRMJobSchedule WHERE ClientAddressId ='"+crm+"'  AND PlanDate = '"+planDate+"' ORDER BY  PlanDate DESC LIMIT 1 ) " +
                        " ) t3 " +
                        " ON t.WareId =t3.WareId " +
                        " LEFT JOIN " +
                        " (SELECT WareId,Quantity AS Zakaztek FROM PPCOrderLine,PPCOrder WHERE PPCOrderLine.OrderNumber = PPCOrder.OrderNumber AND ClientAddressId = '"+crm+"'  AND date(ActionDate) = date('"+planDate+"','+1 day')) t4 " +
                        " ON t.WareId =t4.WareId " +
                        " LEFT JOIN " +
                        " (SELECT DISTINCT UnionGrup,FactorValue,WareId FROM Unit,Mhl WHERE WareId = Unit.WareId AND UnitId ='dal' AND WareId = SkuCode ) t5 " +
                        " ON t.WareId =t5.WareId " +
                        " ) GROUP BY t.WareId) " +
                        " LEFT JOIN " +
                        " (SELECT strftime('%d',PlanDate)-strftime('%d','"+planDate+"')+2 AS sledVizitdnei FROM ( " +
                        " SELECT  PlanDate FROM CRMJobSchedule WHERE ClientAddressId ='"+crm+"'  AND PlanDate > date('"+planDate+"','+1 day') ORDER BY  PlanDate ASC LIMIT 1)) " +
                        " LEFT JOIN " +
                        " (SELECT  " +
                        " CASE  " +
                        " WHEN strftime('%d','"+planDate+"','+1 day')-strftime('%d',PlanDate) <0  " +
                        " THEN (strftime('%d',date('"+planDate+"','start of month','+0 month','-1 day'))-strftime('%d',PlanDate))+(strftime('%d','"+planDate+"','+1 day') -strftime('%d',date('"+planDate+"','start of month','+0 month','+0 day'))) " +
                        " ELSE strftime('%d','"+planDate+"')-strftime('%d',PlanDate) END " +
                        " AS dniprodaj FROM ( " +
                        " SELECT  PlanDate FROM CRMJobSchedule WHERE ClientAddressId ='"+crm+"'  AND PlanDate < date('"+planDate+"','-1 day') ORDER BY  PlanDate DESC LIMIT 1) " +
                        " ) GROUP BY UnionGrup) ; ";

                Cursor rekCur = db.rawQuery(rek,null);
                    while (rekCur.moveToNext()) {
                        Log.i("LOG_TAG", " crm:  " + crm + " План скю:  " + rekCur.getString(0) + " Факт скю: " + rekCur.getString(1) + " Рек дал: " + rekCur.getString(2) + " Факт дал: " + rekCur.getString(3));
                        db.execSQL("REPLACE INTO RekStock (id,PlanDate,ClientAddresId,Stockplan,Stockfact,Rekplan,Rekfact,Confirmation) " +
                                " VALUES ('"+planDate+crm+"','"+planDate+"','"+crm+"','"+rekCur.getString(0)+"','"+rekCur.getString(1)+"','"+rekCur.getString(2)+"','"+rekCur.getString(3)+"','"+conf+"')");
                    }
                rekCur.close();


                /************************************заносим остатки в базу по каждому коду*************************************************/
               String Stock =" SELECT UnionGrup,t.WareId,WareName,CASE WHEN SUM(Ostatkitek) <0 THEN 'Не внесено' ELSE SUM(Ostatkitek) END AS Ostatkitek FROM ( " +
                        " SELECT t.WareId, CASE WHEN Ostatkitek IS NULL THEN '-1' ELSE Ostatkitek END AS Ostatkitek,UnionGrup FROM (( " +
                        " SELECT WareId FROM WorkReportInfo  " +
                        " WHERE FormNumber IN ('21','74') AND ClientAddressId ='"+crm+"' AND PlanDate ='"+planDate+"'  AND WareId NOT LIKE '%CRMT%' AND WareId NOT LIKE '%CRMP%' AND WareId NOT LIKE '%CRMP%AS' AND WareId NOT LIKE '%CRM%AS' AND WareId NOT LIKE '%CRM%A%' AND WareId NOT LIKE '%CRM%А' " +
                        " UNION  " +
                        " SELECT WareId FROM DespatchLine WHERE ClientAddressId ='"+crm+"'  AND WareId NOT LIKE '%CRMT%' AND WareId NOT LIKE '%CRMP%' AND WareId NOT LIKE '%CRMP%AS' AND WareId NOT LIKE '%CRM%AS' AND WareId NOT LIKE '%CRM%A%' AND WareId NOT LIKE '%CRM%А' " +
                        " UNION" +
                        " SELECT WareId FROM PPCOrderLine,PPCOrder WHERE PPCOrderLine.OrderNumber = PPCOrder.OrderNumber AND ClientAddressId = '"+crm+"'  AND date(ActionDate) < date('"+planDate+"','+1 day')  AND WareId NOT LIKE '%CRMT%' AND WareId NOT LIKE '%CRMP%' AND WareId NOT LIKE '%CRMP%AS' AND WareId NOT LIKE '%CRM%AS' AND WareId NOT LIKE '%CRM%A%' AND WareId NOT LIKE '%CRM%А' "+
                        " ) t "+
                        " LEFT JOIN " +
                        " (SELECT WareId,CASE WHEN InfoValue IS NULL THEN '-1' ELSE InfoValue END AS Ostatkitek FROM WorkReportInfo  " +
                        " WHERE FormNumber IN ('21','74') AND ClientAddressId ='"+crm+"'   AND PlanDate =(SELECT PlanDate FROM CRMJobSchedule WHERE ClientAddressId ='"+crm+"'   AND PlanDate = '"+planDate+"' ORDER BY  PlanDate DESC LIMIT 1 ) " +
                        " ) t3 " +
                        " ON t.WareId =t3.WareId " +
                        " LEFT JOIN " +
                        " (SELECT DISTINCT UnionGrup,FactorValue,WareId FROM Unit,Mhl WHERE WareId = Unit.WareId AND UnitId ='dal' AND WareId = SkuCode ) t5 " +
                        " ON t.WareId =t5.WareId " +
                        " )  GROUP BY t.WareId) t,Ware  WHERE Ware.WareId =t.WareId " +
                        " GROUP BY UnionGrup ";
                Cursor StockCur = db.rawQuery(Stock,null);
                while (StockCur.moveToNext()) {
                    db.execSQL("REPLACE INTO Stock (id,PlanDate,ClientAddresId,WareId,WareName,Fact) " +
                            " VALUES ('"+planDate+crm+StockCur.getString(1)+"','"+planDate+"','"+crm+"','"+StockCur.getString(1)+"','"+StockCur.getString(2)+"','"+StockCur.getString(3)+"')");
                }
                StockCur.close();
            }
            CursorRekStock.close();


            String ost ="SELECT SUM(Bablo) FROM ( " +
                    "SELECT (10*1.00/COUNT(Stockplan))*SUM(CASE WHEN StockPlan=Stockfact AND Confirmation='1' THEN '1' ELSE 0 END) AS Bablo FROM RekStock GROUP BY ClientAddresId );";
            Cursor ostCur = db.rawQuery(ost,null);
            while (ostCur.moveToNext()) {
                TextView os = (TextView) findViewById(R.id.textView386);
                double ostat = Double.parseDouble(ostCur.getString(0));
                ostat = new BigDecimal(ostat).setScale(2, RoundingMode.UP).doubleValue();
                os.setText(ostat + lei);
                sumsalary = sumsalary + ostat;

            }

            /********************************считаем дебет***************************/
            /*****************************************вставляем новую запись***********************/
            db.execSQL("INSERT INTO DebetStatus (Date,TotalBalance,OverdueBalance,DebetPercent) SELECT '" + thisday + "',TotalBalance,OverdueBalance,ROUND ((1-OverdueBalance/TotalBalance*1.00)*100,2) AS DebtPercent FROM ( " +
                    "  (SELECT  SUM(CustInvoiceSumm) AS TotalBalance FROM CompanyInvoicesPayDocs) " +
                    " LEFT JOIN " +
                    " (SELECT  SUM(CustInvoiceSumm) AS OverdueBalance FROM CompanyInvoicesPayDocs WHERE OverduePeriod >0)) WHERE NOT EXISTS " +
                    "  (SELECT TotalBalance,OverdueBalance,DebetPercent FROM DebetStatus WHERE Date='" + thisday + "'); ");
            /*******************************обновляем запись (новую, не новую, похер... мало ли что) ***/
           String deb =" SELECT TotalBalance,OverdueBalance,ROUND ((1-OverdueBalance/TotalBalance*1.00)*100,2) AS DebtPercent FROM ( " +
                    " (SELECT  SUM(CustInvoiceSumm) AS TotalBalance FROM CompanyInvoicesPayDocs)" +
                    " LEFT JOIN " +
                    " (SELECT  SUM(CustInvoiceSumm) AS OverdueBalance FROM CompanyInvoicesPayDocs WHERE OverduePeriod >0))";
            Cursor cursorDeb = db.rawQuery(deb, null);
            String tba = null,oba = null,dpe = null;
            while (cursorDeb.moveToNext()) {
                if (cursorDeb.getString(0)==null||cursorDeb.getString(1)==null||cursorDeb.getString(2)==null) {tba ="0";oba="0";dpe="100";}
                else {
                    tba = cursorDeb.getString(0);
                    oba= cursorDeb.getString(1);
                    dpe= cursorDeb.getString(2);
                }
            }
            cursorDeb.close();
            db.execSQL("UPDATE DebetStatus SET TotalBalance ='"+tba+"',OverdueBalance ='"+oba+"',DebetPercent='"+dpe+"' WHERE Date='" + thisday + "' ");
        /***************************************************************************/


        String debet =" SELECT AVG(TotalBalance),AVG(OverdueBalance),ROUND (AVG(DebetPercent),2) FROM DebetStatus ";
            Cursor cursorDebet = db.rawQuery(debet, null);
            String tbalance = null,obalance = null,dpercent = null;
            float dperc=0;
            while (cursorDebet.moveToNext()) {
                if (cursorDebet.getString(0)==null||cursorDebet.getString(1)==null||cursorDebet.getString(2)==null) {tbalance ="0";obalance="0";dpercent="0";}
                else {
                    tbalance = cursorDebet.getString(0);
                    obalance= cursorDebet.getString(1);
                    dpercent= cursorDebet.getString(2);
                    dperc=Float.parseFloat(cursorDebet.getString(2));
                }
            }
            TextView tdeb = (TextView) findViewById(R.id.textView425);
            TextView odeb = (TextView) findViewById(R.id.textView426);
            TextView ddeb = (TextView) findViewById(R.id.textView427);
            TextView dp = (TextView) findViewById(R.id.textView437);
            tdeb.setText(tbalance);
            odeb.setText(obalance);
            ddeb.setText(dpercent+"%");
            int dpercint= (int) Math.floor((dperc));
            int debetsalary =0;
            switch (dpercint){
                case 80:debetsalary = 500;break;
                case 81:debetsalary = 550;break;
                case 82:debetsalary = 600;break;
                case 83:debetsalary = 650;break;
                case 84:debetsalary = 700;break;
                case 85:debetsalary = 750;break;
                case 86:debetsalary = 800;break;
                case 87:debetsalary = 850;break;
                case 88:debetsalary = 900;break;
                case 89:debetsalary = 950;break;
                case 90:debetsalary = 1000;break;
                default: debetsalary = 0;break;
            }
            if (dpercint>90){debetsalary =1000;}
            Log.i("LOG_TAG", "Процент по дебету  " +dpercint);
            Log.i("LOG_TAG", "Процент по дебету  " +debetsalary);
            sumsalary = sumsalary+debetsalary;
            dp.setText(String.valueOf(debetsalary)+lei);
            cursorDebet.close();
            /***********************кнопка дебет***********************************/
            String queryd = "SELECT Date,TotalBalance,OverdueBalance,DebetPercent FROM DebetStatus " ;
            Cursor cursordebu = db.rawQuery(queryd, null);


            while (cursordebu.moveToNext()) {
                hm = new HashMap<String, Object>();
                String da = cursordebu.getString(0).substring(8, cursordebu.getString(0).length()-9);
                hm.put("Дата",da);
                hm.put("ДЗ", cursordebu.getString(1));
                hm.put("ПДЗ", cursordebu.getString(2));
                hm.put("Процент", cursordebu.getString(3)+"%");

                despatchList.add(hm);
            }
            cursordebu.close();

            Button button99 = (Button) findViewById(R.id.button99);

            OnClickListener debButton = new OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SalaryActivity.this);
                    builder.setCancelable(true);
                    LinearLayout view = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.dialog_debet, null);
                    ListView lv = (ListView) view.findViewById(R.id.listView3);
                    adapterList = new SimpleAdapter(SalaryActivity.this, despatchList,
                            R.layout.list_item_dialog_debet, new String[] {"Дата","ДЗ","ПДЗ","Процент"},
                            new int[] {R.id.textView511,R.id.textView512,R.id.textView513,R.id.textView514});
                    lv.setAdapter(adapterList);
                    builder.setView(view);
                    builder.setNegativeButton("Назад",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            };
            button99.setOnClickListener(debButton);

        /*******************************считаем план*******************************/
            String Eplan ="SELECT TargetValue FROM TaskReportView WHERE TaskName ='Продажи в дал (пиво)' ";
            Cursor cursorPlan = db.rawQuery(Eplan, null);
            TextView Plan = (TextView) findViewById(R.id.textView370);
            float tplan =0;
            while (cursorPlan.moveToNext()) {
                tplan = Float.parseFloat(cursorPlan.getString(0));
                Log.i("LOG_TAG", "План  " +cursorPlan.getString(0));
            }
            Plan.setText(String.valueOf(tplan));
            cursorPlan.close();
            Log.i("LOG_TAG", "План  " + tplan);
            String Efact ="SELECT Despatchs -" +
                    "CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END AS Otgruz FROM (" +
                    " (SELECT CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Despatchs FROM Unit,DespatchLine,Despatch " +
                    " WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '" + firstday + "' AND DespatchLine.CustDate < date('" + thisday + "' ,'+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DocumentTypeId ='Despatch') " +
                    " LEFT JOIN " +
                    " (SELECT CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Returnss FROM Unit,DespatchLine,Despatch " +
                    " WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '" + firstday + "' AND DespatchLine.CustDate < date('" + thisday + "' ,'+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DocumentTypeId ='CustReturn' )) ";
            Cursor cursorFact = db.rawQuery(Efact, null);
            TextView Fact = (TextView) findViewById(R.id.textView387);
            float tfact = 0;
            while (cursorFact.moveToNext()) {
                 if (cursorFact.isNull(0) ) { tfact =0;}
                else {
                     tfact = Float.parseFloat(cursorFact.getString(0));
                 }
            }
            Fact.setText(String.valueOf(tfact));
            cursorFact.close();

            TextView Percent = (TextView) findViewById(R.id.textView371);
            double percent;
            if (tplan !=0.0) {
                percent = BigDecimal.valueOf((tfact / tplan) * 100).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                Percent.setText(String.valueOf(percent) + "%");
            }
            else {
                percent =0;
            }
            float execution=0,koff =0;
            int execPercent =(int) Math.floor(percent);
            if (execPercent <90.00 ) { execution =0; Log.i("LOG_TAG", "Деньги за план 0 " +execPercent);}
            else
            if (execPercent >110.00) { execution =2400; Log.i("LOG_TAG", "Деньги за план 2400 " +execPercent);}
            else {

                String EExec ="SELECT Kofficient FROM PlanCalc WHERE PercentFact ='"+execPercent+"'";
                Cursor cursorExec = db.rawQuery(EExec, null);
                while (cursorExec.moveToNext()) {
                    koff = Float.parseFloat(cursorExec.getString(0));
                }
                cursorExec.close();
                execution = koff*2000;
                Log.i("LOG_TAG", "Деньги за план  " +execPercent + "  " + koff);
            }
            sumsalary =sumsalary+execution;
            sumsalary = new BigDecimal(sumsalary).setScale(2, RoundingMode.UP).doubleValue();

            TextView Exec = (TextView) findViewById(R.id.textView440);
            Exec.setText(String.valueOf(execution)+lei);


            /**********************кнопка план *************************************/
         Button button100 = (Button) findViewById(R.id.button100);
            OnClickListener planButton = new OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SalaryActivity.this);
                    builder.setCancelable(true);
                    LinearLayout view = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.dialog_plan, null);
                    builder.setView(view);
                    builder.setNegativeButton("Назад",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            };
            button100.setOnClickListener(planButton);
/********************************************************************************************/
            String Segment=null;
        /****************************************считаем MHL ***********************************/
        String queryPoint ="SELECT t.ClientAddressId,FaxNumber,TradeTypeTreeId,CASE WHEN SUM (CASE WHEN WareId=equipment THEN '1.00' ELSE '0.00' END) >0 THEN '1.00' ELSE '0.00' END AS freezs FROM( " +
                " (SELECT ClientAddressId,FaxNumber,TradeTypeTreeId FROM ClientAddress) t " +
                " LEFT JOIN " +
                " (SELECT ClientAddressId,WareId FROM ClientAddressEquip ) t1 " +
                " ON t.ClientAddressId = t1.ClientAddressId )" +
                " LEFT JOIN " +
                " Equipment ON WareId =equipment AND Equipment.type='XO' GROUP BY t.ClientAddressId ";
            Cursor cursorPoint = db.rawQuery(queryPoint,null);
            String crmKode=null,territory=null,pointCategory=null,freezes=null;
            int Mhlsalary=0,MBsalary=0;
            while (cursorPoint.moveToNext()) {
                crmKode = cursorPoint.getString(0);
                territory = cursorPoint.getString(1);
                pointCategory = cursorPoint.getString(2);
                freezes = cursorPoint.getString(3);

                String mhlPointtype="SELECT  'off' FROM ClientAddress WHERE ClientAddressId='"+crmKode+"' AND TradeTypeTreeId IN ('TT11','TT12','TT21','TT31','TT32','TT33','TT41','TT51','TT61','TT71','TT81','MT91','MT101','MT111','MT112','MT113','MT114','MT121','MT122','MT123','MT124','MT131','MT132','MT133','MT134','MT141') "+
                        " UNION ALL" +
                        " SELECT  'on' FROM ClientAddress WHERE ClientAddressId='"+crmKode+"' AND TradeTypeTreeId NOT IN ('TT11','TT12','TT21','TT31','TT32','TT33','TT41','TT51','TT61','TT71','TT81','MT91','MT101','MT111','MT112','MT113','MT114','MT121','MT122','MT123','MT124','MT131','MT132','MT133','MT134','MT141')";
                Cursor cursorMhp = db.rawQuery(mhlPointtype,null);
                int kl=0;
                String Sm[]= new String[2];
                while (cursorMhp.moveToNext()){
                    Sm[kl] = cursorMhp.getString(0);
                    kl++;
                }
                cursorMhp.close();
                /*************************запрос для mhl***************************************/
                if (Sm[0]==null){Segment=Sm[1];}
                else {Segment=Sm[0];}
               if (Segment==null) {Segment="off";}

                 /*********************подставляем данные отдельной тт в расчет MHL***********/
                String queryMHL ="SELECT SkuName ,Otgruz,tresholds,Exec, 1 AS TypeSku FROM (SELECT UnionGrup, SkuName, SkuCode FROM Mhl WHERE "+pointCategory+" ='1' AND freeze='"+freezes+"' AND territory='"+territory+"' ) p " +
                        " LEFT JOIN " +
                        " (SELECT Clie,UnionGrup,Wared,SUM(Otgruz) AS Otgruz,tresholds,CASE WHEN SUM(CASE WHEN Execution >=0 THEN '1.00' ELSE '0.00' END) >=1 THEN '1.0' ELSE '0.0' END AS Exec FROM ( " +
                        " SELECT  Clie,UnionGrup,Wared,Despatchs - CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END AS Otgruz,tresholds/"+mhlKoff+" AS tresholds, " +
                        " (Despatchs - CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END) -tresholds/"+mhlKoff+" AS Execution " +
                        " FROM ( " +
                        "                    (SELECT DespatchLine.ClientAddressId AS Clie,DespatchLine.WareId AS Wared,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Despatchs FROM Unit,DespatchLine,Despatch  " +
                        "                     WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '"+firstday+"' AND DespatchLine.CustDate < date('"+thisday+"','+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DespatchLine.ClientAddressId='"+crmKode+"' AND DocumentTypeId ='Despatch' GROUP BY Clie,DespatchLine.WareId ) " +
                        "                     LEFT JOIN  " +
                        "                     (SELECT DespatchLine.ClientAddressId,DespatchLine.WareId,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Returnss FROM Unit,DespatchLine,Despatch  " +
                        "                     WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '"+firstday+"' AND DespatchLine.CustDate < date('"+thisday+"','+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DespatchLine.ClientAddressId='"+crmKode+"'  AND DocumentTypeId ='CustReturn' GROUP BY DespatchLine.ClientAddressId, DespatchLine.WareId )) " +
                        " LEFT JOIN Mhl,Tresholds ON Wared = SkuCode AND Wared =ScuId GROUP BY Wared) GROUP BY UnionGrup) t " +
                        " ON p.UnionGrup = t.UnionGrup " +
                        " UNION ALL " +
                        " SELECT WareName,SUM(Otgruz) AS Otgruz1,tresholds AS tresholds1,CASE WHEN SUM(CASE WHEN Execution >=0 THEN '1.00' ELSE '0.00' END) >=1 THEN '0.50' ELSE '0.0' END AS Exec1,2 AS TypeSku1 FROM (" +
                        " SELECT  Clie,UnionGrup,Wared,Despatchs - CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END AS Otgruz,tresholds/"+mhlKoff+" AS tresholds, " +
                        " (Despatchs - CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END) -tresholds/"+mhlKoff+" AS Execution " +
                        " FROM ( " +
                        "                    (SELECT DespatchLine.ClientAddressId AS Clie,DespatchLine.WareId AS Wared,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Despatchs FROM Unit,DespatchLine,Despatch " +
                        "                     WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '"+firstday+"' AND DespatchLine.CustDate < date('"+thisday+"','+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DespatchLine.ClientAddressId='"+crmKode+"' AND DocumentTypeId ='Despatch' GROUP BY Clie,DespatchLine.WareId ) " +
                        "                     LEFT JOIN  " +
                        "                     (SELECT DespatchLine.ClientAddressId,DespatchLine.WareId,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Returnss FROM Unit,DespatchLine,Despatch " +
                        "                     WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '"+firstday+"' AND DespatchLine.CustDate < date('"+thisday+"','+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DespatchLine.ClientAddressId='"+crmKode+"'  AND DocumentTypeId ='CustReturn' GROUP BY DespatchLine.ClientAddressId,DespatchLine.WareId )) " +
                        " LEFT JOIN Mhl,Tresholds ON Wared = SkuCode AND Wared =ScuId GROUP BY Wared), Ware WHERE Wared=WareId AND UnionGrup NOT IN (SELECT UnionGrup FROM Mhl WHERE territory='"+territory+"' AND "+pointCategory+"='1' AND freeze='"+freezes+"')  GROUP BY UnionGrup ";
                /************************************************************************************/
                String queryMB ="SELECT SabbrendName,SUM(Otgruz)AS Otgruz,'только по скю',CASE WHEN SUM(Exec)>1 THEN 1.0 WHEN SUM(Exec)=0.5 AND (CASE WHEN SUM(TypeSku)>2 THEN 1 ELSE SUM(TypeSku) END) =1 THEN 1.0 ELSE SUM(Exec) END AS Exec , CASE WHEN SUM(TypeSku)>2 THEN 1 ELSE SUM(TypeSku) END AS TypeSku FROM ( " +
                        " SELECT SabbrendName,SUM(Otgruz)AS Otgruz,'только по скю',SUM(Exec) AS Exec,TypeSku FROM( " +
                        " SELECT SabbrendName,SUM(Otgruz) AS Otgruz,'только по скю',Exec,TypeSku FROM( " +
                        " SELECT p.UnionGrup,p.SabbrendName,SkuCode, SkuName ,Otgruz,tresholds,Exec, 1 AS TypeSku FROM (SELECT UnionGrup,SabbrendName, SkuName, SkuCode FROM Mhl WHERE "+pointCategory+" ='1' AND freeze='"+freezes+"' AND territory='"+territory+"' ) p " +
                        " LEFT JOIN " +
                        " (SELECT Clie,UnionGrup,SabbrendName,Wared,SUM(Otgruz) AS Otgruz,tresholds,CASE WHEN SUM(CASE WHEN Execution >=0 THEN '1.00' ELSE '0.00' END) >=1 THEN '1.0' ELSE '0.0' END AS Exec FROM ( " +
                        " SELECT  Clie,UnionGrup,SabbrendName,Wared,Despatchs - CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END AS Otgruz,tresholds/"+mhlKoff+" AS tresholds, " +
                        " (Despatchs - CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END) -tresholds/"+mhlKoff+" AS Execution " +
                        " FROM (" +
                        "                    (SELECT DespatchLine.ClientAddressId AS Clie,DespatchLine.WareId AS Wared,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Despatchs FROM Unit,DespatchLine,Despatch " +
                        "                     WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '"+firstday+"' AND DespatchLine.CustDate < date('"+thisday+"','+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DespatchLine.ClientAddressId='"+crmKode+"' AND DocumentTypeId ='Despatch' GROUP BY Clie,DespatchLine.WareId ) " +
                        "                     LEFT JOIN " +
                        "                     (SELECT DespatchLine.ClientAddressId,DespatchLine.WareId,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Returnss FROM Unit,DespatchLine,Despatch " +
                        "                     WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '"+firstday+"' AND DespatchLine.CustDate < date('"+thisday+"','+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DespatchLine.ClientAddressId='"+crmKode+"'  AND DocumentTypeId ='CustReturn' GROUP BY DespatchLine.ClientAddressId,DespatchLine.WareId )) " +
                        " LEFT JOIN Mhl,Tresholds ON Wared = SkuCode AND Wared =ScuId GROUP BY Wared) GROUP BY UnionGrup) t " +
                        " ON p.UnionGrup = t.UnionGrup) GROUP BY SabbrendName " +
                        " UNION ALL " +
                        " SELECT SabbrendName,SUM(Otgruz1),'только по скю',Exec1,TypeSku1 FROM( " +
                        " SELECT UnionGrup,SabbrendName,Wared,WareName,SUM(Otgruz) AS Otgruz1,tresholds AS tresholds1,CASE WHEN SUM(CASE WHEN Execution >=0 THEN '1.00' ELSE '0.00' END) >=1 THEN '0.50' ELSE '0.0' END AS Exec1,2 AS TypeSku1 FROM ( " +
                        " SELECT  Clie,UnionGrup,SabbrendName ,Wared,Despatchs - CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END AS Otgruz,tresholds/"+mhlKoff+" AS tresholds, " +
                        " (Despatchs - CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END) -tresholds/"+mhlKoff+" AS Execution " +
                        " FROM (" +
                        "                    (SELECT DespatchLine.ClientAddressId AS Clie,DespatchLine.WareId AS Wared,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Despatchs FROM Unit,DespatchLine,Despatch " +
                        "                     WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '"+firstday+"' AND DespatchLine.CustDate < date('"+thisday+"','+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DespatchLine.ClientAddressId='"+crmKode+"' AND DocumentTypeId ='Despatch' GROUP BY Clie,DespatchLine.WareId ) " +
                        "                     LEFT JOIN " +
                        "                     (SELECT DespatchLine.ClientAddressId,DespatchLine.WareId,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Returnss FROM Unit,DespatchLine,Despatch " +
                        "                     WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '"+firstday+"' AND DespatchLine.CustDate < date('"+thisday+"','+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DespatchLine.ClientAddressId='"+crmKode+"'  AND DocumentTypeId ='CustReturn' GROUP BY DespatchLine.ClientAddressId,DespatchLine.WareId )) " +
                        "LEFT JOIN Mhl,Tresholds ON Wared = SkuCode AND Wared =ScuId GROUP BY Wared), Ware WHERE Wared=WareId AND UnionGrup NOT IN (SELECT UnionGrup FROM Mhl WHERE territory='"+territory+"' AND "+pointCategory+"='1' AND freeze='"+freezes+"')  GROUP BY UnionGrup " +
                        ") GROUP BY SabbrendName) GROUP BY SabbrendName,TypeSku ) GROUP BY SabbrendName ORDER BY TypeSku ASC";
                String query=null;
                if (Segment.equals("off")){query =queryMHL;}
                else {query=queryMB;}

                Cursor cursorMHL = db.rawQuery(query,null);
                double mfact=0.00,mplan =0.00;
                while (cursorMHL.moveToNext()) {
                    if ( cursorMHL.getString(3) != null ) { mfact = mfact + Double.parseDouble(cursorMHL.getString(3));}
                    else { mfact = mfact + 0;}
                    if (cursorMHL.getString(4) != null && Double.parseDouble(cursorMHL.getString(4)) != 2 ) { mplan = mplan + Double.parseDouble(cursorMHL.getString(4));}
                    else { mplan= mplan + 0;}
                }
                String mhlbonus ="";
                if (Segment.equals("off")) {
                    if (mfact >= mplan && mplan != 0.0) {
                        mhlbonus = "30";
                        Mhlsalary = Mhlsalary + 30;
                    } else {
                        mhlbonus = "0";
                        Mhlsalary = Mhlsalary + 0;
                    }
                }
                else {
                if (mfact>=mplan && mplan!= 0.0 && Segment.equals("on")){
                    mhlbonus="30";MBsalary =MBsalary+30;
                } else {
                    mhlbonus="0";MBsalary=MBsalary+0;
                    }
                }

                db.execSQL("INSERT OR REPLACE INTO MhlFact (ClientAddressId,MhlExec,pointType) VALUES('" +crmKode + "','"+mhlbonus +"','"+Segment +"')");
                cursorMHL.close();
                /****************************************************************************/

            }
            cursorPoint.close();
            /*****************************************выводим mhl на экран***********************/
        String MhlMonitor ="SELECT COUNT(ClientAddressId),SUM(MhlExec) FROM MhlFact WHERE MhlExec ='30' AND pointType='off' " +
                           " UNION ALL" +
                            " SELECT COUNT(ClientAddressId),SUM(MhlExec) FROM MhlFact WHERE MhlExec ='30' AND pointType='on' ";
            Cursor cursorMhlMon = db.rawQuery(MhlMonitor,null);
            String ttp=null,mhlsumm=null;
            int k=0;
            String smt[]=new String[2];
            String smt1[] = new String[2];
            while (cursorMhlMon.moveToNext()) {
                smt[k]= cursorMhlMon.getString(0);
                smt1[k] =cursorMhlMon.getString(1);
                if (smt1[k] ==null) {smt1[k] ="0";}
                k++;
            }
            cursorMhlMon.close();
            TextView ttpview = (TextView) findViewById(R.id.textView376);
            ttpview.setText(smt[0] + " тт");
            TextView mhlsummview = (TextView) findViewById(R.id.textView379);
            mhlsummview.setText(String.valueOf(smt1[0]) + lei);

            TextView ttpview1 = (TextView) findViewById(R.id.textView378);
            ttpview1.setText(smt[1] + " тт");
            TextView mhlsummview1 = (TextView) findViewById(R.id.textView380);
            mhlsummview1.setText(String.valueOf(smt1[1]) + lei);

            sumsalary= sumsalary+Mhlsalary+MBsalary;

            /********************************считаем планограммы****************************************************/

            String planogramQuerry ="  SELECT SUM(Summa) AS plano FROM ( " +
                    " SELECT Equipment,ClientAddressId,Plan,Confirmation, CASE WHEN Plan <3 THEN Confirmation*5 ELSE ROUND ((15.00/Plan*Confirmation),2) END AS Summa FROM  ( " +
                    " SELECT Equipment,v.ClientAddressId, COUNT(v.PlanDate) AS Plan,  SUM(CASE WHEN ConfV =1 AND ConfB =1 THEN 1 ELSE 0 END) AS Confirmation FROM ( " +
                    " (SELECT ClientAddressId, PlanDate, Confirmation AS ConfV FROM VisitsSalary) v " +
                    " LEFT JOIN " +
                    " (SELECT ClientAddresId, PlanDate, Confirmation AS ConfB,Equipment FROM Planograms) b " +
                    " ON v.ClientAddressId =b.ClientAddresId AND date(v.PlanDate)=date(b.PlanDate) " +
                    " ) WHERE Equipment IS NOT NULL GROUP BY v.ClientAddressId, Equipment)) ;";
            Cursor cursorplanogram =db.rawQuery(planogramQuerry, null);
            double palno=0.00;
            while (cursorplanogram.moveToNext()) {
                if (cursorplanogram.getString(0) == null) {palno =0;}
                else {
                    palno = Double.parseDouble(cursorplanogram.getString(0));
                }
            }
            cursorplanogram.close();
            palno= round(palno,2);

            TextView palpal = (TextView) findViewById(R.id.textView394);
            palpal.setText(String.valueOf(palno) + lei);

            sumsalary = sumsalary +palno;
            /**********************************считаем бренд приоритеты******************************************/
        String brends ="SELECT brend,COUNT(ClientAddresId),SUM(Brends.price) FROM BrendPrioritets,Brends WHERE name = brend AND Confirmation ='1' AND BrendPrioritets.type= Brends.type  GROUP BY brend,BrendPrioritets.type";
            Cursor brendCursor= db.rawQuery(brends,null);
            int ctk=brendCursor.getCount();
            ListView lv5 = (ListView) findViewById(R.id.listView5);
            double bresum=0.00;
            while (brendCursor.moveToNext()){
                hm2 = new HashMap<String, Object>();
                hm2.put("Название",brendCursor.getString(0));
                hm2.put("Количество", brendCursor.getString(1));
                hm2.put("Сумма", brendCursor.getString(2));
                bresum = bresum + Float.parseFloat(brendCursor.getString(2));
                despatchList2.add(hm2);
            }
            brendCursor.close();
            adapterList =null;
            adapterList = new SimpleAdapter(SalaryActivity.this, despatchList2,
                    R.layout.list_item_brends, new String[] {"Название","Количество","Сумма"},
                    new int[] {R.id.textView584,R.id.textView583,R.id.textView586});
            lv5.setAdapter(adapterList);
            getTotalHeightofListView(lv5, ctk);

            TextView bresm = (TextView) findViewById(R.id.textView573);
            bresm.setText(String.valueOf(bresum) + lei);

            sumsalary = sumsalary +bresum;

            /********************************считаем бомбы****************************************************/
            String BombQuerry =" SELECT bombname, SUM(Summa) AS boom FROM ( " +
                    " SELECT bombname,ClientAddressId,Plan,Confirmation, ROUND ((10.00/Plan*Confirmation),2) AS Summa FROM  ( " +
                    " SELECT bombname,v.ClientAddressId, COUNT(v.PlanDate) AS Plan,  SUM(CASE WHEN ConfV =1 AND ConfB =1 THEN 1 ELSE 0 END) AS Confirmation FROM ( " +
                    " (SELECT ClientAddressId, PlanDate, Confirmation AS ConfV FROM VisitsSalary) v " +
                    " LEFT JOIN" +
                    " (SELECT ClientAddresId, PlanDate, Confirmation AS ConfB,bombname FROM Bombs) b " +
                    " ON v.ClientAddressId =b.ClientAddresId AND date(v.PlanDate)=date(b.PlanDate) " +
                    " ) WHERE bombname IS NOT NULL GROUP BY v.ClientAddressId, bombname)) GROUP BY bombname ";
            Cursor cursorBombs =db.rawQuery(BombQuerry,null);
            ListView lv9 = (ListView) findViewById(R.id.listView9);
           int ctg = cursorBombs.getCount();
            double bomsum=0.00;
            while (cursorBombs.moveToNext()) {
                hm1 = new HashMap<String, Object>();
                hm1.put("Название",cursorBombs.getString(0));
                hm1.put("Деньги", cursorBombs.getString(1));
                bomsum = bomsum + Float.parseFloat(cursorBombs.getString(1));
                despatchList1.add(hm1);
            }
            cursorBombs.close();
           bomsum= round(bomsum,2);

            adapterList =null;
            adapterList = new SimpleAdapter(SalaryActivity.this, despatchList1,
                    R.layout.list_item_boomsalary, new String[] {"Название","Деньги"},
                    new int[] {R.id.textView522,R.id.textView523});
            lv9.setAdapter(adapterList);
            getTotalHeightofListView(lv9, ctg);
            TextView booms = (TextView) findViewById(R.id.textView521);
            booms.setText(String.valueOf(bomsum) + lei);

            sumsalary = sumsalary +bomsum;

        /************************************спец задача**************************************************/
        String SzQuerry ="SELECT sz1,sz2 FROM Sz";
            Cursor cursorSz = db.rawQuery(SzQuerry,null);
            String spec1=null,spec2= null;
            while (cursorSz.moveToNext()) {
                spec1 = cursorSz.getString(0);
                spec2 = cursorSz.getString(1);
            }
        cursorSz.close();
            TextView specz1 = (TextView) findViewById(R.id.textView415);
            specz1.setText(spec1);
            TextView specz2 = (TextView) findViewById(R.id.textView525);
            specz2.setText(spec2);

        /********************************Вывод итого зп**************************************************/
            sumsalary = new BigDecimal(sumsalary).setScale(2, RoundingMode.UP).doubleValue();
            TextView totalsalary = (TextView) findViewById(R.id.textView420);
            totalsalary.setText(String.valueOf(sumsalary) + lei);

        /*******************************нац промо**************************/

        String natsPromo ="SELECT ClientAddressId,PlanDate,InfoValue FROM WorkReportInfo WHERE FormNumber ='459' AND InfoTypeId ='UA20629'";
            Cursor natsCur =db.rawQuery(natsPromo,null);
            while(natsCur.moveToNext()){
                db.execSQL("INSERT OR REPLACE INTO PromoTyped (id,PlanDate,ClientAddresId,InfoValue) VALUES('" +natsCur.getString(0) +natsCur.getString(1)+ "','"+natsCur.getString(1) +"','"+natsCur.getString(0) +"','"+natsCur.getString(2) +"')");
            }
            natsCur.close();

        String natsDesp ="SELECT ClientAddressId,CustDate,Quantity FROM DespatchLine WHERE WareId ='CRM020103A02'";
            Cursor natsDespCur =db.rawQuery(natsDesp,null);
            while(natsDespCur.moveToNext()){
                db.execSQL("INSERT OR REPLACE INTO PromoDespatch (id,PlanDate,ClientAddresId,Quantity) VALUES('" +natsDespCur.getString(0) +natsDespCur.getString(1)+ "','"+natsDespCur.getString(1) +"','"+natsDespCur.getString(0) +"','"+natsDespCur.getString(2) +"')");
            }
            natsDespCur.close();

            String promo =" SELECT SUM(Info),SUM(Desp) FROM( " +
                    " SELECT t.ClientAddresId,Info,Desp FROM( " +
                    " SELECT ClientAddresId FROM ( " +
                    " SELECT ClientAddresId FROM PromoTyped " +
                    " UNION ALL " +
                    " SELECT ClientAddresId FROM PromoDespatch) " +
                    " GROUP BY ClientAddresId) t " +
                    " LEFT JOIN " +
                    " (SELECT ClientAddresId,SUM(InfoValue) AS Info FROM PromoTyped GROUP BY ClientAddresId) t1 " +
                    " ON t.ClientAddresId=t1.ClientAddresId " +
                    " LEFT JOIN  " +
                    " (SELECT ClientAddresId,SUM(Quantity) AS Desp FROM PromoDespatch GROUP BY ClientAddresId) t2 " +
                    " ON t.ClientAddresId=t2.ClientAddresId )";
            Cursor promoCur = db.rawQuery(promo,null);
            while (promoCur.moveToNext()){
                TextView one = (TextView) findViewById(R.id.textView808);
                one.setText(String.valueOf(promoCur.getString(0))+" шт");
                TextView two = (TextView) findViewById(R.id.textView811);
                two.setText(String.valueOf(promoCur.getString(0))+" шт");
            }
            promoCur.close();


            /******************************************************************/

            db.setTransactionSuccessful();
		} catch (SQLException e) {
        } finally {
			db.endTransaction();
			db.close();
		}
	}

public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem mi = menu.add(0, 1, 0, R.string.action_settings);
		mi.setIntent(new Intent(this, PrefActivity.class));

		MenuItem mie = menu.add(0, 1, 0, R.string.about );
		mie.setIntent(new Intent(this, Info.class));

		//getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);

	}

    public static void getTotalHeightofListView(ListView listView, int Countt) {

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight*Countt
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
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

    public int seasonKoffMhl(){
        int koff =1;
        Calendar bCalendar = Calendar.getInstance();
        bCalendar.add(Calendar.MONTH, 0);
        int month = bCalendar.get(Calendar.MONTH);
        Log.i("LOG_TAG", "Месяц " + month);
        if (month>=4 && month<=9){
            koff =1;
        }
        else{
            koff=2;
        }

        return koff;
    }
	
public void deleteOldRows(){
    File sdPath = Environment.getExternalStorageDirectory();
    DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
    ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SalaryActivity.this, DB_NAME);
    db = extdbc.openDataBase();
    db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
    try {
        db.beginTransaction();

        db.execSQL("DELETE FROM Brends WHERE PlanDate NOT IN (SELECT PlanDate FROM Brends WHERE PlanDate > date('" + firstday + "') AND PlanDate < date('" + thisday + "','+1 day'))");
        db.execSQL("DELETE FROM Bombs WHERE PlanDate NOT IN (SELECT PlanDate FROM Bombs WHERE PlanDate > date('" + firstday + "') AND PlanDate < date('" + thisday + "','+1 day'))");
        db.execSQL("DELETE FROM DebetStatus WHERE Date NOT IN (SELECT Date FROM DebetStatus WHERE Date > date('" + firstday + "') AND Date < date('"+thisday+"','+1 day'))");
        db.execSQL("DELETE FROM Planograms WHERE PlanDate NOT IN (SELECT PlanDate FROM Planograms WHERE PlanDate > date('" + firstday + "') AND PlanDate < date('"+thisday+"','+1 day'))");
        db.execSQL("DELETE FROM VisitsComplete WHERE PlanDate NOT IN (SELECT PlanDate FROM VisitsComplete WHERE PlanDate > date('" + firstday + "') AND PlanDate < date('"+thisday+"','+1 day'))");
        db.execSQL("DELETE FROM VisitsPlan WHERE PlanDate NOT IN (SELECT PlanDate FROM VisitsPlan WHERE PlanDate > date('" + firstday + "') AND PlanDate < date('"+thisday+"','+1 day'))");
        db.execSQL("DELETE FROM VisitsSalary WHERE PlanDate NOT IN (SELECT PlanDate FROM VisitsSalary WHERE PlanDate > date('" + firstday + "') AND PlanDate < date('"+thisday+"','+1 day'))");
        db.execSQL("DELETE FROM Stock WHERE PlanDate NOT IN (SELECT PlanDate FROM Stock WHERE PlanDate > date('" + firstday + "') AND PlanDate < date('"+thisday+"','+1 day'))");

        db.setTransactionSuccessful();
    } catch (SQLException e) {
    } finally {
        db.endTransaction();
        db.close();
    }
    }
}
