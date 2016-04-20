package com.radiuslabs.locus.models;

import java.util.ArrayList;
import java.util.List;

public class Story {

    private String type = "image";
    private String content_url = "http://www.odysseyart.net/Entertainment/EntertainmentFiles/BatmanRainyKnight-12x18print_thb.jpg";
    private String content_text;
    private List<Double> story_location;
    private String topic_id = "1";
    private String user_id;
    private List<Comment> story_comments;
    private List<String> story_hashtags;
    private List<String> likes;

    public Story() {
        story_location = new ArrayList<>();
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public String getContent_text() {
        return content_text;
    }

    public void setContent_text(String content_text) {
        this.content_text = content_text;
    }

    public List<Double> getLocation() {
        return story_location;
    }

    public void setLocation(double latitude, double longitude) {
        this.story_location.clear();
        this.story_location.add(latitude);
        this.story_location.add(longitude);
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<Comment> getComments() {
        return story_comments;
    }

    public void setComments(List<Comment> comments) {
        this.story_comments = comments;
    }

    public List<String> getHashtags() {
        return story_hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.story_hashtags = hashtags;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

}
