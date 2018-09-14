package com.miracas.espresso.fragments.contacts;

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
import com.miracas.espresso.adapters.MyContactsTabAdapter;
import com.miracas.espresso.adapters.MyExpressoTabAdapter;
import com.miracas.espresso.client.GetCountClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.fragments.BaseFragment;

public class ContactPager extends BaseFragment implements TabLayout.OnTabSelectedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean goToBrewing = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        final View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.addTab(tabLayout.newTab().setText("Shoppers I follow"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = view.findViewById(R.id.pager);

        MyContactsTabAdapter adapter = new MyContactsTabAdapter(getFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


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