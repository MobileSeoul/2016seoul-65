package com.sbway.PathSearch;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sbway.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DetailResultActivity extends AppCompatActivity {
    String webAddr, startplace, endplace;
    int getPosition;
    Document doc;
    String result="";
    TextView detail_title1, detail_title3;
    TextView detail_time, detail_dis;

    ArrayList<String> data;
    ListView list;
    DetailResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_result);
        Intent intent = getIntent();
        webAddr = intent.getStringExtra("webAddr");
        getPosition = intent.getIntExtra("position", 0);
        startplace = intent.getStringExtra("startplace2");
        endplace = intent.getStringExtra("endplace2");

        detail_title1 = (TextView)findViewById(R.id.detail_title1);
        detail_title3 = (TextView)findViewById(R.id.detail_title3);
        detail_time = (TextView)findViewById(R.id.detail_time);
        detail_dis = (TextView)findViewById(R.id.detail_dis);
        detail_title1.setText(startplace);
        detail_title3.setText(endplace);
        ((TextView)findViewById(R.id.start_loc)).setText(startplace);
        ((TextView)findViewById(R.id.end_loc)).setText(endplace);


        data = new ArrayList<String>();
        adapter = new DetailResultAdapter(this, data);
        list = (ListView)findViewById(R.id.detail_list);
        list.setAdapter(adapter);


        //경로
        GetXMLTask task = new GetXMLTask();
        task.execute(webAddr);

    }

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
            Node node = nodeList.item(getPosition);
            Element fstElmnt = (Element)node;

            NodeList disList = fstElmnt.getElementsByTagName("distance");
            Element disElement = (Element)disList.item(0);
            disList = disElement.getChildNodes();
            String dis = ((Node)disList.item(0)).getNodeValue();
            String caldis = "약 " + String.valueOf(Float.valueOf(dis)/1000)+"km";

            NodeList timeList = fstElmnt.getElementsByTagName("time");
            Element timeElement = (Element)timeList.item(0);
            timeList = timeElement.getChildNodes();
            String time = ((Node)timeList.item(0)).getNodeValue();

            //itemList 속 pathList개수만큼 for문을 돌린다.
            NodeList nodeList1 = doc.getElementsByTagName("pathList");

            //nodeList1.getLength()/nodeList.getLength()
            for(int j=0;j<fstElmnt.getElementsByTagName("pathList").getLength();j++) {
                NodeList fnameList = fstElmnt.getElementsByTagName("fname");
                Element fnameElmnt = (Element) fnameList.item(j);
                fnameList = fnameElmnt.getChildNodes();
                String fname = ((Node) fnameList.item(0)).getNodeValue();

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

                if(j == fstElmnt.getElementsByTagName("pathList").getLength()-1){
                    tname = ((Node) tnameList.item(0)).getNodeValue();
                }else{
                    tname = ((Node) tnameList.item(0)).getNodeValue();
                    if(fname == tname){}
                    else {
                    }
                }
                data.add(route+fname+" ("+tname+"에서 하차)");
            }

            detail_time.setText(time);
            detail_dis.setText(caldis);

            adapter.notifyDataSetChanged();
        //txtR.setText(result);

        super.onPostExecute(doc);
        }
    }//end inner class - GetXMLTask

}
