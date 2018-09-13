package com.miracas.espresso.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miracas.R;
import com.miracas.espresso.client.responses.Product;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class DirectPurchaseAdapter extends RecyclerView.Adapter<DirectPurchaseAdapter.ViewHolder> {

    private Context context;
    private List<Product> items;
    private boolean reachedEnd = false;

    public DirectPurchaseAdapter(Context context, List<Product> items) {
        super();
        this.context = context;
        this.items = items;
    }

    public void setReachedEnd() {
        reachedEnd = true;
        notifyItemChanged(getItemCount()-1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_purchase_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Product item = items.get(position);

        if (position == getItemCount() - 1 && reachedEnd)
            holder.footerMessage.setVisibility(View.VISIBLE);
        else
            holder.footerMessage.setVisibility(View.GONE);

        holder.title.setText(item.name);

        if (item.img_url != null && !item.img_url.isEmpty())
            try {
                Picasso.with(context)
                        .load(item.img_url)
                        .priority(Picasso.Priority.HIGH)
                        .into(holder.image);
            } catch (Exception ignored) {}

        holder.priceOld.setPaintFlags(holder.priceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        NumberFormat nf = NumberFormat.getInstance(Locale.US);

        double price, discount;
        try {
            price = nf.parse(item.price).doubleValue();
            discount = nf.parse(item.discount).doubleValue();
        } catch (ParseException e) {
            price = 0;
            discount = 0;
        }

        String priceOld = "₹" +  item.price;
        String priceNew = "₹" + String.valueOf(Math.round(price - discount));

        holder.priceOld.setText(priceOld);
        holder.priceNew.setText(priceNew);

        if (priceNew.equals(priceOld)) {
            holder.priceOld.setVisibility(View.GONE);
        }
    }

    private void scaleColor(ExpressoBrewedAdapter.ViewHolder holder) {
        int color = ContextCompat.getColor(context, R.color.pastel_red);
        holder.timerHours.setTextColor(color);
        holder.timerMinutes.setTextColor(color);
        holder.timerSeconds.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView priceOld;
        TextView priceNew;
        View footerMessage;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);
            priceOld = view.findViewById(R.id.priceOld);
            priceNew = view.findViewById(R.id.priceNew);
            footerMessage = view.findViewById(R.id.footerMessage);
        }
    }

}
