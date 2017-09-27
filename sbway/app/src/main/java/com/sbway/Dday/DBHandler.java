package com.sbway.Dday;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBHandler {
    DBHelper helper;	// DBHelper 객체용 변수
    SQLiteDatabase db;	// 사용 가능한 SQLiteDatabase 객체용 변수
    Context context;	// 액티비티 객체를 위한 변수

    // 데이터베이스를 오픈하는 함수
    public void openDB() {
        helper = new DBHelper( context);
        db = helper.getWritableDatabase();
    }

    // 데이터베이스를 닫는 함수
    public void closeDB() {
        if( helper != null && db != null ) {
            helper.close();
            helper = null;
            db = null;
        }
    }

    // DBHandler 생성자 함수 엑티비티 객체의 번지를 저장하고
    // 데이터베이스를 오픈하는 함수
    public DBHandler(Context context){
        this.context = context;
        openDB();
    }

    // 클래스르보투 DBHandler 객체를 생성하는 함수
    public static DBHandler open( Context context ) throws SQLException {
        DBHandler handler = new DBHandler( context );
        return handler;
    }

    // DBHandler 객체를 닫는 함수
    // 데이터베이스를 닫는 기능을 함
    public void close() {
        closeDB();
    }

    // 엑티비티에서 전달된 메모의 내용을 데이터베이스에 사입하는 함수
    public long insert(String milli_date, String memid, int d_year, int d_month, int d_day, String d_title, int type, String reg_date, String status, String aw) {
        ContentValues val = new ContentValues();
        // 현재 날짜를 천분이 1초 단위로 변환하여 writedate에 저장하고
        // 전달된 메모의 값을 content에 저장하여 삽입함
        val.put("milli_date", milli_date);
        val.put("memid", memid );
        val.put("d_year", d_year);
        val.put("d_month", d_month);
        val.put("d_day", d_day);
        val.put("d_title", d_title);
        val.put("type", type);
        val.put("reg_date", reg_date);
        val.put("status", status);
        val.put("a_w", aw);
        return db.insert("Dday", null, val);
    }

    // 전달받은 id와 memo를 이용하여 데이터베이스의 해당 자료를 수정하는
    // 함수
    public long update(String milli_date, String memid, int d_year, int d_month, int d_day, String d_title, int type, String reg_date, String status, String aw) {
        ContentValues val = new ContentValues();
        val.put("d_year", d_year);
        val.put("d_month", d_month);
        val.put("d_day", d_day);
        val.put("d_title", d_title);
        val.put("type", type);
        val.put("reg_date", reg_date);
        val.put("status", status);
        val.put("a_w", aw);
        return db.update("Dday", val, "milli_date = ?", new String[]{milli_date});
    }

    // 데이터베이스에 저장된 모든 메모를 읽어 그 커서를 되돌려 줌
    public Cursor selectAll(String memid) throws SQLiteException {
        // 데이터베이스로부터 모든 데이터를 검색함
        Cursor cursor = db.query( "Dday",
                new String[] { "milli_date", "memid", "d_year", "d_month", "d_day", "d_title", "type", "reg_date", "status", "a_w"},
                "status != ? and memid= ?", new String[] {"d",memid}, null, null, null );
        // 커서가 null(없음)이면 null 값을 되돌림
        if( cursor == null ) return null;

        // 커서로 부터 검색된 레코드 수가 0이면
        // 커서를 닫고 null값을 되돌림
        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }

        // 검색된 데이터가 있으면 데이터 읽을 위치를 처음 레코드로 옮긴
        // 후 커서를 되돌림
        cursor.moveToFirst();
        return cursor;
    }

    // 디데이가 마지막 동기화 시간 후에 생성된 경우(milli_date로 비교)는 모두 DB에 추가
    public Cursor selectSync_Add(String memid, String sync_time) throws SQLiteException {
        // 데이터베이스로부터 모든 데이터를 검색함
        Cursor cursor = db.query( "Dday",
                new String[] { "milli_date", "memid", "d_year", "d_month", "d_day", "d_title", "type", "reg_date", "status", "a_w"},
                "memid = ? and milli_date >= ? and a_w = ?", new String[] {memid,sync_time,"a"}, null, null, null );
        if( cursor == null ) return null;

        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        return cursor;
    }
    // 디데이가 마지막 동기화 시간 후에 변경 된 경우(status, reg_date로 비교)는 DB에서 갱신
    public Cursor selectSync_Update(String memid, String sync_time) throws SQLiteException {
        Cursor cursor = db.query( "Dday",
                new String[] { "milli_date", "memid", "d_year", "d_month", "d_day", "d_title", "type", "reg_date", "status", "a_w"},
                "status != ? and memid = ? and reg_date >= ?", new String[] {"i",memid,sync_time}, null, null, null );
        if( cursor == null ) return null;

        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor selectDday() throws SQLiteException {
        // 데이터베이스로부터 모든 데이터를 검색함
        Cursor cursor = db.query( "Dday",
                new String[] { "milli_date", "memid", "d_year", "d_month", "d_day", "d_title", "type", "reg_date", "status", "a_w"},
                null,  null,  null,  null,  null );
        // 커서가 null(없음)이면 null 값을 되돌림
        if( cursor == null ) return null;

        // 커서로 부터 검색된 레코드 수가 0이면
        // 커서를 닫고 null값을 되돌림
        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }

        // 검색된 데이터가 있으면 데이터 읽을 위치를 처음 레코드로 옮긴
        // 후 커서를 되돌림
        cursor.moveToFirst();
        return cursor;
    }

    // 해당 ID에 대한 자료를 검색하여 되돌려 주는 함스
    public DdayInfo select(String milli_date) {
        // 해당 ID의 자료 검색
        Cursor cursor = db.query( "Dday",
                new String[] { "milli_date", "memid", "d_year", "d_month", "d_day", "d_title", "type", "reg_date", "status", "a_w"},
                "milli_date = ?", new String[] {milli_date}, null, null, null );
        if( cursor == null ) return null;
        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        // 검색된 데이터가 있으면 읽을 위치를 처음오로 올김 후
        // 데이터를 읽어 memoInfo 객체에 저장한 후 되돌려줌
        cursor.moveToFirst();
        DdayInfo dday = new DdayInfo();
        dday.milli_date = cursor.getString(0);
        dday.memid = cursor.getString(1);
        dday.d_year = cursor.getInt(2);
        dday.d_month = cursor.getInt(3);
        dday.d_day = cursor.getInt(4);
        dday.d_title = cursor.getString(5);
        dday.type = cursor.getInt(6);
        dday.reg_date = cursor.getString(7);
        dday.status = cursor.getString(8);
        dday.aw = cursor.getString(9);

        return dday;
    }

    // 해당 ID의 자료를 삭제하는 함수
    public long delete(String milli_date,String reg_date, String status) {
        ContentValues val = new ContentValues();
        val.put("reg_date", reg_date);
        val.put("status", status);
        return db.update("Dday", val, "milli_date = ?", new String[]{milli_date});
    }
}
