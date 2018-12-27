package edu.skku.sw3.skkucafeteria;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;

public abstract class Crawler extends AsyncTask<Void, Void, Void>
{
    public interface IJobCompleteListener
    {
        void crawlingCompleted(Crawler from, Menu menu);
    }

    protected IJobCompleteListener jobCompleteListener;

    protected Menu mMenu;
    protected String url;


    protected Crawler(@Nullable IJobCompleteListener activity, Calendar date)
    {
        this.mMenu = new Menu();

        if(activity != null)
        {
            jobCompleteListener = (IJobCompleteListener) activity;
        }
        else
        {
            jobCompleteListener = null;
        }

        url = convertDateToURL(date);
    }

    protected abstract String convertDateToURL(Calendar date);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mMenu.clearAll();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(jobCompleteListener != null)
        {
            try { jobCompleteListener.crawlingCompleted(this, mMenu); }
            catch (NullPointerException e) {Log.d("Crawler", "NULLPointerException");}
        }
    }
}

