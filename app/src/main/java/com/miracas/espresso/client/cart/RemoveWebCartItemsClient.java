package com.miracas.espresso.client.cart;


import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

public class RemoveWebCartItemsClient extends ARailsClient {

    public RemoveWebCartItemsClient(IOnRequestCompleted fragment) {
        super(fragment);
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            instance.onRequestComplete(ClientCodes.REMOVE_WEB_CART_ITEMS, object);
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
        return "/orders/remove_web_items";
    }
}
