package com.miracas.espresso.adapters;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miracas.R;
import com.miracas.espresso.client.responses.Order;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Fragment fragment;
    private List<Order> orders;

    public OrderAdapter(Fragment fragment, List<Order> values) {
        super();
        this.fragment = fragment;
        this.orders = values;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        View view = inflater.inflate(R.layout.list_item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Order order = orders.get(position);

        if (position == 0) {
            holder.messageHeader.setVisibility(View.VISIBLE);
            holder.parent.setVisibility(View.GONE);
        }
        else {
            holder.messageHeader.setVisibility(View.GONE);
            holder.parent.setVisibility(View.VISIBLE);
        }

        if (position > 0) {
            String orderID = "Order ID : " + order.id_order;
            double total = 0;
            int totalProductCount = 0;

            if (order.products != null && !order.products.isEmpty()) {
                totalProductCount = order.products.size();

                for (Order.Product product : order.products) {
                    NumberFormat nf = NumberFormat.getInstance(Locale.US);
                    double f;
                    try {
                        f = nf.parse(product.price).doubleValue();
                    } catch (ParseException e) {
                        f = 0.0;
                    }
                    total += f;
                }
            }

            NumberFormat formatter = new DecimalFormat("#0.0");
            String orderTotal = "₹" + formatter.format(total);
            String totalProducts;

            if (totalProductCount == 1) {
                totalProducts = String.valueOf(totalProductCount) + " Product";
            } else {
                totalProducts = String.valueOf(totalProductCount) + " Products";
            }

            holder.orderTotal.setText(orderTotal);
            holder.orderID.setText(orderID);
            holder.totalProducts.setText(totalProducts);
            holder.orderDate.setText(order.date_order);

            OrderProductsAdapter adapter = new OrderProductsAdapter(fragment, order.products);
            holder.list.setAdapter(adapter);
            holder.list.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View parent;
        TextView orderID;
        TextView totalProducts;
        TextView orderDate;
        TextView orderTotal;
        RecyclerView list;
        View messageHeader;

        ViewHolder(View view) {
            super(view);
            parent = view.findViewById(R.id.parent);
            orderID = view.findViewById(R.id.orderID);
            totalProducts = view.findViewById(R.id.totalProducts);
            orderDate = view.findViewById(R.id.date);
            orderTotal = view.findViewById(R.id.orderTotal);
            list = view.findViewById(R.id.list);
            messageHeader = view.findViewById(R.id.messageHeader);
        }

    }

}
