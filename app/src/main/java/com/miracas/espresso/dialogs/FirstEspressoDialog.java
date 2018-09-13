package com.miracas.espresso.dialogs;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.miracas.R;
import com.miracas.espresso.fragments.expresso.MyExpressoFragment;
import com.miracas.espresso.fragments.home.HomePageFragment;

import static android.app.Activity.RESULT_OK;

public class FirstEspressoDialog extends DialogFragment {

    public static FirstEspressoDialog newInstance() {
        return new FirstEspressoDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setDimAmount(0);
        }

        setCancelable(false);

        View view = inflater.inflate(R.layout.dialog_first_espresso, container, false);

        view.findViewById(R.id.goToEspressos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("BREWING", true);
                    Fragment fragment = new MyExpressoFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                    transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
                }
            }
        });

        view.findViewById(R.id.continueShopping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        return view;
    }

    private void cancel() {
        Fragment fragment = getTargetFragment();
        if (fragment != null) {
            HomePageFragment homePageFragment = (HomePageFragment) fragment;
            homePageFragment.hideTint();
            dismiss();
        }
    }

}
