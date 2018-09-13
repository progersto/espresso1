package com.miracas.espresso.adapters;


import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miracas.R;
import com.miracas.espresso.client.responses.BrewedItem;
import com.miracas.espresso.config.Settings;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class BrewedKeywordProductAdapter extends RecyclerView.Adapter<BrewedKeywordProductAdapter.ViewHolder> {

    private Fragment fragment;
    private List<BrewedItem> items;
    private boolean circularScroll;

    BrewedKeywordProductAdapter(Fragment fragment, List<BrewedItem> items, boolean circularScroll) {
        this.fragment = fragment;
        this.items = items;
        this.circularScroll = circularScroll;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (fragment != null) {
            LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
            View view = inflater.inflate(R.layout.list_item_brewed_keyword_product, parent, false);
            return new ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder != null) {
            position = position % items.size();
            String image = items.get(position).product.img_url;
            BrewedItem item = items.get(position);

            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            double price;
            try {
                price = nf.parse(item.product.price).doubleValue();
            } catch (ParseException e) {
                price = 0.0;
            }

            double discount = 0;
            try {
                if (item.product.discount != null && !item.product.discount.isEmpty())
                    discount = nf.parse(item.product.discount).doubleValue();
            } catch (ParseException e) {
                discount = 0.0;
            }

            String priceValue = "₹ " + String.valueOf(item.product.price);
            String priceNew = "₹" + String.valueOf(Math.round(price - discount));
            holder.price.setText(priceValue);
            holder.priceNew.setText(priceNew);
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (image != null && !image.isEmpty()) {
                try {
                    Picasso.with(fragment.getContext())
                            .load(image)
                            .centerCrop()
                            .fit()
                            .placeholder(R.drawable.beansbg)
                            .priority(Picasso.Priority.LOW)
                            .tag(Settings.CAROUSEL_TAG)
                            .into(holder.imageView);
                } catch (Exception ignored) {}
            }
        }
    }

    @Override
    public int getItemCount() {
        if (circularScroll)
            return Integer.MAX_VALUE/2;
        else
            return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView price;
        TextView priceNew;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            price = itemView.findViewById(R.id.price);
            priceNew = itemView.findViewById(R.id.priceNew);
        }
    }

}
