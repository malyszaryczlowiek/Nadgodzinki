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

public class ItemExistsDialog extends AppCompatDialogFragment
{
    private ItemExistsButtonClickedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.item_exists_in_db)
                .setIcon(R.drawable.ic_round_error_outline_24px)
                .setMessage(R.string.do_you_want_change_item)
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        listener.itemExistDialogListenerPositiveButtonClicked();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // do nothing
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
            listener = (ItemExistsButtonClickedListener) context;
        }
        catch (Exception e)
        {
            throw new ClassCastException( context.toString() + " must implement NoteDialogListener");
        }
    }

    public interface ItemExistsButtonClickedListener
    {
        void itemExistDialogListenerPositiveButtonClicked();
    }
}
