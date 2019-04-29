package com.example.feed_activity;

import android.location.Location;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Simulates a server for POC
 */
public class Server {
    private HashMap<Integer, User> users;
    private ArrayList<Meal> meals;
    private static Server instance = new Server();
    private int userCount;
    private int mealCount;
    private Set<String> standardRestrictions;

    /**
     * Constructor
     */
    private Server()
    {
        this.users = new HashMap<Integer, User>();
        this.meals = new ArrayList<Meal>();
        userCount = 0;
        mealCount = 0;
        setStandardRestrictions();
    }

    /*
    Sets a standard set of food restrictions
     */
    private void setStandardRestrictions()
    {
        standardRestrictions = new HashSet<String>();

        standardRestrictions.add("Halal");
        standardRestrictions.add("Kosher");
        standardRestrictions.add("Vegan");
        standardRestrictions.add("Vegetarian");
        standardRestrictions.add("Allergenic");
        standardRestrictions.add("Non-Dairy");
    }

    /**
     * @return singleton instance of this class
     */
    public static Server getInstance() {
        return instance;
    }

    /**
     * Creates a new user and adds it to DB
     * @param username - username
     * @param pass - his password
     * @param image - his image
     * @param university - his university
     * @param langs - languages he's speaking
     * @param fieldsOfInterest - his fields of interest
     */
    public int addUser(String username, String pass, Files image, String university,
                        Set<String> langs, Set<String> fieldsOfInterest)
    {

        User newUser = new User(username, pass, image, university, langs,
                fieldsOfInterest, userCount);
        users.put(userCount, newUser);

        userCount++;
        return userCount - 1;
    }

    /**
     * @param userId - removes user from DB upon request
     */
    public void removeUser(int userId)
    {
        users.remove(userId);
    }

    /**
     * adds new meal to DB
     * @param hostId - the host's ID
     * @param title - the title of this meal
     * @param tags - additional taggs for this meal
     * @param restrictions - food restrictions for this meal
     * @param descr - description
     * @param maxGuests - maximum number of guests for this meal
     * @param loc - location for this meal
     * @param time - time and date of meal
     */
    public int addMeal(int hostId, String title, HashSet<String> tags,
                        HashMap<String, Boolean> restrictions, String descr,
                        int maxGuests, String loc, String time)
    {
        Meal newMeal = new Meal(mealCount,  hostId,  title,tags,
                restrictions, descr, maxGuests,  loc,  time);

        meals.add(newMeal);
        mealCount++;
        return mealCount - 1;
    }

    /**
     * removes a meal whose id is id
     * @param id - meal id to remove
     */
    public void removeMeal(int id)
    {
        meals.remove(id);
    }

    /**
     * update feed upon request
     * @param userId - user to update feed for
     * @param restrictions - food restrictions applied

     */
    public void updateFeedRequest(int userId, HashMap<String, Boolean> restrictions)
    {
        HashSet<Integer> results = new HashSet<Integer>();

        for (Meal meal : meals){ // check every existing meal for restrictions
            if(meal.isSuitableWithRestrictions(restrictions)){
                results.add(meal.getID());
            }
        }

        users.get(userId).updateFeed(results);
    }

    /**
     * checks if user is a member of a meal
     * @param userId - user id to check
     * @param mealId - meal to check
     * @return true or false according to result
     */
    public Boolean isUserInMeal(int userId, int mealId)
    {
        if (this.meals.contains(mealId)) {
            return this.meals.get(mealId).isMember(userId);
        }
        return false;
    }

    /**
     * adds a user to a meal
     * @param userId   user's ID
     * @param mealId meal's ID
     * @return true upon success, false otherwise
     */
    public Boolean addUserToMeal(int userId, int mealId){
        if (meals.contains(mealId) && users.containsKey(userId)){
            return meals.get(mealId).addGuest(userId);
        }
        return false; // failed to add
    }

    /**
     * removes a user from a meal
     * @param userId   user's ID
     * @param mealId meal's ID
     * @return true upon success, false otherwise
     */
    public Boolean removeUserToMeal(int userId, int mealId){
        if (meals.contains(mealId) && users.containsKey(userId)){
            return meals.get(mealId).removeGuest(userId);
        }
        return false; // failed to add
    }

    /**
     * @return the set of standard restrictions
     */
    public Set<String> getStandardRestrictions() {
        return standardRestrictions;
    }

    /**
     * @param UserID - userID
     * @return the user whose id is UserID, null otherwise
     */
    public User getUser(int UserID){
        if (users.containsKey(UserID)){
            return users.get(UserID);
        }
        return null;
    }


    public Meal getMeal(int mId)
    {
        return this.meals.get(mId);
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }


}
