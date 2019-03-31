package com.example.sudouser.nadgodzinki;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.BuckUp.BuckUpAlarmBroadcastReceiver;
import com.example.sudouser.nadgodzinki.Dialogs.NoteDialog;
import com.example.sudouser.nadgodzinki.Settings.ListOfCategoriesActivity;
import com.example.sudouser.nadgodzinki.db.Item;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.ConfigurationCompat;
import androidx.lifecycle.*;
import androidx.preference.PreferenceManager;


public class MainActivity extends AppCompatActivity implements NoteDialog.NoteDialogListener
{
    private ItemViewModel mItemViewModel;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private AlarmManager mAlarm;
    private PendingIntent mPendingIntent;
    private static final int pendingIntentRequestCode = 2;

    // fields necessary to create Item
    private String dateOfOvertime;
    private String todayString;
    int dayOfWeek;
    int minutesInt;
    int hoursInt;

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
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                mItemViewModel.setLocalDate(LocalDate.of(year, month + 1, dayOfMonth));
            }
        });
        if (mItemViewModel.getLocalDate().getValue() != null)
            calendar.setDate(mItemViewModel.getLocalDate().getValue().toEpochDay() * 24 * 3600 * 1000);
        else
            mItemViewModel.setLocalDate(LocalDate.ofEpochDay((calendar.getDate() / (24 * 3600 * 1000))));


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        createAlarmManager();
        listener = new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
            {
                switch (key)
                {
                    case "buckup_enabled": // gdy właczamy bądź wyłączamy alarm
                        if (sharedPreferences.getBoolean(key, true))
                            createAlarmManager();
                        else
                            cancelAlarm();
                        break;
                    case "buckupList": // gdy zmieniamy okres przpomnień tydzień-miesiąc-kwartał TODO zmienić nazwę tej zmiennej na buckupPeriodList
                        createAlarmManager();
                        break;
                    case "buckupDay": // gdy zmieniamy dzień notyfikacji
                        createAlarmManager();
                        break;
                    default:
                        break;
                }
            }
        };
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
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
                    intervalMillis =  (long) 1000 * 3600 * 24 * 7 * 13;
                    break;
                default:
                    intervalMillis = (long) 1000 * 3600 * 24 * 7;
                    break;
            }
            int chosenDay = Integer.valueOf(mSharedPreferences.getString("buckupDay", "6"));//getInt("buckupDay", 6);
            long today = LocalDate.now().toEpochDay() * 1000 * 3600 * 24;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(today);
            calendar.add(Calendar.DAY_OF_WEEK, Math.abs(calendar.get(Calendar.DAY_OF_WEEK) - chosenDay));
            calendar.add(Calendar.HOUR_OF_DAY, 19);
            calendar.add(Calendar.MINUTE, 11);

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

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
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
            case R.id.menuItemTest:
                showTest();
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

    private void showTest()
    {
        Intent intent = new Intent(this, ListOfCategoriesActivity.class);
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

        int minutesSigned = Integer.parseInt(minutes);
        int hoursSigned = Integer.parseInt(hours);

        if (!addition)
        {
            minutesSigned = -minutesSigned;
            hoursSigned   = -hoursSigned;
        }

        minutesInt = minutesSigned;
        hoursInt= hoursSigned;

        LocalDate today = LocalDate.now();
        dayOfWeek = date.getDayOfWeek().getValue(); // dzień tygodnia
        // Todo sprawdzić jaki język jest ustawiony w telefonie i wtedy przypisać odpowiednią
        // wartość dnia tygodnia z resource. wpisać w string i ten string ewentualnie zostanie
        // wpisany w konstruktor itemu
        // Locale locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
        // if (locale.getLanguage().equals(new Locale("pl").getLanguage()))
        //String dayOfWeekString = getResources().getStringArray(R.array.buckup_list_of_dayweeks)[dayOfWeek - 1];


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
            dateOfOvertime = date.toString();
            todayString = today.toString();

            if (mSharedPreferences.getBoolean("askAboutNote", true)) //prośba o dodanie notatki
            {
                NoteDialog noteDialog = new NoteDialog();
                noteDialog.show(getSupportFragmentManager(),"note tag");
            }
            else
            {
                mItemViewModel.insert(new Item(0, todayString, dayOfWeek, dateOfOvertime, hoursInt, minutesInt, ""));
                Toast.makeText(this, getText(R.string.operation_saved), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void applyChanges(String note, boolean show)
    {
        mSharedPreferences.edit().putBoolean("askAboutNote", !show).apply(); //zaprzeczamy, że chemy pokazywać ponownie to okno
        mItemViewModel.insert(new Item(0, todayString, dayOfWeek, dateOfOvertime, hoursInt, minutesInt, note));
        Toast.makeText(this, getText(R.string.operation_saved), Toast.LENGTH_SHORT).show();
    }
}





























