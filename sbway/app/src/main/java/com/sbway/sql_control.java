package com.sbway;

/**
 * Created by PC on 2016-09-20.
 */
public class sql_control {
    public static void pwUpdate(String id,String pw){ //비번 변경하기
        controlMysql pwchange=new controlMysql(id,pw,0);
        controlMysql.active=true;
        pwchange.start();
    }
    static public void get_userInfo(String id,int type){ //사용자 정보가져오기
        controlMysql getinfo=new controlMysql(id,type);
        controlMysql.active=true;
        getinfo.start();
    }
    static public void userUpdate(String id,String name,String age,String phone,String mail,String address){    //회원정보 수정하기
        controlMysql updateinfo=new controlMysql(id,name,age,phone,mail,address);
        controlMysql.active=true;
        updateinfo.start();
    }
    static public void userRegist(String status, String memid, String passwd, String question, String answer, String name, String email){    //회원 가입하기
        controlMysql registinfo=new controlMysql(status,memid,passwd,question,answer,name,email);
        controlMysql.active=true;
        registinfo.start();
    }
}
