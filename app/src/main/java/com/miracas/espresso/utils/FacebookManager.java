package com.miracas.espresso.utils;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.miracas.R;
import com.miracas.espresso.client.NewUserClient;
import com.miracas.espresso.client.responses.FacebookUser;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class FacebookManager {

    private Activity activity;
    private LoginResult loginResult;

    public FacebookManager(Activity activity) {
        this.activity = activity;
    }

    public boolean checkLoginStatus() {
        SharedStorage storage = new SharedStorage(activity);

        String lastAccessedTime = storage.getValue(activity.getString(R.string.shared_storage_fb_last_accessed));

        String id = storage.getValue(activity.getString(R.string.shared_storage_user_id));
        String userID = storage.getValue(activity.getString(R.string.shared_storage_fb_user_id));
        String userName = storage.getValue(activity.getString(R.string.shared_storage_fb_user_name));
        String phone = storage.getValue(activity.getString(R.string.shared_storage_user_phone));
        String jwt = storage.getValue(activity.getString(R.string.shared_storage_jwt));

        Log.d("PREFERENCES","login status: id: "+id + ", FBuserID: " +userID +
                "+userName "+userName + ", phone "+phone + ", jwt: "+jwt);

        if (!id.isEmpty() && !userID.isEmpty() && !userName.isEmpty() && !phone.isEmpty() && !jwt.isEmpty()) {
            return true;
//            long facebookSessionTimeout =
//                    Long.valueOf(activity.getString(R.string.facebook_token_expiration_time));
//            if ((System.currentTimeMillis() - Long.valueOf(lastAccessedTime)) < facebookSessionTimeout ) {
//                storage.setValue(activity.getString(R.string.shared_storage_fb_last_accessed),
//                        String.valueOf(System.currentTimeMillis()));
//                return true;
//            }
        }
        return false;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public void saveUserSession(FacebookUser user, String id, String jwt, String phone) {
        String accessToken = loginResult.getAccessToken().getToken();
        String userID = loginResult.getAccessToken().getUserId();
        setValuesInStorage(accessToken, userID, user.profile_picture, user.name, user.email,id, jwt,phone);

        displayUserDetails();
        String gcmId = FirebaseInstanceId.getInstance().getToken();

        String education = "";
        String hometown = "";
        String location = "";
        if (user.location != null)
            location = user.location.name;

        if (user.hometown != null)
            hometown = user.hometown.name;
        try {
            if (user.education != null && !user.education.isEmpty()) {
                for (FacebookUser.EducationPlace place : user.education) {
                    education = education.concat(place.type).concat(" ").concat(place.school.name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();}

        String app_version_code = Build.VERSION.RELEASE;
        String os_version = android.os.Build.MODEL + " " + android.os.Build.PRODUCT;
        String os_api_level = String.valueOf(Build.VERSION.SDK_INT);
        String device_model = android.os.Build.MODEL + " " + android.os.Build.PRODUCT;
        String device_manufacturer = Build.MANUFACTURER;

        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), PackageManager.GET_META_DATA);
            app_version_code = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        NewUserClient newUserClient = new NewUserClient((IOnRequestCompleted) activity, userID, user.name,
                user.email, user.gender, gcmId, education, location, hometown,
        user.locale, app_version_code, os_version, os_api_level, device_model, device_manufacturer);

        newUserClient.execute();
    }

    public void displayUserDetails() {
        SharedStorage storage = new SharedStorage(activity);
        String name = storage.getValue(activity.getString(R.string.shared_storage_fb_user_name));
        String image = storage.getValue(activity.getString(R.string.shared_storage_fb_user_image));

        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        if(navigationView != null) {
            View hView = navigationView.getHeaderView(0);
            TextView userName = hView.findViewById(R.id.userName);
            userName.setText(name);
            ImageView userImage = hView.findViewById(R.id.userImage);
            Picasso.with(activity)
                    .load(image)
                    .transform(new CropCircleTransformation())
                    .fit()
                    .into(userImage);
        }
    }

    public void logout() {
        setValuesInStorage(null, null,
                "https://www.atomix.com.au/media/2015/06/atomix_user31.png",
                "Miracas", "", null, null, null);
        displayUserDetails();
    }

    private void setValuesInStorage(String accessToken, String userID, String profile_picture, String name,
                                    String email, String id, String jwt, String phone) {
        SharedStorage storage = new SharedStorage(activity);
        storage.setValue(activity.getString(R.string.shared_storage_fb_token), accessToken);
        storage.setValue(activity.getString(R.string.shared_storage_fb_user_id), userID);
        storage.setValue(activity.getString(R.string.shared_storage_fb_last_accessed),
                String.valueOf(System.currentTimeMillis()));
        storage.setValue(activity.getString(R.string.shared_storage_fb_user_image), profile_picture);
        storage.setValue(activity.getString(R.string.shared_storage_fb_user_name), name);
        storage.setValue(activity.getString(R.string.shared_storage_fb_user_email), email);

        storage.setValue(activity.getString(R.string.shared_storage_user_id),id);
        storage.setValue(activity.getString(R.string.shared_storage_jwt),jwt);
        storage.setValue(activity.getString(R.string.shared_storage_user_phone),phone);
    }

    public void setUserId(String userId) {
        SharedStorage storage = new SharedStorage(activity);
        storage.setValue(activity.getString(R.string.shared_storage_user_id), userId);
    }

}
