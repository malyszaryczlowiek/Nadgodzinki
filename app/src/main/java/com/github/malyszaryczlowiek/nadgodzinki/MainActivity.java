package com.github.malyszaryczlowiek.nadgodzinki;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.github.malyszaryczlowiek.nadgodzinki.BuckUp.BuckUpAlarmBroadcastReceiver;
import com.github.malyszaryczlowiek.nadgodzinki.BuckUp.BuckUpFile;
import com.github.malyszaryczlowiek.nadgodzinki.BuckUp.MyFileProvider;
import com.github.malyszaryczlowiek.nadgodzinki.BuckUp.XmlParser;
import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.MainActivityDialogs.AskAboutEmailDialog;
import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.MainActivityDialogs.FileDoesNotExistDialog;
import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.MainActivityDialogs.IncorrectHoursCountErrorDialog;
import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.MainActivityDialogs.IncorrectMinutesCountErrorDialog;
import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.MainActivityDialogs.IncorrectOvertimeErrorDialog;
import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.MainActivityDialogs.NoFileInDownloadFolderErrorDialog;
import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.MainActivityDialogs.NullOvertimeErrorDialog;
import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.MainActivityDialogs.ItemExistsDialog;
import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.MainActivityDialogs.SelectBuckUpFileDialog;
import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.NoteDialog;
import com.github.malyszaryczlowiek.nadgodzinki.ViewModels.ItemViewModel;
import com.github.malyszaryczlowiek.nadgodzinki.db.Item;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.*;
import androidx.preference.PreferenceManager;


/**
 * @author Robert Pomorski
 * @version 0.1
 * Klasa w której wykonywane są takie czynności jak dodawanie i odejmowanie
 * nadgodzin, wykonywanie kopii zapasowej, wczytywanie kopii zapasowej,
 *
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NoteDialog.NoteDialogListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        ItemExistsDialog.ItemExistsButtonClickedListener,
        IncorrectOvertimeErrorDialog.ClearEditTextInterface,
        IncorrectMinutesCountErrorDialog.ClearEditTextInterface,
        IncorrectHoursCountErrorDialog.ClearEditTextInterface,
        AskAboutEmailDialog.AskAboutEmailInterface,
        SelectBuckUpFileDialog.OnSelectedFileListener
{

    private ItemViewModel mItemViewModel;
    private SharedPreferences mSharedPreferences;
    private EditText minutesEditText ;
    private EditText hoursEditText   ;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private AlarmManager mAlarm;
    private PendingIntent mPendingIntent;
    private static final int pendingIntentRequestCode = 2;

    // fields necessary to create Item
    private File[] newListOfFiles;
    // private String[] listaPlikow;
    private String todayDate;
    private int yearOfOvertime;
    private int monthOfOvertime;
    private int dayOfOvertime;
    private int dayOfWeek;
    private int minutesInt;
    private int hoursInt;

    // stałe wykorzystane w metodzie readBuckUp()
    private static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private Context thisContext = this;

    private List<Item> listOfItems =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_with_drawer);

        mItemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        mItemViewModel.getAllItems().observe(this, new Observer<List<Item>>()
        {
            @Override
            public void onChanged(List<Item> items)
            {
                listOfItems = items;
            }
        });

        final CalendarView calendarView = findViewById(R.id.mainCalendar);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                Calendar calendarLocal = Calendar.getInstance();
                calendarLocal.set(year, month, dayOfMonth);
                mItemViewModel.setLocalDate(calendarLocal);
            }
        });
        if (mItemViewModel.getLocalDate().getValue() != null)
            calendarView.setDate(mItemViewModel.getLocalDate().getValue().getTimeInMillis());
        else
        {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(calendarView.getDate());
            mItemViewModel.setLocalDate(cal);
        }


        Runnable runnableSetToolbar = () ->
        {
            Toolbar toolbar = findViewById(R.id.mainActivityToolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        };


        Runnable runnableSetNavigationDrawer = () ->
        {
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            minutesEditText = findViewById(R.id.mainMinutesEditText);
            hoursEditText   = findViewById(R.id.mainHoursEditText);
        };



        Runnable setViewModel = () ->
        {
            calendarView.setMaxDate(Calendar.getInstance().getTimeInMillis());

            Calendar today = Calendar.getInstance();
            todayDate = String.valueOf(today.get(Calendar.YEAR)) + "." +
                    String.valueOf(today.get(Calendar.MONTH) + 1) + "." +
                    String.valueOf(today.get(Calendar.DAY_OF_MONTH));
        };


        Runnable setPreferences = () ->
        {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Runnable runnableCreateAlarmManager = this::createAlarmManager;
            Thread threadCreateAlarmManager = new Thread(runnableCreateAlarmManager);
            threadCreateAlarmManager.start();
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

            if (getIntent().getBooleanExtra("fromNotifier", false))
                makeBuckup();

            try
            {
                threadCreateAlarmManager.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        };


        Runnable runnableRestoreFromBundle = () ->
        {
            //if (savedInstanceState != null)
              //  listaPlikow = savedInstanceState.getStringArray("listaPlikow");
        };

        Thread threadRestoreFromBundle = new Thread(runnableRestoreFromBundle);
        Thread threadSetNavigationView = new Thread(runnableSetToolbar);
        Thread threadSetDrawerLayer = new Thread(runnableSetNavigationDrawer);
        Thread threadSetViewModel = new Thread(setViewModel);
        Thread threadSetPreferences = new Thread(setPreferences);


        threadSetNavigationView.start();
        threadSetDrawerLayer.start();
        threadSetViewModel.start();
        threadSetPreferences.start();
        threadRestoreFromBundle.start();

        try
        {
            threadSetNavigationView.join();
            threadSetDrawerLayer.join();
            threadSetViewModel.join();
            threadSetPreferences.join();
            threadRestoreFromBundle.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /*
    /**
     * metoda wyświetlająca menu
     * @param menu
     * @return
     */
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_in_navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menuItemListOfOvertimes:
                showListOfOvertimes();
                return true;
            case R.id.menuItemStatistics:
                showStatistics();
                return true;
            case R.id.menuItemSettings:
                showSettings();
                return true;
            case R.id.menuItemTest:
                showTest();
                return true;
            case R.id.showNotes:
                showNotesActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
     */



    //********************************************
    // Metody używane w DrawerLayout.
    //********************************************

    /**
     * Metoda wywoływana w momencie gdy Drawer Layer (boczny pasek)
     * jest rozwinięty a naciśniemy naprzycisk wstecz. Spowoduje to
     * schowanie się bocznego paska.
     */
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }


    /**
     * Metoda wywoływana gdy użytkownik wybierze jakis element z listy
     * w DrawerLayout. W zależności to tego jaki item jest wybrany
     * wywoływana jest funkcja, która obsługuje ten przypadek, a następnie
     * używając drawer.closeDrawer() chowamy drawer.
     * @param item wybrany element z listy.
     * @return boolean powinien zwracać true bo wtedy menu chowa się samo.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        Runnable runnableRunFunction;
        Runnable runnableCloseDrawerLayer;
        Thread threadExecuteFunction;
        Thread threadCloseDrawer;

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // TODO sprawdzić jak można tu wcisnąć method reference dla obiektu
        // niestatycznego i do tego z argumentami metody
        // https://www.codementor.io/eh3rrera/using-java-8-method-reference-du10866vx
        runnableCloseDrawerLayer = () ->
        {
            drawer.closeDrawer(GravityCompat.START);
        };
        threadCloseDrawer = new Thread(runnableCloseDrawerLayer);
        switch(item.getItemId())
        {
            case R.id.menuItemListOfOvertimes:
                /*
                runnableRunFunction = this::showListOfOvertimes;
                threadExecuteFunction = new Thread(runnableRunFunction);

                threadExecuteFunction.start();
                threadCloseDrawer.start();

                try
                {
                    threadExecuteFunction.join();
                    threadCloseDrawer.join();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                 */
                drawer.closeDrawer(GravityCompat.START);
                showListOfOvertimes();
                return true;
            case R.id.menuItemStatistics:
                showStatistics();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.menuItemSettings:
                showSettings();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.menuItemMakeBuckup:
                makeBuckup();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.menuItemReadBuckup:
                readBuckUp();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            default:
                return true;
        }
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

    /**
     * Gdy nasze activity jest niszczone należy odrejestrować listenera
     * odpowiedzialnego za rejestrowanie zmian w preferencjach.
     */
    @Override
    protected void onDestroy()
    {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        super.onDestroy();
    }

    /*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if (listaPlikow != null)
            outState.putStringArray("listaPlikow", listaPlikow);
    }
     */



    /**
     * Metoda generująca nam alarm Manager, który służy do tego aby Anroid
     * wysyłał broadCast do naszej aplikacji. Broadcast ten będzie odebrany w
     * clase BuckUpAlarmBroadcastReceiver {@link link to BuckUpAlarmBroadcastReceiver }
     * aby ta znowu mogła wywołać notyfikację przy użyciu clasy {@link link}.
     *
     * Metoda wczytuje jaki powinien być interwał czasowy oraz wczytuje w jaki dzień
     * alar będzie wywoływany. Następnie generuje alarm w postaci SystemService
     * używa do tego getSystemService(class) która automatycznie tworzy taki service.
     * Tworzy intent do którego dajemy odnośnik do broadcastu który ma go przechwycić
     * Następnie wkłądając w pending intent tworzymy tak naprawdę Service obsługiwany
     * przez system. następnie do alarmu za pomocą setRepeating() wkłądamy jego typ
     * punkt startowy, interwał oraz intent Service, który będzie ten alrm obsługiwał.
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
            // wartości zmieniają się od 1-7 a zaczynają się od niedzieli= 1.
            int chosenDay = Integer.valueOf(
                    mSharedPreferences.getString("buckupDay", "6"));
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR,0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.DAY_OF_WEEK, Math.abs(
                    calendar.get(Calendar.DAY_OF_WEEK) - chosenDay));
            calendar.add(Calendar.HOUR_OF_DAY, 19);
            calendar.add(Calendar.MINUTE, 0);

            mAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(),
                    BuckUpAlarmBroadcastReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), pendingIntentRequestCode,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    intervalMillis, mPendingIntent);
        }
    }

    /**
     * Metoda kasująca alarm służący do wywołania notyfikacji o potrzebie
     * zrobienia buckup'u. Wywoływana jest gdy listener rejestrujący zmiany
     * w sharedPreferences SharedPreferences.OnSharedPreferenceChangeListener()
     * zdefiniowany w metodzie onCreate() wykryje, że dla danego klucza
     * zminiona została wartość.
     */
    private void cancelAlarm()
    {
        if (mAlarm != null)
            mAlarm.cancel(mPendingIntent);
    }


    /**
     * Metoda przypisana w MainActivity.xml file do przycisku odpowiedzialnego
     * za dodawanie dodatniej nadgodziny.
     * @param view przycisk add/dodaj
     */
    public void addMainButtonClicked(View view)
    {
        insertTimeChecker(true);
        /*

        Runnable runnableAdd = () ->
        {
            insertTimeChecker(true);
        };
        Thread threadAdd = new Thread(runnableAdd);
        threadAdd.start();

        try
        {
            threadAdd.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
         */
    }


    /**
     * metoda wywoływana gdy użytkownik będzie chciał dodać ujemną nadgodzinę
     * @param view przycisk delete/usuń
     */
    public void deleteOvertime(View view)
    {
        insertTimeChecker(false);
    }

    /**
     * Metoda która odpala Activity z listą wykonanych i odebranych nadgodzin
     * jeśli fromNotifier jest true tzn. wejście w tą activity spowoduje od
     * razu wywołanie wykonania kopii zapasowej przy użyciu matody makeBuckup()
     * Metoda wywoływana przez DrawerLayout.
     */
    public void showListOfOvertimes()
    {
        Intent intent = new Intent(this, ListOfOvertimesActivity.class)
                .putExtra("fromNotifier", false);
        startActivity(intent);
    }


    /**
     * Otwieranie activity ze statystykami gdzie zmiajdują sie min.. wykresy
     * Metoda wywoływana przez DrawerLayout.
     */
    public void showStatistics()
    {
        Intent intent = new Intent(this, Statistics.class);
        startActivity(intent);
    }


    /**
     * Otwieranie ustawień. Metoda wywoływana przez DrawerLayout.
     */
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
    private void insertTimeChecker(final boolean addition)
    {
        // wczytanie ile mamy wpisanych minut.
        String minutes = minutesEditText.getText().toString();
        String hours = hoursEditText.getText().toString();

        // wczytanie aktualnie zaznaczonej w kalendarzu daty
        Calendar date = mItemViewModel.getLocalDate().getValue();

        // jeśli gdzieś nie jest wypełnione pole to uzupełniam o
        // brakujące wartości.
        if (minutes.equals(""))
            minutes = "0";
        if (hours.equals(""))
            hours = "0";
        if (minutes.equals("0") && hours.equals("0"))
        {
            // jeśli obie wartości nie są wpisane lub są zerowe to
            // wyświetlam info dialog, ze czas nie został wpsany.
            // a następnie wychodzę z funkcji.
            NullOvertimeErrorDialog dialog = new NullOvertimeErrorDialog();
            dialog.show(getSupportFragmentManager(), "NullOvertimeErrorDialog_Tag");
            return;
        }

        int minutesSigned = Integer.parseInt(minutes);
        int hoursSigned = Integer.parseInt(hours);

        // w zależności czy godziny mają być odejmowane czy dodawane zmieniam ich
        // znak
        if (!addition)
        {
            minutesSigned = -minutesSigned;
            hoursSigned   = -hoursSigned;
        }

        minutesInt = minutesSigned;
        hoursInt= hoursSigned;

        if ( (Math.abs(minutesInt) >= 60) && (Math.abs(hoursInt) >= 24) )
        {
            IncorrectOvertimeErrorDialog dialog = new IncorrectOvertimeErrorDialog();
            dialog.show(getSupportFragmentManager(), "IncorrectOvertimeErrorDialog_Tag");
        }
        else if (Math.abs(minutesInt) >= 60)
        {
            IncorrectMinutesCountErrorDialog dialog = new IncorrectMinutesCountErrorDialog();
            dialog.show(getSupportFragmentManager(), "IncorrectMinutesCountDialog_Tag");
        }
        else if (Math.abs(hoursInt) >= 24)
        {
            IncorrectHoursCountErrorDialog dialog = new IncorrectHoursCountErrorDialog();
            dialog.show(getSupportFragmentManager(), "IncorrectHoursCountDialog_Tag");
        }
        else
        {
            // jeśli dane mają poprawne wartości to
            // przypisuję dane do
            dayOfOvertime = date.get(Calendar.DAY_OF_MONTH);// zaczyna się od 1
            monthOfOvertime = date.get(Calendar.MONTH) +1; // miesiące zaczynają się od 0
            yearOfOvertime = date.get(Calendar.YEAR);
            dayOfWeek = date.get(Calendar.DAY_OF_WEEK); //sunday is beginning of week and has value 0

            // sprawdzam czy taki wpis już istnieje w bazie danych

            boolean isItemExisting  = listOfItems.stream()
                    .anyMatch(item ->
                            Integer.valueOf(item.getYearOfOvertime()).equals(yearOfOvertime)
                                    && Integer.valueOf(item.getMonthOfOvertime()).equals(monthOfOvertime)
                                    && Integer.valueOf(item.getDayOfOvertime()).equals(dayOfOvertime)
                    );

            if (isItemExisting)
            {
                ItemExistsDialog dialog = new ItemExistsDialog();
                dialog.show(getSupportFragmentManager(), "ItemExistsDialog_Tag");
            }
            else
            {
                if (mSharedPreferences.getBoolean("askAboutNote", true))
                { //prośba o dodanie notatki
                    NoteDialog noteDialog = new NoteDialog();
                    noteDialog.show(getSupportFragmentManager(),"note_tag");
                    minutesEditText.setText("");
                    hoursEditText.setText("");
                }
                else
                {
                    mItemViewModel.insert(new Item(0, todayDate, dayOfWeek, yearOfOvertime,
                            monthOfOvertime, dayOfOvertime, hoursInt, minutesInt, ""));
                    minutesEditText.setText("");
                    hoursEditText.setText("");
                    Toast.makeText(this, getText(R.string.operation_saved), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void itemExistDialogListenerPositiveButtonClicked()
    {
        if (mSharedPreferences.getBoolean("askAboutNote", true))
        { //prośba o dodanie notatki
            NoteDialog noteDialog = new NoteDialog();
            noteDialog.show(getSupportFragmentManager(),"note_tag");
            minutesEditText.setText("");
            hoursEditText.setText("");
        }
        else
        {
            mItemViewModel.insert(new Item(0, todayDate, dayOfWeek, yearOfOvertime,
                    monthOfOvertime, dayOfOvertime, hoursInt, minutesInt, ""));
            minutesEditText.setText("");
            hoursEditText.setText("");
            Toast.makeText(thisContext, getText(R.string.operation_saved), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void clearOvertimeEditTextInterface()
    {
        minutesEditText.setText("");
        hoursEditText.setText("");
    }

    @Override
    public void clearMinutesEditTextInterface()
    {
        minutesEditText.setText("");
    }

    @Override
    public void clearHoursEditTextInterface()
    {
        hoursEditText.setText("");
    }


    /**
     * Metoda pochodząca z interfejsu NoteDialog.NoteDialogListener.
     * Jeśli w ustawieniach mamy zaznaczone, że notatka ma się wyświetlać to wywołując
     * NoteDialog po czym jeśli klikniemy tam ok to wpisana w ten dialog notatka
     * będzie dostarczona za pomocą tej funkcji z tamtej klasy tutaj.
     *
     * W metodzie edytuję preferencje z zależności od tego czy użytkownik zanaczył,
     * że będzie chciał pokazywać jeszcze to zapytanie o notatkę czy nie.
     * @param note notatka, którą otrzymujemy od użytkownika, po wpisanu jej w NoteDialog
     * @param show boolean, czy pytanie o notatkę ma byc dalej wyświetlane przy dodawaniu kolejnych
     *             nadgodzin.
     */
    @Override
    public void applyChanges(String note, boolean show)
    {

        mSharedPreferences.edit().putBoolean("askAboutNote", !show).apply();
        mItemViewModel.insert(new Item(0, todayDate, dayOfWeek, yearOfOvertime,
                monthOfOvertime, dayOfOvertime, hoursInt, minutesInt, note));
        Toast.makeText(this, getText(R.string.operation_saved), Toast.LENGTH_SHORT).show();
    }


    /*
    ****************************************************
    METODY UZYWANE DO WYCZTANIA BUCKUP'U
    ****************************************************
    */

    /**
     * Metoda wywołana gdy użytkownik wybierze w DrawerLayout opcję
     * wczytania buckupu.
     */
    private void readBuckUp()
    {
        if (isExternalStorageWritable())
        {
            // jeśli pozwolenie nie zostało zaakceptowane przez użytkownika to wykonaj if
            // if wywołuje metodę, która wyświetla zapytanie systemowe dla dostępu do
            // systemu plików.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                showRequestPermissionDialog();
            }
            else // jeśli dostęp jest zapewniony to następuje przeszukanie folderu.
                ifPermissionGranted();
        }
        else // to jest w przypadku gdy nie można dostać się do external location bo sd card jest niedostępna
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error)
                    .setIcon(R.drawable.ic_round_error_outline_24px)
                    .setMessage(R.string.error_external_storage)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();
                        }
                    });
            builder.show();
        }
    }


    /**
     * Metoda wywołana w readBuckup() a mająca na celu pokazanie zapytania systemowego
     * o dostęp do plików. jeśli użytkownik przy zaznaczy, że nie zezwala i zaznaczy
     * aby nie pokazywać ponownie to to okno mimo wywołania metody już nie pokaże.
     */
    private void showRequestPermissionDialog()
    {
        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
    }


    /**
     *
     */
    private void ifPermissionGranted()
    {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String name = "[.]xml";
        File[] lista = folder.listFiles(); // wczytuje listę plików i folderów z danej lokalizacji
        // jeśli lista nie jest pusta czyli w folderze Downloads są pliki (jakiekolwiek)
        if (lista != null)
        {
            // ekstrahuje ścieżki zawierające rozszerzenie xml.
            newListOfFiles = Arrays.stream(lista)
                    //.filter(file -> file.isFile()) // odfiltrowuje tutaj directory
                    .filter(File::isFile)            // można też odfiltrować referencją
                    .filter(file -> file.getName().matches(".*" + name))
                    // odfiltrowuje tutaj nazwy plików nie zawierające poszukiwanej nazwy.
                    .toArray(File[]::new);

            // jeśli lista jest pusta to wyświetlam dialog z informacją, że nie ma żadnego pasującego pliku
            if (newListOfFiles.length == 0)
            {
                FileDoesNotExistDialog dialog = new FileDoesNotExistDialog();
                dialog.show(getSupportFragmentManager(), "FileDoesNotExist_Tag");
            }
            else // jeśli natomiast lista nie jest pusta to wyświetli dialog z listą do pojedyńczego odtickowania.
            {
                // tutaj ekstrahuje ścieżki do samych nazw plików, które zostanę wyświetlone w dialogu do odtick'owania.
                String[] listaPlikow = Arrays.stream(newListOfFiles).map(File::getName).toArray(String[]::new);
                SelectBuckUpFileDialog dialog = new SelectBuckUpFileDialog();
                Bundle bundle = new Bundle();
                bundle.putStringArray("listaPlikow", listaPlikow);
                dialog.setArguments(listaPlikow);
                //dialog.onSaveInstanceState(bundle);
                dialog.show(getSupportFragmentManager(), "SelectBuckUpFileDialog_Tag");
                // i to wywołuje ostatecznie
            }
        }
        else
        {
            NoFileInDownloadFolderErrorDialog dialog = new NoFileInDownloadFolderErrorDialog();
            dialog.show(getSupportFragmentManager(), "NoFileInDownloadFolderErrorDialog_Tag");
        }
    }

    @Override
    public void selectBuckUpFile(int which)
    {
        File chosenFile = newListOfFiles[which];
        // wczytuję ścieżkę do pliku zakładając, że kolejność ścieżek absolutnych w nowaLista jest
        // taka sama jak w listaPlikow, przez to wybierając plik wybieram ścieżkę absolutną, którą
        // następnie użyje do wczytania i parsingu pliku.
        // parser wczytuje wybraną ścieżkę
        XmlParser parser = new XmlParser(thisContext, chosenFile);
        List<Item> readItems = parser.returnList();
        mItemViewModel.mergeDatabaseWithBuckupFile(readItems);
    }




    //TODO wstawić tutaj tę metodę

    /**
     * Metoda sprawdza czy można zapsiwać na zewnętrzej karcie pamięci
     * @return prawda czy fałsz
     */
    private boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    /**
     * Metoda wywoływana gdy użytkownik wybierze czy akceptuje czy nie akceptuje
     * dostęp aplikacji do plików na telefonie.
     * Metoda jest implementowana z interfajsu:
     * ActivityCompat.OnRequestPermissionsResultCallback, któ©y odpowiada za
     * wyświetlanie dialogów z zapytaniami o dostęp do funkcjonalności
     * telfonu.
     * @param requestCode kod któremu odpowiada wyświetlone zapytanie. tzn.
     *                    dla każdego zapytania o dostęp do jakiejś funkcjonalności
     *                    w telefonie przypisuje się jakiś kod w posataci int.
     * @param permissions typy pozwolenia jakie będą spełnione gdy użytkownik
     *                    zatwierdzi je w wyskakującym dialogu.
     * @param grantResults jeśli dla danego pozwolenia mamy jakąś decyzję
     *                     i jest ona równa
     *                     PackageManager.PERMISSION_GRANTED. można wywołać odpowiednią
     *                     metodę, która obsłuży udostępnioną funkcjonalność.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE :
                {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        ifPermissionGranted();
                    }
                    else
                    {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.

                    }
                    return;
                }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    /*
    ****************************************************
    METODY UżYWANE DO ZROBIENIA BUCKUP'U
    ****************************************************
    */

    /**
     * Metoda wywoływana po kliknięcu w DrawerLayout o zrobienie buckupu
     */
    private void makeBuckup()
    {
        saveOnOtherLocalization();
    }


    /**
     * Metoda sprawdza, czy mamy wpisany buckupowy adress email. jeśli go nie ma to
     * alertDialogiem prosi go o wpisanie, następnie zapisuje go w preferencjach.
     */
    private void saveOnOtherLocalization()
    {
        String email = mSharedPreferences.getString("buckup_email","");
        if (email.equals("")) // Todo też do weryfikacji dlaczego może dawac nullPointerException
        {
            AskAboutEmailDialog dialog = new AskAboutEmailDialog();
            dialog.show(getSupportFragmentManager(), "AskAboutEmailDialog_Tag");
        }
        else
            wyslijbuckup();
    }

    @Override
    public void changeEmail(String adress)
    {
        mSharedPreferences.edit().putString("buckup_email", adress).apply();
        Toast.makeText(getApplicationContext(), getText(R.string.buckup_email_address_changed),
                Toast.LENGTH_SHORT).show();
        wyslijbuckup();
    }

    /**
     * Metoda jest wywołana w metodzie makeBuckup() w przypadku gdy mamy już ustawiony
     * email buckupowy. Metoda tworzy intent, który zostanie użyty do otworzenia
     * innej aplikacji do której zostanie wysłany plik buckupowy.
     */
    private void wyslijbuckup()
    {
        Intent intent = createEmailIntent();
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(getApplicationContext(), "Cannot start email Intent.",
                    Toast.LENGTH_SHORT).show();
    }

    /**
     * Funkcja generuje intent w którym zawieramy plik buckupowy xml.
     *
     * @return Intent, który w załączniku posiada plik z buckupem w postaci
     * pliku xml.
     */
    private Intent createEmailIntent()
    {
        String email = mSharedPreferences.getString("buckup_email","");
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setData(Uri.parse("mailto:"));
        //intent.setDataAndType(Uri.parse("mailto:"), "*/*"); // to powoduje cannot resolve patrz else poniżej
        //intent.setType("*/*");
        intent.setType("text/xml");
        String[] lista = new String[] {email};
        intent.putExtra(Intent.EXTRA_EMAIL, lista);
        Calendar calendar = Calendar.getInstance();
        intent.putExtra(Intent.EXTRA_SUBJECT, "Buckup " + calendar.getTime());

        if (listOfItems == null)
        {
            Toast.makeText(this, "List is NULL", Toast.LENGTH_SHORT).show();
            return null;
        }
        else
        {
            BuckUpFile file = new BuckUpFile(getCacheDir(), listOfItems);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri buckupUri = MyFileProvider.getUriForFile(getApplicationContext(),
                    "com.github.malyszaryczlowiek.nadgodzinki.BuckUp.MyFileProvider", file.getFile());
            intent.putExtra("path", file.getFile().getPath());
            intent.putExtra(Intent.EXTRA_STREAM, buckupUri);

            if (mSharedPreferences.getBoolean("askForAppChooser", true))
                return Intent.createChooser(intent, getText(R.string.which_app_choose));
            else
                return intent;
        }
    }



}

// TODO sprawdzić o co chodzi z handler looperem, gdy uruchamiamiy coś w nowym wątku.
// https://medium.com/mindorks/mastering-android-handler-4f710296bdc6

























