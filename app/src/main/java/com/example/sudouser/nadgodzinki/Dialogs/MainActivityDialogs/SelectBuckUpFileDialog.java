package com.example.sudouser.nadgodzinki.Dialogs.MainActivityDialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.sudouser.nadgodzinki.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class SelectBuckUpFileDialog extends AppCompatDialogFragment
{
    private OnSelectedFileListener listener;
    private String[] listaPlikow;


    public void setArguments(String[] list)
    {
        listaPlikow = list;
    }


    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try
        {
            listener = (OnSelectedFileListener) context;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
            listaPlikow = savedInstanceState.getStringArray("listaPlikow");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.selectBuckupFile)
                .setItems(listaPlikow, new DialogInterface.OnClickListener()
                {
                    // metoda, która zostanie wywołana, gdy użytkownik kliknie w którąś z pozycji z nazwą pliku
                    // argument which jest indeksem pozycji którą wybieram
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.selectBuckUpFile(which);
                        /*
                        File chosenFile = nowaLista[which];
                        // wczytuję ścieżkę do pliku zakładając, że kolejność ścieżek absolutnych w nowaLista jest
                        // taka sama jak w listaPlikow, przez to wybierając plik wybieram ścieżkę absolutną, którą
                        // następnie użyje do wczytania i parsingu pliku.
                        // parser wczytuje wybraną ścieżkę
                        XmlParser parser = new XmlParser(thisContext, chosenFile);
                        List<Item> readItems = parser.returnList();
                        mItemViewModel.mergeDatabaseWithBuckupFile(readItems);
                         */
                    }
                });
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if (listaPlikow != null)
            outState.putStringArray("listaPlikow", listaPlikow);
    }

    public interface  OnSelectedFileListener
    {
        void selectBuckUpFile(int which);
    }

}
