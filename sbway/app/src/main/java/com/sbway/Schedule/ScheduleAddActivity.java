package com.sbway.Schedule;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.sbway.CalendarActivity;
import com.sbway.MainActivity;
import com.sbway.R;
import com.sbway.UserLocation.UserLocationActivity;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleAddActivity extends AppCompatActivity {
    DBHandler_schedule handler;

    //멤버 아이디 저장
    SharedPreferences auto_login;
    String member_id="";

    //상태를 저장하기 위한 변수
    String state = "";
    int select_year=0;
    int select_month=0;
    int select_day=0;
    String s_location="";
    String reg_date="";
    String alarm_time="";

    // 목록표시 엑티비티에서 호출되었는지(isCalled),
    // 새로운 데이터가 삽입되었는지(isadded) 판단하기 위한 변수
    boolean isCalled = false, isadded = false;

    // 뷰 컨트를을 위한 객체용 변수
    Button submitBtn;

    //View
    private Button btn1;
    private Button btn2;

    //년,월,일,시,분
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    //Dialog
    int DATE_DIALOG_ID;
    int TIME_DIALOG_ID;

    static final int DATE_DIALOG_ID_1 = 0;
    static final int TIME_DIALOG_ID_1 = 1;
    static final int DATE_DIALOG_ID_2 = 2;
    static final int TIME_DIALOG_ID_2 = 3;
    static final int DATE_DIALOG_ID_3 = 4;
    static final int TIME_DIALOG_ID_3 = 5;
    static final int DATE_DIALOG_ID_4 = 6;

    SimpleDateFormat frmDate, frmTime;
    String sd, st, ed, et;

    // 선택한 아이탬의 번호를 저장한다, 3번에서 사용
    int alarmChoose = 0;
    int replayChoose = 0;

    TextView schedule_title;
    EditText title;
    EditText location;
    EditText text; //설명
    TextView alarm;
    TextView replay;
    ImageView iv;

    //일정의 프라이머리키 저장
    String get_mdate="";
    String milli_now="";

    //일정 알람 관련 멤버변수
    private AlarmManager alarmManager;
    String get_time = "", set_d = "", set_t = "";
    Calendar calendar = Calendar.getInstance();
    long now;
    CharSequence[] items = { "알람 없음", "정각", "10분 전", "30분 전", "1시간 전", "직접 입력" };

    //통지 관련 멤버변수
    private NotificationManager notificationManager;

    //일정 반복 멤버변수
    String end_repeat = "0";
    final CharSequence[] items_replay = { "반복 없음", "매일", "매주", "매월", "매년" };

    //달력 업데이트 변수
    int d_syear, d_smonth, d_sday, d_eyear, d_emonth, d_eday;
    String d_repeat="", d_erepeat="", d_millidate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        frmDate = new SimpleDateFormat("yyyy.MM.dd (E)");
        sd = frmDate.format(c.getTime());
        ed = frmDate.format(c.getTime());
        frmTime = new SimpleDateFormat("kk:mm");
        st = frmTime.format(c.getTime());
        et = frmTime.format(c.getTime());
        now = System.currentTimeMillis();

        btn1 = (Button) findViewById(R.id.dbtn1_9);
        btn2 = (Button) findViewById(R.id.dbtn2_9);
        btn1.setBackgroundColor(0xffffff);
        btn2.setBackgroundColor(0xffffff);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID_1);
                showDialog(DATE_DIALOG_ID_1);
            }
        });

        //시간설정 이벤트
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID_2);
                showDialog(DATE_DIALOG_ID_2);
            }
        });

        SimpleDateFormat sdf_milli = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");
        milli_now = sdf_milli.format(new Date());

        handler = new DBHandler_schedule(this);
        auto_login = getSharedPreferences("setting", 0);
        member_id = auto_login.getString("id", "null");

        schedule_title = (TextView) findViewById(R.id.schedule_title);
        iv = (ImageView) findViewById(R.id.delete_schedule);
        title = (EditText) findViewById(R.id.title_9);
        location = (EditText) findViewById(R.id.location_9);
        text = (EditText) findViewById(R.id.memo_9);
        alarm = (TextView) findViewById(R.id.alarm_9);
        replay = (TextView) findViewById(R.id.replay_9);
        submitBtn = (Button) findViewById(R.id.btn_9);

        // 목록 표시용 엑티비티에서 전달된 값을 읽어 옴
        final Intent intent = getIntent();
        state = intent.getStringExtra("state");
        get_mdate = intent.getStringExtra("s_primary");

        if(intent.getIntExtra("schedule_year",0)!=0) {
            select_year = intent.getIntExtra("schedule_year", 0);
            select_month = intent.getIntExtra("schedule_month", 0)-1;
            select_day = intent.getIntExtra("schedule_day", 0);
        }else if(intent.getIntExtra("schedule_year",0)==0) {
            select_year = mYear;
            select_month = mMonth;
            select_day = mDay;
        }

        if (state != null) {
            if (state.equals("insert")) {
                iv.setVisibility(View.INVISIBLE);
                schedule_title.setText("일정추가");
                submitBtn.setText("등록하기");

                btn1.setText("시작일 설정");
                btn2.setText("종료일 설정");
            } else if (state.equals("update")) {
                iv.setVisibility(View.VISIBLE);
                displaySelectSchedule();

                schedule_title.setText("일정수정");
                submitBtn.setText("수정하기");

                iv.isClickable();
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final LovelyStandardDialog memoDialog = new LovelyStandardDialog(ScheduleAddActivity.this);
                        memoDialog.setTopColorRes(R.color.schedule)
                                .setTopTitle("SCHEDULE")
                                .setTopTitleColor(Color.WHITE)
                                .setTitle("일정 삭제")
                                .setIcon(R.drawable.alert_delete)
                                .setIconTintColor(Color.WHITE)
                                .setMessage("일정을 삭제하시겠습니까?")
                                .setPositiveButton("확인", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String state = "d";
                                        String aw = "a";
                                        SimpleDateFormat sdf_milli = new SimpleDateFormat("yyyy.MM.dd (E) kk:mm:ss");
                                        reg_date = sdf_milli.format(new Date());
                                        handler.schedule_state(member_id, get_mdate, reg_date, state, aw);

                                        finish();
                                    }
                                })
                                .setNegativeButton("취소", null)
                                .show();
                    }
                });
            }
        } else {
            schedule_title.setText("일정추가");
            submitBtn.setText("등록하기");

            btn1.setText("시작일 설정");
            btn2.setText("종료일 설정");
        }

        //알람
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //통지
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 실행 결과 정보를 되돌리기 위한 인텐트 객체 생성
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //btn1과 btn2의 text를 split
                String startdate = btn1.getText().toString();
                String enddate = btn2.getText().toString();

                Log.d("예", startdate);
                Log.d("예", enddate);

                //날짜랑 시간,요일 분리
                String[] ssdate = startdate.split(" ");
                String[] sedate = enddate.split(" ");

                //요일과 시간분리
                String[] split_stime = ssdate[1].split("\n");
                String[] split_etime = sedate[1].split("\n");
                Log.d("예", split_stime[0]);
                Log.d("예", split_etime[0]);

                //날짜 분리
                String[] s_sdate = ssdate[0].split("\\.");
                String[] s_edate = sedate[0].split("\\.");

                //시간저장
                String stime = split_stime[1];
                String etime = split_etime[1];

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                String now = sdf.format(new Date());
                Log.d("시간", now);

                //알람분리
                String[] split_alarm = alarm.getText().toString().split("\\(");

                //반복분리
                String[] repeat = replay.getText().toString().split("\\(");
                Log.d("반복", repeat[0]);

                if (v == submitBtn) {
                    if (state.equals("insert")) {
                        //상태와 안드로이드 저장
                        String state = "i";
                        String aw = "a";

                        // 등록 버턴이 눌려진 경우
                        // 내용이 입력되었는지 검사하고 내용이 입력되지 않았으면
                        // 입력 안내 메시지 출력 후 함수 실행 종료
                        if (!inputCheck()) {
                            Toast.makeText(getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            // 그렇지 않으면 입력된 내용을 데이터베이스에 삽입홤
                            Log.d("완료", end_repeat);
                            if (handler.insert(member_id, milli_now, title.getText().toString(), s_sdate[0], s_sdate[1], s_sdate[2], split_stime[0], stime, s_edate[0], s_edate[1], s_edate[2], split_etime[0], etime, location.getText().toString(),
                                    text.getText().toString(), split_alarm[0], alarm_time, repeat[0], end_repeat, now, state, aw) == 0) {
                                Toast.makeText(getApplicationContext(), "등록할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show();
                                isadded = true;
                                new AlarmHATT(getApplicationContext()).Alarm();
                                finish();
                            }
                        }
                    } else if (state.equals("update")) {
                        //상태와 안드로이드 저장
                        String state = "u";
                        String aw = "a";

                        // 등록 버턴이 눌려진 경우
                        // 내용이 입력되었는지 검사하고 내용이 입력되지 않았으면
                        // 입력 안내 메시지 출력 후 함수 실행 종료
                        if (!inputCheck()) {
                            Toast.makeText(getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            // 그렇지 않으면 입력된 내용을 데이터베이스에 삽입홤
                            if (handler.update(member_id, get_mdate, title.getText().toString(), s_sdate[0], s_sdate[1], s_sdate[2], split_stime[0], stime, s_edate[0], s_edate[1], s_edate[2], split_etime[0], etime, location.getText().toString(),
                                    text.getText().toString(), split_alarm[0], alarm_time, repeat[0], end_repeat, now, state, aw) == 0)
                                Toast.makeText(getApplicationContext(), "수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                                isadded = true;
                                new AlarmHATT(getApplicationContext()).Alarm();

                                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                                intent.putExtra("d_millidate", d_millidate);
                                intent.putExtra("d_syear", d_syear);
                                intent.putExtra("d_smonth", d_smonth);
                                intent.putExtra("d_sday", d_sday);
                                intent.putExtra("d_eyear", d_eyear);
                                intent.putExtra("d_emonth", d_emonth);
                                intent.putExtra("d_eday", d_eday);
                                intent.putExtra("d_erepeat", d_erepeat);
                                intent.putExtra("eventIndex", intent.getIntExtra("eventIndex",0));
                                intent.putExtra("c_state", "update");
                                setResult(10, intent);
                                finish();
                            }
                        }
                    }
                }
            }
        });
    }

    // 내용이 입력되었는지 검사하여 true(입력됨), false(입력되지 않음)
    // 을 되돌림
    protected boolean inputCheck() {
        if (title.getText().toString().length() == 0) return false;
        else return true;
    }

    protected void onDestroy() {
        // 엑티비티가 종료되면 handler 객체의 close 함수를 이용하여
        // 데이터베이스를 닫아 줌
        super.onDestroy();
        handler.close();
    }

    private void updateDisplay() {
        btn1.setText(sd + "\n" + st);
    }

    private void updateDisplay2() {
        btn2.setText(ed + "\n" + et);
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
                    frmDate = new SimpleDateFormat("yyyy.MM.dd (E)");
                    sd = frmDate.format(c.getTime());
                    updateDisplay();
                }
            };

    //TimePicker 리스너
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Calendar c = Calendar.getInstance();
                    mHour = hourOfDay;
                    mMinute = minute;
                    c.set(mYear, mMonth, mDay, mHour, mMinute);
                    frmTime = new SimpleDateFormat("kk:mm");
                    st = frmTime.format(c.getTime());
                    updateDisplay();
                }
            };

    private DatePickerDialog.OnDateSetListener nDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    c.set(mYear, mMonth, mDay);
                    frmDate = new SimpleDateFormat("yyyy.MM.dd (E)");
                    ed = frmDate.format(c.getTime());
                    updateDisplay2();
                }
            };

    //TimePicker 리스너
    private TimePickerDialog.OnTimeSetListener nTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Calendar c = Calendar.getInstance();
                    mHour = hourOfDay;
                    mMinute = minute;
                    c.set(mYear, mMonth, mDay, mHour, mMinute);
                    frmTime = new SimpleDateFormat("kk:mm");
                    et = frmTime.format(c.getTime());
                    updateDisplay2();
                }
            };

    private DatePickerDialog.OnDateSetListener aDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    calendar.set(mYear, mMonth, mDay);
                    frmDate = new SimpleDateFormat("yyyy.MM.dd (E)");
                    set_d = frmDate.format(calendar.getTime());
                    alarm_time = set_d+" ";
                }
            };

    //TimePicker 리스너
    private TimePickerDialog.OnTimeSetListener aTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    calendar.set(mYear, mMonth, mDay, mHour, mMinute);
                    frmTime = new SimpleDateFormat("kk:mm");
                    set_t = frmTime.format(calendar.getTime());
                    alarm_time += set_t;
                    alarm.setText(items[alarmChoose] + "(시간 : " + alarm_time + ")");
                }
            };

    private DatePickerDialog.OnDateSetListener bDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    calendar.set(mYear, mMonth, mDay);
                    frmDate = new SimpleDateFormat("yyyy.MM.dd");
                    end_repeat = frmDate.format(calendar.getTime());
                    replay.setText(items_replay[replayChoose] + "(완료일 : " + end_repeat + ")");
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID_1:
                return new DatePickerDialog(this, mDateSetListener, select_year, select_month, select_day);

            case TIME_DIALOG_ID_1:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);

            case DATE_DIALOG_ID_2:
                return new DatePickerDialog(this, nDateSetListener, mYear, mMonth, mDay);

            case TIME_DIALOG_ID_2:
                return new TimePickerDialog(this, nTimeSetListener, mHour, mMinute, false);

            case DATE_DIALOG_ID_3:
                return new DatePickerDialog(this, aDateSetListener, select_year, select_month, select_day);

            case TIME_DIALOG_ID_3:
                return new TimePickerDialog(this, aTimeSetListener, mHour, mMinute, false);

            case DATE_DIALOG_ID_4:
                return new DatePickerDialog(this, bDateSetListener, select_year, select_month, select_day);
        }
        return null;
    }

    public void alarm_alert(View v){
        if(sd!=null&&state.equals("insert")) {
            get_time = sd + " " + st;
        }

        //String -> Date
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd (E) kk:mm");
        try {
            final Date get_date = formatter.parse(get_time);

            calendar.setTime(get_date);
            final LovelyChoiceDialog choiceDialog = new LovelyChoiceDialog(ScheduleAddActivity.this);
            choiceDialog.setTopColorRes(R.color.schedule)
                    .setTopTitle("SCHEDULE")
                    .setTopTitleColor(Color.WHITE)
                    .setTopTitle("알람 설정")
                    .setIcon(R.drawable.calendar_alarm)
                    .setItems(items, new LovelyChoiceDialog.OnItemSelectedListener<CharSequence>() {
                        @Override
                        public void onItemSelected(int position, CharSequence item) {
                            // 고를때마다 선택한 값을 저장합니다
                            alarmChoose = position;

                            switch(alarmChoose){
                                case 1:
                                    //정각
                                    calendar.setTime(get_date);
                                    alarm_time=formatter.format(calendar.getTime());
                                    alarm.setText(items[alarmChoose] + "(시간 : " + alarm_time + ")");
                                    break;
                                case 2:
                                    //10분 전
                                    calendar.add(Calendar.MINUTE, -10);
                                    alarm_time=formatter.format(calendar.getTime());
                                    alarm.setText(items[alarmChoose] + "(시간 : " + alarm_time + ")");
                                    break;
                                case 3:
                                    //30분 전
                                    calendar.add(Calendar.MINUTE, -30);
                                    alarm_time=formatter.format(calendar.getTime());
                                    alarm.setText(items[alarmChoose] + "(시간 : " + alarm_time + ")");
                                    break;
                                case 4:
                                    //1시간 전
                                    calendar.add(Calendar.HOUR, -1);
                                    alarm_time=formatter.format(calendar.getTime());
                                    alarm.setText(items[alarmChoose] + "(시간 : " + alarm_time + ")");
                                    break;
                                case 5:
                                    //직접입력
                                    showDialog(TIME_DIALOG_ID_3);
                                    showDialog(DATE_DIALOG_ID_3);
                                    break;
                            }
                            if(alarmChoose==0) {
                                alarm.setText(items[alarmChoose]);
                            }
                        }
                    }).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void replay_alert(View v){
        final LovelyChoiceDialog choiceDialog = new LovelyChoiceDialog(ScheduleAddActivity.this);
        choiceDialog.setTopColorRes(R.color.schedule)
                .setTopTitle("SCHEDULE")
                .setTopTitleColor(Color.WHITE)
                .setTopTitle("반복 설정")
                .setIcon(R.drawable.calendar_replay)
                .setItems(items_replay, new LovelyChoiceDialog.OnItemSelectedListener<CharSequence>() {
                    @Override
                    public void onItemSelected(int id, CharSequence item) {
                        replayChoose = id;
                        switch (replayChoose){
                            case 0:
                                //반복없음
                                replay.setText(items_replay[replayChoose]);
                                end_repeat="0";
                                break;
                            case 1:
                                //매일반복
                                showDialog(DATE_DIALOG_ID_4);
                                break;
                            case 2:
                                //매주반복
                                showDialog(DATE_DIALOG_ID_4);
                                break;
                            case 3:
                                //매월반복
                                showDialog(DATE_DIALOG_ID_4);
                                break;
                            case 4:
                                //매년반복
                                showDialog(DATE_DIALOG_ID_4);
                                break;
                        }
                    }
                })
                .show();
    }

    //primary값으로 일정 가져오기
    protected void displaySelectSchedule() {
        // handler 객체로부터 모든 메모를 읽어 옴
        ScheduleInfo s = handler.select(get_mdate, member_id);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( s == null ) return;

        try {
            btn1.setText(s.s_syear+"."+s.s_smonth+"."+s.s_sday+" "+s.s_sday_week+"\n"+s.s_stime);
            btn2.setText(s.s_eyear+"."+s.s_emonth+"."+s.s_eday+" "+s.s_eday_week+"\n"+s.s_etime);
            get_time = s.s_syear+"."+s.s_smonth+"."+s.s_sday+" "+s.s_sday_week+" "+s.s_stime;
            title.setText(s.stitle);
            location.setText(s.local);
            text.setText(s.sexplain);
            if(s.salarm.equals("알람 없음")||s.salarm.equals("알람을 설정하세요.")){
                alarm.setText(s.salarm);
            }else{
                alarm.setText(s.salarm+"(시간 : "+s.salarm_time+")");
                alarm_time = s.salarm_time;
            }
            if(s.srepeat.equals("반복 없음")||s.srepeat.equals("반복을 설정하세요.")){
                replay.setText(s.srepeat);
            }else {
                replay.setText(s.srepeat + "(완료일 : " + s.srepeat_end + ")");
                end_repeat = s.srepeat_end;
            }
            d_millidate = s.milli_date;
            d_syear=s.s_syear;
            d_smonth=s.s_smonth;
            d_sday=s.s_sday;
            d_eyear=s.s_eyear;
            d_emonth=s.s_emonth;
            d_eday=s.s_eday;
            d_repeat=s.srepeat;
            d_erepeat=s.srepeat_end;
        }catch (Exception e){e.printStackTrace();}

        // 커서를 닫고 listAdapter 객체에게 알려 갱신된 정보가
        // 화면에 표시되도록 함
    }

    public class AlarmHATT{
        private Context context;
        public  AlarmHATT(Context context){
            this.context=context;
        }
        public void Alarm() {
            if (alarm.getText().toString().equals("알람 없음") || alarm.getText().toString().equals("알람을 설정하세요.")) ;
            else if (now < calendar.getTimeInMillis()) {
                if(state.equals("insert")) {
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    intent.setData(Uri.parse(milli_now));
                    PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

                    //알람 예약
                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                }else if(state.equals("update")){
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    intent.setData(Uri.parse(get_mdate));
                    PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                    am.cancel(sender);

                    //알람 예약
                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                }
            }
        }
    }
}

