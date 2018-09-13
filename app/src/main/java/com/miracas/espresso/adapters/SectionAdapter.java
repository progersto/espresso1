package com.miracas.espresso.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.miracas.R;
import com.miracas.espresso.client.responses.ActiveKeyword;
import com.miracas.espresso.client.responses.Quote;
import com.miracas.espresso.client.responses.SectionPageElement;
import com.miracas.espresso.client.responses.custom.BrewedKeyword;
import com.miracas.espresso.client.responses.custom.DailyKeywordItem;
import com.miracas.espresso.config.Settings;
import com.miracas.espresso.fragments.home.DailyCollectionFragment;
import com.miracas.espresso.utils.MiscUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private static final int BREWED_KEYWORD_VIEW = 1010;
    private static final int KEYWORD_VIEW = 1020;
    private static final int DAILY_KEYWORD_ITEM = 1030;

    private Context context;
    private List<SectionPageElement> keywords;
    private List<Quote> quotes;
    private Fragment fragment;
    private boolean displayProgress;

    private String[] colors = { "#50596C", "#9F8241", "#7B9970", "#37889B" };

    public SectionAdapter(Context context, Fragment fragment,
                          List<SectionPageElement> keywords, List<Quote> quotes, boolean displayProgress) {
        super();
        this.context = context;
        this.fragment = fragment;
        this.keywords = keywords;
        this.quotes = quotes;
        this.displayProgress = displayProgress;
    }

    public void refresh() {
        int i = 0;
        for (SectionPageElement element : keywords) {
            if (element instanceof ActiveKeyword)
                this.notifyItemChanged(i);
            i++;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        if (viewType == BREWED_KEYWORD_VIEW) {
            View view = inflater.inflate(R.layout.list_item_section_brewed_item, parent, false);
            return new BrewedItemViewHolder(view);
        } else if (viewType == KEYWORD_VIEW) {
            View view = inflater.inflate(R.layout.list_item_section, parent, false);
            return new NormalViewHolder(view);
        } else if (viewType == DAILY_KEYWORD_ITEM) {
            View view = inflater.inflate(R.layout.list_item_section_daily, parent, false);
            return new DailyKeywordViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (keywords != null && !keywords.isEmpty()
                && position >= 0 && position < keywords.size()) {
            SectionPageElement element = keywords.get(position);
            if (element instanceof BrewedKeyword)
                return BREWED_KEYWORD_VIEW;
            else if (element instanceof ActiveKeyword)
                return KEYWORD_VIEW;
            else if (element instanceof DailyKeywordItem)
                return DAILY_KEYWORD_ITEM;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder != null) {
            SectionPageElement element = keywords.get(position);

            if (element instanceof BrewedKeyword) {
                BrewedItemViewHolder brewedItemViewHolder = (BrewedItemViewHolder) holder;
                BrewedKeyword brewedKeyword = (BrewedKeyword) element;
                if (brewedKeyword.image != null && !brewedKeyword.image.isEmpty()) {
                    //brewedItemViewHolder.progress.setText("");
                    Picasso.with(context)
                            .load(brewedKeyword.image)
                            .centerCrop()
                            .fit()
                            .skipMemoryCache()
                            .priority(Picasso.Priority.HIGH)
                            .into(brewedItemViewHolder.image);
                    boolean circularScroll = brewedKeyword.products.size() >= 3;
                    BrewedKeywordProductAdapter adapter = new BrewedKeywordProductAdapter(
                            fragment, brewedKeyword.products, circularScroll);
                    final LinearLayoutManager manager = new LinearLayoutManager(
                            context, LinearLayoutManager.HORIZONTAL, false);
                    brewedItemViewHolder.carousel.setLayoutManager(manager);
                    brewedItemViewHolder.carousel.setAdapter(adapter);
                    brewedItemViewHolder.carousel.clearOnScrollListeners();
                    brewedItemViewHolder.carousel.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        int lastLoadedPosition = 0;

                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == 0) {
                                int position = manager.findFirstVisibleItemPosition();
                                int lastPosition = manager.findLastVisibleItemPosition();
                                int midPosition = ((position + lastPosition) / 2) % brewedKeyword.products.size();
                                if (midPosition >= 0 && midPosition != lastLoadedPosition) {
                                    lastLoadedPosition = midPosition;
                                    try {
                                        Picasso.with(context)
                                                .load(brewedKeyword.products.get(midPosition).product.img_url)
                                                .centerCrop()
                                                .fit()
                                                .skipMemoryCache()
                                                .placeholder(brewedItemViewHolder.image.getDrawable())
                                                .priority(Picasso.Priority.HIGH)
                                                .into(brewedItemViewHolder.image);
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        }
                    });

                    if (circularScroll)
                        brewedItemViewHolder.carousel.scrollToPosition(Integer.MAX_VALUE / 4);
                }

            } else if (element instanceof ActiveKeyword) {
                ActiveKeyword keyword = (ActiveKeyword) keywords.get(position);
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;

                String text;
                if (keyword.description != null && !keyword.description.isEmpty()
                        && !keyword.description.trim().isEmpty())
                    text = keyword.description;
                else
                    text = keyword.keyword;
                normalViewHolder.title.setText(text);

                String quote;
                if (quotes.size() > position) {
                    quote = "\"" + quotes.get(position).quote + "\"\n- " + quotes.get(position).author;
                } else if (!quotes.isEmpty()) {
                    quote = "\"" + quotes.get(0).quote + "\"\n- " + quotes.get(0).author;
                } else {
                    quote = "We must never confuse elegance with snobbery";
                }

                String color = colors[position % colors.length];

                normalViewHolder.quote.setText(quote);
                normalViewHolder.quote.setBackgroundColor(Color.parseColor(color));

                Typeface customFont = Typeface.createFromAsset(fragment.getActivity().getAssets(),
                        Settings.CUSTOM_FONT);
                normalViewHolder.quote.setTypeface(customFont);

                if (displayProgress) {
                    normalViewHolder.donut.setVisibility(View.VISIBLE);

                    int discountPercent;
                    if (keyword.booking_threshold == 0.0F) {
                        discountPercent = 0;
                    } else {
                        discountPercent = (keyword.booking_count * 100) / keyword.booking_threshold;
                    }

                    String currentDiscount = String.valueOf(Math.round(discountPercent)) + "%";
                    normalViewHolder.progressText.setText(currentDiscount);

                    normalViewHolder.progress.setProgress(discountPercent);
                    normalViewHolder.progress.setText("");

                } else {
                    normalViewHolder.donut.setVisibility(View.GONE);
                }

                if (keyword.image != null && !keyword.image.isEmpty())
                    try {
                        Picasso.with(context)
                                .load(keyword.image)
                                .centerCrop()
                                .fit()
                                .priority(Picasso.Priority.HIGH)
                                .into(normalViewHolder.image);
                    } catch (Exception ignored) {}


            } else if (element instanceof DailyKeywordItem) {
                DailyKeywordViewHolder dailyKeywordViewHolder = (DailyKeywordViewHolder) holder;
                DailyKeywordItem dailyKeywordItem = (DailyKeywordItem) keywords.get(position);

                if (dailyKeywordItem.keywords.size() > 1) {
                    DailyKeywordsAdapter adapter = new DailyKeywordsAdapter(context,
                            dailyKeywordItem.keywords);
                    LinearLayoutManager manager = new LinearLayoutManager(
                            context, LinearLayoutManager.HORIZONTAL, false);
                    dailyKeywordViewHolder.list.setAdapter(adapter);
                    dailyKeywordViewHolder.list.setLayoutManager(manager);

                    String quote;
                    if (quotes.size() > position) {
                        quote = "\"" + quotes.get(position).quote + "\"\n- " + quotes.get(position).author;
                    } else if (!quotes.isEmpty()) {
                        quote = "\"" + quotes.get(0).quote + "\"\n- " + quotes.get(0).author;
                    } else {
                        quote = "We must never confuse elegance with snobbery";
                    }

                    String color = colors[position % colors.length];

                    dailyKeywordViewHolder.quote.setText(quote);
                    dailyKeywordViewHolder.quote.setBackgroundColor(Color.parseColor(color));

                } else {
                    dailyKeywordViewHolder.parent.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return keywords.size();
    }

    class DailyKeywordViewHolder extends ViewHolder {
        RecyclerView list;
        TextView quote;
        View parent;

        DailyKeywordViewHolder(View view) {
            super(view);
            list = view.findViewById(R.id.list);
            quote = view.findViewById(R.id.quote);
            parent = view.findViewById(R.id.parent);
        }
    }

    class NormalViewHolder extends ViewHolder {
        ImageView image;
        TextView title;
        TextView quote;
        TextView progressText;
        DonutProgress progress;
        View donut;

        NormalViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);
            quote = view.findViewById(R.id.quote);
            progressText = view.findViewById(R.id.progressValue);
            progress = view.findViewById(R.id.progress);
            donut = view.findViewById(R.id.donut);
        }
    }

    class BrewedItemViewHolder extends ViewHolder {
        ImageView image;
        RecyclerView carousel;
        BrewedItemViewHolder(View view) {
            super(view);
            carousel = view.findViewById(R.id.carousel);
            image = view.findViewById(R.id.image);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
    }

}
