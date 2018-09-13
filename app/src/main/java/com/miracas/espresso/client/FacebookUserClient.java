package com.miracas.espresso.client;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.FacebookUser;
import com.miracas.espresso.design.IOnRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FacebookUserClient extends ARestClient {

    private static final int code = ClientCodes.FACEBOOK_USER_CLIENT;
    private String token;

    public FacebookUserClient(IOnRequestCompleted activity, String token) {
        super(activity);
        this.token = token;
    }

    @Override
    protected String getAbsoluteURL() {
        return "https://graph.facebook.com/me?" +
                "fields=id,name,picture,email,gender,age_range,birthday," +
                "location,education,hometown,locale" +
                "&access_token=" + token;
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            FacebookUser user = new Gson().fromJson(object, FacebookUser.class);
            try {
                JSONObject jsonResponse = new JSONObject(object);
                user.profile_picture = jsonResponse.getJSONObject("picture")
                        .getJSONObject("data").getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            instance.onRequestComplete(code, user);
        } catch (Exception ignored) {}
    }

    @Override
    protected Map<String, String> getHeaders() {
        return null;
    }

    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected String getMethod() {
        return ARestClient.GET;
    }
}