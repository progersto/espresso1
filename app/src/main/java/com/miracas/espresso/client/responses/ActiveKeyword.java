package com.miracas.espresso.client.responses;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActiveKeyword extends SectionPageElement {

    public String id_keyword;
    public String keyword;
    public int booking_count;
    public double discount;
    public double current_discount;
    public int booking_threshold;
    public String genders;
    public String image;
    public String description;
    public String brand_link;

    private String created_at;
    private String updated_at;

    public String getBrandLinkOrKeyword() {
        if (brand_link != null && !brand_link.isEmpty())
            return brand_link;
        return keyword;
    }

    public Date getDate() {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.sss", Locale.ENGLISH);
        Date date = new Date();

        try {
            date = format.parse(created_at);
        } catch (Exception ignored) {}

        try {
            date = format.parse(updated_at);
        } catch (Exception ignored) {}

        return date;
    }

}
