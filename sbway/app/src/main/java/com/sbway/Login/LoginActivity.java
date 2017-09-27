package com.sbway.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sbway.BackPressCloseHandler;
import com.sbway.HomeActivity;
import com.sbway.MainActivity;
import com.sbway.R;
import com.sbway.SignUp.SignUpActivity;
import com.sbway.controlMysql;
import com.sbway.sql_control;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    DialogInterface manageDialog = null;

    Button Btnlogin;
    TextView regist,findpw;
    static EditText userId,userPw;
    static EditText memid,name,mail, answer;
    static Spinner pw_question;

    ArrayAdapter adapter;
    static String question="";

    static public boolean login_state=false;
    static boolean pw_ok=false,id_ok=false;
    static String getpw="",getid="";
    final int code_chkid=1000;
    static public Context mContext;

    static SharedPreferences auto_login;
    static SharedPreferences.Editor editor;

    static String l_result="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        auto_login = getSharedPreferences("setting", 0);
        editor= auto_login.edit();

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.relative);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userId.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(userPw.getWindowToken(), 0);
            }
        });
    }

    void init(){
        userId=(EditText)findViewById(R.id.id_11);
        userPw=(EditText)findViewById(R.id.pw_11);
        Btnlogin=(Button)findViewById(R.id.login_11);
        regist=(TextView)findViewById(R.id.sign_11); //회원가입
        findpw=(TextView)findViewById(R.id.idpwch_11);
        Btnlogin.setOnClickListener(this);
        regist.setOnClickListener(this);
        findpw.setOnClickListener(this);
        mContext=this;

    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_11:
                if(userId.getText().toString()!=null&&userPw.getText().toString()!=null) {
                    login_proc(login_state);
                }else
                    Toast.makeText(this, "ID와 PASSWORD를 모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sign_11:
                //regist();
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.idpwch_11:
                idpwch();
                break;
        }
    }

    void idpwch() {
        AlertDialog.Builder builder2;
        builder2=new AlertDialog.Builder(this);


        final LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.user_layout, null);
        builder2.setView(layout);

        adapter = ArrayAdapter.createFromResource(this, R.array.question, R.layout.dday_sppinerlist);
        adapter.setDropDownViewResource(R.layout.dday_sppiner);
        pw_question = (Spinner)layout.findViewById(R.id.pw_question);
        pw_question.setAdapter(adapter);
        pw_question.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                question = pw_question.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        memid=(EditText)layout.findViewById(R.id.UserId);
        name=(EditText)layout.findViewById(R.id.UserName);
        mail=(EditText)layout.findViewById(R.id.UserMail);
        answer = (EditText)layout.findViewById(R.id.pw_answer);

        builder2.setPositiveButton("찾기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String infoId=memid.getText().toString();
                String infoName=name.getText().toString();
                String infoMail=mail.getText().toString();
                String infoQues=question;
                String infoAns=answer.getText().toString();

                if(infoName.length()!=0&&infoMail.length()!=0&&infoId.length()!=0&&infoQues.length()!=0&&infoAns.length()!=0){
                    findpw_proc();
                }else{
                    Toast.makeText(getApplication(),"입력하지 않은 항목이 있습니다.",Toast.LENGTH_SHORT).show();
                    idpwch();
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplication(),"Cancel sign up.",Toast.LENGTH_SHORT).show();
            }
        }).create();
        manageDialog=builder2.show();
    }

    void findpw_proc(){
        String id=memid.getText().toString();
        String u_name=name.getText().toString();
        String email=mail.getText().toString();
        String pw_ques=question;
        String pw_ans=answer.getText().toString();
        findpwMysql findpw=new findpwMysql(id,u_name,email,pw_ques,pw_ans);
        findpwMysql.active=true;
        findpw.start();
    }

    void login_proc(boolean login){ //로그인
        if(!login){
            String id=userId.getText().toString();
            String pw=userPw.getText().toString();
            loginMysql idchk=new loginMysql(id,pw);
            loginMysql.active=true;
            idchk.start();
        }else{

        }
    }

    static public void result_login(String result,String pw,String name){
        loginMysql.active=false;
        if(result.equals("false"))
            Toast.makeText(mContext,"사용자 ID가 없습니다.",Toast.LENGTH_SHORT).show();
        else{
            if(pw.equals(result)) {
                Toast.makeText(mContext, name + "님 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                editor.clear();
                editor.putString("id", userId.getText().toString());
                editor.putString("name", name);
                editor.commit();
                l_result = "success";

                Intent intent=new Intent(mContext,MainActivity.class);
                intent.putExtra("end", "end");
                userId.setText("");
                userPw.setText("");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
            }else if(pw.equals(result)==false) {
                if(l_result.equals("")) {
                    Toast.makeText(mContext, "PW가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Toast.makeText(mContext,result,Toast.LENGTH_SHORT).show();
    }

    static public void findpw_result(String result){    //회원가입 결과
        Log.e("regist_result", result);
        findpwMysql.active=false;
        if(result.equals("false")) {
            Toast.makeText(mContext, "사용자 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
        }else if(result.equals("true")) {
            Toast.makeText(mContext, "메일로 비밀번호를 전송하였습니다", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case code_chkid:
                    if (data.getExtras().getBoolean("ok")) {
                        getid = data.getExtras().getString("id");

                        id_ok = true;
                    }
                    manageDialog.dismiss();
                    //regist();
                    break;
            }
        } else {
            Toast.makeText(this, "작업이 취소되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //httppost = new HttpPost("http://sbway.cafe24.com/php/logcheck.php");
}