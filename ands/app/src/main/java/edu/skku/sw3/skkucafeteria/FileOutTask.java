package edu.skku.sw3.skkucafeteria;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by pjhwa on 2018-05-09.
 */

public class FileOutTask extends AsyncTask<String, Void, Void> {
    private File mFile = null;
    private String tag = "FileTask";

    FileOutTask(Context context) {
        File parent = context.getDataDir();
        mFile = new File(parent, "food");
        if (!mFile.exists()) {
            if (mFile.mkdir()) {
                Log.e(tag, "Directory(" + parent.getAbsolutePath() + "/food) already exists.");
            }
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        for (String string : strings) {
            File tmp = new File(mFile, string);
            if (tmp.exists())   continue;
            else {
                try {
                    tmp.createNewFile();
                    Log.e("FileInTask", "File is created(" + tmp.getName() + ")");
                } catch (IOException e) {
                    Log.e("FileInTask", "File is not created(" + tmp.getName() + ")");
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
