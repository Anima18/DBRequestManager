package com.example.chris.requestmanager.entity;

/**
 * Created by Admin on 2016/10/31.
 */

public interface DataCallBack<T> {
    void onFailure(int code, String message);
    void onCompleted();
    void onSuccess(T data);
}
