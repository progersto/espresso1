package com.miracas.espresso.activity.contacts;

import java.io.Serializable;

public class User implements Serializable {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DATA = "data";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String AVATAR = "avatar";

    String id;
    String name;
    String surnme;
    String url;
    int age;
    boolean selected;
    String message_id;
    String last_picture_url;
    String phone;

    public User(){}

    public User(String name, String surname, String url, String id, String number){
        this.name = name;
        this.surnme = surname;
        this.url = url;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurnme() {
        return surnme;
    }

    public void setSurnme(String surnme) {
        this.surnme = surnme;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getLast_picture_url() {
        return last_picture_url;
    }

    public void setLast_picture_url(String last_picture_url) {
        this.last_picture_url = last_picture_url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return getName() + " : "+getPhone();
    }
}
