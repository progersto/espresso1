package com.miracas.espresso.dialogs;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miracas.R;

import static android.app.Activity.RESULT_OK;

public class SelectGenderDialog extends DialogFragment {

    public static final String ACTION = "ACTION";

    public static SelectGenderDialog newInstance() {
        return new SelectGenderDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setCancelable(false);
        View view = inflater.inflate(R.layout.dialog_select_gender, container, false);

        view.findViewById(R.id.male).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnSelection("male");
            }
        });

        view.findViewById(R.id.female).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnSelection("female");
            }
        });

        view.findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnSelection("facebook");
            }
        });

        return view;
    }

    private void returnSelection(String selection) {
        Fragment fragment = getTargetFragment();
        if (fragment != null) {
            Intent intent = new Intent();
            intent.putExtra(ACTION, selection);
            fragment.onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
            dismiss();
        }
    }

}
