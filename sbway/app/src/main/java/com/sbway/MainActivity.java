package com.sbway;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sbway.Splash.SplashActivity;

public class MainActivity extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(intent.getStringExtra("end")==null) {
            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
        }

        //뷰페이저 세팅
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        //뷰페이저의 어댑터를 세팅
        viewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));

        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
}
