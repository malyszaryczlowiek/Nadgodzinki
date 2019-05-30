package com.example.sudouser.nadgodzinki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.BuckUp.BuckUpFile;
import com.example.sudouser.nadgodzinki.BuckUp.MyFileProvider;
import com.example.sudouser.nadgodzinki.Dialogs.SearchFilterItemsDialog;
import com.example.sudouser.nadgodzinki.Dialogs.SearchHelpers.SearchFlags;
import com.example.sudouser.nadgodzinki.RecyclerViewMain.ItemListAdapter;
import com.example.sudouser.nadgodzinki.ViewModels.ItemViewModel;
import com.example.sudouser.nadgodzinki.db.Item;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ListOfOvertimesActivity extends AppCompatActivity
        implements ItemListAdapter.ItemListAdapterListener,
        SearchFilterItemsDialog.ChosenSearchCriteriaListener
{
    private ItemViewModel mItemViewModel;
    private SharedPreferences sharedPreferences;
    private List<Item> listOfItems =  new ArrayList<>();
    private Context thisContext = this;
    private ItemListAdapter adapter;
    private SearchView searchView;
    private String searchViewQuery = "";
    private boolean hasSearchViewFocus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_of_overtimes);

        Toolbar myToolbar = findViewById(R.id.listOfOvertimesActivityToolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        ab.setTitle(R.string.list_of_overtimes);
        ab.setDisplayHomeAsUpEnabled(true);
        //ab.setHideOnContentScrollEnabled(true);// wymuszenie chowania się actionbar
        // powoduje wyjątek z takim komunikatem:
        // java.lang.UnsupportedOperationException: Hide on content scroll
        // is not supported in this action bar configuration.

        mItemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        RecyclerView recyclerView = findViewById(R.id.allItemsTable);
        adapter = new ItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mItemViewModel.getAllItems().observe(this, new Observer<List<Item>>()
        {
            @Override
            public void onChanged(List<Item> items)
            {
                listOfItems = items;
                adapter.setItems(items);
            }
        });

        if (savedInstanceState != null &&
                savedInstanceState.getBoolean("isSearchViewExtended"))
        {
            searchViewQuery = savedInstanceState.getString("searchViewQuery");
            hasSearchViewFocus = true;
        }
    }


    /**
     * Zapisujemy stan activity poprzez zapisanie pewnych wartości w obiekcie bundle
     *
     * @param outState bundle, który przechowuje dane zapisane przy poprzednim
     *                 tworzeniu tej aktywności, dane te można wpisać w obiekt bundle
     *                 przy użyciu metody: onSaveInstanceState(Bundle bundle)
     *                 a następnei putBoolean() dla bundle który jest argumentem
     *                 tej funkcji.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        if (searchView.hasFocus())
        {
            outState.putBoolean("isSearchViewExtended", true);
            outState.putString("searchViewQuery", searchView.getQuery().toString());
        }
        else
            outState.putBoolean("isSearchViewExtended", false);

        super.onSaveInstanceState(outState);
    }

/*
    ****************************************************
    METODY UZYWANE DO OBSŁUGI MENU
    ****************************************************
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_of_overtimes, menu);
        MenuItem searchItem = menu.findItem(R.id.menuItemSearchNote);

        // dzieki temu mangerowi wczytuję ustaiwenia ddla searchView, a następnie dodaję w manifeście jeszcze jedną liniejke
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setQueryHint(getResources().getString(R.string.search_in_notes));
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                mItemViewModel.getMatchingNoteQuery(newText);
                resetGetAllItemsObserver();
                return false;
            }
        });

        if (hasSearchViewFocus)
        {
            searchItem.expandActionView();
            searchView.requestFocus();
            searchView.setQuery(searchViewQuery, true);
            searchView.setMaxWidth(Integer.MAX_VALUE);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.filterDatabase:
                SearchFilterItemsDialog searcher = new SearchFilterItemsDialog();
                searcher.show(getSupportFragmentManager(), "search_filter_tag");
                return true;
            case R.id.clearFilterCriteria:
                mItemViewModel.clearSearchCriteria();
                resetGetAllItemsObserver();
                return true;
            case R.id.clearDatabase:
                clearDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
    ****************************************************
    METODY UżYWANE DO ZROBIENIA BUCKUP'U
    ****************************************************
    */

    /**
     * Metoda wywoływana
     */
    private void makeBuckup()
    {
        // można też użyć contextu this któ©ego lifecycle jest attached to current context
        String email = sharedPreferences.getString("buckup_email","");
        if (email.equals("")) // Todo też do weryfikacji dlaczego może dawac nullPointerException
        {
            LayoutInflater layoutInflater = LayoutInflater.from(ListOfOvertimesActivity.this);
            View promptUserView = layoutInflater.inflate(R.layout.email_dialog, null);
            final EditText userAnswer = (EditText) promptUserView.findViewById(R.id.email_editText_dialog);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.set_buckup_email)
                    .setView(promptUserView)
                    .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            String adres = userAnswer.getText().toString();
                            sharedPreferences.edit().putString("buckup_email", adres).apply();
                            Toast.makeText(getApplicationContext(), getText(R.string.buckup_email_address_changed), Toast.LENGTH_SHORT).show();
                            wyslijbuckup();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), getText(R.string.operation_cancelled), Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.show();
        }
        else
            wyslijbuckup();
    }

    /**
     * Metoda jest wywołana w metodzie makeBuckup() w przypadku gdy mamy już ustawiony
     * email buckupowy.
     */
    private void wyslijbuckup()
    {
        Intent intent = createEmailIntent();
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(getApplicationContext(), "Cannot start email Intent.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Funkcja generuje intent w którym zawieramy plik buckupowy
     * @return Intent , który w załączniku posiada plik z buckupem
     */
    private Intent createEmailIntent()
    {
        String email = sharedPreferences.getString("buckup_email","");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/xml");
        String[] lista = new String[] {email};
        intent.putExtra(Intent.EXTRA_EMAIL, lista);
        Calendar calendar = Calendar.getInstance();
        intent.putExtra(Intent.EXTRA_SUBJECT, "Buckup " + calendar.getTime());

        if (listOfItems == null)
        {
            Toast.makeText(this, "List is NULL", Toast.LENGTH_SHORT).show();
            return new Intent();
        }
        else
        {
            BuckUpFile file = new BuckUpFile(getCacheDir(), listOfItems);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri buckupUri = MyFileProvider.getUriForFile(getApplicationContext(),
                    "com.example.sudouser.nadgodzinki.BuckUp.MyFileProvider", file.getFile());
            intent.putExtra("path", file.getFile().getPath());
            intent.putExtra(Intent.EXTRA_STREAM, buckupUri);

            if (sharedPreferences.getBoolean("askForAppChooser", true))
                return Intent.createChooser(intent, getText(R.string.which_app_choose));
            else
                return intent;
        }
    }

    /*
    ****************************************************
    METODY UżYWANE DO CZYSZCZENIA BAZY DANYCH
    ****************************************************
    */

    /**
     * Metoda wyświEtla dialog w którym pytamy użytkownika czy chce wyczyścić bazę danych. Uzytkownik
     * ma trzy możliwości: czyszczenie (positive), anulownanie (negative), oraz zrobienie buckupu
     * (neutral).
     */
    public void clearDatabase()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.clear_database)
                .setMessage(R.string.clear_database_desctription)
                .setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Integer expected = mItemViewModel.getAllItems().getValue().size();
                        Integer result = mItemViewModel.clearDatabase();
                        if (result.equals(expected))
                        {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), getString(R.string.database_has_beeen_cleared), Toast.LENGTH_SHORT).show();
                        }
                        else if (result.equals(-1))
                        {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "Exception appeared.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "Other problems appeared.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNeutralButton(R.string.make_buckup, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        makeBuckup();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), getText(R.string.operation_cancelled), Toast.LENGTH_SHORT).show();
                    }
                });
        builder.show();
    }

    /*
    *************************************************************************
    METODY UżYWANE DO WYKONANIA ZMIAN JAKIE ZOSTAŁY WPROWADZONE W RECYCLEVIEW
    *************************************************************************
    */
    // korzystamy tutaj z interfejsu utworzonego dla klasy ItemListAdapter

    /**
     * Metody zaimplementowane z interfejsu
     * ItemListAdapter.ItemListAdapterListener
     * po to aby korzystać z tutejszych zasobów bez konieczności wyczytywnai
     * ich do Klasy ItemListAdapter.
     * @param item element klasy Item, który ma zostać usunięty.
     */
    @Override
    public void deleteItem(Item item)
    {
        mItemViewModel.deleteItem(item);
    }

    /**
     * metoda zmianijąca datę dla danego Itemu. pobiera datę którą chcemy wprowadzić
     * oraz id itemu który chemy zmodyfikować. Nie modyfikujemy samego siebie tzn. id m
     * muszą być różne.
     * Data nie może być późniejsza nic dziesiejsza
     *
     * @param chosenYear rok
     * @param chosenMonth miesiąc zmieści ssię od 1-12
     * @param chosenDay   dzień
     * @param id id
     */
    @Override
    public void changeDateOfOvertime(int chosenYear, int chosenMonth, int chosenDay, int id)
    {
        // sprawdzanie czy nie chcemy zmienić wpisu na samego siebie. tzn czy uid się nie pokrywają
        int uidPreviousItem = mItemViewModel.getUIdfromDate(chosenYear, chosenMonth, chosenDay);
        if (id != uidPreviousItem)
        {
            LocalDate date = LocalDate.of(chosenYear, chosenMonth, chosenDay);
            LocalDate today = LocalDate.now();
            // sprawdzanie czy data nie jest późniejsza niż dzisejsza
            if (!date.isAfter(today))
            {
                // chosenMonth musi być od 1-12
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.YEAR, chosenYear);
                calendar.set(Calendar.MONTH, chosenMonth-1);
                calendar.set(Calendar.DAY_OF_MONTH, chosenDay);
                // przypisanie wartości odpowiadającej dniowi tygodnia
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                // sprawdzenie czy dany wpis nie jest już obecny w bazie danych
                int existInDB = mItemViewModel.checkNumberOfItemsWithDate(chosenYear, chosenMonth, chosenDay);
                if (existInDB > 0 )
                {
                    //  jeśli jest więcej niż 0 to znaczy że jest już w bazie wpis o takiej dacie
                    //  i dlatego trzeba zapytać czy należy go podmienić, stąd tworzymy dialog i
                    //  pytamy jeśli użytkownik kllika zamień to wpis zostanie skasowany a bierzący będzie miał jego datę

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.replace_item)
                            .setMessage(R.string.replace_item_explenation)
                            .setPositiveButton(R.string.replace, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    mItemViewModel.updateDateOfOvertime(chosenYear, chosenMonth, chosenDay, dayOfWeek, id);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Toast.makeText(thisContext, R.string.operation_cancelled, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
                else if (existInDB == -1)
                    Toast.makeText(this, "Something has gone wrong.", Toast.LENGTH_SHORT).show();
                else
                    mItemViewModel.updateDateOfOvertime(chosenYear, chosenMonth, chosenDay, dayOfWeek, id);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.incorrect_date)
                        .setIcon(R.drawable.ic_round_error_outline_24px)
                        .setMessage(R.string.cannot_setup_future_date)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        })
                        .show();
            }
        }
        else
            Toast.makeText(this, R.string.nothing_to_change, Toast.LENGTH_SHORT).show();
    }

    /**
     * metoda z interfejsu ItemListAdapter.ItemListAdapterListener
     * wywoływana gdy użytkownik będzie chciał zminić liczbę godzin lub minut
     * @param hours ilość nowych godzin
     * @param minutes ilość nowych minut
     * @param id id elementu który modyfikujemy.
     */
    @Override
    public void changeNumberOfMinutesAndHours(int hours, int minutes, int id)
    {
        mItemViewModel.updateNumberOfMinutesAndHours(hours, minutes, id);
    }

    @Override
    public void saveNote(String note, int id)
    {
        mItemViewModel.updateNote(note, id);
    }


    /**
     * metoda wywoływana w momencie gdy użytkownik w dialogu szukania wpisze liczbę minut
     * większa niż 60 i naciśnie OK w celu szukania.
     */
    @Override
    public void showInvalidMinutesNumberDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.incorrect_minutes)
                .setMessage(R.string.minutes_lower_60)
                .setIcon(R.drawable.ic_round_error_outline_24px)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                })
                .show();
    }

    /**
     * Metoda jest callbackiem, który jest wywoływany gdy użytkownik zatwierdzi w dialogu
     * kryteria wyszukiwania (kryteria są argumentami tej metody).
     * Wewnątrz metody ładujemy wyniki wyszukiwania w bazie i zapisujemy je w liveView
     * selecteditems w ItemViewModel następnie sprawdzamy czy dla tego wyszukiwania
     * istnieje juz jakiś observer, jeśli tak to kasujemy go i tworzymy nowy. Po czym wewnątrz
     * observera dokonujemy przypisania znalezionych danych do recycleView. Teraz przy każdej
     * kolejnej zmianie w bazie danych dane te będą automatycznie aktualizowane w recycleView
     * W przypadku gdy następi obrócenie telefonu, ten observer jest kasowany, ale w onCreate()
     * tworzony jest ponownie kolejny tylko że tym razem selectedItems w ItemViewModel nie jest
     * już null i dlatego observer jest skierowany na selectedItems.
     * @param chosenHours godziny
     * @param chosenMinutes minuty
     * @param flags flaga określająca kryteria wyszukiwawcze.
     */
    @Override
    public void manageChosenCriteria(int yearOfOvertime, int monthOfOvertime, int dayOfOvertime,
                                     int chosenHours, int chosenMinutes, SearchFlags flags)
    {
        mItemViewModel.loadItemsWhere(
                yearOfOvertime, monthOfOvertime, dayOfOvertime, chosenHours, chosenMinutes, flags);

        if(mItemViewModel.getAllItems().hasActiveObservers())
            mItemViewModel.getAllItems().removeObservers(this);

        mItemViewModel.getAllItems().observe(this, new Observer<List<Item>>()
        {
            @Override
            public void onChanged(List<Item> items)
            {
                adapter.setItems(items);
            }
        });
    }

    /**
     * metoda resetująca główny obserwer dla mItemViewModel
     * usówa go a następnie wczytóje wszystkie dane ponownie.
     */
    private void resetGetAllItemsObserver()
    {
        if(mItemViewModel.getAllItems().hasActiveObservers())
            mItemViewModel.getAllItems().removeObservers(this);
        mItemViewModel.getAllItems().observe(this, new Observer<List<Item>>()
        {
            @Override
            public void onChanged(List<Item> items)
            {
                adapter.setItems(items);
            }
        });
    }
}
























