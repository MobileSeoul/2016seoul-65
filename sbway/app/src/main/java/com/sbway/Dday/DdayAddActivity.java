package com.sbway.Dday;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sbway.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DdayAddActivity extends AppCompatActivity implements View.OnClickListener {
    DBHandler handler;

    // 목록표시 엑티비티에서 호출되었는지(isCalled),
    // 새로운 데이터가 삽입되었는지(isadded) 판단하기 위한 변수
    boolean isCalled = false, isadded = false;

    // 뷰 컨트를을 위한 객체용 변수
    Button submitBtn;
    EditText ddaytitle;

    // 메모 내용을 저장하기 위한 객체용 변수
    DdayInfo ddayinfo;

    // 호출한 엑티비티로 실행결과를 들려주기 위한 인텐트 객체의 변수
    Intent result;

    ArrayAdapter<CharSequence> mAdapter;
    TextView title;
    TextView date;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int year, month, day;

    static final int DATE_DIALOG_ID_1 = 0, DATE_DIALOG_ID_2 = 1;

    SimpleDateFormat frmDate;
    String s;
    String now;
    int spinner_index;
    Spinner spinner_2;

    //update
    String status, d_milli;

    //사용자 id 가져오기
    SharedPreferences auto_login;
    SharedPreferences.Editor editor;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dday_add);

        LinearLayout layout = (LinearLayout)findViewById(R.id.ddayAdd_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ddaytitle.getWindowToken(), 0);
            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        frmDate = new SimpleDateFormat("yyyy년 MM월 dd일");
        s = frmDate.format(c.getTime());

        handler = new DBHandler( this );
        title = (TextView)findViewById(R.id.text_dday_add);
        ddaytitle = (EditText) findViewById(R.id.input_2);
        date = (TextView) findViewById(R.id.date_2);
        submitBtn = (Button)findViewById(R.id.add_dday_2);
        submitBtn.setOnClickListener(this);

        // 목록 표시용 엑티비티에서 전달된 값을 읽어 옴
        Intent intent = getIntent();
        status = intent.getStringExtra("status");
        d_milli = intent.getStringExtra("d_milli");

        if (status != null) {
            if (status.equals("insert")) {
                title.setText("디데이 추가");
                submitBtn.setText("등록하기");
                date.setText("날짜 선택");
            } else if (status.equals("update")) {
                displayDdayList(d_milli);

                title.setText("디데이 수정");
                submitBtn.setText("수정하기");
            }
        } else {
            title.setText("일정추가");
            submitBtn.setText("등록하기");
            date.setText("날짜 선택");
        }


        // 실행 결과 정보를 되돌리기 위한 인텐트 객체 생성
        result = new Intent();

        //사용자 id
        auto_login = this.getSharedPreferences("setting", 0);
        editor= auto_login.edit();
        userid = auto_login.getString("id", "null"); //키값, 디폴트값

        mAdapter = ArrayAdapter.createFromResource(this, R.array.data, R.layout.dday_sppinerlist);
        mAdapter.setDropDownViewResource(R.layout.dday_sppiner);
        spinner_2 = (Spinner) findViewById(R.id.spinner_2);
        spinner_2.setAdapter(mAdapter);
        if(status.equals("update")){
            spinner_2.setSelection(spinner_index);
        }
        spinner_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        spinner_index = 0;
                        break;
                    case 1:
                        spinner_index = 1;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("insert")) {
                    showDialog(DATE_DIALOG_ID_1);
                }else {
                    showDialog(DATE_DIALOG_ID_2);
                }
            }
        });
        /*
        findViewById(R.id.add_dday_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Dday_listActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }); */
    }

    @Override
    public void onClick(View v ) {
        // TODO Auto-generated method stub
        if( v == submitBtn ) {	// 등록 버턴이 눌려진 경우
            // 내용이 입력되었는지 검사하고 내용이 입력되지 않았으면
            // 입력 안내 메시지 출력 후 함수 실행 종료
            if( !inputCheck() ) {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_LONG).show();
                return;
            }
            else {
                SimpleDateFormat sdf_milli = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");
                String milli = sdf_milli.format(new Date());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                now = sdf.format(new Date());

                if (status.equals("update")){
                    if( handler.update(d_milli,userid,year,month+1,day,ddaytitle.getText().toString(),spinner_index,now,"u","a") == 0 ) Toast.makeText( this, "수정할 수 없습니다.",  Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText( this, "수정되었습니다.",  Toast.LENGTH_LONG).show();
                        isadded = true;
                        Intent intent = new Intent(getApplicationContext(), DdayListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    if( handler.insert(milli,userid,mYear,mMonth+1,mDay,ddaytitle.getText().toString(),spinner_index,now,"i","a") == 0 ) Toast.makeText( this, "등록할 수 없습니다.",  Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText( this, "등록되었습니다.",  Toast.LENGTH_LONG).show();
                        isadded = true;
                        Intent intent = new Intent(getApplicationContext(), DdayListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }
    }
    // 내용이 입력되었는지 검사하여 true(입력됨), false(입력되지 않음)
    // 을 되돌림
    protected boolean inputCheck() {
        if(ddaytitle.getText().toString().length() == 0 ) return false;
        else return true;
    }

    private void updateDisplay() {
        date.setText(s);
    }
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    c.set(mYear, mMonth, mDay);
                    frmDate = new SimpleDateFormat("yyyy년 MM월 dd일");
                    s = frmDate.format(c.getTime());
                    updateDisplay();
                }
            };
    private DatePickerDialog.OnDateSetListener mDateSetListener2 =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year2, int monthOfYear, int dayOfMonth) {
                    Calendar c = Calendar.getInstance();
                    year = year2;
                    month = monthOfYear;
                    day = dayOfMonth;
                    c.set(year, month, day);
                    frmDate = new SimpleDateFormat("yyyy년 MM월 dd일");
                    s = frmDate.format(c.getTime());
                    updateDisplay();
                }
            };
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID_1:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
            case DATE_DIALOG_ID_2:
                return new DatePickerDialog(this, mDateSetListener2, year, month, day);
        }

        return null;
    }

    //디데이
    protected void displayDdayList(String milli) {
        // handler 객체로부터 모든 메모를 읽어 옴
        DdayInfo dday = handler.select(milli);
        try {
            if( dday == null ) return;

            Calendar cal = Calendar.getInstance();
            ddaytitle.setText(dday.d_title);
            year = dday.d_year;
            month = dday.d_month-1;
            day = dday.d_day;
            cal.set(year, month, day);
            frmDate = new SimpleDateFormat("yyyy년 MM월 dd일");
            s = frmDate.format(cal.getTime());
            updateDisplay();
            spinner_index = dday.type;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // 엑티비티가 종료되면 handler 객체의 close 함수를 이용하여
        // 데이터베이스를 닫아 줌
        super.onDestroy();

        handler.close();
    }
}
