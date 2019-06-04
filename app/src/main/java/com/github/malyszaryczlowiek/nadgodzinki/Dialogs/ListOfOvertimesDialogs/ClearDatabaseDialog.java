package com.github.malyszaryczlowiek.nadgodzinki.Dialogs.ListOfOvertimesDialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.github.malyszaryczlowiek.nadgodzinki.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ClearDatabaseDialog extends AppCompatDialogFragment
{
    private OnSelectedOption listener;

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try
        {
            listener = (OnSelectedOption) context;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.clear_database)
                .setMessage(R.string.clear_database_desctription)
                .setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.clearDatabaseClear();
                    }
                })
                .setNeutralButton(R.string.make_buckup, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.clearDatabaseMakeBuckUp();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.clearDatabaseOperationCancelled();

                    }
                });
        return builder.create();
    }

    public interface OnSelectedOption
    {
        void clearDatabaseClear();
        void clearDatabaseMakeBuckUp();
        void clearDatabaseOperationCancelled();
    }
}














