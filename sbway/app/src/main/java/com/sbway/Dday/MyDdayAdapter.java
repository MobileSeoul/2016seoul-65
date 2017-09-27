package com.sbway.Dday;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbway.R;

import java.util.ArrayList;

/**
 * Created by PC on 2016-05-07.
 */

class ListInfo {
    TextView d_date; //d_year+d_month+d_day
    TextView d_title;
    TextView ddayres; //디데이 D-?
    ImageView imageView;
}

public class MyDdayAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<DdayInfo> listData;
    ListInfo info;
    SharedPreferences dday_sp;
    SharedPreferences.Editor d_editor;


    // 어답터 클래스의 생성자
    // 전달받은 context로부터 Inflate 객체를 얻어 오며
    // 리스트에 표시할 데이터를 전달받아 저장한다.
    public MyDdayAdapter( Context context, ArrayList<DdayInfo> data ) {
        inflater = LayoutInflater.from( context );
        this.context = context;
        listData = data;
    }

    // 목록의 개수를 되돌려 주는 함수
    public int getCount() {
        Log.d("dhkim", listData.size()+"");
        return listData.size();
    }

    // 해당 위치의 데이터를 되돌려 주는 함수
    public DdayInfo getItem(int position) {
        return listData.get( position );
    }

    // 해당 위치의 아이템 ID를 되돌려 주는 함수
    // 여기서는 position 값을 그대로 되돌려줌
    public long getItemId(int position) {
        return position;
    }

    // 해당 위치의 뷰 컨트롤을 되돌려 주는 함수
    public View getView(int position, View convertview, ViewGroup parent) {
        // 기존 View 객체를 얻어 옴
        View v = convertview;

        Activity act = (Activity)context;

        dday_sp = act.getSharedPreferences("dday", 0);
        d_editor = dday_sp.edit();
        int img_position = dday_sp.getInt("img_position",-1);

        if( v == null ) {
            info = new ListInfo();
            v = inflater.inflate( R.layout.dday_item, null );

            info.d_date = (TextView) v.findViewById(R.id.dday_date_17);
            String date = listData.get(position).d_year + "년 " + listData.get(position).d_month + "월 " + listData.get(position).d_day + "일";
            info.d_date.setText(date);

            info.d_title = (TextView) v.findViewById(R.id.dday_title_17);
            info.d_title.setText(listData.get(position).d_title);

            info.ddayres = (TextView) v.findViewById(R.id.dday_17);
            info.ddayres.setText(listData.get(position).ddayres);

            info.imageView = (ImageView) v.findViewById(R.id.dday_select);

            Log.d("ㄱㄱ", img_position + "");
            if (position == img_position) {
                info.imageView.setBackgroundResource(R.drawable.check);
            } else {
                info.imageView.setBackgroundColor(Color.WHITE);
            }

            v.setTag( info );
        }else if( ((ListInfo)v.getTag()).d_title.getText().toString() != listData.get( position ).d_title ) {
            info = new ListInfo();
            v = inflater.inflate(R.layout.dday_item, null);

            info.d_date = (TextView) v.findViewById(R.id.dday_date_17);
            String date = listData.get(position).d_year + "년 " + listData.get(position).d_month + "월 " + listData.get(position).d_day + "일";
            info.d_date.setText(date);

            info.d_title = (TextView) v.findViewById(R.id.dday_title_17);
            info.d_title.setText(listData.get(position).d_title);

            info.ddayres = (TextView) v.findViewById(R.id.dday_17);
            info.ddayres.setText(listData.get(position).ddayres);

            info.imageView = (ImageView) v.findViewById(R.id.dday_select);
            if (position == img_position) {
                info.imageView.setBackgroundResource(R.drawable.check);
            } else {
                info.imageView.setBackgroundColor(Color.WHITE);
            }

            v.setTag(info);
        }
        return v;
    }

    // 목록 정보를 설정하는 함수
    public void setArrayList(ArrayList<DdayInfo> arrays) {
        listData = arrays;
    }

    // 목록 정보를 되돌려 주는 함수
    public ArrayList<DdayInfo> getArrayList() {
        return listData;
    }
}
