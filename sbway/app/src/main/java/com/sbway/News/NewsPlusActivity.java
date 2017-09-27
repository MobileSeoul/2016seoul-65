package com.sbway.News;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sbway.Bookmark.BookmarkActivity;
import com.sbway.CalendarActivity;
import com.sbway.Memo.MemoListActivity;
import com.sbway.R;
import com.sbway.Weather.WeatherActivity;

import java.util.ArrayList;

public class NewsPlusActivity extends AppCompatActivity {
    //뉴스 타이틀 띄우기
    ListView list;
    NewsAdapterPlus adapter;
    ArrayList<String> titlevec = new ArrayList<String>();
    ArrayList<String> linkvec = new ArrayList<String>();
    public ArrayList<String> descvec = new ArrayList<String>();
    public ArrayList<String> imgvec = new ArrayList<String>();
    NewsAsyncTask newsAsyncTask = new NewsAsyncTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_plus);

        newsAsyncTask.execute(null,null,null);
        while(true) {
            try {
                Thread.sleep(1000);
                if (newsAsyncTask.flag == true) {
                    titlevec = newsAsyncTask.titlevec;
                    descvec = newsAsyncTask.descvec;
                    linkvec = newsAsyncTask.linkvec;
                    imgvec = newsAsyncTask.imgvec;
                    break; //반복문 종료
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        adapter = new NewsAdapterPlus(getApplicationContext(),titlevec,descvec,imgvec);
        list = (ListView)findViewById(R.id.news_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), NewsActivity.class);
                i.putExtra("link", linkvec.get(position));
                startActivity(i);
            }
        });


        /****************하단버튼***************/

        findViewById(R.id.mainbtnN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.scheduleN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.memoN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.ddayN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),com.sbway.Dday.DdayListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.newsN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewsPlusActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.bookmarkN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookmarkActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
