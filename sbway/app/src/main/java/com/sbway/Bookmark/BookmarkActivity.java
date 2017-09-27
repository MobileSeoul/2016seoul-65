package com.sbway.Bookmark;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbway.Dday.DdayListActivity;
import com.sbway.PathSearch.DBHandler_search;
import com.sbway.PathSearch.MySearchAdapter;
import com.sbway.PathSearch.ResultActivity;
import com.sbway.PathSearch.SearchActivity;
import com.sbway.PathSearch.SearchInfo;
import com.sbway.R;
import com.sbway.Weather.WeatherActivity;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {
    BookmarkInfo info;
    DBHandler_Bookmark handler;
    ArrayList<BookmarkInfo> listData;
    BookmarkAdapter adapter;
    ListView list;

    String s,e,path,getPath,c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        removeAllPreferences();

        handler = new DBHandler_Bookmark(this);
        listData = new ArrayList<BookmarkInfo>();
        adapter = new BookmarkAdapter(this, listData);
        list = (ListView)findViewById(R.id.bookmarklist);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("path", listData.get(position).path);
                intent.putExtra("start", listData.get(position).slng+","+listData.get(position).slat);
                intent.putExtra("end", listData.get(position).elng+","+listData.get(position).elat);
                intent.putExtra("startplace", listData.get(position).start_place);
                intent.putExtra("endplace", listData.get(position).end_place);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });



        findViewById(R.id.bookmark_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(i);
            }
        });


        /****************하단버튼***************/

        findViewById(R.id.mainbtnB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        findViewById(R.id.scheduleB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.memoB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.sbway.Memo.MemoListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.ddayB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DdayListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.newsB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.sbway.News.NewsPlusActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.bookmarkB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookmarkActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        displayBookmarkList();
        super.onResume();
    }

    protected void displayBookmarkList() {
        SharedPreferences sp = this.getApplicationContext().getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor= sp.edit();
        String id = sp.getString("id", "null");
        Cursor cursor = handler.selectAll(id);
        getPath = "";
        c="";
        if( cursor == null ) return;
        removeAllList();
        do {
            BookmarkInfo search = new BookmarkInfo();
            search.id = cursor.getInt(0);
            search.path = cursor.getString(2);
            search.start_place = cursor.getString(3);
            search.end_place = cursor.getString(4);
            search.slat = cursor.getString(5);
            search.slng = cursor.getString(6);
            search.elat = cursor.getString(7);
            search.elng = cursor.getString(8);

            getPath = search.path;
            listData.add(search);
        } while( cursor.moveToNext());
        // 커서를 닫고 listAdapter 객체에게 알려 갱신된 정보가
        // 화면에 표시되도록 함
        cursor.close();
        adapter.notifyDataSetChanged();
    }
    protected void removeAllList() {
        listData.removeAll(listData);
        adapter.notifyDataSetChanged();
    }
    private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
