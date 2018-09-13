package com.miracas.espresso.fragments.expresso;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miracas.R;
import com.miracas.espresso.config.Settings;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.layouts.TouchImageView;
import com.miracas.espresso.viewpager.ExtendedViewPager;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ZoomedProductFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_zoomed_product, container, false);

        List<String> images = new ArrayList<>();
        int position = 0;

        Bundle bundle = getArguments();
        if (bundle != null) {
            Type type = new TypeToken<List<String>>() {}.getType();
            String imageURLs = getArguments().getString("IMAGES");
            position = getArguments().getInt("POSITION");
            images = new Gson().fromJson(imageURLs, type);
        }

        TouchImageAdapter adapter = new TouchImageAdapter(getContext(), images);
        ExtendedViewPager mViewPager = view.findViewById(R.id.carousel);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(position);

        view.findViewById(R.id.closeButton).setOnClickListener(view1 -> {
            Activity activity = getActivity();
            if (activity != null)
                activity.onBackPressed();
        });

        return view;
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {

    }

    public class TouchImageAdapter extends PagerAdapter {

        private List<String> images;
        private Context context;

        TouchImageAdapter(Context context, List<String> images) {
            this.images = images;
            this.context = context;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {
            TouchImageView img = new TouchImageView(container.getContext());
            try {
                Picasso.with(context)
                        .load(images.get(position))
                        .placeholder(R.drawable.beansbg)
                        .priority(Picasso.Priority.HIGH)
                        .tag(Settings.CAROUSEL_TAG)
                        .into(img);
            } catch (Exception ignored) {}
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

    }
}
