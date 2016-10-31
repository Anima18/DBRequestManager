package com.example.chris.requestmanager.entity;

import java.util.List;

/**
 * Created by Admin on 2016/10/31.
 */

public interface CollectionCallBack<T> {
    void onFailure(int code, String message);
    void onCompleted();
    void onSuccess(List<T> datas);
}
