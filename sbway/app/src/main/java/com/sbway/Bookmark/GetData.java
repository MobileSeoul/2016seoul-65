package com.sbway.Bookmark;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by PC on 2016-05-18.
 */
public class GetData extends AsyncTask<Void, Void, Void> {
    ArrayList<String> titlevec = new ArrayList<>();
    ArrayList<String> descvec = new ArrayList<>();
    // 웹사이트에 접속할 주소
    String uri = "http://ws.bus.go.kr/api/rest/pathinfo/getPathInfoBySubway?ServiceKey=hZnoJmjUcxdBMINo96i0zmSwXBv0LQxzQ1yHSYuDgp5QwASB32SFCigrkx6ExVtWdFcBIIhbPRcxG8Hod9ZRHA%3D%3D&startX=127.0239433&startY=37.6263982&endX=127.0609603&endY=37.5088652";
    // 웹사이트에 접속을 도와주는 클래스
    URL url;
    // XML문서의 내용을 임시로 저장할 변수
    String tagname = "", title="", desc="", time="";

    String getTitle(){
        return tagname;
    }

    String getDesc(){
        return tagname;
    }

    String getTime(){
        return tagname;
    }

    // 데이터의 내용을 모두 읽어드렸는지에 대한 정보를 저장
    Boolean flag = null;

    // 네트워크(url)에 접속해서 xml문서를 가져오는 메소드
    @Override
    protected Void doInBackground(Void... params) {

        try {
            //xml문서를 읽고 해석해줄 수 있는 객체를 선언
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            //네임스페이스 사용여부
            factory.setNamespaceAware(true);
            //실제 xml문서를 읽어 드리면서 데이터를 추출해주는 객체 선언
            XmlPullParser xpp = factory.newPullParser();

            // 웹사이트에 접속
            url = new URL(uri);
            // 사이트 접속후에 xml 문서를 읽어서 가져옴
            InputStream in = url.openStream();
            // 웹사이트로부터 받아온 xml문서를 읽어드리면서 데이터를 추출해 주는 XmlPullParser객체로 넘겨줌
            xpp.setInput(in, "utf-8");

            // 이벤트 내용을 사용하기 위해서 변수 선언
            int eventType = xpp.getEventType();
            // 반복문을 사용하여 문서의 끝까지 읽어 들이면서 데이터를 추출하여 각각의 벡터에 저장
            while(eventType != XmlPullParser.END_DOCUMENT ) {
                if(eventType == XmlPullParser.START_TAG) {
                    // 태그의 이름을 알아야 텍스트를 저장하기에 태그이름을 읽어서 변수에 저장
                    tagname = xpp.getName();
                    if(tagname.equals("time")) time += xpp.getText() + "분";

                } else if(eventType == XmlPullParser.TEXT) {
                    // 태그 이름이 title과 같다면 변수에 title 저장
                    if(tagname.equals("fname")) title += xpp.getText() + "역 ";
                        // 태그 이름이 description과 같다면 desc변수에 저장
                    else if(tagname.equals("routeNm")) title += xpp.getText();
                    else if (tagname.equals("tname")) desc += xpp.getText() + "역 ";
                    else if (tagname.equals("routeNm")) desc += xpp.getText();
                } else if (eventType == XmlPullParser.END_TAG) {
                    // end tag 이름을 얻어옴
                    tagname = xpp.getName();
                    // end tag 이름이 item이라면 저장한 변수 title과 desc를 벡터에 저장
                    if(tagname.equals("pathList")) {
                        titlevec.add(title);
                        descvec.add(desc);
                        // 변수 초기화
                        title="";
                        desc="";
                    }
                }
                // 다음 이벤트를 넘김
                eventType = xpp.next();
            }
            // 모든 xml문서를 읽어드렸다면
            flag = true;

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
