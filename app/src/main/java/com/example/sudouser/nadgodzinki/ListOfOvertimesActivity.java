package com.example.sudouser.nadgodzinki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.BuckUp.BuckUpFile;
import com.example.sudouser.nadgodzinki.BuckUp.MyFileProvider;
import com.example.sudouser.nadgodzinki.BuckUp.XmlParser;
import com.example.sudouser.nadgodzinki.Dialogs.SearchFilterItemsDialog;
import com.example.sudouser.nadgodzinki.Dialogs.SearchHelpers.SearchFlags;
import com.example.sudouser.nadgodzinki.RecyclerViewMain.ItemListAdapter;
import com.example.sudouser.nadgodzinki.ViewModels.ItemViewModel;
import com.example.sudouser.nadgodzinki.db.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// implementuje ActivityCompat.OnRequestPermissionsResultCallback bo używamy intentu, który ma zwracać jakiś resultat
public class ListOfOvertimesActivity
        extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        ItemListAdapter.ItemListAdapterListener,
        SearchFilterItemsDialog.ChosenSearchCriteriaListener
{
    private ItemViewModel mItemViewModel;
    private SharedPreferences sharedPreferences;
    private List<Item> listOfItems =  new ArrayList<>();
    private Context thisContext = this;
    private ItemListAdapter adapter;

    // stałe wykorzystane w metodzie readBuckUp()
    private static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_XML_OPEN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_info);

        mItemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        RecyclerView recyclerView = findViewById(R.id.allItemsTable);
        adapter = new ItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.search_and_filter_button).requestFocus();

        mItemViewModel.getAllItems().observe(this, new Observer<List<Item>>()
        {
            @Override
            public void onChanged(List<Item> items)
            {
                listOfItems = items;
                adapter.setItems(items);
            }
        });


        Button searchFilterButton = findViewById(R.id.search_and_filter_button);
        searchFilterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SearchFilterItemsDialog searcher = new SearchFilterItemsDialog();
                searcher.show(getSupportFragmentManager(), "search_filter_tag");
            }
        });

        Button clearSearch = findViewById(R.id.clear_search_criteria);
        clearSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setNewObserver();
            }
        });



        if (getIntent().getBooleanExtra("fromNotifier", false))
            makeBuckup();
    }

    /*
    ****************************************************
    METODY UZYWANE DO OBSŁUGI MENU
    ****************************************************
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menuItemSaveBuckup:
                makeBuckup();
                return true;
            case R.id.menuItemReadBuckup:
                readBuckUp();
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
    METODY UZYWANE DO WYCZTANIA BUCKUP'U
    ****************************************************
    */

    /**
     * Metoda używana do wczytywania pliku buckupowego.
     */
    private void readBuckUp()
    {
        if (isExternalStorageWritable())
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permision is not granted", Toast.LENGTH_SHORT).show();
                // jako że granted (dostęp) nie jest zaspokojone, trzeba wyświetlić urzytkownikowi
                // wyjaśnienie dlaczego należy zapytanie zaakceptować.
                // w razie problemów należy tutaj zmienić rzutownaie w pierwszym argumencie na Activity
                if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) this, Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                }
                else
                { // to jest wywołanie dialogu z pytaniem o dostęp do folderów.
                    ActivityCompat.requestPermissions((AppCompatActivity) this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                }
            }
            else
            { // to jest kod, który jest wykonywany gdy permission is granted. tzn. gdy użytkownik zezwoli na dostęp do plików
                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String name = ".xml";
                File[] lista = folder.listFiles(); // wczytuje listę plików i folderów z danej lokalizacji
                // jeśli lista nie jest pusta czyli w folderze Downloads są pliki (jakiekolwiek)
                if (lista != null)
                {
                    // ekstrahuje ścieżki zawierające rozszerzenie xml.
                    File[] nowaLista = Arrays.stream(lista)
                            //.filter(file -> file.isFile()) // odfiltrowuje tutaj directory
                            .filter(File::isFile)            // można też odfiltrować referencją
                            .filter(file -> file.getName().matches(".*" + name)) // odfiltrowuje tutaj nazwy plików nie zawierające poszukiwanej nazwy.
                            .toArray(File[]::new);

                    // jeśli lista jest pusta to wyświetlam dialog z informacją, że nie ma żadnego pasującego pliku
                    if (nowaLista.length == 0)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(R.string.error)
                                .setMessage(R.string.error_file_does_not_exist)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        //dialog.cancel();
                                    }
                                });
                        builder.show();
                    }
                    else // jeśli natomiast lista nie jest pusta to wyświetl dialog z listą do pojedyńczego odtickowania.
                    {
                        // tutaj ekstrahuje ścieżki do samych nazw plików, które zostanę wyświetlone w dialogu do odtick'owania.
                        String[] listaPlikow = Arrays.stream(nowaLista).map(File::getName).toArray(String[]::new);
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(R.string.selectBuckupFile)
                                .setItems(listaPlikow, new DialogInterface.OnClickListener()
                                {
                                    // metoda, która zostanie wywołana, gdy użytkownik kliknie w którąś z pozycji z nazwą pliku
                                    // argument which jest indeksem pozycji którą wybieram
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        File chosenFile = nowaLista[which];
                                        // wczytuję ścieżkę do pliku zakładając, że kolejność ścieżek absolutnych w nowaLista jest
                                        // taka sama jak w listaPlikow, przez to wybierając plik wybieram ścieżkę absolutną, którą
                                        // następnie użyje do wczytania i parsingu pliku.
                                        // parser wczytuje wybraną ścieżkę
                                        XmlParser parser = new XmlParser(thisContext, chosenFile);
                                        List<Item> readItems = parser.returnList();
                                        mItemViewModel.mergeDatabaseWithBuckupFile(readItems);
                                    }
                                });
                        builder.show();
                    }
                }
                else
                    Toast.makeText(this, folder.getPath(), Toast.LENGTH_SHORT).show();
            }
        }
        else // to jest w przypadku gdy nie można dostać się do external location bo sd card jest niedostępna
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error)
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
     * Metoda wywoływana gdy wyświtli się zapytanie (dialog generowany automatycznie przez Androida)
     * o dostęp do folderów.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    /**
     * Metoda, która jest wywoływana gdy Intent do wybrania pliku buckuupowego, który wywołaliśmy
     * i który ma zwrócić resultat zwraca właśnie ten rezultat. wewnątrz ciała funkcji ma się znaleźć
     * cały kod, który obsłuży plik buckupowy
     * @param requestCode wartość którą chcemy otrzymać
     * @param resultCode  wartość którą otrzymujemy
     * @param data        intent który zwraca nam wartość
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_XML_OPEN && resultCode == RESULT_OK)
        {
            Uri fileUri = data.getData(); // TODO sprawdzić jak wygrzebać ścieżkę z tego URI
            if (DocumentsContract.isDocumentUri(this, fileUri))
            {
                try
                {
                    DocumentsContract.Path docPath  = DocumentsContract.findDocumentPath(getContentResolver(),fileUri);
                    List<String> listaPath = docPath.getPath();
                    String wynik = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s: listaPath)
                    {
                        stringBuilder.append(s);
                    }
                    wynik = stringBuilder.toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.error)
                            .setMessage(wynik)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.cancel();
                                }
                            });
                    builder.show();


                    Toast.makeText(this, "DOCUMENT URI!!!", Toast.LENGTH_SHORT).show();
                    String path;
                    Cursor cursor = null;
                    try
                    {
                        if (fileUri != null)
                        {
                            String[] projection = {DocumentsContract.Document.COLUMN_DISPLAY_NAME};
                            cursor = getContentResolver().query(fileUri, projection, null, null, null);
                            int column_index = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME);
                            cursor.moveToFirst();
                            path = cursor.getString(column_index);
                            Toast.makeText(this, "path: " + path, Toast.LENGTH_LONG).show();

                            if (fileUri.getPath().equals(path))
                                Toast.makeText(this, "ścieżki są te same", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (NullPointerException e)
                    {
                        // Log.e("TAG", "getRealPathFromURI Exception : " + e.toString());
                        Toast.makeText(this, "Wywaliło wyjątek " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (cursor != null)
                            cursor.close();
                    }
                    //XmlParser parser = new XmlParser(this, file);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(this, "its not document uri", Toast.LENGTH_SHORT).show();
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
        /*
         * jeśli ustawimy że uri jest adresem email to nie ma możliwości by uruchomiła się
         * aplikacji inna niż email, bo inne nie obsługuja adresów emailowych
         */
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
        //intent.setData(Uri.parse("mailto:"));
        //intent.setDataAndType(Uri.parse("mailto:"), "*/*"); // to powoduje cannot resolve patrz else poniżej
        intent.setType("*/*");
        String[] lista = new String[] {email};
        intent.putExtra(Intent.EXTRA_EMAIL, lista);
        LocalDateTime dzisiejszaData = LocalDateTime.now();
        intent.putExtra(Intent.EXTRA_SUBJECT, "Buckup " + dzisiejszaData.toString());

        if (listOfItems == null)
        {
            Toast.makeText(this, "listOfItems jest NULL", Toast.LENGTH_SHORT).show();
            return new Intent();
        }
        else
        {
            System.out.println("Jest ok nie ma null pointera");
            BuckUpFile file = new BuckUpFile(this, listOfItems);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri buckupUri = MyFileProvider.getUriForFile(getApplicationContext(),
                    "com.example.sudouser.nadgodzinki.BuckUp.MyFileProvider", file.getFile());
            intent.putExtra("path", file.getFile().getPath());
            intent.putExtra(Intent.EXTRA_STREAM, buckupUri);
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


    @Override
    public void deleteItem(Item item)
    {
        mItemViewModel.deleteItem(item);
    }

    @Override
    public void changeDateOfOvertime(int chosenYear, int chosenMonth, int chosenDay, int id)
    {
        //
        LocalDate date = LocalDate.of(chosenYear, chosenMonth, chosenDay);
        LocalDate today = LocalDate.now();
        // sprawdzanie czy nie chcemy zmienić wpisu na samego siebie. tzn czy uid się nie pokrywają
        int uidPreviousItem = mItemViewModel.getUIdfromDate(chosenYear, chosenMonth, chosenDay);
        if (id != uidPreviousItem)
        {
            if (!date.isAfter(today))
            {
                // chosenMonth musi być od 1-12
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.set(Calendar.YEAR, chosenYear);
                calendar.set(Calendar.MONTH, chosenMonth-1);
                calendar.set(Calendar.DAY_OF_MONTH, chosenDay);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                int existInDB = mItemViewModel.checkNumberOfItemsWithDate(chosenYear, chosenMonth, chosenDay);
                if (existInDB > 0 )
                {
                    // TODO jeśli jest więcej niż 0 to znaczy że jest już w bazie wpis o takiej nazwie
                    // TODO i dlatego trzeba zapytać czy należy go kasować, stąd tworzymy dialog i
                    // TODO pytamy jeśli użytkownik kllika zamień to wpis zostanie skasowany a bierzący będzie miał jego datę

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
     * metoda wywoływana gdy użytkownik będzie chciał zminić liczbę godzin lub minut
     * @param hours
     * @param minutes
     * @param id
     */
    @Override
    public void changeNumberOfMinutesAndHours(int hours, int minutes, int id)
    {
        mItemViewModel.updateNumberOfMinutesAndHours(hours, minutes, id);
        findViewById(R.id.search_and_filter_button).requestFocus();
        //getWindow().setSoftInputMode(SOFT_INPUT_STATE_HIDDEN);
        /*
        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                findViewById(R.id.spinnerEditTextMinutes).getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS); // hahhaha it's working!!!
         */
    }

    @Override
    public void saveNote(String note, int id)
    {
        mItemViewModel.updateNote(note, id);
        findViewById(R.id.search_and_filter_button).requestFocus();
        //getWindow().setSoftInputMode(SOFT_INPUT_STATE_HIDDEN);
        /*
        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                findViewById(R.id.spinnerEditTextMinutes).getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS); // hahhaha it's working!!!
         */

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

        if(mItemViewModel.getSelectedItems().hasActiveObservers())
            mItemViewModel.getSelectedItems().removeObservers(this);
        if(mItemViewModel.getAllItems().hasActiveObservers())
            mItemViewModel.getAllItems().removeObservers(this);

        mItemViewModel.getSelectedItems().observe(this, new Observer<List<Item>>()
        {
            @Override
            public void onChanged(List<Item> items)
            {
                adapter.setItems(items);
            }
        });
    }


    private void setNewObserver()
    {
        if(mItemViewModel.getSelectedItems() != null && mItemViewModel.getSelectedItems().hasObservers())
            mItemViewModel.getSelectedItems().removeObservers(this);
        if(mItemViewModel.getAllItems().hasActiveObservers())
            mItemViewModel.getAllItems().removeObservers(this);
        mItemViewModel.clearSearchCriteria();
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





























