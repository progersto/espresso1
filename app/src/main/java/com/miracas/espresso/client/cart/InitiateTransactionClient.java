package com.miracas.espresso.client.cart;


import com.google.gson.Gson;
import com.miracas.espresso.client.base.ARailsClient;
import com.miracas.espresso.client.base.ARestClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

import org.json.JSONException;
import org.json.JSONObject;

public class InitiateTransactionClient extends ARailsClient {

    private String transactionID;
    private String userID;
    private String amount;
    private String paymentHash;

    public InitiateTransactionClient(IOnRequestCompleted fragment,
                                     String transactionID, String userID,
                                     String amount, String paymentHash) {
        super(fragment);
        this.transactionID = transactionID;
        this.userID = userID;
        this.amount = amount;
        this.paymentHash = paymentHash;
    }

    @Override
    protected void onRequestComplete(String object) {
        try {
            instance.onRequestComplete(ClientCodes.INITIATE_TRANSACTION,
                    new Gson().fromJson(object, Response.class));
        } catch (Exception ignored) {}
    }

    @Override
    protected String getBody() {
        JSONObject packet = new JSONObject();
        JSONObject transaction = new JSONObject();
        try {
            transaction.put("transaction_id", transactionID);
            transaction.put("user_id", userID);
            transaction.put("amount", amount);
            transaction.put("payment_hash", paymentHash);
            transaction.put("gateway", "payu");
            packet.put("transaction", transaction);
        } catch (JSONException ignored) {}

        return packet.toString();
    }

    @Override
    protected String getMethod() {
        return ARestClient.POST;
    }

    @Override
    protected String getEndpoint() {
        return "/transactions/init";
    }

    public class Response {
        public String message;
    }
}
