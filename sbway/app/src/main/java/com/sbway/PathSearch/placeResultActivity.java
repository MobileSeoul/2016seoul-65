package com.sbway.PathSearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sbway.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

public class placeResultActivity extends AppCompatActivity {
    InputMethodManager imm;

    EditText txtplace;
    ArrayList<String> p_list;
    ArrayList<String> plat;  //위도
    ArrayList<String> lng;  //경도
    AddressAdapter addressAdapter =  null;
    ListView place_list;
    int pos;

    String start, end;

    //장소

    //장소정보저장
    String p_title, p_addr, p_lat, p_lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_result);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        txtplace = (EditText)findViewById(R.id.place);

        final Intent intent = getIntent();
        start = intent.getExtras().getString("출발");
        end = intent.getExtras().getString("도착");

        if(start != null && end == null){
            txtplace.setHint(start+"지 검색(검색할 주소 또는 장소를 입력하세요.)");
        }else{
            txtplace.setHint(end+"지 검색(검색할 주소 또는 장소를 입력하세요.)");
        }

        //키워드
        p_list = new ArrayList<String>();
        plat = new ArrayList<String>();
        lng = new ArrayList<String>();
        addressAdapter = new AddressAdapter(getApplicationContext());
        place_list = (ListView)findViewById(R.id.place_list);
        place_list.setAdapter(addressAdapter);

        txtplace.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Enter키눌렀을떄 처리
                    imm.hideSoftInputFromWindow(txtplace.getWindowToken(), 0);
                    String str = txtplace.getText().toString();
                    if (str != null) {
                        try {
                            find();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return true;
                }
                return false;
            }
        });

        place_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent1 = new Intent(getApplicationContext(), SearchActivity.class);
                intent1.putExtra("place", p_list.get(i));
                intent1.putExtra("lat", plat.get(i));
                intent1.putExtra("lng", lng.get(i));
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(start != null && end == null){
                    intent1.putExtra("start", "출발");
                }else{
                    intent1.putExtra("end", "end");
                }
                startActivity(intent1);
                finish();
            }
        });

    }

    private class ViewHolder{
        public TextView place_title;
        public TextView place_addr;
    }

    private class AddressAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<Place_Data> mPlaceData = new ArrayList<Place_Data>();
        public AddressAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }
        @Override
        public int getCount() {return mPlaceData.size();}
        @Override
        public Object getItem(int position) {return mPlaceData.get(position);}
        @Override
        public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if(view == null){
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.place_list, null);

                holder.place_title = (TextView) view.findViewById(R.id.place_title);
                holder.place_addr = (TextView) view.findViewById(R.id.place_addr);

                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }

            Place_Data mData = mPlaceData.get(position);

            holder.place_title.setText(mData.place_title);
            holder.place_addr.setText(mData.place_addr);

            return view;
        }

        public void listClear(){
            mPlaceData.clear();
            addressAdapter.notifyDataSetChanged();
        }
        public void addItem(String title, String addr1){
            Place_Data addAddr = null;
            addAddr = new Place_Data();
            addAddr.place_title = title;
            addAddr.place_addr = addr1;
            mPlaceData.add(addAddr);
        }
    }

    //키워드 검색
    public void find() throws Exception{
        //버튼이 눌릴때마다 데이터가 쌓이는 것을 방지하기 위해
        p_list.clear();
        plat.clear();
        lng.clear();
        addressAdapter.listClear();
        //요청 url 만들기
        String keyWord = txtplace.getText().toString();
        //한글이 깨지지 않게 하기 위해
        String encodedK = URLEncoder.encode(keyWord, "utf-8");
        StringBuffer buffer = new StringBuffer();
        buffer.append("https://apis.daum.net/local/v1/search/keyword.json?");
        //한글일 경우 인코딩 필요!(영어로 가정한다)
        buffer.append("apikey=150408ab92817d0b92b8331710549192");
        buffer.append("&query="+encodedK);

        String url = buffer.toString();

        //스레드 객체를 생성해서 다운로드 받는다.
        GetJSONThread thread = new GetJSONThread(handler, null, url);
        thread.start();
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0 : //success
                    String jsonStr = (String)msg.obj;
                    try{
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONObject channel = jsonObj.getJSONObject("channel");
                        JSONArray items = channel.getJSONArray("item");
                        if(items.length()==0){
                            Toast.makeText(getApplicationContext(),"검색 결과가 없습니다.",Toast.LENGTH_SHORT).show();
                        } else {
                            for(int i=0 ; i<items.length() ; i++) {
                                JSONObject tmp = items.getJSONObject(i);
                                String title = tmp.getString("title");
                                String lon = tmp.getString("longitude");
                                String lat = tmp.getString("latitude");
                                String addr1 = tmp.getString("address");
                                p_list.add(title);
                                lng.add(lon);
                                plat.add(lat);
                                addressAdapter.addItem(title, addr1);
                            }
                        }
                        addressAdapter.notifyDataSetChanged();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1 : //fail
                    Toast.makeText(getApplicationContext(),"네트워크 연결이 불안정합니다.",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public class GetJSONThread extends Thread{
        //메인스레드의 핸들러
        Handler handler;
        //전송할 데이터가 담겨있는 map객체
        Map<String, String> map;
        //전송할 url 주소
        String url;

        //생성자
        public GetJSONThread(Handler handler,  Map<String, String> map, String url){
            this.handler = handler;
            this.map = map;
            this.url = url;
        }
        //스레드 본체
        @Override
        public void run() {
            HttpURLConnection conn = null;
            StringBuilder builder = new StringBuilder();
            try {
                URL url=new URL(this.url);
                conn= (HttpURLConnection)url.openConnection();
                if(conn != null){//정상접속이 되었다면
                    conn.setConnectTimeout(10000);//최대 대기시간10초
                    conn.setUseCaches(false);//캐쉬사용안함
                    if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                        //InputStreamReader 객체 얻어오기
                        InputStreamReader isr=
                                new InputStreamReader(conn.getInputStream());
                        BufferedReader br=new BufferedReader(isr);
                        //반복문 돌면서 읽어오기
                        while(true){
                            String line=br.readLine();
                            if(line==null)break;
                            //읽어온 문자열을 객체에 저장
                            builder.append(line);
                        }
                        br.close();
                    }//if
                }//if
                Message msg= new Message();
                msg.what=0; //성공
                msg.obj = builder.toString();
                handler.sendMessage(msg);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("post 전송중 에러", e.getMessage());
                Message msg= new Message();
                msg.what=1; //실패
                msg.obj = "데이터를 받아올 수 없습니다.";
                handler.sendMessage(msg);
            }finally{
                conn.disconnect(); //접속 종료
            }
        }//run
    }
}
