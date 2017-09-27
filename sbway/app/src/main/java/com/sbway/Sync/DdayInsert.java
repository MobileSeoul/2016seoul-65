package com.sbway.Sync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sbway.Dday.DBHandler;
import com.sbway.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DdayInsert extends Activity {
    DBHandler d_handler;
    String[] d_aw,d_reg_date,d_status,d_title,d_memid,d_milli_date;
    int[] d_year,d_month,d_day,d_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        d_handler = new DBHandler(getApplicationContext());
        Intent i = getIntent();
        String result = i.getStringExtra("result");
        d_sync(result);
    }

    protected void d_insert(){
        for (int i = 0; i < d_milli_date.length; i++) {
            Log.d("dd", d_milli_date[i]);
            d_handler.insert(d_milli_date[i], d_memid[i], d_year[i], d_month[i], d_day[i], d_title[i], d_type[i], d_reg_date[i], d_status[i], d_aw[i]);
        }
        Toast.makeText(getApplicationContext(),"동기화가 완료되었습니다.",Toast.LENGTH_SHORT).show();
        finish();
    }

    public void d_sync(String result){ //디데이 추가하기위한 사전 작업
        Log.d("동기화",result);
        try{
            JSONArray jarray = new JSONArray(result);
            d_aw = new String[jarray.length()];
            d_reg_date = new String[jarray.length()];
            d_status = new String[jarray.length()];
            d_year = new int[jarray.length()];
            d_month = new int[jarray.length()];
            d_day = new int[jarray.length()];
            d_title = new String[jarray.length()];
            d_type = new int[jarray.length()];
            d_memid = new String[jarray.length()];
            d_milli_date = new String[jarray.length()];

            for(int i=0; i < jarray.length(); i++){
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                d_aw[i] = jObject.get("a_w").toString();
                d_reg_date[i] =jObject.get("reg_date").toString();
                d_status[i] = jObject.get("status").toString();
                d_year[i] = Integer.parseInt(jObject.get("d_year").toString());
                d_month[i] = Integer.parseInt(jObject.get("d_month").toString());
                d_day[i] = Integer.parseInt(jObject.get("d_day").toString());
                d_title[i] = jObject.get("d_title").toString();
                d_type[i] = Integer.parseInt(jObject.get("type").toString());
                d_memid[i] = jObject.get("memid").toString();
                d_milli_date[i] = jObject.get("milli_date").toString();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        d_insert();
    }
}
