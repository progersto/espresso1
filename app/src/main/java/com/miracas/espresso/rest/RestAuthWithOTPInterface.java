package com.miracas.espresso.rest;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface RestAuthWithOTPInterface {
    void onAuthSuccess(JSONObject response);
    void onAuthError(VolleyError error);
}
