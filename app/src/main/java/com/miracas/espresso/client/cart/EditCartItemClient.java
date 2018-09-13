package com.miracas.espresso.client.cart;


import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.CartItem;
import com.miracas.espresso.design.IOnRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

public class EditCartItemClient extends ARailsClient {

    public static final int code = ClientCodes.EDIT_CART_ITEM;

    private CartItem item;

    public EditCartItemClient(IOnRequestCompleted fragment, CartItem item) {
        super(fragment);
        this.item = item;
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
            order_details.put("color", item.color);
            order_details.put("size", item.size);
            order_details.put("qty", item.qty);
            order_details.put("amount", item.price);
            order_details.put("discount", "0");
            order_details.put("id_product_prestashop", item.id_product_prestashop);
            order_details.put("id_product", item.id_product);
            packet.put("order_detail_id", item.id_order_detail);
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
