package com.miracas.espresso.client;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.APHPClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.util.List;

public class GetProductOptionDetailsClient extends APHPClient {

    public static final int code = ClientCodes.GET_PRODUCT_OPTIONS;
    private String id_prestashop;

    public GetProductOptionDetailsClient(IOnRequestCompleted activity, String id_prestashop_product) {
        super(activity);
        this.id_prestashop = id_prestashop_product;
    }

    @Override
    protected String getEndpoint() {
        return "/ZS/json1/get_product_data.php?id_product=" + id_prestashop;
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
        return null;
    }

    @Override
    protected String getMethod() {
        return ARestClient.GET;
    }

    public class Response {
        public List<String> colors;
        public List<String> sizes;
        public String name;
        public String price;
        public List<String> images;
    }
}
