package com.miracas.espresso.rest;

import com.android.volley.VolleyError;
import com.miracas.espresso.client.responses.FacebookUser;

import org.json.JSONObject;

public interface RestFinishLoginWithFbInterface {
    void onLoginSuccess(JSONObject response);
    void onLoginError(VolleyError error);
}
