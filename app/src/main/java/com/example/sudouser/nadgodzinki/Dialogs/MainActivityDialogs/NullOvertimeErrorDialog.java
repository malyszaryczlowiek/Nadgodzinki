package com.example.sudouser.nadgodzinki.Dialogs.MainActivityDialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.sudouser.nadgodzinki.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class NullOvertimeErrorDialog extends AppCompatDialogFragment
{
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.incorrect_overtime)
                .setIcon(R.drawable.ic_round_error_outline_24px)
                .setMessage(R.string.set_non_null_time)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // nic nie zmieniaj, użytkownik ma tylko przyjąć do wiadomości, że
                        // trzeba wprowadzić dane.
                    }
                });
        return builder.create();
    }
}
