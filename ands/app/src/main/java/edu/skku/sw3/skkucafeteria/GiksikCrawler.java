package edu.skku.sw3.skkucafeteria;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;

public class GiksikCrawler extends Crawler {

    public GiksikCrawler(IJobCompleteListener activity, Calendar date){
        super(activity, date);
    }

    @Override
    protected String convertDateToURL(Calendar date)
    {
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("https://dorm.skku.edu/_custom/skku/_common/board/schedule_menu/food_menu_page.jsp?day=");

        strBuilder.append(date.get(Calendar.DAY_OF_MONTH));

        strBuilder.append("&month=");

        strBuilder.append(date.get(Calendar.MONTH) + 1);

        strBuilder.append("&year=");

        strBuilder.append(date.get(Calendar.YEAR));

        strBuilder.append("&board_no=61");

        return strBuilder.toString();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try
        {
            int price = 3600;

            Document doc = Jsoup.connect(url).get();

            Elements bf = doc.select("div#foodlist01 ul li p");

            for (Element e : bf)
            {
                String[] names = e.text().trim().split(",");

                if (names[0].equals("운영없음"))
                {
                    continue;
                }

                mMenu.addMenuItemToBreakfast(new MenuItem(names, price));
            }

            Elements lu = doc.select("div#foodlist02 ul li p");

            for (Element e : lu)
            {
                String[] names = e.text().trim().split(",");

                if (names[0].equals("운영없음"))
                {
                    continue;
                }

                mMenu.addMenuItemToLunch(new MenuItem(names, price));
            }

            Elements di = doc.select("div#foodlist03 ul li p");

            for (Element e : di)
            {
                String[] names = e.text().trim().split(",");

                if (names[0].equals("운영없음"))
                {
                    continue;
                }

                mMenu.addMenuItemToDinner(new MenuItem(names, price));
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