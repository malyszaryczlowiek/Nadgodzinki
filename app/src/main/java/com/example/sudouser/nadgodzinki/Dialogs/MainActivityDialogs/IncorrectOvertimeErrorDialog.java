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

public class IncorrectOvertimeErrorDialog extends AppCompatDialogFragment
{
    private ClearEditTextInterface listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.incorrect_time)
                .setIcon(R.drawable.ic_round_error_outline_24px)
                .setMessage(R.string.minutes_and_time_above_level)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.clearOvertimeEditTextInterface();
                    }
                });
        return builder.create();
    }

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

    public interface ClearEditTextInterface
    {
        void clearOvertimeEditTextInterface();
    }
}
