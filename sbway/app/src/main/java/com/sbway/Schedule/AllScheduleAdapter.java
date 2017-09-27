package com.sbway.Schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sbway.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by PC on 2016-05-16.
 */
class AllSListInfo {
    TextView as_time;
    TextView as_title;
    TextView as_exp;
    TextView as_date;
}

public class AllScheduleAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<ScheduleInfo> listData;
    AllSListInfo info;
    String date1="";
    String date2="";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

    // 어답터 클래스의 생성자
    // 전달받은 context로부터 Inflate 객체를 얻어 오며
    // 리스트에 표시할 데이터를 전달받아 저장한다.
    public AllScheduleAdapter( Context context, ArrayList<ScheduleInfo> data ) {
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

        if( v == null ) {
            // 만약 기존 뷰가 생성되지 않았다면
            // 정보 저장용 객체를 생성하고
            info = new AllSListInfo();

            // XML로부터 목룍에 표시할 레이아웃 객체를 생성한 후
            v = inflater.inflate( R.layout.allschedule_list, null );

            info.as_date = (TextView)v.findViewById(R.id.as_date);
            info.as_time = (TextView)v.findViewById( R.id.as_time );
            info.as_title = (TextView)v.findViewById( R.id.as_title );
            info.as_exp = (TextView)v.findViewById( R.id.as_exp );

            if (position == 0) {
                date1 = (listData.get(position).s_syear) + "년 " + (listData.get(position).s_smonth) + "월 " + (listData.get(position).s_sday) + "일 ";
                info.as_date.setText(date1);
                info.as_time.setText((listData.get(position).s_stime) + "\n-\n" + (listData.get(position).s_etime));
                info.as_title.setText(listData.get(position).stitle);
                info.as_exp.setText(listData.get(position).sexplain);
            } else {
                date1 = (listData.get(position - 1).s_syear) + "년 " + (listData.get(position - 1).s_smonth) + "월 " + (listData.get(position - 1).s_sday) + "일 ";
                date2 = (listData.get(position).s_syear) + "년 " + (listData.get(position).s_smonth) + "월 " + (listData.get(position).s_sday) + "일 ";
                if (date1.equals(date2)) {
                    info.as_date.setVisibility(View.GONE);
                    info.as_time.setText((listData.get(position).s_stime) + "\n-\n" + (listData.get(position).s_etime));
                    info.as_title.setText(listData.get(position).stitle);
                    info.as_exp.setText(listData.get(position).sexplain);
                } else {
                    info.as_date.setText(date2);
                    info.as_time.setText((listData.get(position).s_stime) + "\n-\n" + (listData.get(position).s_etime));
                    info.as_title.setText(listData.get(position).stitle);
                    info.as_exp.setText(listData.get(position).sexplain);
                }
            }

            // 뷰의 태그로 설정함
            v.setTag( info );
        }
        else if( ((AllSListInfo)v.getTag()).as_title.getText().toString() != listData.get( position ).stitle ) {
            // 기존 뷰는 있으나 뷰의 테그로 설정된 정보와 목록 원본의 메모 내용이 다를 경우
            // 위와 동일한 방법으로 뷰를 새로 만들어 설정함
            info = new AllSListInfo();

            // XML로부터 목룍에 표시할 레이아웃 객체를 생성한 후
            v = inflater.inflate( R.layout.allschedule_list, null );

            info.as_date = (TextView)v.findViewById(R.id.as_date);
            info.as_time = (TextView)v.findViewById( R.id.as_time );
            info.as_title = (TextView)v.findViewById( R.id.as_title );
            info.as_exp = (TextView)v.findViewById( R.id.as_exp );

            if (position == 0) {
                date1 = (listData.get(position).s_syear) + "년 " + (listData.get(position).s_smonth) + "월 " + (listData.get(position).s_sday) + "일 ";
                info.as_date.setText(date1);
                info.as_time.setText((listData.get(position).s_stime) + "\n-\n" + (listData.get(position).s_etime));
                info.as_title.setText(listData.get(position).stitle);
                info.as_exp.setText(listData.get(position).sexplain);
            } else {
                date1 = (listData.get(position - 1).s_syear) + "년 " + (listData.get(position - 1).s_smonth) + "월 " + (listData.get(position - 1).s_sday) + "일 ";
                date2 = (listData.get(position).s_syear) + "년 " + (listData.get(position).s_smonth) + "월 " + (listData.get(position).s_sday) + "일 ";
                if (date1.equals(date2)) {
                    info.as_date.setVisibility(View.GONE);
                    info.as_time.setText((listData.get(position).s_stime) + "\n-\n" + (listData.get(position).s_etime));
                    info.as_title.setText(listData.get(position).stitle);
                    info.as_exp.setText(listData.get(position).sexplain);
                } else {
                    info.as_date.setText(date2);
                    info.as_time.setText((listData.get(position).s_stime) + "\n-\n" + (listData.get(position).s_etime));
                    info.as_title.setText(listData.get(position).stitle);
                    info.as_exp.setText(listData.get(position).sexplain);
                }
            }
            /*date1 = (listData.get(position).s_syear) + "년 " + (listData.get(position).s_smonth) + "월 " + (listData.get(position).s_sday) + "일 ";
            date2 = (listData.get(position).s_eyear) + "년 " + (listData.get(position).s_emonth) + "월 " + (listData.get(position).s_eday) + "일 ";

            try{
                Date sdate = simpleDateFormat.parse(date1);
                Date edate = simpleDateFormat.parse(date2);
                long diff = edate.getTime()-sdate.getTime();
                long diffday = diff/(24*60*60*1000);

                if(listData.get(position).srepeat_end.equals("0")){

                }else {

                }
            }catch (Exception e){
                e.printStackTrace();
            }*/

            // / 뷰의 태그로 설정함
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
