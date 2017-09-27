package com.sbway.UserLocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sbway.HomeActivity;
import com.sbway.R;
import com.sbway.Schedule.ScheduleAddActivity;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

public class UserLocationActivity extends AppCompatActivity implements MapView.POIItemEventListener {
    EditText edit;
    InputMethodManager imm;
    MapView mapView;
    MapPOIItem marker;
    int pos;

    ListView listView = null;
    UserlocAdapter userlocAdapter = null;
    ArrayList<String> list_title;
    ArrayList<String> longitude;
    ArrayList<String> latitude;

    SharedPreferences lat_lon;
    SharedPreferences.Editor lat_lon_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        mapView = new MapView(UserLocationActivity.this);
        mapView.setDaumMapApiKey("1b1bd3d5afdef76fdcff4b8e97cb2465");

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        edit = (EditText)findViewById(R.id.userLoc_edit);

        lat_lon = getSharedPreferences("lat_lon", 0);
        lat_lon_editor= lat_lon.edit();

        listView = (ListView)findViewById(R.id.userLoc_list);
        list_title = new ArrayList<String>();
        longitude = new ArrayList<String>();
        latitude = new ArrayList<String>();
        userlocAdapter = new UserlocAdapter(getApplicationContext());
        listView.setAdapter(userlocAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mapView.removeAllPOIItems();
                pos = position;

                // 중심점 변경
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(latitude.get(position)), Double.parseDouble(longitude.get(position))), true);
                mapView.setZoomLevel(1, true);

                // 마커 생성 및 설정
                marker = new MapPOIItem();
                marker.setItemName(list_title.get(position));
                marker.setTag(0);
                //marker.setCustomSelectedImageResourceId(R.drawable.marker_click);
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(latitude.get(position)), Double.parseDouble(longitude.get(position))));
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                mapView.addPOIItem(marker);
                mapView.setPOIItemEventListener(UserLocationActivity.this);//마커클릭시 이벤트
            }
        });

        edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Enter키눌렀을떄 처리
                    imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                    try{
                        find(v);

                        RelativeLayout mapViewContainer = (RelativeLayout)findViewById(R.id.map_view);
                        mapViewContainer.addView(mapView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /* 지도 마커 이벤트 */
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {}
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {}
    @Override //단말 사용자가 길게 누른 후(long press) 끌어서(dragging) 위치 이동이 가능한 POI Item의 위치를 이동시킨 경우 호출된다.
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {}
    @Override //단말 사용자가 POI Item을 선택한 경우 호출된다.
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        lat_lon_editor.clear();
        lat_lon_editor.putFloat("lat", Float.parseFloat(latitude.get(pos)));
        lat_lon_editor.putFloat("lon", Float.parseFloat(longitude.get(pos)));
        lat_lon_editor.commit();
        this.finish();
    }


    /* 리스트뷰 */
    private class ViewHolder {
        public TextView userloc_title;
        public TextView userloc_addr1;
        public TextView userloc_addr2;
    }
    private class UserlocAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<UserLoc_ListData> mListData = new ArrayList<UserLoc_ListData>();
        public UserlocAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }
        @Override
        public int getCount() {
            return mListData.size();
        }
        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.userloc_list_item, null);

                holder.userloc_title = (TextView) convertView.findViewById(R.id.userLoc_title);
                holder.userloc_addr1 = (TextView) convertView.findViewById(R.id.userLoc_addr1);
                holder.userloc_addr2 = (TextView) convertView.findViewById(R.id.userLoc_addr2);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            UserLoc_ListData mData = mListData.get(position);

            holder.userloc_title.setText(mData.userloc_title);
            holder.userloc_addr1.setText(mData.userloc_addr1);
            holder.userloc_addr2.setText(mData.userloc_addr2);

            return convertView;
        }
        public void listClear() {
            mListData.clear();
            userlocAdapter.notifyDataSetChanged();
        }
        public void addItem(String title, String addr1, String addr2){
            UserLoc_ListData addInfo = null;
            addInfo = new UserLoc_ListData();
            addInfo.userloc_title = title;
            addInfo.userloc_addr1 = addr1;
            addInfo.userloc_addr2 = addr2;
            mListData.add(addInfo);
        }
    }


    public void linearOnClick(View v) {
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }


    /* 지역 검색 */
    public void find(View v) throws Exception{
        //버튼이 눌릴때마다 데이터가 쌓이는 것을 방지하기 위해
        list_title.clear();
        latitude.clear();
        longitude.clear();
        userlocAdapter.listClear();
        //요청 url 만들기
        String keyWord = edit.getText().toString();
        //한글이 깨지지 않게 하기 위해
        String encodedK = URLEncoder.encode(keyWord, "utf-8");
        StringBuffer buffer = new StringBuffer();
        buffer.append("https://apis.daum.net/local/v1/search/keyword.json?");
        //한글일 경우 인코딩 필요!(영어로 가정한다)
        buffer.append("query="+encodedK);
        buffer.append("&apikey=6d63486c1be9ee8832a8797861402c0f");

        String url = buffer.toString();

        //스레드 객체를 생성해서 다운로드 받는다.
        GetJSONThread thread = new GetJSONThread(handler, null, url);
        thread.start();
    }
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
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
                                String addr1 = tmp.getString("newAddress");
                                String addr2 = tmp.getString("address");
                                list_title.add(title);
                                longitude.add(lon);
                                latitude.add(lat);
                                userlocAdapter.addItem(title, addr1, addr2);
                            }
                        }
                        userlocAdapter.notifyDataSetChanged();
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
