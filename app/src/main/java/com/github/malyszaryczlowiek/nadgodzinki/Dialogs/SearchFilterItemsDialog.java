package com.github.malyszaryczlowiek.nadgodzinki.Dialogs;

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
import android.icu.util.Calendar;

import com.github.malyszaryczlowiek.nadgodzinki.Dialogs.SearchHelpers.SearchFlags;
import com.github.malyszaryczlowiek.nadgodzinki.R;
import com.github.malyszaryczlowiek.nadgodzinki.ViewModels.SearchFilterItemsDialogViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
        SearchFilterItemsDialogViewModel mViewModel;
        mViewModel = ViewModelProviders.of(getActivity()).get(SearchFilterItemsDialogViewModel.class);

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
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(chosenDate);
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

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
                            if (Math.abs(minutesInt) >= 60)
                            {
                                dialog.cancel();
                                listener.showInvalidMinutesNumberDialog();
                            }
                            else
                                listener.manageChosenCriteria(year, month, day, hoursInt, minutesInt, flags);
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
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mViewModel.setSetDate(cal);
            }
        });

        mViewModel.getSetDate().observe(getActivity(), new Observer<Calendar>()
        {
            @Override
            public void onChanged(Calendar calendar)
            {
                long dataLong = calendar.getTimeInMillis();
                calendarView.setDate(dataLong);
            }
        });

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

        Spinner spinnerOvertimeType = view.findViewById(R.id.spinnerOvertimeType);
        ArrayAdapter<CharSequence> adaprerSpinerOvertimeType = ArrayAdapter.createFromResource(
                getActivity(), R.array.spinner_overtime_type, android.R.layout.simple_spinner_item);
        adaprerSpinerOvertimeType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOvertimeType.setAdapter(adaprerSpinerOvertimeType);
        spinnerOvertimeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        flags.setType(SearchFlags.Type.ALL);
                        break;
                    case 1:
                        flags.setType(SearchFlags.Type.DONE);
                        break;
                    case 2:
                        flags.setType(SearchFlags.Type.TAKEN);
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
        void manageChosenCriteria(int yearOfOvertime, int monthOfOvertime, int dayOfOvertime,
                                  int chosenHours, int chosenMinutes, SearchFlags flags);
        void showInvalidMinutesNumberDialog();
    }
}


































