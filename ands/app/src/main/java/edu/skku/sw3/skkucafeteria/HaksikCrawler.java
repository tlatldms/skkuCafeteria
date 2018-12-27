package edu.skku.sw3.skkucafeteria;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;

public class HaksikCrawler extends Crawler
{
    public HaksikCrawler(IJobCompleteListener activity, Calendar date)
    {
        super(activity, date);
    }

    @Override
    protected String convertDateToURL(Calendar date)
    {
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("http://www.skku.edu/new_home/campus/support/pop_menu1.jsp?restId=202&startDate=");

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
    protected Void doInBackground(Void... params) {
        try
        {
            Document doc = Jsoup.connect(url).get();

            Elements menu = doc.select("p.menu");

            int i = 0;

            for(Element e : menu)
            {
                if(i == 0)
                {
                    String[] names = e.text().trim().split(" ");

                    for(String x : names)
                    {
                        if (x.equals("운영없음"))
                        {
                            throw new IOException();
                        }
                    }

                    mMenu.addMenuItemToBreakfast(new MenuItem(names, 2500));
                    i++;
                }
                else
                {
                    String[] split = e.text().trim().split(" ");

                    for(String x : split)
                    {
                        int index_of_bracket_start = x.indexOf("[");
                        int index_of_bracket_end = x.indexOf("]");

                        if (index_of_bracket_start == -1)
                        {
                            continue;
                        }

                        String[] names = new String[1];
                        names[0] = x.substring(0, index_of_bracket_start);
                        int price = Integer.parseInt(x.substring(index_of_bracket_start + 1, index_of_bracket_end));

                        mMenu.addMenuItemToLunch(new MenuItem(names, price));
                    }
                }
            }

            mMenu.setDinnerNull();
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

