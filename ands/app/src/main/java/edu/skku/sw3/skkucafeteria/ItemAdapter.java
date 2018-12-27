package edu.skku.sw3.skkucafeteria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018-05-28.
 */

public class ItemAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<String> data;
    private int layout_id;
    private File mFoodDir;
    public TextView Nothing;

    public ItemAdapter(Context context, int layout_id, ArrayList<String> data, TextView nothingText) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout_id = layout_id;
        mFoodDir = new File(context.getDataDir(), "food");
        Nothing = nothingText;

    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
//ItemAdapter adapter = new ItemAdapter(context, R.layout.viewitem, mFileNames);
    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.viewitem, viewGroup, false);
        }
        String food = data.get(i);
        TextView textView = (TextView) view.findViewById(R.id.viewitem);
        textView.setText(food);
        Button button = (Button) view.findViewById(R.id.deletebutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup parent = (ViewGroup) view.getParent();

                TextView textView = (TextView) parent.findViewById(R.id.viewitem);
                String food = textView.getText().toString();
                File file = new File(mFoodDir, food);

                if (file.exists()) {
                    file.delete();
                }

                //parent.removeView(view);
                //parent.removeView(textView);
                data.remove(food);
                notifyDataSetChanged();

                if (data.isEmpty()) {
                    Nothing.setText("등록된 메뉴가 없습니다.");
                }
            }
        });

        return view;
    }
}
