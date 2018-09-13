package com.miracas.espresso.rest;

import com.android.volley.VolleyError;
import org.json.JSONObject;

public interface RestGetFreebiesInterface {
    void onGetFreebiesSuccess(JSONObject response);
    void onGetFreebiesError(VolleyError error);
}
