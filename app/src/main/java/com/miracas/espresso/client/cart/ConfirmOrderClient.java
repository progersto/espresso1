package com.miracas.espresso.client.cart;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmOrderClient extends ARailsClient {

    public static final int code = ClientCodes.CONFIRM_ORDER;
    private String amount;

    public ConfirmOrderClient(IOnRequestCompleted fragment, String amount) {
        super(fragment);
        this.amount = amount.replace("₹", "");
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
        JSONObject packet = new JSONObject();
        try {
            packet.put("amount", amount);
            packet.put("payment_method", "COD");
        } catch (JSONException ignored) {}
        return packet.toString();
    }

    @Override
    protected String getMethod() {
        return ARestClient.POST;
    }

    @Override
    protected String getEndpoint() {
        return "/orders/confirm";
    }

    public class Response {
        public String message;
    }

}
