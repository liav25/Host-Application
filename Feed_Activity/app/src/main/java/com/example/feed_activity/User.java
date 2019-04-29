package com.example.feed_activity;

import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A class representing a user
 */
public class User {
    final private String username;
    private String pass;
    private Files image;
    private final int userId;
    private Set<Integer> eventsAttending;
    private String university;
    private Set<String> langs;
    private Set<String> fieldsOfInterest;
    private final Feed feed;

    /**
     * A constructor for this class
     * @param username - username of user i.e. screen name
     * @param pass - password of user
     * @param image - image for user
     * @param university - university studying in
     * @param langs - languages speaking
     * @param fieldsOfInterest - Fields of interest for user
     */
    public User(String username, String pass, Files image, String university,
                Set<String> langs, Set<String> fieldsOfInterest, int userId)
    {
        this.username = username;
        this.pass = pass;

        this.image = image;
        this.university = university;

        this.langs = langs;

        this.fieldsOfInterest = fieldsOfInterest;

        this.eventsAttending = new HashSet<Integer>();

        this.userId = userId;

        this.feed = new Feed(userId, new HashSet<Integer>()); // currently no restrictions
    }

    /**
     * @return image of this user
     */
    public Files getImage() {
        return image;
    }

    /**
     * @return events set in which this user attends
     */
    public Set<Integer> getEventsAttending() {
        return eventsAttending;
    }

    /**
     * @return set of interest this user tagged himself
     */
    public Set<String> getFieldsOfInterest() {
        return fieldsOfInterest;
    }

    /**
     * @return - set of languages this user claims to speak
     */
    public Set<String> getLangs() {
        return langs;
    }

    /**
     * @return password of this user
     */
    public String getPass() {
        return pass;
    }

    /**
     * @return univesity of this user
     */
    public String getUniversity() {
        return university;
    }

    /**
     * @return - username for this user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Asks to join an event
     * @param eventId - id of event
     */
    public void joinEvent(int eventId)
    {
        this.eventsAttending.add(eventId);
    }

    /**
     * Asks to leave an event
     * @param eventId - id of event
     */
    public void leaveEvent(int eventId)
    {
        this.eventsAttending.add(eventId);
    }

    /**
     * Sets a new set of interest for this user
     * @param fieldsOfInterest - new set of interests
     */
    public void setFieldsOfInterest(Set<String> fieldsOfInterest) {
        this.fieldsOfInterest = fieldsOfInterest;
    }

    /**
     * @param image - new image
     */
    public void setImage(Files image) {
        this.image = image;
    }

    /**
     * @param langs - new languages
     */
    public void setLangs(Set<String> langs) {
        this.langs = langs;
    }

    /**
     * @param pass - new password
     */
    public void setPass(String pass) {
        //TODO - validation!!!!!!!!!!!
        this.pass = pass;
    }

    /**
     * Sets a new university for user
     * @param university - new university
     */
    public void setUniversity(String university) {
        this.university = university;
    }

    /**
     * @return userId for this user
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return feed for this user
     */
    public Feed getFeed() {
        return feed;
    }

    /**
     * @param newResults - new feed results to update
     */
    public void updateFeed(HashSet<Integer> newResults) {
        this.feed.update(newResults);
    }

}
