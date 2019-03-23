package com.example.sudouser.nadgodzinki;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;


import com.example.sudouser.nadgodzinki.BuckUp.BuckUpAlarmBroadcastReceiver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; // to tez działa
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


public class SettingsPreferences extends AppCompatActivity // moze też być  PreferenceActivity
{
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private AlarmManager mAlarm;
    private PendingIntent mPendingIntent;
    private static final int pendingIntentRequestCode = 17;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsPreferencesFragment() ).commit();


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        createAlarmManager();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
            {
                switch (key)
                {
                    case "buckup_enabled":
                        if (sharedPreferences.getBoolean(key, true))
                            createAlarmManager();
                        else
                            cancelAlarm();
                        break;
                    case "buckupList":
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        super.onDestroy();
    }

    public static class SettingsPreferencesFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }
    }
    /**
     * Metoda generująca nam alarm Manager, który służy do tego aby sustem wysyłał broadCast do naszej aplikacji
     * dokładniej do BuckUpAlarmBroadcastReceiver'a aby ten mógł wywołać notyfikację. Notyfikacja z kolei
     * wywołuje Intent StatsInfoActivity w jednoczesnym wywołaniem metody makeBuckup()
     */
    private void createAlarmManager()
    {
        if (mSharedPreferences.getBoolean("buckup_enabled", true))
        {
            mAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, BuckUpAlarmBroadcastReceiver.class)
                    .setAction("com.example.sudouser.nadgodzinki");
            mPendingIntent = PendingIntent.getBroadcast(this, pendingIntentRequestCode,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(), 10 * 1000, mPendingIntent);
        }
        else
        if (mAlarm != null)
            mAlarm.cancel(mPendingIntent);
    }

    /**
     * metoda kasująca alarm służący do wywołania notyfikacji o potrzebie zrobienia buckup'u
     */
    private void cancelAlarm()
    {
        if (mAlarm != null)
            mAlarm.cancel(mPendingIntent);
    }
}










