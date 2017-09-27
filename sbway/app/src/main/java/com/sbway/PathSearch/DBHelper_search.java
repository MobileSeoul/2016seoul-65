package com.sbway.PathSearch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by minjee on 2016-05-16.
 */
public class DBHelper_search extends SQLiteOpenHelper {
    SQLiteDatabase mDB;
    public DBHelper_search(Context context) {
        super( context, "path_Search.db", null, 1);
    }

    // 데이터베이스가 최초 만들어질 때 호출되는 함수로
    // 테이블을 생성하는 기능을 수행함
    public void onCreate( SQLiteDatabase db ) {
        String table = "CREATE TABLE pathList ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "path TEXT, start_place TEXT, end_place TEXT, slat TEXT, slng TEXT, elat TEXT, elng TEXT, memid TEXT)";
        db.execSQL(table);
    }

    // 테이블이 있으나 업그레이드가 필요할 때 호출되는 함수로
    // 기존 테이블을 제거한 후 새로 만들거나, 테이블을 수정하는 기능
    // 을 함
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL( "DROP TABLE IF EXISTS pathList" );
        onCreate( db );
    }

    public void deleteAll(DBHelper_search mDBManager) {
        mDBManager.getWritableDatabase();
        mDB.execSQL("DELETE FROM pathList");
        mDBManager.close();
    }
}
