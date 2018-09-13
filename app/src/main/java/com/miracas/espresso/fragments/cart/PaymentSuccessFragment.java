package com.miracas.espresso.fragments.cart;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miracas.espresso.activity.MainActivity;
import com.miracas.R;
import com.miracas.espresso.fragments.BaseFragment;

public class PaymentSuccessFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view =  inflater.inflate(R.layout.fragment_payment_success, container, false);
        onCreateToolbar(view, "Order Confirmation", false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            TextView cartTotal = view.findViewById(R.id.amount);
            String cartTotalValue = bundle.getString("CART_TOTAL");
            cartTotal.setText(cartTotalValue);
        }

        view.findViewById(R.id.myOrdersButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMyOrders();
            }
        });

        return view;
    }

    private void goToMyOrders() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.switchFragment(R.id.nav_home, true,null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView(), "Order Confirmation", false);
    }


    @Override
    public void onRequestComplete(int clientCode, Object response) {

    }
}
