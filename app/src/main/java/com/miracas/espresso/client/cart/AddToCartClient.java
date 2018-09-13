package com.miracas.espresso.client.cart;


import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

public class AddToCartClient extends ARailsClient {

    public static final int code = ClientCodes.ADD_TO_CART;

    private String color;
    private String qty;
    private String size;
    private String id_product_prestashop;
    private String id_product;
    private String amount;
    private String discount;

    public AddToCartClient(IOnRequestCompleted fragment, String color, String qty,
                           String size, String id_product_prestashop, String id_product,
                           String amount, String discount) {
        super(fragment);
        this.color = color;
        this.qty = qty;
        this.size = size;
        this.id_product_prestashop = id_product_prestashop;
        this.id_product = id_product;
        this.amount = amount;
        this.discount = discount;
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            boolean added = false;
            if (object != null) try {
                JSONObject response = new JSONObject(object);
                if (response.getString("status").equals("ORDER CREATED"))
                    added = true;
            } catch (Exception ignored) {
            }

            instance.onRequestComplete(code, added);
        } catch (Exception ignored) {}
    }

    @Override
    protected String getBody() {
        JSONObject packet = new JSONObject();
        JSONObject order_details = new JSONObject();
        try {
            order_details.put("order_type", "espresso");
            order_details.put("color", color);
            order_details.put("size", size);
            order_details.put("qty", qty);
            order_details.put("amount", amount);
            order_details.put("discount", discount);
            order_details.put("id_product_prestashop", id_product_prestashop);
            order_details.put("id_product", id_product);
            packet.put("order_details", order_details);
        } catch (JSONException ignored) {
        }
        return packet.toString();
    }

    @Override
    protected String getMethod() {
        return ARailsClient.POST;
    }

    @Override
    protected String getEndpoint() {
        return "/orders/cart";
    }
}
