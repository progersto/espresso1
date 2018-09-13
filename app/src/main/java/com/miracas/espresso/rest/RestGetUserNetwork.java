package com.miracas.espresso.rest;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.miracas.espresso.Application;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import static com.miracas.espresso.config.Settings.ENDPOINT_RAILS;

public class RestGetUserNetwork implements Response.Listener, Response.ErrorListener {
    public static final String REST_GET_USER_NETWORK = "REST_GET_USER_NETWORK";
    public static final String AUTHORIZATION = "Authorization";
    RestGetUserNetworkInterface listener;

    public RestGetUserNetwork(RestGetUserNetworkInterface l) {
        listener = l;
    }

    public void getUserNetwork(String token) {

        String url= ENDPOINT_RAILS+"/user/network";

        HashMap<String, String> mRequestParams = new HashMap<String, String>();
        final JSONObject jsonObject = new JSONObject(mRequestParams);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, jsonObject,
                this, this)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(AUTHORIZATION,token);
                Log.d("JWT HEADERS",headers.toString());
                return headers;
            }
        };

        Application.getInstance().cancelPendingRequest(REST_GET_USER_NETWORK);
        Application.getInstance().addToRequestQueue(req, REST_GET_USER_NETWORK);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("response", "err: "+ error.toString());
        listener.onGetUserNetworkError(error);
    }

    @Override
    public void onResponse(Object response) {

        Log.e("response", "success: "+ response.toString());
        JSONObject jsonObject = (JSONObject) response;
        listener.onGetUsserNetworkSuccess(jsonObject);

    }
}