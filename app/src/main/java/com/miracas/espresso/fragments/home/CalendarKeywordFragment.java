package com.miracas.espresso.fragments.home;


import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.adapters.SectionAdapter;
import com.miracas.espresso.client.ActiveKeywordClient;
import com.miracas.espresso.client.DailyKeywordClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.ActiveKeyword;
import com.miracas.espresso.client.responses.Quote;
import com.miracas.espresso.client.responses.SectionPageElement;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.layouts.WrapContentLinearLayoutManager;
import com.miracas.espresso.listeners.RecyclerItemClickListener;
import com.miracas.espresso.utils.CalendarHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CalendarKeywordFragment extends Fragment implements IOnRequestCompleted{

    public Date date;

    private List<SectionPageElement> keywords;
    private List<Quote> quotes;
    private List<Quote> myQuotes;
    private SectionAdapter adapter;

    private TextView emptyMessage;
    private ImageView sundayArt;
    private View emptyFrame;

    private View loading;

    public static CalendarKeywordFragment newInstance(Date date, List<Quote> quotes) {
        CalendarKeywordFragment fragment = new CalendarKeywordFragment();
        fragment.date = date;
        fragment.myQuotes = quotes;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        super.onCreate(s);

        View view = inflater.inflate(R.layout.fragment_calendar_keyword, container, false);

        emptyMessage = view.findViewById(R.id.emptyMessage);
        emptyFrame = view.findViewById(R.id.emptyFrame);
        sundayArt = view.findViewById(R.id.sunday_art);

        loading = view.findViewById(R.id.loading);
        keywords = new ArrayList<>();
        quotes = new ArrayList<>();

        if (myQuotes != null)
            quotes.addAll(myQuotes);

        Collections.shuffle(quotes);

        adapter = new SectionAdapter(getContext(), this, keywords, quotes, false);
        RecyclerView listView = view.findViewById(R.id.sections_list);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));

        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), listView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        if (position == -1) return;
                        try {
                            openKeyword((ActiveKeyword) keywords.get(position));
                        } catch (IndexOutOfBoundsException ignored) {}
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // Do nothing
                    }
                })
        );

        if (date != null) {
            DailyKeywordClient client = new DailyKeywordClient(this, date);
            client.execute();
        }

        return view;
    }

    private void openKeyword(ActiveKeyword keyword) {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (fragmentManager != null) {
                Bundle bundle = new Bundle();
                bundle.putString("KEYWORD", new Gson().toJson(keyword));
                Fragment fragment = new DirectPurchaseFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
            }
        }
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        if (clientCode == ClientCodes.DAILY_KEYWORDS && response != null) {
            DailyKeywordClient.Response response1 = (DailyKeywordClient.Response) response;

            if (response1.keywords.isEmpty()) {
                emptyFrame.setVisibility(View.VISIBLE);
                emptyMessage.setVisibility(View.VISIBLE);
                if (CalendarHelper.isSunday(date)) {
                    emptyMessage.setText("Heyo... Its a holiday at Miracas!");
                    sundayArt.setVisibility(View.VISIBLE);
                }
            } else {
                keywords.addAll(response1.keywords);
                emptyMessage.setVisibility(View.GONE);
            }

            Collections.sort(keywords, (k1, k2) -> {
                if (Integer.valueOf(((ActiveKeyword)(k1)).id_keyword) >
                        Integer.valueOf(((ActiveKeyword)(k2)).id_keyword)) return -1;
                return 1;
            });

            adapter.notifyDataSetChanged();
            loading.setVisibility(View.GONE);
        }
    }
}
