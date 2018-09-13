package com.miracas.espresso.client.address;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.Address;
import com.miracas.espresso.design.IOnRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

public class AddAddressClient extends ARailsClient {

    public static final int code = ClientCodes.ADD_ADDRESS;
    private Address address;

    public AddAddressClient(IOnRequestCompleted fragment, Address address) {
        super(fragment);
        this.address = address;
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            instance.onRequestComplete(code, object);
        } catch (Exception ignored) {}
    }

    @Override
    protected String getBody() {
        try {
            JSONObject body = new JSONObject();
            body.put("address", new JSONObject(new Gson().toJson(address)));
            return body.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String getMethod() {
        return ARestClient.POST;
    }

    @Override
    protected String getEndpoint() {
        return "/addresses";
    }

}
