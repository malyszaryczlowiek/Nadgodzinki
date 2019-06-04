package com.github.malyszaryczlowiek.nadgodzinki.Dialogs.MainActivityDialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.github.malyszaryczlowiek.nadgodzinki.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class IncorrectHoursCountErrorDialog extends AppCompatDialogFragment
{
    private ClearEditTextInterface listener;

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try
        {
            listener = (ClearEditTextInterface) context;
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
        builder.setTitle(R.string.incorrect_minutes)
                .setIcon(R.drawable.ic_round_error_outline_24px)
                .setMessage(R.string.minutes_lower_60)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.clearHoursEditTextInterface();
                    }
                });
        return builder.create();
    }

    public interface ClearEditTextInterface
    {
        void clearHoursEditTextInterface();
    }
}
