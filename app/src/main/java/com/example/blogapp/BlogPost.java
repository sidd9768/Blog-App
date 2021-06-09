package com.example.blogapp;

import java.util.Date;

public class BlogPost extends BlogPostId {

    public String user_id, image_url, title, description, thumb_url, fullname;
    public Date timestamp;

    public BlogPost(){}

    public BlogPost(String user_id, String image_url, String title, String description, String thumb_url, String fullname, Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.title = title;
        this.description = description;
        this.thumb_url = thumb_url;
        this.timestamp = timestamp;
        this.fullname = fullname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFulluserName(){
        return fullname;
    }

    public void setFulluserName(String fulluserName){
        this.fullname = fulluserName;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
