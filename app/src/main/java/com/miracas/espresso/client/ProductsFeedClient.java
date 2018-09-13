package com.miracas.espresso.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miracas.espresso.client.base.APHPClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.Product;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

public class ProductsFeedClient extends APHPClient {

    private static final int code = ClientCodes.PRODUCT_FEED_CLIENT;
    private int currentPage;
    //private String gender;
    private String keyword;


    public ProductsFeedClient(IOnRequestCompleted instance, String keyword, String gender, int page) {
        super(instance);
        //this.gender = gender.replace("/", "");
        try {
            this.keyword = URLEncoder.encode(keyword.replace("/", ""), "UTF-8");
        } catch (UnsupportedEncodingException ignored) {

        }
        this.currentPage = page;
    }

    @Override
    protected String getEndpoint() {
        return "/ZS/json1/get_products_espresso.php?"
                + "keyword=" + keyword + "&start=" + String.valueOf(currentPage);
    }

    @Override
    protected void onRequestComplete(String response) {
        try {
            Type type = new TypeToken<List<Product>>() {
            }.getType();
            List<Product> productList = new Gson().fromJson(response, type);
            instance.onRequestComplete(code, productList);
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
