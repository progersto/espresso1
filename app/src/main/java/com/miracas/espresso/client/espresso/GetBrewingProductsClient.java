package com.miracas.espresso.client.espresso;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miracas.espresso.client.base.APHPClient;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.BrewedItem;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.lang.reflect.Type;
import java.util.List;

public class GetBrewingProductsClient extends APHPClient {

    private static final int code = ClientCodes.GET_BREWING_ITEMS;
    private String user_id;

    public GetBrewingProductsClient(IOnRequestCompleted activity, String user_id) {
        super(activity);
        this.user_id = user_id;
    }

    @Override
    protected String getEndpoint() {
        return "/ZS/json1/get_brewing_products.php?user_id=" + user_id;
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            Type type = new TypeToken<List<BrewedItem>>() {}.getType();
            instance.onRequestComplete(code, new Gson().fromJson(object, type));
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

}
