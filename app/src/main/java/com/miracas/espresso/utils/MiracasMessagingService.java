package com.miracas.espresso.utils;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.miracas.espresso.activity.MainActivity;
import com.miracas.R;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class MiracasMessagingService extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            try {

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1");
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
                mBuilder.setSmallIcon(R.drawable.app_logo);
                mBuilder.setContentTitle(remoteMessage.getData().get("title"));
                mBuilder.setContentText(remoteMessage.getData().get("body"));
                mBuilder.setAutoCancel(true);

                if (remoteMessage.getData().get("event").equals("brewed")) {

                    Intent intent2 = new Intent(this, MainActivity.class);
                    intent2.putExtra("event", "brewed");
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            getApplicationContext(), 100, intent2,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(pendingIntent);
                }

                String imageURL = remoteMessage.getData().get("image");
                if (imageURL != null && !imageURL.isEmpty()) {
                    try {
                        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
                        style.bigPicture(Picasso.with(getBaseContext())
                                .load(imageURL)
                                .resize(800, 400)
                                .centerCrop()
                                .get());
                        mBuilder.setStyle(style);
                    } catch (Exception ignored) {}
                }

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (mNotificationManager != null)
                    mNotificationManager.notify(0, mBuilder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "Message Notification Payload: " + remoteMessage.getNotification());
        }
    }

}
