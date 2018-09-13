package com.miracas.espresso.fragments.expresso;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miracas.R;
import com.miracas.espresso.adapters.MyExpressoTabAdapter;
import com.miracas.espresso.client.GetCountClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.espresso.GetBrewedProductsClient;
import com.miracas.espresso.client.espresso.GetBrewingProductsClient;
import com.miracas.espresso.client.responses.BrewedItem;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.utils.SharedStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MyExpressoFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean goToBrewing = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        final View view = inflater.inflate(R.layout.fragment_my_expresso, container, false);
        onCreateToolbar(view, "My Espresso", false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            goToBrewing = bundle.getBoolean("BREWING");
        }

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Brewed"));
        tabLayout.addTab(tabLayout.newTab().setText("Brewing"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = view.findViewById(R.id.pager);

        MyExpressoTabAdapter adapter = new MyExpressoTabAdapter(getFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        View tab1 = inflater.inflate(R.layout.custom_tab, null);
        View tab2 = inflater.inflate(R.layout.custom_tab, null);
        ((TextView) tab1.findViewById(R.id.text)).setText("BREWED");
        ((TextView) tab2.findViewById(R.id.text)).setText("BREWING");

        if (tabLayout.getTabCount() > 1) {
            tabLayout.getTabAt(0).setCustomView(tab1);
            tabLayout.getTabAt(1).setCustomView(tab2);
        }

        if (goToBrewing) {
            TabLayout.Tab brewingTab = tabLayout.getTabAt(1);
            if (brewingTab != null)
                brewingTab.select();
        }

        GetCountClient client = new GetCountClient(this);
        client.execute();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView(), "My Espresso", false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        if (clientCode == ClientCodes.GET_COUNT) {
            GetCountClient.Response countResponse = (GetCountClient.Response) response;
            if (countResponse != null) {
                ((TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.number)).setText(String.valueOf(countResponse.espresso_brewed_count));
                ((TextView) tabLayout.getTabAt(1).getCustomView().findViewById(R.id.number)).setText(String.valueOf(countResponse.espresso_brewing_count));
            }
        }
    }
}
