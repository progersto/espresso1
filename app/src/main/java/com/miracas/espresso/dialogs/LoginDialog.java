package com.miracas.espresso.dialogs;


import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.miracas.espresso.activity.MainActivity;
import com.miracas.R;
import com.miracas.espresso.config.Settings;

public class LoginDialog extends DialogFragment {

    static OnLoginDialogListener loginDialogListenerS;

    public static LoginDialog newInstance(OnLoginDialogListener loginDialogListener) {
        loginDialogListenerS = loginDialogListener;
        return new LoginDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        View view = inflater.inflate(R.layout.dialog_login, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Bundle bundle = getArguments();
        if (bundle != null) {
            String quote = bundle.getString("QUOTE");
            TextView quoteView = view.findViewById(R.id.quote);
            quoteView.setText(quote);
            Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), Settings.CUSTOM_FONT);
            quoteView.setTypeface(customFont);
        }

        view.findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainActivity activity = (MainActivity) getActivity();
//                if (activity != null) {
//                    activity.initFacebookLogin();
//                }
                loginDialogListenerS.onLoginClicked();
                dismiss();
            }
        });

        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    public interface OnLoginDialogListener{
        void onLoginClicked();
    }

}
