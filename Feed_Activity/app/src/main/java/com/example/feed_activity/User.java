package com.example.feed_activity;

import android.media.Image;
import android.net.Uri;

import java.nio.file.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A class representing a user
 */
public class User {
    final private String username;
    private Uri image;
    private final String userId;
    private String university;
    private ArrayList<String> langs;


    /**
     * A constructor for this class
     *
     * @param username   - username of user i.e. screen name
     * @param image      - image for user
     * @param university - university studying in
     * @param langs      - languages speaking
     */
    public User(String username, Uri image, String university,
                ArrayList<String> langs, String userId) {
        this.username = username;

        this.image = image;
        this.university = university;

        this.langs = new ArrayList<String>();
        this.langs.addAll(langs);

        this.userId = userId;

    }

    /**
     * @return image of this user
     */
    public Uri getImage() {
        return image;
    }


    /**
     * @return - set of languages this user claims to speak
     */
    public ArrayList<String> getLangs() {
        return langs;
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
     * @param image - new image
     */
    public void setImage(Uri image) {
        this.image = image;
    }

    /**
     * @param langs - new languages
     */
    public void setLangs(Set<String> langs) {
        this.langs.clear();
        this.langs.addAll(langs);
    }


    /**
     * Sets a new university for user
     *
     * @param university - new university
     */
    public void setUniversity(String university) {
        this.university = university;
    }

    /**
     * @return userId for this user
     */
    public String getUserId() {
        return userId;
    }


}
