package com.example.chris.sqlbritedemo.database;

import com.example.chris.sqlbritedemo.entity.People;
import com.example.chris.sqlbritedemo.entity.PeopleTable;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DatabaseManager {

  private static AtomicInteger openCount = new AtomicInteger();
  private static DatabaseManager instance;
  private static SQLOpenHelper openHelper;
  private static final String TAG = "ATDbManager";
  // rx响应式数据库,
  private static SqlBrite sqlBrite;
  private static BriteDatabase database;

  public static synchronized DatabaseManager getInstance() {
    if (null == instance) {
      throw new IllegalStateException(DatabaseManager.class.getSimpleName()
          + " is not initialized, call initialize(..) method first.");
    }
    return instance;
  }

  public static synchronized void initialize(SQLOpenHelper helper) {
    if (null == instance) {
      instance = new DatabaseManager();
    }
    openHelper = helper;
    sqlBrite = SqlBrite.create(new SqlBrite.Logger() {
      @Override
      public void log(String message) {
        //Logger.wtf(TAG, "log: >>>>" + message);
      }
    });
    database = sqlBrite.wrapDatabaseHelper(openHelper, Schedulers.io());
    database.setLoggingEnabled(true);
  }

/*  public synchronized BriteDatabase openDatabase() {
    if (openCount.incrementAndGet() == 1) {
      // 执行slqbirte 构造数据库的语句
      database = sqlBrite.wrapDatabaseHelper(openHelper, Schedulers.io());
      database.setLoggingEnabled(true);
    }
    return database;
  }

  public synchronized void closeDatabase() {
    if (openCount.decrementAndGet() == 0) {
      database.close();
    }
  }*/

  public Observable<List<People>> queryAll() {
    return database
            .createQuery(PeopleTable.TABLE_NAME, "SELECT * FROM " + PeopleTable.TABLE_NAME)
            .mapToList(PeopleTable.PERSON_MAPPER)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

  }
  public Observable<List<People>> queryPersonByName(String name) {
    return database.createQuery(PeopleTable.TABLE_NAME, "SELECT * FROM "
                    + PeopleTable.TABLE_NAME
                    + " WHERE "
                    + PeopleTable.COLUMN_NAME
                    + " = ?"
            , name)
            .mapToList(PeopleTable.PERSON_MAPPER)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
  }

  public long addPeople(People people) {
    return database.insert(PeopleTable.TABLE_NAME, PeopleTable.toContentValues(people));
  }

}
