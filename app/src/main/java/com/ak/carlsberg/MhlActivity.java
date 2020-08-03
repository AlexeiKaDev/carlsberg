package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class MhlActivity extends AppCompatActivity {

    public static String DB_PATH;
    private static final String DB_NAME = "monolit.db";
    private static final String DB_Carlsberg = "carlsberg.db";
    final String DIR_SD_CARLSBERG = "Carlsberg/Database";
    String[] sNames,sId;
    SharedPreferences sp;
    SQLiteDatabase db;
    String base,kanal,territory,person,firstday,todaydate;
    boolean orders;
    String Ch="",bossTel="",SMS="";
    ArrayList SmsList = new ArrayList();
    Double summFaact =0.0,summPlan=0.0;

    ArrayList<HashMap<String, Object>> despatchList;
    HashMap<String, Object> hm;
    ListView lv;
    MyTask mt;
    TextView textView,textView3,textView4;

    SimpleAdapter adapterList;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mhl);

        despatchList = new ArrayList<HashMap<String, Object>>();

        lv = (ListView)findViewById(R.id.listViewDes);
        textView = (TextView) findViewById(R.id.textView);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);

        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(fonts);
            actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        }

        Calendar bCalendar = Calendar.getInstance();
        bCalendar.add(Calendar.MONTH, 0);
        bCalendar.set(Calendar.HOUR_OF_DAY, 00);
        bCalendar.set(Calendar.MINUTE, 00);
        bCalendar.set(Calendar.MILLISECOND, 00);
        bCalendar.set(Calendar.SECOND,00);
        final int day = bCalendar.get(Calendar.DAY_OF_MONTH);
        bCalendar.set(Calendar.DATE, day-1);

        String thisday = getFullTime(bCalendar.getTimeInMillis());

        bCalendar.set(Calendar.DATE, 1);

        firstday =  getFullTime(bCalendar.getTimeInMillis());
        // set actual maximum date of previous month
        bCalendar.set(Calendar.DATE, bCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        //read it
        todaydate = getFullTime(bCalendar.getTimeInMillis());

        Log.i("LOG_TAG", "Первый день месяца " + firstday + " последний день месяца" + todaydate + "Этот день " + thisday);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        person =sp.getString("person", "");

        mt = new MyTask();
        mt.execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id) {

                String stringall = parent.getItemAtPosition(position).toString();
                String[] PointId = stringall.split(",");
                String pointCrm = PointId[3].substring(4);
                Log.i("LOG_TAG", "Клик на список " +pointCrm );
            }

        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem mi = menu.add(0, 1, 0, R.string.action_settings);
        mi.setIntent(new Intent(this, PrefActivity.class));

        MenuItem mie = menu.add(0, 1, 0, R.string.about );
        mie.setIntent(new Intent(this, Info.class));

        //getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }
    /*******************************************Преобразование даты************************************************/
    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            File sdPath = Environment.getExternalStorageDirectory();
            DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
            ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(MhlActivity.this, DB_NAME);
            db = extdbc.openDataBase();
            db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
            try {
                db.beginTransaction();
                String queryTer = "SELECT territory FROM Stuff WHERE employee ='"+person+"'";

                Cursor cursorTer = db.rawQuery(queryTer, null);
                while (cursorTer.moveToNext()) {
                    Log.i("LOG_TAG",  cursorTer.getString(0));
                    territory=cursorTer.getString(0);
                }
                cursorTer.close();

                String queryMHL = "SELECT ClientAddressId,ClientAddressName,LegalName,Comment,plan,COUNT(mhlgrup) AS ob,COUNT(mhlgrup2)*0.5 AS neob,(COUNT(mhlgrup3) -COUNT(mhlgrup)-COUNT(mhlgrup2))*0.33 AS dop,((COUNT(mhlgrup3) -COUNT(mhlgrup)-COUNT(mhlgrup2))*0.33 +COUNT(mhlgrup2)*0.5 + COUNT(mhlgrup)) AS rez FROM ( "+
                        " SELECT ClientAddressId,ClientAddressName,LegalName,t1.UnionCode,REAL,Comment,MHL.mhlgrup,MHL2.mhlgrup AS mhlgrup2,1 AS mhlgrup3,MHL.ScuId FROM ( "+
                        " SELECT t.ClientAddressId,ClientAddressName,LegalName,UnionCode, SUM(REAL) AS REAL,Comment,tresholds FROM ( "+
                        " SELECT DespatchLine.ClientAddressId,DespatchLine.WareId,WareUnion.UnionCode,CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL) AS REAL,tresholds  FROM Unit,DespatchLine,WareUnion,Tresholds WHERE Tresholds.ScuId=DespatchLine.WareId AND Unit.WareId=DespatchLine.WareId AND SkuCRMCode = DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal'  GROUP BY DespatchLine.WareId,DespatchLine.ClientAddressId "+
                        " ) t, ClientAddress WHERE REAL>=tresholds AND ClientAddress.ClientAddressId = t.ClientAddressId GROUP BY UnionCode,t.ClientAddressId  ) t1 "+
                        " LEFT JOIN MHL ON t1.UnionCode =MHL.UnionCode AND Comment =MHL.Channel_name AND MHL.Territory ='"+territory+"' AND MHL.mhlgrup ='1' "+
                        " LEFT JOIN MHL AS MHL2 ON t1.UnionCode =MHL2.UnionCode AND Comment =MHL2.Channel_name AND MHL2.Territory ='"+territory+"' AND mhlgrup2 ='2' "+
                        " LEFT JOIN MHL AS MHL3 ON t1.UnionCode =MHL3.UnionCode AND Comment =MHL3.Channel_name AND MHL3.Territory ='"+territory+"' ) "+
                        " LEFT JOIN (SELECT Channel_name,COUNT(mhlgrup) AS plan FROM MHL WHERE Territory ='MOLD' AND mhlgrup='1' GROUP BY Channel_name) ON Comment =Channel_name "+
                        " WHERE Comment IN (SELECT Channel_name FROM MHL) GROUP BY ClientAddressId ";

                Cursor cursorMHL = db.rawQuery(queryMHL, null);
                int i =1;
                if (cursorMHL.getCount()==0) {summPlan=1.0;summFaact=1.0;}
                else{
                while (cursorMHL.moveToNext()) {

                    hm = new HashMap<String, Object>();
                    hm.put("Id",cursorMHL.getString(0));
                    hm.put("Number", i + ".");
                    hm.put("Point name", cursorMHL.getString(1) + "\n" + cursorMHL.getString(2));
                    hm.put("Point plan", cursorMHL.getString(4) );
                    hm.put("Point total", cursorMHL.getString(8) );
                    double plan,fact,rez;
                    plan =Double.parseDouble(cursorMHL.getString(4));
                    fact =Double.parseDouble(cursorMHL.getString(8));
                    if ( fact >= plan)
                    {fact = plan;}
                    rez = BigDecimal.valueOf(fact*1.00/plan*100).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
                    hm.put("Point percent", rez);

                    despatchList.add(hm);
                    i++;
                    summPlan = summPlan + plan;
                    summFaact = summFaact + fact;

                }}
                cursorMHL.close();

                adapterList = new SimpleAdapter(MhlActivity.this, despatchList,
                        R.layout.list_item_mhl, new String[] {"Id","Number","Point name","Point plan", "Point total", "Point percent"},
                        new int[] {R.id.textView22,R.id.textView5,R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9 });

                db.setTransactionSuccessful();
            } catch (SQLException e) {
            } finally {
                db.endTransaction();
                db.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            lv.setAdapter(adapterList);
            textView.setText(summPlan.toString());
            double sfact =BigDecimal.valueOf(summFaact).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
            textView3.setText(String.valueOf(sfact));
            double rezult = BigDecimal.valueOf(summFaact*1.00/summPlan*100).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
            textView4.setText(String.valueOf(rezult)+"%");
        }
    }

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