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
    private String whereClause;
    private String[] whereArgs;
    private Func1<Cursor, T> mapper;

    private DBRequest(Builder builder) {
        this.tableName = builder.tableName;
        this.contentValues = builder.contentValues;
        this.querySql = builder.querySql;
        this.whereClause = builder.whereClause;
        this.whereArgs = builder.whereArgs;
        this.mapper = builder.mapper;
    }

    public static class Builder<T> {
        private String tableName;
        private List<ContentValues> contentValues = new ArrayList<>();
        private String querySql;
        private String whereClause;
        private String[] whereArgs;
        private Func1<Cursor, T> mapper;

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder contentValues(ContentValues contentValues) {
            this.contentValues.add(contentValues);
            return this;
        }

        public Builder querySql(String sql) {
            this.querySql = sql;
            return this;
        }

        public Builder whereArgs(String[] whereArgs) {
            this.whereArgs = whereArgs;
            return this;
        }

        public Builder mapper(Func1<Cursor, T> mapper) {
            this.mapper = mapper;
            return this;
        }

        public Builder whereClause(String whereClause) {
            this.whereClause = whereClause;
            return this;
        }

        public DBRequest build() {
            return new DBRequest(this);
        }

        public DBRequest add() {
            DBRequest dbRequest = build();
            dbRequest.add();
            return dbRequest;
        }

        public DBRequest getList(CollectionCallBack<T> callBack) {
            DBRequest dbRequest = build();
            dbRequest.getList(callBack);
            return dbRequest;
        }

        public DBRequest update() {
            DBRequest dbRequest = build();
            dbRequest.update();
            return dbRequest;
        }

        public DBRequest delete() {
            DBRequest dbRequest = build();
            dbRequest.delete();
            return dbRequest;
        }

    }

    private void add() {
        if(tableName == null) {
            throw new NullPointerException("tableName is null");
        }else if(contentValues.size() == 0) {
            throw new NullPointerException("contentValues is null");
        }
        DBManager.getInstance().add(tableName, contentValues);
    }


    private void getList(CollectionCallBack<T> callBack) {
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

    private void update() {
        DBManager.getInstance().update(tableName, contentValues.get(0), whereClause, whereArgs);
    }

    private void delete() {
        DBManager.getInstance().delete(tableName, whereClause, whereArgs);
    }


}
