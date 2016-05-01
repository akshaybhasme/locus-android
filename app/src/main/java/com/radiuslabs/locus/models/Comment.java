package com.radiuslabs.locus.models;

public class Comment {

    private int comment_id;
    private Timestamp comment_created_timestamp;
    private String comment_text;
    private Timestamp comment_updated_timestamp;
    private String user_id;

    public int getId() {
        return comment_id;
    }

    public void setId(int comment_id) {
        this.comment_id = comment_id;
    }

    public Timestamp getCreatedAt() {
        return comment_created_timestamp;
    }

    public void setCreatedAt(Timestamp comment_created_timestamp) {
        this.comment_created_timestamp = comment_created_timestamp;
    }

    public String getText() {
        return comment_text;
    }

    public void setText(String comment_text) {
        this.comment_text = comment_text;
    }

    public Timestamp getUpdatedAt() {
        return comment_updated_timestamp;
    }

    public void setUpdatedAt(Timestamp comment_updated_timestamp) {
        this.comment_updated_timestamp = comment_updated_timestamp;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

}
