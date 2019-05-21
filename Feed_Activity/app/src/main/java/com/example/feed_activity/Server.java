package com.example.feed_activity;

import android.app.Activity;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import static java.lang.Math.toIntExact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Context;
import com.google.common.primitives.Ints;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.DoubleFunction;

import static com.firebase.ui.auth.AuthUI.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;


/**
 * Simulates a server for POC
 */
public class Server {

    private static Server instance = new Server();

    private Set<String> standardRestrictions;


    private FirebaseFirestore db;
    private FirebaseDatabase mDb;
    private FirebaseAuth mAuth;
    private static final String MEALS_STRING = "Meals Info";
    private static final String MEALS_Count_STRING = "Meals Count";
    private static final String USERS_DATA_STRING = "Users Info";



    /**
     * Constructor
     */
    private Server()
    {
        mDb = FirebaseDatabase.getInstance();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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

    }

    /**
     * @return singleton instance of this class
     */
    public static Server getInstance() {
        return instance;
    }


    public void signUp(String email, String pass){
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AuthUI", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AuthUI", "createUserWithEmail:failure", task.getException());
                        }


                    }
                });

    }

    /**
     * Creates a new user and adds it to DB
     * @param disName - display name
     * @param pass - his password
     * @param image - his image
     * @param university - his university
     * @param langs - languages he's speaking
     */
    public void addUser(String disName, String email, String pass, Uri image, String university,
                          ArrayList<String> langs)
    {
        signUp(email, pass);
        MainActivity.user = FirebaseAuth.getInstance().getCurrentUser();



        /* update display name, and image*/
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(disName).setPhotoUri(image)
                .build();


        String userId = MainActivity.userId;

        DocumentReference busRef = db.collection(USERS_DATA_STRING).document(userId);
        User userObj = new User(disName, image, university, langs, userId);
        busRef.set(userObj);

    }

    public Boolean UserExists(final String uId){
        final Boolean[] res = new Boolean[1];

        DocumentReference docIdRef = db.collection(USERS_DATA_STRING).document(String.valueOf(uId));
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("exists", "Document exists!");
                        res[0] = true;
                    } else {
                        Log.d("exists", "Document does not exist!");
                        res[0] = false;
                    }
                } else {
                    Log.d("exists", "Failed with: ", task.getException());
                    res[0] = false;
                }
            }
        });
        return res[0];
    }

    public User getUser(final String userId){

        DocumentReference docRef = db.collection(USERS_DATA_STRING).document(userId);
        final User[] user = new User[1];
        /* gets object from server  */
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists())
                    {
                        user[0] = document.toObject(User.class);

                    }
                    else
                    {
                        System.out.println("no user found");
                    }
                }
            }
        });


        return user[0];

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
    public int addMeal(final String hostId, final String title, final ArrayList<String> tags,
                       final HashMap<String, Boolean> restrictions, final String descr,
                       final int maxGuests, final String loc, final String time)
    {


        final int[] counter = new int[1];

        final DatabaseReference ref = mDb.getReference().child("Meals_Count");

        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);

                if (currentValue == null) {
                    mutableData.setValue(0);
                    counter[0] = 0;
                } else {
                    counter[0] = currentValue;
                    mutableData.setValue(currentValue + 1);

                }
                DocumentReference docRef = db.collection(MEALS_STRING).document(String.valueOf(counter[0]));
                Meal newMeal = new Meal(counter[0],  hostId,  title,tags,
                        restrictions, descr, maxGuests,  loc,  time);
                docRef.set(newMeal);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                System.out.println("Transaction completed");
            }
        });

        return counter[0];
    }

    /**
     * removes a meal whose id is id
     * @param id - meal id to remove
     */
    public void removeMeal(int id)
    {
        DocumentReference ref = db.collection(MEALS_STRING).document(String.valueOf(id));

        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("removeMeal", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("removeMeal", "Error deleting document", e);
                    }
                });

    }



    /**
     * checks if user is a member of a meal
     * @param userId - user id to check
     * @param mealId - meal to check
     * @return true or false according to result
     */
    public Boolean isUserInMeal(String userId, int mealId)
    {
        DocumentReference docRef = db.collection(MEALS_STRING).document(String.valueOf(mealId));
        final Meal[] meal = new Meal[1];
        /* gets object from server  */
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                meal[0] = documentSnapshot.toObject(Meal.class);
            }
        });

        /* applies the desired function on it */
        if (meal[0] != null) {
            return meal[0].isMember(userId);
        }
        return false;
    }

    public Boolean MealExists(final int mealId){
        final Boolean[] res = new Boolean[1];

        DocumentReference docIdRef = db.collection(MEALS_STRING).document(String.valueOf(mealId));
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("exists", "Document exists!");
                        res[0] = true;
                    } else {
                        Log.d("exists", "Document does not exist!");
                        res[0] = false;
                    }
                } else {
                    Log.d("exists", "Failed with: ", task.getException());
                    res[0] = false;
                }
            }
        });
        return res[0];
    }


    /**
     * adds a user to a meal
     * @param userId   user's ID
     * @param mealId meal's ID
     * @return true upon success, false otherwise
     */
    public Boolean addUserToMeal(String userId, int mealId){

        DocumentReference busRef = db.collection(MEALS_STRING).document(String.valueOf(mealId));
        busRef.update("guests", FieldValue.arrayUnion(userId));
        return true;
    }

    /**
     * removes a user from a meal
     * @param userId   user's ID
     * @param mealId meal's ID
     * @return true upon success, false otherwise
     */
    public Boolean removeUserToMeal(String userId, int mealId){


        DocumentReference busRef = db.collection(MEALS_STRING).document(String.valueOf(mealId));
        if (MealExists(mealId) ) { // todo - validate that user also exists
            busRef.update("guests", FieldValue.arrayRemove(userId));
            return true;
        }

        return false; // failed to add

    }

    /**
     * @return the set of standard restrictions
     */
    public Set<String> getStandardRestrictions() {
        return standardRestrictions;
    }


    public Meal getMeal(int mId)
    {

        DocumentReference docRef = db.collection(MEALS_STRING).document(String.valueOf(mId));
        final Meal[] meal  = new Meal[1];
        final AtomicBoolean done = new AtomicBoolean(false);

        docRef.get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                meal[0] = document.toObject(Meal.class);

                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                        done.set(true);
                    }

                });


        return meal[0];
    }

    public void getMeals(final ArrayList<Meal> meals, final MealsListAdapter adapt) {

        db.collection(MEALS_STRING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                meals.add(document.toObject(Meal.class));
                                Log.d("getMeals", document.getId() + " => " + document.getData());
                            }

                            adapt.notifyDataSetChanged();
                        } else {
                            Log.d("getMeals", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    public Boolean isRestricted(int mealId, String restriction){
        if (getMeal(mealId) != null){
            return getMeal(mealId).isRestricted(restriction);
        }
        return false;
    }

}
