package com.sbway.Dday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.sbway.Bookmark.BookmarkActivity;
import com.sbway.Login.LoginActivity;
import com.sbway.R;
import com.sbway.SwipeDelete.SwipeDismissListViewTouchListener;
import com.sbway.Weather.WeatherActivity;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DdayListActivity extends AppCompatActivity {
    DBHandler handler;
    ArrayList<DdayInfo> ddayList;
    ListView list;
    MyDdayAdapter adapter;
    ImageView imageView;

    ArrayList<String> id_list = new ArrayList<String>();

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    SharedPreferences d_sp;
    SharedPreferences.Editor d_editor;

    String d_res, d_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dday_list);

        d_sp = getSharedPreferences("dday", 0);
        d_editor= d_sp.edit();

        handler = new DBHandler(this);
        ddayList = new ArrayList<DdayInfo>();
        adapter = new MyDdayAdapter(this,ddayList);

        list = (ListView)findViewById(R.id.ddaylist);
        list.setAdapter(adapter);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        list,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {
                                    final LovelyStandardDialog ddayDialog = new LovelyStandardDialog(DdayListActivity.this);
                                    ddayDialog.setTopColorRes(R.color.dday)
                                            .setTopTitle("D-DAY")
                                            .setTopTitleColor(Color.WHITE)
                                            .setTitle("디데이 삭제")
                                            .setIcon(R.drawable.alert_delete)
                                            .setIconTintColor(Color.WHITE)
                                            .setMessage("디데이를 삭제하시겠습니까?")
                                            .setPositiveButton("확인", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    int i = position;
                                                    removeList(i);
                                                    adapter.notifyDataSetChanged();
                                                    Toast.makeText(getApplicationContext(), "디데이가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setNegativeButton("취소", null)
                                            .show();
                                }
                            }
                        });
        list.setOnTouchListener(touchListener);
        list.setOnScrollListener(touchListener.makeScrollListener());
        displayDdayList();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), DdayAddActivity.class);
                i.putExtra("status", "update");
                i.putExtra("d_milli", id_list.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                d_res = ((TextView)view.findViewById(R.id.dday_17)).getText().toString();
                d_title = ((TextView)view.findViewById(R.id.dday_title_17)).getText().toString();

                imageView = (ImageView)view.findViewById(R.id.dday_select);

                final LovelyStandardDialog ddayDialog = new LovelyStandardDialog(DdayListActivity.this);
                ddayDialog.setTopColorRes(R.color.dday)
                        .setTopTitle("D-DAY")
                        .setTopTitleColor(Color.WHITE)
                        .setIcon(R.drawable.alert_checked)
                        .setIconTintColor(Color.WHITE)
                        .setMessage("홈 화면에 등록하시겠습니까?")
                        .setPositiveButton("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                d_editor.clear();
                                d_editor.putString("d_milli", id_list.get(position));
                                d_editor.putInt("img_position", position);
                                d_editor.commit();

                                imageView.setBackgroundResource(R.drawable.check);

                                displayDdayList();
                                Toast.makeText(getApplicationContext(), "홈 화면에 등록되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                return true;
            }
        });

        findViewById(R.id.add_dday_17).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getApplicationContext().getSharedPreferences("setting", 0);
                editor= sp.edit();
                String id = sp.getString("id", "null");
                if(id.equals("null")==false){
                    Intent intent = new Intent(getApplicationContext(), DdayAddActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("status", "insert");
                    startActivity(intent);
                }else {
                    final LovelyStandardDialog ddayDialog = new LovelyStandardDialog(DdayListActivity.this);
                    ddayDialog.setTopColorRes(R.color.dday)
                            .setTopTitle("D-DAY")
                            .setTopTitleColor(Color.WHITE)
                            .setIcon(R.drawable.alert_info)
                            .setIconTintColor(Color.WHITE)
                            .setMessage("로그인 후 이용가능합니다. 로그인 창으로 이동하시겠습니까?")
                            .setPositiveButton("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(DdayListActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }
            }
        });


        /****************하단버튼***************/

        findViewById(R.id.mainbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.memo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.sbway.Memo.MemoListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.dday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DdayListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.news).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.sbway.News.NewsPlusActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.bookmark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookmarkActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    protected void displayDdayList() {
        sp = this.getApplicationContext().getSharedPreferences("setting", 0);
        editor= sp.edit();
        String id = sp.getString("id", "null");
        Cursor cursor = handler.selectAll(id);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor == null ) return;
        removeAllList();
        do {
            DdayInfo dday = new DdayInfo();
            dday.milli_date = cursor.getString(0);
            dday.status = cursor.getString(8);
            dday.d_year = cursor.getInt(2);
            dday.d_month = cursor.getInt(3);
            dday.d_day = cursor.getInt(4);
            dday.d_title = cursor.getString(5);

            id_list.add(dday.milli_date);

            Calendar cal = Calendar.getInstance();

            int year = cursor.getInt(2);
            int month = cursor.getInt(3);
            int day = cursor.getInt(4);
            Calendar cal9 = Calendar.getInstance();
            cal9.set(year, month-1, day);
            long diffday = cal9.getTimeInMillis() - cal.getTimeInMillis();
            String rs;
            if(Integer.parseInt(cursor.getString(6))==0) {
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
            ddayList.add(dday);
        } while( cursor.moveToNext() );
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    protected void removeAllList() {
        ddayList.removeAll(ddayList);
    }

    // 데이터가 수정되었을 때 특정 위치의 목록을 삭제하고
    // 변경된 내용이 하면에 표시되도록 하는 함수
    protected void removeList( int position ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss");
        String now = sdf.format(new Date());
        DdayInfo dday = ddayList.get(position);
        handler.delete(dday.milli_date,now,"d");
        ddayList.remove( position );
        adapter.notifyDataSetChanged();
    }
}