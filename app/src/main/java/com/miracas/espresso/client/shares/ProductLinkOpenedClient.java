package com.miracas.espresso.client.shares;

import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.design.IOnRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductLinkOpenedClient extends ARailsClient {

    private String shareID;

    public ProductLinkOpenedClient(IOnRequestCompleted fragment, String shareID) {
        super(fragment);
        this.shareID = shareID;
    }

    @Override
    protected void onRequestComplete(String object) {

    }

    @Override
    protected String getBody() {
        JSONObject packet = new JSONObject();
        try {
            packet.put("share_id", shareID);
        } catch (JSONException ignored) {}
        return packet.toString();
    }

    @Override
    protected String getMethod() {
        return ARestClient.POST;
    }

    @Override
    protected String getEndpoint() {
        return "/socials/callback";
    }
}
