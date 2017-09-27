package com.sbway.Schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sbway.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by PC on 2016-05-16.
 */
class SListInfo {
    TextView startDate;
    TextView endDate;
    TextView title;
    TextView location;
}
/*
public class MyScheduleAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<ScheduleInfo> listData;
    SListInfo info;

    // 어답터 클래스의 생성자
    // 전달받은 context로부터 Inflate 객체를 얻어 오며
    // 리스트에 표시할 데이터를 전달받아 저장한다.
    public MyScheduleAdapter( Context context, ArrayList<ScheduleInfo> data ) {
        inflater = LayoutInflater.from( context );
        this.context = context;
        listData = data;
    }

    // 목록의 개수를 되돌려 주는 함수
    public int getCount() {
        return listData.size();
    }

    // 해당 위치의 데이터를 되돌려 주는 함수
    public ScheduleInfo getItem(int position) {
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

        // 날짜를 표시하기 위한 Calendar 객체 생성
        Calendar cal = Calendar.getInstance();
        if( v == null ) {
            // 만약 기존 뷰가 생성되지 않았다면
            // 정보 저장용 객체를 생성하고
            info = new SListInfo();

            // XML로부터 목룍에 표시할 레이아웃 객체를 생성한 후
            v = inflater.inflate( R.layout.day_plan, null );

            info.startDate = (TextView)v.findViewById( R.id.time_start_8 );
            String stime = listData.get(position).startDate;
            int a1 = stime.indexOf('오');
            info.startDate.setText(stime.substring(a1));
            info.endDate = (TextView)v.findViewById( R.id.time_end_8 );
            String etime = listData.get(position).endDate;
            int a2 = etime.indexOf('오');
            info.endDate.setText(etime.substring(a2));
            info.title = (TextView)v.findViewById( R.id.plan_8 );
            info.title.setText(listData.get( position ).stitle );
            info.location = (TextView)v.findViewById( R.id.location_8 );
            info.location.setText( listData.get( position ).local );
            // 뷰의 태그로 설정함
            v.setTag( info );
        }
        else if(((SListInfo)v.getTag()).title.getText().toString() != listData.get( position ).stitle ) {
            // 기존 뷰는 있으나 뷰의 테그로 설정된 정보와 목록 원본의 메모 내용이 다를 경우
            // 위와 동일한 방법으로 뷰를 새로 만들어 설정함
            info = new SListInfo();
            v = inflater.inflate( R.layout.day_plan, null );
            info.startDate = (TextView)v.findViewById( R.id.time_start_8 );
            String stime = listData.get(position).startDate;
            int a1 = stime.indexOf('오');
            info.startDate.setText(stime.substring(a1));
            info.endDate = (TextView)v.findViewById( R.id.time_end_8 );
            String etime = listData.get(position).endDate;
            int a2 = etime.indexOf('오');
            info.endDate.setText(etime.substring(a2));
            info.title = (TextView)v.findViewById( R.id.plan_8 );
            info.title.setText(listData.get( position ).stitle );
            info.location = (TextView)v.findViewById( R.id.location_8 );
            info.location.setText( listData.get( position ).local );
            v.setTag( info );
        }
        return v;
    }

    // 목록 정보를 설정하는 함수
    public void setArrayList(ArrayList<ScheduleInfo> arrays) {
        listData = arrays;
    }

    // 목록 정보를 되돌려 주는 함수
    public ArrayList<ScheduleInfo> getArrayList() {
        return listData;
    }
}

*/
