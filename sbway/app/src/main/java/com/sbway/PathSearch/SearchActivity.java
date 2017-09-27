package com.sbway.PathSearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.sbway.R;

import java.util.ArrayList;

import com.sbway.SwipeDelete.*;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;


public class SearchActivity extends AppCompatActivity {
    EditText start;
    EditText end;
    String s,e,path,getPath,c;
    int a, b, ab, get_d=0;
    String d[];

    ArrayList<SearchInfo> data;
    MySearchAdapter adapter;
    ListView list;

    String splace, eplace, slat, slng, elat, elng;

    SearchInfo sinfo;
    DBHandler_search handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        start = (EditText)findViewById(R.id.txt_setStart_search);
        end = (EditText)findViewById(R.id.txt_setEnd_search);

        Intent intent = getIntent();
        String stxt = intent.getStringExtra("start");
        String etxt = intent.getStringExtra("end");

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        if(stxt != null && etxt == null){
            splace = intent.getStringExtra("place");
            slat = intent.getStringExtra("lat");
            slng = intent.getStringExtra("lng");
            start.setText(splace);
            savePreferences();
            if(pref != null){
                eplace = pref.getString("end_txt", "");
                elat = pref.getString("elat", "");
                elng = pref.getString("elng", "");
                end.setText(eplace);
            }
            Log.d("시작", splace +"."+slat +"."+slng);
        }else if(etxt != null){
            eplace = intent.getStringExtra("place");
            elat = intent.getStringExtra("lat");
            elng = intent.getStringExtra("lng");
            end.setText(eplace);
            savePreferences();
            if(pref != null){
                splace = pref.getString("start_txt", "");
                slat = pref.getString("slat", "");
                slng = pref.getString("slng", "");
                start.setText(splace);
            }
            Log.d("도착", eplace +"."+elat +"."+elng);
        }

        s = start.getText().toString();
        e = end.getText().toString();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), placeResultActivity.class);
                intent.putExtra("출발", "출발");
                startActivityForResult(intent, 2);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), placeResultActivity.class);
                intent1.putExtra("도착", "도착");
                startActivityForResult(intent1, 2);
            }
        });

        handler = new DBHandler_search(this);
        data = new ArrayList<SearchInfo>();
        adapter = new MySearchAdapter(this, data);
        list = (ListView)findViewById(R.id.search_list);
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
                                    final LovelyStandardDialog memoDialog = new LovelyStandardDialog(SearchActivity.this);
                                    memoDialog.setTopColorRes(R.color.bookmark)
                                            .setTopTitle("BOOKMARK")
                                            .setTopTitleColor(Color.WHITE)
                                            .setTitle("검색 기록 삭제")
                                            .setIcon(R.drawable.alert_delete)
                                            .setIconTintColor(Color.WHITE)
                                            .setMessage("기록을 삭제하시겠습니까?")
                                            .setPositiveButton("확인", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    int i = position;
                                                    removeList(i);
                                                    adapter.notifyDataSetChanged();
                                                    Toast.makeText(getApplicationContext(), "기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setNegativeButton("취소", null)
                                            .show();
                                }
                            }
                        });
        list.setOnTouchListener(touchListener);

        //출발지 도착지 바꾸기
        findViewById(R.id.btn_switch_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = start.getText().toString();
                e = end.getText().toString();
                if((s.length()==0||e.length()==0)||(s.length()==0&&e.length()==0)) {
                    Toast.makeText(getApplicationContext(), "입력된 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    String clat = slat;
                    slat = elat;
                    elat = clat;
                    String clng = slng;
                    slng = elng;
                    elng = clng;
                    start.setText(e);
                    end.setText(s);
                    s = start.getText().toString();
                    e = end.getText().toString();
                }
                displaySearchList();
                d=c.split("\\.");
                get_d=0;
                for(int i=0; i<d.length; i++){if(d[i].contains("2")){get_d=1;}}
            }
        });

        //검색 버튼
        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = start.getText().toString();
                e = end.getText().toString();
                path = s + " → " + e;
                sinfo = new SearchInfo();

                displaySearchList();

                d = c.split("\\.");
                get_d = 0;
                for (int i = 0; i < d.length; i++) {
                    if (d[i].contains("2")) {
                        get_d = 1;
                    }
                }

                SharedPreferences sp = getApplicationContext().getSharedPreferences("setting", 0);
                SharedPreferences.Editor editor= sp.edit();
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                if (s.length() != 0 && e.length() != 0) {
                    try {
                        Log.d("ㅇ", "들어옴1");
                        if (getPath.length() == 0) {    //디비가 없을 때
                            Log.d("ㅇ", "들어옴2");
                            sinfo.path = path;
                            sinfo.start_place = s;
                            sinfo.end_place = e;
                            sinfo.slat = slat;
                            sinfo.slng = slng;
                            sinfo.elat = elat;
                            sinfo.elng = elng;

                            data.add(0, sinfo);
                            String id = sp.getString("id", "null");
                            handler.insert(sinfo.path, sinfo.start_place, sinfo.end_place, sinfo.slat, sinfo.slng, sinfo.elat, sinfo.elng,id);
                            list.setAdapter(adapter);

                            intent.putExtra("path", path);
                            intent.putExtra("start", slng + "," + slat);
                            intent.putExtra("end", elng + "," + elat);
                            intent.putExtra("startplace", s);
                            intent.putExtra("endplace", e);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        } else if (getPath.length() != 0) {
                            Log.d("ㅇ", "들어옴3");
                            if (get_d == 1) {       //디비가 있는데 경로랑 똑같을 때
                                Log.d("ㅇ", "들어옴4");
                                intent.putExtra("path", path);
                                intent.putExtra("start", slng + "," + slat);
                                intent.putExtra("end", elng + "," + elat);
                                intent.putExtra("startplace", s);
                                intent.putExtra("endplace", e);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            } else {
                                Log.d("ㅇ", "들어옴5");
                                sinfo.path = path;
                                sinfo.start_place = s;
                                sinfo.end_place = e;
                                sinfo.slat = slat;
                                sinfo.slng = slng;
                                sinfo.elat = elat;
                                sinfo.elng = elng;

                                data.add(0, sinfo);


                                String id = sp.getString("id", "null");
                                handler.insert(sinfo.path, sinfo.start_place, sinfo.end_place, sinfo.slat, sinfo.slng, sinfo.elat, sinfo.elng,id);
                                list.setAdapter(adapter);

                                intent.putExtra("path", path);
                                intent.putExtra("start", slng + "," + slat);
                                intent.putExtra("end", elng + "," + elat);
                                intent.putExtra("startplace", s);
                                intent.putExtra("endplace", e);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("오류", String.valueOf(e));
                    }
                } else if (s.length() == 0 || e.length() == 0) {
                    Toast.makeText(getApplicationContext(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent2 = new Intent(getApplicationContext(), ResultActivity.class);
                intent2.putExtra("path", data.get(position).path);
                intent2.putExtra("start", data.get(position).slng+","+data.get(position).slat);
                intent2.putExtra("end", data.get(position).elng+","+data.get(position).elat);
                intent2.putExtra("startplace", data.get(position).start_place);
                intent2.putExtra("endplace", data.get(position).end_place);
                Log.d("에이",data.get(position).start_place);
                Log.d("에이",data.get(position).end_place);
                intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent2);
            }
        });
    }

    @Override
    protected void onResume() {
        displaySearchList();
        super.onResume();
    }

    String isSame(String get_path){
        String[] split_path;
        a = 0; b = 0;
        ab = 0;
        if(get_path.length() > 0 && s.length() != 0 && e.length() != 0) {
            split_path = get_path.split(" → ");
            a = split_path[0].equals(s) ? 1 : 2;
            b = split_path[1].equals(e) ? 1 : 2;
            ab=a+b;
            c+=ab+".";
        }
        return c;
    }

    // 값 저장하기
    private void savePreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(start.getText().toString().length() != 0) {
            editor.putString("start_txt", start.getText().toString());
            editor.putString("slat", slat);
            editor.putString("slng", slng);
        }else if(end.getText().toString().length() != 0) {
            editor.putString("end_txt", end.getText().toString());
            editor.putString("elat", elat);
            editor.putString("elng", elng);
        }
        editor.commit();
    }

    // 값(ALL Data) 삭제하기
    private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    protected void displaySearchList() {
        SharedPreferences sp = this.getApplicationContext().getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor= sp.edit();
        String id = sp.getString("id", "null");
        Cursor cursor = handler.selectAll(id);
        getPath = "";
        c="";
        if( cursor == null ) return;
        removeAllList();
        do {
            SearchInfo search = new SearchInfo();
            search.id = cursor.getInt(0);
            search.path = cursor.getString(1);
            search.start_place = cursor.getString(2);
            search.end_place = cursor.getString(3);
            search.slat = cursor.getString(4);
            search.slng = cursor.getString(5);
            search.elat = cursor.getString(6);
            search.elng = cursor.getString(7);

            getPath = search.path;
            isSame(getPath);
            data.add(search);
        } while( cursor.moveToNext());
        // 커서를 닫고 listAdapter 객체에게 알려 갱신된 정보가
        // 화면에 표시되도록 함
        cursor.close();
        adapter.notifyDataSetChanged();
    }
    // memoList에서 모든 목록을 삭제하는 함수
    protected void removeAllList() {
        data.removeAll(data);
    }

    // 데이터가 수정되었을 때 특정 위치의 목록을 삭제하고
    // 변경된 내용이 하면에 표시되도록 하는 함수
    protected void removeList( int position ) {
        SearchInfo search = data.get( position );
        handler.delete(search.id);
        data.remove( position );
        adapter.notifyDataSetChanged();
    }
}
