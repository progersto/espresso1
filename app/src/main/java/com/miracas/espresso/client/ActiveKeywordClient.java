package com.miracas.espresso.client;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.ActiveKeyword;
import com.miracas.espresso.client.responses.Quote;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActiveKeywordClient extends ARailsClient {

    private static final int code = ClientCodes.GET_ACTIVE_KEYWORD;

    public ActiveKeywordClient(IOnRequestCompleted fragment) {
        super(fragment);
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            Response response = null;
            if (object != null && !object.isEmpty()) {
                response = new Gson().fromJson(object, Response.class);
            }
            instance.onRequestComplete(code, response);
        } catch (Exception ignored) {}
    }

    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected String getMethod() {
        return ARailsClient.GET;
    }

    @Override
    protected String getEndpoint() {
        return "/keywords/active";
    }

    public class Response {
        public List<ActiveKeyword> keywords;
        public List<Quote> quotes;
        public List<ActiveKeyword> keywords_last_three_days;
    }

}
