package edu.skku.sw3.skkucafeteria;

import android.support.v4.app.Fragment;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public abstract class SuperFragment extends Fragment {

    protected LinearLayout mLinearLayout_breakfast;
    protected LinearLayout mLinearLayout_lunch;
    protected LinearLayout mLinearLayout_dinner;

    protected TextView textView_title;

    public abstract void updateView(Menu menu, Calendar date);
}
