package com.sbway;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.sbway.Login.LoginActivity;
import com.sbway.Schedule.AllscheduleActivity;
import com.sbway.Schedule.DBHandler_schedule;
import com.sbway.Schedule.ScheduleAddActivity;
import com.sbway.Schedule.ScheduleInfo;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarActivity extends Fragment {
    public static String TAG="dhkim";

    CompactCalendarView compactCalendarView;
    SimpleDateFormat curYearFormat, curMonthFormat, curDayFormat;
    SimpleDateFormat viewYearFormat, viewMonthFormat, viewDayFormat;
    Date date;
    Calendar cc;
    Calendar cc2;
    TextView today;

    //달력 날짜 이동
    TextView calendar_date;
    static final int DATE_DIALOG_ID_1 = 0;
    int mYear, mMonth, mDay;
    SimpleDateFormat frmDate;
    String get_date="";

    int curYear, curMonth,curDay;
    int viewYear, viewMonth, viewDay;

    ArrayList<String> arData;
    ArrayAdapter<String> adapter;

    DBHandler_schedule handlerSchedule;

    //멤버 아이디 저장
    SharedPreferences auto_login;
    String member_id="";

    //DB에서 가져온 값 저장
    String gtxt="", get_mdate="", get_repeat="", get_repeat_end="";
    String get_reg_date="0000-00-00 00:00:00";
    int get_syear=0, get_smonth=0, get_sday=0, get_eyear=0, get_emonth=0, get_eday=0;
    int c_a=0;

    String[] split_getdata;
    String input_getdata="";

    //날씨
    ImageView imageView;
    String weather="";
    String temp="";
    TextView weather_txt;

    LinearLayout all_btn;

    //달력 업데이트 변수
    int u_syear, u_smonth, u_sday, u_eyear, u_emonth, u_eday, eventIndex;
    String u_repeat="", u_erepeat="", u_millidate="", c_state="";

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.activity_calendar,container,false);

        handlerSchedule = new DBHandler_schedule(getActivity());
        auto_login = getActivity().getSharedPreferences("setting", 0);
        member_id = auto_login.getString("id", "null");

        all_btn = (LinearLayout)v.findViewById(R.id.allschedule_btn);
        all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AllscheduleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        compactCalendarView = (CompactCalendarView)v.findViewById(R.id.compactcalendar_view);

        imageView = (ImageView)v.findViewById(R.id.calendar_weather);
        weather_txt = (TextView)v.findViewById(R.id.weather_txt);
        SharedPreferences spr = getActivity().getSharedPreferences("spr", Context.MODE_PRIVATE);
        if(spr!=null){
            weather=spr.getString("weather","");
            temp=spr.getString("temp","");
            weather_txt.setText(" / "+temp);
            weatherImg();
        }

        today = (TextView)v.findViewById(R.id.calendar_date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 M월 dd일");
        today.setText(simpleDateFormat.format(new Date()));

        calendar_date = (TextView)v.findViewById(R.id.calendar_txt);
        calendar_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), mDateSetListener, viewYear, viewMonth-1, viewDay);
                dialog.show();
            }
        });

        //#dhkim member field 초기화
        init_memberfield();
        //-------------------------
        calendar_date.setText(viewYear+"년 "+viewMonth+"월");

        //#리스트뷰 세팅 -----------------------------------------------
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arData);
        ((ListView) v.findViewById(R.id.calendar_list)).setAdapter(adapter);
        //-------------------------------------------------------------

        //달력에 리스트뷰 클릭 시 이벤트
        ((ListView) v.findViewById(R.id.calendar_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cc.set(viewYear, viewMonth, viewDay);
                if(arData.get(i).toString().equals("┼ 일정을 등록하세요.")){
                    if(member_id.equals("null")){
                        final LovelyStandardDialog loginDialog = new LovelyStandardDialog(getActivity());
                        loginDialog.setTopColorRes(R.color.schedule)
                                .setTopTitle("SCHEDULE")
                                .setTopTitleColor(Color.WHITE)
                                .setIcon(R.drawable.alert_info)
                                .setIconTintColor(Color.WHITE)
                                .setMessage("로그인 후 이용가능합니다. 로그인 창으로 이동하시겠습니까?")
                                .setPositiveButton("확인", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("취소", null)
                                .show();
                    }else{
                        Intent intent = new Intent(getActivity(), ScheduleAddActivity.class);
                        intent.putExtra("schedule_year", Integer.valueOf(viewYear));
                        intent.putExtra("schedule_month", Integer.valueOf(viewMonth));
                        intent.putExtra("schedule_day", Integer.valueOf(viewDay));
                        intent.putExtra("state", "insert");
                        startActivity(intent);
                    }
                }else {
                    //일정수정 액티비티 이동
                    String[] put_data = input_getdata.split("\n");
                    Intent intent = new Intent(getActivity(), ScheduleAddActivity.class);
                    intent.putExtra("schedule_year", Integer.valueOf(viewYear));
                    intent.putExtra("schedule_month", Integer.valueOf(viewMonth));
                    intent.putExtra("schedule_day", Integer.valueOf(viewDay));
                    intent.putExtra("s_primary", put_data[i]);
                    intent.putExtra("eventIndex", i);
                    intent.putExtra("state", "update");
                    startActivityForResult(intent, 10);
                }
            }
        });

        compactCalendarView.setShouldShowMondayAsFirstDay(false);

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                checkDeleteDBData(u_millidate);
                reloadlistviewdata();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                reloadlistviewdata();
            }
        });

        v.findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compactCalendarView.showNextMonth();
                checkDeleteDBData(u_millidate);
                reloadlistviewdata();
            }
        });
        v.findViewById(R.id.prev_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compactCalendarView.showPreviousMonth();
                checkDeleteDBData(u_millidate);
                reloadlistviewdata();
            }
        });

        v.findViewById(R.id.insert_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cc.set(viewYear, viewMonth, viewDay);
                if(member_id.equals("null")){
                    final LovelyStandardDialog loginDialog = new LovelyStandardDialog(getActivity());
                    loginDialog.setTopColorRes(R.color.schedule)
                            .setTopTitle("SCHEDULE")
                            .setTopTitleColor(Color.WHITE)
                            .setIcon(R.drawable.alert_info)
                            .setIconTintColor(Color.WHITE)
                            .setMessage("로그인 후 이용가능합니다. 로그인 창으로 이동하시겠습니까?")
                            .setPositiveButton("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }else {
                    //일정추가 액티비티 이동
                    Intent intent = new Intent(getActivity(), ScheduleAddActivity.class);
                    intent.putExtra("schedule_year", Integer.valueOf(viewYear));
                    intent.putExtra("schedule_month", Integer.valueOf(viewMonth));
                    intent.putExtra("schedule_day", Integer.valueOf(viewDay));
                    intent.putExtra("state", "insert");
                    startActivity(intent);
                }
            }
        });

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);

                viewYear = Integer.parseInt(viewYearFormat.format(dateClicked));
                viewMonth = Integer.parseInt(viewMonthFormat.format(dateClicked));
                viewDay = Integer.parseInt(viewDayFormat.format(dateClicked));

                arData.clear();
                if(compactCalendarView.getEvents(dateClicked).size()==0){
                    arData.add("┼ 일정을 등록하세요.");
                    adapter.notifyDataSetChanged();
                }else{
                    reloadlistviewdata();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                viewYear = Integer.parseInt(viewYearFormat.format(firstDayOfNewMonth));
                viewMonth = Integer.parseInt(viewMonthFormat.format(firstDayOfNewMonth));
                viewDay = Integer.parseInt(viewDayFormat.format(firstDayOfNewMonth));

                //setTextView(viewYear, viewMonth);
                ((TextView)getActivity().findViewById(R.id.calendar_txt)).setText(viewYear+"년 "+viewMonth+"월");
                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                checkDeleteDBData(u_millidate);
                reloadlistviewdata();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        displayScheduleList();
        displayDeleteScheduleList();
        reloadlistviewdata();
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==10) {
            u_millidate = data.getStringExtra("d_millidate");
            u_syear = data.getIntExtra("d_syear", 0);
            u_smonth = data.getIntExtra("d_smonth", 0);
            u_sday = data.getIntExtra("d_sday", 0);
            u_eyear = data.getIntExtra("d_eyear", 0);
            u_emonth = data.getIntExtra("d_emonth", 0);
            u_eday = data.getIntExtra("d_eday", 0);
            u_repeat = data.getStringExtra("d_repeat");
            u_erepeat = data.getStringExtra("d_erepeat");
            eventIndex = data.getIntExtra("eventIndex", 0);
            c_state = data.getStringExtra("c_state");
            if (c_state.equals("update")) {
                checkDeleteDBData(u_millidate);
            }
        }
    }

    //dhkim 멤버변수 초기화
    void init_memberfield(){
        //#dhkim
        arData=new ArrayList<String>();

        date = new Date(System.currentTimeMillis());
        cc = Calendar.getInstance();
        cc2 = Calendar.getInstance();

        //현재의 연월일 저장
        curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //화면에 보이는 연월일 저장
        viewYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        viewMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        viewDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //int로 현재 날짜 저장
        curYear = Integer.parseInt(curYearFormat.format(date));
        curMonth = Integer.parseInt(curMonthFormat.format(date));
        curDay = Integer.parseInt(curDayFormat.format(date));

        //int로 현재 보이는 날짜 지정
        viewYear = curYear;
        viewMonth = curMonth;
        viewDay = curDay;
    }

    //#dhkim 데이터 추가될 때마다 리스트 내용 추가
    void reloadlistviewdata(){
        input_getdata = "";
        cc.set(viewYear,viewMonth-1,viewDay);
        List<Event> events = compactCalendarView.getEvents(cc.getTime());
        arData.clear();
        if(events.size()!=0) {
            for (Event e : events) {
                split_getdata = e.getData().toString().split("\n");
                input_getdata += split_getdata[1] + "\n";
                arData.add(split_getdata[0]);
            }
        }else{
            arData.add("┼ 일정을 등록하세요.");
        }
        adapter.notifyDataSetChanged();
    }

    //DB에서 가져온 값으로 달력에 표시
    void checkDBData(){
        if(gtxt == null) {}
        else if(gtxt != null) {
            String sdate = get_syear+"."+get_smonth+"."+get_sday;
            String edate = get_eyear+"."+get_emonth+"."+get_eday;
            String start_date="";
            String end_date="";
            String date="";

            Log.d("캘",gtxt);

            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                Date s_date = dateFormat.parse(sdate);
                Date e_date = dateFormat.parse(edate);

                long diff = e_date.getTime()-s_date.getTime();
                long diffday = diff/(24*60*60*1000);

                if(get_repeat_end.equals("0")){
                    for(int k=0;k<=diffday;k++){
                        cc.set(get_syear, get_smonth-1, get_sday+k);

                        Event ev = new Event(R.color.black, cc.getTime().getTime(), sdate + " - " + edate + "   " + gtxt+"\n"+get_mdate);
                        compactCalendarView.addEvent(ev, true);
                        //-----------------------------------------
                        reloadlistviewdata();
                    }
                }else {
                    Date repeat_date = dateFormat.parse(get_repeat_end);
                    long diff_repeat = repeat_date.getTime()-s_date.getTime();
                    long diff_rp = diff_repeat/(24*60*60*1000);
                    switch (get_repeat) {
                        case "매일":
                            for (int i = 0; i <= (int) diff_rp; i++) {
                                for (int k = 0; k <= diffday; k++) {
                                    cc.set(get_syear, get_smonth - 1, get_sday + k + i);
                                    if(k==0) {
                                        start_date = dateFormat.format(cc.getTime());
                                        cc2.set(get_eyear, get_emonth - 1, get_eday + k + (int)diff_rp);
                                        end_date = dateFormat.format(cc2.getTime());
                                        date = start_date + " - "+ end_date;
                                    }

                                    Event ev = new Event(R.color.white, cc.getTime().getTime(), date + "   " + gtxt + "\n" + get_mdate);
                                    compactCalendarView.addEvent(ev, true);
                                    //-----------------------------------------
                                    reloadlistviewdata();
                                }
                            }
                            break;
                        case "매주":
                            int a = (int) diff_rp / 7;
                            for (int i = 0; i <= a; i++) {
                                for (int k = 0; k <= diffday; k++) {
                                    cc.set(get_syear, get_smonth - 1, get_sday + k + 7 * i);
                                    if(k==0) {
                                        start_date = dateFormat.format(cc.getTime());
                                        cc2.set(get_eyear, get_emonth - 1, get_eday + 7 * a);
                                        end_date = dateFormat.format(cc2.getTime());
                                        date = start_date + " - "+ end_date;
                                    }

                                    Event ev = new Event(R.color.black, cc.getTime().getTime(), date + "   " + gtxt + "\n" + get_mdate);
                                    compactCalendarView.addEvent(ev, true);
                                    //-----------------------------------------
                                    reloadlistviewdata();
                                }
                            }
                            break;
                        case "매월":
                            cc.set(get_syear, get_smonth - 1, get_sday);
                            int day_of_month = cc.getActualMaximum(cc.DAY_OF_MONTH);
                            for (int i = 0; i <= diff_rp / day_of_month; i++) {
                                for (int k = 0; k <= diffday; k++) {
                                    cc.set(get_syear, get_smonth - 1 + i, get_sday + k);
                                    if(k==0&&c_a==0) {
                                        start_date = dateFormat.format(cc.getTime());
                                        cc2.set(get_eyear, get_emonth - 1+i, get_eday + k);
                                        end_date = dateFormat.format(cc2.getTime());
                                        date = start_date + " - "+ end_date;
                                        c_a=1;
                                    }

                                    Event ev = new Event(R.color.black, cc.getTime().getTime(), date + "   " + gtxt + "\n" + get_mdate);
                                    compactCalendarView.addEvent(ev, true);
                                    //-----------------------------------------
                                    reloadlistviewdata();
                                }
                            }
                            break;
                        case "매년":
                            for (int i = 0; i <= diff_rp / 365; i++) {
                                for (int k = 0; k <= diffday; k++) {
                                    cc.set(get_syear + i, get_smonth - 1, get_sday + k);
                                    if(k==0) {
                                        start_date = dateFormat.format(cc.getTime());
                                        cc2.set(get_eyear+i, get_emonth - 1, get_eday + k);
                                        end_date = dateFormat.format(cc2.getTime());
                                        date = start_date + " - "+ end_date;
                                    }

                                    Event ev = new Event(R.color.colorAccent, cc.getTime().getTime(), date + "   " + gtxt + "\n" + get_mdate);
                                    compactCalendarView.addEvent(ev, true);
                                    //-----------------------------------------
                                    reloadlistviewdata();
                                }
                            }
                            break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //DB에서 가져온 값으로 달력에 표시
    void checkDeleteDBData(String delete_milli){
        if(delete_milli.length()!=0){
            cc.set(viewYear, viewMonth, 0);
            List<Event> events = compactCalendarView.getEventsForMonth(cc.getTime());
            for(int i=0;i<events.size();i++){
                String[] d_event=(events.get(i).toString()).split("\n");
                String delete_event=d_event[1].substring(0, d_event[1].length()-1);
                if(delete_milli.equals(delete_event)){
                    compactCalendarView.removeEvent(events.get(i));
                    events.remove(i);
                    reloadlistviewdata();
                }
            }
            Log.d("이벤트", String.valueOf(events.size()));
        }
    }

    //displayScheduleList()에서 받아온 값을 객체에 저장하고 checkData()호출
    void getSchedule(String gmilli_date, String txt1, int get_syear1, int get_smonth1, int get_sday1, int get_eyear1, int get_emonth1, int get_eday1, String get_repeat1, String get_repeat_end1, String get_reg_date1){
        gtxt = txt1;
        get_syear = get_syear1;
        get_smonth = get_smonth1;
        get_sday = get_sday1;
        get_eyear = get_eyear1;
        get_emonth = get_emonth1;
        get_eday = get_eday1;
        get_mdate = gmilli_date;
        get_repeat = get_repeat1;
        get_repeat_end = get_repeat_end1;
        get_reg_date = get_reg_date1;
        checkDBData();
    }

    //오늘 일정(오늘일정만 나오도록 수정해야함)
    protected void displayScheduleList() {
        // handler 객체로부터 모든 메모를 읽어 옴
        Cursor cursor = handlerSchedule.selectAsc_reg(member_id);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor == null ) {
            compactCalendarView.removeAllEvents();
        }else {
            Date date1 = null, date2 = null;
            do {
                ScheduleInfo s = new ScheduleInfo();
                s.milli_date = cursor.getString(0);
                s.s_syear = cursor.getInt(1);
                s.s_smonth = cursor.getInt(2);
                s.s_sday = cursor.getInt(3);
                s.s_stime = cursor.getString(5);
                s.s_eyear = cursor.getInt(6);
                s.s_emonth = cursor.getInt(7);
                s.s_eday = cursor.getInt(8);
                s.s_etime = cursor.getString(10);
                s.stitle = cursor.getString(11);
                s.local = cursor.getString(12);
                s.sexplain = cursor.getString(13);
                s.srepeat = cursor.getString(16);
                s.srepeat_end = cursor.getString(17);
                s.sreg_date = cursor.getString(18);
                s.sstate = cursor.getString(19);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                try {
                    date1 = sdf.parse(s.sreg_date);
                    date2 = sdf.parse(get_reg_date);
                    Log.d("날짜1", String.valueOf(date1));
                    Log.d("날짜2", String.valueOf(date2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date1.after(date2)) {
                    getSchedule(s.milli_date, s.stitle, s.s_syear, s.s_smonth, s.s_sday, s.s_eyear, s.s_emonth, s.s_eday, s.srepeat, s.srepeat_end, s.sreg_date);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        // 커서를 닫고 listAdapter 객체에게 알려 갱신된 정보가
        // 화면에 표시되도록 함
        adapter.notifyDataSetChanged();
    }

    //오늘 일정(오늘일정만 나오도록 수정해야함)
    protected void displayDeleteScheduleList() {
        // handler 객체로부터 모든 메모를 읽어 옴
        Cursor cursor = handlerSchedule.selectDelete(member_id);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor == null ) return;

        do {
            ScheduleInfo s = new ScheduleInfo();
            s.milli_date = cursor.getString(0);

            checkDeleteDBData(s.milli_date);

        } while( cursor.moveToNext() );
        // 커서를 닫고 listAdapter 객체에게 알려 갱신된 정보가
        // 화면에 표시되도록 함
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    protected void removeAllLists() {
        arData.removeAll(arData);
    }

    void weatherImg(){
        switch (weather){
            case "맑음":
                imageView.setBackgroundResource(R.drawable.sun);
                break;
            case "구름 조금":
                imageView.setBackgroundResource(R.drawable.little_cloud);
                break;
            case "흐림":
                imageView.setBackgroundResource(R.drawable.cloud);
                break;
            case "구름 많음":
                imageView.setBackgroundResource(R.drawable.many_cloud);
                break;
            case "비":
                imageView.setBackgroundResource(R.drawable.rain);
                break;
        }
    }

    //DatePicker 리스너
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    c.set(mYear, mMonth, mDay);
                    frmDate = new SimpleDateFormat("yyyy년 M월");
                    get_date = frmDate.format(c.getTime());
                    updateDisplay();
                }
            };

    private void updateDisplay(){
        calendar_date.setText(get_date);
        try {
            Date calendar_date = frmDate.parse(get_date);
            compactCalendarView.setCurrentDate(calendar_date);
            viewYear=calendar_date.getYear();
            viewMonth=calendar_date.getMonth();
            viewDay=calendar_date.getDay();
            reloadlistviewdata();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}