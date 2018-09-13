package com.miracas.espresso.rest;

import com.android.volley.VolleyError;
import com.miracas.espresso.model.FacebookModel;

import org.json.JSONObject;

public interface RestApproveInterface {
    void onApproveAllSuccess(JSONObject response);
    void onApproveAllError(VolleyError error);
    void onApproveOneSuccess(JSONObject response, int position, FacebookModel facebookModel);
    void onApproveOneError(VolleyError error, int position, FacebookModel facebookModel);
}
