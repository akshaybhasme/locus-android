package com.radiuslabs.locus.restservices.responses;

import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.models.User;

import java.util.List;

public class NewsFeedResponse {

    private List<Story> stories;
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

}
