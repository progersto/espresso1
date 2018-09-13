package com.miracas.espresso.client.address;


import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.Address;
import com.miracas.espresso.design.IOnRequestCompleted;

public class DeleteAddressClient extends ARailsClient {

    public static final int code = ClientCodes.DELETE_ADDRESS;
    private Address address;

    public DeleteAddressClient(IOnRequestCompleted fragment, Address address) {
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
        return null;
    }

    @Override
    protected String getMethod() {
        return ARestClient.DELETE;
    }

    @Override
    protected String getEndpoint() {
        return "/addresses?address_id=" + address.id_address;
    }


}
