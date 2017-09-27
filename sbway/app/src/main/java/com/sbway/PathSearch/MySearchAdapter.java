package com.sbway.PathSearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbway.R;

import java.util.ArrayList;

/**
 * Created by minjee on 2016-05-16.
 */
class SearchListInfo {
    int id;
    TextView path1;
}

public class MySearchAdapter extends BaseAdapter{
    LayoutInflater inflater;
    Context context;
    ArrayList<SearchInfo> listData;
    SearchListInfo info;

    public MySearchAdapter(Context context, ArrayList<SearchInfo> data ) {
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

        if(convertView == null){
            info = new SearchListInfo();
            v = inflater.inflate(R.layout.search_list, null);

            info.path1 = (TextView)v.findViewById(R.id.path);
            info.path1.setText(listData.get(position).path);

            v.setTag(info);

        }else{// if(((SListInfo)v.getTag()).path1.getText().toString() != listData.get(position).path){
            info = new SearchListInfo();
            v = inflater.inflate(R.layout.search_list, null);

            info.path1 = (TextView)v.findViewById(R.id.path);
            info.path1.setText(listData.get(position).path);

            v.setTag(info);
        }

        return v;
    }
    public void setArrayList(ArrayList<SearchInfo> arrays) {
        listData = arrays;
    }

    // 목록 정보를 되돌려 주는 함수
    public ArrayList<SearchInfo> getArrayList() {
        return listData;
    }
}
