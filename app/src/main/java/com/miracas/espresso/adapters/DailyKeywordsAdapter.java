package com.miracas.espresso.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miracas.R;
import com.miracas.espresso.client.responses.ActiveKeyword;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyKeywordsAdapter extends RecyclerView.Adapter<DailyKeywordsAdapter.ViewHolder> {

    private Context context;
    private List<ActiveKeyword> keywords;

    DailyKeywordsAdapter (Context context, List<ActiveKeyword> keywords) {
        super();
        this.context = context;
        this.keywords = keywords;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_section_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder != null) {
            ActiveKeyword keyword = keywords.get(position);

            if (position > 1)
                holder.newCollection.setVisibility(View.GONE);

            if (position == getItemCount()-1) {
                holder.newCollection.setVisibility(View.GONE);
                holder.card.setVisibility(View.GONE);
                holder.schedule.setVisibility(View.VISIBLE);
                try {
                    Picasso.with(context)
                            .load(R.drawable.see_more)
                            .centerInside().fit()
                            .into(holder.seeMore);
                } catch (Exception ignored) {}

            } else {
                holder.schedule.setVisibility(View.GONE);

                DateFormat dateFormat1 = new SimpleDateFormat("dd", Locale.US);
                DateFormat dateFormat2 = new SimpleDateFormat("MMMM", Locale.US);

                holder.date.setText(dateFormat1.format(keyword.getDate()));
                holder.month.setText(dateFormat2.format(keyword.getDate()).toUpperCase());

                try {
                    Picasso.with(context)
                            .load(keyword.image)
                            .centerCrop().fit()
                            .priority(Picasso.Priority.HIGH)
                            .into(holder.image);
                } catch (Exception ignored) {}
            }
        }
    }

    @Override
    public int getItemCount() {
        return keywords.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, seeMore;
        View newCollection;
        TextView date, month;
        View schedule, card;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            seeMore = itemView.findViewById(R.id.seeMore);
            newCollection = itemView.findViewById(R.id.newCollection);
            date = itemView.findViewById(R.id.date);
            month = itemView.findViewById(R.id.month);
            schedule = itemView.findViewById(R.id.schedule);
            card = itemView.findViewById(R.id.card);
        }
    }

}
