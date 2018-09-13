package com.miracas.espresso.adapters;


import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.miracas.R;
import com.miracas.espresso.config.Settings;
import com.miracas.espresso.layouts.TouchImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ZoomAdapter extends RecyclerView.Adapter<ZoomAdapter.ViewHolder> {

    private Fragment fragment;
    private List<String> imageUrls;

    public ZoomAdapter(Fragment fragment, List<String> imageUrls) {
        this.fragment = fragment;
        this.imageUrls = imageUrls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (fragment != null) {
            LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
            View view = inflater.inflate(R.layout.list_item_zoom, parent, false);
            return new ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder != null) {
            String image = imageUrls.get(position);
            if (image != null && !image.isEmpty()) {
                try {
                    Picasso.with(fragment.getContext())
                            .load(image)
                            .placeholder(R.drawable.beansbg)
                            .priority(Picasso.Priority.HIGH)
                            .tag(Settings.CAROUSEL_TAG)
                            .into(holder.imageView);
                } catch (Exception ignored) {}
            }
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TouchImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

}
