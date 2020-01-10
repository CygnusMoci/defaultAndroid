package com.moci.defaultandroid.http;

/**
 * @author : Cygnusmoci
 * @create : 2020-01-08 10:40
 * @description :
 */
public interface HttpRequestCallBack {
    void onSuccess(String responseBody);
    void onFailure(int statusCode, byte[] responseBody);
}
