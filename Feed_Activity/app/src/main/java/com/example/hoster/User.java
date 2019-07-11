package com.example.hoster;

import java.util.ArrayList;

/**
 * A class representing a user
 */
public class User {
    private String username;
    private String image;
    private String userId;
    private String university;
    private ArrayList<String> langs;
    private ArrayList<String> mutual;
    private String email;
    private int num_of_raters;
    private int rating_sum;

    /**
     * A constructor for this class
     *
     * @param username   - username of user i.e. screen name
     * @param image      - profile_image for user
     * @param university - university studying in
     * @param langs      - languages speaking
     * @param email - user's email
     */
    public User(String username, String image, String university,
                ArrayList<String> langs, String userId, String email) {
        this.username = username;

        this.image = image;
        this.university = university;

        this.langs = new ArrayList<String>();
        this.langs.addAll(langs);
        this.mutual = new ArrayList<String>();

        this.userId = userId;
        this.rating_sum = 0;
        this.num_of_raters = 0;
        this.email = email;
    }

    public User(){
        this.username = "Profile";
        this.image = null;
        this.university = "University";
        this.langs = new ArrayList<String>();
        this.num_of_raters = 0;
        this.rating_sum = 0;
        this.mutual = new ArrayList<String>();
        this.email = "";
    }

    /**
     * @return number of raters of this user
     */
    public int getnum_of_raters() {
        return num_of_raters;
    }

    /**
     * @return rating sum of this user
     */
    public int getrating_sum() {
        return rating_sum;
    }

    /**
     * @param num - number of raters to set
     */
    public void setnum_of_raters(int num){
        num_of_raters = num;
    }

    /**
     * @param sum - sum of raters to set
     */
    public void setrating_sum(int sum){
        rating_sum = sum;
    }


    /**
     * @return profile_image of this user
     */
    public String getImage() {
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
    public void setImage(String image) {
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

    /**
     * Setter of username
     * @param us  - username to set
     */
    public void setUsername(String us){this.username = us;}

    /**
     * setter for mutual friends list
     * @param mutual - mutual friends list to set
     */
    public void setMutual(ArrayList<String> mutual) { this.mutual = mutual;}

    /**
     * @return - mutual friends list
     */
    public ArrayList<String> getMutual(){return this.mutual;}

    /**
     * @return - this user's email
     */
    public String getEmail(){ return this.email; }

    /**
     * @param email - set this email to be the user's email
     */
    public void setEmail(String email){
        this.email = email;
    }

}
