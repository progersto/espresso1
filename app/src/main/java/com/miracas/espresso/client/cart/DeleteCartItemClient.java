package com.miracas.espresso.client.cart;


import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

public class DeleteCartItemClient extends ARailsClient {

    public static final int code = ClientCodes.DELETE_CART_ITEM;

    private String order_detail_id;

    public DeleteCartItemClient(IOnRequestCompleted fragment, String order_detail_id) {
        super(fragment);
        this.order_detail_id = order_detail_id;
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            instance.onRequestComplete(code, object);
        } catch (Exception ignored) {}
    }

    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected String getMethod() {
        return ARestClient.DELETE;
    }

    @Override
    protected String getEndpoint() {
        return "/orders/cart?order_detail_id=" + order_detail_id;
    }
}
