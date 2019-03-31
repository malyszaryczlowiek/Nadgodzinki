package com.example.sudouser.nadgodzinki.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.sudouser.nadgodzinki.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Solution taken from https://www.youtube.com/watch?v=ARezg1D9Zd0
 */
public class NoteDialog extends AppCompatDialogFragment
{
    private EditText noteEditText;// noteDialogEditText
    private RadioButton showAgainButton;
    private NoteDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.note_dialog, null);

        builder.setTitle(R.string.add_note_question)
                .setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String note = noteEditText.getText().toString();
                        boolean show = showAgainButton.isChecked();
                        listener.applyChanges(note, show);
                    }
                })
                .setNegativeButton(R.string.no_add, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String note = "";
                        boolean show = showAgainButton.isChecked();
                        listener.applyChanges(note, show);
                    }
                });
        noteEditText = view.findViewById(R.id.noteDialogEdtText);
        showAgainButton = view.findViewById(R.id.showNoteDialogAgainButton);

        return builder.create();
    }

    public interface NoteDialogListener
    {
        void applyChanges(String note, boolean show);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        // inicjalizujemy nasz listener
        try
        {
            listener = (NoteDialogListener) context;
        }
        catch (ClassCastException en)
        {
            throw new ClassCastException( context.toString() + " must implement NoteDialogListener");
        }
    }
}


// alt insert - skrót do wyświetlenie wszystkich metod które można nadpisać z klasy/interfejsu po
// któ©ych dzidziczymy

// ctrl alt t - skrót do wrapowania danyj lini np w block try czy while czy if.







