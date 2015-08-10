package com.gurutel.gurufit;

import android.app.Application;

/**
 * Created by tey3 on 15. 8. 9.
 */
public class GlobalApp extends Application {

    private String mMyPhoneNumber;
    public String getmMyPhoneNumber()
    {
        return mMyPhoneNumber;
    }

    public void setmMyPhoneNumber(String MyPhoneNumber)
    {
        this.mMyPhoneNumber = MyPhoneNumber;
    }



}
