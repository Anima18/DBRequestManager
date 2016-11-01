package com.example.chris.sqlbritedemo.database;

import android.app.Application;

import com.example.chris.requestmanager.DBRequest;


/**
 * Created by Admin on 2016/10/23.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DBRequest.initialize(SQLOpenHelper.getInstance(this));

    }
}
