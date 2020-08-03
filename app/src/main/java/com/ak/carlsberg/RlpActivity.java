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
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class RlpActivity extends AppCompatActivity {

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
    ArrayList<HashMap<String, Object>> despatchList,despatchListp,despatchList1,despatchList2,despatchList3,despatchListost;
    HashMap<String, Object> hm,hm1,hmp,hmp1,hmost;
    ListView lv,lv4,lv6, lv7, lv8,lv9;
    SimpleAdapter adapterList;
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
		setContentView(R.layout.rlp);

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

		File sdPath = Environment.getExternalStorageDirectory();
		DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
		ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(RlpActivity.this, DB_NAME);
		db = extdbc.openDataBase();
		db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
		try {
			db.beginTransaction();

			String crm = this.sp.getString("crmcode", "");
			String mai = " SELECT mai3,mai4,mai5,mai6,mai7, " +
					" ROUND(mai,0)," +
					" ROUND(mai-mai3,0) AS mai3r,ROUND(mai-mai4,0) AS mai4r,ROUND(mai-mai5,0) AS mai5r,ROUND(mai-mai6,0) AS mai6r,ROUND(mai-mai7,0) AS mai7r, " +
					" CASE WHEN mai-mai3 >0 THEN mai*0.05 ELSE 0 END AS perc0_5, " +
					" CASE WHEN mai-mai4 >0 THEN mai*0.01 ELSE 0 END AS perc1_0, " +
					" CASE WHEN mai-mai5 >0 THEN mai*0.015 ELSE 0 END AS perc1_5, " +
					" CASE WHEN mai-mai6 >0 THEN mai*0.02 ELSE 0 END AS perc2_0, " +
					" CASE WHEN mai-mai7 >0 THEN mai*0.025 ELSE 0 END AS perc2_5 " +
					" FROM RLP_plan  " +
					" LEFT JOIN RLP_fact  " +
					" ON RLP_plan.ClientAddressId=RLP_fact.ClientAddressId  " +
					" WHERE RLP_plan.ClientAddressId='" + crm + "'";

            Cursor maiCursor = db.rawQuery(mai,null);
            while (maiCursor.moveToNext()) {

                TextView textview433 = (TextView) findViewById(R.id.textView433); textview433.setText(maiCursor.getString(0));
                TextView textview621 = (TextView) findViewById(R.id.textView621); textview621.setText(maiCursor.getString(1));
                TextView textview622 = (TextView) findViewById(R.id.textView622); textview622.setText(maiCursor.getString(2));
                TextView textview623 = (TextView) findViewById(R.id.textView623); textview623.setText(maiCursor.getString(3));
                TextView textview624 = (TextView) findViewById(R.id.textView624); textview624.setText(maiCursor.getString(4));

                TextView textview436 = (TextView) findViewById(R.id.textView436); textview436.setText(maiCursor.getString(5));
                TextView textview617 = (TextView) findViewById(R.id.textView617); textview617.setText(maiCursor.getString(5));
                TextView textview618 = (TextView) findViewById(R.id.textView618); textview618.setText(maiCursor.getString(5));
                TextView textview619 = (TextView) findViewById(R.id.textView619); textview619.setText(maiCursor.getString(5));
                TextView textview620 = (TextView) findViewById(R.id.textView620); textview620.setText(maiCursor.getString(5));

                TextView textview326 = (TextView) findViewById(R.id.textView326); textview326.setText(maiCursor.getString(6));
                TextView textview625 = (TextView) findViewById(R.id.textView625); textview625.setText(maiCursor.getString(7));
                TextView textview626 = (TextView) findViewById(R.id.textView626); textview626.setText(maiCursor.getString(8));
                TextView textview627 = (TextView) findViewById(R.id.textView627); textview627.setText(maiCursor.getString(9));
                TextView textview628 = (TextView) findViewById(R.id.textView628); textview628.setText(maiCursor.getString(10));

                TextView textview328 = (TextView) findViewById(R.id.textView328); textview328.setText(maiCursor.getString(11));
                TextView textview629 = (TextView) findViewById(R.id.textView629); textview629.setText(maiCursor.getString(12));
                TextView textview630 = (TextView) findViewById(R.id.textView630); textview630.setText(maiCursor.getString(13));
                TextView textview631 = (TextView) findViewById(R.id.textView631); textview631.setText(maiCursor.getString(14));
                TextView textview632 = (TextView) findViewById(R.id.textView632); textview632.setText(maiCursor.getString(15));
            }
            maiCursor.close();

            String june = " SELECT june3,june4,june5,june6,june7, " +
                    " ROUND(june,0)," +
                    " ROUND(june-june3,0) AS june3r,ROUND(june-june4,0) AS june4r,ROUND(june-june5,0) AS june5r,ROUND(june-june6,0) AS june6r,ROUND(june-june7,0) AS june7r, " +
                    " CASE WHEN june-june3 >0 THEN june*0.05 ELSE 0 END AS perc0_5, " +
                    " CASE WHEN june-june4 >0 THEN june*0.01 ELSE 0 END AS perc1_0, " +
                    " CASE WHEN june-june5 >0 THEN june*0.015 ELSE 0 END AS perc1_5, " +
                    " CASE WHEN june-june6 >0 THEN june*0.02 ELSE 0 END AS perc2_0, " +
                    " CASE WHEN june-june7 >0 THEN june*0.025 ELSE 0 END AS perc2_5 " +
                    " FROM RLP_plan  " +
                    " LEFT JOIN RLP_fact  " +
                    " ON RLP_plan.ClientAddressId=RLP_fact.ClientAddressId  " +
                    " WHERE RLP_plan.ClientAddressId='" + crm + "'";

            Cursor juneCursor = db.rawQuery(june,null);
            while (juneCursor.moveToNext()) {

                TextView textview644 = (TextView) findViewById(R.id.textView644); textview644.setText(juneCursor.getString(0));
                TextView textview645 = (TextView) findViewById(R.id.textView645); textview645.setText(juneCursor.getString(1));
                TextView textview646 = (TextView) findViewById(R.id.textView646); textview646.setText(juneCursor.getString(2));
                TextView textview647 = (TextView) findViewById(R.id.textView647); textview647.setText(juneCursor.getString(3));
                TextView textview648 = (TextView) findViewById(R.id.textView648); textview648.setText(juneCursor.getString(4));

                TextView textview650 = (TextView) findViewById(R.id.textView650); textview650.setText(juneCursor.getString(5));
                TextView textview651 = (TextView) findViewById(R.id.textView651); textview651.setText(juneCursor.getString(5));
                TextView textview652 = (TextView) findViewById(R.id.textView652); textview652.setText(juneCursor.getString(5));
                TextView textview653 = (TextView) findViewById(R.id.textView653); textview653.setText(juneCursor.getString(5));
                TextView textview654 = (TextView) findViewById(R.id.textView654); textview654.setText(juneCursor.getString(5));

                TextView textview656 = (TextView) findViewById(R.id.textView656); textview656.setText(juneCursor.getString(6));
                TextView textview657 = (TextView) findViewById(R.id.textView657); textview657.setText(juneCursor.getString(7));
                TextView textview658 = (TextView) findViewById(R.id.textView658); textview658.setText(juneCursor.getString(8));
                TextView textview659 = (TextView) findViewById(R.id.textView659); textview659.setText(juneCursor.getString(9));
                TextView textview660 = (TextView) findViewById(R.id.textView660); textview660.setText(juneCursor.getString(10));

                TextView textview662 = (TextView) findViewById(R.id.textView662); textview662.setText(juneCursor.getString(11));
                TextView textview663 = (TextView) findViewById(R.id.textView663); textview663.setText(juneCursor.getString(12));
                TextView textview664 = (TextView) findViewById(R.id.textView664); textview664.setText(juneCursor.getString(13));
                TextView textview665 = (TextView) findViewById(R.id.textView665); textview665.setText(juneCursor.getString(14));
                TextView textview666 = (TextView) findViewById(R.id.textView666); textview666.setText(juneCursor.getString(15));
            }
            juneCursor.close();

            String july = " SELECT july3,july4,july5,july6,july7, " +
                    " ROUND(july,0)," +
                    " ROUND(july-july3,0) AS july3r,ROUND(july-july4,0) AS july4r,ROUND(july-july5,0) AS july5r,ROUND(july-july6,0) AS july6r,ROUND(july-july7,0) AS july7r, " +
                    " CASE WHEN july-july3 >0 THEN july*0.05 ELSE 0 END AS perc0_5, " +
                    " CASE WHEN july-july4 >0 THEN july*0.01 ELSE 0 END AS perc1_0, " +
                    " CASE WHEN july-july5 >0 THEN july*0.015 ELSE 0 END AS perc1_5, " +
                    " CASE WHEN july-july6 >0 THEN july*0.02 ELSE 0 END AS perc2_0, " +
                    " CASE WHEN july-july7 >0 THEN july*0.025 ELSE 0 END AS perc2_5 " +
                    " FROM RLP_plan  " +
                    " LEFT JOIN RLP_fact  " +
                    " ON RLP_plan.ClientAddressId=RLP_fact.ClientAddressId  " +
                    " WHERE RLP_plan.ClientAddressId='" + crm + "'";
            Log.i("LOG_TAG", "RLP  " +july );
            Cursor julyCursor = db.rawQuery(july,null);
            while (julyCursor.moveToNext()) {

                TextView textview678 = (TextView) findViewById(R.id.textView678); textview678.setText(julyCursor.getString(0));
                TextView textview679 = (TextView) findViewById(R.id.textView679); textview679.setText(julyCursor.getString(1));
                TextView textview680 = (TextView) findViewById(R.id.textView680); textview680.setText(julyCursor.getString(2));
                TextView textview681 = (TextView) findViewById(R.id.textView681); textview681.setText(julyCursor.getString(3));
                TextView textview682 = (TextView) findViewById(R.id.textView682); textview682.setText(julyCursor.getString(4));

                TextView textview684 = (TextView) findViewById(R.id.textView684); textview684.setText(julyCursor.getString(5));
                TextView textview685 = (TextView) findViewById(R.id.textView685); textview685.setText(julyCursor.getString(5));
                TextView textview686 = (TextView) findViewById(R.id.textView686); textview686.setText(julyCursor.getString(5));
                TextView textview687 = (TextView) findViewById(R.id.textView687); textview687.setText(julyCursor.getString(5));
                TextView textview688 = (TextView) findViewById(R.id.textView688); textview688.setText(julyCursor.getString(5));

                TextView textview690 = (TextView) findViewById(R.id.textView690); textview690.setText(julyCursor.getString(6));
                TextView textview691 = (TextView) findViewById(R.id.textView691); textview691.setText(julyCursor.getString(7));
                TextView textview692 = (TextView) findViewById(R.id.textView692); textview692.setText(julyCursor.getString(8));
                TextView textview693 = (TextView) findViewById(R.id.textView693); textview693.setText(julyCursor.getString(9));
                TextView textview694 = (TextView) findViewById(R.id.textView694); textview694.setText(julyCursor.getString(10));

                TextView textview696 = (TextView) findViewById(R.id.textView696); textview696.setText(julyCursor.getString(11));
                TextView textview697 = (TextView) findViewById(R.id.textView697); textview697.setText(julyCursor.getString(12));
                TextView textview698 = (TextView) findViewById(R.id.textView698); textview698.setText(julyCursor.getString(13));
                TextView textview699 = (TextView) findViewById(R.id.textView699); textview699.setText(julyCursor.getString(14));
                TextView textview700 = (TextView) findViewById(R.id.textView700); textview700.setText(julyCursor.getString(15));
            }
            julyCursor.close();

            String august = " SELECT august3,august4,august5,august6,august7, " +
                    " ROUND(august,0)," +
                    " ROUND(august-august3,0) AS august3r,ROUND(august-august4,0) AS august4r,ROUND(august-august5,0) AS august5r,ROUND(august-august6,0) AS august6r,ROUND(august-august7,0) AS august7r, " +
                    " CASE WHEN august-august3 >0 THEN august*0.05 ELSE 0 END AS perc0_5, " +
                    " CASE WHEN august-august4 >0 THEN august*0.01 ELSE 0 END AS perc1_0, " +
                    " CASE WHEN august-august5 >0 THEN august*0.015 ELSE 0 END AS perc1_5, " +
                    " CASE WHEN august-august6 >0 THEN august*0.02 ELSE 0 END AS perc2_0, " +
                    " CASE WHEN august-august7 >0 THEN august*0.025 ELSE 0 END AS perc2_5 " +
                    " FROM RLP_plan  " +
                    " LEFT JOIN RLP_fact  " +
                    " ON RLP_plan.ClientAddressId=RLP_fact.ClientAddressId  " +
                    " WHERE RLP_plan.ClientAddressId='" + crm + "'";

            Cursor augustCursor = db.rawQuery(august,null);
            while (augustCursor.moveToNext()) {

                TextView textview712 = (TextView) findViewById(R.id.textView712); textview712.setText(augustCursor.getString(0));
                TextView textview713 = (TextView) findViewById(R.id.textView713); textview713.setText(augustCursor.getString(1));
                TextView textview714 = (TextView) findViewById(R.id.textView714); textview714.setText(augustCursor.getString(2));
                TextView textview715 = (TextView) findViewById(R.id.textView715); textview715.setText(augustCursor.getString(3));
                TextView textview716 = (TextView) findViewById(R.id.textView716); textview716.setText(augustCursor.getString(4));

                TextView textview718 = (TextView) findViewById(R.id.textView718); textview718.setText(augustCursor.getString(5));
                TextView textview719 = (TextView) findViewById(R.id.textView719); textview719.setText(augustCursor.getString(5));
                TextView textview720 = (TextView) findViewById(R.id.textView720); textview720.setText(augustCursor.getString(5));
                TextView textview721 = (TextView) findViewById(R.id.textView721); textview721.setText(augustCursor.getString(5));
                TextView textview722 = (TextView) findViewById(R.id.textView722); textview722.setText(augustCursor.getString(5));

                TextView textview724 = (TextView) findViewById(R.id.textView724); textview724.setText(augustCursor.getString(6));
                TextView textview725 = (TextView) findViewById(R.id.textView725); textview725.setText(augustCursor.getString(7));
                TextView textview726 = (TextView) findViewById(R.id.textView726); textview726.setText(augustCursor.getString(8));
                TextView textview727 = (TextView) findViewById(R.id.textView727); textview727.setText(augustCursor.getString(9));
                TextView textview728 = (TextView) findViewById(R.id.textView728); textview728.setText(augustCursor.getString(10));

                TextView textview730 = (TextView) findViewById(R.id.textView730); textview730.setText(augustCursor.getString(11));
                TextView textview731 = (TextView) findViewById(R.id.textView731); textview731.setText(augustCursor.getString(12));
                TextView textview732 = (TextView) findViewById(R.id.textView732); textview732.setText(augustCursor.getString(13));
                TextView textview733 = (TextView) findViewById(R.id.textView733); textview733.setText(augustCursor.getString(14));
                TextView textview734 = (TextView) findViewById(R.id.textView734); textview734.setText(augustCursor.getString(15));
            }
            augustCursor.close();

            String september = " SELECT september3,september4,september5,september6,september7, " +
                    " ROUND(september,0)," +
                    " ROUND(september-september3,0) AS september3r,ROUND(september-september4,0) AS september4r,ROUND(september-september5,0) AS september5r,ROUND(september-september6,0) AS september6r,ROUND(september-september7,0) AS september7r, " +
                    " CASE WHEN september-september3 >0 THEN september*0.05 ELSE 0 END AS perc0_5, " +
                    " CASE WHEN september-september4 >0 THEN september*0.01 ELSE 0 END AS perc1_0, " +
                    " CASE WHEN september-september5 >0 THEN september*0.015 ELSE 0 END AS perc1_5, " +
                    " CASE WHEN september-september6 >0 THEN september*0.02 ELSE 0 END AS perc2_0, " +
                    " CASE WHEN september-september7 >0 THEN september*0.025 ELSE 0 END AS perc2_5 " +
                    " FROM RLP_plan  " +
                    " LEFT JOIN RLP_fact  " +
                    " ON RLP_plan.ClientAddressId=RLP_fact.ClientAddressId  " +
                    " WHERE RLP_plan.ClientAddressId='" + crm + "'";

            Cursor septemberCursor = db.rawQuery(september,null);
            while (septemberCursor.moveToNext()) {

                TextView textview746 = (TextView) findViewById(R.id.textView746); textview746.setText(septemberCursor.getString(0));
                TextView textview747 = (TextView) findViewById(R.id.textView747); textview747.setText(septemberCursor.getString(1));
                TextView textview748 = (TextView) findViewById(R.id.textView748); textview748.setText(septemberCursor.getString(2));
                TextView textview749 = (TextView) findViewById(R.id.textView749); textview749.setText(septemberCursor.getString(3));
                TextView textview750 = (TextView) findViewById(R.id.textView750); textview750.setText(septemberCursor.getString(4));

                TextView textview752 = (TextView) findViewById(R.id.textView752); textview752.setText(septemberCursor.getString(5));
                TextView textview753 = (TextView) findViewById(R.id.textView753); textview753.setText(septemberCursor.getString(5));
                TextView textview754 = (TextView) findViewById(R.id.textView754); textview754.setText(septemberCursor.getString(5));
                TextView textview755 = (TextView) findViewById(R.id.textView755); textview755.setText(septemberCursor.getString(5));
                TextView textview756 = (TextView) findViewById(R.id.textView756); textview756.setText(septemberCursor.getString(5));

                TextView textview758 = (TextView) findViewById(R.id.textView758); textview758.setText(septemberCursor.getString(6));
                TextView textview759 = (TextView) findViewById(R.id.textView759); textview759.setText(septemberCursor.getString(7));
                TextView textview760 = (TextView) findViewById(R.id.textView760); textview760.setText(septemberCursor.getString(8));
                TextView textview761 = (TextView) findViewById(R.id.textView761); textview761.setText(septemberCursor.getString(9));
                TextView textview762 = (TextView) findViewById(R.id.textView762); textview762.setText(septemberCursor.getString(10));

                TextView textview764 = (TextView) findViewById(R.id.textView764); textview764.setText(septemberCursor.getString(11));
                TextView textview765 = (TextView) findViewById(R.id.textView765); textview765.setText(septemberCursor.getString(12));
                TextView textview766 = (TextView) findViewById(R.id.textView766); textview766.setText(septemberCursor.getString(13));
                TextView textview767 = (TextView) findViewById(R.id.textView767); textview767.setText(septemberCursor.getString(14));
                TextView textview768 = (TextView) findViewById(R.id.textView768); textview768.setText(septemberCursor.getString(15));
            }
            septemberCursor.close();

            String superr = " SELECT super0_5,super1_0,super1_5,super2_0,super2_5, " +
                    " ROUND(super,0)," +
                    " ROUND(super-super0_5,0) AS super3r,ROUND(super-super1_0,0) AS super4r,ROUND(super-super1_5,0) AS super5r,ROUND(super-super2_0,0) AS super6r,ROUND(super-super2_5,0) AS super7r, " +
                    " CASE WHEN super-super0_5 >0 THEN super*0.05 ELSE 0 END AS perc0_5, " +
                    " CASE WHEN super-super1_0 >0 THEN super*0.01 ELSE 0 END AS perc1_0, " +
                    " CASE WHEN super-super1_5 >0 THEN super*0.015 ELSE 0 END AS perc1_5, " +
                    " CASE WHEN super-super2_0 >0 THEN super*0.02 ELSE 0 END AS perc2_0, " +
                    " CASE WHEN super-super2_5 >0 THEN super*0.025 ELSE 0 END AS perc2_5 " +
                    " FROM RLP_plan  " +
                    " LEFT JOIN RLP_fact  " +
                    " ON RLP_plan.ClientAddressId=RLP_fact.ClientAddressId  " +
                    " WHERE RLP_plan.ClientAddressId='" + crm + "'";

            Cursor superCursor = db.rawQuery(superr,null);
            while (superCursor.moveToNext()) {

                TextView textview780 = (TextView) findViewById(R.id.textView780); textview780.setText(superCursor.getString(0));
                TextView textview781 = (TextView) findViewById(R.id.textView781); textview781.setText(superCursor.getString(1));
                TextView textview782 = (TextView) findViewById(R.id.textView782); textview782.setText(superCursor.getString(2));
                TextView textview783 = (TextView) findViewById(R.id.textView783); textview783.setText(superCursor.getString(3));
                TextView textview784 = (TextView) findViewById(R.id.textView784); textview784.setText(superCursor.getString(4));

                TextView textview786 = (TextView) findViewById(R.id.textView786); textview786.setText(superCursor.getString(5));
                TextView textview787 = (TextView) findViewById(R.id.textView787); textview787.setText(superCursor.getString(5));
                TextView textview788 = (TextView) findViewById(R.id.textView788); textview788.setText(superCursor.getString(5));
                TextView textview789 = (TextView) findViewById(R.id.textView789); textview789.setText(superCursor.getString(5));
                TextView textview790 = (TextView) findViewById(R.id.textView790); textview790.setText(superCursor.getString(5));

                TextView textview792 = (TextView) findViewById(R.id.textView792); textview792.setText(superCursor.getString(6));
                TextView textview793 = (TextView) findViewById(R.id.textView793); textview793.setText(superCursor.getString(7));
                TextView textview794 = (TextView) findViewById(R.id.textView794); textview794.setText(superCursor.getString(8));
                TextView textview795 = (TextView) findViewById(R.id.textView795); textview795.setText(superCursor.getString(9));
                TextView textview796 = (TextView) findViewById(R.id.textView796); textview796.setText(superCursor.getString(10));

                TextView textview798 = (TextView) findViewById(R.id.textView798); textview798.setText(superCursor.getString(11));
                TextView textview799 = (TextView) findViewById(R.id.textView799); textview799.setText(superCursor.getString(12));
                TextView textview800 = (TextView) findViewById(R.id.textView800); textview800.setText(superCursor.getString(13));
                TextView textview801 = (TextView) findViewById(R.id.textView801); textview801.setText(superCursor.getString(14));
                TextView textview802 = (TextView) findViewById(R.id.textView802); textview802.setText(superCursor.getString(15));
            }
            superCursor.close();

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
