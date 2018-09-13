package com.miracas.espresso.client;

import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.util.List;


public class GetBookedItemsClient extends ARailsClient {

    private static final int code = ClientCodes.GET_BOOKED_ITEMS;
    private String keywordId;

    public GetBookedItemsClient(IOnRequestCompleted fragment, String keywordId) {
        super(fragment);
        this.keywordId = keywordId;
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            instance.onRequestComplete(code, new Gson().fromJson(object, Response.class));
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
        return "/keywords/booked?keyword_id=" + keywordId;
    }

    public class Response {
        public List<String> products;
    }
}
