package com.example.chris.requestmanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.chris.requestmanager.entity.CollectionCallBack;
import com.example.chris.requestmanager.entity.DataCallBack;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Admin on 2016/10/31.
 */

public class DBManager {

    private static AtomicInteger openCount = new AtomicInteger();
    private static DBManager instance;
    private static SQLiteOpenHelper openHelper;
    private static final String TAG = "DBManager";
    // rx响应式数据库,
    private static SqlBrite sqlBrite;
    private static BriteDatabase db;

    public static synchronized DBManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(DBManager.class.getSimpleName()
                    + " is not initialized, call initialize(..) method first.");
        }
        return instance;
    }

    public static synchronized void initialize(SQLiteOpenHelper helper) {
        if (null == instance) {
            instance = new DBManager();
        }
        openHelper = helper;
        sqlBrite = SqlBrite.create(new SqlBrite.Logger() {
            @Override
            public void log(String message) {
                //Logger.wtf(TAG, "log: >>>>" + message);
            }
        });
        db = sqlBrite.wrapDatabaseHelper(openHelper, Schedulers.io());
        db.setLoggingEnabled(true);
    }

    public void add(String tableName, ContentValues contentValues) {
        long rowId = db.insert(tableName, contentValues);
    }

    public void add(String tableName, List<ContentValues> contentValuesList) {
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

    public <T> void getObject(String tableName, String sql, String[] whereArgs, Func1<Cursor, T> mapper, final DataCallBack<T> callBack) {
        db.createQuery(tableName, sql, whereArgs)
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

    public <T> void getCollection(String tableName, String sql, String[] whereArgs,  Func1<Cursor, T> mapper, final CollectionCallBack<T> callBack) {
        db.createQuery(tableName, sql, whereArgs)
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
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<T> ts) {
                        callBack.onSuccess(ts);
                    }
                });
    }

    public void update(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) {
        db.update(tableName, contentValues, whereClause, whereArgs);
    }

    public void updata(String tableName, List<ContentValues> contentValuesList, String whereClause, String[] whereArgs) {
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for(ContentValues contentValues : contentValuesList) {
                db.update(tableName, contentValues, whereClause, whereArgs);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    public void delete(String tableName, String whereClause, String[] whereArgs) {
        db.delete(tableName, whereClause, whereArgs);
    }
}
