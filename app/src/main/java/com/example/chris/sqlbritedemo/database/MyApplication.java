package com.example.chris.sqlbritedemo.database;

import android.app.Application;

/**
 * Created by Admin on 2016/10/23.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseManager.initialize(SQLOpenHelper.getInstance(this));
    }
}
