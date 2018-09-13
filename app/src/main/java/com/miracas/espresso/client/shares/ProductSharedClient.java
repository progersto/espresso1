package com.miracas.espresso.client.shares;


import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.design.IOnRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductSharedClient extends ARailsClient{

    private String shareID;
    private String productID;

    public ProductSharedClient(IOnRequestCompleted fragment, String shareID, String productID) {
        super(fragment);
        this.shareID = shareID;
        this.productID = productID;
    }

    @Override
    protected void onRequestComplete(String object) {

    }

    @Override
    protected String getBody() {
        JSONObject packet = new JSONObject();
        JSONObject social = new JSONObject();
        try {
            social.put("activity", "share");
            social.put("sharable", "product");
            social.put("sharable_id", productID);
            social.put("share_id", shareID);
            packet.put("social", social);
        } catch (JSONException ignored) {}
        return packet.toString();
    }

    @Override
    protected String getMethod() {
        return ARestClient.POST;
    }

    @Override
    protected String getEndpoint() {
        return "/socials/share";
    }
}
