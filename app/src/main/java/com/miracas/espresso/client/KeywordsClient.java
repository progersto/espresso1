package com.miracas.espresso.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.KeywordResponse;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.lang.reflect.Type;

public class KeywordsClient extends ARailsClient {

    private static final int code = ClientCodes.KEYWORDS_CLIENT;

    public KeywordsClient(IOnRequestCompleted activity) {
        super(activity);
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            Type type = new TypeToken<KeywordResponse>() {
            }.getType();
            KeywordResponse keywordResponse = new Gson().fromJson(object, type);
            instance.onRequestComplete(code, keywordResponse);
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
        return "/keywords";
    }
}
