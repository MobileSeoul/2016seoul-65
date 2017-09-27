package com.sbway.Login;

import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by PC on 2016-09-20.
 */
public class findpwMysql extends Thread {

    public static boolean active=false;

    Handler mHandler;
    String userId=null,userPw=null,url=null;
    String findpw_url="http://sbway.cafe24.com/php/findpw.php?";

    public findpwMysql(String id, String name, String email, String question, String answer){
        mHandler=new Handler();
        try {
            url = findpw_url
                    + "name=" + URLEncoder.encode(name, "UTF-8")
                    + "&id=" + URLEncoder.encode(id, "UTF-8")
                    + "&email=" + URLEncoder.encode(email, "UTF-8")
                    + "&question=" + URLEncoder.encode(question, "UTF-8")
                    + "&answer=" + URLEncoder.encode(answer, "UTF-8");
            Log.e("url", url);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calls the <code>run()</code> method of the Runnable object the receiver
     * holds. If no Runnable is set, does nothing.
     *
     * @see Thread#start
     */
    @Override
    public void run() {
        super.run();
        if(active){
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
                            if ( line == null ) {
                                jsonHtml.append("null");
                                break;
                            }
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
                if(result.equals("null")){
                    LoginActivity.findpw_result("false");
                }else {
                    LoginActivity.findpw_result("true");
                }

            }
        });

    }


}
