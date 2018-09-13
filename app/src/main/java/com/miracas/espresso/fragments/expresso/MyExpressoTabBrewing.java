package com.miracas.espresso.fragments.expresso;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miracas.R;
import com.miracas.espresso.adapters.ExpressoBrewingAdapter;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.espresso.GetBrewingProductsClient;
import com.miracas.espresso.client.responses.BrewedItem;
import com.miracas.espresso.client.responses.Product;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.layouts.CustomLinearLayoutManager;
import com.miracas.espresso.utils.SharedStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MyExpressoTabBrewing extends Fragment implements IOnRequestCompleted {

    private ExpressoBrewingAdapter adapter;
    private RecyclerView listView;
    private List<BrewedItem> items;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_layout_my_expresso_brewing, container, false);

        items = new ArrayList<>();
        adapter = new ExpressoBrewingAdapter(getContext(), items);
        listView = view.findViewById(R.id.products_list);
        CustomLinearLayoutManager customLinearLayoutManager = new CustomLinearLayoutManager(getContext());
        listView.setLayoutManager(customLinearLayoutManager);
        listView.setAdapter(adapter);

        SharedStorage storage = new SharedStorage(getActivity());
        String userID = storage.getValue(getActivity().getString(R.string.shared_storage_user_id));
        GetBrewingProductsClient client = new GetBrewingProductsClient(this, userID);
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
                            calendar.add(Calendar.DATE, 3);
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
    public void onRequestComplete(int clientCode, Object response) {
        View view = getView();

        if (clientCode == ClientCodes.GET_BREWING_ITEMS && view != null) {
            List<BrewedItem> itemsResponse = (List<BrewedItem>) response;
            if (itemsResponse != null) {
                for (Iterator<BrewedItem> iterator = itemsResponse.iterator(); iterator.hasNext();) {
                    BrewedItem item = iterator.next();
                    if (item.product.img_url == null || item.product.img_url.isEmpty())
                        iterator.remove();
                }

                if (itemsResponse.isEmpty()) {
                    view.findViewById(R.id.emptyMessage).setVisibility(View.VISIBLE);

                } else {
                    items.addAll(itemsResponse);
                    adapter.notifyDataSetChanged();

                }

            }

            view.findViewById(R.id.loading).setVisibility(View.GONE);
        }
    }

}
