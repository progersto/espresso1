package com.miracas.espresso.client.responses;


import java.util.List;

public class Order {

    public String id_order;
    public String date_order;
    public List<Product> products;

    public class Product {
        public String id_order;
        public String date_order;
        public String order_type;

        public String id_product;
        public String color;
        public String size;
        public String qty;
        public String img_url;
        public String product_name;
        public String price;
    }

}
