package com.miracas.espresso.dialogs;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.miracas.R;
import com.miracas.espresso.utils.MiscUtils;
import com.miracas.espresso.utils.SharedStorageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ReviewDialog extends DialogFragment {

    List<ImageView> stars;
    TextView postButton;

    boolean rated = false;
    int rating;

    public static ReviewDialog newInstance() {
        return new ReviewDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        View view = inflater.inflate(R.layout.dialog_review, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        stars = new ArrayList<>();
        stars.add(view.findViewById(R.id.star1));
        stars.add(view.findViewById(R.id.star2));
        stars.add(view.findViewById(R.id.star3));
        stars.add(view.findViewById(R.id.star4));
        stars.add(view.findViewById(R.id.star5));

        int i = 1;
        for (View star : stars) {
            int finalI = i;
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setStar(finalI);
                }
            });
            i++;
        }

        postButton = view.findViewById(R.id.post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rated) {
                    SharedStorageHelper helper = new SharedStorageHelper(getActivity());
                    helper.setReviewThreshold(20);

                    if (rating >= 4) {
                        MiscUtils.showPlayStorePage(getActivity());
                        dismiss();

                    } else {
                        Toast.makeText(getContext(), "Thank you for the response", Toast.LENGTH_LONG).show();
                        dismiss();

                    }
                }
            }
        });

        return view;
    }

    public void setStar(int count) {
        if (!rated || count > rating) {
            int i = 1;
            for (ImageView star : stars) {
                if (i <= count)
                    star.setImageResource(R.drawable.star_full);
                else
                    star.setImageResource(R.drawable.star_empty);
                i++;
            }

            if (!rated) {
                if (count >= 4) {
                    postButton.setText("Submit on Play Store");
                } else {
                    postButton.setText("POST");
                }
            }

            rating = count;
            rated = true;
            postButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        try {
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = LinearLayoutCompat.LayoutParams.MATCH_PARENT;
            params.height = LinearLayoutCompat.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        } catch (Exception ignored) {}
        super.onResume();
    }

}
