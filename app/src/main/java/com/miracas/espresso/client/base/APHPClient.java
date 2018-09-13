package com.miracas.espresso.client.base;


import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.config.Settings;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.util.Map;

public abstract class APHPClient extends ARestClient {

    public APHPClient(IOnRequestCompleted activity) {
        super(activity);
    }

    protected abstract String getEndpoint();

    @Override
    protected String getAbsoluteURL() {
        return Settings.ENDPOINT_PRESTASHOP + getEndpoint();
    }

    @Override
    protected Map<String, String> getHeaders() {
        return null;
    }

}
