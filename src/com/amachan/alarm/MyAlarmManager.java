
package com.amachan.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class MyAlarmManager {

    private static AlarmManager alarmMgr;
    private static PendingIntent alarmIntent;

    public static void setAlarm(Context context, String time, int repeat) {
        String[] timeValue = time.split(":");
        int hours = Integer.parseInt(timeValue[0]);
        int minutes = Integer.parseInt(timeValue[1]) - 1;

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("hour", hours);
        intent.putExtra("repeat", repeat);
        intent.putExtra("minute", minutes);
        alarmIntent = PendingIntent.getBroadcast(context, hours*60 + minutes, intent, 0);

        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        // setRepeating() lets you specify a precise custom interval--in this
        // case,
        long millis = calendar.getTimeInMillis();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 60*24, alarmIntent);
    }
    
    public static void cancelAlarm(Context context, String time) {
        String[] timeValue = time.split(":");
        int hours = Integer.parseInt(timeValue[0]);
        int minutes = Integer.parseInt(timeValue[1]);

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, hours*60 + minutes, intent, 0);
        alarmMgr.cancel(alarmIntent);
    }

}
