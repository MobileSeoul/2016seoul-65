package com.sbway;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.navdrawer.SimpleSideDrawer;
import com.sbway.Bookmark.BookmarkInfo;
import com.sbway.Bookmark.DBHandler_Bookmark;
import com.sbway.Dday.DBHandler;
import com.sbway.Dday.DdayInfo;
import com.sbway.Dday.DdayListActivity;
import com.sbway.Login.LoginActivity;
import com.sbway.Memo.DBHandler_memo;
import com.sbway.Memo.MemoInfo;
import com.sbway.Memo.MemoListActivity;
import com.sbway.News.NewsActivity;
import com.sbway.News.NewsAdapterHome;
import com.sbway.News.NewsAsyncTask;
import com.sbway.News.NewsPlusActivity;
import com.sbway.PathSearch.ResultActivity;
import com.sbway.Schedule.DBHandler_schedule;
import com.sbway.Schedule.ScheduleAddActivity;
import com.sbway.Schedule.ScheduleInfo;
import com.sbway.Splash.SplashActivity;
import com.sbway.Sync.SyncActivity;
import com.sbway.Sync.SyncService;
import com.sbway.Sync.sync_control;
import com.sbway.UserLocation.UserLoc_DBInsert;
import com.sbway.UserLocation.UserLocationActivity;
import com.sbway.Weather.WeatherActivity;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class HomeActivity extends Fragment {
    //자동로그인
    SharedPreferences auto_login;
    SharedPreferences.Editor editor;
    TextView login_btn;

    //슬라이드 메뉴
    SimpleSideDrawer slide_menu;
    ImageView btn_slide_menu;

    //동기화
    SharedPreferences sync_sp;
    SharedPreferences.Editor sync_editor;
    TextView sync_btn;
    SharedPreferences sync_time_sp;
    SharedPreferences.Editor sync_time_editor;

    //사용자 기본 위치 설정 항목
    SharedPreferences userLoc_set;
    SharedPreferences.Editor userLoc_editor;


    //스케줄띄우기
    DBHandler_schedule handlerSchedule;
    ArrayList<ScheduleInfo> schedule_data;
    ScheduleAdapter scheduleAdapter;
    String weather;
    LinearLayout schedule_content;
    TextView ts_title;
    TextView ts_exp;
    TextView ts_loc;
    TextView ts_date;
    int schedule_position;

    //날씨
    GetXMLTask task;
    TextView tem;
    TextView loc;
    TextView air;
    TextView rain;
    ImageView imageWeather;
    Document doc = null;
    URL RSSurl = null;
    rcvJson RJ;
    JSONObject JStoken;
    String getJSON;
    String zoneCode;
    SharedPreferences zone_code;
    SharedPreferences.Editor zone_code_editor;

    //gps
    private gpsInfo gps;
    double latitude;
    double longitude;

    //위도 경도
    SharedPreferences lat_lon;
    SharedPreferences.Editor lat_lon_editor;

    //디데이 띄우기
    DBHandler handler;
    TextView getDday, ddaytxt;
    SharedPreferences dday_sp;
    SharedPreferences.Editor d_editor;
    String dday_milli;

    //메모 띄우기
    ListView memolist;
    DBHandler_memo handlerMemo;
    ArrayList<MemoInfo> memo_data;
    MemoAdapter memoAdapter;
    TextView memo_content;
    TextView memo_location;
    TextView memo_date;
    int memo_position=0;

    //뉴스 타이틀 띄우기
    ListView newslist;
    NewsAdapterHome newsAdapter;
    ArrayList<String> titlevec = new ArrayList<String>();
    ArrayList<String> linkvec = new ArrayList<String>();
    NewsAsyncTask newsAsyncTask = new NewsAsyncTask();

    //북마크 띄우기
    ListView bookmarklist;
    Bookmark_Adapter b_adapter;
    DBHandler_Bookmark b_handler;
    ArrayList<BookmarkInfo> b_listData;
    String getPath,c;

    //알람
    SharedPreferences alarm_spf;
    SharedPreferences.Editor alarm_editor;
    CheckedTextView alarm_bell;
    CheckedTextView alarm_vibe;
    Boolean bell_check;
    Boolean vibe_check;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_home, container, false);

        //디바이스 화면 크기 구하기
        DisplayMetrics display = v.getResources().getDisplayMetrics();
        int width = display.widthPixels;

        //슬라이드 메뉴
        slide_menu = new SimpleSideDrawer(getActivity());
        slide_menu.setLeftBehindContentView(R.layout.slidemenu);
        LinearLayout linear = (LinearLayout)slide_menu.findViewById(R.id.Linear_menu);
        LinearLayout.LayoutParams menu = (LinearLayout.LayoutParams) linear.getLayoutParams();
        int a = (String.valueOf(Math.floor(width*0.85))).indexOf(".");
        menu.width = Integer.parseInt((String.valueOf(Math.floor(width*0.85))).substring(0, a));
        linear.setLayoutParams(menu);

        alarm_bell = (CheckedTextView)slide_menu.findViewById(R.id.alarm_bell);
        alarm_vibe = (CheckedTextView)slide_menu.findViewById(R.id.alarm_vibe);

        alarm_spf = getActivity().getSharedPreferences("alarm", 0);
        alarm_editor = alarm_spf.edit();

        bell_check = alarm_spf.getBoolean("bell",false);
        vibe_check = alarm_spf.getBoolean("vibe", false);

        if(bell_check==null && vibe_check==null) {
            alarm_bell.setChecked(false);
            alarm_vibe.setChecked(false);
        }else{
            alarm_bell.setChecked(bell_check);
            alarm_vibe.setChecked(vibe_check);
        }

        //체크박스 선택시 값 저장
        alarm_bell.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(alarm_bell.isChecked()){
                    alarm_editor.putBoolean("bell", false);
                    alarm_editor.commit();
                }else{
                    alarm_editor.putBoolean("bell", true);
                    alarm_editor.commit();
                }
                return false;
            }
        });
        alarm_vibe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(alarm_vibe.isChecked()){
                    alarm_editor.putBoolean("vibe", false);
                    alarm_editor.commit();
                }else{
                    alarm_editor.putBoolean("vibe", true);
                    alarm_editor.commit();
                }
                return false;
            }
        });
        alarm_editor.commit();

        //로그인
        login_btn = (TextView) slide_menu.findViewById(R.id.login_btn);
        btn_slide_menu = (ImageView)v.findViewById(R.id.home_slidemenu);
        btn_slide_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                slide_menu.toggleLeftDrawer();
            }
        });
        auto_login = this.getActivity().getSharedPreferences("setting", 0);
        editor= auto_login.edit();
        final String id = auto_login.getString("id", "null"); //키값, 디폴트값
        if(id.equals("null")){
            final LovelyStandardDialog loginDialog = new LovelyStandardDialog(getActivity());
            loginDialog.setTopColorRes(R.color.etc)
                    .setTopTitle("LOGIN")
                    .setTopTitleColor(Color.WHITE)
                    .setIcon(R.drawable.alert_info)
                    .setIconTintColor(Color.WHITE)
                    .setMessage("원할한 어플리케이션 사용을 위하여 로그인을 하여주십시오.")
                    .setPositiveButton("로그인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
        }else {
            login_btn.setText(id+"님 로그아웃");
        }

        //로그아웃 시 디데이 정보 삭제
        dday_sp = this.getActivity().getSharedPreferences("dday", 0);
        d_editor = dday_sp.edit();

        //로그아웃 시 동기화 정보 삭제
        sync_time_sp = getActivity().getSharedPreferences("sync_time", 0);
        sync_time_editor = sync_time_sp.edit();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(login_btn.getText().equals("로그인")) {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                }else {
                    final LovelyStandardDialog loginDialog = new LovelyStandardDialog(getActivity());
                    loginDialog.setTopColorRes(R.color.etc)
                            .setTopTitle("LOGOUT")
                            .setTopTitleColor(Color.WHITE)
                            .setIcon(R.drawable.alert_info)
                            .setIconTintColor(Color.WHITE)
                            .setMessage("로그아웃 하시겠습니까?")
                            .setPositiveButton("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    editor.clear();
                                    editor.putString("id", "null");
                                    editor.commit();
                                    d_editor.clear();
                                    d_editor.commit();
                                    sync_time_editor.clear();
                                    sync_time_editor.commit();

                                    login_btn.setText("로그인");
                                    onResume();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }
            }
        });
        //사용자 기본 위치
        final TextView userLoc = (TextView)slide_menu.findViewById(R.id.user_Loc);
        userLoc_set = getActivity().getSharedPreferences("userLoc_set", 0);
        userLoc_editor= userLoc_set.edit();
        String userLoc_text = userLoc_set.getString("set", "");
        ((TextView)slide_menu.findViewById(R.id.user_Loc_text)).setText(userLoc_text);
        userLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "gps로 설정", "검색하여 설정" };
                final LovelyChoiceDialog choiceDialog = new LovelyChoiceDialog(getActivity());
                choiceDialog.setTopColorRes(R.color.etc)
                        .setTopTitleColor(Color.WHITE)
                        .setTopTitle("LOCATION")
                        .setTitle("사용자 기본 위치 설정")
                        .setIcon(R.drawable.location_pin)
                        .setItems(items, new LovelyChoiceDialog.OnItemSelectedListener<CharSequence>() {
                            @Override
                            public void onItemSelected(int id, CharSequence item) {
                                userLoc_editor.clear();
                                userLoc_editor.putString("set", items[id].toString());
                                userLoc_editor.putInt("set_id", id);
                                userLoc_editor.commit();
                                ((TextView)slide_menu.findViewById(R.id.user_Loc_text)).setText(items[id].toString());
                                choiceDialog.dismiss();
                                if(id==0){
                                    gps = new gpsInfo(getActivity());
                                    if (gps.isGetLocation() == false) {
                                        final LovelyStandardDialog gpsDialog = new LovelyStandardDialog(getActivity());
                                        gpsDialog.setTopColorRes(R.color.etc)
                                                .setTopTitle("GPS")
                                                .setTopTitleColor(Color.WHITE)
                                                .setIcon(R.drawable.alert_info)
                                                .setIconTintColor(Color.WHITE)
                                                .setMessage("gps 기능을 사용할 수 없습니다.\n" +
                                                        "설정창으로 이동하시겠습니까?")
                                                .setPositiveButton("확인", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                        getActivity().startActivity(i);
                                                    }
                                                })
                                                .setNegativeButton("취소", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Toast.makeText(getActivity(), "현 위치를 알 수 없어 가장 최신 위치로 설정합니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .show();
                                    } else {
                                        task = new GetXMLTask();

                                        latitude = gps.getLatitude();
                                        longitude = gps.getLongitude();
                                        lat_lon_editor.clear();
                                        lat_lon_editor.putFloat("lat", (float) latitude);
                                        lat_lon_editor.putFloat("lon", (float) longitude);
                                        lat_lon_editor.commit();

                                        getZoneCode();

                                        String zone = zone_code.getString("zonecode", "");
                                        task.execute("http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=" + zone);
                                    }
                                } else {
                                    Intent i = new Intent(getActivity(), UserLocationActivity.class);
                                    startActivity(i);
                                }
                            }
                        })
                        .setSavedInstanceState(savedInstanceState)
                        .show();
            }
        });

        //피드백
        TextView feedback = (TextView)slide_menu.findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "jinjuu0u@gmail.com", null));
                startActivity(intent);
            }
        });
        //알람
        final CheckedTextView alarm_bell = (CheckedTextView)slide_menu.findViewById(R.id.alarm_bell);
        alarm_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarm_bell.isChecked()) {
                    alarm_bell.setChecked(false);
                } else {
                    alarm_bell.setChecked(true);
                }
            }
        });
        final CheckedTextView alarm_vibe = (CheckedTextView)slide_menu.findViewById(R.id.alarm_vibe);
        alarm_vibe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarm_vibe.isChecked()) {
                    alarm_vibe.setChecked(false);
                } else {
                    alarm_vibe.setChecked(true);
                }
            }
        });

        //오늘날짜,요일 설정
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        final SimpleDateFormat CurDateFormat = new SimpleDateFormat("dd");
        final String strCurDate = CurDateFormat.format(date);
        ((TextView) v.findViewById(R.id.home_date)).setText(strCurDate);
        ((TextView) v.findViewById(R.id.home_week)).setText(getDayOfWeek(date, true));

        //일정 추가
        v.findViewById(R.id.home_today_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.equals("null")){
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
                    Intent i = new Intent(getActivity(), ScheduleAddActivity.class);
                    i.putExtra("state", "insert");
                    startActivity(i);
                }
            }
        });


        //오늘 일정
        handlerSchedule = new DBHandler_schedule(getActivity());

        schedule_content = (LinearLayout)v.findViewById(R.id.schedule_content);
        ts_title = (TextView)v.findViewById(R.id.ts_title);
        ts_exp = (TextView)v.findViewById(R.id.ts_exp);
        ts_loc = (TextView)v.findViewById(R.id.ts_loc);
        ts_date = (TextView)v.findViewById(R.id.ts_date);

        schedule_data = new ArrayList<ScheduleInfo>();
        scheduleAdapter = new ScheduleAdapter(getActivity(),schedule_data);

        ScheduleThread scheduleThread = new ScheduleThread();
        scheduleThread.setDaemon(true);
        scheduleThread.start();

        schedule_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleAddActivity.class);
                intent.putExtra("s_primary", schedule_data.get(schedule_position).milli_date);
                intent.putExtra("state", "update");
                startActivity(intent);
            }
        });


        //날씨
        zone_code = getActivity().getSharedPreferences("zone",0);
        zone_code_editor = zone_code.edit();
        lat_lon = getActivity().getSharedPreferences("lat_lon", 0);
        lat_lon_editor= lat_lon.edit();
        ((LinearLayout)v.findViewById(R.id.home_weather1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                startActivity(intent);
            }
        });
        ((LinearLayout)v.findViewById(R.id.home_weather2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                startActivity(intent);
            }
        });
        loc = (TextView)v.findViewById(R.id.home_location);
        loc.setSelected(true);
        tem = (TextView)v.findViewById(R.id.home_temp);
        air = (TextView)v.findViewById(R.id.home_airflow);
        rain = (TextView)v.findViewById(R.id.home_rainfall);
        imageWeather = (ImageView) v.findViewById(R.id.image_weather);
        //권한 체크
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                gps = new gpsInfo(getActivity());
                task = new GetXMLTask();
                // GPS 사용유무 가져오기
                int userLoc_id = userLoc_set.getInt("set_id", -1);
                if(userLoc_id == 0 || userLoc_id == -1){
                    if (gps.isGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        lat_lon_editor.clear();
                        lat_lon_editor.putFloat("lat", (float) latitude);
                        lat_lon_editor.putFloat("lon", (float) longitude);
                        lat_lon_editor.commit();

                        getZoneCode();

                        String zone = zone_code.getString("zonecode", "");
                        task.execute("http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=" + zone);
                    }else {
                        final LovelyStandardDialog gpsDialog = new LovelyStandardDialog(getActivity());
                        gpsDialog.setTopColorRes(R.color.etc)
                                .setTopTitle("GPS")
                                .setTopTitleColor(Color.WHITE)
                                .setIcon(R.drawable.alert_info)
                                .setIconTintColor(Color.WHITE)
                                .setMessage("gps 기능을 사용할 수 없습니다.\n" +
                                        "설정창으로 이동하시겠습니까?")
                                .setPositiveButton("확인", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        getActivity().startActivity(i);
                                    }
                                })
                                .setNegativeButton("취소", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(getActivity(), "현 위치를 알 수 없어 가장 최신 위치로 설정합니다.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                    }
                } else {
                    if(zone_code.getString("zonecode","").equals("")) {
                        Intent i = new Intent(getActivity(), UserLocationActivity.class);
                        startActivity(i);
                    }else {
                        String zone = zone_code.getString("zonecode", "");
                        task.execute("http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=" + zone);
                    }
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getActivity(), "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        new TedPermission(getActivity())
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("Sbway 서비스를 사용하기 위해서는 위치 접근 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();



        //디데이
        handler = new DBHandler(getActivity());
        getDday = (TextView)v.findViewById(R.id.home_ddayres);
        ddaytxt = (TextView)v.findViewById(R.id.home_ddaytxt);
        ddaytxt.setSelected(true);
        v.findViewById(R.id.home_dday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DdayListActivity.class);
                startActivity(i);
            }
        });

        //메모
        handlerMemo = new DBHandler_memo(getActivity());
        memo_data = new ArrayList<MemoInfo>();
        memoAdapter = new MemoAdapter(getActivity(),memo_data);
        v.findViewById(R.id.home_memo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MemoListActivity.class);
                i.putExtra("memo_position", memo_position);
                startActivity(i);
            }
        });

        memo_content = (TextView)v.findViewById(R.id.memo_content);
        memo_location = (TextView)v.findViewById(R.id.memo_location);
        memo_date = (TextView)v.findViewById(R.id.memo_date);

        MemoThread memoThread = new MemoThread();
        memoThread.setDaemon(true);
        memoThread.start();

        //뉴스
        newsAsyncTask.execute(null, null, null);
        while(true) {
            try {
                Thread.sleep(1000);
                if (newsAsyncTask.flag == true) {
                    titlevec = newsAsyncTask.titlevec;
                    linkvec = newsAsyncTask.linkvec;
                    break; //반복문 종료
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        newsAdapter = new NewsAdapterHome(getActivity(),titlevec);
        newslist = (ListView)v.findViewById(R.id.home_news_list);
        newslist.setAdapter(newsAdapter);
        newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), NewsActivity.class);
                i.putExtra("link", linkvec.get(position));
                startActivity(i);
            }
        });
        newsThread thread = new newsThread();
        thread.setDaemon(true);
        thread.start();
        v.findViewById(R.id.news_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NewsPlusActivity.class);
                startActivity(i);
            }
        });

        //북마크
        v.findViewById(R.id.home_bookmark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), com.sbway.Bookmark.BookmarkActivity.class);
                startActivity(i);
            }
        });
        b_handler = new DBHandler_Bookmark(getActivity());
        b_listData = new ArrayList<BookmarkInfo>();
        b_adapter = new Bookmark_Adapter(getActivity(), b_listData);
        bookmarklist = (ListView)v.findViewById(R.id.home_bookmark_list);
        bookmarklist.setAdapter(b_adapter);
        bookmarklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ResultActivity.class);
                intent.putExtra("path", b_listData.get(position).path);
                intent.putExtra("start", b_listData.get(position).slng + "," + b_listData.get(position).slat);
                intent.putExtra("end", b_listData.get(position).elng + "," + b_listData.get(position).elat);
                intent.putExtra("startplace", b_listData.get(position).start_place);
                intent.putExtra("endplace", b_listData.get(position).end_place);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


        //동기화
        sync_sp = getActivity().getSharedPreferences("sync", 0);
        sync_editor =  sync_sp.edit();

        sync_btn = (TextView)slide_menu.findViewById(R.id.sync_btn);
        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SyncActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        final TextView sync_tv = (TextView)slide_menu.findViewById(R.id.sync_set);
        String sync_text = sync_sp.getString("sync_text", "");
        ((TextView)slide_menu.findViewById(R.id.sync_set_text)).setText(sync_text);
        sync_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "자동 동기화 off", "20초마다", "6시간마다", "12시간마다", "24시간마다" };
                final LovelyChoiceDialog choiceDialog = new LovelyChoiceDialog(getActivity());
                choiceDialog.setTopColorRes(R.color.etc)
                        .setTopTitleColor(Color.WHITE)
                        .setTopTitle("동기화 시간 설정")
                        .setIcon(R.drawable.sync_img)
                        .setItems(items, new LovelyChoiceDialog.OnItemSelectedListener<CharSequence>() {
                            @Override
                            public void onItemSelected(int id, CharSequence item) {
                                Toast.makeText(getActivity(), items[id]+ " 선택했습니다.", Toast.LENGTH_SHORT).show();
                                sync_editor.clear();
                                sync_editor.putString("sync_text", items[id].toString());
                                sync_editor.putInt("set_id", id);
                                switch (id) {
                                    case 0:
                                        getActivity().stopService(new Intent(getActivity(), SyncService.class));
                                        break;
                                    case 1:
                                        getActivity().stopService(new Intent(getActivity(),SyncService.class));
                                        getActivity().startService(new Intent(getActivity(), SyncService.class));
                                        sync_editor.putInt("sync_set_time", 20000);
                                        //7200000
                                        break;
                                    case 2:
                                        getActivity().stopService(new Intent(getActivity(),SyncService.class));
                                        getActivity().startService(new Intent(getActivity(), SyncService.class));
                                        sync_editor.putInt("sync_set_time", 21600000);
                                        //21600000
                                        break;
                                    case 3:
                                        getActivity().stopService(new Intent(getActivity(),SyncService.class));
                                        getActivity().startService(new Intent(getActivity(), SyncService.class));
                                        sync_editor.putInt("sync_set_time",43200000);
                                        //43200000
                                        break;
                                    case 4:
                                        getActivity().stopService(new Intent(getActivity(),SyncService.class));
                                        getActivity().startService(new Intent(getActivity(), SyncService.class));
                                        sync_editor.putInt("sync_set_time",86400000);
                                        //86400000
                                        break;
                                }
                                sync_editor.commit();
                                ((TextView)slide_menu.findViewById(R.id.sync_set_text)).setText(items[id].toString());
                            }
                        })
                        .setSavedInstanceState(savedInstanceState)
                        .show();
            }
        });


        return v;
    }


    //요일 설정
    public String getDayOfWeek(Date date, boolean korean) {
        String[][] week = {{"일", "Sun"}, {"월", "Mon"}, {"화", "Tue"}, {"수", "Wen"}, {"목", "Thu"}, {"금", "Fri"}, {"토", "Sat"}};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(korean) {
            return week[cal.get(Calendar.DAY_OF_WEEK)-1][0];
        }
        else {
            return week[cal.get(Calendar.DAY_OF_WEEK)-1][1];
        }
    }

    class rcvJson extends Thread {
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        RSSurl.openStream(), "UTF-8"));
                getJSON = in.readLine();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    //날씨
    public void getZoneCode() {
        try {
            String Local1;
            String Local2;
            String Local3;
            String temp;
            JSONArray JSA;

            Double lat = Double.parseDouble(lat_lon.getFloat("lat", 0f) + "");
            Double lon = Double.parseDouble(lat_lon.getFloat("lon", 0f)+"");

            RSSurl = new URL("https://apis.daum.net/local/geo/coord2addr?apikey=1b1bd3d5afdef76fdcff4b8e97cb2465&longitude="+lon+"&latitude="+lat+"&inputCoordSystem=WGS84&output=json");
            RJ = new rcvJson();
            RJ.start();
            RJ.join();
            JStoken = new JSONObject(getJSON);
            Local1 = JStoken.getString("name1");
            Local2 = JStoken.getString("name2");
            Local3 = JStoken.getString("name3");

            RSSurl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/top.json.txt");
            RJ = new rcvJson();
            RJ.start();
            RJ.join();
            JSA = new JSONArray(getJSON);

            temp="";

            for(int i=0; i < JSA.length(); i++){
                JStoken = JSA.getJSONObject(i);
                if(JStoken.getString("value").equals(Local1)){
                    temp += JStoken.getString("code");
                    break;
                }
            }

            RSSurl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/mdl."+temp+".json.txt");
            RJ = new rcvJson();
            RJ.start();
            RJ.join();
            JSA = new JSONArray(getJSON);

            temp="";

            for(int i=0; i < JSA.length(); i++){
                JStoken = JSA.getJSONObject(i);
                if(JStoken.getString("value").equals(Local2)){
                    temp += JStoken.getString("code");
                    break;
                }
            }

            RSSurl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf."+temp+".json.txt");
            RJ = new rcvJson();
            RJ.start();
            RJ.join();
            JSA = new JSONArray(getJSON);

            temp="";

            for(int i=0; i < JSA.length(); i++){
                JStoken = JSA.getJSONObject(i);
                zoneCode = JStoken.getString("code");
                if(JStoken.getString("value").equals(Local3))
                    break;
            }
            zone_code_editor.putString("zonecode", zoneCode);
            zone_code_editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    //날씨 파싱
    private class GetXMLTask extends AsyncTask<String, Void, Document>{

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {

            String s = "";
            String wfKor = "";

            //data태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
            NodeList nodeList = doc.getElementsByTagName("item");
            Element fstElmnt = (Element) nodeList.item(0);

            NodeList cityList = fstElmnt.getElementsByTagName("category");
            String city = cityList.item(0).getChildNodes().item(0).getNodeValue();

            NodeList wfKorList = fstElmnt.getElementsByTagName("wfKor");
            wfKor = wfKorList.item(0).getChildNodes().item(0).getNodeValue();

            NodeList tempList = fstElmnt.getElementsByTagName("temp");
            String temp = tempList.item(0).getChildNodes().item(0).getNodeValue();
            tem.setText(temp + "˚");

            NodeList popList = fstElmnt.getElementsByTagName("pop");//강수
            String pop = popList.item(0).getChildNodes().item(0).getNodeValue();
            rain.setText(pop+"%");

            NodeList wdKorList = fstElmnt.getElementsByTagName("wdKor");//풍향
            String wdKor = wdKorList.item(0).getChildNodes().item(0).getNodeValue();
            NodeList wsList = fstElmnt.getElementsByTagName("ws");//풍속
            String ws = wsList.item(0).getChildNodes().item(0).getNodeValue();
            air.setText(wdKor+" "+ws.substring(0,3)+"m/s");

            switch (wfKor) {
                case "맑음":
                    weather = "맑음";
                    imageWeather.setBackgroundResource(R.drawable.sun);
                    break;
                case "구름 조금":
                    weather = "구름 조금";
                    imageWeather.setBackgroundResource(R.drawable.little_cloud);
                    break;
                case "흐림":
                    weather = "흐림";
                    imageWeather.setBackgroundResource(R.drawable.cloud);
                    break;
                case "구름 많음":
                    weather = "구름 많음";
                    imageWeather.setBackgroundResource(R.drawable.many_cloud);
                    break;
                case "비":
                    weather = "비";
                    imageWeather.setBackgroundResource(R.drawable.rain);
                    break;
            }
            removeAllPreferences();
            savePreferences();
            loc.setText(city);
        }
    }//end inner class - GetXMLTask

    // 값 저장하기
    private void savePreferences(){
        SharedPreferences spr = getActivity().getSharedPreferences("spr", Context.MODE_PRIVATE);
        SharedPreferences.Editor weather_editor = spr.edit();
        weather_editor.putString("weather", weather);
        weather_editor.putString("temp", tem.getText().toString());
        weather_editor.commit();
    }

    // 값(ALL Data) 삭제하기
    private void removeAllPreferences(){
        SharedPreferences spr = getActivity().getSharedPreferences("spr", Context.MODE_PRIVATE);
        SharedPreferences.Editor weather_editor = spr.edit();
        weather_editor.clear();
        weather_editor.commit();
    }

    class ScheduleThread extends Thread {
        public void run() {
            while (true) {
                for (int i=0; i<schedule_data.size();i++) {
                    scheduleHandler.sendEmptyMessage(i);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    Handler scheduleHandler = new Handler() {
        public void handleMessage(Message msg) {
            ts_title.setText(schedule_data.get(msg.what).stitle);
            ts_exp.setText(schedule_data.get(msg.what).sexplain);
            if (schedule_data.get(msg.what).local.equals("장소를 설정하세요.")) {
                ts_loc.setText("장소 없음");
            } else {
                ts_loc.setText(schedule_data.get(msg.what).local);
            }
            ts_date.setText(schedule_data.get(msg.what).s_syear + "." + schedule_data.get(msg.what).s_smonth + "." + schedule_data.get(msg.what).s_sday + " - " + schedule_data.get(msg.what).s_eyear + "." + schedule_data.get(msg.what).s_emonth + "." + schedule_data.get(msg.what).s_eday);
            schedule_position = msg.what;
        }
    };

    class MemoThread extends Thread {
        public void run() {
            while (true) {
                for (int i=0; i<memo_data.size();i++) {
                    memoHandler.sendEmptyMessage(i);
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    Handler memoHandler = new Handler() {
        public void handleMessage(Message msg) {
            memo_content.setText(memo_data.get(msg.what).memo);
            memo_location.setText(memo_data.get(msg.what).local);
            memo_date.setText(DateFormat.format("yy.MM.dd", new Date(memo_data.get(msg.what).writeDate)).toString());
            memo_position = msg.what;
        }
    };
    class newsThread extends Thread {
        public void run() {
            while (true) {
                mHandler.sendEmptyMessage(0);
                try { Thread.sleep(3000); } catch (InterruptedException e) {;}
            }
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what ==0) {
                String title = titlevec.get(0), link = linkvec.get(0);
                titlevec.remove(0); linkvec.remove(0);
                titlevec.add(title); linkvec.add(link);
                newsAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onResume() {
        getZoneCode();
        loc_insert();
        String zone = zone_code.getString("zonecode", "");
        task = new GetXMLTask();
        task.execute("http://web.kma.go.kr/wid/queryDFSRSS.jsp?zone=" + zone);
        displayMemoList();
        displaySchedule();
        displayBookmarkList();
        if(dday_sp.getString("d_milli","").equals("")||auto_login.getString("id", "null").equals("null")){
            getDday.setText("D-?");
            ddaytxt.setText("디데이 추가하기");
        }else {
            dday_milli = dday_sp.getString("d_milli","");
            displayDdayList(dday_milli);
        }
        if(schedule_data.size()==0){
            ts_title.setText("");
            ts_exp.setText("");
            ts_date.setText("");
            ts_loc.setText("");
        }
        super.onResume();
    }

    //디데이
    protected void displayDdayList(String milli) {
        DdayInfo dday = handler.select(milli);
        try {
            if( dday == null ) return;

            Calendar cal = Calendar.getInstance();

            int year = dday.d_year;
            int month = dday.d_month;
            int day = dday.d_day;
            Calendar cal9 = Calendar.getInstance();
            cal9.set(year, month-1, day);
            long diffday = cal9.getTimeInMillis() - cal.getTimeInMillis();
            String rs;
            if(dday.type==0) {
                if (diffday < 0) { //D+인지 D-인지 구별
                    diffday = diffday * -1; //부호 바꾸는거
                    String res = Long.toString(diffday / (1000 * 60 * 60 * 24)); //일로 환산
                    rs = ("D+" + res);
                } else {
                    String res = Long.toString(diffday / (1000 * 60 * 60 * 24));
                    rs = ("D-" + res);
                }
            }else {
                if (diffday <= 0) { //D+인지 D-인지 구별
                    diffday = diffday * -1; //부호 바꾸는거
                    String res = Long.toString(diffday / (1000 * 60 * 60 * 24) + 1); //일로 환산
                    rs = ("D+" + res);
                } else {
                    String res = Long.toString(diffday / (1000 * 60 * 60 * 24) - 1);
                    rs = ("D-" + res);
                }
            }
            dday.ddayres = rs;
            if (rs != String.valueOf(0)) {
                getDday.setText(rs);
                ddaytxt.setText(dday.d_title);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //메모
    protected void displayMemoList() {
        SharedPreferences sp = this.getActivity().getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor= sp.edit();
        String id = sp.getString("id", "null");
        // handler 객체로부터 모든 메모를 읽어 옴
        Cursor cursor = handlerMemo.selectAll(id);
        try {
            // 읽어 들인 데이터가 없으면 함수 종료
            if( cursor == null ) return;
            //memoList에 저장된 목록을 모두 삭제함
            removeAllList();
            // 커서를 통해 읽어 온 데이터를 memoList에 추가함
            do {
                MemoInfo memo = new MemoInfo();
                memo.writeDate = cursor.getLong(0);
                memo.memo = cursor.getString( 1 );
                memo.local = cursor.getString( 2 );
                memo.member_id = cursor.getString(3);
                memo_data.add(memo);
            } while( cursor.moveToNext() );
            // 커서를 닫고 listAdapter 객체에게 알려 갱신된 정보가
            // 화면에 표시되도록 함
            cursor.close();
            memoAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // memoList에서 모든 목록을 삭제하는 함수
    protected void removeAllList() {
        memo_data.removeAll(memo_data);
    }

    protected void removeAllLists() {
        schedule_data.removeAll(schedule_data);
    }


    //오늘 일정
    protected void displaySchedule() {
        removeAllLists();
        SharedPreferences sp = this.getActivity().getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor= sp.edit();
        String id = sp.getString("id", "null");
        // handler 객체로부터 모든 메모를 읽어 옴
        Cursor cursor = handlerSchedule.selectAll(id);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor == null ) return;
        // 커서를 통해 읽어 온 데이터를 memoList에 추가함
        do {
            try {
                ScheduleInfo s = new ScheduleInfo();

                //오늘 날짜 구하기
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
                String n_date = sdf.format(new Date());

                Date now_date = sdf.parse(n_date);
                Date start_date = sdf.parse(String.valueOf(cursor.getInt(1))+"."+cursor.getInt(2)+"."+cursor.getInt(3));
                Date end_date = sdf.parse(String.valueOf(cursor.getInt(6))+"."+cursor.getInt(7)+"."+cursor.getInt(8));

                long diff_date = end_date.getTime() - start_date.getTime();
                long diff_schedule = diff_date/(24*60*60*1000);

                if(now_date.equals(start_date)) {
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
                    s.salarm = cursor.getString(14);
                    s.salarm_time = cursor.getString(15);
                    s.srepeat = cursor.getString(16);
                    s.srepeat_end = cursor.getString(17);
                    schedule_data.add(s);
                }else if(now_date.equals(end_date)){
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
                    s.salarm = cursor.getString(14);
                    s.salarm_time = cursor.getString(15);
                    s.srepeat = cursor.getString(16);
                    s.srepeat_end = cursor.getString(17);
                    schedule_data.add(s);
                }else if(start_date.before(now_date)&&end_date.after(now_date)&&diff_schedule>0){
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
                    s.salarm = cursor.getString(14);
                    s.salarm_time = cursor.getString(15);
                    s.srepeat = cursor.getString(16);
                    s.srepeat_end = cursor.getString(17);
                    schedule_data.add(s);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } while( cursor.moveToNext() );
        // 커서를 닫고 listAdapter 객체에게 알려 갱신된 정보가
        // 화면에 표시되도록 함
        cursor.close();
        scheduleAdapter.notifyDataSetChanged();
    }

    protected void loc_insert() {
        Double lat = Double.parseDouble(lat_lon.getFloat("lat", 0f) + "");
        Double lon = Double.parseDouble(lat_lon.getFloat("lon", 0f) + "");
        String memid = auto_login.getString("id", "null");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss");
        String now = sdf.format(new Date());
        if (memid.equals(null) == false) {
            UserLoc_DBInsert loc_db = new UserLoc_DBInsert(memid, lon, lat, now);
            loc_db.start();
        }
    }

    //북마크
    protected void displayBookmarkList() {
        SharedPreferences sp = this.getActivity().getSharedPreferences("setting", 0);
        String id = sp.getString("id", "null");
        Cursor cursor = b_handler.selectAll(id);
        getPath = "";
        c="";
        if( cursor == null ) return;
        b_removeAllList();
        do {
            BookmarkInfo search = new BookmarkInfo();
            search.id = cursor.getInt(0);
            search.path = cursor.getString(2);
            search.start_place = cursor.getString(3);
            search.end_place = cursor.getString(4);
            search.slat = cursor.getString(5);
            search.slng = cursor.getString(6);
            search.elat = cursor.getString(7);
            search.elng = cursor.getString(8);

            getPath = search.path;
            b_listData.add(search);
        } while( cursor.moveToNext());
        cursor.close();
        b_adapter.notifyDataSetChanged();
    }
    protected void b_removeAllList() {
        b_listData.removeAll(b_listData);
        b_adapter.notifyDataSetChanged();
    }

    static public void sync_result(String result){
        //Log.e("regist_result", result);
        controlMysql.active=false;
        if(result.contains("")){
            Log.d("동기화", "성공");
        }else{
            Log.d("동기화", "실패");
        }
    }
}
