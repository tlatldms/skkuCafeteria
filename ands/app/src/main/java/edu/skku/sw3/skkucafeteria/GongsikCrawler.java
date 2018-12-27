package edu.skku.sw3.skkucafeteria;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;

public class GongsikCrawler extends Crawler {
    public GongsikCrawler(IJobCompleteListener activity, Calendar date)
    {
        super(activity, date);
    }

    @Override
    protected String convertDateToURL(Calendar date)
    {
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("http://www.skku.edu/new_home/campus/support/pop_menu1.jsp?restId=203&startDate=");

        strBuilder.append(date.get(Calendar.YEAR));

        if (date.get(Calendar.MONTH) + 1 < 10)
        {
            strBuilder.append("0");
        }
        strBuilder.append(date.get(Calendar.MONTH) + 1);

        if(date.get(Calendar.DAY_OF_MONTH) < 10)
        {
            strBuilder.append("0");
        }
        strBuilder.append(date.get(Calendar.DAY_OF_MONTH));

        return strBuilder.toString();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            Document doc = Jsoup.connect(url).get();

            Elements menu = doc.select("h4.bul_01");

            int i = 0;
            for(Element e : menu)
            {
                String x = e.text().trim();

                int index_of_bracket_start = x.lastIndexOf("(");
                int index_of_price_start = x.lastIndexOf(": ");
                int index_of_bracket_end = x.lastIndexOf(")");

                if (index_of_bracket_start <= 0)
                {
                    mMenu.setBreakfastNull();
                    mMenu.setLunchNull();
                    mMenu.setDinnerNull();

                    break;
                }

                String[] names = new String[] {x.substring(0, index_of_bracket_start)};
                int price = Integer.parseInt(x.substring(index_of_price_start + 1, index_of_bracket_end).trim());

                if (i >= 0 && i < 3)
                {
                    mMenu.addMenuItemToBreakfast(new MenuItem(names, price));
                }
                else if (i >= 3 && i < 8)
                {
                    mMenu.addMenuItemToLunch(new MenuItem(names, price));
                }
                else
                {
                    mMenu.addMenuItemToDinner(new MenuItem(names, price));
                }

                i++;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            mMenu.setBreakfastNull();
            mMenu.setLunchNull();
            mMenu.setDinnerNull();
        }

        return null;
    }
}
