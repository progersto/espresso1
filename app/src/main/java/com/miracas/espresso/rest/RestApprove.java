package com.miracas.espresso.rest;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.miracas.espresso.Application;
import com.miracas.espresso.model.FacebookModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.miracas.espresso.config.Settings.ENDPOINT_RAILS;

public class RestApprove implements Response.Listener, Response.ErrorListener {

    public static final String REST_APPROVE_ALL = "REST_APPROVE_ALL";
    public static final String AUTHORIZATION = "Authorization";

    public static final String MESSAGE = "message";
    public static final String CONNECTED_LIST = "connected_list";
    public static final String ID = "id";
    public static final String ACCEPTED = "accepted";
    public static final String OK = "ok";

    public static final int APPPROVE_ALL = -1;
    static int position = -1;
    static FacebookModel fbm = new FacebookModel();
    RestApproveInterface listener;

    public RestApprove(RestApproveInterface l) {
        listener = l;
    }

    public void approveAll(String token) {
        sendRequest(ENDPOINT_RAILS+"/invitations/accept?type=all",token,-1);
        this.position = -1;
    }

    public void approveOne(String token, FacebookModel facebookModel,int position){
        sendRequest(ENDPOINT_RAILS+"/invitations/accept?type=one&invitation_id="+facebookModel.getInvitationId(),
                token, position);
        this.position = position;
        this.fbm = facebookModel;
    }

    public void sendRequest(String url,String token, int position){
        HashMap<String, String> userParams = new HashMap<String, String>();
        userParams.put("gcmid", FirebaseInstanceId.getInstance().getToken());
        JSONObject jsonUser = new JSONObject(userParams);
        HashMap<String, String> mRequestParams = new HashMap<String, String>();
        final JSONObject jsonObject = new JSONObject(mRequestParams);
        try {jsonObject.put("user",jsonUser);} catch (JSONException e) {e.printStackTrace();}

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, jsonObject,
                this, this)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(AUTHORIZATION,token);
                Log.d("JWT HEADERS",headers.toString());
                return headers;
            }
        };

        Application.getInstance().cancelPendingRequest(REST_APPROVE_ALL);
        Application.getInstance().addToRequestQueue(req, REST_APPROVE_ALL);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("response", "err: "+ error.toString());
        if(position > APPPROVE_ALL) {
            listener.onApproveOneError(error,position,fbm);
        }else{
            listener.onApproveAllError(error);
        }
    }

    @Override
    public void onResponse(Object response) {

        Log.e("response", "success: "+ response.toString());
        JSONObject jsonObject = (JSONObject) response;

        if(position > APPPROVE_ALL){
            listener.onApproveOneSuccess(jsonObject,position,fbm);
        }else{
            listener.onApproveAllSuccess(jsonObject);

        }
    }
}