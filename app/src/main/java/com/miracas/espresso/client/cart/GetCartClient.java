package com.miracas.espresso.client.cart;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miracas.espresso.client.base.APHPClient;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.CartItem;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.lang.reflect.Type;
import java.util.List;

public class GetCartClient extends APHPClient {

    public static final int code = ClientCodes.GET_CART;

    private String fbId;

    public GetCartClient(IOnRequestCompleted fragment, String fbId) {
        super(fragment);
        this.fbId = fbId; // "694133180";
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            Type type = new TypeToken<List<CartItem>>() {
            }.getType();
            List<CartItem> response = new Gson().fromJson(object, type);
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
        return "/ZS/json1/get_cart_from_user.php?fb_user=" + fbId;
    }

}
