package com.miracas.espresso.client;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;


public class GlobalsClient extends ARailsClient {

    public GlobalsClient(IOnRequestCompleted fragment) {
        super(fragment);
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            instance.onRequestComplete(ClientCodes.GLOBALS_CLIENT,
                    new Gson().fromJson(object, Response.class));
        } catch (Exception ignore) {}
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
        return "/globals";
    }

    public class Response {
        public COD cod;
        public App app;
    }

    public class COD {
        public String cod_upper_limit;
        public String cod_lower_limit;
    }

    public class App {
        public String major_version;
        public String minor_version;
    }
}
