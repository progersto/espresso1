package com.miracas.espresso.model;

import android.os.Parcelable;

import com.miracas.espresso.client.responses.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductModel implements Serializable {

    public static final String PRODUCT = "PRODUCT";
    public static final String ID_PRODUCT_PRESTASHOP = "id_product_prestashop";
    public static final String ID_PRODUCT = "id_product";
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String DISCOUNT = "discount";
    public static final String COLORS = "colors";
    public static final String SIZES = "sizes";
    public static final String IMG_URL = "img_url";
    public static final String POPUP_IMAGES = "popup_images";
    public static final String LOVE_COUNT = "love_count";
    public static final String LAST_START_COUNT = "last_start_count";

    String prestashopId,productId,name, img_url;
    float price,discount, love_count,last_start_count;
    ArrayList<String> sizes, colors, popup_images;

    public static ProductModel initProductModel(JSONObject jsonObject){
            ProductModel productModel = new ProductModel();
            productModel.sizes = new ArrayList<>();
            productModel.colors = new ArrayList<>();
            productModel.popup_images = new ArrayList<>();

        try {
            productModel.setPrice(Float.parseFloat(jsonObject.getString(PRICE)));
            productModel.setName(jsonObject.getString(NAME));
            productModel.setDiscount(Float.parseFloat(jsonObject.getString(DISCOUNT)));
            productModel.setProductId(jsonObject.getString(ID_PRODUCT));
            productModel.setPrestashopId(jsonObject.getString(ID_PRODUCT_PRESTASHOP));
            productModel.setImg_url(jsonObject.getString(IMG_URL));

            JSONArray sizesJson = jsonObject.getJSONArray(SIZES);
            JSONArray colorsJson = jsonObject.getJSONArray(COLORS);
            JSONArray popupImagesJson = jsonObject.getJSONArray(POPUP_IMAGES);

            for(int x = 0; x < sizesJson.length(); x++){
                productModel.sizes.add(sizesJson.getString(x));
            }

            for(int y = 0; y < colorsJson.length(); y++){
                productModel.colors.add(colorsJson.getString(y));
            }

            for(int z = 0; z < popupImagesJson.length(); z++ ){
                productModel.popup_images.add(popupImagesJson.getString(z));
            }

            return productModel;
        } catch (JSONException e) {
            return null;
        }
    }

    public String getPrestashopId() {
        return prestashopId;
    }

    public void setPrestashopId(String prestashopId) {
        this.prestashopId = prestashopId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getLove_count() {
        return love_count;
    }

    public void setLove_count(float love_count) {
        this.love_count = love_count;
    }

    public float getLast_start_count() {
        return last_start_count;
    }

    public void setLast_start_count(float last_start_count) {
        this.last_start_count = last_start_count;
    }

    public void addSize(String size){
        sizes.add(size);
    }

    public ArrayList<String> getSizes() {
        return sizes;
    }

    public void setSizes(ArrayList<String> sizes) {
        this.sizes = sizes;
    }

    public void addColor(String color){
        colors.add(color);
    }

    public ArrayList<String> getColor() {
        return colors;
    }

    public void setColor(ArrayList<String> color) {
        this.colors = color;
    }

    public void addImages(String image){
        this.popup_images.add(image);
    }

    public ArrayList<String> getPopup_images() {
        return popup_images;
    }

    public void setPopup_images(ArrayList<String> popup_images) {
        this.popup_images = popup_images;
    }
}
