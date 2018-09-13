package com.miracas.espresso.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;


public class MiscUtils {

    public static void showPlayStorePage(Activity activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.miracas")));
        } catch (android.content.ActivityNotFoundException ignored) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.miracas")));
        }
    }

}
