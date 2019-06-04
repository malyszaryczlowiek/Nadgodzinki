package com.example.sudouser.nadgodzinki.Dialogs.MainActivityDialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sudouser.nadgodzinki.MainActivity;
import com.example.sudouser.nadgodzinki.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AskAboutEmailDialog extends AppCompatDialogFragment
{
    private AskAboutEmailInterface listener;

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try
        {
            listener = (AskAboutEmailInterface) context;
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
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        // następnie wczytujemy view które ma byc wstawione do AlertDialogu
        View promptUserView = layoutInflater.inflate(R.layout.email_dialog, null);
        final EditText userAnswer = promptUserView.findViewById(R.id.email_editText_dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.set_buckup_email)
                .setView(promptUserView)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.changeEmail(userAnswer.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    public interface AskAboutEmailInterface
    {
        void changeEmail(String email);
    }
}
