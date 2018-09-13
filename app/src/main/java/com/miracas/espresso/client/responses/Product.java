package com.miracas.espresso.client.responses;


import java.util.List;

public class Product {

    public boolean isSectionImage = false;
    public boolean isBrandLink = false;

    public String id_product_prestashop;
    public String id_product;
    public String name;
    public String discount;
    public String price;
    public List<String> colors;
    public List<String> sizes;
    public String img_url;
    public List<String> popup_images;

    public boolean addedToPot = false;

}
