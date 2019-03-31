package com.example.sudouser.nadgodzinki;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.sudouser.nadgodzinki.db.BazaDanych;
import com.example.sudouser.nadgodzinki.db.Item;
import com.example.sudouser.nadgodzinki.db.TabelaDao;

import java.util.List;

public class Repository
{
    // obiekt TabelaDao tak jak w klasie BazaDanych.
    private TabelaDao tabelaDao;
    private LiveData<List<Item>> allItems;

    Repository(Application application)
    {
        /* inicjalizujemy obiekt bazy danych ()tworzymy bazę danych*/
        BazaDanych db = BazaDanych.getDatabase(application);

        /* przypisujemy do wywoływań bazę danych */
        tabelaDao = db.tabelaDao();

        /* pobieramy z bazy danych wszystkie itemy jakie tylko tam są */
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
            //mAsyncTaskDao.deleteItem(items[0]);
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
}

















