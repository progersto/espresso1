package com.miracas.espresso.rest;

import com.android.volley.VolleyError;
import org.json.JSONObject;

public interface RestGetUserNetworkInterface {
    void onGetUsserNetworkSuccess(JSONObject response);
    void onGetUserNetworkError(VolleyError error);
}
