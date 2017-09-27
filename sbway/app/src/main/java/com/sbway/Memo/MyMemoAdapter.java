package com.sbway.Memo;

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
 * Created by PC on 2016-05-07.
 */

class  MListInfo {
    TextView writeDate;
    TextView writeTime;
    TextView memo;
    TextView local;
}

public class MyMemoAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<MemoInfo> listData;
    MListInfo info;

    // 어답터 클래스의 생성자
    // 전달받은 context로부터 Inflate 객체를 얻어 오며
    // 리스트에 표시할 데이터를 전달받아 저장한다.
    public MyMemoAdapter( Context context, ArrayList<MemoInfo> data ) {
        inflater = LayoutInflater.from( context );
        this.context = context;
        listData = data;
    }

    // 목록의 개수를 되돌려 주는 함수
    public int getCount() {
        return listData.size();
    }

    // 해당 위치의 데이터를 되돌려 주는 함수
    public MemoInfo getItem(int position) {
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
        String am_pm ="";

        // 날짜를 표시하기 위한 Calendar 객체 생성
        Calendar cal = Calendar.getInstance();
        if( v == null ) {
            // 만약 기존 뷰가 생성되지 않았다면
            // 정보 저장용 객체를 생성하고
            info = new MListInfo();

            // XML로부터 목룍에 표시할 레이아웃 객체를 생성한 후
            v = inflater.inflate( R.layout.memo_itemview, null );

            // 생성된 레이아웃 객체로부터 날짜 저장요 텍스트 뷰 객체 정보를 가져와
            info.writeDate = (TextView)v.findViewById( R.id.date_16 );
            info.writeTime = (TextView)v.findViewById( R.id.date_16_2 );

            // 날짜객체를 이용하여 년월일을 구분하여 화면에 표시할 수 있도록
            // 가공하여 그 텍스트를 뷰에 설정한다.
            cal.setTimeInMillis( listData.get( position ).writeDate );

            am_pm = cal.get(Calendar.AM_PM)==0? "AM":"PM";
            info.writeDate.setText( cal.get( Calendar.YEAR ) + "년 " + (cal.get( Calendar.MONTH ) + 1) + "월 " + cal.get( Calendar.DAY_OF_MONTH ) + "일 ");
            info.writeTime.setText(am_pm + " " + cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE));

            // 레이아웃 객체로부터 메모 내용을 저장하기 위한 텍스트 뷰 객체 정보를 가져와
            // 메모 내용을 설정하고
            info.memo = (TextView)v.findViewById( R.id.contents_16 );
            info.memo.setText( listData.get( position ).memo );
            info.local = (TextView)v.findViewById( R.id.location_16 );
            info.local.setText(listData.get( position ).local);
            // 뷰의 태그로 설정함
            v.setTag( info );
        }
        else if( ((MListInfo)v.getTag()).memo.getText().toString() != listData.get( position ).memo ) {
            // 기존 뷰는 있으나 뷰의 테그로 설정된 정보와 목록 원본의 메모 내용이 다를 경우
            // 위와 동일한 방법으로 뷰를 새로 만들어 설정함
            info = new MListInfo();
            v = inflater.inflate( R.layout.memo_itemview, null );
            info.writeDate = (TextView)v.findViewById( R.id.date_16 );
            info.writeTime = (TextView)v.findViewById( R.id.date_16_2 );
            cal.setTimeInMillis( listData.get( position ).writeDate );

            am_pm = cal.get(Calendar.AM_PM)==0? "AM":"PM";
            info.writeDate.setText( cal.get( Calendar.YEAR ) + "년 " + (cal.get( Calendar.MONTH ) + 1) + "월 " + cal.get( Calendar.DAY_OF_MONTH ) + "일 ");
            info.writeTime.setText(am_pm + " " + cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE));

            info.memo = (TextView)v.findViewById( R.id.contents_16 );
            info.memo.setText( listData.get( position ).memo );
            info.local = (TextView)v.findViewById( R.id.location_16 );
            info.local.setText( listData.get( position ).local );
            v.setTag( info );
        }
        return v;
    }

    // 목록 정보를 설정하는 함수
    public void setArrayList(ArrayList<MemoInfo> arrays) {
        listData = arrays;
    }

    // 목록 정보를 되돌려 주는 함수
    public ArrayList<MemoInfo> getArrayList() {
        return listData;
    }
}
