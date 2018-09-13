package com.miracas.espresso.dialogs;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.miracas.R;

import static android.app.Activity.RESULT_OK;

public class RemoveWebProductsDialog extends DialogFragment {

    public static RemoveWebProductsDialog newInstance() {
        return new RemoveWebProductsDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        setCancelable(true);
        View view = inflater.inflate(R.layout.dialog_remove_web_products, container, false);

        view.findViewById(R.id.remove).setOnClickListener(view1 -> {
            Fragment fragment = getTargetFragment();
            if (fragment != null) {
                fragment.onActivityResult(getTargetRequestCode(), RESULT_OK, null);
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LinearLayoutCompat.LayoutParams.MATCH_PARENT;
        params.height = LinearLayoutCompat.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

}
