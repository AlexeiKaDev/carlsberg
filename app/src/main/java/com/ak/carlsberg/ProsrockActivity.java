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
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class ProsrockActivity extends AppCompatActivity {

    public static String DB_PATH;
	private static final String DB_NAME = "monolit.db";
    final String DIR_SD_CARLSBERG = "Carlsberg/Database";
    private static final String DB_Carlsberg = "carlsberg.db";
	String[] sNames,sId;
	SharedPreferences sp;
	SQLiteDatabase db;

	/** Called when the activity is first created. */
	
	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prosrock);

        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
        if (actionBarTitleView != null) {
            actionBarTitleView.setTypeface(fonts);
            actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        }

        @SuppressWarnings("unused")
        final String LOG_TAG = null;
/******************************************���������� ��� �������*************************************************/
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(ProsrockActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
            try {
                db.beginTransaction();
                String query = "SELECT WareName,number FROM predprosro,Ware WHERE WareId=skucode";

                Cursor cursor = db.rawQuery(query, null);

                sNames = new String[cursor.getCount()];
                sId = new String[cursor.getCount()];
                while (cursor.moveToNext()) {
                    sNames[cursor.getPosition()] = cursor.getString(0);
                    sId[cursor.getPosition()] = cursor.getString(1);

                }
                cursor.close();
                /*****************************************Выборка данных по точкам для расчета***************************/

                db.setTransactionSuccessful();
            } catch (SQLException e) {
            } finally {
                db.endTransaction();
                db.close();
            }

        final TextView price = (TextView) findViewById(R.id.textView587);
        final TextView otgruz = (TextView) findViewById(R.id.textView595);
        final TextView actia = (TextView) findViewById(R.id.textView599);
        final EditText prosrock = (EditText) findViewById(R.id.editText106);

            /***************************************************************************************************************/
            final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, sNames);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(adapter2);

            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @SuppressLint("CommitPrefEdits")
                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View itemSelected, int selectedItemPosition, long selectedId) {
                    Log.i("LOG_TAG", "Выбрана позиция " + selectedItemPosition);
                    price.setText(sId[selectedItemPosition]+" лей");
                    String getTex= String.valueOf(prosrock.getText());
                    int prosrockBottles = 0;
                    if (getTex.isEmpty() || getTex =="" || getTex ==" " || getTex==null) {
                    prosrockBottles = 0;} else {prosrockBottles =Integer.parseInt(getTex);}
                    double cena = Double.parseDouble(sId[selectedItemPosition]);
                    int countbottles=(int) Math.round((((cena * prosrockBottles) * 0.15) / 10));
                    otgruz.setText(countbottles +" шт");
                    double actiaCena = ((cena*prosrockBottles)-(countbottles*10))/prosrockBottles*1.2;
                    if (String.valueOf(actiaCena)!="NaN"){actiaCena =new BigDecimal(actiaCena).setScale(1, RoundingMode.UP).doubleValue();};
                    actia.setText(String.valueOf(actiaCena) +" лей");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub

                }

            });


            Button Button01 = (Button) findViewById(R.id.Button01);
            OnClickListener perescet = new OnClickListener() {
                @Override
                public void onClick(View v) {
 	    	   /*��������� � �������� Prezentations*/
                    finish();
                    Intent intent = new Intent(ProsrockActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            };

            // �������� ���������� ������ OK (btnOk)
            Button01.setOnClickListener(perescet);


/*************************************************************************************************/

        }

		public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem mi = menu.add(0, 1, 0, R.string.action_settings );									
		mi.setIntent(new Intent(this, PrefActivity.class));
		
		MenuItem mie = menu.add(0, 1, 0, R.string.about );									
		mie.setIntent(new Intent(this, Info.class));
			
		//getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);		
		    
	}
		/*******************************************Преобразование даты************************************************/
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
