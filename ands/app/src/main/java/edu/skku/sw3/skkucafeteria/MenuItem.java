package edu.skku.sw3.skkucafeteria;

public class MenuItem {
    private String[] names;
    private int price;
    private boolean availability;

    public MenuItem(String[] names)
    {
        this(names, 0);
    }

    public MenuItem(String[] names, int price)
    {
        this.names = names;
        this.price = price;
        availability = true;
    }

    public String[] getNames()
    {
        return names;
    }

    public String getNamesInStr(String divider)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < names.length - 1; i++)
        {
            stringBuilder.append(names[i]);
            stringBuilder.append(divider);
        }

        stringBuilder.append(names[names.length - 1]);

        return stringBuilder.toString();
    }

    public int getPrice()
    {
        return price;
    }

    public boolean getAvailability()
    {
        return availability;
    }

    public void setAvailabilityFalse()
    {
        availability = false;
    }
}
