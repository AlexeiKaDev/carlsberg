package com.ak.carlsberg;

/**
 * Created by A_kevshin on 07.03.2017.
 */
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmBroadcast extends BroadcastReceiver{

    SharedPreferences sp;

@Override
public void onReceive(Context context,Intent intent){
        PowerManager pm=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Просыпайся");

//Осуществляем блокировку
        wl.acquire();

        Intent i = new Intent(context, MyService.class);
        context.startService(i);
        Log.i("LOG_TAG", "сработал будильник " );
        wl.release();
        }

    public void SetNews(Context context)
        {
            Calendar aCalendar = Calendar.getInstance();
            aCalendar.set(Calendar.HOUR_OF_DAY, 18);
            aCalendar.set(Calendar.MINUTE, 00);
            aCalendar.set(Calendar.MILLISECOND, 00);
            aCalendar.set(Calendar.SECOND,00);

            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent=new Intent(context,AlarmBroadcast.class);
            PendingIntent pi=PendingIntent.getBroadcast(context,1,intent,0);
            am.setRepeating(AlarmManager.RTC_WAKEUP, aCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
            Log.i("LOG_TAG", "Будильник для синхронизации установлен на " + getFullTime(aCalendar.getTimeInMillis()) + "  " +intent);
        }

public void CancelAlarm(Context context)
{
        Intent intent=new Intent(context,AlarmBroadcast.class);
        PendingIntent sender=PendingIntent.getBroadcast(context,1,intent,0);
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);//Отменяем будильник, связанный с интентом данного класса
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