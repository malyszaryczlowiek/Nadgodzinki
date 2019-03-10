package com.example.sudouser.nadgodzinki.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * W mojej opini jest to pojedyńczy rekord z bazydanych z danej tabeli.
 *
 * klasa Item jako, że zawiera specyfikator @Entity jest czymś w rodzaju tabeli,
 * która będzie zdefiniowana w bazie danych do której będzie można uzsykać dostęp
 * urzywając klasy BazaDanych z tego pakietu,
 */
@Entity(tableName = "tabela")
public class Item
{
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "DateOfItemAddition")
    private String dateOfItemAddition;

    @ColumnInfo(name = "DateOfOvertime")
    private String dateOfOvertime;

    @ColumnInfo(name = "Hours")
    private int numberOfHours;

    @ColumnInfo(name = "Minutes")
    private int numberOfMinutes;

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

    public Item(int uid, String dateOfItemAddition, String dateOfOvertime,
         int numberOfHours, int numberOfMinutes)
    {
        this.uid                = uid;
        this.dateOfItemAddition = dateOfItemAddition;
        this.dateOfOvertime     = dateOfOvertime;
        this.numberOfHours      = numberOfHours;
        this.numberOfMinutes    = numberOfMinutes;
    }

    @Override
    public String toString()
    {
        return getUid() + " | " + getDateOfOvertime() + " | h: " + getNumberOfHours() + " | min: " + getNumberOfMinutes();
    }
}
