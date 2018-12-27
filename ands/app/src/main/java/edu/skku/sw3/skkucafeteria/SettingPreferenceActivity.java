package edu.skku.sw3.skkucafeteria;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-05-21.
 */

public class SettingPreferenceActivity extends AppCompatActivity {

    Button SubmitButton;
    EditText InputKeywordText;
    TextView KeywordsText;
    LayoutInflater inflater;
    TextView Nothing;
    ArrayList<String> filelist = new ArrayList<>();

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.settingmenu);
        Nothing = (TextView)findViewById(R.id.nothing);
        inflater = getLayoutInflater();
        InputKeywordText = (EditText)findViewById(R.id.inputtext);
        final ListView listview = (ListView)findViewById(R.id.list_view);

        listview.setDivider(this.getResources().getDrawable(R.drawable.transperent_color));
        listview.setDividerHeight(15);


        KeywordsText = (TextView)findViewById(R.id.textView);
        final TextView nothingText = (TextView) findViewById(R.id.nothing);

        FileInTask fileInTask = new FileInTask(getApplicationContext(), listview, inflater, nothingText);
        fileInTask.execute();

        SubmitButton = (Button)findViewById(R.id.submitbtn);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileOutTask fileTask = new FileOutTask(getApplicationContext());
                fileTask.execute(InputKeywordText.getText().toString());

                FileInTask fileInTask = new FileInTask(getApplicationContext(), listview, inflater, nothingText);
                fileInTask.execute();
                InputKeywordText.setText("");
            }
        });
    }
}
