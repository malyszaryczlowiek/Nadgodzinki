package com.example.sudouser.nadgodzinki.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.sudouser.nadgodzinki.Dialogs.SearchHelpers.SearchFlags;
import com.example.sudouser.nadgodzinki.R;

import java.time.LocalDate;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class SearchFilterItemsDialog extends AppCompatDialogFragment
{
    private CalendarView calendarView;
    private EditText spinnerEditTextHours;
    private EditText spinnerEditTextMinutes;
    private ChosenSearchCriteriaListener listener;
    private SearchFlags flags;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =  getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.search_filter_items_dialog, null);

        builder.setTitle(R.string.choose_search_criteria)
                .setView(view)
                .setPositiveButton(R.string.search, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        long chosenDate = calendarView.getDate();
                        LocalDate chosenDay = LocalDate.ofEpochDay(chosenDate / ((long) 1000 * 3600 * 24));

                        String hours = spinnerEditTextHours.getText().toString();
                        String minutes = spinnerEditTextMinutes.getText().toString();

                        try
                        {
                            if (hours.equals(""))
                                hours = "0";
                            int hoursInt = Integer.parseInt(hours);
                            if (minutes.equals(""))
                                minutes = "0";
                            int minutesInt = Integer.parseInt(minutes);
                            listener.manageChosenCriteria(chosenDay.toString(), hoursInt, minutesInt, flags);
                        }
                        catch (NumberFormatException e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // do nothing, criteria not changed.
                    }
                });

        flags = SearchFlags.createSearchFlagsObject();

        calendarView = view.findViewById(R.id.searchFilterCalendar);
        Calendar calendar = Calendar.getInstance();
        long todayLong = calendar.getTimeInMillis();
        calendarView.setMaxDate(todayLong);

        Spinner spinnerSortingDate = view.findViewById(R.id.spinnerSortingDate);
        ArrayAdapter<CharSequence> adaprerSpinerSorting = ArrayAdapter.createFromResource(
                getActivity(), R.array.spinner_sorting_date, android.R.layout.simple_spinner_item);
        adaprerSpinerSorting.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortingDate.setAdapter(adaprerSpinerSorting);
        spinnerSortingDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        flags.setSorting(SearchFlags.Sorting.DSC);
                        break;
                    case 1:
                        flags.setSorting(SearchFlags.Sorting.ASC);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        Spinner spinnerCuttingDate = view.findViewById(R.id.spinnerCuttingDate);
        ArrayAdapter<CharSequence> adaprerSpinerCutting = ArrayAdapter.createFromResource(
                getActivity(), R.array.spinner_cutting_date, android.R.layout.simple_spinner_item);
        adaprerSpinerCutting.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuttingDate.setAdapter(adaprerSpinerCutting);
        spinnerCuttingDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        flags.setCutting(SearchFlags.Cutting.BEFORE);
                        break;
                    case 1:
                        flags.setCutting(SearchFlags.Cutting.AFTER);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        Spinner spinnerOvertimeLength = view.findViewById(R.id.spinnerOvertimeLength);
        ArrayAdapter<CharSequence> adaprerSpinerOvertimeLength = ArrayAdapter.createFromResource(
                getActivity(), R.array.spinner_overtime_length, android.R.layout.simple_spinner_item);
        adaprerSpinerOvertimeLength.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOvertimeLength.setAdapter(adaprerSpinerOvertimeLength);
        spinnerOvertimeLength.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        flags.setLength(SearchFlags.Length.LONGER);
                        break;
                    case 1:
                        flags.setLength(SearchFlags.Length.SHORTER);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        spinnerEditTextHours = view.findViewById(R.id.spinnerEditTextHours);
        spinnerEditTextMinutes = view.findViewById(R.id.spinnerEditTextMinutes);

        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        try
        {
            listener = (ChosenSearchCriteriaListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException( context.toString() + " ChosenSearchCriteriaListener");
        }
    }

    public interface ChosenSearchCriteriaListener
    {
        void manageChosenCriteria(String chosenDate, int chosenHours, int chosenMinutes, SearchFlags flags);
    }
}


































