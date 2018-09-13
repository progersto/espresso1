package com.miracas.espresso;

import android.support.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Application extends MultiDexApplication{

    private RequestQueue mRequestQueue;
    private static Application mInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }


    public static synchronized Application getInstance(){
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(tag);
        getmRequestQueue().add(req);
    }

    public void cancelPendingRequest(String req){
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(req);
        }
    }

    public RequestQueue getmRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

}
