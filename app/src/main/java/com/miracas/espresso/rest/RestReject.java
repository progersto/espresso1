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

public class RestReject implements Response.Listener, Response.ErrorListener {
    public static final String REST_REJECT = "REST_REJECT";
    public static final String AUTHORIZATION = "Authorization";
    public static final String REJECTED = "rejected";

    static int position = 0;
    RestRejectInterface listener;

    public RestReject(RestRejectInterface l) {
        listener = l;
    }

    public void reject(String token, String invitation_id, int position) {
        String url = ENDPOINT_RAILS+"/invitations/reject?invitation_id="+invitation_id;

        this.position = position;

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
                return headers;
            }
        };

        Application.getInstance().cancelPendingRequest(REST_REJECT);
        Application.getInstance().addToRequestQueue(req, REST_REJECT);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("response", "err: "+ error.toString());
        listener.onRejectError(error,position);
    }

    @Override
    public void onResponse(Object response) {
        Log.e("response", "success: "+ response.toString());
        JSONObject jsonObject = (JSONObject) response;
        listener.onRejectSuccess(jsonObject,position);
    }
}