package com.sbway.News;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbway.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by PC on 2016-10-09.
 */
class ListInfo {
    TextView news_title;
    TextView news_desc;
    ImageView news_img;
}
public class NewsAdapterPlus extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<String> titleList, descList, imgList;
    ListInfo info;
    Bitmap bitmap;

    public NewsAdapterPlus(Context context, ArrayList<String> title, ArrayList<String>  desc, ArrayList<String> img) {
        inflater = LayoutInflater.from( context );
        this.context = context;
        titleList = title;
        descList = desc;
        imgList = img;
    }

    @Override
    public int getCount() {return titleList.size();}
    @Override
    public Object getItem(int position) {return titleList.get(position);}
    @Override
    public long getItemId(int position) {return position;}
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (convertView == null) {
            info = new ListInfo();
            v = inflater.inflate(R.layout.newsplus_list_item, null);
            info.news_title = (TextView)v.findViewById(R.id.news_title);
            info.news_desc = (TextView)v.findViewById(R.id.news_desc);
            info.news_img = (ImageView)v.findViewById(R.id.news_img);
            v.setTag(info);
        } else {
            info = (ListInfo)v.getTag();
        }

        info.news_title.setText(titleList.get(position));
        info.news_desc.setText(descList.get(position));
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imgList.get(position)); // URL 주소를 이용해서 URL 객체 생성

                    //  아래 코드는 웹에서 이미지를 가져온 뒤
                    //  이미지 뷰에 지정할 Bitmap을 생성하는 과정
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                    } catch(IOException ex) {

                    }
                }
            };
        thread.start();

        try {
            //메인 스레드는 작업 스레드가 이미지 작업을 가져올 때까지
            //대기해야 하므로 작업스레드의 join() 메소드를 호출해서
            //메인 스레드가 작업 스레드가 종료될 까지 기다리도록 합니다.
            thread.join();
            //스레드 작업 완료
            //UI 작업을 할 수 있는 메인스레드에서 이미지뷰에 이미지를 지정
            info.news_img.setImageBitmap(bitmap);
        } catch (InterruptedException e) {}
        return v;
    }

}
