package edu.skku.sw3.skkucafeteria;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingAlarmActivity extends AppCompatActivity {
    private static int ONE_MINUTE = 5626;

    ArrayList<String> spinnerlist = new ArrayList<String>();
    ArrayList<String> spinnerlist2 = new ArrayList<String>();
    ArrayList<String> spinnerlist3 = new ArrayList<String>();

    String noon = "";
    String hour = "";
    String minute = "";

    int num_hour = 0;
    int num_minute = 0;

    Switch switch_button;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_setting_alarm);

        Spinner noon_spinner = (Spinner)findViewById(R.id.noon_spinner);
        Spinner hour_spinner = (Spinner)findViewById(R.id.hour_spinner);
        Spinner minute_spinner = (Spinner)findViewById(R.id.minute_spinner);
        Button set_alarm_button = (Button)findViewById(R.id.setalarm_button);
        switch_button = (Switch)findViewById(R.id.switch1);

        pref = getSharedPreferences("pref",0);
        editor = pref.edit();

        if(pref.getBoolean("Switch_Checked", false))
            switch_button.setChecked(true);
        else
            switch_button.setChecked(false);

        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                    editor.putBoolean("Switch_Checked", isChecked);
                else {
                    editor.putBoolean("Switch_Checked", false);

                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), SettingAlarmActivity.class), PendingIntent.FLAG_NO_CREATE);
                }
               editor.commit();

            }
        });

        spinnerlist.add("오전");
        spinnerlist.add("오후");

        for(int i = 0; i < 12 ; i++)
            spinnerlist2.add(String.valueOf(i+1)+"시");

        for(int i = 0; i < 60 ; i++)
            spinnerlist3.add(String.valueOf(i+1)+"분");



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerlist);
        noon_spinner.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerlist2);
        hour_spinner.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerlist3);
        minute_spinner.setAdapter(adapter3);

        Log.d("TG : ", String.valueOf(pref.getInt("noon", -1)));

        if(pref.getInt("noon", -1) != -1)
        {
            noon = spinnerlist.get(pref.getInt("noon", -1));
            noon_spinner.setSelection(pref.getInt("noon", -1));
        }
        if(pref.getInt("hour", -1) != -1)
        {
            hour = spinnerlist2.get(pref.getInt("hour", -1));
            hour_spinner.setSelection(pref.getInt("hour", -1));
        }
        if(pref.getInt("minute", -1) != -1)
        {
            minute = spinnerlist3.get(pref.getInt("minute", -1));
            minute_spinner.setSelection(pref.getInt("minute", -1));
        }

        noon_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                noon = spinnerlist.get(position);
                editor.putInt("noon", position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hour_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hour = spinnerlist2.get(position);
                editor.putInt("hour", position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        minute_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                minute = spinnerlist3.get(position);
                editor.putInt("minute", position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        set_alarm_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(switch_button.isChecked())
                {
                    num_hour = Integer.valueOf(hour.substring(0, hour.indexOf("시")));
                    num_minute = Integer.valueOf(minute.substring(0, minute.indexOf("분")));

                    if(noon.equals("오후"))
                        num_hour += 12;

                    if(num_hour == 24)
                        num_hour = 0;

                    new AlarmHATT(getApplicationContext()).Alarm();
                    Toast.makeText(getApplicationContext(), noon+" "+hour+" "+minute+"에 알람 설정 되었습니다.", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "알람 스위치를 켜주세요", Toast.LENGTH_LONG).show();
            }
        });


    }

    public class AlarmHATT {
        private Context context;

        public AlarmHATT(Context context) {
            this.context = context;
        }

        public void Alarm() {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(SettingAlarmActivity.this, BroadcastD.class);

            PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), num_hour, num_minute, 0);

            num_hour = 0;
            num_minute = 0;

            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }
}

