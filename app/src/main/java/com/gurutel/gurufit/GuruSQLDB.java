package com.gurutel.gurufit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by udnet_pc1 on 2015-08-17.
 */
public class GuruSQLDB extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "gurufit";
    private static final String TB_NAME = "guru_info";

    public GuruSQLDB (Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TB_NAME + " (name text primary key, val text);");
        db.execSQL("INSERT INTO  " + TB_NAME + "  (name,val) values('step','0')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }

    public String getStep(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TB_NAME, new String[]{"name","val"},"name=?",new String[]{"step"},null,null,null,null);
        if(cursor != null)
            cursor.moveToFirst();
        //cursor.getString(0);   --> name
        return cursor.getString(1); //--> val

    }

    public int updateStep(String stepCnt){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("val",stepCnt);

        return db.update(TB_NAME,values,"name = ?",new String[]{"step"});
    }

}
