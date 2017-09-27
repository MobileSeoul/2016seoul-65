package com.sbway.PathSearch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbway.R;

import java.util.ArrayList;

/**
 * Created by PC on 2016-09-30.
 */
public class DetailResultAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<String> listData;
    TextView result;
    ImageView img;

    public DetailResultAdapter(Context context, ArrayList<String> data ) {
        inflater = LayoutInflater.from( context );
        this.context = context;
        listData = data;
    }
    @Override
    public int getCount() {return listData.size();}
    @Override
    public Object getItem(int position) {return listData.get(position);}
    @Override
    public long getItemId(int position) {return position;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(convertView == null){
            v = inflater.inflate(R.layout.dtailresult_item, null);

            result = (TextView)v.findViewById(R.id.detail_result);
            result.setText(listData.get(position));
            img = (ImageView)v.findViewById(R.id.img);

            v.setTag(result);

        }else{
            v = inflater.inflate(R.layout.dtailresult_item, null);

            result = (TextView)v.findViewById(R.id.detail_result);
            img = (ImageView)v.findViewById(R.id.img);
            String str[] = (listData.get(position)).split(" ");
            String text = "";
            for(int i=1;i<str.length;i++){
                text += str[i]+" ";
            }
            if(str[0].contains("번")){
                String bus_no = str[0].replace("번","");
                Boolean alphabet_ck = false;
                for(int i=0;i<bus_no.length();i++){
                    if(bus_no.contains("A")||bus_no.contains("B")) {
                        alphabet_ck = true;
                    }
                }
                if(alphabet_ck) {
                    img.setBackgroundResource(R.drawable.bus_3);
                    result.setText(listData.get(position));
                }else if(bus_no.length()>4){
                    img.setBackgroundResource(R.drawable.bus_1);
                    String res[] = listData.get(position).split(" ");
                    String res_str = bus_no.replaceAll("[^0-9]", "") + "번 ";
                    for(int i=1;i<res.length;i++) {
                        res_str = res_str + res[i] + " ";
                    }
                    result.setText(res_str);
                }else {
                    switch (bus_no.length()) {
                        case 3:
                            img.setBackgroundResource(R.drawable.bus_3);
                            result.setText(listData.get(position));
                            break;
                        case 4:
                            if (bus_no.substring(1, 2).equals("9")) {
                                img.setBackgroundResource(R.drawable.bus_9);
                            } else if (Character.isDigit(bus_no.charAt(1))) {
                                img.setBackgroundResource(R.drawable.bus_4);
                            } else {
                                img.setBackgroundResource(R.drawable.bus_ma);
                            }
                            result.setText(listData.get(position));
                            break;
                        default:
                            result.setText(listData.get(position));
                            break;
                    }
                }
            }else {
                switch (str[0]) {
                    case "1호선":
                        img.setBackgroundResource(R.drawable.line_one);
                        result.setText(text);
                        break;
                    case "2호선":
                        img.setBackgroundResource(R.drawable.line_two);
                        result.setText(text);
                        break;
                    case "3호선":
                        img.setBackgroundResource(R.drawable.line_three);
                        result.setText(text);
                        break;
                    case "4호선":
                        img.setBackgroundResource(R.drawable.line_four);
                        result.setText(text);
                        break;
                    case "5호선":
                        img.setBackgroundResource(R.drawable.line_five);
                        result.setText(text);
                        break;
                    case "6호선":
                        img.setBackgroundResource(R.drawable.line_six);
                        result.setText(text);
                        break;
                    case "7호선":
                        img.setBackgroundResource(R.drawable.line_seven);
                        result.setText(text);
                        break;
                    case "8호선":
                        img.setBackgroundResource(R.drawable.line_eight);
                        result.setText(text);
                        break;
                    case "9호선":
                        img.setBackgroundResource(R.drawable.line_nine);
                        result.setText(text);
                        break;
                    case "경의중앙선":
                        img.setBackgroundResource(R.drawable.line_center);
                        result.setText(text);
                        break;
                    case "경춘선":
                        img.setBackgroundResource(R.drawable.line_kyungchun);
                        result.setText(text);
                        break;
                    case "분당선":
                        img.setBackgroundResource(R.drawable.line_bun);
                        result.setText(text);
                        break;
                    case "신분당선":
                        img.setBackgroundResource(R.drawable.line_sin);
                        result.setText(text);
                        break;
                    case "공항철도":
                        img.setBackgroundResource(R.drawable.line_air);
                        result.setText(text);
                        break;
                    case "의정부":
                        img.setBackgroundResource(R.drawable.line_eui);
                        result.setText(text);
                        break;
                    case "자기부상":
                        img.setBackgroundResource(R.drawable.line_agi);
                        result.setText(text);
                        break;
                    default:
                        result.setText(listData.get(position));
                        break;
                }
            }
            v.setTag(result);
        }

        return v;
    }
}
