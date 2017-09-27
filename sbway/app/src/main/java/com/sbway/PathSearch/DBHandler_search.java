package com.sbway.PathSearch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by minjee on 2016-05-16.
 */
public class DBHandler_search {
    DBHelper_search helper;	// DBHelper 객체용 변수
    SQLiteDatabase db;	// 사용 가능한 SQLiteDatabase 객체용 변수
    Context context;	// 액티비티 객체를 위한 변수

    public void openDB() {
        helper = new DBHelper_search( context);
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
    public DBHandler_search(Context context){
        this.context = context;
        openDB();
    }

    // 클래스르보투 DBHandler 객체를 생성하는 함수
    public static DBHandler_search open( Context context ) throws SQLException {
        DBHandler_search handler = new DBHandler_search( context );
        return handler;
    }

    // DBHandler 객체를 닫는 함수
    // 데이터베이스를 닫는 기능을 함
    public void close() {
        closeDB();
    }

    // 엑티비티에서 전달된 메모의 내용을 데이터베이스에 사입하는 함수
    public long insert(String path, String start_place, String end_place, String slat, String slng, String elat, String elng, String memid) {
        ContentValues val = new ContentValues();
        val.put("path", path);
        val.put("start_place", start_place);
        val.put("end_place", end_place);
        val.put("slat", slat);
        val.put("slng", slng);
        val.put("elat", elat);
        val.put("elng", elng);
        val.put("memid", memid);
        return db.insert("pathList", null, val);
    }

    // 전달받은 id와 memo를 이용하여 데이터베이스의 해당 자료를 수정하는
    // 함수
    public long update(String path, String start_place, String end_place, String slat, String slng, String elat, String elng, int id,String memid){
        ContentValues val = new ContentValues();
        val.put("path", path);
        val.put("start_place", path);
        val.put("end_place", path);
        val.put("slat", slat);
        val.put("slng", slng);
        val.put("elat", elat);
        val.put("elng", elng);
        val.put("memid", memid);
        return db.update("pathList", val, "id = ?", new String[] { String.valueOf( id ) });
    }

    // 데이터베이스에 저장된 모든 메모를 읽어 그 커서를 되돌려 줌
    public Cursor selectAll(String memid) throws SQLiteException {
        // 데이터베이스로부터 모든 데이터를 검색함
        Cursor cursor = db.query("pathList", new String[]{"id", "path", "start_place", "end_place", "slat", "slng", "elat", "elng","memid"},
                "memid= ?", new String[] {memid}, null, null, "id desc");

        // 커서가 null(없음)이면 null 값을 되돌림
        if (cursor == null) return null;

        // 커서로 부터 검색된 레코드 수가 0이면
        // 커서를 닫고 null값을 되돌림
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        // 검색된 데이터가 있으면 데이터 읽을 위치를 처음 레코드로 옮긴
        // 후 커서를 되돌림
        cursor.moveToFirst();
        return cursor;
    }

    // 해당 ID에 대한 자료를 검색하여 되돌려 주는 함스
    public SearchInfo select( int id ) {
        // 해당 ID의 자료 검색
        Cursor cursor = db.query( "pathList",
                new String[] { "id", "path", "start_place", "end_place", "slat", "slng", "elat", "elng"},
                "id = ?", new String[] { Integer.toString( id ) }, null, null, null );
        if( cursor == null ) return null;
        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }

        // 검색된 데이터가 있으면 읽을 위치를 처음오로 올김 후
        // 데이터를 읽어 memoInfo 객체에 저장한 후 되돌려줌
        cursor.moveToFirst();
        SearchInfo search = new SearchInfo();
        search.id = cursor.getInt( 0 );
        search.path = cursor.getString( 1 );
        return search;
    }

    // 해당 ID의 자료를 삭제하는 함수
    public long delete(int id) {
        return db.delete( "pathList", "id = ?", new String[] { String.valueOf(id) } );
    }
}
