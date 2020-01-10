package com.moci.defaultandroid.http;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * @author : Cygnusmoci
 * @create : 2020-01-08 10:44
 * @description :
 */
public class VolleyHelper {
    private static VolleyHelper mInstacne;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleyHelper(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    //    返回实例方法
    public static synchronized VolleyHelper getInstance(Context context){
        // 防止泄露
        if(mInstacne == null){
            mInstacne = new VolleyHelper(context);
        }
        return mInstacne;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            if (mCtx==null){
                return null;
            }
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> boolean addToRequestQueue(Request<T> req) {
        if (getRequestQueue()==null){
            return false;
        }
        req.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1.0f));
        getRequestQueue().add(req);
        return true;
    }

    public void clearRequestQueue(){
        if (getRequestQueue()!=null){
            getRequestQueue().cancelAll(mCtx.getApplicationContext());
        }
    }
}
