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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class RouteActivity extends AppCompatActivity {

	public static String DB_PATH,crmcode,response;
	private static final String DB_NAME = "monolit.db";
	private static final String DB_Carlsberg = "carlsberg.db";
	final String DIR_SD_CARLSBERG = "Carlsberg/Database";
   SQLiteDatabase db;
    ArrayList<HashMap<String, Object>> despatchList;
    HashMap<String, Object> hm;
    ListView lv;
    MyTask mt;
    FyTask ft;
    String thisday;
    SharedPreferences sp;
    TextView tv;
    EditText inputSearch;
    SimpleAdapter adapterList;
    int switchh=0;
	/** Called when the activity is first created. */

	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route);
        despatchList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;


		int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
		Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
		if (actionBarTitleView != null) {
			actionBarTitleView.setTypeface(fonts);
			actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		}
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
        Log.i("LOG_TAG", "Даты: сегодняшний день " + thisday + "  первый день месяца " + firstday);
        /***********************************кнопка на общий экран***************************/

        final Button button101 =(Button) findViewById(R.id.button101);
        final OnClickListener butt101 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                lv.setAdapter(null);
                ft = new FyTask();
                ft.execute();
            }
        };
        button101.setOnClickListener(butt101);
        final Button button102 =(Button) findViewById(R.id.button102);
        final OnClickListener butt102 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                lv.setAdapter(null);
                mt = new MyTask();
                mt.execute();

            }
        };
        button102.setOnClickListener(butt102);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        lv = (ListView)findViewById(R.id.listView2);
        mt = new MyTask();
        mt.execute();
        /***************************************************************************/

        /**************************************************************************/
        inputSearch = (EditText) findViewById(R.id.editText108);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapterList.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id) {

                String stringall = parent.getItemAtPosition(position).toString();
                String[] PointId = stringall.split(",");
                String pointCrm = PointId[2].substring(11, PointId[2].length() - 1);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("crmcode", pointCrm);
                ed.commit();
                ed.putString("stat", PointId[0].substring(13));
                ed.commit();
                Log.i("LOG_TAG", "Клик на список " + PointId[0].substring(13));
                finish();
                Intent intent = new Intent(RouteActivity.this, PointSalaryInfoActivity.class);
                startActivity(intent);
            }

        });
	}
    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean("allpoints", false);
            ed.commit();
            File sdPath = Environment.getExternalStorageDirectory();
            DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
            ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(RouteActivity.this, DB_NAME);
            db = extdbc.openDataBase();
            db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
            try {
                db.beginTransaction();
                despatchList.clear();
                String queryBom = "SELECT CRMJobSchedule.ClientAddressId,LegalName,Location,CompleteStatus FROM CRMJobSchedule,ClientAddress WHERE CRMJobSchedule.ClientAddressId=ClientAddress.ClientAddressId AND PlanDate >= '"+thisday+"' AND PlanDate < date('"+thisday+"' ,'+1 day')";

                Cursor cursor = db.rawQuery(queryBom, null);
                while (cursor.moveToNext()) {

                    hm = new HashMap<String, Object>();
                    hm.put("Point crm",cursor.getString(0));
                    hm.put("Point name", cursor.getString(1) + "\n" + cursor.getString(2) );
                    if (cursor.getString(3).equals("Completed"))
                    {hm.put("RouteStatus", "выполнен");}
                    else if (cursor.getString(3).equals("Canceled"))
                    {hm.put("RouteStatus", "отменен");}
                    else
                    {hm.put("RouteStatus", "в ожидании");}
                    despatchList.add(hm);
                }
                cursor.close();
                adapterList=null;
                adapterList = new SimpleAdapter(RouteActivity.this, despatchList,
                        R.layout.list_item_route, new String[]{"Point crm","Point name","RouteStatus" },
                        new int[]{R.id.textView317, R.id.textView319, R.id.textView324});

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

        }

    }

    class FyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean("allpoints", true);
            ed.commit();
            File sdPath = Environment.getExternalStorageDirectory();
            DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
            ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(RouteActivity.this, DB_NAME);
            db = extdbc.openDataBase();
            db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
            try {
                db.beginTransaction();
                despatchList.clear();
                String queryBom = "SELECT ClientAddressId,LegalName,Location FROM ClientAddress";

                Cursor cursor = db.rawQuery(queryBom, null);
                while (cursor.moveToNext()) {

                    hm = new HashMap<String, Object>();
                    hm.put("Point crm",cursor.getString(0));
                    hm.put("Point name", cursor.getString(1) + "\n" + cursor.getString(2));
                    hm.put("RouteStatus", "");
                    despatchList.add(hm);
                }
                cursor.close();
                adapterList=null;
                adapterList = new SimpleAdapter(RouteActivity.this, despatchList,
                        R.layout.list_item_route, new String[]{"Point crm","Point name","RouteStatus" },
                        new int[]{R.id.textView317, R.id.textView319, R.id.textView324});

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
