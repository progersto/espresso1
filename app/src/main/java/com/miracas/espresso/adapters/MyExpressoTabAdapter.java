package com.miracas.espresso.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.miracas.espresso.fragments.expresso.MyExpressoTabBrewed;
import com.miracas.espresso.fragments.expresso.MyExpressoTabBrewing;

public class MyExpressoTabAdapter extends FragmentStatePagerAdapter {

    public MyExpressoTabAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyExpressoTabBrewed();
            case 1:
                return new MyExpressoTabBrewing();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "BREWED";
            case 1:
                return "BREWING";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
