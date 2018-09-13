package com.miracas.espresso.rest;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.miracas.espresso.Application;
import org.json.JSONObject;
import java.util.HashMap;

import static com.miracas.espresso.config.Settings.ENDPOINT_RAILS;

public class RestAuthWithOTP implements Response.Listener, Response.ErrorListener {
    public static final String REST_AUTH = "REST_AUTH";
    RestAuthWithOTPInterface listener;

    public RestAuthWithOTP(RestAuthWithOTPInterface l) {
        listener = l;
    }

    public void auth(String number, String otp) {

        String url= ENDPOINT_RAILS+"/auth/token?grant_type=mobile_number&otp="+ otp + "&mobile_number="+number;

        HashMap<String, String> mRequestParams = new HashMap<String, String>();
        mRequestParams.put("mobile_number",number);
        mRequestParams.put("otp",otp);
        mRequestParams.put("grantType","mobile_number");

        final JSONObject jsonObject = new JSONObject(mRequestParams);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject, this, this);

        Application.getInstance().cancelPendingRequest(REST_AUTH);
        Application.getInstance().addToRequestQueue(jsonObjectRequest, REST_AUTH);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("response", "err: "+ error.toString());
        listener.onAuthError(error);
    }

    @Override
    public void onResponse(Object response) {

        Log.e("response", "success: "+ response.toString());
        JSONObject jsonObject = (JSONObject) response;
        listener.onAuthSuccess(jsonObject);

    }
}