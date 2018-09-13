package com.miracas.espresso.client.cart;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miracas.espresso.client.base.APHPClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.Order;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.lang.reflect.Type;
import java.util.List;

public class GetOrdersClient extends APHPClient {

    private static final int code = ClientCodes.GET_ORDERS;
    private String facebookId;

    public GetOrdersClient(IOnRequestCompleted activity, String facebookId) {
        super(activity);
        this.facebookId = facebookId; // "694133180";
    }

    @Override
    protected String getEndpoint() {
        return "/ZS/json1/get_orders_from_user.php?fb_user=" + facebookId;
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            Type type = new TypeToken<List<Order>>() {
            }.getType();
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


}
