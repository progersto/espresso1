package com.miracas.espresso.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.facebook.AccessToken;
import com.miracas.R;
import com.miracas.espresso.utils.FacebookManager;
import com.miracas.espresso.utils.SharedStorage;

public class SplashScreenActivity extends AppCompatActivity {

    private FacebookManager facebookManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        facebookManager = new FacebookManager(this);
        handleSplash();
    }

    private void handleSplash() {
        View splash = findViewById(R.id.splash);

            Handler handler = new Handler();
            Runnable r = () -> {
                AlphaAnimation animation = new AlphaAnimation(1.0F, 0.0F);
                animation.setDuration(1000);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        new SharedStorage(SplashScreenActivity.this).showPreferences("onAnimationFinished");

                        Log.d("FB_","loginStatus: "+facebookManager.checkLoginStatus());

                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                        Log.d("FB_","loginStatus Alternative: "+isLoggedIn);

                        if (!facebookManager.checkLoginStatus()) {
                            startActivity(new Intent(getBaseContext(),WelcomeScreenActviity.class));
                        }else{
                            startActivity(new Intent(getBaseContext(),MainActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                splash.startAnimation(animation);
            };
            handler.postDelayed(r, 3000);

    }
}
