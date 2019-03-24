package com.example.sudouser.nadgodzinki;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.BuckUp.BuckUpAlarmBroadcastReceiver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.TimeZone;

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
    private static final int pendingIntentRequestCode = 2;

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
                        createAlarmManager();
                        break;
                    case "buckupDay":
                        createAlarmManager();
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
            long today = LocalDate.now().toEpochDay() * 1000 * 3600 * 24;
            //LocalDate.now().plusDays()
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(today);
            calendar.add(Calendar.DAY_OF_WEEK, Math.abs(calendar.get(Calendar.DAY_OF_WEEK) - chosenDay));
            calendar.add(Calendar.HOUR_OF_DAY, 18);
            long wynik = calendar.getTimeInMillis();
            mAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), BuckUpAlarmBroadcastReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), pendingIntentRequestCode,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT); // TODO ewentualnie zamienić na cancell current
            mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, mPendingIntent);
        }
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










