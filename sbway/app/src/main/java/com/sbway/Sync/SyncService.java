package com.sbway.Sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.sbway.HomeActivity;


/**
 * Created by PC on 2016-10-19.
 */
public class SyncService extends Service {
    Context context = this;
    int sync_time = 0;
    Handler handler;
    int i=0;
    //Intent intent;
    boolean stopTask = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "Job Start", Toast.LENGTH_SHORT).show();
        SharedPreferences sync_sp = context.getSharedPreferences("sync", Context.MODE_MULTI_PROCESS);
        sync_time = sync_sp.getInt("sync_set_time",0);
        stopTask = false;

        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                Intent intent = new Intent(getApplicationContext(),SyncActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                //Toast.makeText(getApplicationContext(), i+"", Toast.LENGTH_SHORT).show();
                i+=sync_time;
                this.sendEmptyMessageDelayed(0, sync_time);
            }
        };

        startService();
    }



    private void startService() {
        handler.sendEmptyMessage(0);
    }

    public void onDestroy() {
        super.onDestroy();
        stopTask = true;
        handler.removeMessages(0);
        //Toast.makeText(this, "Job End", Toast.LENGTH_SHORT).show();
    }
}
