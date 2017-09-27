package com.sbway.Sync;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.sbway.Bookmark.BookmarkInfo;
import com.sbway.Bookmark.DBHandler_Bookmark;
import com.sbway.Dday.DBHandler;
import com.sbway.Dday.DdayInfo;
import com.sbway.Schedule.DBHandler_schedule;
import com.sbway.Schedule.ScheduleInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PC on 2016-10-19.
 */
public class SyncActivity extends Activity {
    DBHandler handler;
    DBHandler_Bookmark b_handler;
    DBHandler_schedule handlerSchedule;

    Context context;
    sync_control sync;
    SharedPreferences sync_time_sp;
    SharedPreferences.Editor editor;
    SharedPreferences auto_login;

    String now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new DBHandler(this);
        b_handler = new DBHandler_Bookmark(this);
        handlerSchedule = new DBHandler_schedule(this);

        context = this;
        auto_login = getApplicationContext().getSharedPreferences("setting", 0);
        sync_time_sp = getApplicationContext().getSharedPreferences("sync_time",0);
        editor = sync_time_sp.edit();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        now = sdf.format(new Date());

        d_sync_start();
        b_sync_start();
        s_sync_start();
        sync_finish();
    }

    protected void sync_finish() {
        editor.clear();
        editor.putString("time", now);
        editor.commit();
        finish();
    }

    protected void d_sync_start() {
        String sync_time = sync_time_sp.getString("time", "0");
        //Log.d("동기화",sync_time);
        sync = new sync_control();
        String id = auto_login.getString("id", "null");
        //Log.d("동기화", id);
        sync.ddaySync_Sqlite(id, sync_time, context);
        sync.ddaySync_Sqlite_Update(id,sync_time,context);
        Cursor cursor = handler.selectSync_Add(id, sync_time);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor != null ) {
            do {
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

                sync.ddaySync(dday.aw, dday.reg_date, dday.status, dday.d_year, dday.d_month, dday.d_day, dday.d_title, dday.type, dday.memid, dday.milli_date);
            } while (cursor.moveToNext());
            cursor.close();
        }
        Cursor cursor_update = handler.selectSync_Update(id, sync_time);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor_update == null ) return;
        do {
            DdayInfo dday = new DdayInfo();
            dday.milli_date = cursor_update.getString(0);
            dday.memid = cursor_update.getString(1);
            dday.d_year = cursor_update.getInt(2);
            dday.d_month = cursor_update.getInt(3);
            dday.d_day = cursor_update.getInt(4);
            dday.d_title = cursor_update.getString(5);
            dday.type = cursor_update.getInt(6);
            dday.reg_date = cursor_update.getString(7);
            dday.status = cursor_update.getString(8);
            dday.aw = cursor_update.getString(9);
            sync.ddaySync_Update(dday.reg_date, dday.status, dday.d_year, dday.d_month, dday.d_day, dday.d_title, dday.type, dday.memid, dday.milli_date);
            //Log.d("동기화", dday.d_title + "//" + dday.reg_date + "//" + dday.status);
        } while( cursor_update.moveToNext() );
        cursor_update.close();
    }
    protected void b_sync_start() {
        String sync_time = sync_time_sp.getString("time", "0");
        sync = new sync_control();
        //Log.d("동기화", sync_time);
        String id = auto_login.getString("id", "null");
        sync.bookmarkSync_Sqlite(id, sync_time, context);
        sync.bookmarkSync_Sqlite_Update(id, sync_time, context);
        Cursor cursor = b_handler.selectSync_Add(id, sync_time);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor != null ) {
            do {
                BookmarkInfo book = new BookmarkInfo();
                book.milli = cursor.getString(0);
                book.memid = cursor.getString(1);
                book.path = cursor.getString(2);
                book.start_place = cursor.getString(3);
                book.end_place = cursor.getString(4);
                book.slat = cursor.getString(5);
                book.slng = cursor.getString(6);
                book.elat = cursor.getString(7);
                book.elng = cursor.getString(8);
                book.reg_date = cursor.getString(9);
                book.status = cursor.getString(10);
                book.aw = cursor.getString(11);

                sync.bookmarkSync(book.aw, book.reg_date, book.status, book.path, book.start_place, book.end_place, book.slat, book.slng, book.elat, book.elng, book.memid, book.milli);
            } while (cursor.moveToNext());
            cursor.close();
        }

        Cursor cursor_update = b_handler.selectSync_Update(id, sync_time);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor_update == null ) return;
        do {
            BookmarkInfo book = new BookmarkInfo();
            book.milli = cursor_update.getString(0);
            book.memid = cursor_update.getString(1);
            book.path = cursor_update.getString(2);
            book.start_place = cursor_update.getString(3);
            book.end_place = cursor_update.getString(4);
            book.slat = cursor_update.getString(5);
            book.slng = cursor_update.getString(6);
            book.elat = cursor_update.getString(7);
            book.elng = cursor_update.getString(8);
            book.reg_date = cursor_update.getString(9);
            book.status = cursor_update.getString(10);
            book.aw = cursor_update.getString(11);

            sync.bookmarkSync_Update(book.reg_date, book.status, book.memid, book.milli);
        } while( cursor_update.moveToNext() );
        cursor_update.close();
    }
    protected void s_sync_start() {
        String sync_time = sync_time_sp.getString("time", "0");
        sync = new sync_control();
        String id = auto_login.getString("id", "null");
        sync.scheduleSync_Sqlite(id, sync_time, context);
        sync.scheduleSync_Sqlite_Update(id, sync_time, context);
        Cursor cursor = handlerSchedule.selectSync_Add(id, sync_time);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor != null ) {
            do {
                ScheduleInfo s = new ScheduleInfo();
                s.milli_date = cursor.getString(0);
                s.s_syear = cursor.getInt(1);
                s.s_smonth = cursor.getInt(2);
                s.s_sday = cursor.getInt(3);
                s.s_sday_week = cursor.getString(4);
                s.s_stime = cursor.getString(5);
                s.s_eyear = cursor.getInt(6);
                s.s_emonth = cursor.getInt(7);
                s.s_eday = cursor.getInt(8);
                s.s_eday_week = cursor.getString(9);
                s.s_etime = cursor.getString(10);
                s.stitle = cursor.getString(11);
                s.local = cursor.getString(12);
                s.sexplain = cursor.getString(13);
                s.salarm = cursor.getString(14);
                s.salarm_time = cursor.getString(15);
                s.srepeat = cursor.getString(16);
                s.srepeat_end = cursor.getString(17);
                s.sreg_date = cursor.getString(18);
                s.sstate = cursor.getString(19);
                s.s_aw = cursor.getString(20);
                s.s_member_id = cursor.getString(21);

                sync.scheduleSync(s.milli_date, s.s_syear, s.s_smonth, s.s_sday, s.s_sday_week, s.s_stime, s.s_eyear, s.s_emonth, s.s_eday, s.s_eday_week, s.s_etime,
                        s.stitle, s.local, s.sexplain, s.salarm, s.salarm_time, s.srepeat, s.srepeat_end, s.sreg_date, s.sstate, s.s_aw, s.s_member_id);

            } while (cursor.moveToNext());
            cursor.close();
        }

        Cursor cursor_update = handlerSchedule.selectSync_Update(id, sync_time);
        // 읽어 들인 데이터가 없으면 함수 종료
        if( cursor_update == null ) return;
        do {
            ScheduleInfo s_u = new ScheduleInfo();
            s_u.milli_date = cursor_update.getString(0);
            s_u.s_syear = cursor_update.getInt(1);
            s_u.s_smonth = cursor_update.getInt(2);
            s_u.s_sday = cursor_update.getInt(3);
            s_u.s_sday_week = cursor_update.getString(4);
            s_u.s_stime = cursor_update.getString(5);
            s_u.s_eyear = cursor_update.getInt(6);
            s_u.s_emonth = cursor_update.getInt(7);
            s_u.s_eday = cursor_update.getInt(8);
            s_u.s_eday_week = cursor_update.getString(9);
            s_u.s_etime = cursor_update.getString(10);
            s_u.stitle = cursor_update.getString(11);
            s_u.local = cursor_update.getString(12);
            s_u.sexplain = cursor_update.getString(13);
            s_u.salarm = cursor_update.getString(14);
            s_u.salarm_time = cursor_update.getString(15);
            s_u.srepeat = cursor_update.getString(16);
            s_u.srepeat_end = cursor_update.getString(17);
            s_u.sreg_date = cursor_update.getString(18);
            s_u.sstate = cursor_update.getString(19);
            s_u.s_aw = cursor_update.getString(20);
            s_u.s_member_id = cursor_update.getString(21);
            sync.scheduleSync_Update(s_u.milli_date, s_u.s_syear, s_u.s_smonth, s_u.s_sday, s_u.s_sday_week, s_u.s_stime, s_u.s_eyear, s_u.s_emonth, s_u.s_eday, s_u.s_eday_week, s_u.s_etime,
                    s_u.stitle, s_u.local, s_u.sexplain, s_u.salarm, s_u.salarm_time, s_u.srepeat, s_u.srepeat_end, s_u.sreg_date, s_u.sstate, s_u.s_member_id);
        } while( cursor_update.moveToNext() );
        cursor_update.close();
    }

}
