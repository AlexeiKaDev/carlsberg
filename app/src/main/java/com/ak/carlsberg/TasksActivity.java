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
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;

public class TasksActivity extends AppCompatActivity {
		
	private static final String DB_NAME = "monolit.db";
    private static final String DB_Carlsberg = "carlsberg.db";
	String[] sNames,sId;
	SharedPreferences sp;
	SQLiteDatabase db;
    String base,kanal;
    boolean orders;
    String Ch="",bossTel="",SMS="";
    ArrayList SmsList = new ArrayList();

	private ArrayList<HashMap<String, Object>> despatchList;
	private static final String TITLE = "Point Name";
    private static final String COMPANY = "Company Name";
	private static final String DESP1= "scu1";
	/** Called when the activity is first created. */
	
	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tasks);
   
    despatchList = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> hm;
    final ListView lv = (ListView)findViewById(R.id.listViewDes);
    
    int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
    TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
    Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
    if(actionBarTitleView != null){
        actionBarTitleView.setTypeface(fonts);
        actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
    }
    
    @SuppressWarnings("unused")
	final String LOG_TAG = null;
/******************************************���������� ��� �������*************************************************/
    sp = PreferenceManager.getDefaultSharedPreferences(this);
    int baseid =sp.getInt("baseid",0);
    String person =sp.getString("person", "");
    int rubikon1 =Integer.parseInt(sp.getString("rubikon1", "1"));
    int chanel =sp.getInt("chanel", 0);
    String sId1 =sp.getString("scuid1", "");
        int idPos1 =Integer.parseInt(sp.getString("idscupos1", "0"));
        boolean orders =sp.getBoolean("orders", false);

        if (baseid ==0) {base = "Рабочая";};
        if (baseid ==1) {base = "Маршрутная";};
        if (baseid ==2) {base = "Активная";};
/*****************************************************************************************************************/   
    Calendar bCalendar = Calendar.getInstance();
    bCalendar.add(Calendar.MONTH, 0);
    bCalendar.set(Calendar.HOUR_OF_DAY, 00);
    bCalendar.set(Calendar.MINUTE, 00);
    bCalendar.set(Calendar.MILLISECOND, 00);
    bCalendar.set(Calendar.SECOND,00);
    int day = bCalendar.get(Calendar.DAY_OF_MONTH);
    bCalendar.set(Calendar.DATE, day-1);
    
    String thisday = getFullTime(bCalendar.getTimeInMillis());
    
    bCalendar.set(Calendar.DATE, 1);
    
	String firstday =  getFullTime(bCalendar.getTimeInMillis());
    // set actual maximum date of previous month
    bCalendar.set(Calendar.DATE, bCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    //read it
    String todaydate = getFullTime(bCalendar.getTimeInMillis());
    
    Log.i("LOG_TAG", "Первый день месяца " + firstday + " последний день месяца" + todaydate + "Этот день " + thisday);

        TextView tresholdView = (TextView)findViewById(R.id.textView12);

            ExternalDbOpenHelper extdbc = new ExternalDbOpenHelper(this, DB_Carlsberg);
            db = extdbc.openDataBase();
            try {
                db.beginTransaction();
                String queryC = "SELECT tresholds FROM Tresholds WHERE ScuId ='"+sId1+"'";

                Cursor cursorC = db.rawQuery(queryC, null);
                while (cursorC.moveToNext()) {
                    if (cursorC.isNull(0)) {
                        tresholdView.setText(" нет порога ");
                    } else {
                        tresholdView.setText(cursorC.getString(0) + " дал");
                    }
                }
                cursorC.close();
                String queryChanels =null;
                /***************************выбор канала продаж*****************************************************/
                if (chanel == 0) {queryChanels ="SELECT code FROM Segments ";kanal="ВСЕ";};
                if (chanel == 1) {queryChanels ="SELECT code FROM Segments WHERE channel_type ='off' ";kanal="Офф трейд";}
                if (chanel == 2) {queryChanels ="SELECT code FROM Segments WHERE channel_type ='on' ";kanal="Он трейд";}

                Cursor CursorCh = db.rawQuery(queryChanels, null);
                while (CursorCh.moveToNext()) {
                      Ch = Ch +",'"+CursorCh.getString(0)+"'";
                }
                CursorCh.close();
                /***************************************************************************************************/

                db.setTransactionSuccessful();
            } catch (SQLException e) {
            } finally {
                db.endTransaction();
                db.close();
            }

        ExternalDbOpenHelper extdb = new ExternalDbOpenHelper(this, DB_NAME);
    db = extdb.openDataBase();
                            try {
                                    db.beginTransaction();
                                    String query = "SELECT WareName,WareId FROM WARE WHERE WareId IN(SELECT WareId From WareRangeItem)";

                                    Cursor cursor = db.rawQuery(query, null);

                                    sNames= new String[cursor.getCount()];
                        			sId =  new String[cursor.getCount()];
                                    while (cursor.moveToNext()) {
                                			sNames[cursor.getPosition()]= cursor.getString(0);
                                 			sId[cursor.getPosition()]=  cursor.getString(1);

                                	}
                                    if(sId1.isEmpty()==true){sId1 = sId[0];};
                                	cursor.close();
                    /*****************************************Выборка данных по точкам для расчета***************************/

                            //запросы рабочая база
                                String queryWork = "SELECT DISTINCT t.ClientAddressName,t.LegalName,t.ClientAddressId AS ClientAddressId,t.TradeTypeTreeId , t1.REAL FROM "+
                                        " (SELECT ClientAddressName,LegalName,ClientAddress.ClientAddressId,TradeTypeTreeId  FROM ClientAddress "+
                                        " LEFT JOIN DespatchLine ON ClientAddress.ClientAddressId = DespatchLine.ClientAddressId WHERE TradeTypeTreeId  IN ("+Ch.substring(1)+")) t "+
                                        " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId ORDER by ClientAddressId desc";

                                String queryWorkCount ="SELECT COUNT (t2.ClientAddressId), COUNT(t2.REAL),ROUND (CAST (COUNT(t2.T) AS REAL)/COUNT (t2.ClientAddressId) *100,2), SUM(t2.REAL), COUNT(t2.T) FROM ( "+
                                        " SELECT DISTINCT t.ClientAddressId,t.ClientAddressName,t.LegalName,t.TradeTypeTreeId , t1.REAL, t3.REAL AS T FROM "+
                                        " (SELECT ClientAddressName,LegalName,ClientAddress.ClientAddressId,TradeTypeTreeId  FROM ClientAddress "+
                                        " LEFT JOIN DespatchLine ON ClientAddress.ClientAddressId = DespatchLine.ClientAddressId WHERE TradeTypeTreeId  IN ("+Ch.substring(1)+")) t "+
                                        " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
                                        " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId HAVING REAL >=("+rubikon1*1.00+"/Unit.FactorValue )) t3 ON t.ClientAddressId = t3.ClientAddressId) t2";


                                //запросы маршрутная база
                                String queryRoute ="SELECT DISTINCT t.ClientAddressName,t.LegalName,t.ClientAddressId AS ClientAddressId,t.TradeTypeTreeId , t1.REAL FROM "+
                                        " (SELECT DISTINCT ClientAddressName,LegalName,CRMJobSchedule.ClientAddressId,p.TradeTypeTreeId   FROM CRMJobSchedule "+
                                        " LEFT JOIN (SELECT ClientAddressName,LegalName,ClientAddress.ClientAddressId,TradeTypeTreeId  FROM ClientAddress "+
                                        " LEFT JOIN DespatchLine ON ClientAddress.ClientAddressId = DespatchLine.ClientAddressId ) p ON CRMJobSchedule.ClientAddressId = p.ClientAddressId WHERE p.TradeTypeTreeId  IN ("+Ch.substring(1)+")) t "+
                                        " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId ORDER by ClientAddressId desc";

                                String queryRouteCount="SELECT COUNT (t2.ClientAddressId), COUNT(t2.REAL),ROUND (CAST (COUNT(t2.T) AS REAL)/COUNT (t2.ClientAddressId) *100,2), SUM(t2.REAL), COUNT(t2.T) FROM ( "+
                                        " SELECT t.ClientAddressName,t.LegalName,t.ClientAddressId,t.TradeTypeTreeId , t1.REAL, t3.REAL AS T FROM "+
                                        " (SELECT DISTINCT ClientAddressName,LegalName,CRMJobSchedule.ClientAddressId,p.TradeTypeTreeId   FROM CRMJobSchedule "+
                                        " LEFT JOIN (SELECT ClientAddressName,LegalName,ClientAddress.ClientAddressId,TradeTypeTreeId  FROM ClientAddress "+
                                        " LEFT JOIN DespatchLine ON ClientAddress.ClientAddressId = DespatchLine.ClientAddressId ) p ON CRMJobSchedule.ClientAddressId = p.ClientAddressId WHERE p.TradeTypeTreeId  IN ("+Ch.substring(1)+")) t "+
                                        " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
                                        " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId HAVING REAL >=("+rubikon1*1.00+"/Unit.FactorValue )) t3 ON t.ClientAddressId = t3.ClientAddressId) t2";

                                //запросы активная база
                                String queryActive=" SELECT t.ClientAddressName,t.LegalName,t.ClientAddressId AS ClientAddressId,t.TradeTypeTreeId , t1.REAL FROM "+
                                        " (SELECT DISTINCT ClientAddressName,LegalName,DespatchLine.ClientAddressId,p.TradeTypeTreeId  FROM DespatchLine "+
                                        " LEFT JOIN (SELECT ClientAddressName,LegalName,ClientAddress.ClientAddressId, TradeTypeTreeId  FROM ClientAddress "+
                                        " LEFT JOIN DespatchLine ON ClientAddress.ClientAddressId = DespatchLine.ClientAddressId) p ON DespatchLine.ClientAddressId = p.ClientAddressId WHERE p.TradeTypeTreeId  IN ("+Ch.substring(1)+") AND DespatchLine.CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day')) t "+
                                        " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId ORDER by ClientAddressId desc";

                                String queryActiveCount="SELECT COUNT (t2.ClientAddressId), COUNT(t2.REAL),ROUND (CAST (COUNT(t2.T) AS REAL)/COUNT (t2.ClientAddressId) *100,2), SUM(t2.REAL), COUNT(t2.T) FROM ( "+
                                        " SELECT t.ClientAddressName,t.LegalName,t.ClientAddressId,t.TradeTypeTreeId, t1.REAL, t3.REAL AS T FROM "+
                                        " (SELECT DISTINCT ClientAddressName,LegalName,DespatchLine.ClientAddressId,p.TradeTypeTreeId   FROM DespatchLine "+
                                        " LEFT JOIN (SELECT ClientAddressName,LegalName,ClientAddress.ClientAddressId,TradeTypeTreeId   FROM ClientAddress "+
                                        " LEFT JOIN DespatchLine ON ClientAddress.ClientAddressId = DespatchLine.ClientAddressId ) p ON DespatchLine.ClientAddressId = p.ClientAddressId WHERE p.TradeTypeTreeId  IN ("+Ch.substring(1)+")) t "+
                                        " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
                                        " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId HAVING REAL >=("+rubikon1*1.00+"/Unit.FactorValue )) t3 ON t.ClientAddressId = t3.ClientAddressId) t2";

                                String ordersBegin="",ordersEnd="";
                                if (orders==true){
                                    ordersBegin="SELECT ClientAddressName,LegalName,ClientAddressId,TradeTypeTreeId,ROUND(SUM(REAL),2) FROM ( " +
                                            " SELECT ClientAddress.ClientAddressName,ClientAddress.LegalName,PPCOrder.ClientAddressId,TradeTypeTreeId,PPCOrderLine.Quantity*1.00/FactorValue AS REAL FROM Unit,PPCOrderLine,PPCOrder,ClientAddress WHERE Unit.WareId=PPCOrderLine.WareId AND PPCOrderLine.CreateId >= '"+thisday+"' AND PPCOrderLine.CreateId < date('"+thisday+"' ,'+2 day') AND Unit.UnitId='dal' AND PPCOrder.OrderNumber =PPCOrderLine.OrderNumber AND PPCOrderLine.WareId ='"+sId1+"'  AND PPCOrder.ClientAddressId =ClientAddress.ClientAddressId " +
                                            " UNION ALL ";
                                    ordersEnd=" ) GROUP BY ClientAddressId ORDER BY ClientAddressId desc";

                                    queryWorkCount =" SELECT COUNT (ClientAddressId) , COUNT(REAL) ,ROUND (CAST (COUNT(T) AS REAL)/COUNT (ClientAddressId) *100,2), SUM(REAL) , COUNT(T) FROM ( "+
                                    " SELECT ClientAddressId,ClientAddressName,LegalName,TradeTypeTreeId , SUM(REAL) AS REAL, SUM(T) AS T FROM ( "+
                                            " SELECT DISTINCT t.ClientAddressId,t.ClientAddressName,t.LegalName,t.TradeTypeTreeId , t1.REAL, t3.REAL AS T FROM "+
                                            " (SELECT ClientAddressName,LegalName,ClientAddress.ClientAddressId,TradeTypeTreeId  FROM ClientAddress "+
                                            " LEFT JOIN DespatchLine ON ClientAddress.ClientAddressId = DespatchLine.ClientAddressId WHERE Comment IN ("+Ch.substring(1)+")) t "+
                                            " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
                                            " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId HAVING REAL >=("+rubikon1*1.00+"/Unit.FactorValue )) t3 ON t.ClientAddressId = t3.ClientAddressId "+

                                    " UNION ALL "+

	                                " SELECT t.ClientAddressId,t.ClientAddressName,t.LegalName,t.TradeTypeTreeId , t1.REAL, t3.REAL AS T FROM "+
	                                " (SELECT DISTINCT ClientAddressName,LegalName,ClientAddress.ClientAddressId,TradeTypeTreeId  FROM ClientAddress "+
                                    " LEFT JOIN PPCOrder ON ClientAddress.ClientAddressId = PPCOrder.ClientAddressId WHERE TradeTypeTreeId  IN ("+Ch.substring(1)+")) t "+
                                    " LEFT JOIN (SELECT PPCOrder.ClientAddressId, CAST(ROUND( SUM(CAST ((PPCOrderLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,PPCOrderLine,PPCOrder WHERE Unit.WareId=PPCOrderLine.WareId AND PPCOrderLine.CreateId >= '"+thisday+"' AND PPCOrderLine.CreateId< date('"+thisday+"' ,'+2 day') AND Unit.UnitId='dal' AND PPCOrderLine.WareId ='"+sId1+"' AND PPCOrder.OrderNumber =PPCOrderLine.OrderNumber GROUP BY PPCOrder.ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
	                                " LEFT JOIN (SELECT PPCOrder.ClientAddressId, CAST(ROUND( SUM(CAST ((PPCOrderLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,PPCOrderLine,PPCOrder WHERE Unit.WareId=PPCOrderLine.WareId AND PPCOrderLine.CreateId>= '"+thisday+"' AND PPCOrderLine.CreateId< date('"+thisday+"' ,'+2 day') AND Unit.UnitId='dal' AND PPCOrderLine.WareId ='"+sId1+"' AND PPCOrder.OrderNumber =PPCOrderLine.OrderNumber GROUP BY PPCOrder.ClientAddressId HAVING REAL >=("+rubikon1*1.00+"/Unit.FactorValue )) t3 ON t.ClientAddressId = t3.ClientAddressId "+
                                    " WHERE t1.REAL>0) GROUP BY ClientAddressId) t2  ;";
                                    queryRouteCount =" SELECT COUNT (ClientAddressId) , COUNT(REAL) ,ROUND (CAST (COUNT(T) AS REAL)/COUNT (ClientAddressId) *100,2), SUM(REAL) , COUNT(T) FROM ( "+
                                            " SELECT ClientAddressId,ClientAddressName,LegalName,TradeTypeTreeId , SUM(REAL) AS REAL, SUM(T) AS T FROM ( "+
                                            " SELECT t.ClientAddressId,t.ClientAddressName,t.LegalName,t.TradeTypeTreeId , t1.REAL, t3.REAL AS T FROM "+
                                            " (SELECT DISTINCT CRMJobSchedule.ClientAddressId,ClientAddressName,LegalName,p.TradeTypeTreeId   FROM CRMJobSchedule "+
                                            " LEFT JOIN (SELECT ClientAddress.ClientAddressId,ClientAddressName,LegalName,TradeTypeTreeId   FROM ClientAddress "+
                                            " LEFT JOIN DespatchLine ON ClientAddress.ClientAddressId = DespatchLine.ClientAddressId ) p ON CRMJobSchedule.ClientAddressId = p.ClientAddressId WHERE p.TradeTypeTreeId  IN ("+Ch.substring(1)+")) t "+
                                            " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
                                            " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId HAVING REAL >=("+rubikon1*1.00+"/Unit.FactorValue )) t3 ON t.ClientAddressId = t3.ClientAddressId "+

                                    " UNION ALL "+

                                            " SELECT t.ClientAddressId,t.ClientAddressName,t.LegalName,t.TradeTypeTreeId , t1.REAL, t3.REAL AS T FROM "+
                                            " (SELECT DISTINCT ClientAddressName,LegalName,ClientAddress.ClientAddressId,TradeTypeTreeId  FROM ClientAddress "+
                                            " LEFT JOIN PPCOrder ON ClientAddress.ClientAddressId = PPCOrder.ClientAddressId WHERE TradeTypeTreeId  IN ("+Ch.substring(1)+")) t "+
                                            " LEFT JOIN (SELECT PPCOrder.ClientAddressId, CAST(ROUND( SUM(CAST ((PPCOrderLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,PPCOrderLine,PPCOrder WHERE Unit.WareId=PPCOrderLine.WareId AND PPCOrderLine.CreateId >= '"+thisday+"' AND PPCOrderLine.CreateId< date('"+thisday+"' ,'+2 day') AND Unit.UnitId='dal' AND PPCOrderLine.WareId ='"+sId1+"' AND PPCOrder.OrderNumber =PPCOrderLine.OrderNumber GROUP BY PPCOrder.ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
                                            " LEFT JOIN (SELECT PPCOrder.ClientAddressId, CAST(ROUND( SUM(CAST ((PPCOrderLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,PPCOrderLine,PPCOrder WHERE Unit.WareId=PPCOrderLine.WareId AND PPCOrderLine.CreateId>= '"+thisday+"' AND PPCOrderLine.CreateId< date('"+thisday+"' ,'+2 day') AND Unit.UnitId='dal' AND PPCOrderLine.WareId ='"+sId1+"' AND PPCOrder.OrderNumber =PPCOrderLine.OrderNumber GROUP BY PPCOrder.ClientAddressId HAVING REAL >=("+rubikon1*1.00+"/Unit.FactorValue )) t3 ON t.ClientAddressId = t3.ClientAddressId "+
                                            " WHERE t1.REAL>0) GROUP BY ClientAddressId) t2  ;";

                                    queryActiveCount =" SELECT COUNT (ClientAddressId) , COUNT(REAL) ,ROUND (CAST (COUNT(T) AS REAL)/COUNT (ClientAddressId) *100,2), SUM(REAL) , COUNT(T) FROM ( "+
                                            " SELECT ClientAddressId,ClientAddressName,LegalName,TradeTypeTreeId , SUM(REAL) AS REAL, SUM(T) AS T FROM ( "+
                                            " SELECT t.ClientAddressId,t.ClientAddressName,t.LegalName,t.TradeTypeTreeId, t1.REAL, t3.REAL AS T FROM "+
                                            " (SELECT DISTINCT DespatchLine.ClientAddressId,ClientAddressName,LegalName,p.TradeTypeTreeId  FROM DespatchLine "+
                                            " LEFT JOIN (SELECT ClientAddress.ClientAddressId,ClientAddressName,LegalName,TradeTypeTreeId  FROM ClientAddress "+
                                            " LEFT JOIN DespatchLine ON ClientAddress.ClientAddressId = DespatchLine.ClientAddressId ) p ON DespatchLine.ClientAddressId = p.ClientAddressId WHERE p.TradeTypeTreeId IN ("+Ch.substring(1)+")) t "+
                                            " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
                                            " LEFT JOIN (SELECT DespatchLine.ClientAddressId, CAST(ROUND( SUM(CAST ((DespatchLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,DespatchLine WHERE Unit.WareId=DespatchLine.WareId AND CustDate >= '"+firstday+"' AND CustDate < date('"+todaydate+"' ,'+1 day') AND Unit.UnitId='dal' AND Despatchline.WareId ='"+sId1+"' GROUP BY ClientAddressId HAVING REAL >=("+rubikon1*1.00+"/Unit.FactorValue )) t3 ON t.ClientAddressId = t3.ClientAddressId "+

                                    " UNION ALL "+

                                            " SELECT t.ClientAddressId,t.ClientAddressName,t.LegalName,t.TradeTypeTreeId , t1.REAL, t3.REAL AS T FROM "+
                                            " (SELECT DISTINCT ClientAddressName,LegalName,ClientAddress.ClientAddressId,TradeTypeTreeId FROM ClientAddress "+
                                            " LEFT JOIN PPCOrder ON ClientAddress.ClientAddressId = PPCOrder.ClientAddressId WHERE TradeTypeTreeId IN ("+Ch.substring(1)+")) t "+
                                            " LEFT JOIN (SELECT PPCOrder.ClientAddressId, CAST(ROUND( SUM(CAST ((PPCOrderLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,PPCOrderLine,PPCOrder WHERE Unit.WareId=PPCOrderLine.WareId AND PPCOrderLine.CreateId >= '"+thisday+"' AND PPCOrderLine.CreateId< date('"+thisday+"' ,'+2 day') AND Unit.UnitId='dal' AND PPCOrderLine.WareId ='"+sId1+"' AND PPCOrder.OrderNumber =PPCOrderLine.OrderNumber GROUP BY PPCOrder.ClientAddressId) t1 ON t.ClientAddressId = t1.ClientAddressId "+
                                            " LEFT JOIN (SELECT PPCOrder.ClientAddressId, CAST(ROUND( SUM(CAST ((PPCOrderLine.Quantity)AS REAL)/Unit.FactorValue ),2) AS REAL)AS REAL FROM Unit,PPCOrderLine,PPCOrder WHERE Unit.WareId=PPCOrderLine.WareId AND PPCOrderLine.CreateId>= '"+thisday+"' AND PPCOrderLine.CreateId< date('"+thisday+"' ,'+2 day') AND Unit.UnitId='dal' AND PPCOrderLine.WareId ='"+sId1+"' AND PPCOrder.OrderNumber =PPCOrderLine.OrderNumber GROUP BY PPCOrder.ClientAddressId HAVING REAL >=("+rubikon1*1.00+"/Unit.FactorValue )) t3 ON t.ClientAddressId = t3.ClientAddressId "+
                                            " WHERE t1.REAL>0) GROUP BY ClientAddressId) t2  ;";
                                }
                                String query1="",query2="";
                                if (baseid == 0){query1 =ordersBegin+ queryWork+ordersEnd;query2 =queryWorkCount;};
                                if (baseid == 1){query1 =ordersBegin+ queryRoute+ordersEnd;query2 =queryRouteCount;};
                                if (baseid == 2){query1 =ordersBegin+ queryActive+ordersEnd;query2 =queryActiveCount;};
                                Log.i("LOG_TAG", "запрос " + query2);
                                	Cursor cursor1 = db.rawQuery(query1, null);
                                     int i =1;
                                	 while (cursor1.moveToNext()) {

                                     	hm = new HashMap<String, Object>();
                                         hm.put("Number", i + ".");
                                         hm.put(TITLE, cursor1.getString(0));
                                          hm.put(COMPANY, cursor1.getString(1));
                                         hm.put(DESP1, cursor1.getString(4));
                                         despatchList.add(hm);
                                        i++;
                             	}
                                	 cursor1.close();

                                Cursor cursor2 = db.rawQuery(query2, null);
                                while (cursor2.moveToNext()) {
                                    /**всего точек**/
                                    TextView totalPoints = (TextView) findViewById(R.id.textView17);
                                    totalPoints.setText(cursor2.getString(0) + " тт");
                                    SMS ="Точек всего: " +cursor2.getString(0)+"\n";
                                    SmsList.add(SMS);
                                    /**отгружено тт**/
                                    TextView despatchedPoins = (TextView) findViewById(R.id.TextView03);
                                    despatchedPoins.setText(cursor2.getString(4) + " тт");
                                    SMS ="Отгружено точек: " +cursor2.getString(4)+"\n";
                                    SmsList.add(SMS);
                                    /**дистрибьюция**/
                                    TextView distribution = (TextView) findViewById(R.id.TextView01);
                                    if (cursor2.getString(2)==null){distribution.setText(0+"%");}
                                    else{distribution.setText(cursor2.getString(2)+"%");}
                                    SMS ="Дистрибьюция: " +cursor2.getString(2)+"%\n";
                                    SmsList.add(SMS);
                                    /**Дистрибьюция**/
                                    TextView despatchedVolum = (TextView) findViewById(R.id.TextView02);
                                    if (cursor2.getString(3)==null){despatchedVolum.setText(0+" дал");}
                                    else{despatchedVolum.setText(cursor2.getString(3)+" дал");}
                                    SMS ="Отгружено всего: " +cursor2.getString(3)+"дал\n";
                                    SmsList.add(SMS);
                                }
                                cursor2.close();

                 SimpleAdapter adapterList = new SimpleAdapter(this, despatchList,
         		R.layout.list_item_tasks, new String[] { "Number",TITLE,COMPANY, DESP1},
         		new int[] {R.id.textView19,R.id.TextView07, R.id.textView18, R.id.TextView05 });

                 lv.setAdapter(adapterList);

                                    db.setTransactionSuccessful();
                            } catch (SQLException e) {
                            } finally {
                                    db.endTransaction();
                                    db.close();
                            }

		final Switch switch1 = (Switch)findViewById(R.id.switch1);
        orders =sp.getBoolean("orders", false);
        switch1.setChecked(orders);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor ed = sp.edit();
                ed.putBoolean("orders", isChecked);
                ed.commit();
            }
        });;

 /***************************************************************************************************************/
		final Spinner spinner2 = (Spinner)findViewById(R.id.spinner2);

		ArrayAdapter<?> adapter3 =
                ArrayAdapter.createFromResource(this, R.array.base_of_sales, android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter3);
		spinner2.setSelection(baseid);
 /***************************************************************************************************************/
        final Spinner spinner5 = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter<?> adapter5 =
                ArrayAdapter.createFromResource(this, R.array.Chanel, android.R.layout.simple_spinner_item);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(adapter5);
        spinner5.setSelection(chanel);
/***************************************************************************************************************/    
final Spinner spinner1 = (Spinner)findViewById(R.id.Spinner04);
    
    ArrayAdapter<?> adapter1 = 
    		ArrayAdapter.createFromResource(this, R.array.rubikonss, android.R.layout.simple_spinner_item);
    	adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    spinner1.setAdapter(adapter1);
    spinner1.setSelection(rubikon1-1);
/****************************************************************************************************************/    
    final Spinner spinner4 = (Spinner)findViewById(R.id.Spinner02);
    
    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, sNames);
    
    	adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	


    	spinner4.setAdapter(adapter2);
    	spinner4.setSelection(idPos1);
 /***************************************************************************************************************/
        spinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                SharedPreferences.Editor ed = sp.edit();
                int position = selectedItemPosition;
                Log.i("LOG_TAG", "������� �������" +position);
                ed.putInt("chanel", position);
                ed.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                SharedPreferences.Editor ed = sp.edit();
                int position = selectedItemPosition;
                Log.i("LOG_TAG", "������� �������" +position);
                ed.putInt("baseid", position);
                ed.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                SharedPreferences.Editor ed = sp.edit();
                int position = selectedItemPosition + 1;
                Log.i("LOG_TAG", "������� �������" + position);
                ed.putString("rubikon1", String.valueOf(position));
                ed.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });

        spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	@SuppressLint("CommitPrefEdits")
			@Override
        	public void onItemSelected(AdapterView<?> parent,
        			View itemSelected, int selectedItemPosition, long selectedId) {
        		
        			SharedPreferences.Editor ed = sp.edit();  
        			int idScu = selectedItemPosition;
        			Log.i("LOG_TAG", "Id ���������� ���" + sId[idScu]);
        			ed.putString("scuid1", sId[idScu] );
        			ed.commit();
        			ed.putString("idscupos1", String.valueOf(idScu));       			
        			ed.commit();     		
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
 	    	 Intent intent = new Intent(TasksActivity.this, TasksActivity.class);
	    	   startActivity(intent);	     	 	    	   
 	       }
 	     };
 	 
 	     // �������� ���������� ������ OK (btnOk)
 	    Button01.setOnClickListener(perescet);

        Button point = (Button) findViewById(R.id.point);
        OnClickListener sendsms = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LOG_TAG", SMS);
               /* SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendMultipartTextMessage(bossTel, null, SmsList,null, null);
                Toast.makeText(getApplicationContext(), "Сообщение отправлено", Toast.LENGTH_LONG).show();*/
                /*Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>"+SMS+"</p>"));
                startActivity(Intent.createChooser(sharingIntent, "Отправить с помощью"));*/

            }
        };

        // �������� ���������� ������ OK (btnOk)
        point.setOnClickListener(sendsms);


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
