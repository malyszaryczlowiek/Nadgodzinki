package com.example.sudouser.nadgodzinki.db;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.sudouser.nadgodzinki.Dialogs.SearchHelpers.SearchFlags;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Repository
{
    // obiekt TabelaDao tak jak w klasie BazaDanych.
    private TabelaDao tabelaDao;
    private LiveData<List<Item>> allItems;
    //private Context applicationContext;

    public Repository(Application application)
    {
        /* inicjalizujemy obiekt bazy danych ()tworzymy bazę danych*/
        BazaDanych db = BazaDanych.getDatabase(application);
        //applicationContext = application.getApplicationContext();

        /* przypisujemy do wywoływań bazę danych */
        tabelaDao = db.tabelaDao();
        allItems = tabelaDao.loadAllItems();
    }


    /** to jest wrapper za pomocą którego będziemy przekazywali wszystkie znaleizone
     * itemy wyżej, patrz nie jest to wykonywanie w innym wątku.
     * @return zwraca LiveData<List<Item>>,
     */
    public LiveData<List<Item>> getAllItems()
    {
        return allItems;
    }


    /** w tym momencie wewnątrz tej funkcji tworząc nowy obiekt insertAsyncTask()
     * insertujemy item w innym wątku. ukrywamy w ten sposób implementację wielowątkowości
     * którą stosujemy.
     * */
    public void insert(Item item)
    {
        new insertAsyncTask(tabelaDao).execute(item);
    }


    /**
     * query zwracające LiveData<List<Item>> lub Flowable są same z siebie wykonywane asynchronicznie
     * dlatego nie ma potrzeby umieszczać ich w nowym wątku.
     * @param chosenYear
     * @param chosenHours
     * @param chosenMinutes
     * @param flags
     * @return
     */
    @NonNull
    public LiveData<List<Item>> loadItemsWhere(int chosenYear, int chosenMonth, int chosenDay, int chosenHours, int chosenMinutes, SearchFlags flags)
    {
        switch (flags.getSorting())
        {
            case DSC:
                switch (flags.getCutting())
                {
                    case BEFORE:
                        switch (flags.getLength())
                        {
                            case LONGER:
                                switch(flags.getType())
                                {
                                    case ALL:
                                        return tabelaDao.loadAllItemsWhereOvertimesAreLongerThanAndDateIsBeforeDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case DONE:
                                        return tabelaDao.loadDoneItemsWhereOvertimesAreLongerThanAndDateIsBeforeDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case TAKEN:
                                        return tabelaDao.loadTakenItemsWhereOvertimesAreLongerThanAndDateIsBeforeDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                }
                            case SHORTER:
                                switch(flags.getType())
                                {
                                    case ALL:
                                        return tabelaDao.loadAllItemsWhereOvertimesAreShorterThanAndDateIsBeforeDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case DONE:
                                        return tabelaDao.loadDoneItemsWhereOvertimesAreShorterThanAndDateIsBeforeDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case TAKEN:
                                        return tabelaDao.loadTakenItemsWhereOvertimesAreShorterThanAndDateIsBeforeDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                }
                            default:
                                return null;
                        }
                    case AFTER:
                        switch (flags.getLength())
                        {
                            case LONGER:
                                switch(flags.getType())
                                {
                                    case ALL:
                                        return tabelaDao.loadAllItemsWhereOvertimesAreLongerThanAndDateIsAfterDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case DONE:
                                        return tabelaDao.loadDoneItemsWhereOvertimesAreLongerThanAndDateIsAfterDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case TAKEN:
                                        return tabelaDao.loadTakenItemsWhereOvertimesAreLongerThanAndDateIsAfterDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                }
                            case SHORTER:
                                switch(flags.getType())
                                {
                                    case ALL:
                                        return tabelaDao.loadAllItemsWhereOvertimesAreShorterThanAndDateIsAfterDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case DONE:
                                        return tabelaDao.loadDoneItemsWhereOvertimesAreShorterThanAndDateIsAfterDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case TAKEN:
                                        return tabelaDao.loadTakenItemsWhereOvertimesAreShorterThanAndDateIsAfterDesc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                }
                            default:
                                return null;
                        }
                    default:
                        return null;
                }
            case ASC:
                switch (flags.getCutting())
                {
                    case BEFORE:
                        switch (flags.getLength())
                        {
                            case LONGER:
                                switch(flags.getType())
                                {
                                    case ALL:
                                        return tabelaDao.loadAllItemsWhereOvertimesAreLongerThanAndDateIsBeforeASC(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case DONE:
                                        return tabelaDao.loadDoneItemsWhereOvertimesAreLongerThanAndDateIsBeforeASC(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case TAKEN:
                                        return tabelaDao.loadTakenItemsWhereOvertimesAreLongerThanAndDateIsBeforeASC(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                }
                            case SHORTER:
                                switch(flags.getType())
                                {
                                    case ALL:
                                        return tabelaDao.loadAllItemsWhereOvertimesAreShorterThanAndDateIsBeforeASC(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case DONE:
                                        return tabelaDao.loadDoneItemsWhereOvertimesAreShorterThanAndDateIsBeforeASC(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case TAKEN:
                                        return tabelaDao.loadTakenItemsWhereOvertimesAreShorterThanAndDateIsBeforeASC(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                }
                            default:
                                return null;
                        }
                    case AFTER:
                        switch (flags.getLength())
                        {
                            case LONGER:
                                switch(flags.getType())
                                {
                                    case ALL:
                                        return tabelaDao.loadAllItemsWhereOvertimesAreLongerThanAndDateDateIsAfterAsc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case DONE:
                                        return tabelaDao.loadDoneItemsWhereOvertimesAreLongerThanAndDateDateIsAfterAsc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case TAKEN:
                                        return tabelaDao.loadTakenItemsWhereOvertimesAreLongerThanAndDateDateIsAfterAsc(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                }
                            case SHORTER:
                                switch(flags.getType())
                                {
                                    case ALL:
                                        return tabelaDao.loadAllItemsWhereOvertimesAreShorterThanAndDateIsAfterASC(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case DONE:
                                        return tabelaDao.loadDoneItemsWhereOvertimesAreShorterThanAndDateIsAfterASC(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                    case TAKEN:
                                        return tabelaDao.loadTakenItemsWhereOvertimesAreShorterThanAndDateIsAfterASC(chosenYear, chosenMonth, chosenDay, chosenHours, chosenMinutes);
                                }
                            default:
                                return null;
                        }
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Item, Void, Void>
    {
        private TabelaDao mAsyncTaskDao;
        insertAsyncTask(TabelaDao dao)
        {
            mAsyncTaskDao = dao;
        }

        /** używając następnie tego dao wykonujemy operację dodania elementu
         * ale robimy to asynchronicznie!!! */
        @Override
        protected Void doInBackground(final Item... items)
        {
            mAsyncTaskDao.insert(items[0]);
            return null;
        }
    }

    public void delete(Item item)
    {
        new deleteAsyncTask(tabelaDao).execute(item);
    }

    private static class deleteAsyncTask extends AsyncTask<Item, Void, Void>
    {
        private TabelaDao mAsyncTaskDao;

        private deleteAsyncTask(TabelaDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Item... items)
        {
            mAsyncTaskDao.deleteItem(items[0]);
            return null;
        }
    }

    /**
     * metoda
     */
    public int clearDatabase()
    {
        try
        {
            return new clearDatabaseAsyncTask(tabelaDao).execute().get();
        }
        catch (java.util.concurrent.ExecutionException |
                java.lang.InterruptedException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    private static class clearDatabaseAsyncTask extends AsyncTask<Void, Void, Integer>
    {
        private TabelaDao mAsyncTaskDao;
        clearDatabaseAsyncTask(TabelaDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(final Void... b)
        {
            return mAsyncTaskDao.clearDatabase();
        }
    }

    /**
     *
     * @param items
     */
    public void mergeDatabaseWithBuckupFile(List<Item> items)
    {
        new updateDatabaseAsyncTask(tabelaDao).execute(items);
    }

    private static class updateDatabaseAsyncTask extends AsyncTask<List<Item>, Void, Void>
    {
        private TabelaDao mAsyncTaskDao;
        updateDatabaseAsyncTask(TabelaDao dao)
        {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<Item>... lists)
        {
            for (Item item : lists[0])
            {
                mAsyncTaskDao.insert(item);
            }
            return null;
        }
    }

    public void updateDayOfOvertime(int newYear, int newMonth, int newDay, int dayOfWeek, int id)
    {
        new updateDateOfOvertimeAsyncTask(tabelaDao).execute(newYear, newMonth, newDay, dayOfWeek, id);
    }

    private static class updateDateOfOvertimeAsyncTask extends AsyncTask<Integer, Void, Void>
    {
        private TabelaDao tabelaDao;

        updateDateOfOvertimeAsyncTask(TabelaDao tab)
        {
            tabelaDao = tab;
        }

        @Override
        protected Void doInBackground(Integer... intTable)
        {
            tabelaDao.deleteItemeWhereOvertimeDateIs(intTable[0], intTable[1], intTable[2]);
            tabelaDao.updateDateOfOvertime(intTable[0], intTable[1], intTable[2], intTable[3], intTable[4]);
            return null;
        }
    }

    public void updateNumberOfMinutesAndHours(int hours, int minutes, int id)
    {
        new updateNumberOfMinutesAndHoursAsyncTask(tabelaDao).execute(hours, minutes, id);
    }

    private static class updateNumberOfMinutesAndHoursAsyncTask extends AsyncTask<Integer, Void, Void>
    {
        TabelaDao tabelaDao;

        updateNumberOfMinutesAndHoursAsyncTask(TabelaDao tabela)
        {
            tabelaDao = tabela;
        }

        @Override
        protected Void doInBackground(Integer... integers)
        {
            tabelaDao.updateNumberOfMinutesAndHours(integers[0],integers[1],integers[2]);
            return null;
        }
    }

    public void updateNote(String note, int id)
    {
        new updateNoteAsyncTask(tabelaDao, id).execute(note);
    }

    private static class updateNoteAsyncTask extends AsyncTask<String, Void , Void>
    {
        private TabelaDao tabelaDao;
        private int id;

        updateNoteAsyncTask(TabelaDao tabela, int id)
        {
            tabelaDao = tabela;
            this.id = id;
        }

        @Override
        protected Void doInBackground(String... notes)
        {
            tabelaDao.updateNote(notes[0], id);
            return null;
        }
    }

    public int numberOfMatchingItems(int year, int month, int day)
    {
        try
        {
            return new numberOfMatchingItemsAsyncTask(tabelaDao).execute(year, month, day).get();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
            return -1;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    private static class numberOfMatchingItemsAsyncTask extends AsyncTask<Integer, Void , Integer>
    {
        private TabelaDao tabelaDao;

        numberOfMatchingItemsAsyncTask(TabelaDao tabela)
        {
            tabelaDao = tabela;
        }

        @Override
        protected Integer doInBackground(Integer... integers)
        {
            return tabelaDao.numberOfMatchingItems(integers[0], integers[1], integers[2]);
        }
    }

    public int getUIdFromDate(int year, int month, int day)
    {
        try
        {
            return new getUIdFromDateAsyncTask(tabelaDao).execute(year, month, day).get();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
            return -1;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    private static class getUIdFromDateAsyncTask extends AsyncTask<Integer, Void , Integer>
    {
        private TabelaDao tabelaDao;

        getUIdFromDateAsyncTask(TabelaDao tabela)
        {
            tabelaDao = tabela;
        }

        @Override
        protected Integer doInBackground(Integer... integers)
        {
            return tabelaDao.getUIdFromDate(integers[0], integers[1], integers[2]);
        }
    }
}



























