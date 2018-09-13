package com.miracas.espresso.fragments.expresso;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.adapters.ExpressoBrewedAdapter;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.espresso.GetBrewedProductsClient;
import com.miracas.espresso.client.responses.BrewedItem;
import com.miracas.espresso.client.responses.Product;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.layouts.CustomLinearLayoutManager;
import com.miracas.espresso.listeners.RecyclerItemClickListener;
import com.miracas.espresso.utils.SharedStorage;
import com.miracas.espresso.utils.SharedStorageHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyExpressoTabBrewed extends Fragment implements IOnRequestCompleted{

    ExpressoBrewedAdapter adapter;
    RecyclerView listView;
    List<BrewedItem> items;

    boolean userVisitedNonEmptyCart;
    View tint;
    View tooltip;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.tab_layout_my_expresso_brewed, container, false);

        tint = view.findViewById(R.id.tint);
        tooltip = view.findViewById(R.id.tooltipBrewed);

        SharedStorageHelper helper = new SharedStorageHelper(getActivity());
        String userID = helper.getUserID();

        items = new ArrayList<>();
        adapter = new ExpressoBrewedAdapter(getContext(), items);
        listView = view.findViewById(R.id.products_list);
        CustomLinearLayoutManager customLinearLayoutManager = new CustomLinearLayoutManager(getContext());
        listView.setLayoutManager(customLinearLayoutManager);
        listView.setAdapter(adapter);

        userVisitedNonEmptyCart = helper.getUserVisitedNonEmptyCart();
        //userVisitedNonEmptyCart = false;

        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), listView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        if (!userVisitedNonEmptyCart) {
                            userVisitedNonEmptyCart = true;
                            return;
                        }

                        Product product = items.get(position).product;
                        FragmentManager fragmentManager = getFragmentManager();
                        if (fragmentManager != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString("PRODUCT", new Gson().toJson(product));
                            bundle.putBoolean("SHOW_CART", true);
                            Fragment fragment = new ProductFragment();
                            fragment.setArguments(bundle);
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                            transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // Do nothing
                    }
                })
        );

        GetBrewedProductsClient client = new GetBrewedProductsClient(this, userID);
        client.execute();

        Handler timer = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int firstPosition = customLinearLayoutManager.findFirstVisibleItemPosition();
                int lastPosition = customLinearLayoutManager.findLastVisibleItemPosition();
                if (items != null && !items.isEmpty() && firstPosition >= 0) {
                    long now = System.currentTimeMillis();
                    for (int i = 0; i <= lastPosition - firstPosition; ++i) {
                        View child = listView.getChildAt(i);
                        BrewedItem item = items.get(i + firstPosition);
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
                            TextView timerHours = child.findViewById(R.id.timerHours);
                            TextView timerMinutes = child.findViewById(R.id.timerMinutes);
                            TextView timerSeconds = child.findViewById(R.id.timerSeconds);
                            if (difference > 0) {
                                long seconds = difference / 1000;
                                long minutes = (seconds / 60) % 60;
                                long hours = (seconds / 60) / 60;
                                timerSeconds.setText(String.valueOf(seconds % 60));
                                timerMinutes.setText(String.valueOf(minutes));
                                timerHours.setText(String.valueOf(hours));
                            } else {
                                timerSeconds.setText("0");
                                timerMinutes.setText("0");
                                timerHours.setText("0");
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                timer.postDelayed(this, 1000);
            }
        };

        timer.postDelayed(runnable, 1000);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void handleTooltipDisplay() {
        if (!userVisitedNonEmptyCart) {
            tint.setVisibility(View.VISIBLE);
            tooltip.setVisibility(View.VISIBLE);
            tint.setOnClickListener(view1 -> {
                tint.setVisibility(View.GONE);
                tooltip.setVisibility(View.GONE);
            });
        }
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        View view = getView();
        if (clientCode == ClientCodes.GET_BREWED_ITEMS && view != null) {

            List<BrewedItem> brewedItems = (List<BrewedItem>) response;
            if (brewedItems != null) {
                for (Iterator<BrewedItem> iterator = brewedItems.iterator(); iterator.hasNext();) {
                    BrewedItem item = iterator.next();
                    if (item.product.img_url == null || item.product.img_url.isEmpty())
                        iterator.remove();
                }

                if (brewedItems.isEmpty()) {
                    view.findViewById(R.id.emptyMessage).setVisibility(View.VISIBLE);

                } else {
                    items.addAll(brewedItems);
                    adapter.notifyDataSetChanged();
                    handleTooltipDisplay();
                }

            }

            view.findViewById(R.id.loading).setVisibility(View.GONE);
        }
    }

}
