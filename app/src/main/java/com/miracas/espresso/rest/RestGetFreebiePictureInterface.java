package com.miracas.espresso.rest;

import com.android.volley.VolleyError;
import org.json.JSONArray;

public interface RestGetFreebiePictureInterface {
    void onGetFreebiePicturesSuccess(JSONArray response);
    void onGetFreebiePictureError(VolleyError error);
}
