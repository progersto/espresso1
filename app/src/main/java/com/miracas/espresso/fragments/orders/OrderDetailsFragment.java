package com.miracas.espresso.fragments.orders;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.adapters.OrderProductsAdapter;
import com.miracas.espresso.client.responses.Order;
import com.miracas.espresso.fragments.BaseFragment;


public class OrderDetailsFragment extends BaseFragment{

    private Order order;
    private OrderProductsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);
        onCreateToolbar(view, "Order Details", false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String orderString = bundle.getString("ORDER");
            order = new Gson().fromJson(orderString, Order.class);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(false);
        initList();
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView(), "Order Details", false);
    }

    private void initList() {
        View view = getView();
        if (view != null) {
            adapter = new OrderProductsAdapter(this, order.products);
            RecyclerView listView = view.findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {

    }
}
