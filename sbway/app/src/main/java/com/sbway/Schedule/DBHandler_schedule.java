package com.sbway.Schedule;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by PC on 2016-05-07.
 */
public class DBHandler_schedule {
    DBHelper_schedule helper;	// DBHelper 객체용 변수
    SQLiteDatabase db;	// 사용 가능한 SQLiteDatabase 객체용 변수
    Context context;	// 액티비티 객체를 위한 변수

    // 데이터베이스를 오픈하는 함수
    public void openDB() {
        helper = new DBHelper_schedule( context);
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
    public DBHandler_schedule(Context context){
        this.context = context;
        openDB();
    }

    // 클래스르보투 DBHandler 객체를 생성하는 함수
    public static DBHandler_schedule open( Context context ) throws SQLException {
        DBHandler_schedule handler = new DBHandler_schedule( context );
        return handler;
    }

    // DBHandler 객체를 닫는 함수
    // 데이터베이스를 닫는 기능을 함
    public void close() {
        closeDB();
    }

    // 엑티비티에서 전달된 메모의 내용을 데이터베이스에 사입하는 함수
    public long insert(String member_id, String milli_date, String title, String syear, String smonth, String sday, String sday_week, String stime, String eyear, String emonth, String eday, String eday_week, String etime, String location, String explain, String alarm, String alarm_time, String repeat, String repeat_end, String reg_date, String state, String aw) {
        ContentValues val = new ContentValues();
        val.put("milli_date", milli_date);
        val.put("syear", syear);
        val.put("smonth", smonth);
        val.put("sday", sday);
        val.put("sday_week", sday_week);
        val.put("stime", stime);
        val.put("eyear", eyear);
        val.put("emonth", emonth);
        val.put("eday", eday);
        val.put("eday_week", eday_week);
        val.put("etime", etime);
        val.put("title", title );
        val.put("location", location);
        val.put("explain", explain);
        val.put("alarm", alarm);
        val.put("alarm_time", alarm_time);
        val.put("reg_date", reg_date);
        val.put("repeat", repeat);
        val.put("repeat_end", repeat_end);
        val.put("state", state);
        val.put("a_w", aw);
        val.put("member_id", member_id);
        return db.insert("Schedule", null, val);
    }

    // 전달받은 id와 memo를 이용하여 데이터베이스의 해당 자료를 수정하는
    // 함수
    public long update(String member_id, String milli_date, String title, String syear, String smonth, String sday, String sday_week, String stime, String eyear, String emonth, String eday, String eday_week, String etime, String location, String explain, String alarm, String alarm_time, String repeat, String repeat_end, String reg_date, String state, String aw) {
        ContentValues val = new ContentValues();
        val.put("syear", syear);
        val.put("smonth", smonth);
        val.put("sday", sday);
        val.put("sday_week", sday_week);
        val.put("stime", stime);
        val.put("eyear", eyear);
        val.put("emonth", emonth);
        val.put("eday", eday);
        val.put("eday_week", eday_week);
        val.put("etime", etime);
        val.put( "title", title );
        val.put( "location", location);
        val.put( "explain", explain);
        val.put( "alarm", alarm);
        val.put( "alarm_time", alarm_time);
        val.put("reg_date", reg_date);
        val.put("repeat", repeat);
        val.put("repeat_end", repeat_end);
        val.put("state", state);
        val.put("a_w", aw);
        val.put("member_id", member_id);
        return db.update("Schedule", val, "milli_date = ? and member_id=?", new String[]{String.valueOf(milli_date), member_id});
    }

    // 데이터베이스에 저장된 모든 메모를 읽어 그 커서를 되돌려 줌
    public Cursor selectAll(String member_id) throws SQLiteException {
        // 데이터베이스로부터 모든 데이터를 검색함
        Cursor cursor = db.query( "Schedule",
                new String[] { "milli_date", "syear", "smonth", "sday", "sday_week", "stime", "eyear", "emonth", "eday", "eday_week", "etime", "title", "location", "explain", "alarm", "alarm_time", "repeat", "repeat_end","reg_date", "state"}
                ,"state!=? and member_id=?",new String[] {"d", member_id},null,null,null);

        // 검색된 데이터가 있으면 데이터 읽을 위치를 처음 레코드로 옮긴
        // 후 커서를 되돌림
        cursor.moveToFirst();

        // 커서가 null(없음)이면 null 값을 되돌림
        if( cursor == null ) return null;

        // 커서로 부터 검색된 레코드 수가 0이면
        // 커서를 닫고 null값을 되돌림
        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    // 데이터베이스에 저장된 모든 메모를 읽어 그 커서를 되돌려 줌
    public Cursor selectAllasc(String member_id) throws SQLiteException {
        // 데이터베이스로부터 모든 데이터를 검색함
        Cursor cursor = db.query( "Schedule",
                new String[] { "milli_date", "syear", "smonth", "sday", "sday_week", "stime", "eyear", "emonth", "eday", "eday_week", "etime", "title", "location", "explain", "alarm", "alarm_time", "repeat", "repeat_end"}
                ,"state!=? and member_id=?",new String[] {"d", member_id},null,null,"syear asc, smonth asc, sday asc");

        // 검색된 데이터가 있으면 데이터 읽을 위치를 처음 레코드로 옮긴
        // 후 커서를 되돌림
        cursor.moveToFirst();

        // 커서가 null(없음)이면 null 값을 되돌림
        if( cursor == null ) return null;

        // 커서로 부터 검색된 레코드 수가 0이면
        // 커서를 닫고 null값을 되돌림
        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor selectDelete(String member_id) throws SQLiteException {
        Cursor cursor = db.query( "Schedule",
                new String[] { "milli_date", "syear", "smonth", "sday", "sday_week", "stime", "eyear", "emonth", "eday", "eday_week", "etime", "title", "location", "explain", "alarm", "alarm_time", "repeat", "repeat_end"}
                ,"state=? and member_id=?",new String[] {"d", member_id},null,null,"syear asc, smonth asc, sday asc");

        cursor.moveToFirst();

        if( cursor == null ) return null;

        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    // 해당 ID에 대한 자료를 검색하여 되돌려 주는 함스
    public ScheduleInfo select(String milli_date, String member_id) {
        // 해당 ID의 자료 검색
        Cursor cursor = db.query( "Schedule",
                new String[] { "milli_date", "syear", "smonth", "sday", "sday_week", "stime", "eyear", "emonth", "eday", "eday_week", "etime", "title", "location", "explain", "alarm", "alarm_time", "repeat", "repeat_end", "reg_date", "state", "a_w", "member_id"},
                "milli_date = ? and member_id=?", new String[] { milli_date , member_id}, null, null, null );
        if( cursor == null ) return null;
        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }

        // 검색된 데이터가 있으면 읽을 위치를 처음오로 올김 후
        // 데이터를 읽어 memoInfo 객체에 저장한 후 되돌려줌
        cursor.moveToFirst();
        ScheduleInfo schedule = new ScheduleInfo();
        schedule.milli_date = cursor.getString(0);
        schedule.s_syear = cursor.getInt(1);
        schedule.s_smonth = cursor.getInt(2);
        schedule.s_sday = cursor.getInt(3);
        schedule.s_sday_week = cursor.getString(4);
        schedule.s_stime = cursor.getString(5);
        schedule.s_eyear = cursor.getInt(6);
        schedule.s_emonth = cursor.getInt(7);
        schedule.s_eday = cursor.getInt(8);
        schedule.s_eday_week = cursor.getString(9);
        schedule.s_etime = cursor.getString(10);
        schedule.stitle = cursor.getString(11);
        schedule.local = cursor.getString(12);
        schedule.sexplain = cursor.getString(13);
        schedule.salarm = cursor.getString(14);
        schedule.salarm_time = cursor.getString(15);
        schedule.srepeat = cursor.getString(16);
        schedule.srepeat_end = cursor.getString(17);
        schedule.sreg_date = cursor.getString(18);
        schedule.sstate = cursor.getString(19);
        schedule.s_aw = cursor.getString(20);
        schedule.s_member_id = cursor.getString(21);
        return schedule;
    }
    // 마지막 동기화 시간 후에 생성된 경우(milli_date로 비교)는 모두 DB에 추가
    public Cursor selectSync_Add(String memid, String sync_time) throws SQLiteException {
        // 데이터베이스로부터 모든 데이터를 검색함
        Cursor cursor = db.query( "Schedule",
                new String[] { "milli_date", "syear", "smonth", "sday", "sday_week", "stime", "eyear", "emonth", "eday", "eday_week", "etime",
                        "title", "location", "explain", "alarm", "alarm_time", "repeat", "repeat_end", "reg_date", "state", "a_w", "member_id" },
                "member_id = ? and milli_date >= ? and a_w = ?", new String[] {memid,sync_time,"a"}, null, null, null );
        if( cursor == null ) return null;

        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor selectAsc_reg(String member_id) throws SQLiteException {
        // 데이터베이스로부터 모든 데이터를 검색함
        Cursor cursor = db.query( "Schedule",
                new String[] { "milli_date", "syear", "smonth", "sday", "sday_week", "stime", "eyear", "emonth", "eday", "eday_week", "etime", "title", "location", "explain", "alarm", "alarm_time", "repeat", "repeat_end","reg_date", "state"}
                ,"state!=? and member_id=?",new String[] {"d", member_id},null,null,"reg_date asc");

        // 검색된 데이터가 있으면 데이터 읽을 위치를 처음 레코드로 옮긴
        // 후 커서를 되돌림
        cursor.moveToFirst();

        // 커서가 null(없음)이면 null 값을 되돌림
        if( cursor == null ) return null;

        // 커서로 부터 검색된 레코드 수가 0이면
        // 커서를 닫고 null값을 되돌림
        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    // 마지막 동기화 시간 후에 변경 된 경우(status, reg_date로 비교)는 DB에서 갱신
    public Cursor selectSync_Update(String memid, String sync_time) throws SQLiteException {
        Cursor cursor = db.query( "Schedule",
                new String[] { "milli_date", "syear", "smonth", "sday", "sday_week", "stime", "eyear", "emonth", "eday", "eday_week", "etime",
                        "title", "location", "explain", "alarm", "alarm_time", "repeat", "repeat_end", "reg_date", "state", "a_w", "member_id" },
                "state != ? and member_id = ? and reg_date >= ?", new String[] {"i",memid,sync_time}, null, null, null );
        if( cursor == null ) return null;

        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        return cursor;
    }

    // 데이터베이스에 저장된 모든 메모를 읽어 그 커서를 되돌려 줌
    public Cursor selectToday(String member_id, String now_year, String now_month, String now_day) throws SQLiteException {
        // 데이터베이스로부터 모든 데이터를 검색함
        Cursor cursor = db.query("Schedule",
                new String[] { "milli_date", "syear", "smonth", "sday", "sday_week", "stime", "eyear", "emonth", "eday", "eday_week", "etime", "title", "location", "explain", "alarm", "repeat", "reg_date", "repeat_end", "state", "a_w"},
                "syear=? and smonth=? and sday=? and state!=? and member_id=?", new String[] { now_year, now_month, now_day ,"d",member_id}, null, null, null );

        // 검색된 데이터가 있으면 데이터 읽을 위치를 처음 레코드로 옮긴
        // 후 커서를 되돌림
        cursor.moveToFirst();

        // 커서가 null(없음)이면 null 값을 되돌림
        if( cursor == null ) return null;

        // 커서로 부터 검색된 레코드 수가 0이면
        // 커서를 닫고 null값을 되돌림
        if( cursor.getCount() == 0 ) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public long schedule_state(String member_id, String milli_date, String reg_date, String state, String aw) {
        ContentValues val = new ContentValues();
        val.put("reg_date", reg_date);
        val.put("state", state);
        val.put("a_w", aw);
        return db.update("Schedule", val, "milli_date = ? and member_id=?", new String[]{String.valueOf(milli_date),member_id});
    }

    // 해당 ID의 자료를 삭제하는 함수
    public long delete(String member_id, String milli_date) {
        return db.delete( "Schedule", "milli_date = ? and member_id=?", new String[] { String.valueOf(milli_date), member_id } );
    }

    public long deleteAll(String member_id, String reg_date, String state, String aw) {
        ContentValues val = new ContentValues();
        val.put("reg_date", reg_date);
        val.put("state", state);
        val.put("a_w", aw);
        return db.delete( "Schedule", "member_id=?", new String[] {member_id } );
    }
}
