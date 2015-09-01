package com.gurutel.gurufit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by udnet_pc1 on 2015-09-01.
 */
public class StepGraphInfoActivity extends Activity {
    private  static final String TAG = "GuruFit";
    private static final int MENU_MAP_RETURN = Menu.FIRST + 1;
    String responseBody="";
    Calendar calendar = Calendar.getInstance();
    Date sDate, eDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String serial = i.getStringExtra("serial");
        Log.d(TAG, "Step Graph Info Serial : " + serial);

        try{
            String sURL = "http://data.udnet.co.kr/GetCustStep.php?serial="+serial;
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

        setContentView(R.layout.fragment_main);
        GraphView graph = (GraphView) findViewById(R.id.graph);

        int avg = 0;
        int tot = 0;
        for( int cnt=1; cnt < StepList.size(); cnt++){
            tot = tot + Integer.parseInt(StepList.get(cnt).get("step_cnt"));
        }
        if( tot > 0) avg = tot/StepList.size();

        DataPoint[] dp01 = generateData();

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(generateAvgData(avg));
        graph.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(dp01);
        graph.addSeries(series2);

        PointsGraphSeries<DataPoint> series3 = new PointsGraphSeries<DataPoint>(dp01);
        graph.addSeries(series3);
        series3.setColor(Color.BLACK);
        series3.setSize(10);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        series.setTitle("AVG");
        series2.setTitle("STEP");

        series.setColor(Color.BLUE);
        series2.setColor(Color.RED);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(sDate.getTime());
        graph.getViewport().setMaxX(eDate.getTime());

        //graph.onDataChanged(false, false);
        //graph.getViewport().setScrollable(true);
        //graph.getViewport().setScalable(true);

       // graph.getLegendRenderer().setVisible(true);
       // graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);


        series3.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {

                String tDate = new SimpleDateFormat("yyyy-MM-dd").format(dataPoint.getX());
                Toast.makeText(getApplicationContext(), "DATE[" + tDate + "]  STEPS ["+ Double.toString(dataPoint.getY()).replace(".0","") +"]", Toast.LENGTH_SHORT).show();
            }
        });

    }


    List<Map<String,String>> StepList = new ArrayList<Map<String,String>>();

    private void initList(){
        try{
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("StepList");
            Log.d(TAG, "========= JSON CNT [" + jsonMainNode.length() + "]===============================================");

            for(int i = 0; i<jsonMainNode.length();i++){
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String step_cnt = jsonChildNode.optString("step_cnt").toString();
                String reg_date = jsonChildNode.optString("reg_date").toString();
                StepList.add(createChannel(step_cnt, reg_date));
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private HashMap<String, String> createChannel(String step_cnt,String reg_date){
        HashMap<String, String> employeeNameNo = new HashMap<String, String>();
        employeeNameNo.put("step_cnt", step_cnt);
        employeeNameNo.put("reg_date", reg_date);
        return employeeNameNo;
    }

    private DataPoint[] generateAvgData(int avg) {
        int count = StepList.size();
        DataPoint[] values = new DataPoint[count];

        int j = 0;
         for (int i = count; i >  0; i--) {

                 //  double x = Double.parseDouble(StepList.get(i - 1).get("reg_date").substring(3,10).replaceAll("-",""));


             String from = StepList.get(i-1).get("reg_date");

             SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
             Date x = null;
             try {
                 x = transFormat.parse(from);
             }catch(Exception e){
             }


             double y = avg;
                DataPoint v = new DataPoint(x,y);
                values[j] = v;
                j++;
         }
        return values;
    }

    private DataPoint[] generateData() {
        int count = StepList.size();
        DataPoint[] values = new DataPoint[count];


        int j = 0;
        for (int i = count; i >  0; i--) {

                // double x = Double.parseDouble(StepList.get(i - 1).get("reg_date").substring(2,10).replaceAll("-",""));
           // double x = Double.parseDouble(StepList.get(i - 1).get("reg_date"));
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
            String from = StepList.get(i - 1).get("reg_date");

            java.util.Date x = null;
            try {
                x = transFormat.parse(from);
            }catch(Exception e){
            }

            if (i==count) sDate = x;
            //if (j < 5)
                eDate = x;

            double y = Double.parseDouble(StepList.get(i - 1).get("step_cnt"));

            Log.d(TAG, "========= x [" +  x + "] y [" + y + "]===============================================");

            DataPoint v = new DataPoint(x,y);
            values[j] = v;
            j++;
        }

        return values;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_MAP_RETURN, Menu.NONE, "Main");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int itemId = item.getItemId();

        switch (itemId) {
            case MENU_MAP_RETURN:{
                startActivity(new Intent(this, CustomerListActivity.class));
                finish();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}