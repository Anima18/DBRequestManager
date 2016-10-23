package com.example.chris.sqlbritedemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.chris.sqlbritedemo.entity.PeopleTable;

public class SQLOpenHelper extends SQLiteOpenHelper {
  public static final String DB_NAME = "sqlbrite.db";
  public static final int DB_VERSION = 1;

  private static SQLOpenHelper instance;

  public static SQLOpenHelper getInstance(Context context) {
    if (null == instance) {
      instance = new SQLOpenHelper(context);
    }
    return instance;
  }

  private SQLOpenHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(PeopleTable.CREATE);

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}
