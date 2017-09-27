package com.sbway.Bookmark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by PC on 2016-10-02.
 */
public class DBHandler_Bookmark {
    DBHelper_Bookmark helper;	// DBHelper 객체용 변수
    SQLiteDatabase db;	// 사용 가능한 SQLiteDatabase 객체용 변수
    Context context;	// 액티비티 객체를 위한 변수

    public void openDB() {
        helper = new DBHelper_Bookmark(context);
        db = helper.getWritableDatabase();
    }

    public void closeDB() {
        if( helper != null && db != null ) {
            helper.close();
            helper = null;
            db = null;
        }
    }

    public DBHandler_Bookmark(Context context){
        this.context = context;
        openDB();
    }

    public static DBHandler_Bookmark open( Context context ) throws SQLException {
        DBHandler_Bookmark handler = new DBHandler_Bookmark( context );
        return handler;
    }

    public void close() {
        closeDB();
    }

    public long insert(String milli_date, String memid,String path, String start_place, String end_place, String slat, String slng, String elat, String elng, String reg_date, String status, String aw) {
        ContentValues val = new ContentValues();
        val.put("milli_date", milli_date);
        val.put("memid", memid );
        val.put("path", path);
        val.put("start_place", start_place);
        val.put("end_place", end_place);
        val.put("slat", slat);
        val.put("slng", slng);
        val.put("elat", elat);
        val.put("elng", elng);
        val.put("reg_date", reg_date);
        val.put("status", status);
        val.put("a_w", aw);
        return db.insert("bookmark", null, val);
    }

    public Cursor selectAll(String memid) throws SQLiteException {
        Cursor cursor = db.query("bookmark", new String[]{"milli_date", "memid", "path", "start_place", "end_place", "slat", "slng", "elat", "elng", "reg_date", "status", "a_w"},
                "status != ? and memid= ?", new String[] {"d",memid}, null, null, "milli_date desc");
        if (cursor == null) return null;
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        return cursor;
    }

    // 마지막 동기화 시간 후에 생성된 경우(milli_date로 비교)는 모두 DB에 추가
    public Cursor selectSync_Add(String memid, String sync_time) throws SQLiteException {
        Cursor cursor = db.query( "bookmark",
                new String[] {"milli_date", "memid", "path", "start_place", "end_place", "slat", "slng", "elat", "elng", "reg_date", "status", "a_w"},
                "memid = ? and milli_date >= ? and a_w = ?", new String[] {memid,sync_time,"a"}, null, null, null );
        if( cursor == null ) return null;

        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        return cursor;
    }
    // 마지막 동기화 시간 후에 변경 된 경우(status, reg_date로 비교)는 DB에서 갱신
    public Cursor selectSync_Update(String memid, String sync_time) throws SQLiteException {
        Cursor cursor = db.query("bookmark",
                new String[]{"milli_date", "memid", "path", "start_place", "end_place", "slat", "slng", "elat", "elng", "reg_date", "status", "a_w"},
                "memid = ? and reg_date >= ?", new String[]{ memid, sync_time}, null, null, null);
        if (cursor == null) return null;

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        return cursor;
    }

    // 해당 ID에 대한 자료를 검색하여 되돌려 주는 함스
    public BookmarkInfo select(String path,String memid) {
        Cursor cursor = db.query( "bookmark",
                new String[] {"milli_date", "memid", "path", "start_place", "end_place", "slat", "slng", "elat", "elng", "reg_date", "status", "a_w"},
                "status != ? and path = ? and memid = ?", new String[] {"d",path,memid}, null, null, null );
        if( cursor == null ) return null;
        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        BookmarkInfo search = new BookmarkInfo();
        search.id = cursor.getInt(0);
        search.milli = cursor.getString(0);
        search.path = cursor.getString(1);
        return search;
    }

    public long delete(String milli, String reg_date, String status) {
        ContentValues val = new ContentValues();
        val.put("reg_date", reg_date);
        val.put("status", status);
        return db.update("bookmark", val, "milli_date = ?", new String[] {milli} );
    }
}
