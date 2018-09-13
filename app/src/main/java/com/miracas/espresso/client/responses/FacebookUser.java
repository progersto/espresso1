package com.miracas.espresso.client.responses;


import java.util.List;

public class FacebookUser {

    public String id;
    public String name;
    public String email;
    public String gender;
    public String birthday;
    public String profile_picture;
    public String age_range_min;
    public String age_range_max;
    public String locale;

    public Entity location;
    public Entity hometown;

    public List<EducationPlace> education;


    public class EducationPlace {
        public Entity school;
        public String type;
        public String id;
    }

    public class Entity {
        public String id;
        public String name;
    }
}
