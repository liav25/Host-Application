package com.example.feed_activity;

public class Card {
    private final int mealId;
    private Boolean isIn; // is userId in MealId
    private final int userId;
    private String title;
    private String description;
    public Card(int mealId, int userId, Boolean isIn) {
        this.mealId = mealId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.isIn = isIn;
    }

    /**
     * @return isIn
     */
    public Boolean getIn() {
        return isIn;
    }

    /**
     * @return - this card's meal id
     */
    public int getMealId() {
        return mealId;
    }

    /**
     * @return - this user's ID
     */
    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
