package com.miracas.espresso.adapters;


import android.content.Context;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miracas.R;
import com.miracas.espresso.client.responses.BrewedItem;
import com.miracas.espresso.client.responses.Product;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExpressoBrewedAdapter extends RecyclerView.Adapter<ExpressoBrewedAdapter.ViewHolder> {

    private Context context;
    private List<BrewedItem> items;

    public ExpressoBrewedAdapter(Context context, List<BrewedItem> items) {
        super();
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_expresso_brewed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BrewedItem item = items.get(position);

        if (position == getItemCount() - 1)
            holder.footerMessage.setVisibility(View.VISIBLE);
        else
            holder.footerMessage.setVisibility(View.GONE);

        holder.title.setText(item.product.name);

        if (item.product.img_url != null && !item.product.img_url.isEmpty())
            try {
                Picasso.with(context)
                        .load(item.product.img_url)
                        .priority(Picasso.Priority.HIGH)
                        .into(holder.image);
            } catch (Exception ignored) {}

        holder.priceOld.setPaintFlags(holder.priceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        NumberFormat nf = NumberFormat.getInstance(Locale.US);

        double price, discount;
        try {
            price = nf.parse(item.product.price).doubleValue();
            discount = nf.parse(item.product.discount).doubleValue();
        } catch (ParseException e) {
            price = 0;
            discount = 0;
        }

        String priceOld = "₹" +  item.product.price;
        String priceNew = "₹" + String.valueOf(Math.round(price - discount));

        holder.priceOld.setText(priceOld);
        holder.priceNew.setText(priceNew);

        scaleColor(holder);

        long now = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        long difference = 10000;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(item.created_at));
            calendar.add(Calendar.DATE, 6);
            long end = calendar.getTime().getTime();
            difference = end - now;
        } catch (ParseException ignored) {
        }
        try {
            long seconds = difference / 1000;
            long minutes = (seconds / 60) % 60;
            long hours = (seconds / 60) / 60;
            holder.timerSeconds.setText(String.valueOf(seconds % 60));
            holder.timerMinutes.setText(String.valueOf(minutes));
            holder.timerHours.setText(String.valueOf(hours));
        } catch (Exception ignored) {
        }
    }

    private void scaleColor(ViewHolder holder) {
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
        TextView timerHours;
        TextView timerMinutes;
        TextView timerSeconds;
        View footerMessage;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);
            priceOld = view.findViewById(R.id.priceOld);
            priceNew = view.findViewById(R.id.priceNew);
            timerHours = view.findViewById(R.id.timerHours);
            timerMinutes = view.findViewById(R.id.timerMinutes);
            timerSeconds = view.findViewById(R.id.timerSeconds);
            footerMessage = view.findViewById(R.id.footerMessage);
        }
    }

}
