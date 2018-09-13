package com.miracas.espresso.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.adapters.SectionAdapter;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.ActiveKeywordClient;
import com.miracas.espresso.client.espresso.GetBrewedProductsClient;
import com.miracas.espresso.client.responses.ActiveKeyword;
import com.miracas.espresso.client.responses.BrewedItem;
import com.miracas.espresso.client.responses.Quote;
import com.miracas.espresso.client.responses.SectionPageElement;
import com.miracas.espresso.client.responses.custom.BrewedKeyword;
import com.miracas.espresso.client.responses.custom.DailyKeywordItem;
import com.miracas.espresso.fragments.expresso.MyExpressoFragment;
import com.miracas.espresso.fragments.home.DailyCollectionFragment;
import com.miracas.espresso.fragments.home.HomePageFragment;
import com.miracas.espresso.layouts.WrapContentLinearLayoutManager;
import com.miracas.espresso.listeners.RecyclerItemClickListener;
import com.miracas.espresso.utils.FacebookManager;
import com.miracas.espresso.utils.SharedStorageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SectionPageFragment extends BaseFragment {

    private List<SectionPageElement> elements;
    private List<ActiveKeyword> keywords;
    private List<Quote> quotes;
    private List<ActiveKeyword> keywords_last_three_days;

    private BrewedKeyword keyword;

    private SectionAdapter adapter;
    private AppBarLayout appBarLayout;
    private RecyclerView listView;

    private boolean sharedProductInvoked = false;
    private int keywordID;
    private String productString;
    private boolean brewedItemsFetched = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_section_page, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            productString = bundle.getString("PRODUCT");
            keywordID = bundle.getInt("KEYWORD_ID");
            if (productString != null && !productString.isEmpty()) {
                sharedProductInvoked = true;
            }
        }

        appBarLayout = view.findViewById(R.id.appBarLayout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSections();
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView(), "Home", true);
        refreshList();
    }

    public void refreshList() {
        showProgress(true);
        onCreateToolbar(getView(), "Home", true);
        ActiveKeywordClient client = new ActiveKeywordClient(this);
        client.execute();
        FacebookManager manager = new FacebookManager(getActivity());

        //noinspection StatementWithEmptyBody
        if (manager.checkLoginStatus()) {
            if (!brewedItemsFetched) {
                SharedStorageHelper helper = new SharedStorageHelper(getActivity());
                String userID = helper.getUserID();
                GetBrewedProductsClient client1 = new GetBrewedProductsClient(this, userID);
                client1.execute();
            }
        } else {
            listView.stopScroll();
            keywords.clear();
            elements.clear();
            keyword = null;
            brewedItemsFetched = false;
            //appBarLayout.setVisibility(View.VISIBLE);
            //hamburger.setVisibility(View.GONE);
            //hamburgerEnabled = false;
            //onCreateToolbar(getView(), "Home", true);
        }
    }

    private void initSections() {
        View view = getView();
        if (view != null) {
            elements = new ArrayList<>();
            keywords = new ArrayList<>();
            quotes = new ArrayList<>();
            keywords_last_three_days = new ArrayList<>();

            adapter = new SectionAdapter(getContext(), this, elements, quotes, true);
            listView = view.findViewById(R.id.sections_list);
            listView.setAdapter(adapter);
            listView.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));

            listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (getView() != null) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0) {
                            appBarLayout.setExpanded(true, true);
                        } else if (dy < 0) {
                            appBarLayout.setExpanded(false, true);
                        }
                    }
                }
            });

            listView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getContext(), listView ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            if (position == -1) return;
                            try {
                                if (elements.get(position) instanceof ActiveKeyword) {
                                    openKeyword((ActiveKeyword) elements.get(position), false);
                                } else if (elements.get(position) instanceof BrewedKeyword) {
                                    FragmentManager fragmentManager = getFragmentManager();
                                    if (fragmentManager != null) {
                                        Fragment fragment = new MyExpressoFragment();
                                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                                        transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                                        transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
                                    }
                                } else if (elements.get(position) instanceof DailyKeywordItem) {
                                    FragmentManager fragmentManager = getFragmentManager();
                                    if (fragmentManager != null) {
                                        Fragment fragment = new DailyCollectionFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("QUOTES", new Gson().toJson(quotes));
                                        fragment.setArguments(bundle);
                                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                                        transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                                        transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
                                    }
                                }
                            } catch (IndexOutOfBoundsException ignored) {}
                        }

                        @Override public void onLongItemClick(View view, int position) {
                            // Do nothing
                        }
                    })
            );

        }
    }

    private void openKeyword(ActiveKeyword keyword, boolean sharedProduct) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            Bundle bundle = new Bundle();
            bundle.putString("KEYWORD", new Gson().toJson(keyword));

            if (sharedProduct) {
                bundle.putString("PRODUCT", productString);
            }

            Fragment fragment = new HomePageFragment();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
            transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
        }
    }

    private void handleSharedProduct() {
        boolean found = false;
        for (SectionPageElement keyword : keywords) {
            if (Integer.valueOf(((ActiveKeyword)keyword).id_keyword) == keywordID) {
                sharedProductInvoked = false;
                openKeyword((ActiveKeyword) keyword, true);
                found = true;
                break;
            }
        }

        if (!found) {
            Toast.makeText(getContext(), "The product you are looking for has expired",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestComplete(int code, Object response) {
        View view = getView();
        if (view != null) {
            if (code == ClientCodes.GET_ACTIVE_KEYWORD && response != null) {
                ActiveKeywordClient.Response response1 = (ActiveKeywordClient.Response) response;
                listView.stopScroll();
                keywords.clear();
                quotes.clear();
                elements.clear();

                if (keyword != null)
                    elements.add(keyword);

                keywords.addAll(response1.keywords);
                quotes.addAll(response1.quotes);

                Collections.sort(keywords, (k1, k2) -> {
                    if (Integer.valueOf(k1.id_keyword) >
                            Integer.valueOf(k2.id_keyword)) return -1;
                    return 1;
                });

                keywords_last_three_days.clear();
                keywords_last_three_days.addAll(response1.keywords_last_three_days);
                if (keywords_last_three_days.size() > 0) {
                    DailyKeywordItem dailyKeywordItem = new DailyKeywordItem();
                    dailyKeywordItem.keywords = new ArrayList<>();
                    dailyKeywordItem.keywords.addAll(keywords_last_three_days);
                    Collections.sort(dailyKeywordItem.keywords, (k1, k2) -> {
                        if (k1.getDate().after(k2.getDate()))
                            return -1;
                        return 1;
                    });
                    dailyKeywordItem.keywords.add(new ActiveKeyword());
                    elements.add(dailyKeywordItem);
                }

                elements.addAll(keywords);

                adapter.refresh();
                //adapter.notifyDataSetChanged();
                showProgress(false);
                if (sharedProductInvoked)
                    handleSharedProduct();

            } else if (code == ClientCodes.GET_BREWED_ITEMS && response != null) {
                List<BrewedItem> brewedItems = (List<BrewedItem>) response;
                brewedItemsFetched = true;
                if (!brewedItems.isEmpty()) {
                    keyword = new BrewedKeyword();
                    if (brewedItems.size() >= 3)
                        keyword.products = brewedItems;
                    else
                        keyword.products = new ArrayList<>();
                    keyword.image = brewedItems.get(0).product.img_url;
                    elements.clear();
                    if (!brewedItems.isEmpty()) {
                        listView.stopScroll();
                        elements.add(keyword);
                    }

                    if (keywords != null) {

                        if (keywords_last_three_days.size() > 0) {
                            DailyKeywordItem dailyKeywordItem = new DailyKeywordItem();
                            dailyKeywordItem.keywords = new ArrayList<>();
                            dailyKeywordItem.keywords.addAll(keywords_last_three_days);
                            Collections.sort(dailyKeywordItem.keywords, (k1, k2) -> {
                                if (k1.getDate().after(k2.getDate()))
                                    return -1;
                                return 1;
                            });
                            dailyKeywordItem.keywords.add(new ActiveKeyword());
                            elements.add(dailyKeywordItem);
                        }

                        elements.addAll(keywords);
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        }
    }

}
