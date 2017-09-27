package com.sbway.Dday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // DBHelper 셍성자 함수
    // 최초 오픈시 데이터베이스를 만듦
    public DBHelper(Context context) {
        super( context, "ddaydata.db", null, 1);
    }

    // 데이터베이스가 최초 만들어질 때 호출되는 함수로
    // 테이블을 생성하는 기능을 수행함
    public void onCreate( SQLiteDatabase db ) {
        String table = "CREATE TABLE Dday ( "
                + "milli_date TEXT PRIMARY KEY , "
                + "memid TEXT, d_year INTEGER, d_month INTEGER, d_day INTEGER, d_title TEXT, type TEXT , reg_date DATE, status TEXT, a_w TEXT)";
        db.execSQL(table);

    }

    // 테이블이 있으나 업그레이드가 필요할 때 호출되는 함수로
    // 기존 테이블을 제거한 후 새로 만들거나, 테이블을 수정하는 기능
    // 을 함
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL( "DROP TABLE IF EXISTS Dday" );
        onCreate( db );
    }
}
