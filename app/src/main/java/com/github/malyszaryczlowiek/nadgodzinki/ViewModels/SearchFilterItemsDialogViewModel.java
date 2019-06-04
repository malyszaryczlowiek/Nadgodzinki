package com.github.malyszaryczlowiek.nadgodzinki.ViewModels;

import android.app.Application;
import android.icu.util.Calendar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SearchFilterItemsDialogViewModel extends AndroidViewModel
{
    private MutableLiveData<Calendar> setDate;

    public SearchFilterItemsDialogViewModel(@NonNull Application application)
    {
        super(application);
        setDate = new MutableLiveData<>();
    }

    public void setSetDate(Calendar calendar)
    {
        setDate.setValue(calendar);
    }

    public LiveData<Calendar> getSetDate()
    {
        return setDate;
    }
}
