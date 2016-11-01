package com.example.chris.requestmanager.entity;

import android.content.ContentValues;
import android.database.Cursor;

import rx.functions.Func1;

/**
 * Created by Admin on 2016/10/31.
 */

public interface SqlBriteable {
    String createTable();
    String getTableName();
    ContentValues contentValues();
    <T extends SqlBriteable> Func1<Cursor, T> func1();
}
