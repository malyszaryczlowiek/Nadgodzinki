package com.example.sudouser.nadgodzinki.Dialogs.SearchHelpers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SearchFlags
{
    private Sorting sorting;
    private Cutting cutting;
    private Length length;

    public enum Sorting {ASC, DSC};
    public enum Cutting {BEFORE, AFTER};
    public enum Length {LONGER, SHORTER};

    private SearchFlags()
    {
        sorting = Sorting.DSC;
        cutting = Cutting.BEFORE;
        length = Length.LONGER;
    }

    @NotNull
    @Contract(" -> new")
    public static SearchFlags createSearchFlagsObject()
    {
        return new SearchFlags();
    }

    public Sorting getSorting()
    {
        return sorting;
    }

    public Cutting getCutting()
    {
        return cutting;
    }

    public Length getLength()
    {
        return length;
    }

    public void setSorting(Sorting sorting)
    {
        this.sorting = sorting;
    }

    public void setCutting(Cutting cutting)
    {
        this.cutting = cutting;
    }

    public void setLength(Length length)
    {
        this.length = length;
    }
}



























