package edu.skku.sw3.skkucafeteria;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by pjhwa on 2018-05-09.
 */

public class FileInTask extends AsyncTask<Void, Void, Void> {
    private String mText;
    private File mFoodDir = null;
    ArrayList<String> mFileNames = new ArrayList<>();
    Context context;

    private ListView mListView;
    private String tag = "FileTask";
    private TextView mNothingText;


    FileInTask(Context context, ListView listView, LayoutInflater inflater, TextView nothingText) { //nothingText는 이미 id를 통해 textview를 찾은 상태
        File parent = context.getDataDir();
        mFoodDir = new File(parent, "food");
        if (!mFoodDir.exists()) {
            if (mFoodDir.mkdir()) {
                Log.e(tag, "Directory(" + parent.getAbsolutePath() + "/food) already exists.");
            }
        }
        mListView = listView;
        this.context = context;
        mNothingText = nothingText;
    }

    public ArrayList<String> getArray() {
        return this.mFileNames;
    }
    @Override
    protected Void doInBackground(Void... voids) { //무거운 일 처리하는애. thread pool이 관리하는 other thread
        Log.i(tag, "background process for file read starts");
        File[] foods = mFoodDir.listFiles();
        for (File file : foods) {
            Log.i("FileNames", file.getName());
            if (!mFileNames.contains(file.getName().toString())) {
                mFileNames.add(file.getName());
            }
        }

        return null;
    }

    @Override
    public void onPostExecute(Void v) { //UI업데이트하라고 있는애. main thread

        if (!mFileNames.isEmpty()) {
            ItemAdapter adapter = new ItemAdapter(context, R.layout.viewitem, mFileNames, mNothingText);
            mListView.setAdapter(adapter);
            mNothingText.setText("");
        } else {
            mNothingText.setText("등록된 메뉴가 없습니다.");
        }
    }
}
