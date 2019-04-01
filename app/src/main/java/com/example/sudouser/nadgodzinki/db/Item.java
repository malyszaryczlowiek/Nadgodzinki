package com.example.sudouser.nadgodzinki.db;

import java.time.LocalDate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * klasa Item jako, że zawiera specyfikator @Entity jest czymś w rodzaju tabeli,
 * która będzie zdefiniowana w bazie danych do której będzie można uzsykać dostęp
 * używając klasy BazaDanych z tego pakietu,
 */
@Entity(tableName = "tabela", indices = {@Index(value = {"DateOfOvertime"}, unique = true)})
public class Item
{
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "DateOfItemAddition")
    private String dateOfItemAddition;

    @ColumnInfo(name = "Day") // Day of week has values in 1 to 7 range. See: java.time.DayOfWeek public int getValue()
    private int dayOfWeek;

    @ColumnInfo(name = "DateOfOvertime")
    private String dateOfOvertime;

    @ColumnInfo(name = "Hours")
    private int numberOfHours;

    @ColumnInfo(name = "Minutes")
    private int numberOfMinutes;

    @ColumnInfo(name = "Note")
    private String note;

    //@ColumnInfo(name = "test")
    //private LocalDate test;

    public void setUid(int i) { uid = i;}

    public void setDateOfItemAddition(String dateOfItemAddition)
    {
        this.dateOfItemAddition = dateOfItemAddition;
    }

    public void setDateOfOvertime(String dateOfOvertime)
    {
        this.dateOfOvertime = dateOfOvertime;
    }

    public void setNumberOfHours(int hours)
    {
        this.numberOfHours = hours;
    }

    public void setNumberOfMinutes(int minutes)
    {
        this.numberOfMinutes = minutes;
    }

    public int getUid()
    {
        return uid;
    }

    public String getDateOfItemAddition()
    {
        return dateOfItemAddition;
    }

    public int getDayOfWeek()
    {
        return dayOfWeek;
    }

    public String getDateOfOvertime()
    {
        return dateOfOvertime;
    }

    public int getNumberOfHours()
    {
        return numberOfHours;
    }

    public int getNumberOfMinutes()
    {
        return numberOfMinutes;
    }

    public String getNote()
    {
        return note;
    }

    public Item(int uid, String dateOfItemAddition, int dayOfWeek, String dateOfOvertime,
         int numberOfHours, int numberOfMinutes, String note)
    {
        this.uid                = uid;
        this.dateOfItemAddition = dateOfItemAddition;
        this.dayOfWeek          = dayOfWeek;
        this.dateOfOvertime     = dateOfOvertime;
        this.numberOfHours      = numberOfHours;
        this.numberOfMinutes    = numberOfMinutes;
        this.note                = note;
    }

    @Override
    public String toString()
    {
        return getUid() + " | " + getDayOfWeek() + " | " + getDateOfOvertime() + " | h: "
                + getNumberOfHours() + " | min: " + getNumberOfMinutes();
    }
}
