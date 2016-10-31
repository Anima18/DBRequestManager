package com.example.chris.requestmanager;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.chris.requestmanager.entity.CollectionCallBack;
import com.example.chris.requestmanager.entity.DataCallBack;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Admin on 2016/10/31.
 */

public class DBRequest {
    private static BriteDatabase db;

    public static synchronized void initialize(BriteDatabase briteDatabase) {
        if (briteDatabase == null) {
            throw new NullPointerException("BriteDatabase database is null");
        }else {
            db = briteDatabase;
        }
    }


    public static void add(String tableName, ContentValues contentValues) {
        long rowId = db.insert(tableName, contentValues);
    }

    public static void add(String tableName, List<ContentValues> contentValuesList) {
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for(ContentValues contentValues : contentValuesList) {
                db.insert(tableName, contentValues);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public static <T> void getObject(String tableName, String sql, Func1<Cursor, T> mapper, final DataCallBack<T> callBack) {
        db.createQuery(tableName, sql)
        .mapToOne(mapper)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<T>() {
            @Override
            public void onCompleted() {
                callBack.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                callBack.onFailure(500, e.getMessage());
            }

            @Override
            public void onNext(T t) {
                callBack.onSuccess(t);
            }
        });
    }

    public static <T> void getCollection(String tableName, String sql, Func1<Cursor, T> mapper, final CollectionCallBack<T> callBack) {
        db.createQuery(tableName, sql)
                .mapToList(mapper)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<T>>() {
                    @Override
                    public void onCompleted() {
                        callBack.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.onFailure(500, e.getMessage());
                    }

                    @Override
                    public void onNext(List<T> ts) {
                        callBack.onSuccess(ts);
                    }
                });
    }

}
