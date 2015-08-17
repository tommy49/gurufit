package com.gurutel.gurufit;

/**
 * Created by udnet_pc1 on 2015-08-07.
 */

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;

public class History {
    public static final String TAG = "GuruFit";
    private GoogleApiClient client;

    private SendInfo sendInfo;

    private GuruSQLDB db;

    public History(GoogleApiClient client) {
        this.client = client;
    }

    public void readBefore(Date date) {
        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
        cal.setTime(date);
        long endTime = cal.getTimeInMillis();
        //cal.add(Calendar.DAY_OF_YEAR, -1);
        //long startTime = cal.getTimeInMillis();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        java.util.Date mDate = cal.getTime();
        String Today = new SimpleDateFormat("yyyyMMdd").format(mDate);
        try {
            cal.setTime(formatter.parse(Today));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        long startTime = cal.getTimeInMillis();
        read(startTime, endTime);
    }


    public void read(long start, long end) {

//        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");// SimpleDateFormat.getDateInstance();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// SimpleDateFormat.getDateInstance();

        Log.i(TAG, "history reading range: " + dateFormat.format(start) + " - " + dateFormat.format(end));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
               // .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(start, end, TimeUnit.MILLISECONDS)
                .build();

        Fitness.HistoryApi.readData(client, readRequest).setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(DataReadResult dataReadResult) {
                if (dataReadResult.getBuckets().size() > 0) {
                    Log.i(TAG, "DataSet.size(): "
                           + dataReadResult.getBuckets().size());
                    for (Bucket bucket : dataReadResult.getBuckets()) {
                        List<DataSet> dataSets = bucket.getDataSets();
                        for (DataSet dataSet : dataSets) {
                            Log.i(TAG, "dataSet.dataType: " + dataSet.getDataType().getName());

                            for (DataPoint dp : dataSet.getDataPoints()) {
                                describeDataPoint(dp, dateFormat);
                            }
                        }
                    }
                } else if (dataReadResult.getDataSets().size() > 0) {
                    Log.i(TAG, "dataSet.size(): " + dataReadResult.getDataSets().size());
                    for (DataSet dataSet : dataReadResult.getDataSets()) {
                        Log.i(TAG, "dataType: " + dataSet.getDataType().getName());

                        for (DataPoint dp : dataSet.getDataPoints()) {
                            describeDataPoint(dp, dateFormat);
                        }
                    }
                }

            }
        });
    }

    public void describeDataPoint(DataPoint dp, DateFormat dateFormat) {
        String msg = "dataPoint: "
                + "type: " + dp.getDataType().getName() +"\n"
                + ", range: [" + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "-" + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "]\n"
                + ", fields: [";

        MyGlobals myGlobals = MyGlobals.getInstance();
        myGlobals.setmStepStartDate(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
        myGlobals.setmStepEndDate(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
        //Log.i(TAG, "Step Start Date : " + globalApp.getmStepStartDate());

        for(Field field : dp.getDataType().getFields()) {
            msg += field.getName() + "=" + dp.getValue(field) + " ";
            if(field.getName().equals("steps")){
                myGlobals.setmStepCount(String.valueOf(dp.getValue(field)));
                db = new GuruSQLDB(this.client.getContext());
                int dbUpStep = db.updateStep(String.valueOf(dp.getValue(field)));
                Log.i(TAG, "STEPS COUNT : "+dp.getValue(field));
                Log.i(TAG, "DB UPDATE RESULT : "+dbUpStep);
            }
        }

        msg += "]";
        Log.i(TAG, msg);
        sendInfo = new SendInfo();
        sendInfo.execute("");
    }

}
