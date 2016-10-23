package com.example.chris.sqlbritedemo.entity;

import android.content.ContentValues;
import android.database.Cursor;

import rx.functions.Func1;

/**
 * Created by Admin on 2016/10/23.
 */

public class PeopleTable {
    // 表名
    public static final String TABLE_NAME = "people";

    // 表字段
    public static final String ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUME_AGE = "age";

    // 建表语句
    public static final String CREATE =
            "CREATE TABLE "
                    + TABLE_NAME
                    + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT NOT NULL,"
                    + COLUME_AGE + " INT,"
                    + "UNIQUE (" + COLUMN_NAME + ")  ON CONFLICT REPLACE"
                    + " ); ";

    // 响应式的查询,根据表中的row生成一个对象
    public static Func1<Cursor, People> PERSON_MAPPER = new Func1<Cursor, People>() {
        @Override
        public People call(Cursor cursor) {
            People people = new People();
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            people.setName(name);
            int age = cursor.getInt(cursor.getColumnIndexOrThrow(COLUME_AGE));
            people.setAge(age);
            return people;
        }
    };

    public static ContentValues toContentValues(People person) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, person.getName());
        values.put(COLUME_AGE, person.getAge());
        return values;
    }
}
