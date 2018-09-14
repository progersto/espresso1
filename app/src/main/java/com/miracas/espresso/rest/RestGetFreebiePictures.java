package com.miracas.espresso.rest;

import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.miracas.R;
import com.miracas.espresso.Application;
import org.json.JSONArray;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import static com.miracas.espresso.config.Settings.ENDPOINT_RAILS;
import static com.miracas.espresso.config.Settings.ENDPOINT_RAILS_SITE;
import static com.miracas.espresso.config.Settings.ENDPOINT_RAILS_SITE_LIVE;

public class RestGetFreebiePictures implements Response.Listener, Response.ErrorListener {
    public static final String IMG_URL = "img_url";
    public static final String POPUP_IMAGES = "popup_images";
    public static final String INVITED = "invited";
    public static final String COULDNT_INVITE = "couldnt_invite";
    public static final String SMS_RESPONSE = "sms_response";
    public static final String MESSAGE = "message";
    public static final String TYPE = "type";

    public static final String REST_GET_FREEBIE_PICTURE = "REST_GET_FREBIE_PICTURE";
    public static final String AUTHORIZATION = "Authorization";
    RestGetFreebiePictureInterface listener;
    int position = 0;

    public RestGetFreebiePictures(RestGetFreebiePictureInterface l) {
        listener = l;
    }

    public void getFreebieImages(String token, String keyword,int position) {
        this.position = position;
        String encodedKeyword = "";
        try {
            encodedKeyword = URLEncoder.encode(keyword,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedKeyword = keyword;
        }

        String url = "";
        if (ENDPOINT_RAILS.equals(R.string.rails_server_developemnt)) {
            url= ENDPOINT_RAILS_SITE+"/ZS/json1/get_products_espresso.php?keyword="+
                    encodedKeyword+"&start=1";
        }else{
            url = ENDPOINT_RAILS_SITE_LIVE+"/ZS/json1/get_products_espresso.php?keyword="+
                    encodedKeyword+"&start=1";
        }

        Log.d("IMAGES_","URL : "+url +" , "+position);

        HashMap<String, String> mRequestParams = new HashMap<String, String>();
        final JSONArray jsonArray = new JSONArray();

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,url,jsonArray,
                this,this){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put(AUTHORIZATION,token);
                return headers;
            }
        };

        Application.getInstance().cancelPendingRequest(REST_GET_FREEBIE_PICTURE+position);
        Application.getInstance().addToRequestQueue(req, REST_GET_FREEBIE_PICTURE+position);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("response", "err: "+ error.toString());
        listener.onGetFreebiePictureError(error);
    }

    @Override
    public void onResponse(Object response) {

        Log.e("response", "success: "+ response.toString());
        JSONArray jsonArray = (JSONArray) response;
        listener.onGetFreebiePicturesSuccess(jsonArray);

    }
}