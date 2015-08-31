package com.gurutel.gurufit;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.MapView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by udnet_pc1 on 2015-08-31.
 */
public class CustomerListActivity extends Activity {
    private  static final String TAG = "GuruFit";
    String responseBody="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listmain);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try{

            String sURL = "http://data.udnet.co.kr/GetCustomerList.php";
            HttpClient httpclient = new DefaultHttpClient(); //httpclient 객체를 생성
            HttpGet httpget = new HttpGet(sURL);

            // Create a response handler
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = httpclient.execute(httpget, responseHandler);
            Log.d(TAG, "========= response [" + responseBody + "]===============================================");

            httpclient.getConnectionManager().shutdown();        //서버와의 통신 종료
        }catch(Exception e){
            e.printStackTrace();
        }

        initList();
        ListView listView =(ListView) findViewById(R.id.list);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, CustomerList,
                R.layout.listbtntxt,
                new String[] {"email","tel","serial"},
                new int[] {R.id.list_button3 ,R.id.list_button1,R.id.list_button2})
                //create list of the items to set on adapter
                {
                  public View getView(final int position, View convertView, android.view.ViewGroup parent){
                      View v = super.getView(position,convertView,parent);
                      Button btn1 = (Button)v.findViewById(R.id.list_button1);
                      btn1.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              Log.d(TAG, "========= Click Button 1 [" + CustomerList.get(position).get("tel") + "]===============================================");
                              String tel =  CustomerList.get(position).get("tel");
                              tel = tel.replace("+82","0");
                              Uri uri = Uri.parse("tel:"+tel);
                              Intent i = new Intent(Intent.ACTION_CALL,uri);
                              startActivity(i);
                          }
                      });

                      Button btn2 = (Button)v.findViewById(R.id.list_button2);
                      btn2.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {

                              Log.d(TAG, "========= Click Button 2 [" + CustomerList.get(position).get("serial") + "]===============================================");
                              Intent i = new Intent(v.getContext(), MapViewActivity.class);
                              i.putExtra("serial",CustomerList.get(position).get("serial"));
                              startActivity(i);
                              finish();
                          }
                      });

                      Button btn3 = (Button)v.findViewById(R.id.list_button3);
                      btn3.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              Log.d(TAG, "========= Click Button 3 [" + CustomerList.get(position).get("email") + "]===============================================");
                          }
                      });

                      return v;
                  }
                };


        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent;
             // Log.d(TAG, "========= List Click[" + CustomerList.get(position).get("email") + "]===============================================");

                // if (channelList.get(position).get("type").equals("vitamio")) {
                 //   Log.d(TAG, "========= List Click[" + channelList.get(position).get("type") + "]===============================================");
                  //  intent = new Intent(ListActivity.this, PlayerActivity.class);
                  //  intent.putExtra("URL", channelList.get(position).get("val"));
                  //  startActivity(intent);
               // } else {
                //    Log.d(TAG, "========= List Click[" + channelList.get(position).get("type") + "]===============================================");
                   // intent = new Intent(ListActivity.this, Players.class);
                   // intent.putExtra("URL", channelList.get(position).get("val"));
                   // startActivity(intent);
               // }
            }
        });

    }

    List<Map<String,String>> CustomerList = new ArrayList<Map<String,String>>();

    private void initList(){
        try{
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("CustList");
            Log.d(TAG, "========= JSON CNT [" + jsonMainNode.length() + "]===============================================");

            for(int i = 0; i<jsonMainNode.length();i++){
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String email = jsonChildNode.getString("email").toString();
                String tel = jsonChildNode.optString("tel").toString();
                String serial = jsonChildNode.optString("serial").toString();
                //String outPut = channel + "--->" +val;
                Log.d(TAG, "========= JSON [" + email + "]===============================================");
                CustomerList.add(createChannel(email, tel, serial));
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private HashMap<String, String> createChannel(String email,String tel,String serial){
        HashMap<String, String> employeeNameNo = new HashMap<String, String>();
        employeeNameNo.put("email", email);
        employeeNameNo.put("tel", tel);
        employeeNameNo.put("serial",serial);
        return employeeNameNo;
    }

}
