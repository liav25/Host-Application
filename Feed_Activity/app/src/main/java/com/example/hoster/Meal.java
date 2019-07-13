package com.example.hoster;

import java.io.Serializable;
import java.util.*;

/**
 * A class representing a meal event
 */
public class Meal implements Serializable {

    final private String hostId;
    private String ID;

    private ArrayList<String> guests;
    private ArrayList<String> guestsMails;
    private String title;
    private String description;
    private int maxGuests;
    private String time;
    private String location;
    private HashMap<String, Boolean> restrictions;
    private ArrayList<String> tags;
    //key is what needed, value is user who brings
    private HashMap<String, String> needed;

    public final static String NEEDED = "NEEDED";

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
    public Meal(String id, String hostId, String title, ArrayList<String> tags,
                HashMap<String, Boolean> restrictions, String descr,
                int maxGuests, String loc, String time, HashMap<String, String> mNeeded,
                ArrayList<String> guestsMails)
    {
        this.ID = id;
        this.hostId = hostId;
        this.title = title;
        this.description = descr;

        this.tags = new ArrayList<>();
        this.tags.addAll(tags);

        this.restrictions = new HashMap<>(restrictions);


        this.maxGuests = maxGuests; // the +1 is because the host is already included


        this.guests = new ArrayList<String>(); // initialize empty set of guests, then add host

        this.guests.add(hostId);

        this.location = loc;
        this.time = time;

        this.needed = mNeeded;
        this.guestsMails = guestsMails;
    }

    public Meal()
    {
        this.ID = ""+0;
        this.hostId = "host";
        this.title = "title";
        this.description = "descr";

        this.tags = new ArrayList<>();

        this.restrictions = new HashMap<>();


        this.maxGuests = 10; // the +1 is because the host is already included

        this.guests = new ArrayList<String>(); // initialize empty set of guests, then add host
        this.guestsMails = new ArrayList<>();
        this.location = "location";
        this.time = "time";

        this.needed = new HashMap<>();
        needed.put("beer", null);
        needed.put("drinks", null);
        needed.put("dessert", null);
        needed.put("flowers",null);
    }

    /**
     * @return this meal's host user ID
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * @return this meal's ID
     */
    public String getID(){
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
     * sets new id for this meal
     * @param id - new id
     */
    public void setId(String id) {
        this.ID = id;
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

    public ArrayList<String> getGuests() {
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
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * sets new tags for this event ( a whole new tag list)
     * @param tags - new tags
     */
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    /**
     * Adds a new guest to this event
     * @param guestId - ID of guest
     * @return - true upon success, false otherwise
     */
    public Boolean addGuest(String guestId){
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
    public Boolean removeGuest(String guestId)
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
    public Boolean isMember(String userId)
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

    /**
     * @return - true if meal is full. false otherwise
     */
    public Boolean isFull() { return curNumberOfGuests() >= maxGuests; }

    /**
     * @param restr - food restriction
     * @return true if meal contains restriction. false otherwise
     */
    public Boolean isRestricted(String restr){
        if (this.restrictions.containsKey(restr)){
            return this.restrictions.get(restr); // true or false depends on map
        }
        return false;  // not in map hence fits restrictions
    }


    public String toString(){
        return ("Title:"+title+" time:"+time+" Restrictions:"+restrictions.toString()
                +" maxNumber:"+maxGuests);
    }

    public HashMap<String, String> getNeeded() {
        return needed;
    }

    public void setNeeded(HashMap<String, String> needed) {
        this.needed = needed;
    }

    public void setGuestsMails(ArrayList<String> mails){
        this.guestsMails = mails;
    }

    public ArrayList<String> getGuestsMails(){return this.guestsMails; }


}
