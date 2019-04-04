package com.example.sudouser.nadgodzinki.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sudouser.nadgodzinki.Dialogs.SearchHelpers.SearchFlags;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Repository
{
    // obiekt TabelaDao tak jak w klasie BazaDanych.
    private TabelaDao tabelaDao;
    private LiveData<List<Item>> allItems;

    public Repository(Application application)
    {
        /* inicjalizujemy obiekt bazy danych ()tworzymy bazę danych*/
        BazaDanych db = BazaDanych.getDatabase(application);

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
     * @param chosenDate
     * @param chosenHours
     * @param chosenMinutes
     * @param flags
     * @return
     */
    @NonNull
    public LiveData<List<Item>> loadItemsWhere(String chosenDate, int chosenHours, int chosenMinutes, SearchFlags flags)
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
                                return tabelaDao.loadItemsWhereOvertimesAreLongerThanAndDateIsBeforeDesc(chosenHours, chosenMinutes, chosenDate);
                            case SHORTER:
                                return tabelaDao.loadItemsWhereOvertimesAreShorterThanAndDateIsBeforeDesc(chosenHours, chosenMinutes, chosenDate);
                            default:
                                return null;
                        }
                    case AFTER:
                        switch (flags.getLength())
                        {
                            case LONGER:
                                return tabelaDao.loadItemsWhereOvertimesAreLongerThanAndDateIsAfterDesc(chosenHours, chosenMinutes, chosenDate);
                            case SHORTER:
                                return tabelaDao.loadItemsWhereOvertimesAreShorterThanAndDateIsAfterDesc(chosenHours, chosenMinutes, chosenDate);
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
                                return tabelaDao.loadItemsWhereOvertimesAreLongerThanAndDateIsBeforeASC(chosenHours, chosenMinutes, chosenDate);
                            case SHORTER:
                                return tabelaDao.loadItemsWhereOvertimesAreShorterThanAndDateIsBeforeASC(chosenHours, chosenMinutes, chosenDate);
                            default:
                                return null;
                        }
                    case AFTER:
                        switch (flags.getLength())
                        {
                            case LONGER:
                                return tabelaDao.loadItemsWhereOvertimesAreLongerThanAndDateDateIsAfterAsc(chosenHours, chosenMinutes, chosenDate);
                            case SHORTER:
                                return tabelaDao.loadItemsWhereOvertimesAreShorterThanAndDateIsAfterASC(chosenHours, chosenMinutes, chosenDate);
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

    public void updateDayOfOvertime(String dayOfOvertime, int day, int id)
    {
        new updateDateOfOvertimeAsyncTask(tabelaDao, day, id).execute(dayOfOvertime);
    }

    private static class updateDateOfOvertimeAsyncTask extends AsyncTask<String, Void, Void>
    {
        private TabelaDao tabelaDao;
        private int id;
        private int day;

        updateDateOfOvertimeAsyncTask(TabelaDao tab, int day, int id)
        {
            tabelaDao = tab;
            this.day = day;
            this.id = id;
        }

        @Override
        protected Void doInBackground(String... strings)
        {
            tabelaDao.updateDateOfOvertime(strings[0], day, id);
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
}



























