package com.miracas.espresso.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alpesh on 8/22/2018.
 */

public class BronzResponse {

    @SerializedName("Images")
    @Expose
    public List<Image> images = null;

    public class Image {

        @SerializedName("CategoryName")
        @Expose
        public String categoryName;
        @SerializedName("Name")
        @Expose
        public String name;
        @SerializedName("Friends")
        @Expose
        public Integer friends;
        @SerializedName("Price")
        @Expose
        public Integer price;
        @SerializedName("IsSinlge")
        @Expose
        public Boolean isSinlge;
        @SerializedName("Image")
        @Expose
        public String image;
        @SerializedName("Image1")
        @Expose
        public String image1;
        @SerializedName("Image2")
        @Expose
        public String image2;
        @SerializedName("Image3")
        @Expose
        public String image3;

    }

}
