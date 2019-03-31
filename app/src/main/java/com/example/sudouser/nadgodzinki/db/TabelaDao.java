package com.example.sudouser.nadgodzinki.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

/**
 * Dao - Data Access object - obiekt dostępu do danych
 *
 * interface, który zawiera zapytania do bazy danych.
 * ten interface zawiera wszelkie wyszukiwania jakie będzie sie wykonywac na bazie danych
 *
 * zgodnie z https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#4
 * TODO UWAGA!!! każdy query powinno być wywołane w innym wątku
 */
@Dao
public interface TabelaDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Item item);

    @Query("SELECT * FROM tabela ORDER BY DateOfOvertime DESC")
    LiveData<List<Item>> loadAllItems();

    //*******************************
    // Sorting queries

    ///*
    @Query("SELECT * FROM tabela WHERE Hours> :hours AND Minutes > :minutes AND DateOfOvertime > :dateOfOvertime ORDER BY DateOfOvertime DESC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreBiggerThanAndDateIsLaterDesc(int hours, int minutes, String dateOfOvertime);

    @Query("SELECT * FROM tabela WHERE Hours > :hours AND Minutes > :minutes AND DateOfOvertime > :dateOfOvertime ORDER BY DateOfOvertime ASC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreBiggerThanAndDateDateIsLaterAsc(int hours, int minutes, String dateOfOvertime);

    @Query("SELECT * FROM tabela WHERE Hours > :hours AND Minutes > :minutes AND DateOfOvertime <= :dateOfOvertime ORDER BY DateOfOvertime DESC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreBiggerThanAndDateIsEarlierDesc(int hours, int minutes, String dateOfOvertime);

    @Query("SELECT * FROM tabela WHERE Hours > :hours AND Minutes > :minutes AND DateOfOvertime <= :dateOfOvertime ORDER BY DateOfOvertime ASC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreBiggerThanAndDateIsEarlierASC(int hours, int minutes, String dateOfOvertime);

    @Query("SELECT * FROM tabela WHERE Hours < :hours AND Minutes < :minutes AND DateOfOvertime > :dateOfOvertime ORDER BY DateOfOvertime DESC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreLowerThanAndDateIsLaterDesc(int hours, int minutes, String dateOfOvertime);

    @Query("SELECT * FROM tabela WHERE Hours < :hours AND Minutes < :minutes AND DateOfOvertime > :dateOfOvertime ORDER BY DateOfOvertime ASC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreLowerThanAndDateIsLaterASC(int hours, int minutes, String dateOfOvertime);

    @Query("SELECT * FROM tabela WHERE Hours < :hours AND Minutes < :minutes AND DateOfOvertime <= :dateOfOvertime ORDER BY DateOfOvertime DESC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreLowerThanAndDateIsEarlierDesc(int hours, int minutes, String dateOfOvertime);

    @Query("SELECT * FROM tabela WHERE Hours < :hours AND Minutes < :minutes AND DateOfOvertime <= :dateOfOvertime ORDER BY DateOfOvertime ASC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreLowerThanAndDateIsEarlierASC(int hours, int minutes, String dateOfOvertime);
     //*/


    //*******************
    // Deleting Queries

    @Query("DELETE FROM tabela")
    int clearDatabase();

    //@Delete
    //void deleteItem(Item item);

    // TODO zmień ilość minut, godzin i datę.
    // ***************************
    // changing Queries


    /*
    @Query("SELECT * FROM tabela  WHERE Minutes > :minutes AND Hours > :hours ORDER BY DateOfOvertime ASC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreBiggerThanAndDateAsc(int minutes, int hours);

    @Query("SELECT * FROM tabela WHERE Minutes < :minutes AND Hours < :hours ORDER BY DateOfOvertime DESC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreLowerThanAndDateDesc(int minutes, int hours);

    @Query("SELECT * FROM tabela WHERE Minutes < :minutes AND Hours < :hours ORDER BY DateOfOvertime ASC")
    LiveData<List<Item>> loadItemsWhereOvertimesAreLowerThanAndDateAsc(int minutes, int hours);
     */

    // jeśli zamiast zwykłej List<Item> użyjemy LiveData<List<Item>> to będziemy mogli
    // operując na takiej bazie danych, wprowadzać bezpośrednio w niej zmiany.
    // sprawdzić czy nie będzie trzeba czasami dodać jeszcze: modułów do LiveData<> i ViewModel
    // bo puki co ładuje się bez nich. Link do dodania znajduje się poniżej:
    // https://developer.android.com/topic/libraries/architecture/adding-components#lifecycle
    // trzeba to wkleić do w build.gradle (module app) .
}

