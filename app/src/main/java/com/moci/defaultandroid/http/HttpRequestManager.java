package com.moci.defaultandroid.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Cygnusmoci
 * @create : 2020-01-08 10:41
 * @description :
 */
public class HttpRequestManager {

    // 设置超时时间
    public final static int TIMEOUT = 10000;
    // 获取单例
    private static HttpRequestManager instance;

    public static HttpRequestManager getInstance() {
        if (instance == null) {
            instance = new HttpRequestManager();
        }
        return instance;
    }

    // 包装getBizToken的http信息
    public void postName(Context context, String url, String sign, byte[] byteArr,
                            HttpRequestCallBack listener){
        MultipartEntity entity = new MultipartEntity();
        // 添加包体
        entity.addStringPart("sign",sign);
        entity.addBinaryPart("byteArr",byteArr);
        // 发送请求
        sendMultipartRequest(context,url,entity,new HashMap<String, String>(),listener);
    }


    private void sendMultipartRequest(Context context, String url, MultipartEntity entity,
                                      final HashMap header,
                                      final HttpRequestCallBack listener){
        MultipartRequest multipartRequest = new MultipartRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error == null){
                    listener.onFailure(-1,"error == null , timeout exception".getBytes());
                }else if(error.networkResponse == null){

                    listener.onFailure(-1,"error.networkResponse == null , timeout exception".getBytes());
                }else {
                    listener.onFailure(error.networkResponse.statusCode,error.networkResponse.data);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return header;
            }
        };

        // 通过MultipartEntity来设置参数
        multipartRequest.setmMultiPartEntity(entity);
        VolleyHelper.getInstance(context).addToRequestQueue(multipartRequest);
    }
}
