package com.sbway.Memo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.sbway.Bookmark.BookmarkActivity;
import com.sbway.Dday.DdayListActivity;
import com.sbway.Login.LoginActivity;
import com.sbway.R;
import com.sbway.SwipeDelete.SwipeDismissListViewTouchListener;
import com.sbway.Weather.WeatherActivity;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;

public class MemoListActivity extends AppCompatActivity {
    DBHandler_memo handler;
    ArrayList<MemoInfo> memoList;
    ListView list;
    MyMemoAdapter adapter;

    int memo_position=0;

    SharedPreferences sharedPreferences;
    String member_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        sharedPreferences = getSharedPreferences("setting", 0);
        member_id = sharedPreferences.getString("id","null");

        handler = new DBHandler_memo(this);
        memoList = new ArrayList<MemoInfo>();
        adapter = new MyMemoAdapter(this,memoList);

        Intent intent = getIntent();
        memo_position = intent.getIntExtra("memo_position",0);

        list = (ListView)findViewById(R.id.memolist_16);
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
                                    final LovelyStandardDialog memoDialog = new LovelyStandardDialog(MemoListActivity.this);
                                    memoDialog.setTopColorRes(R.color.memo)
                                            .setTopTitle("MEMO")
                                            .setTopTitleColor(Color.WHITE)
                                            .setTitle("메모 삭제")
                                            .setIcon(R.drawable.alert_delete)
                                            .setIconTintColor(Color.WHITE)
                                            .setMessage("메모를 삭제하시겠습니까?")
                                            .setPositiveButton("확인", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    int i = position;
                                                    removeList(i);
                                                    adapter.notifyDataSetChanged();
                                                    Toast.makeText(getApplicationContext(), "메모가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setNegativeButton("취소", null)
                                            .show();
                                }
                            }
                        });
        list.setOnTouchListener(touchListener);
        list.setOnScrollListener(touchListener.makeScrollListener());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MemoAddActivity.class);
                intent.putExtra("state", "update");
                intent.putExtra("writedate", String.valueOf(memoList.get(i).writeDate));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        list.requestFocusFromTouch();
        adapter.notifyDataSetChanged();
        list.setSelection(memo_position);

        displayMemoList();

        findViewById(R.id.add_memo_16).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(member_id.equals("null")&&!MemoListActivity.this.isFinishing()){
                    final LovelyStandardDialog memoDialog = new LovelyStandardDialog(MemoListActivity.this);
                    memoDialog.setTopColorRes(R.color.memo)
                            .setTopTitle("MEMO")
                            .setTopTitleColor(Color.WHITE)
                            .setIcon(R.drawable.alert_info)
                            .setIconTintColor(Color.WHITE)
                            .setMessage("로그인 후 이용가능합니다. 로그인 창으로 이동하시겠습니까?")
                            .setPositiveButton("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MemoListActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }else {
                    Intent intent = new Intent(getApplicationContext(), MemoAddActivity.class);
                    intent.putExtra("state", "insert");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        /****************하단버튼***************/

        findViewById(R.id.mainbtnM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        findViewById(R.id.scheduleM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.memoM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.ddayM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DdayListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.newsM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.sbway.News.NewsPlusActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.bookmarkM).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookmarkActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void displayMemoList() {
        // handler 객체로부터 모든 메모를 읽어 옴
        Cursor cursor = handler.selectAll(member_id);
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
            memoList.add(memo);
        } while( cursor.moveToNext() );
        // 커서를 닫고 listAdapter 객체에게 알려 갱신된 정보가
        // 화면에 표시되도록 함
        cursor.close();
        adapter.notifyDataSetChanged();
    }
    // memoList에서 모든 목록을 삭제하는 함수
    protected void removeAllList() {
        memoList.removeAll(memoList);
    }

    // 데이터가 수정되었을 때 특정 위치의 목록을 삭제하고
    // 변경된 내용이 하면에 표시되도록 하는 함수
    protected void removeList( int position ) {
        MemoInfo memo = memoList.get( position );
        handler.delete(String.valueOf(memo.writeDate), memo.member_id);
        memoList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
