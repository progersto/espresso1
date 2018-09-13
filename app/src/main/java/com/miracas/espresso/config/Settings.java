package com.miracas.espresso.config;


import java.util.HashMap;
import java.util.Map;

public class Settings {

    public static final String CAROUSEL_TAG = "CAROUSEL_TAG";

    public static final String SELECTED_COLOR_CODE = "#f5f5f5";
    public static final String DEFAULT_COLOR_CODE = "#ffffff";

    public static String ENDPOINT_RAILS = "http://test-api.miracas.com";
    public static String ENDPOINT_RAILS_SITE = "http://test.miracas.com";
    public static String ENDPOINT_RAILS_SITE_LIVE = "http://miracas.com";
    public static String ENDPOINT_PRESTASHOP = "";

    public static final String CUSTOM_FONT = "fonts/geomanist.ttf";

    public static Map<String, String> SIZE_MAP;

    static {
        SIZE_MAP = new HashMap<>();
        SIZE_MAP.put("Extra Large", "XL");
        SIZE_MAP.put("Large", "L");
        SIZE_MAP.put("Medium", "M");
        SIZE_MAP.put("Small", "S");
        SIZE_MAP.put("Extra Small", "XS");
    }

}
