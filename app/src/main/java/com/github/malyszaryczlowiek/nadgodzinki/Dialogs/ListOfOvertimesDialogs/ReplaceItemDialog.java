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

public class ReplaceItemDialog extends AppCompatDialogFragment
{
    private OnSelectedButtonClicked listener;
    private int chosenYear;
    private int chosenMonth;
    private int chosenDay;
    private int id;

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try
        {
            listener = (OnSelectedButtonClicked) context;
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
        builder.setTitle(R.string.replace_item)
                .setMessage(R.string.replace_item_explenation)
                .setPositiveButton(R.string.replace, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.onPositiveButtonClicked(chosenYear, chosenMonth, chosenDay, id);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.onNegativeButtonClicked();
                    }
                });
        return builder.create();

    }

    public void setDate(int chosenYear, int chosenMonth, int chosenDay, int id)
    {
        this.chosenYear = chosenYear;
        this.chosenMonth = chosenMonth;
        this.chosenDay = chosenDay;
        this.id = id;
    }

    public interface OnSelectedButtonClicked
    {
        void onPositiveButtonClicked(int chosenYear, int chosenMonth, int chosenDay, int id);
        void onNegativeButtonClicked();
    }
}
