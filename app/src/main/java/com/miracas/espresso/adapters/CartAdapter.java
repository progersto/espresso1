package com.miracas.espresso.adapters;


import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.CartItem;
import com.miracas.espresso.dialogs.DeleteCartItemDialog;
import com.miracas.espresso.dialogs.EditCartItemDialog;
import com.miracas.espresso.fragments.cart.CartFragment;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.GrayscaleTransformation;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Fragment fragment;
    private List<CartItem> cartItems;
    private int webProductPosition;

    public CartAdapter(Fragment fragment, List<CartItem> values) {
        super();
        this.fragment = fragment;
        this.cartItems = values;
        Collections.sort(this.cartItems, (t1, t2) -> t1.order_type.equals("espresso") ? 1:0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        View view = inflater.inflate(R.layout.list_item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CartItem item = cartItems.get(position);

        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        double price;
        try {
            price = nf.parse(item.price).doubleValue();
        } catch (ParseException e) {
            price = 0.0;
        }

        double discount = 0;
        try {
            if (item.keyword_discount != null && !item.keyword_discount.isEmpty())
                discount = nf.parse(item.keyword_discount).doubleValue();
        } catch (ParseException e) {
            discount = 0.0;
        }

        String priceValue = "₹ " + String.valueOf(item.price);
        String priceNew = "₹" + String.valueOf(Math.round( price * 100 / (100F - discount)));

        holder.textView.setText(item.product_name);
        holder.price.setText(priceNew);
        holder.priceNew.setText(priceValue);
        holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (item.size != null && !item.size.isEmpty() && !item.size.equals("NA")) {
            holder.size.setVisibility(View.VISIBLE);
            String sizeValue = "Size : " + item.size;
            holder.size.setText(sizeValue);
        } else {
            holder.size.setVisibility(View.GONE);
        }

        if (item.color != null && !item.color.isEmpty() && !item.color.equals("NA")) {
            holder.colorRow.setVisibility(View.VISIBLE);
            GradientDrawable bgShape = (GradientDrawable) holder.color.getBackground();
            try {
                bgShape.setColor(Color.parseColor(item.color));
            } catch (IllegalArgumentException ignored) {}

        } else {
            holder.colorRow.setVisibility(View.GONE);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment editCartItemDialog = EditCartItemDialog.newInstance();
                editCartItemDialog.setTargetFragment(fragment, ClientCodes.EDIT_CART_ITEM_DIALOG);
                editCartItemDialog.show(fragment.getFragmentManager(), "dialog");
                Bundle bundle = new Bundle();
                bundle.putString("CART_ITEM", new Gson().toJson(item));
                editCartItemDialog.setArguments(bundle);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment deleteCartItemDialog = DeleteCartItemDialog.newInstance();
                deleteCartItemDialog.setTargetFragment(fragment, ClientCodes.DELETE_CART_ITEM);
                deleteCartItemDialog.show(fragment.getFragmentManager(), "dialog");
                Bundle bundle = new Bundle();
                bundle.putString("CART_ITEM", new Gson().toJson(item));
                deleteCartItemDialog.setArguments(bundle);
            }
        });

        if (item.img_url != null && !item.img_url.isEmpty()) {
            if (item.order_type != null && item.order_type.equals("espresso"))
                try {
                    Picasso.with(fragment.getContext()).load(item.img_url).into(holder.imageView);
                } catch (Exception ignored) {}
            else
                try {
                    Picasso.with(fragment.getContext()).load(item.img_url)
                            .transform(new GrayscaleTransformation()).into(holder.imageView);
                } catch (Exception ignored) {}
        }

        if (item.order_type != null && item.order_type.equals("espresso")) {
            holder.masterLayout.setPadding(10, 10, 10, 10);
            holder.header.setVisibility(View.GONE);
            holder.parent.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.orderType.setText("ESPRESSO");
            holder.buttons.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            holder.warning.setVisibility(View.GONE);
            holder.priceLayout.setVisibility(View.VISIBLE);

        } else {
            if (position == webProductPosition) {
                holder.masterLayout.setPadding(10, 10, 10, 0);
                holder.header.setVisibility(View.VISIBLE);

            } else {
                holder.masterLayout.setPadding(10, 1, 10, 0);
                holder.header.setVisibility(View.GONE);
            }

            holder.orderType.setAlpha(0.3F);
            holder.parent.setBackgroundColor(Color.parseColor("#f7f7f7"));
            holder.orderType.setText("ADDED FROM WEB");
            holder.buttons.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
            holder.warning.setVisibility(View.VISIBLE);
            holder.priceLayout.setVisibility(View.GONE);
        }
    }

    public void calculateWebOrderPosition() {
        webProductPosition = -1;
        for (CartItem item : cartItems) {
            if (item.order_type == null || item.order_type.isEmpty() || !item.order_type.equals("espresso")) {
                webProductPosition = cartItems.indexOf(item);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View masterLayout;
        View header;

        LinearLayout parent;
        ImageView imageView;
        TextView textView;
        TextView size;
        TextView price;
        TextView priceNew;
        RelativeLayout edit;
        RelativeLayout delete;
        ImageView color;
        LinearLayout colorRow;
        TextView orderType;

        View divider;
        View buttons;
        View warning;
        View priceLayout;

        ViewHolder(View view) {
            super(view);

            masterLayout = view.findViewById(R.id.masterLayout);
            header = view.findViewById(R.id.header);

            parent = view.findViewById(R.id.parent);
            imageView = view.findViewById(R.id.item_product_image);
            textView = view.findViewById(R.id.item_product);
            size = view.findViewById(R.id.item_product_size);
            price = view.findViewById(R.id.item_product_price);
            priceNew = view.findViewById(R.id.item_product_price_new);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
            color = view.findViewById(R.id.color);
            colorRow = view.findViewById(R.id.colorRow);
            orderType = view.findViewById(R.id.orderType);

            divider = view.findViewById(R.id.divider);
            buttons = view.findViewById(R.id.buttons);
            warning = view.findViewById(R.id.warning);
            priceLayout = view.findViewById(R.id.priceLayout);
        }

    }

}
