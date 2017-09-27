package com.sbway;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sbway.Bookmark.BookmarkInfo;
import com.sbway.R;

import java.util.ArrayList;

/**
 * Created by PC on 2016-10-03.
 */

class BookmarkListInfo {
    int id;
    TextView path;
}

public class Bookmark_Adapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<BookmarkInfo> listData;
    BookmarkListInfo info;

    public Bookmark_Adapter(Context context, ArrayList<BookmarkInfo> data) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        listData = data;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (convertView == null) {
            info = new BookmarkListInfo();
            v = inflater.inflate(R.layout.home_bookmark_item, null);

            info.path = (TextView) v.findViewById(R.id.path);
            info.path.setText(listData.get(position).path);

            v.setTag(info);

        } else {
            info = new BookmarkListInfo();
            v = inflater.inflate(R.layout.home_bookmark_item, null);
            info.path = (TextView) v.findViewById(R.id.path);
            info.path.setText(listData.get(position).path);

            v.setTag(info);
        }

        return v;
    }

    public void setArrayList(ArrayList<BookmarkInfo> arrays) {
        listData = arrays;
    }

    // 목록 정보를 되돌려 주는 함수
    public ArrayList<BookmarkInfo> getArrayList() {
        return listData;
    }
}