package com.example.sudouser.nadgodzinki;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.db.Item;

import java.time.LocalDate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.*;


public class MainActivity extends AppCompatActivity
{
    private ItemViewModel mItemViewModel;

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


        mItemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // to nie jest dobre rozwiązanie ponieważ przypisanie jest wykonywane w głównym watku
        // należy to przenieść do jakiegoś pobocznego wątku.

        Toast.makeText(getApplicationContext(), getApplicationContext().getDatabasePath("Baza_Danych").getPath(), Toast.LENGTH_LONG).show();

        final CalendarView calendar = findViewById(R.id.mainCalendar);
        mItemViewModel.setLocalDate(LocalDate.ofEpochDay( (calendar.getDate()/(24*3600*1000))));

        /*         * WAŻNE to jest listener, który jest wywoływany gdy zmienmy datę w kalendarzu.         */
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                mItemViewModel.setLocalDate(LocalDate.of(year, month+1, dayOfMonth));
            }
            /*
        // powyższego lisenra można zapisać w postaci wyrażenia lambda
        calendar.setOnDateChangeListener( (@NonNull CalendarView view, int year, int month, int dayOfMonth) ->
                mItemViewModel.setLocalDate(LocalDate.of(year, month+1, dayOfMonth)));

         */
        });

        // tworzę notyfikator
        /*
        Because you must create the notification channel before posting any notifications on
        Android 8.0 and higher, you should execute this code as soon as your app starts.
        It's safe to call this repeatedly because creating an existing notification channel
        performs no operation.
         */
        //Intent[] intents = new Intent[] {new Intent(this, MainActivity.class).putExtra("fromNotifier", true)};
        //NotifierMain notifier = new NotifierMain(getApplicationContext(), intents);

    }

    @Override
    protected void onDestroy()
    {
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
        Intent intent = new Intent(this, StatsInfoActivity.class);
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

        // tutaj zrobić check time czy czas jest poprawny
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
        boolean datecomparison = date.isAfter(today); // TODO sprawdzić dlaczego może wywyalać NullPointerException

        if (datecomparison || Math.abs(minutesInt) >= 60 )
        {
            if ( Math.abs(minutesInt) >= 60)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.incorrect_minutes)
                        .setMessage(R.string.minutes_lower_60)
                        //.setView(promptUserView)
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

            // dodaję info notyfikację do
            //Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.operation_saved), duration);
            toast.show();
        }

    }
}





















