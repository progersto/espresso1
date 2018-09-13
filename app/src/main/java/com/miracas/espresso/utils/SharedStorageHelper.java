package com.miracas.espresso.utils;


import android.app.Activity;
import android.util.Log;

import com.miracas.R;

public class SharedStorageHelper {

    private SharedStorage storage;

    public SharedStorageHelper(Activity activity) {
        this.storage = new SharedStorage(activity);
    }

    public String getGender() {
        return storage.getValue("gender");
    }

    public void setGender(String gender) {
        storage.setValue("gender", gender);
    }

    public void setStorageAccessPermitted(boolean permitted) {
        String value = "no";
        if (permitted)
            value = "yes";
        storage.setValue("storage_access_permitted", value);
    }

    public boolean getStorageAccessPermitted() {
        String value;
        value = storage.getValue("storage_access_permitted");
        return value != null && !value.isEmpty() && value.equals("yes");
    }

    public void setCODLimit(String upperLimit, String lowerLimit) {
        storage.setValue("storage_cod_upper_limit", upperLimit);
        storage.setValue("storage_cod_lower_limit", lowerLimit);
    }

    public double getCODUpperLimit() {
        String string = storage.getValue("storage_cod_upper_limit");
        if (string == null || string.isEmpty())
            string = "0";
        return Double.valueOf(string);
    }

    public double getCODLowerLimit() {
        String string = storage.getValue("storage_cod_lower_limit");
        if (string == null || string.isEmpty())
            string = "0";
        return Double.valueOf(string);
    }

    public int getAppOpens() {
        int opens = 0;
        String string = storage.getValue("storage_app_opens");
        if (string != null && !string.isEmpty())
            opens = Integer.valueOf(string);
        return opens;
    }

    public void setAppOpens(int opens) {
        storage.setValue("storage_app_opens", String.valueOf(opens));
    }

    public int getReviewThreshold() {
        int threshold = 10;
        String string = storage.getValue("storage_review_threshold");
        if (string != null && !string.isEmpty())
            threshold = Integer.valueOf(string);
        return threshold;
    }

    public void setReviewThreshold(int threshold) {
        storage.setValue("storage_review_threshold", String.valueOf(threshold));
    }

    public String getUserID() {
        return storage.getValue("shared_storage_user_id");
    }

    public void setUserVisitedNonEmptyCart() {
        storage.setValue("setUserVisitedNonEmptyCart", "yes");
    }

    public boolean getUserVisitedNonEmptyCart() {
        String result = storage.getValue("setUserVisitedNonEmptyCart");
        return result != null && !result.isEmpty() && result.equals("yes");
    }

    public void setUserAddedProduct() {
        storage.setValue("setUserAddedProduct", "yes");
    }

    public boolean getUserAddedProduct() {
        String result = storage.getValue("setUserAddedProduct");
        return result != null && !result.isEmpty() && result.equals("yes");
    }

}
