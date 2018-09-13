package com.miracas.espresso.fragments.cart;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.miracas.BuildConfig;
import com.miracas.R;
import com.miracas.espresso.client.cart.InitiateTransactionClient;
import com.miracas.espresso.client.responses.PayUResponse;
import com.miracas.espresso.config.Settings;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.cart.ConfirmOrderClient;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.utils.SharedStorage;
import com.miracas.espresso.utils.SharedStorageHelper;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class PaymentFragment extends BaseFragment {

    private View selectedMethod;
    private View selectedMethodParentView;
    private boolean cod_selected;
    private String cartTotalValue;

    private String key;
    private String salt;
    private String amount;
    private String productInfo;
    private String email;
    private String txnid;
    private String firstName;
    private String paymentHash;
    private String userID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view =  inflater.inflate(R.layout.fragment_payment, container, false);
        onCreateToolbar(view, "Payment", false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            TextView cartTotal = view.findViewById(R.id.cartTotal);
            cartTotalValue = bundle.getString("CART_TOTAL");
            cartTotal.setText(cartTotalValue);
        }

        view.findViewById(R.id.bottomNav).setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initButtons(view);
        showProgress(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView(), "Payment", false);
    }

    private void initButtons(View view) {
        Button continueButton = view.findViewById(R.id.btn_continue);
        continueButton.setOnClickListener(view12 -> {
            if (cod_selected) {
                initiateCOD();
            } else {
                initiateTransaction();
            }
        });

        double total = Double.valueOf(cartTotalValue.replace("₹", ""));

        SharedStorageHelper helper = new SharedStorageHelper(getActivity());
        if (total > helper.getCODUpperLimit() ||
                total < helper.getCODLowerLimit()) {
            view.findViewById(R.id.cod).setVisibility(View.GONE);
            TextView messageView = view.findViewById(R.id.codWarning);
            messageView.setVisibility(View.VISIBLE);
        }

        selectedMethod = view.findViewById(R.id.check2);

        view.findViewById(R.id.cod).setOnClickListener(view1 -> {
            uncheck();
            View selected = view1.findViewById(R.id.check1);
            view1.setBackgroundColor(Color.parseColor(Settings.SELECTED_COLOR_CODE));
            selectedMethodParentView = view1;
            selected.setVisibility(View.VISIBLE);
            selectedMethod = selected;
            cod_selected = true;
        });

        view.findViewById(R.id.online).setOnClickListener(view2 -> {
            uncheck();
            View selected = view2.findViewById(R.id.check2);
            selected.setVisibility(View.VISIBLE);
            view2.setBackgroundColor(Color.parseColor(Settings.SELECTED_COLOR_CODE));
            selectedMethod = selected;
            selectedMethodParentView = view2;
            cod_selected = false;
        });

        cod_selected = false;
        selectedMethod = view.findViewById(R.id.check2);
        selectedMethodParentView = view.findViewById(R.id.online);
        selectedMethodParentView.setBackgroundColor(Color.parseColor(Settings.SELECTED_COLOR_CODE));

    }

    private void uncheck() {
        if (selectedMethod != null) {
            selectedMethodParentView.setBackgroundColor(Color.parseColor(Settings.DEFAULT_COLOR_CODE));
            selectedMethod.setVisibility(View.INVISIBLE);
        }
    }

    private String getSHA(String str) {
        MessageDigest md;
        String out = "";
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(str.getBytes());
            byte[] mb = md.digest();
            for (int i = 0; i < mb.length; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(temp);
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                out += s;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return out;
    }

    private void initiateCOD() {
        showProgress(true);
        ConfirmOrderClient client = new ConfirmOrderClient(this, cartTotalValue);
        client.execute();
    }

    private void initiateTransaction() {
        if (getActivity() != null) {
            showProgress(true);
            key = getString(R.string.payu_key);
            salt = getString(R.string.payu_salt);

            SharedStorage storage = new SharedStorage(getActivity());
            userID = storage.getValue(getActivity().getString(R.string.shared_storage_user_id));
            email = storage.getValue(getActivity().getString(R.string.shared_storage_fb_user_email));
            firstName = storage.getValue(getActivity().getString(R.string.shared_storage_fb_user_name));

            txnid = UUID.randomUUID().toString().substring(0, 20);

            if (BuildConfig.DEBUG)
                amount = "1";
            else
                amount = cartTotalValue.replace("₹", "");

            productInfo = "Miracas Cart Checkout";

            paymentHash = getSHA(key + "|" + txnid + "|" + amount + "|"
                    + productInfo + "|" + firstName + "|" + email + "|||||||||||" + salt);

            InitiateTransactionClient client = new InitiateTransactionClient(this,
                    txnid, userID, amount, paymentHash);
            client.execute();
        }
    }

    private void initiatePayUPayment() {

        try {
            assert getActivity() != null;
            ProviderInstaller.installIfNeeded(getActivity());
        } catch (Exception ignored) {}

        String user_credentials = key + ":" + userID;

        String hash1 = getSHA(key + "|" + "payment_related_details_for_mobile_sdk" + "|" + user_credentials + "|" + salt);
        String hash2 = getSHA(key + "|" + "vas_for_mobile_sdk" + "|" + user_credentials + "|" + salt);

        PaymentParams mPaymentParams = new PaymentParams();
        mPaymentParams.setKey(key);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo(productInfo);
        mPaymentParams.setFirstName(firstName);
        mPaymentParams.setEmail(email);
        mPaymentParams.setTxnId(txnid);
        mPaymentParams.setSurl(Settings.ENDPOINT_RAILS + "/transactions/confirm");
        mPaymentParams.setFurl(Settings.ENDPOINT_RAILS + "/transactions/fail");
        mPaymentParams.setUserCredentials(user_credentials);
        mPaymentParams.setUdf1("");
        mPaymentParams.setUdf2("");
        mPaymentParams.setUdf3("");
        mPaymentParams.setUdf4("");
        mPaymentParams.setUdf5("");

        PayuHashes payuHashes = new PayuHashes();
        payuHashes.setPaymentRelatedDetailsForMobileSdkHash(hash1);
        payuHashes.setPaymentHash(paymentHash);
        payuHashes.setVasForMobileSdkHash(hash2);

        PayuConfig payuConfig = new PayuConfig();
        payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);

        Payu.setInstance(getContext());

        Intent intent = new Intent(getContext(), PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);

        showProgress(false);
    }

    private void success() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Fragment fragment = new PaymentSuccessFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
            transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
            Bundle bundle = new Bundle();
            bundle.putString("CART_TOTAL", cartTotalValue);
            fragment.setArguments(bundle);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {
                String responseString = data.getStringExtra("payu_response");
                //Log.d("PAYU Response", responseString);
                PayUResponse response = new Gson().fromJson(responseString, PayUResponse.class);
                if (response.status.equals("success")) {
                    success();

                } else {
                    if (getView() != null)
                        getView().findViewById(R.id.error).setVisibility(View.VISIBLE);

                }
                //Log.d("Server Response", data.getStringExtra("result"));
            }
            showProgress(false);
        }
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        if (clientCode == ClientCodes.CONFIRM_ORDER && response != null) {
            ConfirmOrderClient.Response confirmResponse = (ConfirmOrderClient.Response) response;
            Toast.makeText(getContext(), confirmResponse.message, Toast.LENGTH_LONG).show();
            if (confirmResponse.message.equals("order confirmed")) {
                success();
            }
            showProgress(false);

        } else if (clientCode == ClientCodes.INITIATE_TRANSACTION && response != null) {
            InitiateTransactionClient.Response transactionResponse = (InitiateTransactionClient.Response) response;
            String message = transactionResponse.message;
            if (message != null && !message.isEmpty() && message.equals("transaction initiated"))
                initiatePayUPayment();
            else {
                Toast.makeText(getContext(), "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        }
    }
}
