package edu.skku.sw3.skkucafeteria;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class GongsikFragment extends SuperFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_layout_gongsik, container, false);

        mLinearLayout_breakfast = view.findViewById(R.id.linearLayout_gongsik_breakfast_container);
        mLinearLayout_lunch = view.findViewById(R.id.linearLayout_gongsik_lunch_container);
        mLinearLayout_dinner = view.findViewById(R.id.linearLayout_gongsik_dinner_container);

        textView_title = view.findViewById(R.id.textView_gongsik_title);

        return view;
    }

    @Override
    public void updateView(Menu gongsik_menu, Calendar date)
    {
        ArrayList<MenuItem> breakfast = gongsik_menu.getBreakfast();
        ArrayList<MenuItem> lunch = gongsik_menu.getLunch();
        ArrayList<MenuItem> dinner = gongsik_menu.getDinner();

        mLinearLayout_breakfast.removeAllViews();
        mLinearLayout_lunch.removeAllViews();
        mLinearLayout_dinner.removeAllViews();

        textView_title.setText(Fragment_DateIndicator.getDateInString(date) + " 공식 메뉴");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0, 20);

        if (breakfast == null)
        {
            TextView textView = new TextView(getActivity());
            textView.setText("정보가 없습니다.");
            textView.setLayoutParams(lp);

            mLinearLayout_breakfast.addView(textView);
        }
        else
        {
            for(MenuItem x : breakfast)
            {
                TextView textView = new TextView((getActivity()));
                textView.setText(x.getNamesInStr(", "));
                textView.setLayoutParams(lp);

                if(x.getAvailability() == false)
                {
                    textView.setTextColor(Color.parseColor("#FF0000"));
                }

                mLinearLayout_breakfast.addView(textView);
            }
        }

        if (lunch == null)
        {
            TextView textView = new TextView(getActivity());
            textView.setText("정보가 없습니다.");
            textView.setLayoutParams(lp);

            mLinearLayout_lunch.addView(textView);
        }
        else
        {
            for(MenuItem x : lunch)
            {
                TextView textView = new TextView((getActivity()));
                textView.setText(x.getNamesInStr(", "));
                textView.setLayoutParams(lp);

                if(x.getAvailability() == false)
                {
                    textView.setTextColor(Color.parseColor("#FF0000"));
                }

                mLinearLayout_lunch.addView(textView);
            }
        }

        if (dinner == null)
        {
            TextView textView = new TextView(getActivity());
            textView.setText("정보가 없습니다.");
            textView.setLayoutParams(lp);

            mLinearLayout_dinner.addView(textView);
        }
        else
        {
            for(MenuItem x : dinner)
            {
                TextView textView = new TextView((getActivity()));
                textView.setText(x.getNamesInStr(", "));
                textView.setLayoutParams(lp);

                if(x.getAvailability() == false)
                {
                    textView.setTextColor(Color.parseColor("#FF0000"));
                }

                mLinearLayout_dinner.addView(textView);
            }
        }
    }
}