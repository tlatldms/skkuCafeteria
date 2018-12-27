package edu.skku.sw3.skkucafeteria;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.O)
public class BroadcastD extends BroadcastReceiver implements Crawler.IJobCompleteListener{
    String INTENT_ACTION = Intent.ACTION_BOOT_COMPLETED;
    private static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "wow";
    StringBuilder text;

    private File mFoodDir = null;
    ArrayList<String> mFileNames = new ArrayList<>();

    private Menu haksik_menu;
    private Menu gongsik_menu;
    private Menu giksik_menu;

    private HaksikCrawler haksikCrawler;
    private GongsikCrawler gongsikCrawler;
    private GiksikCrawler giksikCrawler;

    private ArrayList<String> haksikMatches;
    private ArrayList<String> gongsikMatches;
    private ArrayList<String> giksikMatches;

    private String msg_text = "";
    private boolean flag_gongsik = false;
    private boolean flag_haksik = false;
    private boolean flag_giksik = false;

    private boolean flag_crawling_haksik = false;
    private boolean flag_crawling_gongsik = false;
    private boolean flag_crawling_giksik = false;

    Notification mNoti;
    NotificationManager notificationmanager;
    PendingIntent pendingIntent;
    Context context;

    File[] foods;

    @Override
    public void onReceive(Context context1, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함
        Log.d("TG : ", "알람매니저 작동");

        notificationmanager = (NotificationManager) context1.getSystemService(Context.NOTIFICATION_SERVICE);
        context = context1;

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
        notificationmanager.createNotificationChannel(channel);

        pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, SettingAlarmActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        //파일 목록 읽어와서 리스트화화
        File parent = context.getDataDir();
        mFoodDir = new File(parent, "food");
        if (!mFoodDir.exists()) {
            if (mFoodDir.mkdir()) {
                Log.e("TG : ", "Directory(" + parent.getAbsolutePath() + "/food) already exists.");
            }
        }

        foods = mFoodDir.listFiles();

        haksikCrawler = new HaksikCrawler(this, Calendar.getInstance());
        gongsikCrawler = new GongsikCrawler(this, Calendar.getInstance());
        giksikCrawler = new GiksikCrawler(this, Calendar.getInstance());

        haksikCrawler.execute();
        gongsikCrawler.execute();
        giksikCrawler.execute();
    }

    @Override
    public void crawlingCompleted(Crawler from, Menu menu) {
        if(from instanceof HaksikCrawler)
        {
            StringBuilder haksik_strBuilder = new StringBuilder();

            for(MenuItem item : menu.getBreakfast())
            {
                haksik_strBuilder.append(item.getNamesInStr(" "));
            }

            for(MenuItem item : menu.getLunch())
            {
                haksik_strBuilder.append(item.getNamesInStr(" "));
            }

            for(MenuItem item : menu.getDinner())
            {
                haksik_strBuilder.append(item.getNamesInStr(" "));
            }

            String haksik = haksik_strBuilder.toString();

            for(File food : foods)
            {
                if(haksik.matches(".*"+food.getName()+".*"))
                {
                    flag_haksik = true;
                    Log.d("================BroadcastD", "haksik matches");
                    break;
                }
            }

            flag_crawling_haksik = true;
        }
        else if (from instanceof GongsikCrawler)
        {
            StringBuilder gongsik_strBuilder = new StringBuilder();

            for(MenuItem item : menu.getBreakfast())
            {
                gongsik_strBuilder.append(item.getNamesInStr(" "));
            }

            for(MenuItem item : menu.getLunch())
            {
                gongsik_strBuilder.append(item.getNamesInStr(" "));
            }

            for(MenuItem item : menu.getDinner())
            {
                gongsik_strBuilder.append(item.getNamesInStr(" "));
            }

            String gongsik = gongsik_strBuilder.toString();

            for(File food : foods)
            {
                if(gongsik.matches(".*"+food.getName()+".*"))
                {
                    flag_gongsik = true;
                    Log.d("================BroadcastD", "gongsik matches");
                    break;
                }
            }

            flag_crawling_gongsik = true;
        }
        else if (from instanceof GiksikCrawler)
        {
            StringBuilder giksik_strBuilder = new StringBuilder();

            for(MenuItem item : menu.getBreakfast())
            {
                giksik_strBuilder.append(item.getNamesInStr(" "));
            }

            for(MenuItem item : menu.getLunch())
            {
                giksik_strBuilder.append(item.getNamesInStr(" "));
            }

            for(MenuItem item : menu.getDinner())
            {
                giksik_strBuilder.append(item.getNamesInStr(" "));
            }

            String giksik = giksik_strBuilder.toString();

            for(File food : foods)
            {
                if(giksik.matches(".*"+food.getName()+".*"))
                {
                    flag_giksik = true;
                    Log.d("================BroadcastD", "giksik matches");
                    break;
                }
            }

            flag_crawling_giksik = true;
        }

        //if (flag_crawling_giksik && flag_crawling_gongsik && flag_crawling_haksik)
        {
            if(flag_gongsik)
                msg_text += "공대식당에 좋아하는 메뉴 등장!!\n" ;

            if(flag_haksik)
                msg_text += "학생회관식당에 좋아하는 메뉴 등장!!\n";

            if(flag_giksik)
                msg_text += "기숙사식당에 좋아하는 메뉴 등장!!\n";

            Log.d("TG : ", "flag 확인");
            if(flag_gongsik || flag_giksik || flag_haksik)
            {
                Log.d("TG : ", "푸시알림 쏜다.");
                mNoti = new Notification.Builder(context,CHANNEL_ID)
                        .setContentTitle("선호 메뉴 알림")
                        .setContentText(msg_text)
                        .setTicker("Notice!!!")
                        .setSmallIcon(R.drawable.settingmain)
                        .setContentIntent(pendingIntent)
                        .build();

                mNoti.defaults = Notification.DEFAULT_SOUND;
                mNoti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                mNoti.flags = Notification.FLAG_AUTO_CANCEL;

                notificationmanager.notify(777, mNoti);
            }
        }
    }
}

