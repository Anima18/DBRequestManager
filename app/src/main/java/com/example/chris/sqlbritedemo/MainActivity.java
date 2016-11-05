package com.example.chris.sqlbritedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chris.requestmanager.DBRequest;
import com.example.chris.requestmanager.entity.CollectionCallBack;
import com.example.chris.sqlbritedemo.entity.People;
import com.example.chris.sqlbritedemo.entity.PeopleTable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";
    private EditText nameEt;
    private Button addBt;
    private Button searchBt;
    private Button updateBt;
    private Button deleteBt;
    private RecyclerView dataLv;

    private List<People> dataList = new ArrayList<>();
    private People people;
    private MainAdapter adapter;

    final AtomicInteger queries = new AtomicInteger();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Queries: " + queries.get());
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        nameEt = (EditText) findViewById(R.id.mainAct_name_et);
        addBt = (Button) findViewById(R.id.mainAct_add_bt);
        searchBt = (Button) findViewById(R.id.mainAct_search_bt);
        updateBt = (Button) findViewById(R.id.mainAct_update_bt);
        deleteBt = (Button) findViewById(R.id.mainAct_delete_bt);
        dataLv = (RecyclerView) findViewById(R.id.mainAct_data_lv);
    }

    private void initData() {
        adapter = new MainAdapter(this, dataList);
        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                people = dataList.get(position);
                nameEt.setText(people.getName());
            }
        });
        dataLv.setAdapter(adapter);
        dataLv.setLayoutManager(new LinearLayoutManager(this));

        search();
    }

    private void initEvent() {
        addBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
        updateBt.setOnClickListener(this);
        deleteBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mainAct_add_bt:
                addPeople();
                break;
            case R.id.mainAct_search_bt:
                search();
                break;
            case R.id.mainAct_update_bt:
                update();
                break;
            case R.id.mainAct_delete_bt:
                delete();
                break;
        }
    }

    private void update() {
        String name = nameEt.getText().toString().trim();
        if(people == null) {
            Toast.makeText(this, "请选择一条记录", Toast.LENGTH_SHORT).show();
            return;
        }else {
            people.setName(name);
        }

        new DBRequest.Builder()
                .tableName(PeopleTable.TABLE_NAME)
                .contentValues(PeopleTable.toContentValues(people))
                .whereClause("_id = ?")
                .whereArgs(new String[] {people.getId()+""})
                .update();
        //DBManager.getInstance().update(PeopleTable.TABLE_NAME, PeopleTable.toContentValues(people), "_id = ?", new String[] {people.getId()+""});

        nameEt.setText("");
        people = null;
        search();
    }

    public void delete() {
        if(people == null) {
            Toast.makeText(this, "请选择一条记录", Toast.LENGTH_SHORT).show();
            return;
        }else {
            //DBManager.getInstance().delete(PeopleTable.TABLE_NAME, "_id = ?", new String[] {people.getId()+""});
            new DBRequest.Builder()
                    .tableName(PeopleTable.TABLE_NAME)
                    .whereClause("_id = ?")
                    .whereArgs(new String[] {people.getId()+""})
                    .delete();
            nameEt.setText("");
            people = null;
        }
    }

    private void addPeople() {
        String name = nameEt.getText().toString().trim();
        if(name == null || "".equals(name)) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        People people = new People(name, 25);
        new DBRequest.Builder().tableName(PeopleTable.TABLE_NAME).contentValues(PeopleTable.toContentValues(people)).add();

        nameEt.setText("");
        search();
        Log.i(TAG, "Queries: " + queries.get());
    }

    private void search() {
        String name = nameEt.getText().toString().trim();
        if(name == null || "".equals(name)) {
            new DBRequest.Builder()
                    .tableName(PeopleTable.TABLE_NAME)
                    .querySql("SELECT * FROM " + PeopleTable.TABLE_NAME)
                    .mapper(PeopleTable.PERSON_MAPPER)
                    .getList(getSubscriber());
        }else {
            new DBRequest.Builder()
                    .tableName(PeopleTable.TABLE_NAME)
                    .querySql("SELECT * FROM " + PeopleTable.TABLE_NAME+ " where name = ?")
                    .whereArgs(new String[]{name})
                    .mapper(PeopleTable.PERSON_MAPPER)
                    .getList(getSubscriber());
        }
    }

    private CollectionCallBack<People> getSubscriber() {
        return new CollectionCallBack<People>() {
            @Override
            public void onFailure(int code, String message) {
                Log.i(TAG, message);
            }

            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted");
            }

            @Override
            public void onSuccess(List<People> datas) {
                dataList.clear();
                dataList.addAll(datas);
                adapter.notifyDataSetChanged();
                queries.getAndIncrement();
            }
        };
    }
}
