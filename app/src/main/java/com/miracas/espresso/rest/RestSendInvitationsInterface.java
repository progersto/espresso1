package com.miracas.espresso.rest;

import com.android.volley.VolleyError;
import org.json.JSONObject;

public interface RestSendInvitationsInterface {
    void onSendInvitationsSuccess(JSONObject response);
    void onSendInvitationsError(VolleyError error);
}
