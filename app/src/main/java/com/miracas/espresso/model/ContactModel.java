package com.miracas.espresso.model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactModel {

    public static final String URL = "url";
    public static final String DISPLAY_PIC = "display_pic";
    public static final String DATA = "data";
    public static final String GCMID = "gcmid";
    public static final String GENDER = "gender";
    public static final String EMAIL = "email";
    public static final String FACEBOOK_ID = "facebook_id";
    public static final String ID = "id";
    public static final String CONTACTS = "contacts";
    public static final String FOLLOWERS = "followers";
    public static final String USER_NETWORK = "user_network";
    public static final String NAME = "name";
    public static final String MOBILE_NUMBER = "mobile_number";

    String name, mobileNumber,id, facebookId, email, gender, gcmid, url;

    public static ContactModel initContactModel(JSONObject object){
        ContactModel contactModel = new ContactModel();

        try {
            contactModel.setEmail(object.getString(EMAIL));
            contactModel.setFacebookId(object.getString(FACEBOOK_ID));
            contactModel.setGcmid(object.getString(GCMID));
            contactModel.setGender(object.getString(GENDER));
            contactModel.setId(object.getString(ID));
            contactModel.setMobileNumber(object.getString(MOBILE_NUMBER));
            contactModel.setName(object.getString(NAME));

            contactModel.setUrl(object.getJSONObject(DISPLAY_PIC).getJSONObject(DATA)
                    .getString(URL));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("USER_NETWORK","catch2: "+e.toString());
            return null;
        }

        return contactModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGcmid() {
        return gcmid;
    }

    public void setGcmid(String gcmid) {
        this.gcmid = gcmid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
