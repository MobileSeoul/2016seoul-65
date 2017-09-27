package com.sbway.SignUp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sbway.Login.LoginActivity;
import com.sbway.R;
import com.sbway.controlMysql;
import com.sbway.sql_control;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {
    ArrayAdapter adapter;
    String question;
    static public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mContext = this;

        final EditText id=(EditText)findViewById(R.id.id_30);
        final EditText pw=(EditText)findViewById(R.id.pw_30);
        final EditText pwch=(EditText)findViewById(R.id.pwch_30);

        adapter = ArrayAdapter.createFromResource(this, R.array.question, R.layout.dday_sppinerlist);
        adapter.setDropDownViewResource(R.layout.dday_sppiner);
        final Spinner spinner = (Spinner)this.findViewById(R.id.question);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                question = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final EditText answer=(EditText)findViewById(R.id.answer_30);
        final EditText name=(EditText)findViewById(R.id.name_30);
        final EditText email=(EditText)findViewById(R.id.email);


        findViewById(R.id.btn_30).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf_milli = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");
                String milli = sdf_milli.format(new Date());

                String infoId=id.getText().toString();
                String infoPw=pw.getText().toString();
                String infoName=name.getText().toString();
                String infoEmail=email.getText().toString();
                String infoQuestion=question;
                String infoAnswer=answer.getText().toString();
                if(infoId.length()!=0&&infoPw.length()!=0&&infoEmail.length()!=0&&infoName.length()!=0&&infoQuestion.length()!=0&&infoAnswer.length()!=0){
                    sql_control.userRegist("i",infoId, infoPw, infoQuestion, infoAnswer, infoName, infoEmail);
                }else{
                    Toast.makeText(getApplication(),"입력하지 않은 항목이 있습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    static public void regist_result(String result){    //회원가입 결과
        Log.e("regist_result", result);
        controlMysql.active=false;
        if(result.contains("")){
            Toast.makeText(mContext,"회원가입에 성공하였습니다.",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(mContext,LoginActivity.class);
            mContext.startActivity(i);
            ((Activity)mContext).finish();
        }else{
            Toast.makeText(mContext,"회원가입에 실패하였습니다.",Toast.LENGTH_SHORT).show();
        }
    }
}

