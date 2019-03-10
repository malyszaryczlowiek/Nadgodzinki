package com.example.sudouser.nadgodzinki.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * klasa BazaDanych jest holderem i to ona zapewnia bezpośredni dostęp do danych w bazie
 * danych.
 */
@Database(entities = Item.class, version = 1)
public abstract class BazaDanych extends RoomDatabase
{
    public abstract TabelaDao tabelaDao();

    public static volatile BazaDanych INSTANCE;

    // tworzymy jeszcze singletone tak aby nie można było
    // utworzyć kilku egzemplarzy tego samego obiektu
    public static BazaDanych getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            /** wywołujemy w innym wątku niż ma to miejsce w wątku activity*/
            synchronized (BazaDanych.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BazaDanych.class, "Baza_Danych")//addCallback(sRoomDatabaseCallback).
                            .build(); // baza danych to nazwa pliku
                }
            }
        }
        return INSTANCE;
    }

    /*
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // If you want to keep the data through app restarts,
            // comment out the following line.

        }
    };
     */
}
