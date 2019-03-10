package com.example.sudouser.nadgodzinki.BuckUp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.R;

import java.io.File;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * klasa będzie odpowiedzialna za wczytanie pliku z buckupem i wyczyszczenie
 * bazy danych () ewentualnie porównanie bazy danych  i dostawienie brakujących rekordów
 */
public class ReadBuckUp implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private Context mContext;
    private static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;

    public ReadBuckUp(Context context)
    {
        mContext = context;

        // sprawdź czy jest umieszczona karta pamięci
        if (isExternalStorageWritable())
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "permision is not granted", duration);
                toast.show();

                // jako że granted (dostęp) nie jest zaspokojone, trzeba wyświetlić urzytkownikowi
                // wyjaśnienie dlaczego należy zapytanie zaakceptować.

                // todo w razie problemów należy tutaj zmienić rzutownaie w pierwszym argumencie na Activity
                if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context, Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                }
                else
                { // to jest wywołanie dialogu z pytaniem o dostęp do folderów.
                    ActivityCompat.requestPermissions((AppCompatActivity) context, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                }
            }
            else
            {
                Toast.makeText(context, "permision is granted", Toast.LENGTH_SHORT).show();


                //TODO poniższy kod należy wywlołać w oddzielnym wątku bo za dużo tutaj pieprzenia się,
                // sprawdź czy w folderze Downloads istnieje plik buckupowy.
                String name = context.getText(R.string.app_name) + "_Buckup.xml";
                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                File[] lista = folder.listFiles();
                if (lista != null)
                {
                    File[] nowaLista = Arrays.stream(lista)
                            //.filter(file -> file.isFile()) // odfiltrowuje tutaj directory
                            .filter(File::isFile)
                            .filter(file ->                // odfiltrowuje tutaj nazwy plików nie zawierające poszukiwanej nazwy.
                            {
                                String nazwa = file.getName();
                                return nazwa.matches(".*" + name);
                            })
                            .toArray(File[]::new);

                    // name = tutaj robię ponowne przypisanie do name tym razem z poprawną nazwą pliku buckupowego , który wybrał użytkownik.
                    // lista jest pusta to dialog, że w folderze Pobrane nie ma żadnego pasującego pliku Buckupowego i należy go pobrać z zewnętrzego źródłą
                    if (nowaLista.length == 0)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                    else // jeśli natomiast lista nie jest pusta to wyświetl dialog z listą do odtickowania.
                    {
                        Toast.makeText(context, "wyświtlam dialog z listą plików ", Toast.LENGTH_SHORT).show();
                        //TODO w pierwszej kolejnośco po powrocie z wyjazdu zrobić ten dialog z listą plików gdzie wybiera się jeden który ma byc wczytany
                        // jako plik buckupowy
                        // wdrugiej kolejności zrobić api do usunięcia konkretnego wpisu tak aby
                        // wybierać w dialogu datę, którą chcemu skasować i zrobić powiadomienia jeśli nie ma w bazie wpisu z takeigo dnia?


                        // następnie zrobić parser do wczytywania danych z xml'a
                        // dsfgsa
                    }
                }
                else
                    Toast.makeText(context, folder.getPath(), Toast.LENGTH_SHORT).show();
            }
        }
        else // to jest w przypadku gdy nie można dostać się do external location bo sd card jest niedostępna
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    /*
    to jest metoda wywoływana gdy wyświtli się zapytanie o dostęp do folderów.
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
     * funkcja wczytuje folder, następnie z folderu wczytuje nazwy wszystkich plików (nie folderów - zrobić sprawdzanie)
     * następnie regexem wyłuskuję te które zawierają Nadgodzinki_Buckup.xml i tak przygotowaną listę wyświetlam na dialogue
     * gdzie urzytkowniek odtickowuje który plik chce wczytać
     * @param folder File, który zawiera folder do przeszukania.
     * @return zwraca File który został wybrany przez urzytkownika.
     */
    private File selectFile(File folder)
    {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "nazwa_pliku");
    }
}
