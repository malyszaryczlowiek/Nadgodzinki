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

    @Query("SELECT * FROM tabela ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadAllItems();

    //*******************************
    // Sorting queries

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadAllItemsWhereOvertimesAreLongerThanAndDateIsAfterDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) AND (Hours + Minutes) > 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadDoneItemsWhereOvertimesAreLongerThanAndDateIsAfterDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) AND (Hours + Minutes) < 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadTakenItemsWhereOvertimesAreLongerThanAndDateIsAfterDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);




    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadAllItemsWhereOvertimesAreLongerThanAndDateDateIsAfterAsc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) AND (Hours + Minutes) > 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadDoneItemsWhereOvertimesAreLongerThanAndDateDateIsAfterAsc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) AND (Hours + Minutes) < 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadTakenItemsWhereOvertimesAreLongerThanAndDateDateIsAfterAsc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);




    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC , MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadAllItemsWhereOvertimesAreLongerThanAndDateIsBeforeDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) AND (Hours + Minutes) > 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadDoneItemsWhereOvertimesAreLongerThanAndDateIsBeforeDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) AND (Hours + Minutes) < 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadTakenItemsWhereOvertimesAreLongerThanAndDateIsBeforeDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);




    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadAllItemsWhereOvertimesAreLongerThanAndDateIsBeforeASC(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) AND (Hours + Minutes) > 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadDoneItemsWhereOvertimesAreLongerThanAndDateIsBeforeASC(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) > ABS(:hours * 60 + :minutes) AND (Hours + Minutes) < 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadTakenItemsWhereOvertimesAreLongerThanAndDateIsBeforeASC(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);




    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadAllItemsWhereOvertimesAreShorterThanAndDateIsAfterDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) AND (Hours + Minutes) > 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadDoneItemsWhereOvertimesAreShorterThanAndDateIsAfterDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) AND (Hours + Minutes) < 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadTakenItemsWhereOvertimesAreShorterThanAndDateIsAfterDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);




    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadAllItemsWhereOvertimesAreShorterThanAndDateIsAfterASC(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes)  AND (Hours + Minutes) > 0 "+
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadDoneItemsWhereOvertimesAreShorterThanAndDateIsAfterASC(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) AND (Hours + Minutes) < 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "> ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadTakenItemsWhereOvertimesAreShorterThanAndDateIsAfterASC(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);





    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadAllItemsWhereOvertimesAreShorterThanAndDateIsBeforeDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) AND (Hours + Minutes) > 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadDoneItemsWhereOvertimesAreShorterThanAndDateIsBeforeDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) AND (Hours + Minutes) < 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> loadTakenItemsWhereOvertimesAreShorterThanAndDateIsBeforeDesc(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);





    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadAllItemsWhereOvertimesAreShorterThanAndDateIsBeforeASC(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) AND (Hours + Minutes) > 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadDoneItemsWhereOvertimesAreShorterThanAndDateIsBeforeASC(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);

    @Query("SELECT * FROM tabela " +
            "WHERE ABS(Hours * 60 + Minutes) < ABS(:hours * 60 + :minutes) AND (Hours + Minutes) > 0 " +
            "AND ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            "<= ( :yearOfOvertime * 10000 + :monthOfOvertime * 100 + :dayOfOvertime)" +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    LiveData<List<Item>> loadTakenItemsWhereOvertimesAreShorterThanAndDateIsBeforeASC(
            int yearOfOvertime, int monthOfOvertime, int dayOfOvertime, int hours, int minutes);



    //************************
    // returning items with matching regex note query

    @Query("SELECT * FROM tabela WHERE Note LIKE :query " +
            "ORDER BY YearOfOvertime DESC, MonthOfOvertime DESC, DayOfOvertime DESC")
    LiveData<List<Item>> getMatchingNoteQuery(String query);



    //*******************
    // Deleting Queries

    @Query("DELETE FROM tabela")
    int clearDatabase();

    @Delete
    void deleteItem(Item item);

    @Query("DELETE FROM tabela WHERE YearOfOvertime = :year AND MonthOfOvertime = :month " +
            "AND DayOfOvertime = :day" )
    void deleteItemeWhereOvertimeDateIs(int year, int month, int day);




    //************************
    // updating queries

    @Query("UPDATE tabela SET YearOfOvertime = :newYear, MonthOfOvertime = :newMonth, " +
            "DayOfOvertime = :newDay, Day = :dayOfWeek WHERE uid = :id")
    void updateDateOfOvertime(int newYear, int newMonth, int newDay, int dayOfWeek, int id);

    @Query("UPDATE tabela SET Minutes = :minutes, Hours = :hours WHERE uid = :id")
    void updateNumberOfMinutesAndHours(int hours, int minutes, int id);

    @Query("UPDATE tabela SET Note = :note WHERE uid = :id")
    void updateNote(String note, int id);



    //************************
    // return number of matching items

    @Query("SELECT COUNT(*) FROM tabela WHERE YearOfOvertime = :year AND MonthOfOvertime = :month " +
            "AND DayOfOvertime = :day")
    int numberOfMatchingItems(int year, int month, int day);


    @Query("SELECT uid FROM tabela WHERE YearOfOvertime = :year AND MonthOfOvertime = :month " +
            "AND DayOfOvertime = :day")
    int getUIdFromDate(int year, int month, int day);



    //************************
    // return number of minutes and hours

    @Query("SELECT SUM(Minutes) + 60 * SUM(Hours) FROM tabela " +
            "WHERE ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            " > ( :year * 10000 + :month * 100 + :day)" +
            " AND ( Hours + Minutes ) > 0")
    int numberOfMinutesDone(int year, int month, int day);

    @Query("SELECT ABS(SUM(Minutes)) + 60 * ABS(SUM(Hours)) FROM tabela " +
            "WHERE ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            " > ( :year * 10000 + :month * 100 + :day)" +
            " AND ( Hours + Minutes ) < 0")
    int numberOfMinutesTaken(int year, int month, int day);

    @Query("SELECT SUM(Minutes) + 60 * SUM(Hours) FROM tabela ")
    int allNumberOfMinutesToTake();



    //************************
    // return number of minutes and hours
    @Query("SELECT * FROM tabela " +
            "WHERE ( YearOfOvertime * 10000 + MonthOfOvertime * 100 + DayOfOvertime) " +
            ">= ( :year * 10000 + :month * 100 + :day) " +
            "ORDER BY YearOfOvertime ASC, MonthOfOvertime ASC, DayOfOvertime ASC")
    List<Item> listOfItemsSince(int year, int month, int day);
}


// jeśli zamiast zwykłej List<Item> użyjemy LiveData<List<Item>> to będziemy mogli
// operując na takiej bazie danych, wprowadzać bezpośrednio w niej zmiany.
// sprawdzić czy nie będzie trzeba czasami dodać jeszcze: modułów do LiveData<> i ViewModel
// bo puki co ładuje się bez nich. Link do dodania znajduje się poniżej:
// https://developer.android.com/topic/libraries/architecture/adding-components#lifecycle
// trzeba to wkleić do w build.gradle (module app) .
