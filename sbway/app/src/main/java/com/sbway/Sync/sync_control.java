package com.sbway.Sync;

import android.content.Context;
import android.util.Log;

import com.sbway.controlMysql;

/**
 * Created by PC on 2016-09-29.
 */
public class sync_control {
    static public void ddaySync(String aw,String reg_date,String status,int year,int month,int day,String title,int type,String memid,String milli){ //디데이 동기화
        sync_Mysql ddaysync=new sync_Mysql(aw,reg_date,status,year,month,day,title,type,memid,milli);
        sync_Mysql.active=true;
        ddaysync.start();
    }
    static public void ddaySync_Update(String reg_date,String status,int year,int month,int day,String title,int type,String memid,String milli){ //디데이 동기화
        sync_Mysql ddaysync=new sync_Mysql(reg_date,status,year,month,day,title,type,memid,milli);
        sync_Mysql.active=true;
        ddaysync.start();
    }
    static public void ddaySync_Sqlite(String memid,String sync,Context context){ //디데이 동기화
        sync_Mysql ddaysync=new sync_Mysql(memid,sync,context);
        sync_Mysql.active=true;
        ddaysync.start();
    }
    static public void ddaySync_Sqlite_Update(String memid,String sync,Context context){ //디데이 동기화
        sync_Mysql ddaysync=new sync_Mysql(memid,sync,context,"update");
        sync_Mysql.active=true;
        ddaysync.start();
    }
    static public void bookmarkSync(String aw,String reg_date,String status,String path, String start_place, String end_place, String slat, String slng, String elat, String elng, String memid, String milli){ //북마크 동기화
        sync_Mysql bookmarksync=new sync_Mysql(aw,reg_date,status,path,start_place,end_place,slat,slng,elat,elng,memid,milli);
        sync_Mysql.active=true;
        bookmarksync.start();
    }
    static public void bookmarkSync_Update(String reg_date,String status,String memid,String milli){ //북마크 동기화
        sync_Mysql bookmarksync=new sync_Mysql(reg_date,status,memid,milli);
        sync_Mysql.active=true;
        bookmarksync.start();
    }
    static public void bookmarkSync_Sqlite(String memid,String sync,Context context){ //디데이 동기화
        sync_Mysql bookmarksync=new sync_Mysql(memid,sync,context,"","");
        sync_Mysql.active=true;
        bookmarksync.start();
    }
    static public void bookmarkSync_Sqlite_Update(String memid,String sync,Context context){ //디데이 동기화
        sync_Mysql bookmarksync=new sync_Mysql(memid,sync,context,"","","update");
        sync_Mysql.active=true;
        bookmarksync.start();
    }
    static public void scheduleSync(String milli,int syear,int smonth,int sday,String sday_week,String stime,int eyear,int emonth,int eday,String eday_week,String etime,
                                    String title,String local,String explain,String alarm,String alarm_time,String repeat, String repeat_end,String reg_date,String status,String aw,String memid){ //디데이 동기화
        sync_Mysql schedulesync=new sync_Mysql(milli,syear,smonth,sday,sday_week,stime,eyear,emonth,eday,eday_week,etime,title,local,explain,alarm,alarm_time,repeat,repeat_end,reg_date,status,aw,memid);
        sync_Mysql.active=true;
        schedulesync.start();
    }
    static public void scheduleSync_Update(String milli,int syear,int smonth,int sday,String sday_week,String stime,int eyear,int emonth,int eday,String eday_week,String etime,
                                           String title,String local,String explain,String alarm,String alarm_time,String repeat, String repeat_end,String reg_date,String status,String memid){ //디데이 동기화
        sync_Mysql schedulesync=new sync_Mysql(milli,syear,smonth,sday,sday_week,stime,eyear,emonth,eday,eday_week,etime,title,local,explain,alarm,alarm_time,repeat,repeat_end,reg_date,status,memid);
        sync_Mysql.active=true;
        schedulesync.start();
    }
    static public void scheduleSync_Sqlite(String memid,String sync,Context context){ //디데이 동기화
        sync_Mysql schedulesync=new sync_Mysql(memid,sync,context,0);
        sync_Mysql.active=true;
        schedulesync.start();
    }
    static public void scheduleSync_Sqlite_Update(String memid,String sync,Context context){ //디데이 동기화
        sync_Mysql schedulesync=new sync_Mysql(memid,sync,context,0,0);
        sync_Mysql.active=true;
        schedulesync.start();
    }
}
