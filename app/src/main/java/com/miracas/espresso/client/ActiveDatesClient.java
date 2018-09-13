package com.miracas.espresso.client;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.util.HashMap;


public class ActiveDatesClient extends ARailsClient {

    public ActiveDatesClient(IOnRequestCompleted fragment) {
        super(fragment);
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            instance.onRequestComplete(ClientCodes.ACTIVE_KEYWORDS,
                    new Gson().fromJson(object, Response.class));
        } catch (Exception ignored) {}
    }

    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected String getMethod() {
        return ARestClient.GET;
    }

    @Override
    protected String getEndpoint() {
        return "/keywords/active_dates";
    }


    public class Response {
        public HashMap<String, String> dates;
    }
}
