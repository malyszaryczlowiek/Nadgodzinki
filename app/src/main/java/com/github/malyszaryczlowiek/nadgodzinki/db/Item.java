package com.github.malyszaryczlowiek.nadgodzinki.db;

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
@Entity(tableName = "tabela", indices = {@Index(value = {"YearOfOvertime", "MonthOfOvertime", "DayOfOvertime"}, unique = true)})
public class Item
{
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "DateOfItemAddition")
    private String dateOfItemAddition;

    @ColumnInfo(name = "Day") // Day of week has values in 1 to 7 range.
    private int dayOfWeek;

    @ColumnInfo(name = "DayOfOvertime")
    private int dayOfOvertime;

    @ColumnInfo(name = "MonthOfOvertime")
    private int monthOfOvertime;

    @ColumnInfo(name = "YearOfOvertime")
    private int yearOfOvertime;

    @ColumnInfo(name = "Hours")
    private int numberOfHours;

    @ColumnInfo(name = "Minutes")
    private int numberOfMinutes;

    @ColumnInfo(name = "Note")
    private String note;

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

    public int getDayOfOvertime()
    {
        return dayOfOvertime;
    }

    public int getMonthOfOvertime()
    {
        return monthOfOvertime;
    }

    public int getYearOfOvertime()
    {
        return yearOfOvertime;
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

    public Item(int uid, String dateOfItemAddition, int dayOfWeek,  int yearOfOvertime,
                int monthOfOvertime, int dayOfOvertime, int numberOfHours, int numberOfMinutes,
                String note)
    {
        this.uid                = uid;
        this.dateOfItemAddition = dateOfItemAddition;
        this.dayOfWeek          = dayOfWeek;
        this.dayOfOvertime      = dayOfOvertime;
        this.monthOfOvertime    = monthOfOvertime;
        this.yearOfOvertime     = yearOfOvertime;
        this.numberOfHours      = numberOfHours;
        this.numberOfMinutes    = numberOfMinutes;
        this.note               = note;
    }

    @Override
    public String toString()
    {
        return getUid() + "" ;
    }
}
