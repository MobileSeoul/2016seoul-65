package com.sbway.Memo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sbway.R;
import com.sbway.gpsInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class MemoAddActivity extends AppCompatActivity implements View.OnClickListener{

    DBHandler_memo handler;
    // 수정을 위한 edit_id, edit_position(목록에서의 항목 위치)
    // 지정을 위한 변수
    int edit_id = 0, edit_position = 0;
    String state="";
    String writedate="";
    TextView memotitle;
    SharedPreferences auto_login;

    // 목록표시 엑티비티에서 호출되었는지(isCalled),
    // 새로운 데이터가 삽입되었는지(isadded) 판단하기 위한 변수
    boolean isCalled = false, isadded = false;

    // 뷰 컨트를을 위한 객체용 변수
    Button submitBtn;
    EditText memo;

    // 메모 내용을 저장하기 위한 객체용 변수
    MemoInfo memoinfo;

    // 호출한 엑티비티로 실행결과를 들려주기 위한 인텐트 객체의 변수
    Intent result;

    //gps
    private gpsInfo gps;
    double latitude;
    double longitude;
    URL RSSurl = null;
    rcvJson RJ;
    JSONObject JStoken;
    String getJSON;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_add);

        auto_login= getSharedPreferences("setting", 0);
        TextView txt = (TextView)findViewById(R.id.date_18);

        GregorianCalendar c = new GregorianCalendar();
        SimpleDateFormat fm = new SimpleDateFormat("yyyy년 MM월 dd일 a h시 mm분");
        String setTime = fm.format(c.getTime());

        txt.setText(setTime);

        handler = new DBHandler_memo( this );
        memotitle = (TextView)findViewById(R.id.addmemotitle);
        memo = (EditText)findViewById(R.id.memo_18);
        submitBtn = (Button)findViewById(R.id.btn_18);

        // 목록 표시용 엑티비티에서 전달된 값을 읽어 옴
        Intent intent = getIntent();
        state = intent.getStringExtra("state");
        if(state.equals("update")){
            memotitle.setText("메모 수정");
            writedate = intent.getStringExtra("writedate");
            memoinfo = handler.select(intent.getStringExtra("writedate"), auto_login.getString("id",""));
            memo.setText(memoinfo.memo);
            submitBtn.setText("수정하기");
        }else{
            memotitle.setText("메모 작성");
            submitBtn.setText("등록하기");
        }

        ((ScrollView)findViewById(R.id.memoview_18)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(memo.getWindowToken(), 0);
                return false;
            }
        });

        // 실행 결과 정보를 되돌리기 위한 인텐트 객체 생성
        result = new Intent();
        submitBtn.setOnClickListener(this);
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

    @Override
    public void onClick(View v ) {
        // TODO Auto-generated method stub
        if( v == submitBtn ) {    // 등록 버턴이 눌려진 경우
            // 내용이 입력되었는지 검사하고 내용이 입력되지 않았으면
            // 입력 안내 메시지 출력 후 함수 실행 종료
            if (state.equals("insert")) {
                if (!inputCheck()) {
                    Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    gps = new gpsInfo(getApplicationContext());
                    if (gps.isGetLocation() == false) {
                        if (handler.insert(memo.getText().toString(), "", auto_login.getString("id", "")) == 0)
                            Toast.makeText(this, "등록할 수 없습니다.", Toast.LENGTH_LONG).show();
                        else {
                            Toast.makeText(this, "등록되었습니다.", Toast.LENGTH_LONG).show();
                            isadded = true;
                            Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                        try {
                            RSSurl = new URL("https://apis.daum.net/local/geo/coord2addr?apikey=1b1bd3d5afdef76fdcff4b8e97cb2465&longitude=" + longitude + "&latitude=" + latitude + "&inputCoordSystem=WGS84&output=json");
                            RJ = new rcvJson();
                            RJ.start();
                            RJ.join();
                            JStoken = new JSONObject(getJSON);
                            String dong = JStoken.getString("name3");
                            if (handler.insert(memo.getText().toString(), dong, auto_login.getString("id", "")) == 0)
                                Toast.makeText(this, "등록할 수 없습니다.", Toast.LENGTH_LONG).show();
                            else {
                                Toast.makeText(this, "등록되었습니다.", Toast.LENGTH_LONG).show();
                                isadded = true;
                                Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }else if (state.equals("update")) {
                if (!inputCheck()) {
                    Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    gps = new gpsInfo(getApplicationContext());
                    if (gps.isGetLocation() == false) {
                        if (handler.update(writedate, memo.getText().toString(), "", auto_login.getString("id", "")) == 0)
                            Toast.makeText(this, "수정할 수 없습니다.", Toast.LENGTH_LONG).show();

                        else {
                            Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_LONG).show();
                            isadded = true;
                            Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                        try {
                            RSSurl = new URL("https://apis.daum.net/local/geo/coord2addr?apikey=1b1bd3d5afdef76fdcff4b8e97cb2465&longitude=" + longitude + "&latitude=" + latitude + "&inputCoordSystem=WGS84&output=json");
                            RJ = new rcvJson();
                            RJ.start();
                            RJ.join();
                            JStoken = new JSONObject(getJSON);
                            String dong = JStoken.getString("name3");
                            if (handler.update(writedate, memo.getText().toString(), dong, auto_login.getString("id", "")) == 0)
                                Toast.makeText(this, "수정할 수 없습니다.", Toast.LENGTH_LONG).show();

                            else {
                                Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_LONG).show();
                                isadded = true;
                                Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            Log.d("수정오류", e.toString());
                        }
                    }
                }
            }
        }
    }
    // 내용이 입력되었는지 검사하여 true(입력됨), false(입력되지 않음)
    // 을 되돌림
    protected boolean inputCheck() {
        if(memo.getText().toString().length() == 0 ) return false;
        else return true;
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        // 엑티비티가 종료되면 handler 객체의 close 함수를 이용하여
        // 데이터베이스를 닫아 줌
        super.onDestroy();

        handler.close();
    }
}
