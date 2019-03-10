package com.example.sudouser.nadgodzinki.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
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
    // jeśli na przykłąd używamy @Query to jest to o tyle dobrze, że poprawność zapytania jest sprawdzana
    // już na poziomie kompilacji przez co dostaniemy najwyżej compile error a nie runtime error.

    @Insert//(onConflict = OnConflictStrategy.REPLACE))
    void insert(Item item);

    @Query("SELECT * FROM tabela ORDER BY DateOfOvertime")
    List<Item> selectAllItemsOrderByDateOfOvertime();

    @Query("SELECT * FROM tabela")
    LiveData<List<Item>> selectAllItems();

    @Query("SELECT * FROM tabela WHERE Minutes > :minutes")
    LiveData<List<Item>> loadAllDaysWhereHoursAreBiggerThan(int minutes);

    @Query("DELETE FROM tabela")
    int clearDatabase();

    //@Query("SELECT * FROM tabela WHERE MAX(uid)")
    //LiveData<Item> deleteLastItem();



    /**
     ponieważ typ zwracany jest jako int to wartość ta odpowiada ilości usuniętych dnaych z bazy danych,
     podejrzewam, że jeśli będzie zeor to znaczy, że żaden item nie został usunięty.
     */
    //@Delete
    //int deleteItem();

    /**
     * przykład z wykorzystaniem parametrów
     * załąduj tabelę z gdzie byłem w pracy dłużej niż :minutes minut
     */
    //@Query("SELECT * FROM tabela WHERE Minutes > :minutes")
    //List<Item> loadAllDaysHourBiggerThan(int minutes);

    // jeśli zamiast zwykłej List<Item> użyjemy LiveData<List<Item>> to będziemy mogli
    // operując na takiej bazie danych, wprowadzać bezpośrednio w niej zmiany.
    // sprawdzić czy nie będzie trzeba czasami dodać jeszcze: modułów do LiveData<> i ViewModel
    // bo puki co ładuje się bez nich. Link do dodania znajduje się poniżej:
    // https://developer.android.com/topic/libraries/architecture/adding-components#lifecycle
    // trzeba to wkleić do w build.gradle (module app) .
    //@Query("SELECT * FROM tabela WHERE Minutes > :minutes")
    //LiveData<List<Item>> loadSomething(int minutes);
}

// TODO pododawać dodatkowe query aby dało się
// 1. usunąć ostatni wpis
// 2. dodać item z ujemnymi wartościami godzin i mitu co oznacza, że właśnie odebraliśmu sobie nadgodzinę
