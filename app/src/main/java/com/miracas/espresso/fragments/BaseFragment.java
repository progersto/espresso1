package com.miracas.espresso.fragments;


import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.miracas.R;
import com.miracas.espresso.design.IOnRequestCompleted;

public abstract class BaseFragment extends Fragment implements IOnRequestCompleted {

    public void showProgress(boolean show) {
        View view = getView();
        if (view != null) {
            if (show)
                view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
            else
                view.findViewById(R.id.loading).setVisibility(View.GONE);
        }
    }

    protected void onCreateToolbar(View view, String title, boolean base) {
        setTitle(title);

        Activity activity = getActivity();
        if (activity == null)
            return;

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(null);
        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);

        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        if (base) {
            drawerLayout.addDrawerListener(toggle);
            toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#ffffff"));
            toolbar.setNavigationOnClickListener(view1 -> drawerLayout.openDrawer(GravityCompat.START));
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            AppBarLayout appBarLayout = activity.findViewById(R.id.appBarLayout);
            appBarLayout.setExpanded(false);

        } else {
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                toggle.setDrawerIndicatorEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        toggle.syncState();
    }

    private void setTitle(String title) {
        Activity activity = getActivity();
        if (activity == null)
            return;
        activity.setTitle(title);
        if (getContext() != null) {
            FirebaseAnalytics.getInstance(getContext())
                    .setCurrentScreen(getActivity(), title, null);
        }
    }

}
