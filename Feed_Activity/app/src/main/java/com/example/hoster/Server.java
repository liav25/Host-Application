package com.example.hoster;

import android.app.Activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;


import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
    private Context main;
    FirebaseStorage storage;
    StorageReference storageReference;
    HashMap<String, Bitmap> pics;
    FirebaseFirestore db;
    FirebaseDatabase mDb;
    FirebaseAuth mAuth;
    private static final String MEALS_STRING = "Meals Info";
    private static final String MEALS_Count_STRING = "Meals Count";
    private static final String USERS_DATA_STRING = "User info";
    private static final String USER_PIC_PATH ="ProfilePics/";

    /**
     * Constructor
     */
    private Server()
    {
        mDb = FirebaseDatabase.getInstance();
        pics = new HashMap<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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

    /**
     * Signs up a new user, with email and password, then creates an empty user in DB
     * @param email - email
     * @param pass - password
     * @param act - activity to use for moving on to MainActivity
     */
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
     * @param image - his profile_image
     * @param university - his university
     * @param langs - languages he's speaking
     */
    public void addUser(String disName, String image, String university,
                          ArrayList<String> langs)
    {

        final String userId = MainActivity.userId;

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
     * @param old - old user to compare to current changes
     * @param disName - display name
     * @param university - his university
     * @param langs - languages he's speaking
     */
    public void editUser(User old, String disName, String university,
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


    /**
     * Edit and change's the user's profile picture
     * @param image - profile picture uri to update
     */
    public void editProfilePic(Uri image){
        DocumentReference busRef = db.collection(USERS_DATA_STRING).document(MainActivity.userId);

        if (image != null) {
            busRef.update("image", image.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("ProfilePic edit : success!");
                }
            });
        }
    }


    /**
     * Gets the user from the DB and downloads it to a User object
     * @param userId - user ID
     * @param user - user reference to assign new user to
     * @param name - view to display user name at
     * @param uni - view to display user university at
     * @param langs - view to display user languages at
     * @param img - view to display user profile picture at
     */
    public void getUser(final String userId, final User[] user, final TextView name,
                        final TextView uni, final TextView langs, final ImageView img){

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
                        if (got.getImage() != null){
                            if(!got.getImage().equals("")){
                                downloadProfilePic(img, userId);
                            }

                        }
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
     * Downloads a user's profile picture
     * @param img - imgview to put downloaded pictrue at
     * @param userId  - user's ID
     */
    public void downloadProfilePic(final ImageView img, final String userId) {
        if (pics.containsKey(userId)){ // already downloaded
            img.setImageBitmap(pics.get(userId));
        } else { // download from server
            try {
                StorageReference ref = storageReference.child(USER_PIC_PATH + userId);

                final File localFile = File.createTempFile("Images", "bmp");

                ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener < FileDownloadTask.TaskSnapshot >() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        pics.put(userId, my_image);
                        img.setImageBitmap(my_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error downloading Image");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
                getMeals(MainActivity.meals, MainActivity.adapter);
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




    /**
     * adds a user to a meal
     * @param userId   user's ID
     * @param meal meal to add user to
     * @return true upon success, false otherwise
     */
    public Boolean addUserToMeal(final Context context, String userId, final Meal meal){

        DocumentReference busRef = db.collection(MEALS_STRING).document(meal.getID());
        busRef.update("guests", FieldValue.arrayUnion(userId)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setNotification(meal, context); // sets notification
            }
        });
        return true;
    }


    /**
     * Sets the "rating" notification after joining a meal
     * @param meal - meal just joined to
     * @param cont - context to use in order to create intent
     */
    public void setNotification(Meal meal, Context cont){
        createNotificationChannel(cont);
        Intent intent = new Intent(cont, HowWasItPop.class);
        Bundle b = new Bundle();
        b.putString("userToRate", meal.getHostId());
        b.putInt("mealIdToRate", Integer.parseInt(meal.getID()));

        intent.putExtras(b);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(cont);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(Integer.parseInt(meal.getID()), PendingIntent.FLAG_UPDATE_CURRENT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        NotificationCompat.Builder build = new NotificationCompat.Builder(cont, "Hoster")
                .setSmallIcon(R.drawable.spice)
                .setContentTitle("Hoster")
                .setContentText("Help us by rating your host!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).
                        setContentIntent(resultPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cont);


        notificationManager.notify(Integer.parseInt(meal.getID()), build.build());

    }

    /**
     * Helper function to create notification channel
     * @param cont - context to use
     */
    private void createNotificationChannel(Context cont) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Hoster";
            String description = "Help us by rating your Host!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Hoster", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = cont.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * removes a user from a meal
     * @param userId   user's ID
     * @param mealId meal's ID
     * @return true upon success, false otherwise
     */
    public Boolean removeUserToMeal(String userId, final String mealId, String hostId, final Context cont){
        DocumentReference busRef = db.collection(MEALS_STRING).document(mealId);

        if (userId.equals(hostId)){
            busRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cont);
                            notificationManager.cancel(Integer.parseInt(mealId));
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

    /**
     * Downloads a specific meal according to its ID
     * Does not work since its not asynchronous
     * @param mId - meal id
     * @return - Meal object
     */
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

    /**
     * Gets the list of meals and puts it in the desired list and adapter
     * @param meals - list to insert meals from server to
     * @param adapt - adapter to refresh after changing the list
     */
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




    public void getUsername(String uId, final TextView toShow) {
        DocumentReference docRef = db.collection(USERS_DATA_STRING).document(uId);

        /* gets object from server  */
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        User got = document.toObject(User.class);
                        toShow.setText(got.getUsername());

                    } else {
                        System.out.println("no such user found");
                    }
                }
            }
        });
    }

    /**
     * Ranks a given user
     * @param rank - int from 1 to 5 according to the tnaking
     * @param userId - user id to rank
     * @param mealId - meal that userId was the host of
     * @param cont - Context to use in order to move intents
     */
    public void setRanking(final int rank, final String userId, int mealId, Context cont){
        DocumentReference busRef = db.collection(USERS_DATA_STRING).document(userId);

        busRef.update("num_of_raters", FieldValue.increment(1));
        busRef.update("rating_sum", FieldValue.increment(rank));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cont);
        notificationManager.cancel(mealId);

    }


}
