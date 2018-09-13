package com.miracas.espresso.rest;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface RestGenerateOTPInterface {
    void onOTPgenerate(JSONObject response);
    void onOTPerror(VolleyError error);
}
