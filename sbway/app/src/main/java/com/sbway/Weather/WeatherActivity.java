package com.sbway.Weather;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbway.Bookmark.BookmarkActivity;
import com.sbway.Bookmark.BookmarkAdapter;
import com.sbway.Bookmark.BookmarkInfo;
import com.sbway.Bookmark.DBHandler_Bookmark;
import com.sbway.CalendarActivity;
import com.sbway.Dday.DdayListActivity;
import com.sbway.Memo.MemoListActivity;
import com.sbway.R;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import it.sephiroth.android.library.widget.HListView;

public class WeatherActivity extends AppCompatActivity {
    ImageView background;
    ImageView weather_img;
    TextView temp_now;
    TextView weather_loc;
    TextView weather_today;
    TextView low_temp;
    TextView high_temp;
    TextView water_drop;
    ImageView hum_cup;
    TextView humidity;

    ArrayList<WeatherInfo> weatherData;
    WeatherAdapter weatherAdapter;
    HListView weather_list;
    SharedPreferences tmxtmn;

    //날씨파싱
    SharedPreferences zone_code;
    GetXMLTask task;
    String zone="";
    Document doc;
    WeatherInfo w;
    int a=0;


    ProgressDialog progressDialog;
    int aa=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        ProgressThread progressThread = new ProgressThread();
        progressThread.setDaemon(true);
        progressThread.start();

        zone_code = getSharedPreferences("zone",0); //zonecode=파싱코드 city=지역
        zone = zone_code.getString("zonecode","");

        tmxtmn = getSharedPreferences("tmxtmn", 0);

        task = new GetXMLTask();

        background = (ImageView)findViewById(R.id.background);
        weather_img = (ImageView)findViewById(R.id.weather_img);
        temp_now = (TextView)findViewById(R.id.temp_now);
        weather_loc = (TextView)findViewById(R.id.weather_loc);
        weather_today = (TextView)findViewById(R.id.weather_today);
        low_temp = (TextView)findViewById(R.id.weather_templow);
        high_temp = (TextView)findViewById(R.id.weather_temphigh);
        water_drop = (TextView)findViewById(R.id.water_drop);
        hum_cup = (ImageView) findViewById(R.id.humidity_cup);
        humidity = (TextView)findViewById(R.id.humidity);

        if(tmxtmn!=null){
            low_temp.setText(tmxtmn.getString("tmn", ""));
            high_temp.setText(tmxtmn.getString("tmx", ""));
        }

        SimpleDateFormat spf=new SimpleDateFormat("yyyy년 MM월 dd일(E)");
        String today = spf.format(new Date());
        weather_today.setText(today);

        //시간에 따라 배경변경
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm", Locale.KOREA);
        String n = sdf.format(new Date());
        String snight = "18:00";
        String sday = "06:00";
        Date now = null;
        Date night = null;
        Date day = null;
        try {
            now = sdf.parse(n);
            night = sdf.parse(snight);
            day = sdf.parse(sday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(now!=null&&now.after(night)||now.before(day)){
            background.setBackgroundResource(R.drawable.night);
        }else{
            background.setBackgroundResource(R.drawable.day);
        }


        if(zone.length()!=0) {
            task.execute("http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=" + zone);
            Log.d("존", zone);
        }

        weatherData = new ArrayList<WeatherInfo>();
        weatherAdapter = new WeatherAdapter(this, weatherData);
        weather_list = (HListView)findViewById(R.id.weather_list);
        weather_list.setAdapter(weatherAdapter);

        /****************하단버튼***************/

        findViewById(R.id.mainbtnM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        findViewById(R.id.scheduleM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.memoM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.ddayM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DdayListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.newsM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.sbway.News.NewsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.bookmarkM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookmarkActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    class ProgressThread extends Thread {
        public void run() {
            while (aa<2) {
                progressHandler.sendEmptyMessage(aa);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what==0){
                progressDialog = ProgressDialog.show(WeatherActivity.this, "", "로딩중입니다.", true, true);
                aa++;
            }else if(msg.what==1){
                progressDialog.dismiss();
            }
        }
    };


    //날씨 파싱
    private class GetXMLTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            String wfKor = "";
            int i=0;

            //data태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
            NodeList nodeList = doc.getElementsByTagName("item");
            org.w3c.dom.Element fstElmnt = (org.w3c.dom.Element) nodeList.item(0);

            NodeList cityList = fstElmnt.getElementsByTagName("category");
            String city = cityList.item(0).getChildNodes().item(0).getNodeValue();
            weather_loc.setText(city);

            //if data seq=0이면
            //if day=1일 때 maxtemp랑 mintemp구하기
            //if reh해서 수치로 이미지 정하기

            NodeList dataList = fstElmnt.getElementsByTagName("data");
            for(i=0; i<dataList.getLength();i++) {
                if(i==0){
                    w = new WeatherInfo();
                    w.w_day = "오늘";
                    NodeList wfKorList = fstElmnt.getElementsByTagName("wfKor");
                    wfKor = wfKorList.item(i).getChildNodes().item(0).getNodeValue();
                    w.w_img = wfKor;

                    NodeList tempList = fstElmnt.getElementsByTagName("temp");
                    String temp = tempList.item(i).getChildNodes().item(0).getNodeValue();
                    temp_now.setText(temp + "˚");
                    w.w_now_temp = temp;

                    NodeList popList = fstElmnt.getElementsByTagName("pop");//강수
                    String pop = popList.item(i).getChildNodes().item(0).getNodeValue();
                    water_drop.setText(pop+"%");
                    w.w_now_rain = pop;

                    NodeList rehList = fstElmnt.getElementsByTagName("reh");
                    String reh = rehList.item(i).getChildNodes().item(0).getNodeValue();
                    humidity.setText(reh);
                    int get_reh = Integer.valueOf(reh);

                    switch (wfKor) {
                        case "맑음":
                            weather_img.setBackgroundResource(R.drawable.sun);
                            break;
                        case "구름 조금":
                            weather_img.setBackgroundResource(R.drawable.little_cloud);
                            break;
                        case "흐림":
                            weather_img.setBackgroundResource(R.drawable.cloud);
                            break;
                        case "구름 많음":
                            weather_img.setBackgroundResource(R.drawable.many_cloud);
                            break;
                        case "비":
                            weather_img.setBackgroundResource(R.drawable.rain);
                            break;
                    }

                    if(0<=get_reh && 30>get_reh){
                        hum_cup.setBackgroundResource(R.drawable.cup1);
                    }else if(30<=get_reh && 40>get_reh){
                        hum_cup.setBackgroundResource(R.drawable.cup2);
                    }else if(40<=get_reh && 50>get_reh){
                        hum_cup.setBackgroundResource(R.drawable.cup3);
                    }else if(50<=get_reh && 60>get_reh){
                        hum_cup.setBackgroundResource(R.drawable.cup4);
                    }else if(60<=get_reh && 70>get_reh){
                        hum_cup.setBackgroundResource(R.drawable.cup5);
                    }else if(70<=get_reh && 90>get_reh){
                        hum_cup.setBackgroundResource(R.drawable.cup6);
                    }else if(90<=get_reh && 100>=get_reh){
                        hum_cup.setBackgroundResource(R.drawable.cup7);
                    }

                    //최저기온, 최고기온
                    NodeList tmxList = fstElmnt.getElementsByTagName("tmx");
                    String tmx = tmxList.item(0).getChildNodes().item(0).getNodeValue();

                    NodeList tmnList = fstElmnt.getElementsByTagName("tmn");
                    String tmn = tmnList.item(0).getChildNodes().item(0).getNodeValue();
                    if(Double.parseDouble(tmx)>-300.0&&Double.parseDouble(tmn)>-300.0){
                        tmxtmn = getSharedPreferences("tmxtmn", 0);
                        SharedPreferences.Editor tmxtmnEditor = tmxtmn.edit();
                        tmxtmnEditor.putString("tmx", tmx);
                        tmxtmnEditor.putString("tmn", tmn);
                        tmxtmnEditor.commit();
                        Log.d("기온", "1");
                        a++;
                    }

                    NodeList hourList = fstElmnt.getElementsByTagName("hour");
                    String whour = hourList.item(0).getChildNodes().item(0).getNodeValue();
                    SimpleDateFormat formatwhour = new SimpleDateFormat("kk:mm");
                    try {
                        Date parsewhour = formatwhour.parse(whour+":00");
                        String wtime = formatwhour.format(parsewhour);
                        w.w_time = wtime;
                    }catch (Exception e){Log.d("오류", e.toString());}

                    weatherData.add(w);
                    weatherAdapter.notifyDataSetChanged();
                }else{
                    w = new WeatherInfo();
                    NodeList dayList = fstElmnt.getElementsByTagName("day");
                    String wday = dayList.item(i).getChildNodes().item(0).getNodeValue();
                    if(wday.equals("0")){
                        w.w_day = "오늘";
                    }else if(wday.equals("1")){
                        w.w_day = "내일";
                    }else{
                        SimpleDateFormat twoday = new SimpleDateFormat("MM/dd");
                        String formatTwodays = twoday.format(new Date());
                        try {
                            Date parseTwodays = twoday.parse(formatTwodays);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(parseTwodays);
                            calendar.add(Calendar.DATE, +Integer.parseInt(wday));
                            String getTowdays = twoday.format(calendar.getTime());
                            w.w_day = getTowdays;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    NodeList hourList = fstElmnt.getElementsByTagName("hour");
                    String whour = hourList.item(i).getChildNodes().item(0).getNodeValue();
                    SimpleDateFormat formatwhour = new SimpleDateFormat("kk:mm");
                    try {
                        Date parsewhour = formatwhour.parse(whour+":00");
                        String wtime = formatwhour.format(parsewhour);
                        w.w_time = wtime;
                    }catch (Exception e){Log.d("오류", e.toString());}

                    NodeList wfKorList = fstElmnt.getElementsByTagName("wfKor");
                    w.w_img = wfKorList.item(i).getChildNodes().item(0).getNodeValue();

                    NodeList tempList = fstElmnt.getElementsByTagName("temp");
                    w.w_now_temp = tempList.item(i).getChildNodes().item(0).getNodeValue()+"˚";

                    NodeList popList = fstElmnt.getElementsByTagName("pop");//강수
                    w.w_now_rain = popList.item(i).getChildNodes().item(0).getNodeValue();

                    //최저기온, 최고기온
                    NodeList tmxList = fstElmnt.getElementsByTagName("tmx");
                    String tmx = tmxList.item(i).getChildNodes().item(0).getNodeValue();

                    NodeList tmnList = fstElmnt.getElementsByTagName("tmn");
                    String tmn = tmnList.item(i).getChildNodes().item(0).getNodeValue();
                    if(Double.parseDouble(tmx)>-300.0&&Double.parseDouble(tmn)>-300.0&&a==0){
                        tmx = tmxList.item(i).getChildNodes().item(0).getNodeValue();
                        tmn = tmnList.item(i).getChildNodes().item(0).getNodeValue();

                        tmxtmn = getSharedPreferences("tmxtmn", 0);
                        SharedPreferences.Editor tmxtmnEditor = tmxtmn.edit();
                        tmxtmnEditor.putString("tmx", tmx);
                        tmxtmnEditor.putString("tmn", tmn);
                        tmxtmnEditor.commit();
                        a++;
                    }

                    weatherData.add(w);
                    weatherAdapter.notifyDataSetChanged();
                }
            }
        }
    }//end inner class - GetXMLTask
}
