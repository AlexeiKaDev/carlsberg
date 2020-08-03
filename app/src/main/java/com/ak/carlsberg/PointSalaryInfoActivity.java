package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class PointSalaryInfoActivity extends AppCompatActivity {

	public static String DB_PATH,crmcode,response;
	private static final String DB_NAME = "monolit.db";
	private static final String DB_Carlsberg = "carlsberg.db";
	final String DIR_SD_CARLSBERG = "Carlsberg/Database";
    final String lei =" лей";
    String territory=null,boomba="null",brend="null";
    String pointCategory=null;
    String freezes=null;
    String Segment=null;
    String typePoint=null,price=null;
    double sumsalary =0;
    String image=" ";
	SharedPreferences sp;
	SQLiteDatabase db;
    ArrayList<HashMap<String, Object>> despatchList,despatchListp,despatchList1,despatchList2,despatchList3,despatchListost,despatchListostt;
    HashMap<String, Object> hm,hm1,hmp,hmp1,hmost,hmostt;
    ListView lv,lv4,lv6, lv7, lv8,lv9;
    SimpleAdapter adapterList,adapter;
    Button planogram,bombbutton,brendbutton;
    String thisday,Bt;
    String crmKode=null;
    final int TYPE_PHOTO = 1;
    final int TYPE_BOMB = 2;
    final int TYPE_BREND= 3;
    int FOTO_TYPE =0;
    int mhlKoff;
    final int REQUEST_CODE_PHOTO = 1;
    RadioGroup radioGroup;
    LinearLayout view;
	/** Called when the activity is first created. */

	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pointsalaryinfo);
        MobileAds.initialize(this, "ca-app-pub-5841832298128104~5926147671");
        despatchList = new ArrayList<HashMap<String, Object>>();
        despatchList1 = new ArrayList<HashMap<String, Object>>();
        despatchList2 = new ArrayList<HashMap<String, Object>>();
        despatchList3 = new ArrayList<HashMap<String, Object>>();
        despatchListost = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm,hm1,hmp,hmp1,hmost;

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
		String firstday = getFullTime(bCalendar.getTimeInMillis());
		bCalendar.set(Calendar.DATE, bCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		String todaydate = getFullTime(bCalendar.getTimeInMillis());

        /***********************************кнопка на общий экран***************************/
        Button button01 =(Button) findViewById(R.id.point);
        OnClickListener butt01 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PointSalaryInfoActivity.this, RouteActivity.class);
                startActivity(intent);
            }
        };
        button01.setOnClickListener(butt01);


        Button Button01 = (Button) findViewById(R.id.Button01);
        OnClickListener rlp = new OnClickListener() {
            @Override
            public void onClick(View v) {
		  /*��������� � �������� Prezentations*/
                Intent intent = new Intent(PointSalaryInfoActivity.this, RlpActivity.class);
                startActivity(intent);

            }
        };
        Button01.setOnClickListener(rlp);

        mhlKoff = seasonKoffMhl();
        Log.i("LOG_TAG", "Коэффициент месяца " +mhlKoff);

        /*******************************считаем визиты*********************************/
		File sdPath = Environment.getExternalStorageDirectory();
		DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
		ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(PointSalaryInfoActivity.this, DB_NAME);
		db = extdbc.openDataBase();
		db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
		try {
			db.beginTransaction();
            /*******вывод информации по точке****/
        final String crm=sp.getString("crmcode","");
		String info ="SELECT LegalName,FaxNumber,chanel_name FROM ClientAddress,Segments WHERE TradeTypeTreeId=code AND ClientAddressId ='"+crm+"'";
			Cursor cursorInfo = db.rawQuery(info,null);
			while (cursorInfo.moveToNext())
			{
				TextView infot =(TextView) findViewById(R.id.textView344);
				infot.setText(cursorInfo.getString(0));
                infot.setAllCaps(true);
                TextView infot1 =(TextView) findViewById(R.id.textView341);
                infot1.setText(cursorInfo.getString(1));
                TextView infot2 =(TextView) findViewById(R.id.textView345);
                infot2.setText(cursorInfo.getString(2));
			}
			cursorInfo.close();
/***********************************визиты**********************************************/
            String XoCount ="SELECT COUNT(ClientAddressId),SUM(Confirmation),CASE WHEN COUNT(ClientAddressId) <4 THEN 5 ELSE MAX(Equipment)*1.00/COUNT(ClientAddressId) END,MAX(Equipment) FROM VisitsSalary WHERE ClientAddressId='"+crm+"'";
            Cursor cursor2 = db.rawQuery(XoCount, null);
            String St[] = new String[4];
            while (cursor2.moveToNext()) {
                St[0] = cursor2.getString(0);
                St[1] = cursor2.getString(1);
                St[2] = cursor2.getString(2);
                St[3] = cursor2.getString(3);
            }
            TextView NetXo = (TextView) findViewById(R.id.textView433);
            NetXo.setText(St[0] );
            TextView EstiXo = (TextView) findViewById(R.id.textView436);
            EstiXo.setText(St[1]);
            TextView EstiXo1 = (TextView) findViewById(R.id.textView326);
            EstiXo1.setText(St[2]);
            TextView EstiXo2 = (TextView) findViewById(R.id.textView321);
            if ( St[3] ==null){EstiXo2.setText("Нет визитов за этот месяц");}
            else {
                if (St[3].equals("50")) {
                    EstiXo2.setText("ТТ c ХО/РО( 50 лей в месяц)");
                    if(St[2].equals("5")){EstiXo1.setText("12.50");};
                } else {
                    EstiXo2.setText("ТТ бех ХО/РО( 25 лей в месяц)");
                }
            }
            cursor2.close();
            /**************************выводим деньги по визитам на экран****************/
          String visitSalary =" SELECT SUM(SUMMA)*1.00 FROM ( SELECT t.ClientAddressId,FULLPLAN ,PLAN,FACT, CONF, t.Equipment, "+
                    " CASE WHEN FULLPLAN <4 THEN 5*CONF*1.00 ELSE t.Equipment*1.00/FULLPLAN*CONF*1.00 END AS SUMMA "+
                    "  FROM ( (SELECT ClientAddressId,COUNT(ClientAddressId) AS FULLPLAN FROM VisitsSalary WHERE ClientAddressId='"+crm+"' " +
                    "  GROUP BY ClientAddressId) i " +
                    " LEFT JOIN (SELECT ClientAddressId,COUNT(ClientAddressId) AS PLAN,Equipment FROM VisitsSalary WHERE ClientAddressId='"+crm+"' "+
                    " GROUP BY ClientAddressId,Equipment) t "+
                    " ON i.ClientAddressId = t.ClientAddressId "+
                    " LEFT JOIN (SELECT ClientAddressId,COUNT(LatitudeComplete)  AS FACT,Equipment  FROM VisitsSalary "+
                    " WHERE LatitudeComplete >'0.00' AND ClientAddressId='"+crm+"' "+
                    " GROUP BY ClientAddressId,Equipment) t1 "+
                    " ON t.ClientAddressId =t1.ClientAddressId AND t.Equipment = t1.Equipment "+
                    " LEFT JOIN (SELECT ClientAddressId,COUNT(Confirmation) AS CONF,Equipment  FROM VisitsSalary "+
                    " WHERE Confirmation>0 AND ClientAddressId='"+crm+"'GROUP BY ClientAddressId,Equipment) t2 "+
                    " ON t.ClientAddressId =t2.ClientAddressId AND t.Equipment = t2.Equipment) ) WHERE Equipment ='25' "+
                    " UNION ALL "+
                    "  SELECT SUM(SUMMA)*1.00 FROM ( SELECT t.ClientAddressId,FULLPLAN ,PLAN,FACT, CONF, t.Equipment, " +
                    " CASE WHEN FULLPLAN <4 THEN 12.50*CONF*1.00 ELSE t.Equipment*1.00/FULLPLAN*CONF*1.00 END AS SUMMA "+
                    "  FROM ( (SELECT ClientAddressId,COUNT(ClientAddressId) AS FULLPLAN FROM VisitsSalary WHERE ClientAddressId='"+crm+"'  " +
                    " GROUP BY ClientAddressId) i "+
                    "  LEFT JOIN (SELECT ClientAddressId,COUNT(ClientAddressId) AS PLAN,Equipment FROM VisitsSalary WHERE ClientAddressId='"+crm+"'  "+
                    " GROUP BY ClientAddressId,Equipment) t "+
                    " ON i.ClientAddressId = t.ClientAddressId "+
                    " LEFT JOIN (SELECT ClientAddressId,COUNT(LatitudeComplete)  AS FACT,Equipment  FROM VisitsSalary "+
                    " WHERE LatitudeComplete >'0.00' AND ClientAddressId='"+crm+"'  GROUP BY ClientAddressId,Equipment) t1"+
                    " ON t.ClientAddressId =t1.ClientAddressId AND t.Equipment = t1.Equipment "+
                    " LEFT JOIN (SELECT ClientAddressId,COUNT(Confirmation) AS CONF,Equipment  FROM VisitsSalary "+
                    " WHERE Confirmation>0 AND  ClientAddressId='"+crm+"'  GROUP BY ClientAddressId,Equipment) t2 "+
                    " ON t.ClientAddressId =t2.ClientAddressId AND t.Equipment = t2.Equipment) ) WHERE Equipment ='50' ";
            Cursor cursor3 = db.rawQuery(visitSalary, null);
            int ik=0;
            while (cursor3.moveToNext()) {
                if (cursor3.getString(0)==null) {St[ik]="0";}
                else {
                    St[ik] = cursor3.getString(0);
                }
                ik++;
            }
            sumsalary = Float.valueOf(St[0]) + Float.valueOf(St[1]);
            TextView NetXoS = (TextView) findViewById(R.id.textView328);
            sumsalary= new BigDecimal(sumsalary).setScale(2, RoundingMode.UP).doubleValue();
            NetXoS.setText(String.valueOf(sumsalary) + lei);
            cursor3.close();
            /*******************************остатки**********************************************************/
            lv9 = (ListView)findViewById(R.id.listView11);
            String ost="SELECT PlanDate,Stockplan,Stockfact,Confirmation AS GPS,SUM(CASE WHEN StockPlan=Stockfact AND Confirmation='1' THEN '1' ELSE 0 END) AS Egal FROM RekStock " +
                " WHERE ClientAddresId='"+crm+"' GROUP BY ClientAddresId,PlanDate ";
            Cursor ostCur = db.rawQuery(ost,null);
            LinearLayout.LayoutParams layoutParamsos = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 65*ostCur.getCount());
            lv9.setLayoutParams(layoutParamsos);
            int osplan=ostCur.getCount(),osfact = 0;
            while (ostCur.moveToNext()) {
                hmost = new HashMap<String, Object>();
                hmost.put("ActionDate",ostCur.getString(0).substring(0, 10));
                hmost.put("plansku", ostCur.getString(1) );
                hmost.put("facktsku", ostCur.getString(2));
                hmost.put("Gps", ostCur.getString(3));
                hmost.put("fact", ostCur.getString(4));
                osfact = osfact + Integer.parseInt(ostCur.getString(4));
                despatchListost.add(hmost);
            }
            ostCur.close();
            double ostatki;
            if (osplan ==0){ostatki=0.00;}
             else {ostatki =(10*1.00/osplan)*osfact;}
            Log.i("LOG_TAG", "Остатки" +ostatki + " План "+osplan+" osfact "+osfact);
            ostatki= new BigDecimal(ostatki).setScale(2, RoundingMode.UP).doubleValue();
            sumsalary = sumsalary+ostatki;
            TextView osplans = (TextView) findViewById(R.id.textView342);
            osplans.setText(String.valueOf(osplan));
            TextView osfacts = (TextView) findViewById(R.id.textView601);
            osfacts.setText(String.valueOf(osfact));
            TextView osleis = (TextView) findViewById(R.id.textView608);
            osleis.setText(String.valueOf(ostatki)+lei);

            adapterList=null;
            adapterList = new SimpleAdapter(PointSalaryInfoActivity.this, despatchListost,
                    R.layout.list_item_pointost, new String[]{"ActionDate","plansku","facktsku","Gps","fact" },
                    new int[]{R.id.textView351, R.id.textView352, R.id.textView353, R.id.textView354, R.id.textView355});
            lv9.setAdapter(adapterList);

            /*************************************нажатие на строчку******************************/
            lv9.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(final AdapterView<?> parent, View view,
                                        final int position, long id) {

                    String stringall = parent.getItemAtPosition(position).toString();
                    String[] PointId = stringall.split(",");
                    Log.i("LOG_TAG", "Клик на строчку " + "SELECT WareName,Fact FROM Stock WHERE PlanDate=date('"+PointId[0].substring(12)+"') AND ClientAddresId='"+crm+"'" );
                    ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(PointSalaryInfoActivity.this, DB_NAME);
                    db = extdbc.openDataBase();
                    db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
                    try {
                        db.beginTransaction();
                    String ost="SELECT WareName,Fact FROM Stock WHERE date(PlanDate)='"+PointId[0].substring(12)+"' AND ClientAddresId='"+crm+"' ";
                    Cursor ostCur =db.rawQuery(ost,null);
                        despatchListostt = new ArrayList<HashMap<String, Object>>();
                    while(ostCur.moveToNext()){
                        hmostt = new HashMap<String, Object>();
                        hmostt.put("Sku", ostCur.getString(0));
                        hmostt.put("Fact", ostCur.getString(1));

                        despatchListostt.add(hmostt);
                    }
                        ostCur.close();

                        AlertDialog.Builder builder = new AlertDialog.Builder(PointSalaryInfoActivity.this);
                        builder.setCancelable(true);
                        LinearLayout vieww = (LinearLayout) getLayoutInflater()
                                .inflate(R.layout.dialog_ostatki, null);
                        ListView lv = (ListView) vieww.findViewById(R.id.listView3);
                        adapter=null;
                        adapter = new SimpleAdapter(PointSalaryInfoActivity.this, despatchListostt,
                                R.layout.list_item_ostatki, new String[] {"Sku","Fact"},
                                new int[] {R.id.textView329,R.id.textView350});
                        lv.setAdapter(adapter);
                        builder.setView(vieww);
                        builder.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                        db.setTransactionSuccessful();
                    } catch (SQLException e) {
                    } finally {
                        db.endTransaction();
                        db.close();
                    }
                }

            });


/*******************************************Дебиторка******************************************/
            lv4 = (ListView)findViewById(R.id.listView4);
            String querydeb="SELECT  ActionDate,PaymentDate,CustInvoiceSumm,OverduePeriod FROM CompanyInvoicesPayDocs WHERE ClientAddressId='"+crm+"'";
            Cursor cursor4 = db.rawQuery(querydeb, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 65*cursor4.getCount());
            lv4.setLayoutParams(layoutParams);
            while (cursor4.moveToNext()) {
                hm = new HashMap<String, Object>();
                hm.put("ActionDate",cursor4.getString(0).substring(0, 10));
                hm.put("PaymentDate", cursor4.getString(1).substring(0, 10) );
                hm.put("CustInvoiceSumm", cursor4.getString(2));
                hm.put("OverduePeriod", cursor4.getString(3) );
                despatchList.add(hm);
                Log.i("LOG_TAG", "Дата  " + cursor4.getString(0)+ " "+ cursor4.getString(1) +" "+ cursor4.getString(2)+" "+cursor4.getString(3));
            }
            cursor4.close();

            adapterList=null;
            adapterList = new SimpleAdapter(PointSalaryInfoActivity.this, despatchList,
                    R.layout.list_item_debet, new String[]{"ActionDate","PaymentDate","CustInvoiceSumm","OverduePeriod" },
                    new int[]{R.id.textView329, R.id.textView330, R.id.textView349, R.id.textView350});
            lv4.setAdapter(adapterList);
            /****************************************план на точку***********************************************/
            String planQuerry="SELECT PlanDate, " +
                    " CASE " +
                    " WHEN strftime('%w', PlanDate)='1' THEN 'pn' " +
                    " WHEN strftime('%w', PlanDate)='2' THEN 'vt' " +
                    " WHEN strftime('%w', PlanDate)='3' THEN 'sr' " +
                    " WHEN strftime('%w', PlanDate)='4' THEN 'ct' " +
                    " WHEN strftime('%w', PlanDate)='5' THEN 'pt' " +
                    " ELSE 'Нет расчета на этот день' " +
                    " END AS 'день недели', " +
                    " CASE " +
                    " WHEN strftime('%w', PlanDate)='1' THEN (SELECT pn*1.00/10000 FROM sales_day_plan ) " +
                    " WHEN strftime('%w', PlanDate)='2' THEN (SELECT vt*1.00/10000 FROM sales_day_plan ) " +
                    " WHEN strftime('%w', PlanDate)='3' THEN (SELECT sr*1.00/10000 FROM sales_day_plan ) " +
                    " WHEN strftime('%w', PlanDate)='4' THEN (SELECT ct*1.00/10000 FROM sales_day_plan ) " +
                    " WHEN strftime('%w', PlanDate)='5' THEN (SELECT pt*1.00/10000 FROM sales_day_plan ) " +
                    " ELSE 'Нет расчета на этот день' " +
                    " END AS 'Процент дня для тт', " +
                    " (SELECT percent*1.00/10000 FROM point_sales_plan WHERE crm='"+crm+"') AS pointpercent, " +
                    " (SELECT TargetValue FROM TaskReportView WHERE TaskName ='Продажи в дал (пиво)') AS allplan " +
                    " FROM VisitsSalary WHERE ClientAddressId='"+crm+"'";
                    int[] weekDay=getWorkDays();
            TextView plantt = (TextView) findViewById(R.id.textView562);
            TextView ttpercent = (TextView) findViewById(R.id.textView564);
            Cursor planCursor =db.rawQuery(planQuerry,null);
            while (planCursor.moveToNext()) {
                String a = "0", b = "0";
                if (!planCursor.isNull(0)) {
                    if ( planCursor.getString(3) == null) {
                        a = "0";
                    } else {
                        a = planCursor.getString(3);
                    }
                    if (planCursor.getString(4) == null) {
                        b = "0";
                    } else {
                        b = planCursor.getString(4);
                    }
                }
                double ttP = new BigDecimal(Double.parseDouble(a)).setScale(3, RoundingMode.UP).doubleValue();
                double ttPercent = new BigDecimal(ttP*100).setScale(2, RoundingMode.UP).doubleValue();
                double planTt;
                if (b!="0" && ttP!=0) {
                    planTt = new BigDecimal(Double.parseDouble(b)*ttP).setScale(2, RoundingMode.UP).doubleValue();
                    plantt.setText(String.valueOf(planTt) + " дал");}
                else {
                    plantt.setText("новая тт, нет плана");
                }
                ttpercent.setText(String.valueOf(ttPercent) + " %");
            }
            planCursor.close();

            String Efact ="SELECT Despatchs -" +
                    "CASE WHEN Returnss  IS NULL THEN '0.00' ELSE Returnss END AS Otgruz FROM (" +
                    " (SELECT CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Despatchs FROM Unit,DespatchLine,Despatch " +
                    " WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '" + firstday + "' AND DespatchLine.CustDate < date('" + thisday + "' ,'+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DocumentTypeId ='Despatch' AND DespatchLine.ClientAddressId ='"+crm+"') " +
                    " LEFT JOIN " +
                    " (SELECT CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue),2) AS REAL)AS Returnss FROM Unit,DespatchLine,Despatch " +
                    " WHERE Unit.WareId=DespatchLine.WareId AND DespatchLine.CustDate >= '" + firstday + "' AND DespatchLine.CustDate < date('" + thisday + "' ,'+1 day') AND Unit.UnitId='dal' AND Despatch.CustNumber= DespatchLine.CustNumber AND DocumentTypeId ='CustReturn' AND DespatchLine.ClientAddressId ='"+crm+"' )) ";
            Cursor cursorFact = db.rawQuery(Efact, null);
            TextView Fact = (TextView) findViewById(R.id.textView566);
            float tfact = 0;
            while (cursorFact.moveToNext()) {
                if (cursorFact.isNull(0) ) { tfact =0;}
                else {
                    tfact = Float.parseFloat(cursorFact.getString(0));
                }
            }
            Fact.setText(String.valueOf(tfact + " дал"));
            cursorFact.close();



/*****************************************************MHL/Матрица Брендов**********************************/
            lv6 = (ListView)findViewById(R.id.listView6);
           String mhlPointtype="SELECT  'off' FROM ClientAddress WHERE ClientAddressId='"+crm+"' AND TradeTypeTreeId IN ('TT11','TT12','TT21','TT31','TT32','TT33','TT41','TT51','TT61','TT71','TT81','MT91','MT101','MT111','MT112','MT113','MT114','MT121','MT122','MT123','MT124','MT131','MT132','MT133','MT134','MT141') "+
           " UNION ALL" +
           " SELECT  'on' FROM ClientAddress WHERE ClientAddressId='"+crm+"' AND TradeTypeTreeId NOT IN ('TT11','TT12','TT21','TT31','TT32','TT33','TT41','TT51','TT61','TT71','TT81','MT91','MT101','MT111','MT112','MT113','MT114','MT121','MT122','MT123','MT124','MT131','MT132','MT133','MT134','MT141')";
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

            String queryPoint ="SELECT t.ClientAddressId,FaxNumber,TradeTypeTreeId,CASE WHEN SUM (CASE WHEN WareId=equipment THEN '1.00' ELSE '0.00' END) >0 THEN '1.00' ELSE '0.00' END AS freezs FROM( " +
                    " (SELECT ClientAddressId,FaxNumber,TradeTypeTreeId FROM ClientAddress WHERE ClientAddressId='"+crm+"') t " +
                    " LEFT JOIN " +
                    " (SELECT ClientAddressId,WareId FROM ClientAddressEquip ) t1 " +
                    " ON t.ClientAddressId = t1.ClientAddressId )" +
                    " LEFT JOIN " +
                    " Equipment ON WareId =equipment AND Equipment.type='XO' GROUP BY t.ClientAddressId ";
            Cursor cursorPoint = db.rawQuery(queryPoint,null);
            int Mhlsalary=0;
            while (cursorPoint.moveToNext()) {
                crmKode = cursorPoint.getString(0);
                territory = cursorPoint.getString(1);
                pointCategory = cursorPoint.getString(2);
                freezes = cursorPoint.getString(3);
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
              /**********************************************************************************************************************************************************************************/
               String queryMB ="SELECT SabbrendName,SUM(Otgruz)AS Otgruz,'порог sku',CASE WHEN SUM(Exec)>1 THEN 1.0 WHEN SUM(Exec)=0.5 AND (CASE WHEN SUM(TypeSku)>2 THEN 1 ELSE SUM(TypeSku) END) =1 THEN 1.0 ELSE SUM(Exec) END AS Exec  , CASE WHEN SUM(TypeSku)>2 THEN 1 ELSE SUM(TypeSku) END AS TypeSku FROM ( " +
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
                if (Segment.equals("off")){
                    query =queryMHL;
                    TextView mh = (TextView) findViewById(R.id.textView373);
                    mh.setText("MHL");
                }
                else {
                    query=queryMB;
                    TextView mh = (TextView) findViewById(R.id.textView373);
                    mh.setText("Матрица Брендов");
                    TextView m = (TextView) findViewById(R.id.textView375);
                    m.setText("Бренд");
                }
                Cursor cursorMHL = db.rawQuery(query,null);

                LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 68*cursorMHL.getCount()*3);
                lv6.setLayoutParams(layoutParam);
                String mhlbonus;
                double mfact=0.00,mplan =0.00;
                while (cursorMHL.moveToNext()) {

                    hm1 = new HashMap<String, Object>();
                    hm1.put("Skuname", cursorMHL.getString(0));
                    hm1.put("Otgruz", cursorMHL.getString(1));
                    hm1.put("tresholds", cursorMHL.getString(2));
                    hm1.put("Exec", cursorMHL.getString(3));
                    if (cursorMHL.getString(4).equals("2")){hm1.put("TypeSku", "доп.");
                    } else {hm1.put("TypeSku", "обяз.");
                    }
                    despatchList1.add(hm1);
                    Log.i("LOG_TAG", "Дата  " + cursorMHL.getString(0)+ " "+ cursorMHL.getString(1) +" "+ cursorMHL.getString(2)+" "+cursorMHL.getString(3)+" "+cursorMHL.getString(4));

                    if ( cursorMHL.getString(3) != null ) { mfact = mfact + Double.parseDouble(cursorMHL.getString(3));}
                    else { mfact = mfact + 0;}
                    if (cursorMHL.getString(4) != null && Double.parseDouble(cursorMHL.getString(4)) != 2 ) { mplan = mplan + Double.parseDouble(cursorMHL.getString(4));}
                    else { mplan= mplan + 0;}
                }
                cursorMHL.close();
                TextView tvm = (TextView) findViewById(R.id.textView335);
                tvm.setText(String.valueOf(mplan));
                TextView tvm1 = (TextView) findViewById(R.id.textView337);
                tvm1.setText(String.valueOf(mfact));
                if (mfact>=mplan && mplan!= 0.0 ){mhlbonus="15";} else {mhlbonus="0";}
                TextView tvm2 = (TextView) findViewById(R.id.textView338);
                tvm2.setText(String.valueOf(mhlbonus)+lei);

                sumsalary = sumsalary + Integer.parseInt(mhlbonus);

                adapterList=null;
                adapterList = new SimpleAdapter(PointSalaryInfoActivity.this, despatchList1,
                        R.layout.list_item_pointmhl, new String[]{"Skuname","Otgruz","tresholds","Exec","TypeSku" },
                        new int[]{R.id.textView351, R.id.textView352, R.id.textView353, R.id.textView354, R.id.textView355});
                lv6.setAdapter(adapterList);
                /****************************************************************************/

            }
            cursorPoint.close();
            /***********************************планограммы***********************************************/
        Boolean iniList =sp.getBoolean("allpoints", false);
            String stat = sp.getString("stat","");
        String peremplanogram = null;
            if (Segment.equals("off")) {peremplanogram=territory;} else {peremplanogram ="horeca";}
        String queryPlanogram =" SELECT Equipment.'"+peremplanogram+"' FROM ClientAddress,ClientAddressEquip,Equipment " +
                " WHERE ClientAddress.ClientAddressId= ClientAddressEquip.ClientAddressId AND WareId=equipment AND ClientAddress.ClientAddressId ='"+crmKode+"' AND Equipment.type='XO' ";
            planogram =(Button) findViewById(R.id.button95);
            Cursor cursorPlanogram = db.rawQuery(queryPlanogram,null);
            if (cursorPlanogram.getCount()==0 || iniList==true || stat.equals("выполнен")){
                planogram.setEnabled(false);
                planogram.setText("Запрещена");
            }
            while (cursorPlanogram.moveToNext()) {
                image = cursorPlanogram.getString(0);
                Log.i("LOG_TAG"," Проверка" +cursorPlanogram.getString(0));
            }
            cursorPlanogram.close();

            OnClickListener planButton = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FOTO_TYPE=1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(PointSalaryInfoActivity.this);
                    builder.setCancelable(true);
                    LinearLayout view = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.dialogplanograms, null);
                    builder.setView(view);
                    ImageView img = (ImageView) view.findViewById(R.id.imageView2);
                    int resID = getResources().getIdentifier(image, "drawable", getPackageName());
                    img.setImageResource(resID);
                    Log.i("LOG_TAG", "код фотографии " + image);
                    Log.i("LOG_TAG", "код фотографии " + resID );

                    builder.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            FOTO_TYPE=0;
                        }

                    });
                    builder.setPositiveButton("Сделать фото", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FOTO_TYPE=1;
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                            startActivityForResult(intent, REQUEST_CODE_PHOTO);
                            dialog.cancel();
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            };
            planogram.setOnClickListener(planButton);
            lv7 = (ListView)findViewById(R.id.listView7);
            String queryPl="SELECT  t.ClientAddressId,t.PlanDate,GPS, CASE WHEN Foto IS NULL THEN 0 ELSE Foto END, CASE WHEN GPS =1 AND Foto=1 THEN 1 ELSE 0 END AS Confirmation FROM ( " +
                    " (SELECT ClientAddressId,PlanDate, Confirmation AS GPS FROM VisitsSalary WHERE ClientAddressId ='"+crmKode+"') t " +
                    " LEFT JOIN " +
                    " (SELECT ClientAddresId,PlanDate,Confirmation AS Foto FROM Planograms WHERE ClientAddresId ='"+crmKode+"') t1 " +
                    " ON ClientAddresId = ClientAddressId AND date (t.PlanDate) = date( t1.PlanDate) )";
            Cursor cursorPlano = db.rawQuery(queryPl,null);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 65*cursorPlano.getCount());
            lv7.setLayoutParams(layoutParams2);
            final int planpl=cursorPlano.getCount();
            int gpspl=0;
            int foto=0;
            int total=0;
            while (cursorPlano.moveToNext()) {

                hmp = new HashMap<String, Object>();
                hmp.put("Skuname", cursorPlano.getString(1).substring(0, 10));
                hmp.put("Otgruz", cursorPlano.getString(2) );
                hmp.put("tresholds", cursorPlano.getString(3));
                hmp.put("Exec", cursorPlano.getString(4));
                Log.i("LOG_TAG", "планограммы " + cursorPlano.getString(1)+ " "+ cursorPlano.getString(2)+" "+cursorPlano.getString(3)+" " +cursorPlano.getString(4));

                if ( cursorPlano.getString(2)!=null) {gpspl =gpspl+ Integer.parseInt(cursorPlano.getString(2));}
                if ( cursorPlano.getString(3)!=null) {foto =foto+ Integer.parseInt(cursorPlano.getString(3));}
                if ( cursorPlano.getString(4)!=null) {total =total+ Integer.parseInt(cursorPlano.getString(4));}

                despatchList2.add(hmp);
                    }
            cursorPlano.close();
            double tot=0.00;
            if (planpl <=3){tot=total*5;}
            else {
                tot=(15.00/planpl)*total;
                tot= new BigDecimal(tot).setScale(2, RoundingMode.UP).doubleValue();
            }

            TextView pl = (TextView) findViewById(R.id.textView364);
            pl.setText(String.valueOf(planpl));
            TextView pl1 = (TextView) findViewById(R.id.textView365);
            pl1.setText(String.valueOf(total));
            TextView pl3 = (TextView) findViewById(R.id.textView515);
            pl3.setText(String.valueOf(tot)+ lei);

            sumsalary = (float) (sumsalary + tot);

            adapterList=null;
            adapterList = new SimpleAdapter(PointSalaryInfoActivity.this, despatchList2,
                    R.layout.list_item_debet, new String[]{"Skuname","Otgruz","tresholds","Exec" },
                    new int[]{R.id.textView329, R.id.textView330, R.id.textView349, R.id.textView350});
            lv7.setAdapter(adapterList);
            /*************************************Бренд приоритеты***************************************/
            brendbutton =(Button) findViewById(R.id.button109);
            if (iniList==true || stat.equals("выполнен")){
                brendbutton.setEnabled(false);
                brendbutton.setText("Запрещен");
            }

            final String [] brendsprior = new String [2];
            final String [] prices = new String [2];
            final String [] number = new String [2];
            String prior ="SELECT type,name,price,CASE WHEN Confirmation='1' THEN price ELSE '0' END AS equal FROM ( " +
                    "(SELECT  " +
                    "CASE " +
                    "WHEN NetTreeId='1867501588' " +
                    "OR NetTreeId='1911191948' " +
                    "OR NetTreeId='1911904329' " +
                    "OR NetTreeId='442' " +
                    "OR NetTreeId='443' " +
                    "OR NetTreeId='444' " +
                    "OR NetTreeId='445' " +
                    "OR NetTreeId='446' " +
                    "OR NetTreeId='447' " +
                    "OR NetTreeId='449' " +
                    "OR NetTreeId='450' " +
                    "OR NetTreeId='524' " +
                    "OR NetTreeId='555' " +
                    "THEN 'Rka' ELSE channel_type END AS type," +
                    "name,price,number FROM ClientAddress,Segments,BrendPrioritets WHERE ClientAddressId ='"+crmKode+"' AND code =TradeTypeTreeId AND type=channel_type)" +
                    "LEFT JOIN " +
                    "(SELECT brend,Confirmation FROM Brends WHERE ClientAddresId ='"+crmKode+"') " +
                    "ON name=brend)" ;
            Cursor bredP = db.rawQuery(prior,null);
            int br =0;
            double brendssum = 0;
            if (bredP.getCount()!=0) {
                while (bredP.moveToNext()) {
                    if (bredP.getString(0)!="0"){
                        typePoint=bredP.getString(0);
                        brendsprior[br]= bredP.getString(1);
                        prices[br]=bredP.getString(2);
                        number[br]=bredP.getString(3);
                    }
                    brendssum = brendssum + Double.parseDouble(bredP.getString(3));
                    br=br+1;
                }
            }
            else{ brendbutton.setEnabled(false);}
            bredP.close();

            TextView name1 = (TextView) findViewById(R.id.textView570);
            TextView price1 = (TextView) findViewById(R.id.textView575);
            TextView eq1 = (TextView) findViewById(R.id.textView571);
            name1.setText(brendsprior[0]);
            price1.setText(prices[0]);
            eq1.setText(number[0]);

            TextView name2 = (TextView) findViewById(R.id.textView398);
            TextView price2 = (TextView) findViewById(R.id.textView576);
            TextView eq2 = (TextView) findViewById(R.id.textView568);
            name2.setText(brendsprior[1]);
            price2.setText(prices[1]);
            eq2.setText(number[1]);

            OnClickListener brendButton1 = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FOTO_TYPE=3;
                    AlertDialog.Builder builder = new AlertDialog.Builder(PointSalaryInfoActivity.this);
                    builder.setCancelable(true);
                    view = null;
                        view = (LinearLayout) getLayoutInflater()
                                .inflate(R.layout.brends, null);
                    RadioButton radioButton1 = (RadioButton) view.findViewById(R.id.id1);
                    RadioButton radioButton2 = (RadioButton) view.findViewById(R.id.id2);
                    int rcount=2;
                    if (brendsprior[0]==null || brendsprior[0]=="" || Integer.parseInt(number[0])>0) {radioButton1.setVisibility(View.GONE);rcount=rcount-1;radioButton2.setChecked(true);price=prices[1];} else{radioButton1.setText(brendsprior[0]);price=prices[0];}
                    if (brendsprior[1]==null || brendsprior[1]=="" || Integer.parseInt(number[1])>0) {radioButton2.setVisibility(View.GONE);rcount=rcount-1;} else{radioButton2.setText(brendsprior[1]);price=prices[1];}

                    builder.setView(view);

                    radioGroup = (RadioGroup) view.findViewById(R.id.radio1);
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            RadioButton radioButton1 = (RadioButton) view.findViewById(checkedId);
                            Log.i("LOG_TAG", "Бренд приоритет" + radioButton1.getText());
                            brend = radioButton1.getText().toString();

                        }
                    });

                    builder.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            FOTO_TYPE=0;
                            brend="null";
                            price=null;
                        }

                    });
                    if (rcount>0){
                    builder.setPositiveButton("Сделать фото", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FOTO_TYPE=3;
                            if (brend.equals("null")){
                                RadioButton radioButton2 =(RadioButton)  view.findViewById(R.id.id1);
                                brend=radioButton2.getText().toString();
                                price=prices[0];
                                Log.i("LOG_TAG", "Бренд приоритет" + brend);
                                if (brend=="null"||brend.isEmpty() ||brend==null){
                                    radioButton2 =(RadioButton)  view.findViewById(R.id.id2);
                                    brend=radioButton2.getText().toString();
                                    price=prices[1];
                                    Log.i("LOG_TAG", "Бренд приоритет" + brend);
                                }
                            }
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_BREND));
                            startActivityForResult(intent, REQUEST_CODE_PHOTO);
                            dialog.cancel();
                        }

                    });}

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            };
            brendbutton.setOnClickListener(brendButton1);

            TextView brensum = (TextView) findViewById(R.id.textView582);
            brensum.setText(String.valueOf(brendssum));
            sumsalary = sumsalary + brendssum;

/***************************************************Бомбы******************************************/
        /*******************************************************************************/
            bombbutton =(Button) findViewById(R.id.button103);
            if (iniList==true || stat.equals("выполнен")){
                bombbutton.setEnabled(false);
                bombbutton.setText("Запрещены");
            }

            String querryBomType="SELECT CASE WHEN NetTreeId='1867501588' OR NetTreeId='1911191948' OR NetTreeId='1911904329' OR " +
                    " NetTreeId='442' OR NetTreeId='443' OR NetTreeId='444' OR NetTreeId='445' OR NetTreeId='446' OR " +
                    " NetTreeId='447' OR NetTreeId='449' OR NetTreeId='450' OR NetTreeId='524' OR NetTreeId='555' " +
                    " THEN 'Rka' ELSE channel_type END FROM ClientAddress,Segments WHERE ClientAddressId ='"+crmKode+"' AND code =TradeTypeTreeId";
            Cursor cursorBt= db.rawQuery(querryBomType,null);
            Bt ="";
            while (cursorBt.moveToNext()) {
                Bt = cursorBt.getString(0);
                Log.i("LOG_TAG", "Тип бомбы " + cursorBt.getString(0));
            }
            cursorBt.close();

            OnClickListener planButton1 = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FOTO_TYPE=2;
                    AlertDialog.Builder builder = new AlertDialog.Builder(PointSalaryInfoActivity.this);
                    builder.setCancelable(true);
                   view = null;
                    if (Bt.equals("off")){
                        view = (LinearLayout) getLayoutInflater()
                                .inflate(R.layout.bomboff, null);
                    }
                    if (Bt.equals("on")){
                        view = (LinearLayout) getLayoutInflater()
                                .inflate(R.layout.bombon, null);
                    }
                    if (Bt.equals("Rka")){
                        view = (LinearLayout) getLayoutInflater()
                                .inflate(R.layout.bombmodern, null);
                    }

                    builder.setView(view);
                    Log.i("LOG_TAG", "код фотографии " + image);
                    radioGroup = (RadioGroup) view.findViewById(R.id.radio1);
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            RadioButton radioButton1 = (RadioButton) view.findViewById(checkedId);
                            Log.i("LOG_TAG", "БОООООМБАААА" + radioButton1.getText());
                            boomba = radioButton1.getText().toString();

                        }
                    });

                    builder.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            FOTO_TYPE=0;
                            boomba="null";
                        }

                    });
                    builder.setPositiveButton("Сделать фото", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FOTO_TYPE=2;
                            if (boomba.equals("null")){
                                RadioButton radioButton2 =(RadioButton)  view.findViewById(R.id.id1);
                                boomba=radioButton2.getText().toString();
                            }
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_BOMB));
                            startActivityForResult(intent, REQUEST_CODE_PHOTO);
                            dialog.cancel();
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            };
            bombbutton.setOnClickListener(planButton1);

            /*******************************вывод инфо по бомбам******************************/
            lv8 = (ListView)findViewById(R.id.listView8);
        String bomberman ="SELECT bombname,Plan,SUM(Confirmation),10.00/Plan*Sum(Confirmation) FROM ( " +
                " SELECT  t.ClientAddressId,t.PlanDate,GPS,'"+planpl+"' AS Plan, CASE WHEN Foto IS NULL THEN 0 ELSE Foto END AS Foto, CASE WHEN GPS =1 AND Foto=1 THEN 1 ELSE 0 END AS Confirmation,t1.bombname FROM ( " +
                "                     (SELECT ClientAddressId,PlanDate, Confirmation AS GPS,0 AS bombname FROM VisitsSalary WHERE ClientAddressId ='"+crmKode+"') t  " +
                "                     LEFT JOIN  " +
                "                    (SELECT ClientAddresId,PlanDate,Confirmation AS Foto, bombname FROM Bombs WHERE ClientAddresId ='"+crmKode+"') t1 " +
                "                    ON ClientAddresId = ClientAddressId AND date (t.PlanDate) = date( t1.PlanDate) ) " +
                " ) WHERE bombname IS NOT NULL GROUP BY bombname";
            Double bombSum=0.00;
            Cursor cursorBomba= db.rawQuery(bomberman,null);
           int size =cursorBomba.getCount();
          /*  LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120*cursorBomba.getCount());
            lv8.setLayoutParams(layoutParams3);*/
            while (cursorBomba.moveToNext()) {
                hmp1 = new HashMap<String, Object>();
                hmp1.put("Skuname", cursorBomba.getString(0));
                hmp1.put("Otgruz", cursorBomba.getString(1));
                hmp1.put("tresholds", cursorBomba.getString(2));
                hmp1.put("Exec", cursorBomba.getString(3)+lei);

                bombSum = bombSum +Double.parseDouble(cursorBomba.getString(3));
                despatchList3.add(hmp1);
            }
            cursorBomba.close();
            adapterList=null;
            adapterList = new SimpleAdapter(PointSalaryInfoActivity.this, despatchList3,
                    R.layout.list_item_debet, new String[]{"Skuname","Otgruz","tresholds","Exec" },
                    new int[]{R.id.textView329, R.id.textView330, R.id.textView349, R.id.textView350});
            lv8.setAdapter(adapterList);
            getTotalHeightofListView(lv8,size);

            TextView pls = (TextView) findViewById(R.id.textView516);
            pls.setText(String.valueOf(bombSum) + lei);

            sumsalary = (float) (sumsalary + bombSum);

            /***********************************************************************/
            TextView totalsalary = (TextView) findViewById(R.id.textView420);
            sumsalary=new BigDecimal(sumsalary).setScale(2, RoundingMode.UP).doubleValue();
            totalsalary.setText(String.valueOf(sumsalary) + lei);
            /************************************спец задача**************************************************/
            String SzQuerry ="SELECT sz1,sz2 FROM Sz";
            Cursor cursorSz = db.rawQuery(SzQuerry,null);
            String spec1=null,spec2= null;
            while (cursorSz.moveToNext()) {
                spec1 = cursorSz.getString(0);
                spec2 = cursorSz.getString(1);
            }
            cursorSz.close();
            TextView specz1 = (TextView) findViewById(R.id.textView530);
            specz1.setText(spec1);
            TextView specz2 = (TextView) findViewById(R.id.textView532);
            specz2.setText(spec2);
        /******************************************нац промо*********************************/
            String promo =" SELECT t.ClientAddresId,Info,Desp FROM( " +
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
                    " ON t.ClientAddresId=t2.ClientAddresId WHERE t.ClientAddresId ='"+crmKode+"'";
            Cursor promoCur = db.rawQuery(promo,null);
            while (promoCur.moveToNext()){
                TextView one = (TextView) findViewById(R.id.textView814);
                one.setText(String.valueOf(promoCur.getString(1))+" шт");
                TextView two = (TextView) findViewById(R.id.textView816);
                two.setText(String.valueOf(promoCur.getString(2))+" шт");
            }
            promoCur.close();
        /************************************************************************************/

            db.setTransactionSuccessful();
		} catch (SQLException e) {
		} finally {
			db.endTransaction();
			db.close();
		}
	}

    private Uri generateFileUri(int type) {
        File file = null;

        File sdnew =Environment.getExternalStorageDirectory();
        if (FOTO_TYPE==1)
        {sdnew = new File(sdnew.getAbsolutePath() + "/Carlsberg/Photo/planogramms/");}
        if (FOTO_TYPE==2)
        {sdnew = new File(sdnew.getAbsolutePath() + "/Carlsberg/Photo/bombs/");}
        if (FOTO_TYPE==3)
        {sdnew = new File(sdnew.getAbsolutePath() + "/Carlsberg/Photo/brends/");}
        if (!sdnew.exists()){sdnew.mkdirs();

        }
        switch (type) {

            case TYPE_PHOTO:
                file = new File(sdnew.getPath() +"/" +thisday+"_"+crmKode+"_pl.jpg");
                break;
            case TYPE_BOMB:
                file = new File(sdnew.getPath() +"/" +thisday+"_"+crmKode+"_"+ boomba+".jpg");
                break;
            case TYPE_BREND:
                file = new File(sdnew.getPath() +"/"+crmKode+"_"+ brend+".jpg");
                break;
        }
        Log.d("LOG_TAG", "fileName = " + file);
        return Uri.fromFile(file);
    }

public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem mi = menu.add(0, 1, 0, R.string.action_settings );
		mi.setIntent(new Intent(this, PrefActivity.class));

		MenuItem mie = menu.add(0, 1, 0, R.string.about );
		mie.setIntent(new Intent(this, Info.class));

		//getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);

	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                File sdPath = Environment.getExternalStorageDirectory();
                DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
                ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(PointSalaryInfoActivity.this, DB_NAME);
                db = extdbc.openDataBase();
                db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
                try {
                    db.beginTransaction();

                    if (FOTO_TYPE==1) {
                        Toast toast = Toast.makeText(getApplicationContext(), "планограмма сохранена", Toast.LENGTH_LONG);
                        ((TextView) ((LinearLayout) toast.getView()).getChildAt(0))
                                .setGravity(Gravity.CENTER_HORIZONTAL);
                        toast.show();
                        db.execSQL(" INSERT OR REPLACE INTO Planograms (id,PlanDate,ClientaddresId,Equipment,PersonConf) VALUES('" + thisday + crmKode + "','" + thisday + "','" + crmKode + "','" + image + "','Торговый')");
                    }
                    if (FOTO_TYPE==2) {
                        Toast toast = Toast.makeText(getApplicationContext(), "бомба сохранена", Toast.LENGTH_LONG);
                        ((TextView) ((LinearLayout) toast.getView()).getChildAt(0))
                                .setGravity(Gravity.CENTER_HORIZONTAL);
                        toast.show();
                        db.execSQL(" INSERT OR REPLACE INTO Bombs (id,PlanDate,ClientaddresId,bombname,PersonConf) VALUES('" + thisday + crmKode + boomba +"','" + thisday + "','" + crmKode + "','" + boomba + "','Торговый')");
                    }
                    if (FOTO_TYPE==3) {
                        Toast toast = Toast.makeText(getApplicationContext(), "бренд приоритет сохранен", Toast.LENGTH_LONG);
                        ((TextView) ((LinearLayout) toast.getView()).getChildAt(0))
                                .setGravity(Gravity.CENTER_HORIZONTAL);
                        toast.show();
                        db.execSQL("DELETE FROM Brends WHERE ClientaddresId='" + crmKode + "' AND brend = '" + brend + "'");
                        db.execSQL(" INSERT OR REPLACE INTO Brends (id,PlanDate,ClientaddresId,brend,PersonConf,type,price) VALUES('"+crmKode + brend+ "','" + thisday + "','" + crmKode + "','" + brend + "','Торговый','"+typePoint+"','"+price+"')");
                    }

                db.setTransactionSuccessful();
            } catch (SQLException e) {
            } finally {
                db.endTransaction();
                db.close();
            }
                if (intent == null) {
                    Log.i("LOG_TAG", "Intent is null");
                } else {
                    Log.i("LOG_TAG", "Photo uri: " + intent.getData());
                    Bundle bndl = intent.getExtras();
                    if (bndl != null) {
                        Object obj = intent.getExtras().get("data");
                        if (obj instanceof Bitmap) {
                            Bitmap bitmap = (Bitmap) obj;
                            Log.i("LOG_TAG", "bitmap " + bitmap.getWidth() + " x "
                                    + bitmap.getHeight());
                            // ivPhoto.setImageBitmap(bitmap);

                        }
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("LOG_TAG", "Canceled");
            }
        }
        FOTO_TYPE=0;
        boomba="null";
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

    public static final int[] getWorkDays(){

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
        int daysInMonth = aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Log.i("LOG_TAG", String.valueOf(aCalendar.getTime()));
        for(int i = 1 ; i <= daysInMonth; i++)
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
}
