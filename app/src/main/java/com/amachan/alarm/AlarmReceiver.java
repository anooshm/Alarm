
package com.amachan.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Inside Alarm receiver");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (!isValid(calendar, intent)) {
            Log.d(TAG, "return alarm");
            return;
        }
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        i.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
        i.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE)+1);
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "Inside Alarm receiver - " + calendar.get(Calendar.HOUR_OF_DAY) + ", "+calendar.get(Calendar.MINUTE));
        context.startActivity(i);

    }

    private boolean isValid(Calendar calendar,Intent intent) {
        int calHour = calendar.get(Calendar.HOUR_OF_DAY);
        int calMinute = calendar.get(Calendar.MINUTE);
        int calDay = calendar.get(Calendar.DAY_OF_WEEK); 
        int hours = intent.getIntExtra("hour",25);
        int minutes = intent.getIntExtra("minute", 61);
        int repeat = intent.getIntExtra("repeat", -1);
        if(calHour == hours && (calMinute + 2 > minutes && calMinute - 2 < minutes) ){
            if (repeat == 2 && (calDay > 1 && calDay<7) || repeat == 1 && (calDay == 1 || calDay == 7) ) {
                Log.d(TAG, "not in the repeating zone");
                return false;
            }
            Log.d(TAG, "hour matching");
            return true;
        }

        return false;
    }

}
