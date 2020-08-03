package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class PrezentationsActivity extends AppCompatActivity {
	
	String toastText;
	private ArrayList<HashMap<String, Object>> prezentationsList;
	private static final String TITLE = "File Name";
	private static final String DESCRIPTION = "description";
	private static final String ICON = "icon";
	protected static final String LOG_TAG = null;	
	SharedPreferences sp;

	Button refreshButton;
	File sdPath = Environment.getExternalStorageDirectory();
	
/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.prezentations);
   
    prezentationsList = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> hm;    
    final ListView lv = (ListView)findViewById(R.id.listView1);
    toastText  =  getString (R.string.new_folder_prezentations);      
    sp = PreferenceManager.getDefaultSharedPreferences(this);
    
    /*setTitle("Carlsberg");*/
    
    int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
    TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
    Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
    if(actionBarTitleView != null){
        actionBarTitleView.setTypeface(fonts);
        actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
    }
    // ��������� ���� ������� � ����
    sdPath = new File(sdPath.getAbsolutePath() + "/Carlsberg/prezentations");
    final String Path = sdPath.toString();
    
    String state = Environment.getExternalStorageState();
    if (!Environment.MEDIA_MOUNTED.equals(state)){
    	// �������� ������� � SD �����
    	String toastSdfail  =  getString (R.string.SD_card_fail);
    	Toast toast = Toast.makeText(getApplicationContext(), toastSdfail , Toast.LENGTH_SHORT); 
    			toast.show(); 
    }
    
    else if(!sdPath.exists()){
    	   // ���������� �������� ��� ������
    	sdPath.mkdirs();
    	
    	Toast toast = Toast.makeText(getApplicationContext(), toastText  , Toast.LENGTH_SHORT); 
    			toast.show(); 
    	} 
  
  
   /********��������� ������� ������ ����������***************************************************/	
    		 refreshButton = (Button) findViewById(R.id.refreshButton);  
    		
    		 OnClickListener refresh = new OnClickListener() {
       	       @SuppressLint("InflateParams")
			@Override
       	       public void onClick(View v) {    	    	   
       	    	   /*��������� ����������� � ���������*/ 
       	    	   if (isNetworkAvailable()==false){
       	    		   
       	    		  AlertDialog.Builder builder = new AlertDialog.Builder(PrezentationsActivity.this);
       	    			builder.setCancelable(true);
       	    			
       	    			LinearLayout view = (LinearLayout) getLayoutInflater()
       	    			        .inflate(R.layout.dialog, null);
       	    			builder.setView(view);
       	    			TextView info = (TextView) view.findViewById(R.id.info);
       	    			info.setText(R.string.connection_fail);
       	    			
       	    			AlertDialog alert = builder.create();
       	    			alert.show();
       	    	   }
       	    	   
       	    	   else {

       	    		final String urlstring = "http://carlsberg.esy.es/prezentations/default.php";
       	    	
        			Thread background = new Thread(new Runnable() {
                        
                        private final HttpClient Client = new DefaultHttpClient();
                        private String URL = urlstring;
                         
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
								Log.i("LOG_TAG", "Запрос презентаций "+ aResponse );
                                if ((null != aResponse)) {
                                      
                               /***********************����� �������******************************************/                               	
                          	    	try {
                          	    	String[] webString = aResponse.split(";");
                          	    	String[] web;
                          	    	String LOG_TAG = null;
										web = downloadmass(webString);
										
										if (web[0].isEmpty()){
											
											 AlertDialog.Builder builder = new AlertDialog.Builder(PrezentationsActivity.this);
						       	    			builder.setCancelable(true);
						       	    			
						       	    			LinearLayout view = (LinearLayout) getLayoutInflater()
						       	    			        .inflate(R.layout.refreshdialog, null);
						       	    			builder.setView(view);
						       	    			TextView info = (TextView) view.findViewById(R.id.info);
						       	    			info.setText(R.string.refreshinfo);
						       	    			
						       	    			AlertDialog alert = builder.create();
						       	    			alert.show();																					
										
										}
										else {
											
											AlertDialog.Builder builder = new AlertDialog.Builder(PrezentationsActivity.this);
					       	    			builder.setCancelable(true);
					       	    			
					       	    			LinearLayout view = (LinearLayout) getLayoutInflater()
					       	    			        .inflate(R.layout.refreshdialog, null);
					       	    			builder.setView(view);
					       	    				String refreshes ="";
					       	    				for (int i=0; i<web.length; i++){
		                          	    		 					       	    						                      	        		
					       	    					refreshes = refreshes +getText(R.string.refreshnames)+ web[i].trim() +"\n";					       	    					
					       	    				}
					       	    			
					       	    			TextView info = (TextView) view.findViewById(R.id.info);
					       	    			info.setText(refreshes);
					       	    			
					       	    			AlertDialog alert = builder.create();
					       	    			alert.show();
											
											for (int i=0; i<web.length; i++){
		                          	    		 
		                      	        		Log.d(LOG_TAG,  web[i]); 	                      	        		
		                      	        		String fileurl = "http://carlsberg.esy.es/prezentations/" + web[i].trim();
		                      	        		downloadFile(fileurl, web[i].trim());
											  }
											
										}
										
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
                          	    	
                          	    	                          	    
                          	   /*****************************************************************************/
                                    
                                }
                                else
                                {
                                        // ALERT MESSAGE
                                        Toast.makeText(
                                                getBaseContext(),
                                                "Not Got Response From Server.",
                                                Toast.LENGTH_LONG).show();
                                }    
     
                            }
                        };
     
                    });
                    // Start Thread
                    background.start();  //After call start method thread called run Method
                }
           
       	    		
       	    	   }
       	       
       	     };
       	 
       	     // �������� ���������� ������ OK (btnOk)
       	 
       	     refreshButton.setOnClickListener(refresh); 
    		
              File[] fileArray = sdPath.listFiles();
              for (int i=0; i<fileArray.length; i++){
            	  hm = new HashMap<String, Object>();
                  hm.put(TITLE, fileArray[i].getName()); // ��������
          		  hm.put(DESCRIPTION, getFullTime(fileArray[i].lastModified())); // ��������
				  String filenameArray[] = fileArray[i].getName().toString().split("\\.");
				  String extension = filenameArray[filenameArray.length-1];
				  Log.i("LOG_TAG", "расширение " + extension);
				  /*********проверка расширения ***/
				  if (extension.equals("txt")||extension.equals("ppt")||
						  extension.equals("doc")||extension.equals("pdf")||extension.equals("xls"))
				  {
					  hm.put(ICON,  R.drawable.pdf_icon);
				  }
				  else
				  if (extension.equals("mp4")||extension.equals("3gp")||extension.equals("wmv"))
				  {
					  hm.put(ICON,  R.drawable.icon_video);
				  }
				  else
				  if (extension.equals("jpeg")||extension.equals("gif")||extension.equals("png"))
				  {
					  hm.put(ICON,  R.drawable.photo);
				  }
          		  else {
					  {
						  hm.put(ICON,  R.drawable.no_pdf);
					  }
				  }
          		  prezentationsList.add(hm);
                  
              }
                                                                   
              SimpleAdapter adapter = new SimpleAdapter(this, prezentationsList,
      				R.layout.list_item_prezentations, new String[] { TITLE, DESCRIPTION, ICON},
      				new int[] { R.id.text1, R.id.text2, R.id.img });
              	
              	lv.setAdapter(adapter);                          	
              	
              	lv.setOnItemClickListener(new OnItemClickListener() {            	              		
              		             		
                @SuppressLint("SdCardPath")
				@SuppressWarnings("rawtypes")
				public void onItemClick(final AdapterView<?> parent, View view,
                    final int position, long id) {   
                	
                	Runnable runnable = new Runnable() {
              	        public void run() {
              	           
              	        	String stringall = parent.getItemAtPosition(position).toString();/*�������� ������*/
                        	/*�������� ������� ������� ������� �������� �����*/
                        	String namefilepozition = String.valueOf(stringall.indexOf("File Name="));
                        	int pozitionf = Integer.parseInt(namefilepozition) +10;
                        	/*�������� ��� �� ������ �������� �����*/
                        	String text = stringall.substring(pozitionf);
                        	/*�������� ������� �������*/
                        	final String[] namefile = text.split(",");
                        	/* ��������� ����*/
                        	File file = new File(Path + "/"+ namefile[0]);
                        	Log.i("LOG_TAG", "���� � ������������� ����� " + file.toString() + " ������� ������� " + namefile[0]);
            	     	    PackageManager packageManager = getPackageManager();
            	     	    Intent testIntent = new Intent(Intent.ACTION_VIEW);
            	     	    testIntent.setType("application/pdf");
            	     	    
        					List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
            	     	    if (list.size() > 0 && file.isFile()) {
								String filenameArray[] = file.toString().split("\\.");
								String extension = filenameArray[filenameArray.length-1];
								String ext="";
								/*********проверка расширения для вызова интента***/
								if (extension.equals("txt")||extension.equals("ppt")||
										extension.equals("doc")||extension.equals("pdf")||extension.equals("xls"))
								{ext ="application/pdf";}
								if (extension.equals("mp4")||extension.equals("3gp")||extension.equals("wmv"))
								{ext ="video/mp4";}
								if (extension.equals("jpeg")||extension.equals("gif")||extension.equals("png"))
								{ext ="image/jpeg";}

								/**************************************************/
            	     	    Intent intent = new Intent();
            	     	    intent.setAction(Intent.ACTION_VIEW);
            	     	    Uri uri = Uri.fromFile(file);
            	     	    intent.setDataAndType(uri, ext);
            	     	    startActivity(intent);
            	     	    }    
              	        	
              	        }
              	    };
              	    Thread thread = new Thread(runnable);
              	    thread.start();
                	                	
    	     	 
                }                               
             
              });
            
            lv.setOnItemLongClickListener(new OnItemLongClickListener(){

				@SuppressLint("InflateParams")
				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					// TODO Auto-generated method stub

					String stringall = parent.getItemAtPosition(position).toString();/*�������� ������*/
                	/*�������� ������� ������� ������� �������� �����*/
                	String namefilepozition = String.valueOf(stringall.indexOf("File Name="));
                	int pozitionf = Integer.parseInt(namefilepozition) +10;
                	/*�������� ��� �� ������ �������� �����*/
                	String text = stringall.substring(pozitionf);
                	/*�������� ������� �������*/
                	final String[] namefile = text.split(",");
                	/* ��������� ����*/
                	final File file = new File(Path + "/"+ namefile[0]);

	    			AlertDialog.Builder builder = new AlertDialog.Builder(PrezentationsActivity.this);
   	    			builder.setCancelable(true);

   	    			LinearLayout view1 = (LinearLayout) getLayoutInflater()
   	    			        .inflate(R.layout.delete_dialog, null);
   	    			builder.setView(view1);

   	    			builder.setNegativeButton(R.string.no , new DialogInterface.OnClickListener() {
 	 						public void onClick(DialogInterface dialog, int id) {
	  	 							dialog.cancel();
	  	 						}
	  	 					}).setPositiveButton(R.string.yes ,
   	   	 					new DialogInterface.OnClickListener() {
   	   	 						public void onClick(DialogInterface dialog, int id) {
   	   	 							file.delete();
   	   	 							dialog.cancel();
   	   	 							Intent intent = new Intent(PrezentationsActivity.this, PrezentationsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
   	   	 							startActivity(intent);
   	   	 						}
   	   	 					});

   	    			AlertDialog alert = builder.create();
   	    			alert.show();

					return true;


				}


            });
           
 
    	}

             
   
  
/////////////////////////////////////////////////////////////////////////////////////////////////
			public String get_mime_by_filename(String filename){
				String ext;
				String type;
	 
				int lastdot = filename.lastIndexOf(".");
				if(lastdot > 0){
					ext = filename.substring(lastdot + 1);
					MimeTypeMap mime = MimeTypeMap.getSingleton();
					type = mime.getMimeTypeFromExtension(ext);
					if(type != null) {
						return type;
					}
				}
				return "application/octet-stream";
			}
           
            
            @SuppressLint("SimpleDateFormat")
			public static final String getFullTime(final long timeInMillis)
            {
                final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(timeInMillis);
                c.setTimeZone(TimeZone.getDefault());
                return format.format(c.getTime());
            }
 /*********************************************�������� ����***************************************/
            private boolean isNetworkAvailable() {
                ConnectivityManager connectivityManager 
                      = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null;
            }
            
            
 
 /****************************************�������� ��� ������ 
 * @throws ParseException ***********************************************/ 
    
			@SuppressLint("SimpleDateFormat")
			private String[] downloadmass(String[] webstring) throws ParseException {
				
				
				
				String LOG_TAG = null; 
            	String[] webmass = new String[webstring.length*2];
            	String elements ="";
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
     	    			Log.d(LOG_TAG,  "����� ������� ��� �� ��� " +loadstring);
     	    			
     	    		}
     	    		
     	    		else if (webmass[i].trim()== names.getName() || newdatebool>0 ) {
     	    		     	    		  	        		     	    		      	    			
     	    			loadstring =loadstring + webmass[i]+";";
     	    			Log.d(LOG_TAG,  "������� ������ ��� �������� " +loadstring);	     	    		     	    		
     	    		}
     	    		
     	    		else {
     	    			loadstring =loadstring+"";
     	    		}
     	    	}
     	    	
            	return loadmass=loadstring.split(";") ;
            }
/******************************************�������� ������****************************************************/
			private void downloadFile(final String url, final String filename) {
				final ProgressDialog progressDialog = new ProgressDialog(this);
				 
				  new AsyncTask<String, Integer, File>() {
				   private Exception m_error = null;
				 
				   protected void onPreExecute() {
					   Log.d(LOG_TAG,  "������ ��� �������� " +url);
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
				 		     
				     
				     /////////////////////////////////////////
				  // ��������� ����������� SD
				     if (!Environment.getExternalStorageState().equals(
				         Environment.MEDIA_MOUNTED)) {
				    	
				       return file;
				     }
				     // �������� ���� � SD				  
				     File szFile = new File(sdPath +"/"+filename);
				     
				     try {
				    	 fos = new FileOutputStream(szFile);
					     inputStream = urlConnection.getInputStream();
					  
					     totalSize = urlConnection.getContentLength();
					     downloadedSize = 0;
					     
					     buffer = new byte[1024];
					     bufferLength = 0;
					 
					     // ������ �� ����� � ����� � �����, 
					     // � ������ ��������� ��������� ��������
					     while ((bufferLength = inputStream.read(buffer)) > 0) {
					      fos.write(buffer, 0, bufferLength);
					      downloadedSize += bufferLength;
					      publishProgress(downloadedSize, totalSize);
					      
					      
					     }
					 
					     fos.close();
					     inputStream.close();
					 
					     return file;
				     } catch (IOException e) {
				       e.printStackTrace();
				       
				     }
				   
				     //////////////////////////////////////
				    
				    } catch (MalformedURLException e) {
				     e.printStackTrace();
				     m_error = e;
				    } catch (IOException e) {
				     e.printStackTrace();
				     m_error = e;
				    }
				 
				    return null;
				   }
				 
				   // ��������� progressDialog
				   protected void onProgressUpdate(Integer... values) {
					    progressDialog
					      .setProgress((int) ((values[0] / (float) values[1]) * 100));
					   };
				 
				   @Override
				   protected void onPostExecute(File file) {
				    // ���������� ���������, ���� �������� ������
				    if (m_error != null) {
				     m_error.printStackTrace();
				     
				     return;
				    }
				    progressDialog.hide();
				    // ��������� �������� 

					   Intent intent = new Intent(PrezentationsActivity.this, PrezentationsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					   startActivity(intent);
				    
				   }
				  }.execute(url);
				 }
			
			public boolean onCreateOptionsMenu(Menu menu) {
				
				MenuItem mi = menu.add(0, 1, 0, R.string.action_settings );									
				mi.setIntent(new Intent(this, PrefActivity.class));
				
				MenuItem mie = menu.add(0, 1, 0, R.string.about );									
				mie.setIntent(new Intent(this, Info.class));
					
				//getMenuInflater().inflate(R.menu.main, menu);
				return super.onCreateOptionsMenu(menu);		
				    
			}
			
}






