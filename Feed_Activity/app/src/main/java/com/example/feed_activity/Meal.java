package com.example.feed_activity;

import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.*;

/**
 * A class representing a meal event
 */
public class Meal {

    final private int hostId;
    private final int ID;
    private HashSet<Integer> guests;
    private String title;
    private String description;
    private int maxGuests;
    private String time;
    private String location;
    private HashMap<String, Boolean> restrictions;
    private HashSet<String> tags;

    /**
     * constructor
     * @param id - meal ID (for server)
     * @param hostId - User Id of host
     * @param title - Title for meal (created by host)
     * @param tags - tags included in meal
     * @param restrictions - food restrictions included - <Restriction, boolVal>
     * @param descr - description for meal (optional, created by host)
     * @param maxGuests - maximal number of guests determined by host (hence can be changed)
     * @param loc - Location for event
     * @param time - time of event
     */
    public Meal(int id, int hostId, String title, HashSet<String> tags,
                HashMap<String, Boolean> restrictions, String descr,
                int maxGuests, String loc, String time)
    {
        this.ID = id;
        this.hostId = hostId;
        this.title = title;
        this.description = descr;

        this.tags = new HashSet<>();
        this.tags.addAll(tags);

        this.restrictions = new HashMap<>();
        this.restrictions.putAll(restrictions);

        this.maxGuests = maxGuests; // the +1 is because the host is already included

        this.guests = new HashSet<Integer>(); // initialize empty set of guests, then add host
        this.guests.add(hostId);

        this.location = loc;
        this.time = time;
    }
    /**
     * @return this meal's host user ID
     */
    public int getHostId() {
        return hostId;
    }

    /**
     * @return this meal's ID
     */
    public int getID(){
        return ID;
    }


    /**
     * @return this meal's maximal number of guests
     */
    public int getMaxGuests(){
        return maxGuests;
    }

    /**
     * changes the number of guests allowed in event
     * @param newLim - new number limit of guests
     */
    public void setMaxGuests(int newLim)
    {
        maxGuests = newLim;
    }

    /**
     * @return this event's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * sets new title for this meal
     * @param title - new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return this meal description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a new description for this meal
     * @param description - new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return - set of guests
     */
    public Set<Integer> getGuests() {
        return guests;
    }

    /**
     * @return - current number of guests in event (not including host)
     */
    public int curNumberOfGuests()
    {
        return guests.size() - 1;
    }

    /**
     * @return time of Meal
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets a new date for the meal
     * @param time - new time for meal
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return - Location of event
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets a new location for this event
     * @param location - new location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return food restrictions mentioned for this meal
     */
    public Map<String, Boolean> getRestrictions() {
        return restrictions;
    }

    /**
     * sets new restrictions for this meal
     * @param restrictions - new restrictions
     */
    public void setRestrictions(HashMap<String, Boolean> restrictions) {
        this.restrictions = restrictions;
    }

    /**
     * @return - tags tagged by host for this meal
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * sets new tags for this event ( a whole new tag list)
     * @param tags - new tags
     */
    public void setTags(HashSet<String> tags) {
        this.tags = tags;
    }

    /**
     * Adds a new guest to this event
     * @param guestId - ID of guest
     * @return - true upon success, false otherwise
     */
    public Boolean addGuest(int guestId){
        if (curNumberOfGuests() < maxGuests){
            this.guests.add(guestId);
            return true;
        }
        return false; // too many guests
    }


    /**
     * Removes a  guest from this event
     * @param guestId - ID of guest
     * @return - true upon success, false otherwise
     */
    public Boolean removeGuest(int guestId)
    {
        if (this.guests.contains(guestId))
        {
            this.guests.remove(guestId);
            return true;
        }
        return false;
    }

    /**
     * @param userId - user Id to check
     * @return true if user is in this meal, false otherwise
     */
    public Boolean isMember(int userId)
    {
        return this.guests.contains(userId);
    }

    /**
     * Validates whether this meal is suitable for the given restrictions
     * @param rest - restriction map <restriction, true\false>
     * @return - true or false according to result
     */
    public Boolean isSuitableWithRestrictions(HashMap<String, Boolean> rest)
    {
        for (String restriction : rest.keySet())
        {
            if (rest.get(restriction) && !isRestrictionTrue(restriction)){
                return false;
            }
            // if one of the restrictions is true in rest but not true in this meal,
            // then meal is not suitable
        }
        return true;
    }

    /*
    checks whether a certain restriction applies to this meal
     */
    private Boolean isRestrictionTrue(String restriction){
        return this.restrictions.get(restriction);
    }

    /**
     * @param tag - tag to check
     * @return true if this meal contains the given tag
     */
    public Boolean isTagged(String tag){
        return tags.contains(tag);
    }

    /** @return  true if meal is not full yet*/
    public Boolean isFull() { return guests.size() < maxGuests;}

}
