package com.miracas.espresso.rest;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.miracas.espresso.Application;
import com.miracas.espresso.activity.contacts.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.miracas.espresso.config.Settings.ENDPOINT_RAILS;

public class RestSendInvitations implements Response.Listener, Response.ErrorListener {
    public static final String REST_SEND_INVITATIONS = "REST_SEND_INVITATIONS";
    public static final String AUTHORIZATION = "Authorization";
    public static final String MOBILE_NUMBERS = "mobile_numbers";
    RestSendInvitationsInterface listener;

    public RestSendInvitations(RestSendInvitationsInterface l) {
        listener = l;
    }

    public void sendInvitations(String token,ArrayList<User> contacts) {

        JSONArray numberArray = new JSONArray();

        String url= ENDPOINT_RAILS+"/invitations";

        for(User u : contacts){
            numberArray.put(u.getPhone());
        }

        Log.d("CONTACTS_","numberString: "+numberArray.toString());

        HashMap<String, String> mRequestParams = new HashMap<String, String>();
        final JSONObject jsonObject = new JSONObject(mRequestParams);
        try {jsonObject.put(MOBILE_NUMBERS,numberArray);}catch(JSONException e){}

        Log.d("CONTACTS_","params: "+jsonObject.toString());

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                this, this)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(AUTHORIZATION,token);
                return headers;
            }
        };

        Application.getInstance().cancelPendingRequest(REST_SEND_INVITATIONS);
        Application.getInstance().addToRequestQueue(req, REST_SEND_INVITATIONS);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("response", "err: "+ error.toString());
        listener.onSendInvitationsError(error);
    }

    @Override
    public void onResponse(Object response) {
        Log.e("response", "success: "+ response.toString());
        JSONObject jsonObject = (JSONObject) response;
        listener.onSendInvitationsSuccess(jsonObject);

    }
}