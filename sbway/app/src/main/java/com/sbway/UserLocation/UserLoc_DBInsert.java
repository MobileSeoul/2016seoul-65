package com.sbway.UserLocation;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.sbway.HomeActivity;
import com.sbway.Sync.BookmarkInsert;
import com.sbway.Sync.BookmarkUpdate;
import com.sbway.Sync.DdayInsert;
import com.sbway.Sync.DdayUpdate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by PC on 2016-10-17.
 */
public class UserLoc_DBInsert extends Thread {
    Handler mHandler;
    String url=null;
    public UserLoc_DBInsert(String memid, Double lng, Double lat, String reg_date){  //디데이 추가(10)
        String dsync_url="http://sbway.cafe24.com/php/userloc.php?";
        mHandler=new Handler();
        try {
            url = dsync_url
                    + "memid=" + URLEncoder.encode(memid, "UTF-8")
                    + "&lng=" + lng
                    + "&lat=" + lat
                    + "&reg_date=" + URLEncoder.encode(reg_date, "UTF-8");
            // Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
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

    void show(final String result){
        mHandler.post(new Runnable(){
            @Override
            public void run() {
                HomeActivity.sync_result(result);
            }
        });
    }
}
