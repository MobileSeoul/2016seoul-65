package com.sbway.Weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbway.Bookmark.BookmarkInfo;
import com.sbway.R;

import java.util.ArrayList;

/**
 * Created by minjee on 2016-10-11.
 */

class WeatherList{
    TextView weather_day;
    TextView weather_day_time;
    ImageView weather_little_img;
    TextView weather_now_temp;
    TextView weather_now_rain;
}
public class WeatherAdapter extends BaseAdapter{
    LayoutInflater inflater;
    Context context;
    ArrayList<WeatherInfo> w_data;
    WeatherList input_data;

    public WeatherAdapter(Context context, ArrayList<WeatherInfo> weatherData) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.w_data = weatherData;
    }

    @Override
    public int getCount() {return w_data.size();}

    @Override
    public Object getItem(int position) {return w_data.get(position);};

    @Override
    public long getItemId(int position) {return position;};

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = convertView;

        if(convertView==null){
            input_data = new WeatherList();
            v = inflater.inflate(R.layout.weather_list, null);
            
            input_data.weather_day = (TextView)v.findViewById(R.id.weather_day);
            input_data.weather_day_time = (TextView)v.findViewById(R.id.weather_day_time);
            input_data.weather_little_img = (ImageView)v.findViewById(R.id.weather_little_img);
            input_data.weather_now_rain = (TextView)v.findViewById(R.id.weather_now_rain);
            input_data.weather_now_temp = (TextView)v.findViewById(R.id.weather_now_temp);

            input_data.weather_day.setText(w_data.get(position).w_day);
            input_data.weather_day_time.setText(w_data.get(position).w_time);
            input_data.weather_now_rain.setText(w_data.get(position).w_now_rain);
            input_data.weather_now_temp.setText(w_data.get(position).w_now_temp);

            switch (w_data.get(position).w_img) {
                case "맑음":
                    input_data.weather_little_img.setBackgroundResource(R.drawable.sun);
                    break;
                case "구름 조금":
                    input_data.weather_little_img.setBackgroundResource(R.drawable.little_cloud);
                    break;
                case "흐림":
                    input_data.weather_little_img.setBackgroundResource(R.drawable.cloud);
                    break;
                case "구름 많음":
                    input_data.weather_little_img.setBackgroundResource(R.drawable.many_cloud);
                    break;
                case "비":
                    input_data.weather_little_img.setBackgroundResource(R.drawable.rain);
                    break;
            }

            v.setTag(input_data);
        }else{
            input_data = new WeatherList();
            v = inflater.inflate(R.layout.weather_list, null);

            input_data.weather_day = (TextView)v.findViewById(R.id.weather_day);
            input_data.weather_day_time = (TextView)v.findViewById(R.id.weather_day_time);
            input_data.weather_little_img = (ImageView)v.findViewById(R.id.weather_little_img);
            input_data.weather_now_rain = (TextView)v.findViewById(R.id.weather_now_rain);
            input_data.weather_now_temp = (TextView)v.findViewById(R.id.weather_now_temp);

            input_data.weather_day.setText(w_data.get(position).w_day);
            input_data.weather_day_time.setText(w_data.get(position).w_time);
            input_data.weather_now_rain.setText(w_data.get(position).w_now_rain);
            input_data.weather_now_temp.setText(w_data.get(position).w_now_temp);

            switch (w_data.get(position).w_img) {
                case "맑음":
                    input_data.weather_little_img.setBackgroundResource(R.drawable.sun);
                    break;
                case "구름 조금":
                    input_data.weather_little_img.setBackgroundResource(R.drawable.little_cloud);
                    break;
                case "흐림":
                    input_data.weather_little_img.setBackgroundResource(R.drawable.cloud);
                    break;
                case "구름 많음":
                    input_data.weather_little_img.setBackgroundResource(R.drawable.many_cloud);
                    break;
                case "비":
                    input_data.weather_little_img.setBackgroundResource(R.drawable.rain);
                    break;
            }

            v.setTag(input_data);
        }
        return v;
    }
    public void setArrayList(ArrayList<WeatherInfo> arrays) {
        w_data = arrays;
    }

    // 목록 정보를 되돌려 주는 함수
    public ArrayList<WeatherInfo> getArrayList() {
        return w_data;
    }
}
