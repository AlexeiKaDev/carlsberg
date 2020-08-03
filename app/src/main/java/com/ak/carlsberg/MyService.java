package com.ak.carlsberg;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

public class MyService extends Service {
    private static final String LOG_TAG = "myLogs";
    NotificationManager nm;
    Notification myNotication;
    SimpleDateFormat  format;
    SharedPreferences sp;
    String thisday;

  @Override
  public void onCreate() {
    super.onCreate();

    nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    
  }
  
  public int onStartCommand(Intent intent, int flags, int startId) {
      final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

      ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo nInfo = cm.getActiveNetworkInfo();

      String LOG_TAG = null;
      if (nInfo.isConnected() == true) {
          Log.i(LOG_TAG, "Подключение к интернету " + nInfo.isConnected());
              String urlstring = "http://carlsberg.esy.es/prezentations/default.php";
              lastdate1(urlstring);
              sendNotif2();
              stopSelf();
          stopService(new Intent(this,MyService.class));
          }

      else {
          Log.d(LOG_TAG, "Нет подключения к интернету");

      }

      return START_NOT_STICKY;


  }
  
  @SuppressLint("NewApi")
  @SuppressWarnings("deprecation")
private void sendNotif1() {
    Intent intent = new Intent(this, PrezentationsActivity.class);
   
    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

      Notification.Builder builder = new Notification.Builder(MyService.this);

      builder.setAutoCancel(false);
      builder.setTicker("this is ticker text");
      builder.setContentTitle(getText(R.string.notifi_title));
      builder.setContentText(getText(R.string.notifi_prezentations));
      builder.setSmallIcon(R.drawable.ic_stat);
      builder.setContentIntent(pIntent);
      builder.setOngoing(false);
      builder.setVibrate(new long[]{300, 100, 100, 300, 100, 300, 500});
      builder.setLights(Color.GREEN, 100, 300);
      builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
      //builder.setNumber(1);
      builder.build();

      myNotication = builder.getNotification();
      nm.notify(0, myNotication);

  }      
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void sendNotif2() {
        Intent intent = new Intent(MyService.this, SinhrActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(MyService.this);

        builder.setAutoCancel(false);
        builder.setTicker("this is ticker text");
        builder.setContentTitle("Обязательная синхронизация");
        builder.setContentText("Зайдите в Carlsberg Agent");
        builder.setSmallIcon(R.drawable.ic_stat);
        builder.setContentIntent(pIntent);
        builder.setOngoing(false);
        builder.setVibrate(new long[]{300, 100, 100, 300, 100, 300, 500});
        builder.setLights(Color.GREEN, 100, 300);
        builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.carlsberg ));
        //builder.setNumber(2);
        builder.build();

        myNotication = builder.getNotification();
        nm.notify(1, myNotication);

    }



    private void lastdate1(final String url) {

	Thread background = new Thread(new Runnable() {
        
        private final HttpClient Client = new DefaultHttpClient();
        private String URL = url;
         
        // After call for background.start this run method call
        public void run() {
            try {

                String SetServerString = "";
                HttpGet httpget = new HttpGet(URL);
                
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                SetServerString = Client.execute(httpget, responseHandler);
                threadMsg(SetServerString);

            } catch (Throwable t) {
                // just end the background thread
               
            }
        }

        private void threadMsg(String msg) {

            if (!msg.equals(null) && !msg.equals("")) {
                Message msgObj = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("message", msg);
                msgObj.setData(b);
                handler.sendMessage(msgObj);
            }
        }

        // Define the Handler that receives messages from the thread and update the progress
        @SuppressLint("HandlerLeak")
		private final Handler handler = new Handler() {
        	
        	
            public void handleMessage(Message msg) {
                 
                String aResponse = msg.getData().getString("message");
                
                if ((null != aResponse)) {
                      
               /***********************����� �������******************************************/                               	
          	    	try {
          	    	String[] webString = aResponse.split(";");
          	    	String[] web;
          	    	
						web = downloadmass(webString);
						if (web[0].isEmpty()){																																		
						
						}
						else { sendNotif1();}
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
          	    	
          	    	                          	    
          	   /*****************************************************************************/
                    
                }
                else
                {

                	String LOG_TAG = null;
					Log.d(LOG_TAG, "��� ������ �� �������");
                }    

            }
        };

    });
    // Start Thread
    background.start();  //After call start method thread called run Method
}

/****************************************�������� ��� ������
 * @throws ParseException ********************************************************************************/ 
    
			@SuppressLint("SimpleDateFormat")
			private String[] downloadmass(String[] webstring) throws ParseException {
            	String[] webmass = new String[webstring.length*2];
            	String elements ="";
            	File sdPath = Environment.getExternalStorageDirectory();
            	sdPath = new File(sdPath.getAbsolutePath() + "/Carlsberg/prezentations");
     	    	 for (int i=0; i<webstring.length; i++){
         	        
         	         String droblenie = String.valueOf(webstring[i]);
         	         String[] filfull = droblenie.split("/");
         	         
         	         for (int j=0; j<filfull.length; j++){ 
         	        	                      
         	        	 	elements = elements + filfull[j]+";";
         	        	 	
         	         	}
     	    	 }
     	    	webmass=elements.split(";");
     	    	@SuppressWarnings("unused")
				String[] loadmass = new String[webmass.length];
     	    	String loadstring="";
     	    	
     	    	for (int i=0; i<webmass.length; i=i+2){
     	    		String filePath = sdPath+"/"+webmass[i].trim();     	    		
     	    		File names = new File (filePath);     	    		
     	    		SimpleDateFormat format = new SimpleDateFormat();
     	    		format.applyPattern("dd.MM.yyyy HH:mm:ss");
     	    		Date webDate = format.parse(webmass[i+1]);    	    		
     	    		Date localDate = format.parse(getFullTime(names.lastModified()));    	    		
     	    		Date newdate =  webDate;
     	    		int newdatebool = newdate.compareTo(localDate);    	    		
     	    		if(names.exists()==false){
     	    			loadstring =loadstring + webmass[i]+";";     	    			
     	    			
     	    		}
     	    		
     	    		else if (webmass[i].trim()== names.getName() || newdatebool>0 ) {
     	    		     	    		  	        		     	    		      	    			
     	    			loadstring =loadstring + webmass[i]+";";    	    				     	    		     	    		
     	    		}
     	    		
     	    		else {
     	    			loadstring =loadstring+"";
     	    		}
     	    	}
     	    	
            	return loadmass=loadstring.split(";") ;
            }
/**************************************************************************************************************/			
			 @SuppressLint("SimpleDateFormat")
				public static final String getFullTime(final long timeInMillis)
	            {
	                final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	                final Calendar c = Calendar.getInstance();
	                c.setTimeInMillis(timeInMillis);
	                c.setTimeZone(TimeZone.getDefault());
	                return format.format(c.getTime());
	            }
/*************************************************************************************************************/			 



public IBinder onBind(Intent arg0) {
    return null;
  }
}