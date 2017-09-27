package com.sbway.News;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sbway.R;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by PC on 2016-09-30.
 */
public class NewsAdapterHome extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<String> listData;
    TextView result;

    public NewsAdapterHome(Context context, ArrayList<String> data ) {
        inflater = LayoutInflater.from( context );
        this.context = context;
        listData = data;
    }

    @Override
    public int getCount() {return listData.size();}
    @Override
    public Object getItem(int position) {return listData.get(position);}
    @Override
    public long getItemId(int position) {return position;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (convertView == null) {
            if(position==0){
                v = inflater.inflate(R.layout.news_listitem_first, null);
                result = (com.sbway.TextviewCustom.OutlineTextView) v.findViewById(R.id.news_title);
            }else {
                v = inflater.inflate(R.layout.news_listitem, null);
                result = (TextView) v.findViewById(R.id.news_title);
            }
            result.setText(listData.get(position));
            v.setTag(result);
        } else {
            if(position==0){
                v = inflater.inflate(R.layout.news_listitem_first, null);
                result = (com.sbway.TextviewCustom.OutlineTextView) v.findViewById(R.id.news_title);
            }else {
                v = inflater.inflate(R.layout.news_listitem, null);
                result = (TextView) v.findViewById(R.id.news_title);
            }
            result.setText(listData.get(position));

            v.setTag(result);
        }
        return v;
    }
}
