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

import com.example.chris.sqlbritedemo.database.DatabaseManager;
import com.example.chris.sqlbritedemo.entity.People;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Subscriber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";
    private EditText nameEt;
    private EditText ageEt;
    private Button addBt;
    private Button searchBt;
    private RecyclerView dataLv;

    private List<People> dataList = new ArrayList<>();
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
        ageEt = (EditText) findViewById(R.id.mainAct_age_et);
        addBt = (Button) findViewById(R.id.mainAct_add_bt);
        searchBt = (Button) findViewById(R.id.mainAct_search_bt);
        dataLv = (RecyclerView) findViewById(R.id.mainAct_data_lv);
    }

    private void initData() {
        adapter = new MainAdapter(this, dataList);
        dataLv.setAdapter(adapter);
        dataLv.setLayoutManager(new LinearLayoutManager(this));

        DatabaseManager.getInstance().queryAll().subscribe(getSubscriber());
    }

    private void initEvent() {
        addBt.setOnClickListener(this);
        searchBt.setOnClickListener(this);
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
        }
    }

    private void addPeople() {
        String name = nameEt.getText().toString().trim();
        String age = ageEt.getText().toString().trim();
        if(name == null || "".equals(name)) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(age == null || "".equals(age)) {
            Toast.makeText(this, "年龄不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        People people = new People(name, Integer.parseInt(age));
        long row = DatabaseManager.getInstance().addPeople(people);
        if(row > dataList.size()) {
            /*dataList.add(people);
            adapter.notifyItemInserted(dataList.size());*/
            nameEt.setText("");
            ageEt.setText("");
        }else {
            Toast.makeText(this, "新增数据失败", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "Queries: " + queries.get());
    }

    private void search() {
        String name = nameEt.getText().toString().trim();
        if(name == null || "".equals(name)) {
            DatabaseManager.getInstance().queryAll().subscribe(getSubscriber());
        }else {
            DatabaseManager.getInstance().queryPersonByName(name).subscribe(getSubscriber());
        }
    }

    private Subscriber<List<People>> getSubscriber() {
        return new Subscriber<List<People>>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError");
                e.printStackTrace();
            }

            @Override
            public void onNext(List<People> peoples) {
                dataList.clear();
                dataList.addAll(peoples);
                adapter.notifyDataSetChanged();
                queries.getAndIncrement();
            }
        };
    }
}
