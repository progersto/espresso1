package com.miracas.espresso.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopModel {

    public static final int SUBTYPE_ALL = -1;
    public static final int SUBTYPE_BEGGINER = 0;
    public static final int SUBTYPE_BRONZE = 1;
    public static final int SUBTYPE_SILVER = 2;
    public static final int SUBTYPE_GOLD = 3;
    public static final int SUBTYPE_PLATINUM = 4;

    public static final String KEYWORDS = "keywords";
    public static final String ID_KEYWORD = "id_keyword";
    public static final String KEYWORD = "keyword";
    public static final String IMAGE = "image";
    public static final String DESCRIPTION = "description";
    public static final String BOOKING_THRESHOLD = "booking_threshold";
    public static final String KEYWORD_TYPE = "keyword_type";
    public static final String KEYWORD_SUBTYPE = "keyword_subtype";
    public static final String CURRENT_DISCOUNT = "current_discount";
    public static final String FREEBIE_LOCK = "freebie_lock";

    String keyword, image, image1,image2,image3,image4, description, keyword_type;
    boolean locked, images_obtained;
    float price1, discount;
    int subtype,booking_threshold,keyword_subtype,keyword_id;


    public static ShopModel initShopModel(JSONObject jsonObject){
        ShopModel shopModel = new ShopModel();

        try {
            shopModel.setKeyword(jsonObject.getString(KEYWORD));
            shopModel.setKeyword_id(jsonObject.getInt(ID_KEYWORD));
            shopModel.setImage(jsonObject.getString(IMAGE));
            shopModel.setDescription(jsonObject.getString(DESCRIPTION));
            shopModel.setBooking_threshold(jsonObject.getInt(BOOKING_THRESHOLD));
            shopModel.setKeyword_type(jsonObject.getString(KEYWORD_TYPE));
            shopModel.setKeyword_subtype(jsonObject.getInt(KEYWORD_SUBTYPE));
            shopModel.setDIscount(jsonObject.getInt(CURRENT_DISCOUNT));
            shopModel.setLocked(jsonObject.getBoolean(FREEBIE_LOCK));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


        return shopModel;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeyword_type() {
        return keyword_type;
    }

    public void setKeyword_type(String keyword_type) {
        this.keyword_type = keyword_type;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public int getBooking_threshold() {
        return booking_threshold;
    }

    public void setBooking_threshold(int booking_threshold) {
        this.booking_threshold = booking_threshold;
    }

    public int getKeyword_subtype() {
        return keyword_subtype;
    }

    public void setKeyword_subtype(int keyword_subtype) {
        this.keyword_subtype = keyword_subtype;
    }

    public int getKeyword_id() {
        return keyword_id;
    }

    public void setKeyword_id(int keyword_id) {
        this.keyword_id = keyword_id;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public float getPrice1() {
        return price1;
    }

    public void setPrice1(float price1) {
        this.price1 = price1;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDIscount(float discount) {
        this.discount = discount;
    }

    public boolean isImages_obtained() {
        return images_obtained;
    }

    public void setImages_obtained(boolean images_obtained) {
        this.images_obtained = images_obtained;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
