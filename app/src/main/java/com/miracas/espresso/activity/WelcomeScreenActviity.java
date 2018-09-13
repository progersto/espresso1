package com.miracas.espresso.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.miracas.R;
import com.miracas.espresso.adapters.CardItem;
import com.miracas.espresso.adapters.CardPagerAdapter;
import com.miracas.espresso.client.FacebookUserClient;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.utils.FacebookManager;
import com.miracas.espresso.utils.RotateDownPageTransformer;
import com.miracas.espresso.widget.DButton;
import com.ravindu1024.indicatorlib.ViewPagerIndicator;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.miracas.espresso.activity.MainActivity.FIRST_RUN;


public class WelcomeScreenActviity extends AppCompatActivity {


    @BindView(R.id.vpSplash)
    ViewPager vpSplash;
    @BindView(R.id.indicatorSplash)
    ViewPagerIndicator indicatorSplash;
    @BindView(R.id.btn_login)
    DButton btnSplash;

    private CardPagerAdapter mCardAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        //pager - the target ViewPager
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_1));

        vpSplash.setAdapter(mCardAdapter);

        vpSplash.setPageTransformer(true, new RotateDownPageTransformer());
        indicatorSplash.setPager(vpSplash);
        vpSplash.setOffscreenPageLimit(3);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        width = width * 10 / 720;

        vpSplash.setPageMargin(width);

    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        Intent intent = new Intent(getBaseContext(), MobileVerificationActviity.class);
        startActivity(intent);
        finish();
    }
}
