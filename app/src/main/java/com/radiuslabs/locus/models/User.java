package com.radiuslabs.locus.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private MongoId _id;

    private String email;

    private String first_name;
    private String last_name;
    private String gender;
    private String username;
    private String password;
    private List<Interest> interests;
    private String profile_pic;

    public String get_id() {
        return _id.get$oid();
    }

    public void set_id(MongoId _id) {
        this._id = _id;
    }

    public String getEmail_id() {
        return email;
    }

    public void setEmail_id(String email_id) {
        this.email = email_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFullName() {
        return first_name + " " + last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUser_name() {
        return username;
    }

    public void setUser_name(String user_name) {
        this.username = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public static class Interest implements Serializable {

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
