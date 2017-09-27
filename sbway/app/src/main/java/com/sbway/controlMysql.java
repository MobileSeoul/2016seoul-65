package com.sbway;

import android.os.Handler;
import android.util.Log;

import com.sbway.Login.LoginActivity;
import com.sbway.SignUp.SignUpActivity;

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
public class controlMysql extends Thread {

    public static boolean active=false;
    Handler mHandler;
    String url=null;
    int gettype=0;


    public controlMysql(String id, String name, String age, String phone, String mail, String address){ //사용자 정보 업데이트 (6 String)
        mHandler=new Handler();
        String userinfo_url="http://118.47.55.71/android/updateUser.php?id=";
        String userId=id;
        String nameuser="&name="+name;
        String ageuser="&age="+age;
        String phoneuser="&phone="+phone;
        String mailuser="&mail="+mail;
        String addressuser="&address="+address;
        url=userinfo_url+userId+nameuser+ageuser+phoneuser+mailuser+addressuser;
        gettype=0;
    }

    public controlMysql(String status, String memid, String passwd, String question, String answer, String name, String email){  //사용자 가입 (7 String)
        String insert_userinfo_url="http://sbway.cafe24.com/php/memjoin.php?";
        mHandler=new Handler();
        try {
            url = insert_userinfo_url
                    + "name=" + URLEncoder.encode(name, "UTF-8")
                    + "&id=" + URLEncoder.encode(memid, "UTF-8")
                    + "&password=" + URLEncoder.encode(passwd, "UTF-8")
                    + "&question=" + URLEncoder.encode(question, "UTF-8")
                    + "&answer=" + URLEncoder.encode(answer, "UTF-8")
                    + "&email=" + URLEncoder.encode(email, "UTF-8")
                    + "&a_w=" + URLEncoder.encode("a", "UTF-8")
                    + "&status=" + URLEncoder.encode(status, "UTF-8");
        }catch (Exception e) {
            e.printStackTrace();
        }
        gettype=3;
    }

    public controlMysql(String id, String no, int type){     //비밀번호 정보 업데이트,장비삭제,특정일 이벤트 가져오기 (2 String)
        String pwinfo_url="http://118.47.55.71/android/updatePw.php?id=";
        mHandler=new Handler();
        String userId=id;

        Log.e("active", active + "");

        switch(type){
            case 0:
                String userPw="&pw="+no;
                url=pwinfo_url+userId+userPw;
                gettype=2;
                break;
        }


    }

    public controlMysql(String id,int type){     //사용자/장비 정보 가져오기/idchk/event가져오기/drel table 생성 (1 String)
        String userinfo_url="http://118.47.55.71/android/getuserinfo.php?id=";
        String idchk_url="http://118.47.55.71/android/chkid.php?id=";
        mHandler=new Handler();
        String userId=id;

        switch(type){
            case 0:
                url=userinfo_url+userId;
                gettype=6;
                break;
            case 2:
                url=idchk_url+userId;
                gettype=8;
                break;
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
            Log.e("gettype",gettype+","+url);
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
                        SignUpActivity.regist_result(result);
                        break;
                    case 8:
                        //chkId.chkidresult(result);
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
