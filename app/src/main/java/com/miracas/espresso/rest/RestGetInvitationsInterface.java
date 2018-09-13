package com.miracas.espresso.rest;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface RestGetInvitationsInterface {
    void onGetInvitationsSuccess(JSONObject response);
    void onGetInvitationsError(VolleyError error);
}
