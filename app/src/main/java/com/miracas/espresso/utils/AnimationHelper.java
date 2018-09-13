package com.miracas.espresso.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AnimationHelper {



    public static void startAnimation(View v){
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(500);
        animation1.setStartOffset(250);
        v.startAnimation(animation1);
    }

    public static void startPulseAnimation(View v) {
        final AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        // Change alpha from fully visible to invisible
        animation.setDuration(1000); // duration 250 milliseconds
        // animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        v.startAnimation(animation);


    }

//To Stop Animation, simply use cancel method of ObjectAnimator object as below.

    public static void stopPulseAnimation(View v) {
        //animation.cancel();
        v.clearAnimation();
    }

}
