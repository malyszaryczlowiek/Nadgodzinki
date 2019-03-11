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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.BuckUp.BuckUpFile;
import com.example.sudouser.nadgodzinki.BuckUp.MyFileProvider;
import com.example.sudouser.nadgodzinki.RecyclerViewMain.ItemListAdapter;
import com.example.sudouser.nadgodzinki.db.Item;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatsInfoActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private ItemViewModel mItemViewModel;
    private SharedPreferences sharedPreferences;
    private List<Item> listOfItems =  new ArrayList<>();

    // stałe wykorzystane w metodzie readBuckUp()
    private static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_XML_OPEN = 1;
    private int wybranaLiczba;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_info);

        mItemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mItemViewModel.getAllItems().observe(this, items -> listOfItems = items );

        RecyclerView recyclerView = findViewById(R.id.allItemsTable);
        final ItemListAdapter adapter = new ItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mItemViewModel.getAllItems().observe(this, new Observer<List<Item>>()
        {
            @Override
            public void onChanged(List<Item> items)
            {
                adapter.setmItems(items);
            }
        });
    }

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



    private void readBuckUp()
    {
        //ReadBuckUp readBuckUp = new ReadBuckUp(this);

        if (isExternalStorageWritable())
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
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
            {
                // Toast.makeText(context, "permision is granted", Toast.LENGTH_SHORT).show();
                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                ///intent.setDataAndType( Uri.fromFile(folder), "text/xml");
                intent.setType("text/xml");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
                startActivityForResult(intent, REQUEST_XML_OPEN);
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
     * to jest metoda wywoływana gdy wyświtli się zapytanie o dostęp do folderów.
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
        if (requestCode == REQUEST_XML_OPEN && resultCode == RESULT_OK) {
            // tutaj zawarty jest kod
            Uri fileUri = data.getData(); // TODO sprawdzić jak wygrzebać ścieżkę z tego URI

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error)
                    .setMessage("file name: ")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();
                        }
                    });
            builder.show();


            // todo tutaj trzeba teraz umieścić całą machinę ze sprawdzeniem czy
        }
    }


    /*
    ****************************************************
    METODY URZYWANE DO ZROBIENIA BUCKUP'U
    ****************************************************
    */

    private void makeBuckup()
    {
        // tutaj lepiej dać applicationContext niż this, który odnośi się do aktywności CHYBA, choć this tez działa poprawnie.
        // można też użyć contextu this któ©ego lifecycle jest attached to current context

        String email = sharedPreferences.getString("buckup_email","");
        if (email.equals("")) // Todo też do weryfikacji dlaczego może dawac nullPointerException
        {
            LayoutInflater layoutInflater = LayoutInflater.from(StatsInfoActivity.this);
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

                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.buckup_email_address_changed), duration);
                            toast.show();

                            wyslijbuckup();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();

                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.operation_cancelled), duration);
                            toast.show();
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
            System.out.println("CAŁY CZAS NULL POINTER");

            return new Intent();
        }
        else
        {
            System.out.println("Jest ok nie ma null pointera");
            BuckUpFile file = new BuckUpFile(this, listOfItems);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri buckupUri = MyFileProvider.getUriForFile(getApplicationContext(), "com.example.sudouser.nadgodzinki.BuckUp.MyFileProvider", file.getFile());
            intent.putExtra("path", file.getFile().getPath());
            intent.putExtra(Intent.EXTRA_STREAM, buckupUri);

            return intent;
        }
    }



    /*
    ****************************************************
    METODY URZYWANE DO CZYSZCZENIA BAZY DANYCH
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
                        Integer expected = mItemViewModel.getAllItems().getValue().size(); // TODO może produkować nullPointerException
                        Integer result = mItemViewModel.clearDatabase();
                        int i = -1;

                        if (result.equals(expected))
                        {
                            dialog.cancel();

                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.database_has_beeen_cleared), duration);
                            toast.show();
                        }
                        else if (result.equals(-1))
                        {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "Exception appeared.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            dialog.cancel();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getApplicationContext(), "Other problems appeared.", duration);
                            toast.show();
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

                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), getText(R.string.operation_cancelled), duration);
                        toast.show();
                    }
                });
        builder.show();
    }
}





















