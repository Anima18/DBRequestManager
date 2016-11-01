package com.example.chris.requestmanager;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.chris.requestmanager.entity.CollectionCallBack;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

/**
 * Created by Admin on 2016/11/1.
 */

public class DBRequest<T> {
    private String tableName;
    private List<ContentValues> contentValues = new ArrayList<>();
    private String querySql;
    private String[] whereArgs;
    private Func1<Cursor, T> mapper;


    private DBRequest() {}

    public static DBRequest create() {
        return new DBRequest();
    }

    public void add() {
        if(tableName == null) {
            throw new NullPointerException("tableName is null");
        }else if(contentValues.size() == 0) {
            throw new NullPointerException("contentValues is null");
        }
        DBManager.getInstance().add(tableName, contentValues);
    }

    public void getList(CollectionCallBack<T> callBack) {
        if(tableName == null) {
            throw new NullPointerException("tableName is null");
        }else if(querySql == null) {
            throw new NullPointerException("querySql is null");
        }else if(mapper == null) {
            throw new NullPointerException("mapper is null");
        }else if(callBack == null) {
            throw new NullPointerException("callBack is null");
        }
        DBManager.getInstance().getCollection(tableName, querySql, whereArgs, mapper, callBack);
    }

    public DBRequest tableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DBRequest contentValues(ContentValues contentValues) {
        this.contentValues.add(contentValues);
        return this;
    }

    public DBRequest querySql(String sql) {
        this.querySql = sql;
        return this;
    }

    public DBRequest whereArgs(String[] whereArgs) {
        this.whereArgs = whereArgs;
        return this;
    }

    public DBRequest mapper(Func1<Cursor, T> mapper) {
        this.mapper = mapper;
        return this;
    }
}
