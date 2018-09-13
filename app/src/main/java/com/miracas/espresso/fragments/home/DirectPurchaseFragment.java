package com.miracas.espresso.fragments.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.adapters.DirectPurchaseAdapter;
import com.miracas.espresso.client.ProductsFeedClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.ActiveKeyword;
import com.miracas.espresso.client.responses.Product;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.fragments.expresso.ProductFragment;
import com.miracas.espresso.layouts.CustomLinearLayoutManager;
import com.miracas.espresso.listeners.RecyclerItemClickListener;
import java.util.ArrayList;
import java.util.List;

public class DirectPurchaseFragment extends BaseFragment {

    private DirectPurchaseAdapter adapter;
    private List<Product> items;
    private ActiveKeyword activeKeyword;
    private int currentPage = 0;
    private boolean productsRequested = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_direct_purchase, container, false);

        if (getArguments() != null) {
            String keywordString = getArguments().getString("KEYWORD");
            activeKeyword = new Gson().fromJson(keywordString, ActiveKeyword.class);
            onCreateToolbar(view, activeKeyword.getBrandLinkOrKeyword(), false);
        }

        items = new ArrayList<>();
        adapter = new DirectPurchaseAdapter(getContext(), items);
        RecyclerView listView = view.findViewById(R.id.products_list);
        CustomLinearLayoutManager customLinearLayoutManager = new CustomLinearLayoutManager(getContext());
        listView.setLayoutManager(customLinearLayoutManager);
        listView.setAdapter(adapter);

        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), listView,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Product product = items.get(position);

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

        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastPosition = customLinearLayoutManager.findLastVisibleItemPosition();
                if (!productsRequested && lastPosition == items.size() - 1) {
                    appendProducts();
                }
            }
        });

        ProductsFeedClient client = new ProductsFeedClient(this,
                activeKeyword.getBrandLinkOrKeyword(), "male", 0);
        client.execute();
        return view;
    }

    private void appendProducts() {
        currentPage += 10;
        ProductsFeedClient client = new ProductsFeedClient(
                this, activeKeyword.getBrandLinkOrKeyword(), activeKeyword.genders, currentPage);
        client.execute();
        productsRequested = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onCreateToolbar(view, activeKeyword.getBrandLinkOrKeyword(), false);
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        View view = getView();
        if (clientCode == ClientCodes.PRODUCT_FEED_CLIENT && view != null) {

            List<Product> products = (List<Product>) response;
            if (products != null) {
                if (!products.isEmpty()) {
                    items.addAll(products);
                    adapter.notifyDataSetChanged();
                    productsRequested = false;

                } else {
                    adapter.setReachedEnd();
                }
            }

            view.findViewById(R.id.loading).setVisibility(View.GONE);
        }
    }
}
