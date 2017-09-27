package com.sbway.Sync;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.sbway.HomeActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by PC on 2016-09-29.
 */
public class sync_Mysql extends Thread {
    Handler mHandler;
    public static boolean active=false;
    static Context context;
    String url=null;
    int gettype=0;

    //디데이
    public sync_Mysql(String aw,String reg_date,String status,int year,int month,int day,String title,int type, String memid, String milli){  //디데이 추가(10)
        String dsync_url="http://sbway.cafe24.com/php/d_insert.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "a_w=" + URLEncoder.encode(aw, "UTF-8")
                    + "&reg_date=" + URLEncoder.encode(reg_date, "UTF-8")
                    + "&status=" + URLEncoder.encode(status, "UTF-8")
                    + "&d_year=" + year
                    + "&d_month=" + month
                    + "&d_day=" + day
                    + "&d_title=" + URLEncoder.encode(title, "UTF-8")
                    + "&type=" + type
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&milli_date=" + URLEncoder.encode(milli, "UTF-8");
           // Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        gettype=10;
    }
    public sync_Mysql(String reg_date,String status,int year,int month,int day,String title,int type, String memid, String milli){  //디데이 갱신(9)
        String dsync_url="http://sbway.cafe24.com/php/d_update.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "&reg_date=" + URLEncoder.encode(reg_date, "UTF-8")
                    + "&status=" + URLEncoder.encode(status, "UTF-8")
                    + "&d_year=" + year
                    + "&d_month=" + month
                    + "&d_day=" + day
                    + "&d_title=" + URLEncoder.encode(title, "UTF-8")
                    + "&type=" + type
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&milli_date=" + URLEncoder.encode(milli, "UTF-8");
            //Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        gettype=9;
    }
    public sync_Mysql(String memid,String sync_time,Context mContext){  //디데이 SQLite 추가(3)
        String dsync_url="http://sbway.cafe24.com/php/d_sqlite_insert.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&sync_time=" + URLEncoder.encode(sync_time, "UTF-8");
            Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        context=mContext;
        gettype=3;
    }
    public sync_Mysql(String memid,String sync_time,Context mContext,String u){  //디데이 SQLite 갱신(4)
        String dsync_url="http://sbway.cafe24.com/php/d_sqlite_update.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&sync_time=" + URLEncoder.encode(sync_time, "UTF-8");
            Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        context=mContext;
        gettype=4;
    }

    //북마크
    public sync_Mysql(String aw,String reg_date,String status,String path, String start_place, String end_place, String slat, String slng, String elat, String elng, String memid, String milli){  //북마크 추가(12)
        String dsync_url="http://sbway.cafe24.com/php/b_insert.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "a_w=" + URLEncoder.encode(aw, "UTF-8")
                    + "&reg_date=" + URLEncoder.encode(reg_date, "UTF-8")
                    + "&status=" + URLEncoder.encode(status, "UTF-8")
                    + "&path=" + URLEncoder.encode(path, "UTF-8")
                    + "&start_place=" + URLEncoder.encode(start_place, "UTF-8")
                    + "&end_place=" + URLEncoder.encode(end_place, "UTF-8")
                    + "&slat=" + URLEncoder.encode(slat, "UTF-8")
                    + "&slng=" + URLEncoder.encode(slng, "UTF-8")
                    + "&elat=" + URLEncoder.encode(elat, "UTF-8")
                    + "&elng=" + URLEncoder.encode(elng, "UTF-8")
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&milli_date=" + URLEncoder.encode(milli, "UTF-8");
            // Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        gettype=12;
    }
    public sync_Mysql(String reg_date,String status,String memid,String milli){  //북마크 갱신(11)
        String dsync_url="http://sbway.cafe24.com/php/b_update.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "&reg_date=" + URLEncoder.encode(reg_date, "UTF-8")
                    + "&status=" + URLEncoder.encode(status, "UTF-8")
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&milli_date=" + URLEncoder.encode(milli, "UTF-8");
            // Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        gettype=11;
    }
    public sync_Mysql(String memid,String sync_time,Context mContext,String ck,String ck2){
        String dsync_url="http://sbway.cafe24.com/php/b_sqlite_insert.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&sync_time=" + URLEncoder.encode(sync_time, "UTF-8");
            Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        context=mContext;
        gettype=5;
    }
    public sync_Mysql(String memid,String sync_time,Context mContext,String ck,String ck2,String u){
        String dsync_url="http://sbway.cafe24.com/php/b_sqlite_update.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&sync_time=" + URLEncoder.encode(sync_time, "UTF-8");
            Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        context=mContext;
        gettype=6;
    }
    //스케줄
    public sync_Mysql(String milli,int syear,int smonth,int sday,String sday_week,String stime,int eyear,int emonth,int eday,String eday_week,String etime,
                      String title,String local,String explain,String alarm,String alarm_time,String repeat, String repeat_end,String reg_date,String status,String aw,String memid){
        String dsync_url="http://sbway.cafe24.com/php/s_insert.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "a_w=" + URLEncoder.encode(aw, "UTF-8")
                    + "&reg_date=" + URLEncoder.encode(reg_date, "UTF-8")
                    + "&status=" + URLEncoder.encode(status, "UTF-8")
                    + "&s_year=" + syear
                    + "&s_month=" + smonth
                    + "&s_day=" + sday
                    + "&s_week=" + URLEncoder.encode(sday_week, "UTF-8")
                    + "&s_time=" + URLEncoder.encode(stime, "UTF-8")
                    + "&e_year=" + eyear
                    + "&e_month=" + emonth
                    + "&e_day=" + eday
                    + "&e_week=" + URLEncoder.encode(eday_week, "UTF-8")
                    + "&e_time=" + URLEncoder.encode(etime, "UTF-8")
                    + "&alarm=" + URLEncoder.encode(alarm, "UTF-8")
                    + "&alarm_time=" + URLEncoder.encode(alarm_time, "UTF-8")
                    + "&s_repeat=" + URLEncoder.encode(repeat, "UTF-8")
                    + "&repeat_date=" + URLEncoder.encode(repeat_end, "UTF-8")
                    + "&title=" + URLEncoder.encode(title, "UTF-8")
                    + "&loc=" + URLEncoder.encode(local, "UTF-8")
                    + "&s_explain=" + URLEncoder.encode(explain, "UTF-8")
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&milli_date=" + URLEncoder.encode(milli, "UTF-8");
            Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        gettype=22;
    }
    public sync_Mysql(String milli,int syear,int smonth,int sday,String sday_week,String stime,int eyear,int emonth,int eday,String eday_week,String etime,
                      String title,String local,String explain,String alarm,String alarm_time,String repeat, String repeat_end,String reg_date,String status,String memid){
        String dsync_url="http://sbway.cafe24.com/php/s_update.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "&reg_date=" + URLEncoder.encode(reg_date, "UTF-8")
                    + "&status=" + URLEncoder.encode(status, "UTF-8")
                    + "&s_year=" + syear
                    + "&s_month=" + smonth
                    + "&s_day=" + sday
                    + "&s_week=" + URLEncoder.encode(sday_week, "UTF-8")
                    + "&s_time=" + URLEncoder.encode(stime, "UTF-8")
                    + "&e_year=" + eyear
                    + "&e_month=" + emonth
                    + "&e_day=" + eday
                    + "&e_week=" + URLEncoder.encode(eday_week, "UTF-8")
                    + "&e_time=" + URLEncoder.encode(etime, "UTF-8")
                    + "&alarm=" + URLEncoder.encode(alarm, "UTF-8")
                    + "&alarm_time=" + URLEncoder.encode(alarm_time, "UTF-8")
                    + "&s_repeat=" + URLEncoder.encode(repeat, "UTF-8")
                    + "&repeat_date=" + URLEncoder.encode(repeat_end, "UTF-8")
                    + "&title=" + URLEncoder.encode(title, "UTF-8")
                    + "&loc=" + URLEncoder.encode(local, "UTF-8")
                    + "&s_explain=" + URLEncoder.encode(explain, "UTF-8")
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&milli_date=" + URLEncoder.encode(milli, "UTF-8");
            Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        gettype=21;
    }
    public sync_Mysql(String memid,String sync_time,Context mContext,int ck){
        String dsync_url="http://sbway.cafe24.com/php/s_sqlite_insert.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&sync_time=" + URLEncoder.encode(sync_time, "UTF-8");
            Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        context=mContext;
        gettype=7;
    }
    public sync_Mysql(String memid,String sync_time,Context mContext,int ck,int ck2){
        String dsync_url="http://sbway.cafe24.com/php/s_sqlite_update.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "&memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&sync_time=" + URLEncoder.encode(sync_time, "UTF-8");
            Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
        context=mContext;
        gettype=8;
    }

    @Override
    public void run() {
        super.run();
        if(active){
            Log.e("gettype", gettype + "," + url);
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL phpUrl = new URL(url);

                HttpURLConnection conn = (HttpURLConnection)phpUrl.openConnection();

                if ( conn != null ) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ( true ) {
                            String line = br.readLine();
                            if ( line == null )
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            show(jsonHtml.toString());
        }
    }

    void show(final String result){
        mHandler.post(new Runnable(){
            @Override
            public void run() {
                Log.e("result",gettype+result);
                switch (gettype){
                    case 3:
                        Intent i = new Intent(context,DdayInsert.class);
                        i.putExtra("result",result);
                        context.startActivity(i);
                        break;
                    case 4:
                        Intent intent = new Intent(context,DdayUpdate.class);
                        intent.putExtra("result",result);
                        context.startActivity(intent);
                        break;
                    case 5:
                        Intent b_i = new Intent(context,BookmarkInsert.class);
                        b_i.putExtra("result",result);
                        context.startActivity(b_i);
                        break;
                    case 6:
                        Intent b_intent = new Intent(context,BookmarkUpdate.class);
                        b_intent.putExtra("result",result);
                        context.startActivity(b_intent);
                        break;
                    case 7:
                        Intent s_i = new Intent(context,ScheduleInsert.class);
                        s_i.putExtra("result",result);
                        context.startActivity(s_i);
                        break;
                    case 8:
                        Intent s_intent = new Intent(context,ScheduleUpdate.class);
                        s_intent.putExtra("result",result);
                        context.startActivity(s_intent);
                        break;
                    case 10:
                        HomeActivity.sync_result(result);
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
