package com.miracas.espresso.adapters;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miracas.R;
import com.miracas.espresso.client.responses.Order;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.ViewHolder>  {

    private Fragment fragment;
    private List<Order.Product> products;

    public OrderProductsAdapter(Fragment fragment, List<Order.Product> products) {
        super();
        this.fragment = fragment;
        this.products = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        View view = inflater.inflate(R.layout.list_item_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Order.Product item = products.get(position);

        if (item.img_url != null && !item.img_url.isEmpty())
            try {
                Picasso.with(fragment.getContext()).load(item.img_url).into(holder.imageView);
            } catch (Exception ignored) {}

        //String priceValue = "₹ " + String.valueOf(item.price);

        holder.textView.setText(item.product_name);
        //holder.price.setText(priceValue);

        if (item.size != null && !item.size.isEmpty() && !item.size.equals("NA")) {
            holder.size.setVisibility(View.VISIBLE);
            String sizeValue = item.size;
            holder.size.setText(sizeValue);
        } else {
            holder.size.setVisibility(View.GONE);
        }

        if (item.color != null && !item.color.isEmpty() && !item.color.equals("NA")) {
            holder.color.setVisibility(View.VISIBLE);
            GradientDrawable bgShape = (GradientDrawable) holder.color.getBackground();
            try {
                bgShape.setColor(Color.parseColor(item.color));
            } catch (IllegalArgumentException ignored) {}

        } else {
            holder.color.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        TextView size;
        //TextView price;
        ImageView color;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.item_product_image);
            textView = view.findViewById(R.id.item_product);
            size = view.findViewById(R.id.size);
            //price = view.findViewById(R.id.item_product_price);
            color = view.findViewById(R.id.color);
        }

    }

}
