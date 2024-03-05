package com.Farm.e_agric.Model;

import java.util.Date;

public class Post extends PostId {

    private String image , user , question;
    private Date time;

    public String getImage() {
        return image;
    }

    public String getUser() {
        return user;
    }

    public String getQuestion() {
        return question;
    }

    public Date getTime() {
        return time;
    }
}
