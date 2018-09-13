package com.miracas.espresso.client.address;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.Address;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.util.List;

public class GetAddressesClient extends ARailsClient {

    public static final int code = ClientCodes.GET_ADDRESSES;

    public GetAddressesClient(IOnRequestCompleted fragment) {
        super(fragment);
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            Response response = new Gson().fromJson(object, Response.class);
            instance.onRequestComplete(code, response);
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
        return "/addresses";
    }

    public class Response {
        public List<Address> addresses;
    }

}
