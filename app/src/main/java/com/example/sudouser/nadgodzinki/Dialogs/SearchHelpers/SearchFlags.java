package com.example.sudouser.nadgodzinki.Dialogs.SearchHelpers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SearchFlags
{
    private Sorting sorting;
    private Cutting cutting;
    private Length length;
    private Type type;

    public enum Sorting {ASC, DSC};
    public enum Cutting {BEFORE, AFTER};
    public enum Length {LONGER, SHORTER};
    public enum Type {ALL, DONE, TAKEN};

    private SearchFlags()
    {
        sorting = Sorting.DSC;
        cutting = Cutting.BEFORE;
        length = Length.LONGER;
        type = Type.ALL;
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

    public Type getType()
    {
        return type;
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

    public void setType(Type type)
    {
        this.type = type;
    }
}



























