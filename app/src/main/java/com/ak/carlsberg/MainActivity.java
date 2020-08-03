package com.ak.carlsberg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private AlarmBroadcast alarm;
	private static final String DB_NAME = "monolit.db";
	private static final int MY_PERMISSIONS_REQUEST_DB =1 ;
	TextView textView1;
	Button Button01,button1,button2,button3,Button02,button104,button107,button108;
    ImageView imageView;
	final String LOG_TAG = "myLogs";
	SharedPreferences sp;
	SQLiteDatabase db,dbCarl;
	String sales,response,chek;
	int month;
    RequestSend rqs;
    SimpleDateFormat  format;
    File sdPath = Environment.getExternalStorageDirectory(),sdPathFile =Environment.getExternalStorageDirectory(),sdnew =Environment.getExternalStorageDirectory();
	/** Called when the activity is first created. */
	
	@SuppressLint({ "DefaultLocale", "InflateParams" })
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main1);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (isNetworkAvailable()==true){
            rqs = new RequestSend();
            rqs.execute();

        }

		if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
					Manifest.permission.READ_EXTERNAL_STORAGE)) {

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {

				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(MainActivity.this,
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
						MY_PERMISSIONS_REQUEST_DB);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}

		if (ContextCompat.checkSelfPermission(MainActivity.this,
				Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {
				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {
				// No explanation needed, we can request the permission.
				ActivityCompat.requestPermissions(MainActivity.this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_DB);
				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.SEND_SMS)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_DB);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    File file = new File(Environment.getExternalStorageDirectory()+ "/MonolitAgent/Database/monolit.db");
		File folder = new File(Environment.getExternalStorageDirectory()+ "/MonolitAgent");
		boolean folderEx = folder.exists();
    boolean exists = file.exists();
    if (exists == false || folderEx == false)
    { 	
    	
    	 AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setCancelable(false);
			LinearLayout view = (LinearLayout) getLayoutInflater()
			        .inflate(R.layout.dialog, null);
			builder.setView(view);
			TextView info = (TextView) view.findViewById(R.id.info);
			info.setText(R.string.nomonolit);
			builder.setNegativeButton(R.string.exit,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
			
			
			AlertDialog alert = builder.create();
			alert.show();
	
    }
        else{

        ExternalDbOpenHelper extdb = new ExternalDbOpenHelper(this, DB_NAME);
        db = extdb.openDataBase();
        try {
            db.beginTransaction();
            //проверка есть ли таблицы в базе
            String queryCheck = "SELECT count(*) FROM sqlite_master WHERE type = 'table' AND name != 'android_metadata' AND name != 'sqlite_sequence';";
            Cursor cursorCheck = db.rawQuery(queryCheck, null);
            while (cursorCheck.moveToNext()) {
                if (cursorCheck.getInt(0) <2){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    LinearLayout view = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.dialog, null);
                    builder.setView(view);
                    TextView info = (TextView) view.findViewById(R.id.info);
                    info.setText(R.string.nobase);
                    builder.setNegativeButton(R.string.exit,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                };

            }
            cursorCheck.close();

            String queryPerson = "SELECT FinManagerName FROM Pars;";
            Cursor cursorPerson = db.rawQuery(queryPerson, null);
            while (cursorPerson.moveToNext()) {
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("person", cursorPerson.getString(0));
                ed.commit();
            }
            cursorPerson.close();
            /***************************удаление оборудования***************************************/
            /*String queryI = "DELETE FROM ClientAddressEquip";
            Cursor cursor1 = db.rawQuery(queryI, null);
            while (cursor1.moveToNext()) {}
            cursor1.close();*/
            /***************************************************************************************/

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
            db.close();
        }
    }

        sdPath = new File(sdPath.getAbsolutePath() + "/Carlsberg/Database/");
		sdPathFile = new File(sdPathFile.getAbsolutePath() + "/Carlsberg/Database/carlsberg.db");
        if (!sdPath.exists() ){sdPath.mkdirs();copyFile("carlsberg.db");}
        if (!sdPathFile.exists() ){copyFile("carlsberg.db");}
        /*****************************ОБНОВЛЕНИЕ БАЗЫ ДАННЫХ CARLSBERG*****************************/
        ExternalDbOpenHelper extdbСarl = new ExternalDbOpenHelper(this, "carlsberg.db");
            dbCarl = extdbСarl.openDataBase();
            try {
                dbCarl.beginTransaction();
            dbCarl.getVersion();
            Log.i("LOG_TAG", "Версия базы данных carlsberg " + dbCarl.getVersion());
            if (dbCarl.getVersion() <15) {
                extdbСarl.onUpgrade(dbCarl, dbCarl.getVersion(), 14);
            }
            Log.i("LOG_TAG", "Новая версия базы данных carlsberg " + dbCarl.getVersion());
            dbCarl.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            dbCarl.endTransaction();
            dbCarl.close();
        }

        /*****************************ОБНОВЛЕНИЕ БАЗЫ ДАННЫХ CARLSBERG*****************************/

    /***************************���� ������*******************************************/
    
    Calendar aCalendar = Calendar.getInstance();
    // �������� ������� �����
    aCalendar.add(Calendar.MONTH,0);
    // �������� ������ ����� ������       
    
    aCalendar.set(Calendar.HOUR_OF_DAY, 00);
    aCalendar.set(Calendar.MINUTE, 00);
    aCalendar.set(Calendar.MILLISECOND, 00);
    aCalendar.set(Calendar.SECOND,00);
    
    aCalendar.set(Calendar.DAY_OF_MONTH, 1);
    String firstday =  getFullTime(aCalendar.getTimeInMillis());
    Date date,tod;
    String dtStart = "2018-01-01 00:00:00";
    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    
   
    // set actual maximum date of previous month
    aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    //read it
    String today = getFullTime(aCalendar.getTimeInMillis());  
    
    
    aCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);   
    String monday = getFullTime(aCalendar.getTimeInMillis());
    String perem = sp.getString("lang", "default");
    Log.i("LOG_TAG", "���� " + perem);
    if (perem == "en"|| perem =="be"){month =6;} else{month =11;}
    aCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY + month);
    String sunday =  getFullTime(aCalendar.getTimeInMillis());
   
    Log.i("LOG_TAG", "������ ���� " + firstday +" ��������� ���� "+today);
    Log.i("LOG_TAG", "�����������" + monday +"����������� "+sunday);

    /********************************������ �������************************************/	    
    Boolean servisestate = sp.getBoolean("notifi", false);
    servise(servisestate);
    /**********************************************************************************/ 
    
    Typeface fonttahoma = Typeface.createFromAsset(getAssets(), "fonts/tahomabd.ttf");
    Typeface fontbauhs93 = Typeface.createFromAsset(getAssets(), "fonts/bauhs93.TTF");
        Typeface days = Typeface.createFromAsset(getAssets(), "fonts/Days.ttf");
    /*Typeface mistral = Typeface.createFromAsset(getAssets(), "fonts/mistral.TTF");*/
    

    /*setTitle("Carlsberg");*/
    
    int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
    TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
    Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
    if(actionBarTitleView != null){
        actionBarTitleView.setTypeface(fonts);
        actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
    }
    
    textView1 = (Button) findViewById(R.id.textView1);
    
    textView1.setTypeface(days);
   // textView03.setTypeface(fontbauhs93);
    

    Button02 = (Button) findViewById(R.id.Button02);
    button1 = (Button) findViewById(R.id.button1);
    button2 = (Button) findViewById(R.id.button2);
    button3 = (Button) findViewById(R.id.button3);
        button104 = (Button) findViewById(R.id.button104);
        button107 = (Button) findViewById(R.id.button107);
        button108 = (Button) findViewById(R.id.button108);



        button108.setTypeface(fonttahoma);
    Button02.setTypeface(fonttahoma);
    button1.setTypeface(fonttahoma);
    button2.setTypeface(fonttahoma);
    button3.setTypeface(fonttahoma);
   
    
    Animation right = AnimationUtils.loadAnimation(this, R.anim.mytrans);
    Animation left = AnimationUtils.loadAnimation(this, R.anim.mytransleft);
        Animation bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

       // imageView = (ImageView) findViewById(R.id.imageView);
       // imageView.startAnimation(shake);

    //Button02.startAnimation(bottom);
    button1.startAnimation(left);
    button2.startAnimation(right);
    button3.startAnimation(left);
    textView1.startAnimation(right);

        button104.startAnimation(right);
        button107.startAnimation(bottom);
        button108.startAnimation(bottom);
 
    OnClickListener prezentations = new OnClickListener() {
	       @Override
	       public void onClick(View v) {
	    	   /*��������� � �������� Prezentations*/ 
	    	   Intent intent = new Intent(MainActivity.this, PrezentationsActivity.class);
	    	   startActivity(intent);
	    	   
	       }
	     };
	 
	     // �������� ���������� ������ OK (btnOk)
	     button2.setOnClickListener(prezentations);


		OnClickListener segments = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SegmentsActivity.class);
				startActivity(intent);

			}
		};
		textView1.setOnClickListener(segments);
	     
	     OnClickListener Visits = new OnClickListener() {
		       @Override
		       public void onClick(View v) {
		    	   /*��������� � �������� Prezentations*/ 
		    	   Intent intent = new Intent(MainActivity.this, SalaryActivity.class);
		    	   startActivity(intent);
		    	   
		       }
		     };
		 
		     // �������� ���������� ������ OK (btnOk)
		     Button02.setOnClickListener(Visits);
	    
	     OnClickListener tasks = new OnClickListener() {
		       @Override
		       public void onClick(View v) {
		    	   /*��������� � �������� Prezentations*/ 
		    	   Intent intent = new Intent(MainActivity.this, TasksActivity.class);
		    	   startActivity(intent);
		    	   
		       }
		     };
		 
		     // �������� ���������� ������ OK (btnOk)
		     button3.setOnClickListener(tasks);

	 OnClickListener sales = new OnClickListener() {
		   @Override
		   public void onClick(View v) {
		      /*��������� � �������� Prezentations*/
		      Intent intent = new Intent(MainActivity.this, SalesTabActivity.class);
		      startActivity(intent);

		    }
		  };

		     // �������� ���������� ������ OK (btnOk)
		     button1.setOnClickListener(sales);

        OnClickListener photo = new OnClickListener() {
            @Override
            public void onClick(View v) {
		      /*��������� � �������� Prezentations*/
                Intent intent = new Intent(MainActivity.this, AtataActivity.class);
                startActivity(intent);

            }
        };

        // �������� ���������� ������ OK (btnOk)
        button104.setOnClickListener(photo);

        OnClickListener kalc = new OnClickListener() {
            @Override
            public void onClick(View v) {
		      /*��������� � �������� Prezentations*/
                Intent intent = new Intent(MainActivity.this, ProsrockActivity.class);
                startActivity(intent);

            }
        };

        // �������� ���������� ������ OK (btnOk)
        button107.setOnClickListener(kalc);




        OnClickListener email = new OnClickListener() {
				   @Override
		public void onClick(View v) {
		  /*��������� � �������� Prezentations*/ 
		Intent intent = new Intent(MainActivity.this, EmailActivity.class);
		startActivity(intent);
				    	   
		 }
	 };
		button108.setOnClickListener(email);
	   
	}
  
	@Override
	  protected void onResume() {
	    super.onResume();
	    
	    Animation right = AnimationUtils.loadAnimation(this, R.anim.mytrans);
	    Animation left = AnimationUtils.loadAnimation(this, R.anim.mytransleft);
	    //textView01.setText(String.valueOf(CountFiles()));
        Animation bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);
	   // Button02.startAnimation(bottom);
	    button1.startAnimation(left);
	    button2.startAnimation(right);
	    button3.startAnimation(left);
        button104.startAnimation(right);
        button107.startAnimation(bottom);
	    textView1.startAnimation(right);
	   
	  }
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem mi = menu.add(0, 1, 0, R.string.action_settings );									
		mi.setIntent(new Intent(this, PrefActivity.class));
		
		MenuItem mie = menu.add(0, 1, 0, R.string.about );									
		mie.setIntent(new Intent(this, Info.class));
			
		//getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);		
		    
	}
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
/****************************************������/��������� ������***************************************/
	public void servise(Boolean notifi){
        Context context= this.getApplicationContext();
	if (notifi == true)
	    {
            Intent myIntent = new Intent(MainActivity.this, AlarmBroadcast.class);

            boolean isWorking = (PendingIntent.getBroadcast(MainActivity.this, 1, myIntent, PendingIntent.FLAG_NO_CREATE) != null);
            if (isWorking) {
                Log.i("LOG_TAG", "Будильник уже установлен " );
            } else {
                alarm=new AlarmBroadcast();
                if(alarm!=null){
                    alarm.SetNews(context);
                }else{
                    Toast.makeText(context,"Напоминание не установленно", Toast.LENGTH_SHORT).show();
                }
            }

	    }
	else {
            alarm=new AlarmBroadcast();
			alarm.CancelAlarm(context);
        Log.i("LOG_TAG", "Будильник выключен ");
		}
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
	 
	 /************************************������� ���-�� �����������***********************/
	/* public static final int CountFiles()
	 {
		 File sdPath = Environment.getExternalStorageDirectory();                       
		 File fullPath = new File(sdPath.getAbsolutePath() + "/Carlsberg/prezentations");
		 File[] countList = fullPath.listFiles();
		 int countFiles;
		 if (countList == null){
			 countFiles = 0;
		 }
		 else {countFiles = countList.length;}
		 
		 return countFiles;
		 
	 }*/
     private void copyFile(String filename) {
         AssetManager assetManager = this.getAssets();

         InputStream in = null;
         OutputStream out = null;
         try {
             in = assetManager.open(filename);
             String newFileName = sdPath.toString() + "/" + filename;
             out = new FileOutputStream(newFileName);

             byte[] buffer = new byte[1024];
             int read;
             while ((read = in.read(buffer)) != -1) {
                 out.write(buffer, 0, read);
             }
             in.close();
             in = null;
             out.flush();
             out.close();
             out = null;
         } catch (Exception e) {
             Log.e("tag", e.getMessage());
         }

     }
    class RequestSend extends AsyncTask<Void, Void, Void> {

        int i,k=0;
        boolean rezreq;
		AlertDialog.Builder appdate = new AlertDialog.Builder(MainActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
/******************************вызов диалога отправки данных****************************************/
            try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost http = new HttpPost("http://carlsberg.esy.es/check_date.php");
                        List nameValuePairs = new ArrayList();

                http.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf8"));
                response = (String) httpclient.execute(http, new BasicResponseHandler());
				chek =sp.getString("appdate","2017-07-11 00:00:00");
                try {
                    Date first = format.parse(chek);
                    Date last = format.parse(response);
                    int newdatebool = last.compareTo(first);
                    if (newdatebool >0){
                        Log.i("LOG_TAG", "Доступна новая версия приложения ");
                        rezreq =true;
                    }
                    else {
                        rezreq =false;
                    }
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (rezreq ==true){


            appdate.setCancelable(false);
            LinearLayout viewap = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.dialog, null);
            appdate.setView(viewap);
            TextView infoap = (TextView) viewap.findViewById(R.id.info);
            TextView mist = (TextView) viewap.findViewById(R.id.mistake);
            mist.setText("Внимание");
            infoap.setText("Доступно обновление приложения!");
            appdate.setNegativeButton("Потом поставлю", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            appdate.setPositiveButton("Установить сейчас", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    sdnew = new File(sdnew.getAbsolutePath() + "/Carlsberg/apk/");
                    if (!sdnew.exists()){sdnew.mkdirs();}
                    else {
                        String filename ="agent.apk";
                        String url ="http://carlsberg.esy.es/download_apk.php";
                        downloadFile(url, filename);
                        SharedPreferences.Editor edb = sp.edit();
                        edb.putString("appdate", response);
                        edb.commit();
                    }

                    dialog.cancel();
                }
            });

            AlertDialog alertap = appdate.create();
            alertap.show();
        }
        }
    }
    private void downloadFile(final String url, final String filename) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        new AsyncTask<String, Integer, File>() {
            private Exception m_error = null;

            protected void onPreExecute() {
                String text = getText(R.string.dialogdownload) +" "+ filename+"...";
                progressDialog.setMessage( text);
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                progressDialog.show();
            }

            @Override
            protected File doInBackground(String... params) {
                URL url;
                HttpURLConnection urlConnection;
                InputStream inputStream;
                byte[] buffer;
                int bufferLength,downloadedSize,totalSize;

                File file = null;
                FileOutputStream fos = null;

                try {
                    url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setReadTimeout(20000);
                    urlConnection.connect();

                    File szFile = new File(sdnew +"/"+filename);

                    try {
                        fos = new FileOutputStream(szFile);
                        inputStream = urlConnection.getInputStream();

                        totalSize = urlConnection.getContentLength();
                        Log.i("LOG_TAG","Общий размер файла " + totalSize);
                        downloadedSize = 0;

                        buffer = new byte[8192];
                        bufferLength = 0;

                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            fos.flush();
                            publishProgress(downloadedSize, totalSize);
                        }

                        fos.close();
                        inputStream.close();
                        return file;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    m_error = e;
                } catch (IOException e) {
                    e.printStackTrace();
                    m_error = e;
                }
                return null;
            }

            protected void onProgressUpdate(Integer... values) {
                progressDialog
                        .setProgress((int) ((values[0] / (float) values[1]) * 100));
            };

            @Override
            protected void onPostExecute(File file) {
                if (m_error != null) {
                    m_error.printStackTrace();

                    return;
                }
                progressDialog.hide();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File (sdnew.getAbsolutePath() + "/agent.apk")), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }.execute(url);
    }
}
