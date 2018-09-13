package com.miracas.espresso.fragments.orders;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miracas.R;
import com.miracas.espresso.adapters.OrderAdapter;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.cart.GetOrdersClient;
import com.miracas.espresso.client.responses.Order;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.utils.SharedStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ListOrdersFragment extends BaseFragment {

    private List<Order> orders;
    private OrderAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_list_orders, container, false);
        onCreateToolbar(view, "My Orders", false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initOrders();
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView(), "My Orders", false);
    }

    private void initOrders() {
        View view = getView();
        if (view != null) {
            orders = new ArrayList<>();

            // Dummy order for warning message
            orders.add(new Order());

            adapter = new OrderAdapter(this, orders);
            RecyclerView listView = view.findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setLayoutManager(new LinearLayoutManager(getContext()));

            SharedStorage storage = new SharedStorage(getActivity());
            String facebookID = storage.getValue(getActivity().getString(R.string.shared_storage_fb_user_id));
            GetOrdersClient client = new GetOrdersClient(this, facebookID);
            client.execute();
        }
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        if (clientCode == ClientCodes.GET_ORDERS) {
            if (response != null) {
                List<Order> orderResponse = (List<Order>) response;
                if (!orderResponse.isEmpty()) {
                    for (Iterator<Order> iterator = orderResponse.iterator(); iterator.hasNext();) {
                        Order order = iterator.next();
                        if (order.products == null || order.products.isEmpty()) {
                            iterator.remove();
                        } else {
                            for (Order.Product product : order.products) {
                                if (product.order_type == null || product.order_type.isEmpty() ||
                                        !product.order_type.equals("espresso")) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                    }
                    orders.addAll(orderResponse);
                    adapter.notifyDataSetChanged();
                }
            }
            View view = getView();
            if (view != null)
                view.findViewById(R.id.loading).setVisibility(View.GONE);

        }
    }
}
