package com.miracas.espresso.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.miracas.espresso.fragments.MyCircleContactsFragment;
import com.miracas.espresso.fragments.expresso.MyExpressoTabBrewing;

public class MyContactsTabAdapter extends FragmentStatePagerAdapter {

    public MyContactsTabAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyCircleContactsFragment();
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
                return "Contacts";
            case 1:
                return "Shoppers I follow";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
