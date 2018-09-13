package com.miracas.espresso.adapters;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.config.Settings;
import com.miracas.espresso.fragments.expresso.ZoomedProductFragment;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {

    private Fragment fragment;
    private List<String> imageUrls;

    public CarouselAdapter(Fragment fragment, List<String> imageUrls) {
        this.fragment = fragment;
        this.imageUrls = imageUrls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (fragment != null) {
            LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
            View view = inflater.inflate(R.layout.carousel_item, parent, false);
            return new ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder != null) {
            String image = imageUrls.get(position);
            final int finalPosition = position;
            if (image != null && !image.isEmpty()) {
                try {
                    Picasso.with(fragment.getContext())
                            .load(image)
                            .placeholder(R.drawable.beansbg)
                            .priority(Picasso.Priority.LOW)
                            .tag(Settings.CAROUSEL_TAG)
                            .into(holder.imageView);
                } catch (Exception ignored) {}
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fragmentManager = fragment.getFragmentManager();
                        if (fragmentManager != null) try {
                            Bundle bundle = new Bundle();
                            bundle.putString("IMAGES", new Gson().toJson(imageUrls));
                            bundle.putInt("POSITION", finalPosition);
                            Fragment fragment = new ZoomedProductFragment();
                            fragment.setArguments(bundle);
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                            transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
                        } catch (Exception ignored) {}
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }

}
