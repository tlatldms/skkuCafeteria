package edu.skku.sw3.skkucafeteria;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class Fragment_DateIndicator extends Fragment {
    public interface ISetDateListener
    {
        void setDate(Calendar date);
    }

    private ISetDateListener setDateListeners;

    private Calendar date;
    private TextView mTextView_date;
    private Button mButton_gotoYesterday;
    private Button mButton_gotoTommorow;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof ISetDateListener)
        {
            setDateListeners = (ISetDateListener)context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement SetDateListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_layout_date_indicator, container, false);

        mTextView_date = view.findViewById(R.id.textView_date);
        mButton_gotoYesterday = view.findViewById(R.id.button_gotoYesterday);
        mButton_gotoTommorow = view.findViewById(R.id.button_gotoTomorrow);

        date = Calendar.getInstance();
        setTextOnTextView();

        mButton_gotoYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.add(Calendar.DATE, -1);
                setTextOnTextView();
            }
        });

        mButton_gotoTommorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.add(Calendar.DATE, 1);
                setTextOnTextView();
            }
        });

        mTextView_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = Calendar.getInstance();
                setTextOnTextView();
            }
        });

        return view;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        setDateListeners = null;
    }

    private void setTextOnTextView() {
        mTextView_date.setText(getDateInString(date));

        setDateListeners.setDate(date);
    }

    public static String getDateInString(Calendar date)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(date.get(Calendar.YEAR));
        stringBuilder.append(".");
        stringBuilder.append(date.get(Calendar.MONTH) + 1);
        stringBuilder.append(".");
        stringBuilder.append(date.get(Calendar.DAY_OF_MONTH));
        stringBuilder.append(".(");
        switch(date.get(Calendar.DAY_OF_WEEK))
        {
            case 1:
                stringBuilder.append("일");
                break;
            case 2:
                stringBuilder.append("월");
                break;
            case 3:
                stringBuilder.append("화");
                break;
            case 4:
                stringBuilder.append("수");
                break;
            case 5:
                stringBuilder.append("목");
                break;
            case 6:
                stringBuilder.append("금");
                break;
            case 7:
                stringBuilder.append("토");
                break;
        }
        stringBuilder.append(")");

        return stringBuilder.toString();
    }
}
