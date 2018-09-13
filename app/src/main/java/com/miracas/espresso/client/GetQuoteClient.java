package com.miracas.espresso.client;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.Product;
import com.miracas.espresso.client.responses.Quote;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.lang.reflect.Type;
import java.util.List;

public class GetQuoteClient extends ARailsClient {

    private static final int code = ClientCodes.GET_QUOTE;

    public GetQuoteClient(IOnRequestCompleted fragment) {
        super(fragment);
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            Type type = new TypeToken<List<Quote>>() {}.getType();
            instance.onRequestComplete(code, new Gson().fromJson(object, type));
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
        return "/splash";
    }

}
