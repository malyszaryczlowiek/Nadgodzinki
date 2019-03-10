package com.example.sudouser.nadgodzinki;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.BuckUp.BuckUpFile;
import com.example.sudouser.nadgodzinki.BuckUp.MyFileProvider;
import com.example.sudouser.nadgodzinki.BuckUp.ReadBuckUp;
import com.example.sudouser.nadgodzinki.RecyclerViewMain.ItemListAdapter;
import com.example.sudouser.nadgodzinki.db.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StatsInfoActivity extends AppCompatActivity
{
    private ItemViewModel mItemViewModel;
    private SharedPreferences sharedPreferences;
    private List<Item> listOfItems =  new ArrayList<>();

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
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getApplicationContext(), "Exception appeared.", duration);
                            toast.show();
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

    private void readBuckUp()
    {
        ReadBuckUp readBuckUp = new ReadBuckUp(this);
    }

    public void makeBuckup()
    {
        sendWithoutAttachment();
    }


    private void sendWithoutAttachment()
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
        {
            wyslijbuckup();
        }
    }

    /**
     * Metoda jest wywołana w metodzie sendWithoutAttachment() w przypadku gdy mamy już ustawiony
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
            System.out.println("Cannot resolve");
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
}
