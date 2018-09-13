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

public class RestGenerateOTP implements Response.Listener, Response.ErrorListener {
    public static final String REST_GENERATE_OTP = "REST_GENERATE_OTP";
    RestGenerateOTPInterface listener;

    public RestGenerateOTP(RestGenerateOTPInterface l) {
        listener = l;
    }

    public void generateOTP(String number) {

        String url= ENDPOINT_RAILS+"/auth/otp?mobile_number="+number;
//        String url= ENDPOINT_RAILS+"/auth/otp?mobile_number="+"7838882502";

        HashMap<String, String> mRequestParams = new HashMap<String, String>();
        final JSONObject jsonObject = new JSONObject(mRequestParams);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, jsonObject, this, this);

        Application.getInstance().cancelPendingRequest(REST_GENERATE_OTP);
        Application.getInstance().addToRequestQueue(jsonObjectRequest, REST_GENERATE_OTP);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("response", "err: "+ error.toString());
        listener.onOTPerror(error);
    }

    @Override
    public void onResponse(Object response) {

        Log.e("response", "success: "+ response.toString());
        JSONObject jsonObject = (JSONObject) response;
        listener.onOTPgenerate(jsonObject);

    }
}