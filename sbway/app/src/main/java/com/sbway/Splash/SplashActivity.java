package com.sbway.Splash;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbway.MainActivity;
import com.sbway.R;

public class SplashActivity extends AppCompatActivity {
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SplashThread splashThread = new SplashThread();
        splashThread.setDaemon(true);
        splashThread.start();
    }

    class SplashThread extends Thread{
        @Override
        public void run() {
            while (i<6){
                splashHandler.sendEmptyMessage(i);
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    Handler splashHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    ((TextView)findViewById(R.id.txt_s)).setVisibility(View.VISIBLE);
                    i++;
                    break;
                case 1:
                    ((TextView)findViewById(R.id.txt_b)).setVisibility(View.VISIBLE);
                    i++;
                    break;
                case 2:
                    ((TextView)findViewById(R.id.txt_w)).setVisibility(View.VISIBLE);
                    i++;
                    break;
                case 3:
                    ((TextView)findViewById(R.id.txt_a)).setVisibility(View.VISIBLE);
                    i++;
                    break;
                case 4:
                    ((TextView)findViewById(R.id.txt_y)).setVisibility(View.VISIBLE);
                    i++;
                    break;
                case 5:
                    finish();
                    break;
            }
        }
    };
}
