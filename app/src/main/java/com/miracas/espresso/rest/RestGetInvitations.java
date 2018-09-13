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

public class RestGetInvitations implements Response.Listener, Response.ErrorListener {
    public static final String REST_GET_INVITATIONS = "REST_GET_INVITATIONS";
    RestGetInvitationsInterface listener;

    public RestGetInvitations(RestGetInvitationsInterface l) {
        listener = l;
    }

    public void getInvitations(String number) {

        String url= ENDPOINT_RAILS+"/invitations?mobile_number="+number+"&type=for_number";
        HashMap<String, String> mRequestParams = new HashMap<String, String>();
        final JSONObject jsonObject = new JSONObject(mRequestParams);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, jsonObject, this, this);

        Application.getInstance().cancelPendingRequest(REST_GET_INVITATIONS);
        Application.getInstance().addToRequestQueue(jsonObjectRequest, REST_GET_INVITATIONS);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("response", "err: "+ error.toString());
        listener.onGetInvitationsError(error);
    }

    @Override
    public void onResponse(Object response) {

        Log.e("response", "success: "+ response.toString());
        JSONObject jsonObject = (JSONObject) response;
        listener.onGetInvitationsSuccess(jsonObject);

    }
}