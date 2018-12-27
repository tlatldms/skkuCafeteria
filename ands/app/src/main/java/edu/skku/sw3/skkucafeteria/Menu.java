package edu.skku.sw3.skkucafeteria;

import java.io.Serializable;
import java.util.ArrayList;

public class Menu implements Serializable{
    private ArrayList<MenuItem> mBreakfast;
    private ArrayList<MenuItem> mLunch;
    private ArrayList<MenuItem> mDinner;

    public void clearAll()
    {
        setBreakfastNull();
        setLunchNull();
        setDinnerNull();
    }

    public void addMenuItemToBreakfast(MenuItem newItem)
    {
        if (mBreakfast == null)
        {
            mBreakfast = new ArrayList<>();
        }
        mBreakfast.add(newItem);
    }

    public void addMenuItemToLunch(MenuItem newItem)
    {
        if (mLunch == null)
        {
            mLunch = new ArrayList<>();
        }
        mLunch.add(newItem);
    }

    public void addMenuItemToDinner(MenuItem newItem)
    {
        if (mDinner == null)
        {
            mDinner = new ArrayList<>();
        }
        mDinner.add(newItem);
    }

    public ArrayList<MenuItem> getBreakfast() {
        return mBreakfast;
    }

    public ArrayList<MenuItem> getLunch() {
        return mLunch;
    }

    public ArrayList<MenuItem> getDinner() {
        return mDinner;
    }

    public void setBreakfastNull()
    {
        mBreakfast = null;
    }

    public void setLunchNull()
    {
        mLunch = null;
    }

    public void setDinnerNull()
    {
        mDinner = null;
    }
}
