package com.gurutel.gurufit;

/**
 * Created by udnet_pc1 on 2015-08-10.
 */
public class MyGlobals {

    private String mMyPhoneNumber;
    public String getmMyPhoneNumber()
    {
        return mMyPhoneNumber;
    }

    public void setmMyPhoneNumber(String MyPhoneNumber)
    {
        this.mMyPhoneNumber = MyPhoneNumber;
    }

    private String mStepCount;
    public String getmStepCount()
    {
        return mStepCount;
    }
    public void setmStepCount(String StepCount)
    {
        this.mStepCount = StepCount;
    }


    private String mStepStartDate;
    public String getmStepStartDate()
    {
        return mStepStartDate;
    }
    public void setmStepStartDate(String StepStartDate)
    {
        this.mStepStartDate = StepStartDate;
    }

    private String mStepEndDate;
    public String getmStepEndDate()
    {
        return mStepEndDate;
    }
    public void setmStepEndDate(String StepEndDate)
    {
        this.mStepEndDate = StepEndDate;
    }

    private String mLat;
    public String getmLat()
    {
        return mLat;
    }
    public void setmLat(String Lat)
    {
        this.mLat = Lat;
    }

    private String mLon;
    public String getmLon()
    {
        return mLon;
    }
    public void setmLon(String Lon)
    {
        this.mLon = Lon;
    }

    private String mAccuracy;
    public String getmAccuracy()
    {
        return mAccuracy;
    }
    public void setmAccuracy(String Accuracy)
    {
        this.mAccuracy = Accuracy;
    }

    /*
    private static MyGlobals instance = null;

    public static synchronized MyGlobals getInstance(){
        if(null == instance){
            instance = new MyGlobals();
        }
        return instance;
    }
  */
    private MyGlobals(){

    }

    private volatile static MyGlobals instance = null;
    public static MyGlobals getInstance(){
        if(instance==null){
            synchronized (MyGlobals.class){
                if(instance==null){
                    instance = new MyGlobals();
                }
            }
        }
        return instance;
    }

}
