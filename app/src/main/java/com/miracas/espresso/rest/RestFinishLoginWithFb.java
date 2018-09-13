package com.miracas.espresso.rest;

import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.miracas.espresso.Application;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import static com.miracas.espresso.config.Settings.ENDPOINT_RAILS;

public class RestFinishLoginWithFb implements Response.Listener, Response.ErrorListener {
    public static final String REST_FINISH_LOGIN = "REST_FINISH_LOGIN";
    public static final String AUTHORIZATION = "Authorization";
    RestFinishLoginWithFbInterface listener;

    public RestFinishLoginWithFb(RestFinishLoginWithFbInterface l) {
        listener = l;
    }

    public void finishLogin(String fbToken, String token) {

        String url= ENDPOINT_RAILS+"/auth/finish_login_with_facebook?access_token="+fbToken;

        HashMap<String, String> userParams = new HashMap<String, String>();
        userParams.put("gcmid", FirebaseInstanceId.getInstance().getToken());
        JSONObject jsonUser = new JSONObject(userParams);
        HashMap<String, String> mRequestParams = new HashMap<String, String>();
        final JSONObject jsonObject = new JSONObject(mRequestParams);
        try {jsonObject.put("user",jsonUser);} catch (JSONException e) {e.printStackTrace();}

        Log.d("FINISH_LOGIN","input params: "+jsonObject.toString());

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
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

        Application.getInstance().cancelPendingRequest(REST_FINISH_LOGIN);
        Application.getInstance().addToRequestQueue(req, REST_FINISH_LOGIN);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("response", "err: "+ error.toString());
        listener.onLoginError(error);
    }

    @Override
    public void onResponse(Object response) {

        Log.e("response", "success: "+ response.toString());
        JSONObject jsonObject = (JSONObject) response;
        listener.onLoginSuccess(jsonObject);

    }
}