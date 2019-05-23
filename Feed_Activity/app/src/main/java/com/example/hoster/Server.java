package com.example.hoster;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.firebase.ui.auth.AuthUI.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static java.lang.Math.ulp;


/**
 * Simulates a server for POC
 */
public class Server {

    private static Server instance = new Server();

    private Set<String> standardRestrictions;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

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


    public void signUp(String email, String pass, final Activity act){
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AuthUI", "createUserWithEmail:success");
                            MainActivity.user = mAuth.getCurrentUser();
                            MainActivity.userId = MainActivity.user.getUid();
                            addUser("", null, "", new ArrayList<String>());
                            Intent main = new Intent(act.getApplicationContext(), MainActivity.class);
                            act.startActivity(main);
                            act.finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AuthUI", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(act, "Registeration failed",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }

    /**
     * Creates a new user and adds it to DB
     * @param disName - display name
     * @param image - his image
     * @param university - his university
     * @param langs - languages he's speaking
     */
    public void addUser(String disName, Uri image, String university,
                          ArrayList<String> langs)
    {

        String userId = MainActivity.userId;

        DocumentReference busRef = db.collection(USERS_DATA_STRING).document(userId);
        User userObj = new User(disName, image, university, langs, userId);
        busRef.set(userObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("User addition : success!");
            }
        });

    }

    /**
     * Creates a new user and adds it to DB
     * @param disName - display name
     * @param image - his image
     * @param university - his university
     * @param langs - languages he's speaking
     */
    public void editUser(User old, String disName, Uri image, String university,
                        ArrayList<String> langs)
    {

        DocumentReference busRef = db.collection(USERS_DATA_STRING).document(MainActivity.userId);
        if (!old.getUsername().equals(disName)) {
            busRef.update("username", disName).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }
        if (image != null) {
            busRef.update("image", image).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }
        if (university != old.getUniversity()){
            busRef.update("university", university).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }
        if (langs != old.getLangs()){
            busRef.update("langs", langs).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }
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

    public void getUser(final String userId, final User[] user, final TextView name,
                        final TextView uni, final TextView langs){

        DocumentReference docRef = db.collection(USERS_DATA_STRING).document(userId);

        /* gets object from server  */
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists())
                    {
                        User got =  document.toObject(User.class);
                        user[0] = got;
                        name.setText(got.getUsername());
                        uni.setText(got.getUniversity());
                        langs.setText(Profile.getLangsString(got.getLangs()));

                    }
                    else
                    {
                        System.out.println("no user found");
                    }
                }
            }
        });

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
                    DocumentReference docRef = db.collection(MEALS_STRING).document(String.valueOf(counter[0]));
                    Meal newMeal = new Meal(String.valueOf(counter[0]),  hostId,  title,tags,
                            restrictions, descr, maxGuests,  loc,  time);
                    docRef.set(newMeal);
                }

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
    public Boolean addUserToMeal(Context context, String userId, String mealId){

        DocumentReference busRef = db.collection(MEALS_STRING).document(mealId);
        busRef.update("guests", FieldValue.arrayUnion(userId));
        return true;
    }



    /**
     * removes a user from a meal
     * @param userId   user's ID
     * @param mealId meal's ID
     * @return true upon success, false otherwise
     */
    public Boolean removeUserToMeal(String userId, String mealId, String hostId){
        DocumentReference busRef = db.collection(MEALS_STRING).document(mealId);

        if (userId.equals(hostId)){
            busRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            getMeals(MainActivity.meals, MainActivity.adapter);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);

                        }
                    });
        } else {
            busRef.update("guests", FieldValue.arrayRemove(userId)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    getMeals(MainActivity.meals, MainActivity.adapter);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });;
        }


        return true;

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
                            MainActivity.meals.clear();
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
