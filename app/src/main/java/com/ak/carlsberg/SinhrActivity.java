package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SinhrActivity extends AppCompatActivity {

    public static String DB_PATH,crmcode;
    private static final String DB_NAME = "monolit.db";
    private static final String DB_Carlsberg = "carlsberg.db";
    final String DIR_SD_CARLSBERG = "Carlsberg/Database";
    SharedPreferences sp;
    SQLiteDatabase db;
    SimpleAdapter adapterList;
    Button send,button105;
    String url,type;
    String thisday,person,response,rezreq,firstday,todaydate;
    RequestSend rqs;
    TextView bomba,visitss,debett,plann,mhll,planogrm,szz,bm,pl,brendsm,bp,reckstocks;
    ProgressDialog progressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sinhr);

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
        todaydate = getFullTime(bCalendar.getTimeInMillis());

        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(fonts);
            actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        }

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /***********************определяем код сотрудника*************************/
            String PersonQuerry="SELECT BaseRegionTreeId FROM Pars";
            Cursor cursorPerson = db.rawQuery(PersonQuerry,null);
            while (cursorPerson.moveToNext()) {
                person = cursorPerson.getString(0);
            }
            cursorPerson.close();
            person = person.substring(2);
            Log.i("LOG_TAG", person);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
            db.close();
        }

        bomba= (TextView) findViewById(R.id.textView541);
        visitss= (TextView) findViewById(R.id.textView536);
        reckstocks= (TextView) findViewById(R.id.textView610);
        debett= (TextView) findViewById(R.id.textView543);
        plann= (TextView) findViewById(R.id.textView545);
        mhll= (TextView) findViewById(R.id.textView539);
        planogrm = (TextView) findViewById(R.id.textView549);
        brendsm = (TextView) findViewById(R.id.textView537);
        szz = (TextView) findViewById(R.id.textView547);
        bm = (TextView) findViewById(R.id.textView554);
        bp = (TextView) findViewById(R.id.textView589);
        pl = (TextView) findViewById(R.id.textView556);

        button105 = (Button) findViewById(R.id.button105);

        View.OnClickListener senddata = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button105.setEnabled(false);
                if (isNetworkAvailable()==false) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет интернет соединения", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SinhrActivity.this);
                    builder.setCancelable(false);
                    LinearLayout view1 = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.dialog_send_request, null);
                    builder.setView(view1);
                    builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            rqs = new RequestSend();
                            rqs.execute();
                        }
                    });
                    builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            }
        };
        button105.setOnClickListener(senddata);


         progressDialog = new ProgressDialog(this);
        // TODO Auto-generated method stub
    }

    class RequestSend extends AsyncTask<Void, Void, Void> {
String plan,sz;int bm1,pl1,bp1;
        String[] bombs = new String[2];
        String[] planogr = new String[2];
        String[] brends = new String[2];
        String[] visits = new String[2];
        String[] reckstock = new String[2];
        String[] debet = new String[2];
        String[] mhl = new String[2];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String text = "Идет синхронизация, ждите ...";
            progressDialog.setMessage( text);
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog
                    .setProgressStyle(ProgressDialog.STYLE_SPINNER);

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            bombs = sendbombaData().split(";");
            planogr = sendplanogramData().split(";");
            brends = sendbrendsData().split(";");
            visits = sendvisits().split(";");
            reckstock = sendreckstock().split(";");
            debet =senddebet().split(";");
            plan =sendplan();
            mhl=sendmhl().split(";");

            uploadseazon();
            uploadpointplan();
            uploadbrendPrioritets();
            uploadprosrock();
            updateMhlMatrix();
            uploadRLPplan();
            uploadRLPfact();
            sz= uploadsz();
            bm1 = uploadBombs();
            bp1 = uploadBrends();
            pl1 = uploadplanograms();
            uploadVisitNotExist();
            uploadReckstockNotExist();
            uploadDebetNotExist();
            uploadPlanogramsNotExist();
            uploadBombsNotExist();
            uploadBrendNotExist();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            bomba.setText("Отправлено " + bombs[0] + " из " + bombs[1]);
            planogrm.setText("Отправлено "+planogr[0]+" из " +planogr[1]);
            brendsm.setText("Отправлено "+brends[0]+" из " +brends[1]);
            visitss.setText("Отправлено "+visits[0]+" из " +visits[1]);
            reckstocks.setText("Отправлено "+reckstock[0]+" из " +reckstock[1]);
            debett.setText("Отправлено "+debet[0]+" из " +debet[1]);
            plann.setText(plan);
            mhll.setText("Отправлено "+mhl[0]+" из " +mhl[1]);
            szz.setText(sz);
            bm.setText(bm1 + " шт");
            bp.setText(bp1 + " шт");
            pl.setText(pl1 + " шт");
            button105.setEnabled(true);
            progressDialog.hide();
            AlertDialog.Builder builder = new AlertDialog.Builder(SinhrActivity.this);
            builder.setCancelable(false);
            LinearLayout view1 = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.dialog_send_request, null);
            TextView view = (TextView) view1.findViewById(R.id.textView85);
            view.setText("Синхронизация закончена");
            builder.setView(view1);
            builder.setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();

                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public String sendvisits(){
        rezreq =null;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************отправка визитов************************************/
            int k;int i=0;
            String querySend = "SELECT PlanDate,ClientAddressId,Distance,Confirmation,Equipment FROM VisitsSalary WHERE Send =0";
            Cursor cursor = db.rawQuery(querySend, null);
            k= cursor.getCount();
            Log.i("LOG_TAG", "Кол-во записей " + k);
            if (k==0){
                rezreq = "0;0";
            }
            else {
                JSONObject jsonObject = new JSONObject();
                String allVisits ="";
                while (cursor.moveToNext()) {

                    allVisits = allVisits+ ",{\"person\":\"" +person+ "\",\"PlanDate\":\""+ cursor.getString(0)+"\",\"ClientAddressId\":\""+ cursor.getString(1)+"\",\"Distance\":\""+ cursor.getString(2)+"\",\"Confirmation\":\""+ cursor.getString(3)+"\",\"Equipment\":\""+ cursor.getString(4)+"\"}";
                }

                try{
                    allVisits="["+allVisits.substring(1)+"]";
                    JSONArray jsonArray = new JSONArray(allVisits);
                    jsonObject.put("visits", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String json = jsonObject.toString();
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost http = new HttpPost("http://carlsberg.esy.es/sendquerry/visitsupload.php");
                    StringEntity se = new StringEntity("json="+json.toString());
                    http.addHeader("content-type", "application/x-www-form-urlencoded");
                    http.setEntity(se);

                    response = (String) httpclient.execute(http, new BasicResponseHandler());
                    Log.i("LOG_TAG", "Запрос " + response);

                    if (response.contains("data_send")) {
                        String[] cnt = new String[2];
                        cnt = response.split(";");
                        i =Integer.parseInt(cnt[1]);
                        String queryU = "UPDATE VisitsSalary SET Send='1' WHERE PlanDate < date('"+thisday+"','-4 day')";
                        db.execSQL(queryU);
                    }

                rezreq = i+";"+k;
            }
            cursor.close();

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    public String sendreckstock(){
        rezreq =null;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************отправка визитов************************************/
            int k;int i=0;
            String querySend = "SELECT PlanDate,ClientAddresId,StockPlan,StockFact,Rekplan,Rekfact,Confirmation FROM RekStock WHERE Send =0";
            Cursor cursor = db.rawQuery(querySend, null);
            k= cursor.getCount();
            Log.i("LOG_TAG", "Кол-во записей " + k);
            if (k==0){
                rezreq = "0;0";
            }
            else {
                JSONObject jsonObject = new JSONObject();
                String allVisits ="";
                while (cursor.moveToNext()) {

                    allVisits = allVisits+ ",{\"person\":\"" +person+ "\",\"PlanDate\":\""+ cursor.getString(0)+"\",\"ClientAddressId\":\""+ cursor.getString(1)+"\",\"StockPlan\":\""+ cursor.getString(2)+"\",\"StockFact\":\""+ cursor.getString(3)+"\",\"Rekplan\":\""+ cursor.getString(4)+"\",\"Rekfact\":\""+ cursor.getString(5)+"\",\"Confirmation\":\""+ cursor.getString(6)+"\"}";
                }

                try{
                    allVisits="["+allVisits.substring(1)+"]";
                    JSONArray jsonArray = new JSONArray(allVisits);
                    jsonObject.put("reckstock", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String json = jsonObject.toString();
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost http = new HttpPost("http://carlsberg.esy.es/sendquerry/reckstockupload.php");
                StringEntity se = new StringEntity("json="+json.toString());
                http.addHeader("content-type", "application/x-www-form-urlencoded");
                http.setEntity(se);

                response = (String) httpclient.execute(http, new BasicResponseHandler());
                Log.i("LOG_TAG", "Остатки " + response);

                if (response.contains("data_send")) {
                    String[] cnt = new String[2];
                    cnt = response.split(";");
                    i =Integer.parseInt(cnt[1]);
                    String queryU = "UPDATE RekStock SET Send='1' WHERE PlanDate < date('"+thisday+"','-3 day')";
                    db.execSQL(queryU);
                }

                rezreq = i+";"+k;
            }
            cursor.close();

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
/*******************************отправка дебета**************************************************/
    public String senddebet(){
        rezreq =null;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************отправка визитов************************************/
            int k;int i=0;
            String querySend = "SELECT Date,TotalBalance,OverdueBalance,DebetPercent FROM DebetStatus WHERE Send =0";
            Cursor cursor = db.rawQuery(querySend, null);
            k= cursor.getCount();
            if (k==0){
                rezreq = "0;0";

            }
            else {

                while (cursor.moveToNext()) {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost http = new HttpPost("http://carlsberg.esy.es/sendquerry/debet.php");
                    List nameValuePairs = new ArrayList();
                    nameValuePairs.add(new BasicNameValuePair("person", person));
                    nameValuePairs.add(new BasicNameValuePair("Date", cursor.getString(0)));
                    nameValuePairs.add(new BasicNameValuePair("TotalBalance", cursor.getString(1)));
                    nameValuePairs.add(new BasicNameValuePair("OverdueBalance", cursor.getString(2)));
                    nameValuePairs.add(new BasicNameValuePair("DebetPercent", cursor.getString(3)));

                    http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
                    response = (String) httpclient.execute(http, new BasicResponseHandler());
                    Log.i("LOG_TAG", "Отправка дебета " + response);
                    if (response.equals("data_send")) {
                        i = i+1;
                        String queryU = "UPDATE DebetStatus SET Send='1' WHERE Date ='" + cursor.getString(0) + "' AND TotalBalance ='" +cursor.getString(1)+"' AND OverdueBalance ='" +cursor.getString(2)+"' AND date('"+cursor.getString(0)+"') < date('"+thisday+"','-4 day')";
                        db.execSQL(queryU);
                        Log.i("LOG_TAG", "i = " + i + " k= " + k);
                    }

                }

                rezreq = i+";"+k;
                Log.i("LOG_TAG", "Отправка дебета " + rezreq);
            }
            cursor.close();

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    /*******************************отправка Плана**************************************************/
    public String sendplan(){
        rezreq =null;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();

            /******************************отправка визитов************************************/
            String Eplan ="SELECT TargetValue FROM TaskReportView WHERE TaskName ='Продажи в дал (пиво)' ";
            Cursor cursorPlan = db.rawQuery(Eplan, null);
            float tplan =0;
            while (cursorPlan.moveToNext()) {
                tplan = Float.parseFloat(cursorPlan.getString(0));
                Log.i("LOG_TAG", "План  " +cursorPlan.getString(0));
            }
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
            float tfact = 0;
            while (cursorFact.moveToNext()) {
                if (cursorFact.isNull(0) ) { tfact =0;}
                else {
                    tfact = Float.parseFloat(cursorFact.getString(0));
                }
            }
            cursorFact.close();

            double percent;
            if (tplan !=0.0) {
                percent = BigDecimal.valueOf((tfact / tplan) * 100).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
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
            }

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/sendquerry/plan.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));
            nameValuePairs.add(new BasicNameValuePair("Date", thisday));
            nameValuePairs.add(new BasicNameValuePair("Plan", String.valueOf(tplan)));
            nameValuePairs.add(new BasicNameValuePair("Fact", String.valueOf(tfact)));
            nameValuePairs.add(new BasicNameValuePair("Percent", String.valueOf(percent)));
            nameValuePairs.add(new BasicNameValuePair("Bablo", String.valueOf(execution)));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            Log.i("LOG_TAG", "Запрос " + response);
            if (response.equals("data_send")) {
                rezreq = String.valueOf("Данные отправлены");
            }
            else{
                rezreq = String.valueOf("Попробуйте еще раз");
            }


            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
      /**************************************MHL***********************************************/
    public String sendmhl(){
        rezreq=null;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************отправка визитов************************************/
            int k;int i=0;
            String querySend = "SELECT ClientAddressId,MhlExec,PointType FROM MhlFact ";
            Cursor cursor = db.rawQuery(querySend, null);
            k= cursor.getCount();
            if (k==0){
                rezreq = "0;0";
            }
            else {

                JSONObject jsonObject = new JSONObject();
                String mhlAll ="";
                while (cursor.moveToNext()) {

                    mhlAll = mhlAll+ ",{\"person\":\"" +person+ "\",\"Date\":\""+ firstday+"\",\"ClientAddressId\":\""+ cursor.getString(0)+"\",\"MhlExec\":\""+ cursor.getString(1)+"\",\"PointType\":\""+ cursor.getString(2)+"\"}";
                }

                try{
                    mhlAll="["+mhlAll.substring(1)+"]";
                    JSONArray jsonArray = new JSONArray(mhlAll);
                    jsonObject.put("mhlAll", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String json = jsonObject.toString();
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost http = new HttpPost("http://carlsberg.esy.es/sendquerry/mhlupload.php");
                StringEntity se = new StringEntity("json="+json.toString());
                http.addHeader("content-type", "application/x-www-form-urlencoded");
                http.setEntity(se);

                response = (String) httpclient.execute(http, new BasicResponseHandler());
                Log.i("LOG_TAG", "Запрос " + response);

                if (response.contains("data_send")) {
                    String[] cnt = new String[2];
                    cnt = response.split(";");
                    i =Integer.parseInt(cnt[1]);
                }

                rezreq = i+";"+k;

            }
            cursor.close();

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    /*********************************************************************************************/
    public String uploadsz(){
        rezreq=null;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем спец задачи************************************/

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/szupload.php");
                    List nameValuePairs = new ArrayList();
                    nameValuePairs.add(new BasicNameValuePair("person", person));

                    http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
                    response = (String) httpclient.execute(http, new BasicResponseHandler());
                    Log.i("LOG_TAG", "Запрос " + response);
                    String[] insert = response.split(";");
                    if (insert[0].equals("data_get")) {
                        db.execSQL("REPLACE INTO Sz (sz1,sz2) VALUES ('" + insert[1] + "','" + insert[2] + "')");
                        rezreq =String.valueOf("Задачи обновлены");
                    }
            else {
                        rezreq =String.valueOf("Ошибка обновления");
                    }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    /*********************************************************************************************/
    public String uploadseazon(){
        response =null;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем спец задачи************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/seazonupload.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            Log.i("LOG_TAG", "Запрос " + response);
            db.execSQL("DELETE FROM sales_day_plan");
                String[] insert = response.split(";");
                    db.execSQL("REPLACE INTO sales_day_plan (id,pn,vt,sr,ct,pt) VALUES ('1','" + insert[1] + "','" + insert[2] + "','" + insert[3] + "','" + insert[4] + "','" + insert[5] + "')");
                    rezreq = String.valueOf(getText(R.string.justsen));

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    /*********************************************************************************************/
    public String uploadRLPplan(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/rlpplan.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
                JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("ProductsData");
                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        String oneObjectsItem = oneObject.getString("ClientAddressId");
                        String oneObjectsItem2 = oneObject.getString("mai3");
                        String oneObjectsItem3 = oneObject.getString("mai4");
                        String oneObjectsItem4 = oneObject.getString("mai5");
                        String oneObjectsItem5 = oneObject.getString("mai6");
                        String oneObjectsItem6 = oneObject.getString("mai7");
                        String oneObjectsItem7 = oneObject.getString("june3");
                        String oneObjectsItem8 = oneObject.getString("june4");
                        String oneObjectsItem9 = oneObject.getString("june5");
                        String oneObjectsItem10 = oneObject.getString("june6");
                        String oneObjectsItem11 = oneObject.getString("june7");
                        String oneObjectsItem12 = oneObject.getString("july3");
                        String oneObjectsItem13 = oneObject.getString("july4");
                        String oneObjectsItem14 = oneObject.getString("july5");
                        String oneObjectsItem15 = oneObject.getString("july6");
                        String oneObjectsItem16 = oneObject.getString("july7");
                        String oneObjectsItem17 = oneObject.getString("august3");
                        String oneObjectsItem18 = oneObject.getString("august4");
                        String oneObjectsItem19 = oneObject.getString("august5");
                        String oneObjectsItem20 = oneObject.getString("august6");
                        String oneObjectsItem21 = oneObject.getString("august7");
                        String oneObjectsItem22 = oneObject.getString("september3");
                        String oneObjectsItem23 = oneObject.getString("september4");
                        String oneObjectsItem24 = oneObject.getString("september5");
                        String oneObjectsItem25 = oneObject.getString("september6");
                        String oneObjectsItem26 = oneObject.getString("september7");
                        String oneObjectsItem27 = oneObject.getString("super0_5");
                        String oneObjectsItem28 = oneObject.getString("super1_0");
                        String oneObjectsItem29 = oneObject.getString("super1_5");
                        String oneObjectsItem30 = oneObject.getString("super2_0");
                        String oneObjectsItem31 = oneObject.getString("super2_5");



                           db.execSQL("REPLACE INTO RLP_plan (ClientAddressId,mai3,mai4,mai5,mai6,mai7,june3,june4,june5,june6,june7,july3,july4,july5,july6,july7,august3,august4,august5,august6,august7,september3,september4,september5,september6,september7,super0_5,super1_0,super1_5,super2_0,super2_5) " +
                                   " VALUES ('" + oneObjectsItem + "','" + oneObjectsItem2 + "','" + oneObjectsItem3 + "','" + oneObjectsItem4 + "','" + oneObjectsItem5 + "','" + oneObjectsItem6 + "','" + oneObjectsItem7 + "','" + oneObjectsItem8 + "','" + oneObjectsItem9 + "','" + oneObjectsItem10 + "','" + oneObjectsItem11 + "','" + oneObjectsItem12 + "'" +
                                   ",'" + oneObjectsItem13 + "','" + oneObjectsItem14 + "','" + oneObjectsItem15 + "','" + oneObjectsItem16 + "','" + oneObjectsItem17 + "','" + oneObjectsItem18 + "','" + oneObjectsItem19 + "','" + oneObjectsItem20 + "','" + oneObjectsItem21 + "'" +
                                   " ,'" + oneObjectsItem22 + "','" + oneObjectsItem23 + "','" + oneObjectsItem24 + "','" + oneObjectsItem25 + "','" + oneObjectsItem26 + "','" + oneObjectsItem27 + "','" + oneObjectsItem28 + "','" + oneObjectsItem29 + "','" + oneObjectsItem30 + "','" + oneObjectsItem31 + "')");
                    } catch (JSONException e) {
                        // Oops
                    }
                }
            rezreq =String.valueOf(getText(R.string.justsen));

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    /*********************************************************************************************/
    public String uploadRLPfact(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/rlpfact.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("ProductsData");
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String oneObjectsItem = oneObject.getString("ClientAddressId");
                    String oneObjectsItem2 = oneObject.getString("mai");
                    String oneObjectsItem3 = oneObject.getString("june");
                    String oneObjectsItem4 = oneObject.getString("july");
                    String oneObjectsItem5 = oneObject.getString("august");
                    String oneObjectsItem6 = oneObject.getString("september");
                    String oneObjectsItem7 = oneObject.getString("super");

                    db.execSQL("REPLACE INTO RLP_fact (ClientAddressId,mai,june,july,august,september,super) VALUES ('" + oneObjectsItem + "','" + oneObjectsItem2 + "','" + oneObjectsItem3 + "','" + oneObjectsItem4 + "','" + oneObjectsItem5 + "','" + oneObjectsItem6 + "','" + oneObjectsItem7 + "')");
                } catch (JSONException e) {
                    // Oops
                }
            }
            rezreq =String.valueOf(getText(R.string.justsen));

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    /*********************************************************************************************/
    public String uploadpointplan(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/pointplanupload.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("ProductsData");
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String oneObjectsItem = oneObject.getString("crm");
                    String oneObjectsItem2 = oneObject.getString("percent");
                    db.execSQL("REPLACE INTO point_sales_plan (crm,percent) VALUES ('" + oneObjectsItem + "','" + oneObjectsItem2 + "')");
                } catch (JSONException e) {
                    // Oops
                }
            }
            rezreq =String.valueOf(getText(R.string.justsen));

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    /*********************************************************************************************/
    public String uploadbrendPrioritets(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/brendprioritetsupload.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            Log.i("LOG_TAG", "Запрос " + response);
            JSONObject jObject = new JSONObject(response);
            db.execSQL("DELETE FROM BrendPrioritets");
            JSONArray jArray = jObject.getJSONArray("ProductsData");
            for (int i=0; i < jArray.length(); i++)
            {
                try {

                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String oneObjectsItem = oneObject.getString("name");
                    String oneObjectsItem1 = oneObject.getString("price");
                    String oneObjectsItem2 = oneObject.getString("type");
                    String oneObjectsItem3 = oneObject.getString("number");
                    db.execSQL("INSERT INTO BrendPrioritets (name,price,type,number) VALUES ('" + oneObjectsItem + "','" + oneObjectsItem1 + "','" + oneObjectsItem2 + "','" + oneObjectsItem3 + "')");
                } catch (JSONException e) {
                    // Oops
                }
            }
            rezreq =String.valueOf(getText(R.string.justsen));

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    /*********************************************************************************************/
    public String updateMhlMatrix(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/matrixupload.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("ProductsData");
            db.execSQL("DELETE FROM Mhl");
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String oneObjectsItem = oneObject.getString("UnionGrup");String oneObjectsItem2 = oneObject.getString("SkuName");
                    String oneObjectsItem3 = oneObject.getString("SkuCode");String oneObjectsItem4 = oneObject.getString("SabbrendName");
                    String oneObjectsItem5 = oneObject.getString("territory");String oneObjectsItem6 = oneObject.getString("freeze");
                    String oneObjectsItem7 = oneObject.getString("rozliv");String oneObjectsItem8 = oneObject.getString("TT11");
                    String oneObjectsItem9 = oneObject.getString("TT12");String oneObjectsItem10 = oneObject.getString("TT21");
                    String oneObjectsItem11 = oneObject.getString("TT31");String oneObjectsItem12 = oneObject.getString("TT32");
                    String oneObjectsItem13 = oneObject.getString("TT33");String oneObjectsItem14 = oneObject.getString("TT41");
                    String oneObjectsItem15 = oneObject.getString("TT51");String oneObjectsItem16 = oneObject.getString("TT61");
                    String oneObjectsItem17 = oneObject.getString("TT71");String oneObjectsItem18 = oneObject.getString("TT81");
                    String oneObjectsItem19 = oneObject.getString("MT91");String oneObjectsItem20 = oneObject.getString("MT101");
                    String oneObjectsItem21 = oneObject.getString("MT111");String oneObjectsItem22 = oneObject.getString("MT112");
                    String oneObjectsItem23 = oneObject.getString("MT113");String oneObjectsItem24 = oneObject.getString("MT114");
                    String oneObjectsItem25 = oneObject.getString("MT121");String oneObjectsItem26 = oneObject.getString("MT122");
                    String oneObjectsItem27 = oneObject.getString("MT123");String oneObjectsItem28 = oneObject.getString("MT124");
                    String oneObjectsItem29 = oneObject.getString("MT131");String oneObjectsItem30 = oneObject.getString("MT132");
                    String oneObjectsItem31 = oneObject.getString("MT133");String oneObjectsItem32 = oneObject.getString("MT134");
                    String oneObjectsItem33 = oneObject.getString("MT141");String oneObjectsItem34 = oneObject.getString("OT161");
                    String oneObjectsItem35 = oneObject.getString("OT162");String oneObjectsItem36 = oneObject.getString("OT171");
                    String oneObjectsItem37 = oneObject.getString("OT172");String oneObjectsItem38 = oneObject.getString("OT173");
                    String oneObjectsItem39 = oneObject.getString("OT174");String oneObjectsItem40 = oneObject.getString("OT175");
                    String oneObjectsItem41 = oneObject.getString("OT176");String oneObjectsItem42 = oneObject.getString("OT177");
                    String oneObjectsItem43 = oneObject.getString("OT178");String oneObjectsItem44 = oneObject.getString("OT179");
                    String oneObjectsItem45 = oneObject.getString("OT181");String oneObjectsItem46 = oneObject.getString("OT182");
                    String oneObjectsItem47 = oneObject.getString("OT183");String oneObjectsItem48 = oneObject.getString("OT191");
                    String oneObjectsItem49 = oneObject.getString("OT192");String oneObjectsItem50 = oneObject.getString("OT193");
                    String oneObjectsItem51 = oneObject.getString("OT194");String oneObjectsItem52 = oneObject.getString("OT195");
                    String oneObjectsItem53 = oneObject.getString("OT196");String oneObjectsItem54 = oneObject.getString("OT197");
                    String oneObjectsItem55 = oneObject.getString("OT198");String oneObjectsItem56 = oneObject.getString("OT199");
                    String oneObjectsItem57 = oneObject.getString("OT201");String oneObjectsItem58 = oneObject.getString("OT202");
                    String oneObjectsItem59 = oneObject.getString("OT203");String oneObjectsItem60 = oneObject.getString("OT204");
                    String oneObjectsItem61 = oneObject.getString("OT205");

                    db.execSQL("INSERT INTO Mhl " +
                                    " (UnionGrup,SkuName,SkuCode,SabbrendName,territory,freeze,rozliv,TT11,TT12,TT21,TT31,TT32,TT33,TT41,TT51,TT61,TT71,TT81,MT91,MT101,MT111,MT112," +
                                    " MT113,MT114,MT121,MT122,MT123,MT124,MT131,MT132,MT133,MT134,MT141,OT161,OT162,OT171,OT172,OT173,OT174,OT175,OT176,OT177,OT178,OT179,OT181,OT182," +
                                    " OT183,OT191,OT192,OT193,OT194,OT195,OT196,OT197,OT198,OT199,OT201,OT202,OT203,OT204,OT205) "+
                                    " VALUES ('" + oneObjectsItem + "','" + oneObjectsItem2 + "','" + oneObjectsItem3 + "','" + oneObjectsItem4 + "'" +
                            ",'" + oneObjectsItem5 + "','" + oneObjectsItem6 + "','" + oneObjectsItem7 + "','" + oneObjectsItem8 + "'" +
                            ",'" + oneObjectsItem9 + "','" + oneObjectsItem10 + "','" + oneObjectsItem11 + "','" + oneObjectsItem12 + "'" +
                            ",'" + oneObjectsItem13 + "','" + oneObjectsItem14 + "','" + oneObjectsItem15 + "','" + oneObjectsItem16 + "'" +
                            ",'" + oneObjectsItem17 + "','" + oneObjectsItem18 + "','" + oneObjectsItem19 + "','" + oneObjectsItem20 + "'" +
                            ",'" + oneObjectsItem21 + "','" + oneObjectsItem22 + "','" + oneObjectsItem23 + "','" + oneObjectsItem24 + "'" +
                            ",'" + oneObjectsItem25 + "','" + oneObjectsItem26 + "','" + oneObjectsItem27 + "','" + oneObjectsItem28 + "'" +
                            ",'" + oneObjectsItem29 + "','" + oneObjectsItem30 + "','" + oneObjectsItem31 + "','" + oneObjectsItem32 + "'" +
                            ",'" + oneObjectsItem33 + "','" + oneObjectsItem34 + "','" + oneObjectsItem35 + "','" + oneObjectsItem36 + "'" +
                            ",'" + oneObjectsItem37 + "','" + oneObjectsItem38 + "','" + oneObjectsItem39 + "','" + oneObjectsItem40 + "'" +
                            ",'" + oneObjectsItem41 + "','" + oneObjectsItem42 + "','" + oneObjectsItem43 + "','" + oneObjectsItem44 + "'" +
                            ",'" + oneObjectsItem45 + "','" + oneObjectsItem46 + "','" + oneObjectsItem47 + "','" + oneObjectsItem48 + "'" +
                            ",'" + oneObjectsItem49 + "','" + oneObjectsItem50 + "','" + oneObjectsItem51 + "','" + oneObjectsItem52 + "'" +
                            ",'" + oneObjectsItem53 + "','" + oneObjectsItem54 + "','" + oneObjectsItem55 + "','" + oneObjectsItem56 + "'" +
                            ",'" + oneObjectsItem57 + "','" + oneObjectsItem58 + "','" + oneObjectsItem59 + "','" + oneObjectsItem60 + "'" +
                            ",'" + oneObjectsItem61 + "')");
                } catch (JSONException e) {
                    // Oops
                }
            }
            rezreq =String.valueOf(getText(R.string.justsen));

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    /*********************************************************************************************/
    public String uploadprosrock(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/prosrock.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            Log.i("LOG_TAG", "Запрос просрок " + response);
            JSONObject jObject = new JSONObject(response);
            db.execSQL("DELETE FROM predprosro");
            JSONArray jArray = jObject.getJSONArray("ProductsData");
            for (int i=0; i < jArray.length(); i++)
            {
                try {

                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String oneObjectsItem = oneObject.getString("skucode");
                    String oneObjectsItem1 = oneObject.getString("number");
                    db.execSQL("INSERT INTO predprosro (skucode,number) VALUES ('" + oneObjectsItem + "','" + oneObjectsItem1 + "')");
                } catch (JSONException e) {
                    // Oops
                }
            }
            rezreq =String.valueOf(getText(R.string.justsen));

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    public int uploadBombs(){
        int rez=0;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/
            int i;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/bombsupload.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("ProductsData");
            for ( i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String oneObjectsItem = oneObject.getString("id");
                    String oneObjectsItem2 = oneObject.getString("Confirmation");
                    String oneObjectsItem3 = oneObject.getString("PersonConf");
                    String oneObjectsItem4 = oneObject.getString("comment");
                    Log.i("LOG_TAG", "Запрос " + oneObjectsItem +"   "+ oneObjectsItem2);
                    db.execSQL("UPDATE Bombs SET Confirmation='" + oneObjectsItem2 + "',PersonConf='" + oneObjectsItem3 + "',comment='" + oneObjectsItem4 + "',Updated='1'  WHERE id ='" + oneObjectsItem + "'");
                } catch (JSONException e) {
                    // Oops
                }
            }
            rez =i;

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rez;
    }
    public int uploadBrends(){
        int rez=0;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/
            int i;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/brendsupload.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("ProductsData");
            for ( i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String oneObjectsItem = oneObject.getString("id");
                    String oneObjectsItem2 = oneObject.getString("Confirmation");
                    String oneObjectsItem3 = oneObject.getString("PersonConf");
                    String oneObjectsItem4 = oneObject.getString("comment");
                    String oneObjectsItem5 = oneObject.getString("type");
                    String oneObjectsItem6 = oneObject.getString("price");
                    Log.i("LOG_TAG", "Запрос " + oneObjectsItem +"   "+ oneObjectsItem2);
                    db.execSQL("UPDATE Brends SET Confirmation='" + oneObjectsItem2 + "',PersonConf='" + oneObjectsItem3 + "',comment='" + oneObjectsItem4 + "',Updated='1',type='" + oneObjectsItem5 + "',price='" + oneObjectsItem6 + "'  WHERE id ='" + oneObjectsItem + "'");
                } catch (JSONException e) {
                    // Oops
                }
            }
            rez =i;

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rez;
    }
    public int uploadplanograms(){
        int rez=0;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/
            int i;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/planogramupload.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("ProductsData");
            for (i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String oneObjectsItem = oneObject.getString("id");
                    String oneObjectsItem2 = oneObject.getString("Confirmation");
                    String oneObjectsItem3 = oneObject.getString("PersonConf");
                    String oneObjectsItem4 = oneObject.getString("comment");
                    Log.i("LOG_TAG", "Запрос " + oneObjectsItem + "   " + oneObjectsItem2);
                    db.execSQL("UPDATE Planograms SET Confirmation='" + oneObjectsItem2 + "',PersonConf='" + oneObjectsItem3 + "',comment='" + oneObjectsItem4 + "',Updated='1'  WHERE id ='" + oneObjectsItem + "'");
                } catch (JSONException e) {
                    // Oops
                }
            }
            rez =i;

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rez;
    }
    public String uploadVisitNotExist(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/visitsnotexist.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
                JSONObject jObject = new JSONObject(response);
                JSONArray jArray = jObject.getJSONArray("ProductsData");
                //db.execSQL("DELETE FROM VisitsSalary");
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        String oneObjectsItem = oneObject.getString("PlanDate");
                        String oneObjectsItem2 = oneObject.getString("ClientAddressId");
                        String oneObjectsItem3 = oneObject.getString("Distance");
                        String oneObjectsItem4 = oneObject.getString("Confirmation");
                        String oneObjectsItem5 = oneObject.getString("Equipment");
                         db.execSQL("REPLACE INTO VisitsSalary (id,PlanDate,ClientAddressId,Distance,Confirmation,Equipment,Send) VALUES ('" + oneObjectsItem2 + oneObjectsItem + "','" + oneObjectsItem + "','" + oneObjectsItem2 + "','" + oneObjectsItem3 + "','" + oneObjectsItem4 + "','" + oneObjectsItem5 + "','1')");
                            } catch (JSONException e) {
                        // Oops
                    }
                }
                rezreq = String.valueOf(getText(R.string.justsen));

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    public String uploadReckstockNotExist(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/reckstocknotexist.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            JSONObject jObject = new JSONObject(response);
            JSONArray jArray = jObject.getJSONArray("ProductsData");

            for (int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String oneObjectsItem = oneObject.getString("PlanDate");
                    String oneObjectsItem2 = oneObject.getString("ClientAddressId");
                    String oneObjectsItem3 = oneObject.getString("StockPlan");
                    String oneObjectsItem4 = oneObject.getString("StockFact");
                    String oneObjectsItem5 = oneObject.getString("RekPlan");
                    String oneObjectsItem6 = oneObject.getString("RekFact");
                    String oneObjectsItem7 = oneObject.getString("Confirmation");
                    db.execSQL("REPLACE INTO RekStock (id,PlanDate,ClientAddresId,StockPlan,StockFact,RekPlan,RekFact,Confirmation,Send) VALUES ('" + oneObjectsItem + oneObjectsItem2 + "','" + oneObjectsItem + "','" + oneObjectsItem2 + "','" + oneObjectsItem3 + "','" + oneObjectsItem4 + "','" + oneObjectsItem5 + "','" + oneObjectsItem6 + "','" + oneObjectsItem7 + "','1')");
                } catch (JSONException e) {
                    // Oops
                }
            }
            rezreq = String.valueOf(getText(R.string.justsen));

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    public String uploadDebetNotExist(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/debetnotexist.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            if (!response.equals("<br")) {
                JSONObject jObject = new JSONObject(response);
                JSONArray jArray = jObject.getJSONArray("ProductsData");
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        String oneObjectsItem = oneObject.getString("Date");
                        String oneObjectsItem2 = oneObject.getString("TotalBalance");
                        String oneObjectsItem3 = oneObject.getString("OverdueBalance");
                        String oneObjectsItem4 = oneObject.getString("DebetPercent");
                        Log.i("LOG_TAG", "Запрос " + oneObjectsItem + "   " + oneObjectsItem2);
                        db.execSQL("REPLACE INTO DebetStatus (Date,TotalBalance,OverdueBalance,DebetPercent,Send) VALUES ('" + oneObjectsItem + "','" + oneObjectsItem2 + "','" + oneObjectsItem3 + "','" + oneObjectsItem4 + "','1')");
                    } catch (JSONException e) {
                        // Oops
                    }
                }
                rezreq = String.valueOf(getText(R.string.justsen));
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    public String uploadPlanogramsNotExist(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/planogramsnotexist.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
               if (!response.equals("<br")) {
                JSONObject jObject = new JSONObject(response);
                JSONArray jArray = jObject.getJSONArray("ProductsData");
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        String oneObjectsItem = oneObject.getString("PlanDate");
                        String oneObjectsItem2 = oneObject.getString("ClientAddressId");
                        String oneObjectsItem3 = oneObject.getString("Equipment");
                        String oneObjectsItem4 = oneObject.getString("Confirmation");
                        String oneObjectsItem5 = oneObject.getString("PersonConf");
                        String oneObjectsItem6 = oneObject.getString("updated");
                        String oneObjectsItem7 = oneObject.getString("comment");
                           db.execSQL("REPLACE INTO Planograms (id,PlanDate,ClientAddresId,Equipment,Confirmation,PersonConf,Updated,comment,Send) VALUES ('" + oneObjectsItem + oneObjectsItem2 + "','" + oneObjectsItem + "','" + oneObjectsItem2 + "','" + oneObjectsItem3 + "','" + oneObjectsItem4 + "','" + oneObjectsItem5 + "','" + oneObjectsItem6 + "','" + oneObjectsItem7 + "','1')");
                    } catch (JSONException e) {
                        // Oops
                    }
                }
                rezreq = String.valueOf(getText(R.string.justsen));
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    public String uploadBombsNotExist(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/bombsnotexist.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
              if (!response.equals("<br")) {
                JSONObject jObject = new JSONObject(response);
                JSONArray jArray = jObject.getJSONArray("ProductsData");
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        String oneObjectsItem = oneObject.getString("PlanDate");
                        String oneObjectsItem2 = oneObject.getString("ClientAddressId");
                        String oneObjectsItem3 = oneObject.getString("bombname");
                        String oneObjectsItem4 = oneObject.getString("Confirmation");
                        String oneObjectsItem5 = oneObject.getString("PersonConf");
                        String oneObjectsItem6 = oneObject.getString("updated");
                        String oneObjectsItem7 = oneObject.getString("comment");
                           db.execSQL("REPLACE INTO Bombs (id,PlanDate,ClientAddresId,bombname,Confirmation,PersonConf,Updated,comment,Send) VALUES ('" + oneObjectsItem + oneObjectsItem2 + "','" + oneObjectsItem + "','" + oneObjectsItem2 + "','" + oneObjectsItem3 + "','" + oneObjectsItem4 + "','" + oneObjectsItem5 + "','" + oneObjectsItem6 + "','" + oneObjectsItem7 + "','1')");
                    } catch (JSONException e) {
                        // Oops
                    }
                }
                rezreq = String.valueOf(getText(R.string.justsen));
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }
    public String uploadBrendNotExist(){

        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************обновляем долю точек************************************/

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost http = new HttpPost("http://carlsberg.esy.es/uploadquerry/brendnotexist.php");
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("person", person));

            http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
            if (!response.equals("<br")) {
                JSONObject jObject = new JSONObject(response);
                JSONArray jArray = jObject.getJSONArray("ProductsData");
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        String oneObjectsItem = oneObject.getString("PlanDate");
                        String oneObjectsItem2 = oneObject.getString("ClientAddressId");
                        String oneObjectsItem3 = oneObject.getString("brend");
                        String oneObjectsItem4 = oneObject.getString("Confirmation");
                        String oneObjectsItem5 = oneObject.getString("PersonConf");
                        String oneObjectsItem6 = oneObject.getString("updated");
                        String oneObjectsItem7 = oneObject.getString("comment");
                        String oneObjectsItem8 = oneObject.getString("type");
                        String oneObjectsItem9 = oneObject.getString("price");
                        db.execSQL("REPLACE INTO Brends (id,PlanDate,ClientAddresId,brend,Confirmation,PersonConf,Updated,comment,Send,type,price) VALUES ('" + oneObjectsItem2 + oneObjectsItem3 + "','" + oneObjectsItem + "','" + oneObjectsItem2 + "','" + oneObjectsItem3 + "','" + oneObjectsItem4 + "','" + oneObjectsItem5 + "','" + oneObjectsItem6 + "','" + oneObjectsItem7 + "','1','" + oneObjectsItem8 + "','" + oneObjectsItem9 + "')");
                    } catch (JSONException e) {
                        // Oops
                    }
                }
                rezreq = String.valueOf(getText(R.string.justsen));
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return rezreq;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem mi = menu.add(0, 1, 0, getText(R.string.action_settings));
        MenuItem mispravka = menu.add(0, 2, 0, getText(R.string.about));

        mi.setIntent(new Intent(this, PrefActivity.class));
        mispravka.setIntent(new Intent(this, Info.class));

        //	getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
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
/******************************************загрузка картинок на сервер*************************/

    public boolean sendfile(final String name, String type) {
        String URL = "http://carlsberg.esy.es/photo/"+type+"/uploadfile.php";
        File file =Environment.getExternalStorageDirectory();
        file = new File(file.getAbsolutePath() + "/Carlsberg/Photo/"+type+"/"+name);

        int serverResponseCode = 0;
        boolean result = false;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        if (!file.isFile()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(), "Фото " +name+" не найдено ", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            result = false;
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                /*************************сжимаем фото***********************************/
                InputStream in = new FileInputStream(file);
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    try {
                        OutputStream out = new FileOutputStream(file);
                        try {
                            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out)) {

                            } else {
                                throw new Exception("Failed to save the image as a JPEG");
                            }
                        } finally {
                            out.close();
                        }
                    } catch (Throwable t) {
                        throw t;
                    }
                } finally {
                    in.close();
                }
             /***************************************************************************/
                FileInputStream fileInputStream = new FileInputStream(file);
                URL url = new URL(URL);
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary+";charset=utf-8");
                conn.setRequestProperty("uploaded_file", name);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename=\"" +URLEncoder.encode(name, "UTF-8")+"\""+ lineEnd);
                        dos.writeBytes(lineEnd);
                  // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP ответ : "
                        + serverResponseMessage + ": " + serverResponseCode);
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(SinhrActivity.this, "Фото "+ name+ " загружено",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    result = true;
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SinhrActivity.this, "Ошибка загрузки файла " + name,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                result = false;
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SinhrActivity.this, "Ошибка загрузки файла "+ name,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                result = false;
            }
        } // End else block
        return result;
    }

    public String sendbombaData() {
        response =null;
        String data =null;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        File file =Environment.getExternalStorageDirectory();
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************вытягиваем бомбы из базы********************************/
        String planogram ="SELECT id,PlanDate,ClientAddresId,bombname,Confirmation,PersonConf FROM Bombs WHERE Send='0'";
            Cursor planCursor =db.rawQuery(planogram,null);
            int i=0;
            int k =planCursor.getCount();
            while (planCursor.moveToNext()) {
                if(sendfile(planCursor.getString(1)+"_"+planCursor.getString(2)+"_"+planCursor.getString(3)+".jpg","bombs")==true){
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost http = new HttpPost("http://carlsberg.esy.es/sendquerry/sendbomb.php");
                    List nameValuePairs = new ArrayList();
                    nameValuePairs.add(new BasicNameValuePair("person", person));
                    nameValuePairs.add(new BasicNameValuePair("PlanDate",planCursor.getString(1)));
                    nameValuePairs.add(new BasicNameValuePair("ClientAddressId", planCursor.getString(2)));
                    nameValuePairs.add(new BasicNameValuePair("bombname", planCursor.getString(3)));
                    nameValuePairs.add(new BasicNameValuePair("Confirmation", planCursor.getString(4)));
                    nameValuePairs.add(new BasicNameValuePair("PersonConf", planCursor.getString(5)));
                    http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
                    response = (String) httpclient.execute(http, new BasicResponseHandler());
                    Log.i("LOG_TAG", "Ответ сервера " + response);
                    if (response.equals("data_send")){
                        db.execSQL("UPDATE Bombs SET Send ='1' WHERE id='"+planCursor.getString(0)+"'");
                        final File fileboom = new File(file.getAbsolutePath() + "/Carlsberg/Photo/bombs/"+planCursor.getString(1)+"_"+planCursor.getString(2)+"_"+planCursor.getString(3)+".jpg");
                        Runnable background = new Runnable() {
                            @Override
                            public void run() {
                                fileboom.delete();
                            }
                        };
                        new Thread( background ).start();
                        Log.i("LOG_TAG", "Заработало ");
                        i=i+1;
                    }
                }

            }
            planCursor.close();
            data = i+";"+k;
        /*****************************************************************************************/
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
                db.endTransaction();
            db.close();
        }

        return data;
    }
    public String sendbrendsData() {
        response =null;
        String data =null;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        File file =Environment.getExternalStorageDirectory();
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************вытягиваем бомбы из базы********************************/
            String planogram ="SELECT id,PlanDate,ClientAddresId,brend,Confirmation,PersonConf,type,price FROM Brends WHERE Send='0'";
            Cursor planCursor =db.rawQuery(planogram,null);
            int i=0;
            int k =planCursor.getCount();
            while (planCursor.moveToNext()) {
                if(sendfile(planCursor.getString(2)+"_"+planCursor.getString(3)+".jpg","brends")==true){
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost http = new HttpPost("http://carlsberg.esy.es/sendquerry/sendbrend.php");
                    List nameValuePairs = new ArrayList();
                    nameValuePairs.add(new BasicNameValuePair("person", person));
                    nameValuePairs.add(new BasicNameValuePair("PlanDate",planCursor.getString(1)));
                    nameValuePairs.add(new BasicNameValuePair("ClientAddressId", planCursor.getString(2)));
                    nameValuePairs.add(new BasicNameValuePair("brend", planCursor.getString(3)));
                    nameValuePairs.add(new BasicNameValuePair("Confirmation", planCursor.getString(4)));
                    nameValuePairs.add(new BasicNameValuePair("PersonConf", planCursor.getString(5)));
                    nameValuePairs.add(new BasicNameValuePair("type", planCursor.getString(6)));
                    nameValuePairs.add(new BasicNameValuePair("price", planCursor.getString(7)));
                    http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
                    response = (String) httpclient.execute(http, new BasicResponseHandler());
                    Log.i("LOG_TAG", "Ответ сервера " + response);
                    if (response.equals("data_send")){
                        db.execSQL("UPDATE Brends SET Send ='1' WHERE id='" + planCursor.getString(0) + "'");
                      final File filebrend = new File(file.getAbsolutePath() + "/Carlsberg/Photo/brends/"+planCursor.getString(2)+"_"+planCursor.getString(3)+".jpg");
                        Runnable background = new Runnable() {
                            @Override
                            public void run() {
                                filebrend.delete();
                            }
                        };
                        new Thread( background ).start();
                        Log.i("LOG_TAG", "Заработало ");
                        i=i+1;
                    }
                }

            }
            planCursor.close();
            data = i+";"+k;
            /*****************************************************************************************/
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return data;
    }
    public String sendplanogramData() {
        response =null;
        String data =null;
        File sdPath = Environment.getExternalStorageDirectory();
        DB_PATH = String.format(sdPath.getAbsolutePath() + "/" + DIR_SD_CARLSBERG + "/", DB_Carlsberg);
        ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(SinhrActivity.this, DB_NAME);
        File file =Environment.getExternalStorageDirectory();
        db = extdbc.openDataBase();
        db.execSQL("ATTACH DATABASE '/mnt/sdcard/Carlsberg/Database/carlsberg.db' AS carlsbergDb");
        try {
            db.beginTransaction();
            /******************************вытягиваем бомбы из базы********************************/
            String planogram ="SELECT id,PlanDate,ClientAddresId,Equipment,Confirmation,PersonConf FROM Planograms WHERE Send='0'";
            Cursor planCursor =db.rawQuery(planogram,null);
            int i=0;
            int k =planCursor.getCount();
            while (planCursor.moveToNext()) {
                if(sendfile(planCursor.getString(1)+"_"+planCursor.getString(2)+"_pl.jpg","planogramms")==true){
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost http = new HttpPost("http://carlsberg.esy.es/sendquerry/sendplanogram.php");
                    List nameValuePairs = new ArrayList();
                    nameValuePairs.add(new BasicNameValuePair("person", person));
                    nameValuePairs.add(new BasicNameValuePair("PlanDate",planCursor.getString(1)));
                    nameValuePairs.add(new BasicNameValuePair("ClientAddressId", planCursor.getString(2)));
                    nameValuePairs.add(new BasicNameValuePair("Equipment", planCursor.getString(3)));
                    nameValuePairs.add(new BasicNameValuePair("Confirmation", planCursor.getString(4)));
                    nameValuePairs.add(new BasicNameValuePair("PersonConf", planCursor.getString(5)));
                    http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
                    response = (String) httpclient.execute(http, new BasicResponseHandler());
                    Log.i("LOG_TAG", "Ответ сервера " + response);
                    if (response.equals("data_send")){
                        db.execSQL("UPDATE Planograms SET Send ='1' WHERE id='"+planCursor.getString(0)+"'");
                        final File filepl = new File(file.getAbsolutePath() + "/Carlsberg/Photo/planogramms/"+planCursor.getString(1)+"_"+planCursor.getString(2)+"_pl.jpg");
                        Runnable background = new Runnable() {
                            @Override
                            public void run() {
                                filepl.delete();
                            }
                        };
                        new Thread( background ).start();
                        Log.i("LOG_TAG", "Заработало ");
                        i=i+1;
                    }
                }

            }
            planCursor.close();
            data = i+";"+k;
            /*****************************************************************************************/
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return data;
    }
}
