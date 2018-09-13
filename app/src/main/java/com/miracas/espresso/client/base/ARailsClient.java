package com.miracas.espresso.client.base;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;

import com.miracas.R;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.config.Settings;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.utils.SharedStorage;

import java.util.Map;

public abstract class ARailsClient extends ARestClient {

    private String facebookId;

    public ARailsClient(IOnRequestCompleted fragment) {
        super(fragment);
        Activity activity;
        if (fragment instanceof Fragment) {
            activity = ((Fragment)fragment).getActivity();
        } else {
            activity = (Activity) fragment;
        }
        SharedStorage storage = new SharedStorage(activity);
        facebookId = storage.getValue(activity.getString(R.string.shared_storage_fb_user_id));
    }

    protected abstract String getEndpoint();

    @Override
    protected String getAbsoluteURL() {
        return Settings.ENDPOINT_RAILS + getEndpoint();
    }

    @Override
    protected Map<String, String> getHeaders() {
        Map<String, String> headers = new ArrayMap<>();
        headers.put("Authorization", "Token token=\"6b9f92c471f44df4f1df0171e5e3bc57\"");
        headers.put("Content-Type", "application/json");
        headers.put("FACEBOOK-ID", facebookId);
        return headers;
    }
}
