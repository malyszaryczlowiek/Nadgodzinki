package com.github.malyszaryczlowiek.nadgodzinki.ViewModels;

import android.app.Application;

import com.github.malyszaryczlowiek.nadgodzinki.db.Item;
import com.github.malyszaryczlowiek.nadgodzinki.db.Repository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class StatisticsViewModel extends AndroidViewModel
{
    private Repository mRepository;
    //private LiveData<List<Item>> mLiveData;

    public StatisticsViewModel(Application application)
    {
        super(application);
        mRepository = new Repository(application);
    }

    public int numberOfMinutesDone(int year, int month, int day)
    {
        return mRepository.numberOfMinutesDone(year, month, day);
    }

    public int numberOfMinutesTaken(int year, int month, int day)
    {
        return mRepository.numberOfMinutesTaken(year, month, day);
    }

    public int allNumberOfMinutesTaken()
    {
        return mRepository.allNumberOfMinutesToTake();
    }

    public List<Item> getListOfItemsFrom(int year, int month, int day)
    {
        return mRepository.listOfItemsSince(year, month, day);
    }
}
