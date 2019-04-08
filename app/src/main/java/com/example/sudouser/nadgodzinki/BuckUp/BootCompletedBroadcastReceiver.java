package com.example.sudouser.nadgodzinki.BuckUp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;

import androidx.preference.PreferenceManager;

/**
 * BroadcastReceiver który odpowiada za ponowne uruchomienie alarmu w przypadku gdy
 * telefon zostanie ponownie włączony po wyłączeniu. Wyłączenie tel powoduje wyłączenie
 * wszystkich alarmów dlatego my go tutaj uruchomimy ponownie.
 */
public class BootCompletedBroadcastReceiver extends BroadcastReceiver
{
    private SharedPreferences mSharedPreferences;
    private static final int pendingIntentRequestCode = 2;

    /**
     * Metoda, która będzie wywołana gdy, zgodznie z tym co jest w manifeście,
     * system wyśle intent, który ma w setAction() ustawione
     * "android.intent.action.BOOT_COMPLETED". jeśli uruchomimy ponownie telfon
     * to wszystkie alarmy są kasowane, ale system wysyła też intent, że boot completed
     * wtedy wychwytując ten intent uruchomimy metodę onReceive, która ponownie ustawi nam
     * zadany alarm według parametrów zapsianych w preferencjach.
     * @param context
     * @param intentFromSystem intent, który wysyła system gdy jest ponownie uruchomiony
     */
    @Override
    public void onReceive(Context context, Intent intentFromSystem)
    {

        // to należy uruchomić przy użyciu powyższych metod
        // https://developer.android.com/guide/components/broadcasts#effects-process-state
        // chedule a JobService from the receiver using the JobScheduler, so the system knows that the process continues to perform active work. For more information, see Processes and Application Life Cycle.
        if (intentFromSystem.getAction() != null && intentFromSystem.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            createAlarmManager(context);

            /*
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (mSharedPreferences.getBoolean("buckup_enabled", true))
            {
                AlarmManager mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, BuckUpAlarmBroadcastReceiver.class);//.setAction("com.example.sudouser.nadgodzinki.BuckUpNotification");
                PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                mAlarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 10 * 1000, 10* 1000, mPendingIntent);
            }
             */
        }
    }

    private void createAlarmManager(Context context)
    {
        AlarmManager mAlarm;
        PendingIntent mPendingIntent;
        if (mSharedPreferences.getBoolean("buckup_enabled", true))
        {
            long intervalMillis;
            switch (mSharedPreferences.getString("buckupList", "week"))
            {
                case "week":
                    intervalMillis = (long) 1000 * 3600 * 24 * 7;
                    break;
                case "month":
                    intervalMillis = (long) 1000 * 3600 * 24 * 7 * 4 ;
                    break;
                case "quarter":
                    intervalMillis = (long) 1000 * 3600 * 24 * 7 * 13;
                    break;
                default:
                    intervalMillis = (long) 1000 * 3600 * 24 * 7;
                    break;
            }
            int chosenDay = Integer.valueOf(mSharedPreferences.getString("buckupDay", "6"));//getInt("buckupDay", 6);
            Calendar calendar = android.icu.util.Calendar.getInstance();
            calendar.set(android.icu.util.Calendar.HOUR,0);
            calendar.set(android.icu.util.Calendar.MINUTE, 0);
            calendar.add(android.icu.util.Calendar.DAY_OF_WEEK, Math.abs(calendar.get(android.icu.util.Calendar.DAY_OF_WEEK) - chosenDay));
            calendar.add(android.icu.util.Calendar.HOUR_OF_DAY, 19);
            calendar.add(android.icu.util.Calendar.MINUTE, 0);

            mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context.getApplicationContext(),
                    BuckUpAlarmBroadcastReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                    pendingIntentRequestCode,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // TODO ewentualnie zamienić na cancell current
            mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    intervalMillis, mPendingIntent);
        }
    }
}
