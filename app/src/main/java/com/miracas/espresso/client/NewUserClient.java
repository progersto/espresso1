package com.miracas.espresso.client;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

public class NewUserClient extends ARailsClient {

    private static final int code = ClientCodes.NEW_USER_CLIENT;

    private String facebookId;
    private String name;
    private String email;
    private String gender;
    private String gcmId;
    private String education;
    private String location;
    private String hometown;
    private String locale;
    private String app_version_code;
    private String os_version;
    private String os_api_level;
    private String device_model;
    private String device_manufacturer;

    public NewUserClient(IOnRequestCompleted fragment,
                         String facebookId, String name, String email, String gender,
                         String gcmId, String education, String location, String hometown,
                         String locale, String app_version_code, String os_version,
                         String os_api_level, String device_model, String device_manufacturer) {
        super(fragment);
        this.facebookId = facebookId;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.gcmId = gcmId;
        this.education = education;
        this.location = location;
        this.hometown = hometown;
        this.locale = locale;
        this.app_version_code = app_version_code;
        this.os_version = os_version;
        this.os_api_level = os_api_level;
        this.device_model = device_model;
        this.device_manufacturer = device_manufacturer;
    }

    @Override
    protected String getEndpoint() {
        return "/users";
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            instance.onRequestComplete(code, new Gson().fromJson(object, Response.class));
        } catch (Exception ignored) {}
    }

    @Override
    protected String getBody() {
        try {
            JSONObject user = new JSONObject();
            user.put("email", email);
            user.put("name", name);
            user.put("gender", gender);
            user.put("gcmid", gcmId);
            user.put("facebook_id", facebookId);
            user.put("education", education);
            user.put("location", location);
            user.put("locale", locale);
            user.put("hometown", hometown);
            user.put("app_version_code", app_version_code);
            user.put("os_version", os_version);
            user.put("os_api_level", os_api_level);
            user.put("device_model", device_model);
            user.put("device_manufacturer", device_manufacturer);

            JSONObject body = new JSONObject();
            body.put("user", user);
            return body.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String getMethod() {
        return ARestClient.POST;
    }

    public class Response {
        public User user;
    }

    public class User {
        public String id;
        public String name;
        public String facebook_id;
        public String email;
        public String birthday;
        public String gender;
    }
}
