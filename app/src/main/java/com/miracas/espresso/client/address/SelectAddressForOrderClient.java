package com.miracas.espresso.client.address;


import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

public class SelectAddressForOrderClient extends ARailsClient {

    private static final int code = ClientCodes.SELECT_ADDRESS_FOR_ORDER;
    private String addressID;

    public SelectAddressForOrderClient(IOnRequestCompleted fragment, String addressID) {
        super(fragment);
        this.addressID = addressID;
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
        return ARestClient.GET;
    }

    @Override
    protected String getEndpoint() {
        return "/orders/set_address?address_id=" + addressID;
    }
}
