package com.sbway.PathSearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.sbway.Bookmark.BookmarkInfo;
import com.sbway.Bookmark.DBHandler_Bookmark;
import com.sbway.Login.LoginActivity;
import com.sbway.R;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ResultActivity extends AppCompatActivity {
    String path, s, e, result = "", id, now, milli_date;
    String[] ss, es;
    Document doc = null;
    String startplace, endplace;

    ArrayList<String> data;
    ArrayAdapter adapter;
    ListView list;
    ImageView bookmark;

    DBHandler_Bookmark handler;
    BookmarkInfo bookmarkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        s = intent.getExtras().getString("start");
        e = intent.getExtras().getString("end");
        ss = s.split("\\,");
        es = e.split("\\,");

        startplace = intent.getStringExtra("startplace");
        endplace = intent.getStringExtra("endplace");

        //즐겨찾기
        handler = new DBHandler_Bookmark(getApplicationContext());

        bookmark = (ImageView)findViewById(R.id.bookmark_btn);

        SharedPreferences sp = this.getApplicationContext().getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor= sp.edit();
        id = sp.getString("id", "null");
        try {
            if (handler.select(path, id) == null) {
                bookmark.setBackgroundResource(R.drawable.empty_star);
            } else {
                bookmark.setBackgroundResource(R.drawable.star);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.equals("null")){
                    final LovelyStandardDialog memoDialog = new LovelyStandardDialog(ResultActivity.this);
                    memoDialog.setTopColorRes(R.color.bookmark)
                            .setTopTitle("BOOKMARK")
                            .setTopTitleColor(Color.WHITE)
                            .setIcon(R.drawable.alert_info)
                            .setIconTintColor(Color.WHITE)
                            .setMessage("로그인 후 이용가능합니다. 로그인 창으로 이동하시겠습니까?")
                            .setPositiveButton("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ResultActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }else {
                    if(handler.select(path,id)==null){
                        bookmark.setBackgroundResource(R.drawable.star);
                        SimpleDateFormat sdf_milli = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");
                        String milli = sdf_milli.format(new Date());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                        now = sdf.format(new Date());
                        handler.insert(milli,id,path, startplace, endplace, ss[1], ss[0], es[1], es[0],now,"i","a");
                        Toast.makeText(getApplicationContext(),"즐겨찾기가 설정되었습니다.",Toast.LENGTH_SHORT).show();
                    }else{
                        bookmark.setBackgroundResource(R.drawable.empty_star);
                        searchMilli();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                        now = sdf.format(new Date());
                        Log.d("테스트3",bookmarkInfo.milli);
                        handler.delete(bookmarkInfo.milli,now,"d");
                        Toast.makeText(getApplicationContext(),"즐겨찾기가 해제되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        data = new ArrayList<>();
        //adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        adapter = new ArrayAdapter(this, R.layout.result_list, data);
        list = (ListView)findViewById(R.id.result_list);

        //경로
        GetXMLTask task = new GetXMLTask();
        task.execute("http://ws.bus.go.kr/api/rest/pathinfo/getPathInfoByBusNSub?ServiceKey=hZnoJmjUcxdBMINo96i0zmSwXBv0LQxzQ1yHSYuDgp5QwASB32SFCigrkx6ExVtWdFcBIIhbPRcxG8Hod9ZRHA%3D%3D&startX="+ ss[0] +"&startY="+ ss[1] +"&endX="+ es[0] +"&endY="+ es[1]);

        Log.d("주소", "http://ws.bus.go.kr/api/rest/pathinfo/getPathInfoByBusNSub?ServiceKey=hZnoJmjUcxdBMINo96i0zmSwXBv0LQxzQ1yHSYuDgp5QwASB32SFCigrkx6ExVtWdFcBIIhbPRcxG8Hod9ZRHA%3D%3D&startX="+ ss[0] +"&startY="+ ss[1] +"&endX="+ es[0] +"&endY="+ es[1]);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent2 = new Intent(getApplicationContext(), DetailResultActivity.class);
                intent2.putExtra("webAddr", "http://ws.bus.go.kr/api/rest/pathinfo/getPathInfoByBusNSub?ServiceKey=hZnoJmjUcxdBMINo96i0zmSwXBv0LQxzQ1yHSYuDgp5QwASB32SFCigrkx6ExVtWdFcBIIhbPRcxG8Hod9ZRHA%3D%3D&startX="+ ss[0] +"&startY="+ ss[1] +"&endX="+ es[0] +"&endY="+ es[1]);
                intent2.putExtra("position", i);
                intent2.putExtra("startplace2", startplace);
                intent2.putExtra("endplace2", endplace);
                intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent2);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (handler.select(path, id) == null) {
                bookmark.setBackgroundResource(R.drawable.empty_star);
            } else {
                bookmark.setBackgroundResource(R.drawable.star);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 값(ALL Data) 삭제하기
    private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    //날씨 파싱
    private class GetXMLTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                doc.getDocumentElement().normalize();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            String route;
            String tname;

            NodeList nodeList = doc.getElementsByTagName("itemList");

            if(nodeList.getLength()==0){
                Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }

            //itemlist의 총 개수만큼 for문을 돌린다.
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Element fstElmnt = (Element)node;

                NodeList timeList = fstElmnt.getElementsByTagName("time");
                Element timeElement = (Element)timeList.item(0);
                timeList = timeElement.getChildNodes();
                String time = "시간 : 약 " + ((Node)timeList.item(0)).getNodeValue() + "분";

                //itemList 속 pathList개수만큼 for문을 돌린다.
                NodeList nodeList1 = doc.getElementsByTagName("pathList");

                //nodeList1.getLength()/nodeList.getLength()
                for(int j=0;j<fstElmnt.getElementsByTagName("pathList").getLength();j++) {
                    NodeList fnameList = fstElmnt.getElementsByTagName("fname");
                    Element fnameElmnt = (Element) fnameList.item(j);
                    fnameList = fnameElmnt.getChildNodes();
                    String fname = ((Node) fnameList.item(0)).getNodeValue() + " → ";

                    NodeList routeList = fstElmnt.getElementsByTagName("routeNm");
                    Element routeElmnt = (Element) routeList.item(j);
                    routeList = routeElmnt.getChildNodes();
                    if((((Node) routeList.item(0)).getNodeValue()).contains("호선")) {
                        route = ((Node) routeList.item(0)).getNodeValue() +" ";
                    }else if(((Node) routeList.item(0)).getNodeValue().contains("선")){
                        route = ((Node) routeList.item(0)).getNodeValue() +" ";
                    }else{
                        route = ((Node) routeList.item(0)).getNodeValue() + "번 ";
                    }

                    NodeList tnameList = fstElmnt.getElementsByTagName("tname");
                    Element tnameElmnt = (Element) tnameList.item(j);
                    tnameList = tnameElmnt.getChildNodes();

                    result += route +fname;
                    if(j == fstElmnt.getElementsByTagName("pathList").getLength()-1){
                        tname = ((Node) tnameList.item(0)).getNodeValue();
                        result += tname+"\n";
                    }
                }

                result += time;
                data.add(result);
                list.setAdapter(adapter);
                result = "";
            }
            //txtR.setText(result);

            super.onPostExecute(doc);
        }
    }//end inner class - GetXMLTask

    protected BookmarkInfo searchMilli() {
        SharedPreferences sp = this.getApplicationContext().getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor= sp.edit();
        String id = sp.getString("id", "null");
        bookmarkInfo = handler.select(path,id);
        return bookmarkInfo;
    }
}
