package com.miracas.espresso.client;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.ActiveKeyword;
import com.miracas.espresso.client.responses.Quote;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.utils.CalendarHelper;

import java.util.Date;
import java.util.List;

public class DailyKeywordClient extends ARailsClient {

    private Date date;

    public DailyKeywordClient(IOnRequestCompleted fragment, Date date) {
        super(fragment);
        this.date = CalendarHelper.addDays(date, 1);
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            Response response = new Gson().fromJson(object, Response.class);
            instance.onRequestComplete(ClientCodes.DAILY_KEYWORDS, response);
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
        return "/keywords/by_date?epoch=" + String.valueOf(date.getTime()/1000);
    }

    public class Response {
        public List<ActiveKeyword> keywords;
    }
}
