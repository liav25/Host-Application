package com.example.feed_activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * class including all the data members and fields relevant for a feed
 */
public class Feed {
    private Set<Card> cards;
    private final int userId;
    private Map<Integer, Boolean> results; // <id, visibility>
    // what users sees according to params. contains meal ID's
    private Server serv;

    public Feed(int userId, HashSet<Integer> results) {
        this.userId = userId;
        this.serv = serv.getInstance();
        update(results);
    }

    /*
    update the cards set for this feed
     */
    private void updateCards()
    {
        cards = new HashSet<Card>();

        for (int result : results.keySet()) {
            if (results.get(result)) { // if result is visible
                Card newCard = new Card(result, userId, serv.isUserInMeal(userId, result));
                cards.add(newCard);
            }
        }
    }

    /**
     * updates this feed according to new results
     * @param results - results to update
     */
    public void update(HashSet<Integer> results){
        this.results = new HashMap<Integer, Boolean>();
        for (int res : results){
            this.results.put(res, true); // before filtering, everything is visible
        }
        updateCards();
    }

    /**
     * @return the set of cards for this feed
     */
    public Set<Card> getCards()
    {
        return this.cards;
    }

    /**
     * filters out results accorsing to these params
     * @param languages - languages a user speaks
     * @param fieldsOfInterest - fields of interest for user
     */
    public void filter(Set<String> languages,
                       Set<String> fieldsOfInterest ){
        //TODO - milestone 2
    }

}
