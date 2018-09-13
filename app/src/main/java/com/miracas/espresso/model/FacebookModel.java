package com.miracas.espresso.model;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookModel {
    public static final String INVITATIONS = "invitations";
    public static final String ID = "id";
    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String USER = "user";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String DISPLAY_PIC = "display_pic";
    public static final String DATA = "data";


    String id,phone,name,url, invitationId;
    boolean check;

    public static FacebookModel initFbModel(JSONObject json){
        FacebookModel facebookModel = new FacebookModel();

        try {
            JSONObject userJson = json.getJSONObject(USER);

            facebookModel.setInvitationId(json.getString(ID));
            facebookModel.setPhone(json.getString(MOBILE_NUMBER));
            facebookModel.setName(userJson.getString(NAME));
            facebookModel.setId(userJson.getString(ID));
            facebookModel.setUrl(userJson.getJSONObject(DISPLAY_PIC).getJSONObject(DATA).getString(URL));

            return facebookModel;
        } catch (JSONException e) {
            Log.d("INVITATIONS","fbModel init catch: "+e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }
}
