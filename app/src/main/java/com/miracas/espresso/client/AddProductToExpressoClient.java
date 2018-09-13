package com.miracas.espresso.client;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.Product;
import com.miracas.espresso.design.IOnRequestCompleted;

public class AddProductToExpressoClient extends ARailsClient {

    private static final int code = ClientCodes.ADD_PRODUCT_TO_EXPRESSO_CLIENT;

    private Product product;
    private String keyword_id;
    private boolean isFirstProduct = false;

    public AddProductToExpressoClient(IOnRequestCompleted fragment, Product product, String keyword_id) {
        super(fragment);
        this.product = product;
        this.keyword_id = keyword_id;
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            instance.onRequestComplete(code, new Gson().fromJson(object, Response.class));
        } catch (Exception ignored) {}
    }

    @Override
    protected String getBody() {
        return "";
    }

    @Override
    protected String getMethod() {
        return ARailsClient.POST;
    }

    @Override
    protected String getEndpoint() {
        return "/espresso/add?id_product_prestashop=" + product.id_product_prestashop
                + "&keyword_id=" + keyword_id
                + "&id_product=" + product.id_product
                + "&update=" + String.valueOf(isFirstProduct);
    }

    public class Response {
        public String message;
        public KeywordInfo keyword_info;
    }

    public class KeywordInfo {
        public String discount;
        public String booking_count;
        public String current_discount;
    }

}
