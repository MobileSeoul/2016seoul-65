package com.sbway.Sync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.sbway.Bookmark.DBHandler_Bookmark;
import com.sbway.Dday.DBHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PC on 2016-10-11.
 */
public class BookmarkInsert extends Activity {
    DBHandler_Bookmark b_handler;
    String[] aw, reg_date, status, b_path, start_place, end_place, s_lng, s_lat, e_lng, e_lat, memid, milli_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b_handler = new DBHandler_Bookmark(getApplicationContext());
        Intent i = getIntent();
        String result = i.getStringExtra("result");
        b_sync(result);
    }

    protected void b_insert() {
        for (int i = 0; i < milli_date.length; i++) {
            b_handler.insert(milli_date[i], memid[i], b_path[i], start_place[i], end_place[i], s_lat[i], s_lng[i], e_lat[i], e_lng[i], reg_date[i], status[i], aw[i]);
        }
        Toast.makeText(getApplicationContext(), "동기화가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void b_sync(String result) { //디데이 추가하기위한 사전 작업
        try {
            JSONArray jarray = new JSONArray(result);
            aw = new String[jarray.length()];
            reg_date = new String[jarray.length()];
            status = new String[jarray.length()];
            b_path = new String[jarray.length()];
            start_place = new String[jarray.length()];
            end_place = new String[jarray.length()];
            s_lng = new String[jarray.length()];
            s_lat = new String[jarray.length()];
            e_lng = new String[jarray.length()];
            e_lat = new String[jarray.length()];
            memid = new String[jarray.length()];
            milli_date = new String[jarray.length()];

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                aw[i] = jObject.get("a_w").toString();
                reg_date[i] = jObject.get("reg_date").toString();
                status[i] = jObject.get("status").toString();
                b_path[i] = jObject.get("b_path").toString();
                start_place[i] = jObject.get("start_place").toString();
                end_place[i] = jObject.get("end_place").toString();
                s_lng[i] = jObject.get("s_lng").toString();
                s_lat[i] = jObject.get("s_lat").toString();
                e_lng[i] = jObject.get("e_lng").toString();
                e_lat[i] = jObject.get("e_lat").toString();
                memid[i] = jObject.get("memid").toString();
                milli_date[i] = jObject.get("milli_date").toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        b_insert();
    }
}
