package com.sbway.Schedule;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.CheckedTextView;

import com.sbway.MainActivity;
import com.sbway.R;

/**
 * Created by minjee on 2016-09-29.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private int APP_NOTIFICATION_ID;

    Boolean alarm_bell;
    Boolean alarm_vibe;
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("alarm", 0);
        alarm_bell = sharedPreferences.getBoolean("bell", false);
        alarm_vibe = sharedPreferences.getBoolean("vibe", false);

        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);

        builder.setSmallIcon(R.drawable.sbwaylogo).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("일정 알람").setContentText("등록하신 일정이 얼마 남지 않았습니다.").setContentIntent(pendingIntent).setAutoCancel(true);

        if(alarm_bell==true&&alarm_vibe==false) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }else if(alarm_bell==false&&alarm_vibe==true){
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        }else if(alarm_bell=true&&alarm_vibe==true){
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        }else;
        notificationmanager.notify(1, builder.build());
    }
}
