package com.miracas.espresso.rest;

import com.android.volley.VolleyError;
import org.json.JSONObject;

public interface RestRejectInterface {
    void onRejectSuccess(JSONObject response,int position);
    void onRejectError(VolleyError error,int position);
}
