package com.example.sudouser.nadgodzinki.BuckUp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

import androidx.preference.PreferenceManager;

/**
 * BroadcastReceiver który odpowiada za ponowne uruchomienie alarmu w przypadku gdy
 * telefon zostanie ponownie włączony po wyłączeniu. Wyłączenie tel powoduje wyłączenie
 * wszystkich alarmów dlatego my go tutaj uruchomimy ponownie.
 */
public class BootCompletedBroadcastReceiver extends BroadcastReceiver
{
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
            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (mSharedPreferences.getBoolean("buckup_enabled", true))
            {
                AlarmManager mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, BuckUpAlarmBroadcastReceiver.class);//.setAction("com.example.sudouser.nadgodzinki.BuckUpNotification");
                PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                mAlarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 10 * 1000, 10* 1000, mPendingIntent);
            }
        }
    }
}
