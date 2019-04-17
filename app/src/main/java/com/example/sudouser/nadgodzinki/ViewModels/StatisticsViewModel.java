package com.example.sudouser.nadgodzinki.ViewModels;

import android.app.Application;

import com.example.sudouser.nadgodzinki.db.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class StatisticsViewModel extends AndroidViewModel
{
    private Repository mRepository;

    public StatisticsViewModel(@NonNull Application application)
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
}
