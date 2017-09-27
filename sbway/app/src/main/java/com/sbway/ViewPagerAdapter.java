package com.sbway;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by TonyChoi on 2016. 4. 5..
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitle[] = new String[]{"홈", "캘린더"};
    Context ctx;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    //뷰페이저 페이지 수를 지정
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    //뷰페이저 내부 뷰를 지정(아이템 지정)
    @Override
    public Fragment getItem(int position) {
        //switch-case문을 사용하여 페이지 포지션 별로 그에 맞는 Fragment를 리턴하여 화면을 작성한다
        switch (position) {
            case 0:
                HomeActivity homeActivity = new HomeActivity();
                return homeActivity;

            case 1:
                CalendarActivity calendarActivity = new CalendarActivity();
                return calendarActivity;
        }
        //페이지 포지션이 없는 곳으로 이동을 하려는 경우 null을 리턴하여 움직임이 없게 한다
        return null;
    }

    //페이지 타이틀을 지정
    //포지션을 이용하여 페이지별 이름을 다 다르게도 설정가능
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }
}
