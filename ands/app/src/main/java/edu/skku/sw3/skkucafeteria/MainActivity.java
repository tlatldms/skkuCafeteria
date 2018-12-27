package edu.skku.sw3.skkucafeteria;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements Fragment_DateIndicator.ISetDateListener, Crawler.IJobCompleteListener{

    private Calendar crawlingDate;

    private Fragment mDateIndicator_fragment;

    protected SuperFragment mHaksik_fragment;
    protected SuperFragment mGiksik_fragment;
    protected SuperFragment mGongsik_fragment;

    private SuperFragment curTabSelected;

    private edu.skku.sw3.skkucafeteria.Menu haksik_menu;
    private edu.skku.sw3.skkucafeteria.Menu gongsik_menu;
    private edu.skku.sw3.skkucafeteria.Menu giksik_menu;

    private int mPos;

    private LoginArtik mLoginArtik;
    private boolean mArtikEnabled = false;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_layout_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mLoginArtik = new LoginArtik(this);
        crawlingDate = Calendar.getInstance();
        mHaksik_fragment = new HaksikFragment();
        mGongsik_fragment = new GongsikFragment();
        mGiksik_fragment = new GiksikFragment();
        mDateIndicator_fragment = new Fragment_DateIndicator();
        curTabSelected = mHaksik_fragment;
        mPos = 0;

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mHaksik_fragment).commit();

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("학식"));
        tabs.addTab(tabs.newTab().setText("긱식"));
        tabs.addTab(tabs.newTab().setText("공식"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                mPos = pos;

                mLoginArtik.stopArtikConnection();
                if(pos == 0)
                {
                    curTabSelected = mHaksik_fragment;
                }
                else if (pos == 1)
                {
                    curTabSelected = mGiksik_fragment;
                }
                else if (pos == 2)
                {
                    curTabSelected = mGongsik_fragment;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, curTabSelected).commit();

                executeCrawler();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.date_indicator_container, mDateIndicator_fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int curId = item.getItemId();
        switch(curId)
        {
            case R.id.mainactivity_menu_artiklogin:
                mLoginArtik.doAuth(haksik_menu, gongsik_menu, haksik_menu);
                mArtikEnabled = true;
                break;
            case R.id.mainactivity_menu_soldout:
                if (mPos == 0) {
                    mLoginArtik.startArtikConnection(0, haksik_menu, this.crawlingDate);
                } else if (mPos == 1) {
                    mLoginArtik.startArtikConnection(1, giksik_menu, this.crawlingDate);
                } else {
                    mLoginArtik.startArtikConnection(2, gongsik_menu, this.crawlingDate);
                }
                break;
            case R.id.mainactivity_menu_preferenceSetting:
                Intent intent_settingPreferenceActivity = new Intent(MainActivity.this, SettingPreferenceActivity.class);
                startActivity(intent_settingPreferenceActivity);
                break;
            case R.id.mainactivity_menu_alarmSetting:
                Intent intent_settingAlarmActivity = new Intent(MainActivity.this, SettingAlarmActivity.class);
                startActivity(intent_settingAlarmActivity);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setDate(Calendar date) {
        this.crawlingDate = date;
        executeCrawler();
    }

    private void executeCrawler()
    {
        Crawler crawler;

        if (curTabSelected instanceof HaksikFragment)
        {
            crawler = new HaksikCrawler(this, crawlingDate);
        }
        else if (curTabSelected instanceof GongsikFragment)
        {
            crawler = new GongsikCrawler(this, crawlingDate);
        }
        else
        {
            crawler = new GiksikCrawler(this, crawlingDate);
        }

        crawler.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLoginArtik.checkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mLoginArtik.checkIntent(intent);
    }

    @Override
    public void crawlingCompleted(Crawler from, edu.skku.sw3.skkucafeteria.Menu menu)
    {
        if(from instanceof HaksikCrawler)
        {
            haksik_menu = menu;
        }
        else if (from instanceof GongsikCrawler)
        {
            gongsik_menu = menu;
        }
        else if (from instanceof GiksikCrawler)
        {
            giksik_menu = menu;
        }

        if (curTabSelected instanceof HaksikFragment)
        {
            curTabSelected.updateView(haksik_menu, crawlingDate);
        }
        else if (curTabSelected instanceof GongsikFragment)
        {
            curTabSelected.updateView(gongsik_menu, crawlingDate);
        }
        else if (curTabSelected instanceof GiksikFragment)
        {
            curTabSelected.updateView(giksik_menu, crawlingDate);
        }
    }
}
