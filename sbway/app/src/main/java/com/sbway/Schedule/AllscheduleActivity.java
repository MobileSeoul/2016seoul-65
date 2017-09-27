package com.sbway.Schedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.sbway.CalendarActivity;
import com.sbway.Login.LoginActivity;
import com.sbway.R;
import com.sbway.SwipeDelete.SwipeDismissListViewTouchListener;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AllscheduleActivity extends AppCompatActivity {
    LinearLayout as_btn;
    LinearLayout nodata;
    ImageView s_plus;
    TextView all_delete;

    //스케줄띄우기
    DBHandler_schedule handlerSchedule;
    ArrayList<ScheduleInfo> schedule_data;
    ListView schedulelist;
    AllScheduleAdapter scheduleAdapter;

    String member_id="";
    String reg_date="";
    SharedPreferences spf;
    int getVisible = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allschedule);

        spf = getSharedPreferences("setting", 0);
        member_id = spf.getString("id", "null");

        as_btn = (LinearLayout)findViewById(R.id.calendar_btn);
        as_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        all_delete = (TextView)findViewById(R.id.list_delete);

        s_plus = (ImageView)findViewById(R.id.as_plus);
        s_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(member_id.equals("null")){
                    final LovelyStandardDialog ddayDialog = new LovelyStandardDialog(AllscheduleActivity.this);
                    ddayDialog.setTopColorRes(R.color.dday)
                            .setTopTitle("SCHEDULE")
                            .setTopTitleColor(Color.WHITE)
                            .setIcon(R.drawable.alert_info)
                            .setIconTintColor(Color.WHITE)
                            .setMessage("로그인 후 이용가능합니다. 로그인 창으로 이동하시겠습니까?")
                            .setPositiveButton("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(AllscheduleActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }else {
                    //일정추가 액티비티 이동
                    Intent intent = new Intent(getApplicationContext(), ScheduleAddActivity.class);
                    intent.putExtra("state", "insert");
                    startActivity(intent);
                }
            }
        });

        //일정
        handlerSchedule = new DBHandler_schedule(getApplicationContext());
        schedulelist = (ListView)findViewById(R.id.all_list);
        schedule_data = new ArrayList<ScheduleInfo>();
        scheduleAdapter = new AllScheduleAdapter(getApplicationContext(),schedule_data);
        schedulelist.setAdapter(scheduleAdapter);

        schedulelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(((TextView)findViewById(R.id.as_title)).toString().equals("등록된 일정이 없습니다.")){
                    //일정추가 액티비티 이동
                    Intent intent = new Intent(getApplicationContext(), ScheduleAddActivity.class);
                    intent.putExtra("state", "insert");
                    startActivity(intent);
                }else {
                    //일정수정 액티비티 이동
                    Intent intent = new Intent(getApplicationContext(), ScheduleAddActivity.class);
                    intent.putExtra("s_primary", schedule_data.get(i).milli_date);
                    intent.putExtra("state", "update");
                    startActivity(intent);
                }
            }
        });

        all_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LovelyStandardDialog ddayDialog = new LovelyStandardDialog(AllscheduleActivity.this);
                ddayDialog.setTopColorRes(R.color.schedule)
                        .setTopTitle("SCHEDULE")
                        .setTopTitleColor(Color.WHITE)
                        .setIcon(R.drawable.alert_delete)
                        .setIconTintColor(Color.WHITE)
                        .setTitle("일정 삭제")
                        .setMessage("모든 일정을 삭제하시겠습니까?")
                        .setPositiveButton("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String state = "d";
                                String aw = "a";
                                SimpleDateFormat sdf_milli = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss");
                                reg_date = sdf_milli.format(new Date());
                                handlerSchedule.deleteAll(member_id, reg_date, state, aw);

                                Intent intent = new Intent(getApplicationContext(), AllscheduleActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "모든 일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        schedulelist,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {
                                    final LovelyStandardDialog ddayDialog = new LovelyStandardDialog(AllscheduleActivity.this);
                                    ddayDialog.setTopColorRes(R.color.schedule)
                                            .setTopTitle("SCHEDULE")
                                            .setTitle("일정 삭제")
                                            .setTopTitleColor(Color.WHITE)
                                            .setIcon(R.drawable.alert_delete)
                                            .setIconTintColor(Color.WHITE)
                                            .setMessage("일정을 삭제하시겠습니까?")
                                            .setPositiveButton("확인", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    int i = position;
                                                    String state = "d";
                                                    String aw = "a";
                                                    SimpleDateFormat sdf_milli = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss");
                                                    reg_date = sdf_milli.format(new Date());
                                                    handlerSchedule.schedule_state(member_id, schedule_data.get(i).milli_date, reg_date, state, aw);

                                                    Intent intent = new Intent(getApplicationContext(), AllscheduleActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton("취소", null)
                                            .show();
                                }
                            }
                        });
        schedulelist.setOnTouchListener(touchListener);

        nodata=(LinearLayout)findViewById(R.id.nodata);
        nodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getVisible == 1) {
                    if (member_id.equals("null")) {
                        final LovelyStandardDialog ddayDialog = new LovelyStandardDialog(AllscheduleActivity.this);
                        ddayDialog.setTopColorRes(R.color.dday)
                                .setTopTitle("SCHEDULE")
                                .setTopTitleColor(Color.WHITE)
                                .setIcon(R.drawable.alert_info)
                                .setIconTintColor(Color.WHITE)
                                .setMessage("로그인 후 이용가능합니다. 로그인 창으로 이동하시겠습니까?")
                                .setPositiveButton("확인", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(AllscheduleActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("취소", null)
                                .show();
                    } else {
                        nodata.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //일정추가 액티비티 이동
                                Intent intent = new Intent(getApplicationContext(), ScheduleAddActivity.class);
                                intent.putExtra("state", "insert");
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        displaySchedule();
        checkData();
        super.onResume();
    }

    protected void removeAllLists() {
        schedule_data.removeAll(schedule_data);
    }

    //오늘 일정
    protected void displaySchedule() {
        //memoList에 저장된 목록을 모두 삭제함
        removeAllLists();
        // handler 객체로부터 모든 메모를 읽어 옴
        Cursor cursor = handlerSchedule.selectAllasc(member_id);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor == null ) return;
        // 커서를 통해 읽어 온 데이터를 memoList에 추가함
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
            schedule_data.add(s);
        } while( cursor.moveToNext() );
        // 커서를 닫고 listAdapter 객체에게 알려 갱신된 정보가
        // 화면에 표시되도록 함
        cursor.close();
        scheduleAdapter.notifyDataSetChanged();
    }

    protected void checkData(){
        if(schedule_data.size()==0){
            nodata.setVisibility(View.VISIBLE);
            all_delete.setVisibility(View.INVISIBLE);
            getVisible = 1;
        }else{
            nodata.setVisibility(View.INVISIBLE);
            all_delete.setVisibility(View.VISIBLE);
            getVisible = 2;
        }
    }
}