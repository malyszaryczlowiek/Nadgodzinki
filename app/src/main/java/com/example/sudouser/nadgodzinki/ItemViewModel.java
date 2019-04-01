package com.example.sudouser.nadgodzinki;

import android.app.Application;

import com.example.sudouser.nadgodzinki.db.Item;
import com.example.sudouser.nadgodzinki.db.Repository;

import java.time.LocalDate;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


/**
 * klasa ViewModel separuje dane do UI. tzn. UI jest odpowiedzialne za wyświetlenie
 * tych danych a VieWModel jest odpowiedzialny za przygotowanie tych dancyh dla UI.
 */
public class ItemViewModel extends AndroidViewModel
{
    /**  tworzymy  obiekty repozytorium oraz obiekt LiveData<>, do których
     * i z któ©ych będziemy przekazywali sobie dane. */
    private Repository mRepository;
    private LiveData<List<Item>> allItems;
    private MutableLiveData<LocalDate> localeDataLiveData = new MutableLiveData<>();

    public ItemViewModel(Application application)
    {
        super(application);
        mRepository = new Repository(application);
        allItems = mRepository.getAllItems();
    }


    public LiveData<List<Item>> getAllItems() { return allItems; }

    public void insert(Item item)
    {
        mRepository.insert(item);
    }

    public void deleteItem(Item item)
    {
        mRepository.delete(item);
    }

    public int clearDatabase()
    {
        return mRepository.clearDatabase();
    }

    public void mergeDatabaseWithBuckupFile(@NonNull List<Item> itemsFromBuckUp)
    {
        mRepository.mergeDatabaseWithBuckupFile(itemsFromBuckUp);
    }

    public void updateDateOfOvertime(String dayOfWeek, int day, int id)
    {
        mRepository.updateDayOfOvertime(dayOfWeek, day, id);
    }

    public void updateNumberOfMinutesAndHours(int hours, int minutes, int id)
    {
        mRepository.updateNumberOfMinutesAndHours(hours, minutes, id);
    }
    public void updateNote(String note, int id)
    {
        mRepository.updateNote(note, id);
    }

    public void setLocalDate(LocalDate date)
    {
        localeDataLiveData.setValue(date);
    }

    public LiveData<LocalDate> getLocalDate()
    {
        return localeDataLiveData;
    }
}
