package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
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

public class AtataActivity extends AppCompatActivity {

	public static String DB_PATH,crmcode,response;
	private static final String DB_NAME = "monolit.db";
	private static final String DB_Carlsberg = "carlsberg.db";
	final String DIR_SD_CARLSBERG = "Carlsberg/Database";
   SQLiteDatabase db;
    ArrayList<HashMap<String, Object>> despatchList;
    HashMap<String, Object> hm;
    ListView lv;
    String thisday;
    SimpleAdapter adapterList;

	/** Called when the activity is first created. */

	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atata);
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
        lv = (ListView) findViewById(R.id.listView10);

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(AtataActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            despatchList.clear();
            String queryBom = "SELECT Bombs.ClientAddresId,LegalName,Location,PlanDate,bombname,PersonConf,Bombs.comment FROM Bombs,ClientAddress WHERE ClientAddress.ClientAddressId =Bombs.ClientAddresId AND Bombs.Updated=1 AND Confirmation='0'" +
                    " UNION ALL " +
                    "SELECT Planograms.ClientAddresId,LegalName,Location,PlanDate,'Планограмма',PersonConf,Planograms.comment FROM Planograms,ClientAddress WHERE ClientAddress.ClientAddressId =Planograms.ClientAddresId AND Planograms.Updated=1 AND Confirmation='0'" +
                    " UNION ALL " +
                    "SELECT Brends.ClientAddresId,LegalName,Location,PlanDate,brend,PersonConf,Brends.comment FROM Brends,ClientAddress WHERE ClientAddress.ClientAddressId =Brends.ClientAddresId AND Brends.Updated=1 AND Confirmation='0'" ;
            Cursor cursor = db.rawQuery(queryBom, null);
            while (cursor.moveToNext()) {
                hm = new HashMap<String, Object>();
                hm.put("Point crm","Код тт: " +cursor.getString(0)+"\nНазвание тт: " + cursor.getString(1)+ "\nАдрес: " + cursor.getString(2));
                hm.put("Datename","Дата: " +cursor.getString(3) + "\nНазвание фото:" + cursor.getString(4) );
                hm.put("person","Проверяющий: " + cursor.getString(5) + "\nКомментарий: \"" + cursor.getString(6)+"\"" );
                despatchList.add(hm);
            }
            cursor.close();
            adapterList = new SimpleAdapter(AtataActivity.this, despatchList,
                    R.layout.list_item_atata, new String[]{"Point crm","Datename","person" },
                    new int[]{R.id.textView522, R.id.textView523, R.id.textView560});

            lv.setAdapter(adapterList);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
            db.close();
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
