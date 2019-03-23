package com.example.sudouser.nadgodzinki;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.BuckUp.BuckUpAlarmBroadcastReceiver;
import com.example.sudouser.nadgodzinki.db.Item;

import java.time.LocalDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.*;
import androidx.preference.PreferenceManager;


public class MainActivity extends AppCompatActivity
{
    private ItemViewModel mItemViewModel;
    private SharedPreferences mSharedPreferences;
    private AlarmManager mAlarm;
    private PendingIntent mPendingIntent;
    private static final int pendingIntentRequestCode = 17;

    /**
     * Jest to pierwsza z metod callback - jest ona wykonywana tylko raz w momencie gdy tworzona jest
     * activity.
     * @param savedInstanceState to jest parametr, który przechowuje poprzednio zapisany stan aktywności
     *                           czyli w momencie gdy, zmieniamy ułożenie telefonu na landscape to,
     *                           w tę zmienną wpisywane są wartości wszelkich parametrów gdy activity jest
     *                           w pozycji portrait. jeśli aktywność nie była nigdy wcześniej tworzona
     *                           to obiekt ten ma wartość null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // TODO zrobić najpierw nowy wątek dla mItemViewModel'a to nam potwierdzi lub obali koncepcję
        // czy można w ten sposób uruchamiać Activity
        mItemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        final CalendarView calendar = findViewById(R.id.mainCalendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            // powyższego lisetener'a można zapisać w postaci wyrażenia lambda
            // calendar.setOnDateChangeListener( (@NonNull CalendarView view, int year, int month, int dayOfMonth) ->
            // mItemViewModel.setLocalDate(LocalDate.of(year, month+1, dayOfMonth)));
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                mItemViewModel.setLocalDate(LocalDate.of(year, month+1, dayOfMonth));
                /*
                Reports synchronization on a call to getClass(). If the class containing the synchronization is
                subclassed, the subclass will synchronize on a different class object. Usually the call to getClass()
                can be replaced with a class literal expression, for example String.class. An even better solution is
                 synchronizing on a private static final lock object, access to which can be completely controlled.
                 */
            }
        });
        if (mItemViewModel.getLocalDate().getValue() != null)
            calendar.setDate(mItemViewModel.getLocalDate().getValue().toEpochDay() * 24 * 3600 * 1000);
        else
            mItemViewModel.setLocalDate(LocalDate.ofEpochDay( (calendar.getDate()/(24*3600*1000))));




        // tutaj będzie wywołany wątek, który zajmie się wygenerowaniem ustawień.
        // todo działa, ale nie działa bezpośrednio po ustawieniu w ustawieniach tam też trzeba
        // zaimplementowac mSharedPreferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        createAlarmManager();

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


    @Override
    protected void onDestroy()
    {
        // this.unregisterReceiver(br);
        // casujemy listentery
        //mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        cancelAlarm();
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menuItemStatistics:
                showStatistics();
                return true;

            case R.id.menuItemSettings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * FUnkcja wywoływana po naciśnięciu przycisku dodaj. Dalej jest opisane co po kolei się dzieje
     * 1. Przypisujemy pola w które wprowadzamy godzinę i minutę do odpowiednich lokalnych zmiennych
     * 2. Wyłuskujemy wartości, które są tam wpisane.
     * 3. Jeśli nic nie ma wpisanego to wyskakuje dialogue że nie wprowadziłem żadnej nadgodziny
     * 4. Transformuję stringi do Integerów.
     * 5. Zczytuję datę wybraną przez urzytkownika oraz dzisiejszą i porównuję je
     * 6. jeśli  jest mniej niż 60 min lub data jest późniejsza
     * 7. jeśli jest ok to zapisz dane do bazy danych.
     * @param view
     */
    public void addMainButtonClicked(View view)
    {
        insertTimeChecker(true);
    }

    public void deleteOvertime(View view)
    {
        insertTimeChecker(false);
    }

    public void showStatistics()
    {
        Intent intent = new Intent(this, StatsInfoActivity.class).putExtra("fromNotifier", false);
        startActivity(intent);
    }

    public void showSettings()
    {
        Intent intent = new Intent(this, SettingsPreferences.class);
        startActivity(intent);
    }



    /**
     * metoda dodaje lub odejmuje item do bazy danych, w zależności od parametru addidtion
      * @param addition jeśli parametr ma wartość true to wartości są dodatnie jeśli ma false to znaczy
     *                 . że odejmujemy sobie nadgodzinki
     */
    private void insertTimeChecker(boolean addition)
    {
        final EditText minutesEditText = findViewById(R.id.mainMinutesEditText);
        final EditText hoursEditText   = findViewById(R.id.mainHoursEditText);

        String minutes = minutesEditText.getText().toString();
        String hours = hoursEditText.getText().toString();

        LocalDate date = mItemViewModel.getLocalDate().getValue();

        if (minutes.equals(""))
            minutes = "0";
        if (hours.equals(""))
            hours = "0";
        if (minutes.equals("0") && hours.equals("0"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.incorrect_overtime)
                    .setMessage(R.string.set_non_null_time)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            // nic nie zmieniaj, użytkownik ma tylko przyjąć do wiadomości, że
                            // trzeba wprowadzić dane.
                        }
                    });
            builder.show();
            return;
        }

        int minutesInt = Integer.parseInt(minutes);
        int hoursInt = Integer.parseInt(hours);

        if (!addition)
        {
            minutesInt = -minutesInt;
            hoursInt   = -hoursInt;
        }

        LocalDate today = LocalDate.now();
        boolean datecomparison = date.isAfter(today);
        // isAfter nie może zwrócić nullPointerException, ponieważ w onCreate()
        // do tego intentu mamy sprawdzanie czy mItemViewModel.getLocalDate().getValue() != null
        // jeśli jest null to zostaje mu przypisana wartość daty z kalendarza i to warunkuje, że nigdy null nie będzie
        if (datecomparison || Math.abs(minutesInt) >= 60 )
        {
            if (Math.abs(minutesInt) >= 60)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.incorrect_minutes)
                        .setMessage(R.string.minutes_lower_60)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                minutesEditText.setText("");
                            }
                        });
                builder.show();
            }
            if (datecomparison)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.incorrect_date)
                        .setMessage(R.string.cannot_setup_future_date)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {

                            }
                        });
                builder.show();
            }
        }
        else  // to jest w przypadku gdy wszystko jest w porządku
        {
            String dateOfOvertime = date.toString();
            String todayString = today.toString();
            mItemViewModel.insert(new Item(0, todayString, dateOfOvertime, hoursInt, minutesInt));
            Toast.makeText(this, getText(R.string.operation_saved), Toast.LENGTH_SHORT).show();
        }
    }
}

// SOME NOTES :D

/*
#### Założnia co do notyfikacji
jeśli uruchamia się mainIntent to sprawdzamy, czy jest wystartowany service? uruchamiający notyfikację,
    Tak: jeśli jest uruchomiony to nie wchodzimy do wnętrza if()
    Nie: jeśli service nie jest uruchomiony to sprawdzamy czy w ustawieniach mamy go uruchomić
        Tak: uruchamiamy serwis z notyfikacjami co zadaną w ustawieniach wartość czasu
        Nie: nie uruchamiamy serwisu, czyli użytkownik będzie robił buckupy' manualnie
 */



/*
// z metody onCreate(View view)
        // przykład z
        // https://codelabs.developers.google.com/codelabs/android-training-alarm-manager/index.html?index=..%2F..android-training#3
        //mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //createNotificationChannel();
        //deliverNotification(this);

        if (mSharedPreferences.getBoolean("buckup_enabled", true))
        {
            Intent notifyIntent = new Intent(this, BroRec.class);
            mPendingIntent = PendingIntent.getBroadcast
                    (this, notificationId, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            //mPendingIntent = PendingIntent.getBroadcast(this, 0, notifyIntent, 0);
            mAlarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    System.currentTimeMillis() + 10 * 1000, 10 * 1000, mPendingIntent);
        }
        else
        {
            if (mAlarm != null)
                mAlarm.cancel(mPendingIntent);
        }
         */


    /*
    private void deliverNotification(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // flagi mówią tutaj sstemowi jak ma traktować ten intent gdy pojawi on się ponownie,
        // wy wtym przypadku intent ma być ten sam tylko etras mają zostać zmienione na nowe
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("content title")
                .setContentText("content text")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("tu jest text który pojawi się " +
                        "gdy notyfikacja jest expandible. tzn pociągając w dół pojawiają się dodadtkowe" +
                        " linie textu."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // to automatycznie usówa notyfikacje gdy urzytkownik na nią klilknie
        mNotificationManager.notify(notificationId, builder.build());
    }
    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "nazwa notyfikacji",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies every 15 minutes to stand up and walk");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
    */

    /* // wycięty fragment manifestu
        <receiver
            android:name=".BuckUp.BootCompletedBroadcastReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
        <receiver
            android:name=".BuckUp.BuckUpAlarmBroadcastReceiver"
            android:enabled="true"
            android:exported="true">
        </receiver>
        <receiver
            android:name=".BuckUp.BroRec"
            android:enabled="true"
            android:exported="false"></receiver>
         */


        /*
        //działa tylko jak intent jest jawny
        Intent sampleIntent = new Intent(this, SampleReceiver.class); // to działa bo intent jest explicit
        //Intent sampleIntent = new Intent();
        sampleIntent.setAction("com.example.sudouser.nadgodzinki.BuckUp.SampleReceiver");
        sendBroadcast(sampleIntent);

        //ponieważ powyższy intent jest niejawny (nie zawiera informacji o kalsie w swoim konstuktorze)
        // to musimy zdefiniować context receiver (ale ten jest ważny tylko dopóki istnieje ten context)
        BroadcastReceiver br = new SampleReceiver();
        IntentFilter filter = new IntentFilter(); // można też od razu new IntentFilter("com.example.sudouser.nadgodzinki.BuckUp.SampleReceiver") bo argumentem jest string action;
        filter.addAction("com.example.sudouser.nadgodzinki.BuckUp.SampleReceiver");
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(br, filter);
         */
// i na koniec kasujemy z manifestu info o tym że jest SampleReceiver
        /*
        <receiver
            android:name=".BuckUp.SampleReceiver">
            <intent-filter>
                <action android:name="com.example.sudouser.nadgodzinki.BuckUp.SampleReceiver" />
            </intent-filter>
        </receiver>
         */


/*
// treść sample receivera:
package com.example.sudouser.nadgodzinki.BuckUp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class SampleReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intentFromAlarm)
    {
        int i = 0;
    }
}


        //ponieważ powyższy intent jest niejawny (nie zawiera informacji o klasie w swoim konstuktorze)
        // to musimy zdefiniować context receiver (ale ten jest ważny tylko dopóki istnieje ten context)
        BroadcastReceiver br = new SampleReceiver();
        IntentFilter filter = new IntentFilter(); // można też od razu new IntentFilter("com.example.sudouser.nadgodzinki.BuckUp.SampleReceiver") bo argumentem jest string action;
        filter.addAction("com.example.sudouser.nadgodzinki.BuckUp.SampleReceiver");
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(br, filter);
        // this.unregisterReceiver(br); // trzeba go jeszcze skasować na koniec

        Intent sampleIntent = new Intent(this, SampleReceiver.class); // to działa manifest receiver'em bo intent jest explicit, ale nie działa z
        //Intent sampleIntent = new Intent();
        sampleIntent.setAction("com.example.sudouser.nadgodzinki.BuckUp.SampleReceiver");
        lbm.sendBroadcast(sampleIntent);
         */




/*
//  tutaj treba wstawić jeszcze listenera, który będzie sparawdzał czy jak się zmieną
        // ustawinia to czy należy wywoła mAlarm.cancel() aby anulować alarm
        listener = new SharedPreferences.OnSharedPreferenceChangeListener()
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
        };
 */







