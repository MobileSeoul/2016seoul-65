package com.sbway.Sync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sbway.Schedule.DBHandler_schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PC on 2016-10-18.
 */
public class ScheduleInsert extends Activity {
    DBHandler_schedule s_handler;
    String[] aw,reg_date,status,title,syear,smonth,sday,sday_week,stime,eyear,emonth,eday,eday_week,etime,location,explain,alarm,alarm_time,repeat,repeat_end,memid,milli_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        s_handler = new DBHandler_schedule(getApplicationContext());
        Intent i = getIntent();
        String result = i.getStringExtra("result");
        s_sync(result);
    }

    protected void s_insert(){
        for(int i=0;i<milli_date.length;i++){
            s_handler.insert(memid[i],milli_date[i],title[i],syear[i],smonth[i],sday[i],sday_week[i],stime[i],eyear[i],emonth[i],eday[i],eday_week[i],etime[i],location[i],explain[i],alarm[i],alarm_time[i],repeat[i],repeat_end[i],reg_date[i],status[i],aw[i]);
        }
        Toast.makeText(getApplicationContext(), "동기화가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void s_sync(String result){ //디데이 추가하기위한 사전 작업
        try{
            JSONArray jarray = new JSONArray(result);
            aw = new String[jarray.length()];
            reg_date = new String[jarray.length()];
            status = new String[jarray.length()];
            title = new String[jarray.length()];
            syear = new String[jarray.length()];
            smonth = new String[jarray.length()];
            sday = new String[jarray.length()];
            sday_week = new String[jarray.length()];
            stime = new String[jarray.length()];
            eyear = new String[jarray.length()];
            emonth = new String[jarray.length()];
            eday = new String[jarray.length()];
            eday_week = new String[jarray.length()];
            etime = new String[jarray.length()];
            location = new String[jarray.length()];
            explain = new String[jarray.length()];
            alarm = new String[jarray.length()];
            alarm_time = new String[jarray.length()];
            repeat = new String[jarray.length()];
            repeat_end = new String[jarray.length()];
            memid = new String[jarray.length()];
            milli_date = new String[jarray.length()];

            for(int i=0; i < jarray.length(); i++){
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                aw[i] = jObject.get("a_w").toString();
                reg_date[i] =jObject.get("reg_date").toString();
                Log.d("레그데이트",reg_date[i]);
                status[i] = jObject.get("status").toString();
                title[i] = jObject.get("title").toString();
                syear[i] = jObject.get("s_year").toString();
                smonth[i] = jObject.get("s_month").toString();
                sday[i] = jObject.get("s_day").toString();
                sday_week[i] = jObject.get("s_week").toString();
                stime[i] = jObject.get("s_time").toString();
                eyear[i] = jObject.get("e_year").toString();
                emonth[i] = jObject.get("e_month").toString();
                eday[i] = jObject.get("e_day").toString();
                eday_week[i] = jObject.get("e_week").toString();
                etime[i] = jObject.get("e_time").toString();
                location[i] = jObject.get("loc").toString();
                explain[i] = jObject.get("s_explain").toString();
                alarm[i] = jObject.get("alarm").toString();
                alarm_time[i] = jObject.get("alarm_time").toString();
                repeat[i] = jObject.get("s_repeat").toString();
                repeat_end[i] = jObject.get("repeat_date").toString();
                memid[i] = jObject.get("memid").toString();
                milli_date[i] = jObject.get("milli_date").toString();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        s_insert();
    }
}
