package com.example.hoster;

import android.net.Uri;

import java.util.ArrayList;

/**
 * A class representing a user
 */
public class User {
    private String username;
    private Uri image;
    private String userId;
    private String university;
    private ArrayList<String> langs;


    /**
     * A constructor for this class
     *
     * @param username   - username of user i.e. screen name
     * @param image      - profile_image for user
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

    public User(){
        this.username = "Profile";
        this.image = null;
        this.university = "University";
        this.langs = new ArrayList<String>();

    }

    /**
     * @return profile_image of this user
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
     * @param image - new profile_image
     */
    public void setImage(Uri image) {
        this.image = image;
    }

    /**
     * @param langs - new languages
     */
    public void setLangs(ArrayList<String> langs) {
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

    /**
     * @return userId for this user
     */
    public void setUserId(String uid) {
        this.userId = uid;
    }

    public void setUsername(String us){this.username = us;}
}
