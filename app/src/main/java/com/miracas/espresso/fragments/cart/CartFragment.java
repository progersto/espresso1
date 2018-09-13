package com.miracas.espresso.fragments.cart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.adapters.CartAdapter;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.cart.GetCartClient;
import com.miracas.espresso.client.cart.RemoveWebCartItemsClient;
import com.miracas.espresso.client.responses.CartItem;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.dialogs.EditCartItemDialog;
import com.miracas.espresso.dialogs.RemoveWebProductsDialog;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CartFragment extends BaseFragment {

    private List<CartItem> cartItems;
    private CartAdapter adapter;
    private String facebookId;
    private String cartTotalValue;
    private String orderID;
    private boolean webProductsRemoved = false;
    public boolean proceedAutomatically;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        onCreateToolbar(view, "Cart", false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cartItems = new ArrayList<>();

        initButtons(view);

        adapter = new CartAdapter(this, cartItems);
        RecyclerView listView = view.findViewById(R.id.products_list);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedStorage storage = new SharedStorage(getActivity());
        facebookId = storage.getValue(getActivity().getString(R.string.shared_storage_fb_user_id));
        GetCartClient client = new GetCartClient(this, facebookId);
        client.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView() ,"Cart", false);
    }

    private void initButtons(View view) {
        Button continueButton = view.findViewById(R.id.btn_continue);
        continueButton.setOnClickListener(view1 -> verifyCartAndProceed());
    }

    private boolean webProductsPresent() {
        for (CartItem item : cartItems) {
            if (item.order_type == null || item.order_type.isEmpty() || !item.order_type.equals("espresso")) {
                return true;
            }
        }
        return false;
    }

    public void removeWebProducts() {
        showProgress(true);
        RemoveWebCartItemsClient client = new RemoveWebCartItemsClient(this);
        client.execute();
    }

    private void showDialog() {
        DialogFragment fragment = RemoveWebProductsDialog.newInstance();
        fragment.setTargetFragment(this, ClientCodes.REMOVE_WEB_PRODUCTS_DIALOG);
        fragment.show(getFragmentManager(), "dialog");
    }

    private void verifyCartAndProceed() {
        if (webProductsPresent() && !webProductsRemoved) {
            showDialog();
            return;
        }
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            Fragment fragment = new AddressFragment();
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
        if (requestCode == ClientCodes.EDIT_CART_ITEM_DIALOG && resultCode == Activity.RESULT_OK) {
            showProgress(true);
            GetCartClient client = new GetCartClient(this, facebookId);
            client.execute();

        } else if (requestCode == ClientCodes.DELETE_CART_ITEM && resultCode == Activity.RESULT_OK) {
            showProgress(true);
            GetCartClient client = new GetCartClient(this, facebookId);
            client.execute();

        } else if (requestCode == ClientCodes.REMOVE_WEB_PRODUCTS_DIALOG && resultCode == Activity.RESULT_OK) {
            proceedAutomatically = true;
            removeWebProducts();
        }
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        if (clientCode == ClientCodes.GET_CART && response != null) {
            cartItems.clear();
            List<CartItem> items = (List<CartItem>) response;

            View view = getView();
            if (view != null) {
                if (!items.isEmpty()) {
                    SharedStorageHelper helper = new SharedStorageHelper(getActivity());
                    helper.setUserVisitedNonEmptyCart();
                    cartItems.addAll(items);
                    view.findViewById(R.id.bottomNav).setVisibility(View.VISIBLE);

                } else {
                    view.findViewById(R.id.emptyMessage).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.bottomNav).setVisibility(View.INVISIBLE);
                }

                adapter.calculateWebOrderPosition();
                adapter.notifyDataSetChanged();

                double totalCartPrice = 0F;

                for (CartItem item : items) {
                    if (item.order_type != null && !item.order_type.isEmpty()
                            && item.order_type.equals("espresso")) {
                        NumberFormat nf = NumberFormat.getInstance(Locale.US);
                        double f;
                        try {
                            f = nf.parse(item.price).doubleValue();
                        } catch (ParseException e) {
                            f = 0.0;
                        }
                        totalCartPrice += f;
                    }
                }
                NumberFormat formatter = new DecimalFormat("#0.0");
                cartTotalValue = "₹" + formatter.format(totalCartPrice);

                showProgress(false);

                TextView cartTotal = view.findViewById(R.id.cartTotal);
                cartTotal.setText(cartTotalValue);
            }

        } else if (clientCode == ClientCodes.REMOVE_WEB_CART_ITEMS && response != null) {
            webProductsRemoved = true;
            if (proceedAutomatically)
                verifyCartAndProceed();
            GetCartClient client = new GetCartClient(this, facebookId);
            client.execute();
        }
    }
}
