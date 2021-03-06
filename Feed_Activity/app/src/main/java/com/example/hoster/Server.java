package com.example.hoster;

import android.app.Activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;


import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.google.firebase.firestore.CollectionReference;
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
import com.google.rpc.Help;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

import static com.firebase.ui.auth.AuthUI.TAG;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static java.lang.Math.ulp;



/**
 * Aמ API class for all server-based operations. Built as a singleton
 */
public class Server {


    private static Server instance = new Server();

    private Set<String> standardRestrictions;

    FirebaseStorage storage;
    StorageReference storageReference;
    volatile private HashMap<String, String> pics;
    private HashMap<String, Location> locations;
    private FirebaseFirestore db;
    private FirebaseDatabase mDb;
    FirebaseAuth mAuth;
    private static final String MEALS_STRING = "Meals Info";
    private static final String MEALS_Count_STRING = "Meals Count";
    private static final String USERS_DATA_STRING = "User info";
    private static final String MEALS_DATA_STRING = "Meals Info";
    private static final String USER_PIC_PATH = "ProfilePics/";

    /**
     * Constructor
     */
    private Server() {
        mDb = FirebaseDatabase.getInstance();
        pics = new HashMap<>();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        setupLocations();

    }


    /*
    Sets a standard set of food restrictions
     */
    private void setStandardRestrictions() {
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
     *
     * @param email - email
     * @param pass - password
     * @param act - activity to use for moving on to MainActivity
     * @param disname - display name for user
     */
    public void signUp(final String email, String pass, final Activity act, final String disname) {

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("AuthUI", "createUserWithEmail:success");
                            MainActivity.user = mAuth.getCurrentUser();
                            MainActivity.userId = MainActivity.user.getUid();
                            MainActivity.userMail = email;
                            addUser(disname, null, "", new ArrayList<String>(), email);
                            Intent main = new Intent(act.getApplicationContext(), MainActivity.class);
                            act.startActivity(main);
                            act.finish();
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w("AuthUI", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(act, "Registeration failed",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }

    /**
     * Creates a new user and adds it to DB
     * @param disName    - display name
     * @param image      - his profile_image
     * @param university - his university
     * @param langs      - languages he's speaking
     */
    public void addUser(String disName, String image, String university,
                          ArrayList<String> langs, String email)
    {
        final String userId = MainActivity.userId;

        DocumentReference busRef = db.collection(USERS_DATA_STRING).document(userId);
        User userObj = new User(disName, image, university, langs, userId, email);
        busRef.set(userObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("User addition : success!");
            }

        });

    }

    /**
     * Creates a new user and adds it to DB
     *
     * @param old        - old user to compare to current changes
     * @param disName    - display name
     * @param university - his university
     * @param langs      - languages he's speaking
     */
    public void editUser(User old, String disName, String university,
                         ArrayList<String> langs) {

        DocumentReference busRef = db.collection(USERS_DATA_STRING).document(MainActivity.userId);
        if (!old.getUsername().equals(disName)) {
            busRef.update("username", disName).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }

        if (university != old.getUniversity()) {
            busRef.update("university", university).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }
        if (langs != old.getLangs()) {
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
     *
     * @param image - profile picture uri to update
     */
    public void editProfilePic(Uri image) {
        DocumentReference busRef = db.collection(USERS_DATA_STRING).document(MainActivity.userId);

        if (image != null) {
            busRef.update("image", image.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("ProfilePic edit : success!");
                    pics.remove(MainActivity.userId);
                    downloadProfilePic(MainActivity.profilePicture,
                            MainActivity.userId);
                }
            });
        }
    }


    /**
     * Gets the user from the DB and downloads it to a User object
     *
     * @param userId - user ID
     * @param user   - user reference to assign new user to
     * @param name   - view to display user name at
     * @param uni    - view to display user university at
     * @param langs  - view to display user languages at
     * @param img    - view to display user profile picture at
     */
    public void getUser(final String userId, final User[] user, final TextView name,
                        final TextView uni, final TextView langs, final ImageView img
            , final ArrayList<String> mutual, final Profile profile_act, final ImageView pepper,
                        final TextView ranktext) {

        final DocumentReference docRef = db.collection(USERS_DATA_STRING).document(userId);
        DocumentReference self = db.collection(USERS_DATA_STRING).document(MainActivity.userId);


        /* gets object from server  */
        self.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot myProfile = task.getResult();
                    if (myProfile != null && myProfile.exists()) {
                        User personal_prof = myProfile.toObject(User.class); // downloads personal interactions
                        final ArrayList<String> personal_interactions = personal_prof.getMutual();

                        /* gets object from server  */
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        User got = document.toObject(User.class);
                                        user[0] = got;
                                        name.setText(got.getUsername());
                                        uni.setText(got.getUniversity());
                                        langs.setText(Profile.getLangsString(got.getLangs()));
                                        if (got.getImage() != null) {
                                            if (!got.getImage().equals("")) {
                                                Server.getInstance().downloadProfilePic(img, userId);
                                            }

                                        }
                                        for (String id : got.getMutual()) {
                                            if (personal_interactions.contains(id) && !id.equals(userId)
                                                    && !id.equals(MainActivity.userId)) {
                                                // we don't want to get these users
                                                mutual.add(id);
                                            }
                                        }

                                        if (profile_act != null) {
                                            profile_act.setImages();
                                        }

                                        if (pepper != null && ranktext != null) {
                                            Profile.show_rank(got.getnum_of_raters(), got.getrating_sum(),
                                                    pepper, ranktext);
                                        }

                                    } else {
                                        System.out.println("no user found");
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });


    }

    /**
     * Downloads a user's profile picture
     *
     * @param img    - imgview to put downloaded pictrue at
     * @param userId - user's ID
     */
    public synchronized void downloadProfilePic(final ImageView img, final String userId) {
        if (pics.containsKey(userId)) { // already downloaded
            if(pics.get(userId) != null) {
                Bitmap my_image = BitmapFactory.decodeFile(pics.get(userId));
                if (img != null ) img.setImageBitmap(my_image);
            } else {
                if (img != null) img.setImageResource(R.drawable.profile_picture);
            }
        } else { // download from server
            try {

                StorageReference ref = storageReference.child(USER_PIC_PATH + userId);

                final File localFile = File.createTempFile("Images", "bmp");

                ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap my_image = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        pics.put(userId, localFile.getAbsolutePath());
                        downloadProfilePic(img, userId);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "Error downloading Image");
                        pics.put(userId, null);

                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * adds new meal to DB
     *
     * @param hostId       - the host's ID
     * @param title        - the title of this meal
     * @param tags         - additional taggs for this meal
     * @param restrictions - food restrictions for this meal
     * @param descr        - description
     * @param maxGuests    - maximum number of guests for this meal
     * @param loc          - location for this meal
     * @param time         - time and date of meal
     * @param mNeeded      - what needed
     */
    public int addMeal(final String hostId, final String title, final ArrayList<String> tags,
                       final HashMap<String, Boolean> restrictions, final String descr,
                       final int maxGuests, final String loc, final String time,
                       final HashMap<String, String> mNeeded, final String hostMail) {


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
                    ArrayList<String> mails = new ArrayList<>();
                    mails.add(hostMail);
                    Meal newMeal = new Meal(String.valueOf(counter[0]), hostId, title, tags,
                            restrictions, descr, maxGuests, loc, time, mNeeded, mails);
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
     *
     * @param id - meal id to remove
     */
    public void removeMeal(int id) {
        DocumentReference ref = db.collection(MEALS_STRING).document(String.valueOf(id));

        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.d("removeMeal", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.w("removeMeal", "Error deleting document", e);
                    }
                });

    }


    /**
     * checks if user is a member of a meal
     *
     * @param userId - user id to check
     * @param mealId - meal to check
     * @return true or false according to result
     */
    public Boolean isUserInMeal(String userId, int mealId) {
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
     *
     * @param userId user's ID
     * @param meal   meal to add user to
     * @return true upon success, false otherwise
     */
    public Boolean addUserToMeal(final Context context, String userId, final Meal meal,
                                 final String mail) {

        DocumentReference busRef = db.collection(MEALS_STRING).document(meal.getID());
        busRef.update("guests", FieldValue.arrayUnion(userId)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSuccess(Void aVoid) {
                setNotification(meal, context); // sets notification
                setMutuals(meal.getGuests());
            }
        });
        busRef.update("guestsMails", FieldValue.arrayUnion(mail));
        return true;
    }

    /*
     * makes intersection between userId's friends and the members of meal in order to set the
     * mutual section
     */
    private void setMutuals(ArrayList<String> membersOfMeal) {
        DocumentReference busRef = db.collection(USERS_DATA_STRING).document(MainActivity.userId);
        try {
            for (String id : membersOfMeal) {
                busRef.update("mutual", FieldValue.arrayUnion(id));
            }
        } catch (Exception e) {
            ArrayList<String> arr = new ArrayList<>(membersOfMeal);
            busRef.update("mutual", arr);

        }

    }


    /**
     * Sets the "rating" notification after joining a meal
     * @param meal - meal just joined to
     * @param cont - context to use in order to create intent
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setNotification(Meal meal, Context cont) {
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

    /*
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
     *
     * @param userId user's ID
     * @param mealId meal's ID
     * @return true upon success, false otherwise
     */
    public Boolean removeUserToMeal(String userId, final String mealId, String hostId,
                                    final Context cont, final String mail) {
        DocumentReference busRef = db.collection(MEALS_STRING).document(mealId);

        if (userId.equals(hostId)) {
            busRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cont);
                            notificationManager.cancel(Integer.parseInt(mealId));
                            getMeals(MainActivity.meals, MainActivity.adapter);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error deleting document", e);

                        }
                    });
        } else {
            busRef.update("guestsMails", FieldValue.arrayRemove(mail));
            busRef.update("guests", FieldValue.arrayRemove(userId)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    getMeals(MainActivity.meals, MainActivity.adapter);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//
//                           Log.w(TAG, "Error deleting document", e);
                        }
                    });
            ;
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
     *
     * @param mId - meal id
     * @return - Meal object
     */
    public Meal getMeal(int mId) {

        DocumentReference docRef = db.collection(MEALS_STRING).document(String.valueOf(mId));
        final Meal[] meal = new Meal[1];
        final AtomicBoolean done = new AtomicBoolean(false);

        docRef.get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                meal[0] = document.toObject(Meal.class);

//                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            } else {
//                                Log.d(TAG, "No such document");
                            }
                        } else {
//                            Log.d(TAG, "get failed with ", task.getException());
                        }
                        done.set(true);
                    }

                });


        return meal[0];
    }

    /**
     * Gets the list of meals and puts it in the desired list and adapter
     *
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
                                Meal me = document.toObject(Meal.class);
                                for (String uid : me.getGuests()){
                                    downloadProfilePic(null, uid);
                                }

                                meals.add(me);
//                                Log.d("getMeals", document.getId() + " => " + document.getData());
                            }

                            adapt.notifyDataSetChanged();
                        } else {
//                            Log.d("getMeals", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    public void getUsername(String uId, final TextView toShow) {
        if (uId != null) {
            try {
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
            } catch (RuntimeException e){
                System.out.println(e.toString());
            }
        }
    }

    /**
     * This function is setting a field from user to a TextView
     * @param uId - the user Id
     * @param toShow - the field we put on
     * @param field - the name of the field
     */
    public void getField(String uId, final TextView toShow, final String field) {
        DocumentReference docRef = db.collection(USERS_DATA_STRING).document(uId);

        /* gets object from server  */
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String got = document.getString(field);

                        toShow.setText(got);

                    } else {
                        System.out.println("no such user found");
                    }
                }
            }
        });
    }

    public void getFieldForHint(String uId, final TextView toShow, final String field) {
        DocumentReference docRef = db.collection(USERS_DATA_STRING).document(uId);

        /* gets object from server  */
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String got = document.getString(field);
                        if(got!=null) {
                            toShow.setHint(got);
                        }
                        else{toShow.setHint(field);}
                    } else {
                        System.out.println("no such user found");
                    }
                }
            }
        });
    }



    /**
     * Ranks a given user
     *
     * @param rank   - int from 1 to 5 according to the tnaking
     * @param userId - user id to rank
     * @param mealId - meal that userId was the host of
     * @param cont   - Context to use in order to move intents
     */
    public void setRanking(final int rank, final String userId, int mealId, Context cont) {
        DocumentReference busRef = db.collection(USERS_DATA_STRING).document(userId);

        busRef.update("num_of_raters", FieldValue.increment(1));
        busRef.update("rating_sum", FieldValue.increment(rank));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cont);
        notificationManager.cancel(mealId);

    }


    private static final String[] NEIGHBORHOODS_JLM = new String[]{
            "Musrara", "Ein Kerem", "Nachlaot", "Jewish Quarter", "Yemin Moshe",
            "Old City", "Machne Yehuda", "Emek Refaim", "Rehavia", "Rasko", "Talpiot",
            "Nayot", "Mamila", "Mea' She'arim", "Arnona", "Holyland", "Mishkanot She'ananim",
            "Motza", "Geula", "Katamon", "Kiryat haYovel", "Givat Ram", "Har Nof", "Migrash haRusim",
            "Bait vaGan", "Givat Shaul", "Malha", "Talbiya", "Makor Baruch", "Nachalat Shiv'a",
            "Geulim Bak'a", "Kerem Avraham", "Kiryat Menachem", "Givat Masuaa", "Givat Mordechai",
            "Kiryat haMemshala", "Hutzot haYotzer", "Ramat Sharet", "Romema", "Pat", "Sanhedriya",
            "Bucharim", "Mahane Israel", "She'arey Hesed", "Ir Ganim Gimel", "Hameshulash", "Ramat Beit Hakerem",
            "Makor Hayim", "Kiryat haLeom", "Ramat Deniya", "Tel Arza"
    };


    /**
     * returns location of neighborhoods in jerusalem. used the following python
     * script to get these locations:
     * >>> from geopy.geocoders import Nominatim
     * >>> needed = []
     * >>> for neigh in neighlist:
     * >>> try:
     * 		location = geolocator.geocode(neigh + ", Jerusalem Israel")
     * 		print ("Location " + neigh + " = new Location (\"" + neigh + "\");")
     * 		print(neigh  + ".setLatitude(" + str(location.latitude) + ");")
     * 		print(neigh  + ".setLongitude(" + str(location.longitude) + ");")
     * 		print("locations.put(\"" + neigh + "\", " + neigh + ");")
     * 	>>> except Exception as e:
     */
    private void setupLocations() {
        locations = new HashMap<>();
        Location rehavia = new Location("Rehavia");
        rehavia.setLatitude(31.7708234);
        rehavia.setLongitude(35.2107216);

        locations.put("Rehavia", rehavia);

        Location Musrara = new Location ("Musrara");
        Musrara.setLatitude(31.7820246);
        Musrara.setLongitude(35.2260718);
        locations.put("Musrara", Musrara);
        Location EinKerem = new Location ("Ein Kerem");
        EinKerem.setLatitude(31.7676367);
        EinKerem.setLongitude(35.1639025);
        locations.put("Ein Kerem", EinKerem);
        Location Nachlaot = new Location ("Nachlaot");
        Nachlaot.setLatitude(31.7831454);
        Nachlaot.setLongitude(35.2123968);
        locations.put("Nachlaot", Nachlaot);
        Location JewishQuarter = new Location ("Jewish Quarter");
        JewishQuarter.setLatitude(31.7753539);
        JewishQuarter.setLongitude(35.2317629);
        locations.put("Jewish Quarter", JewishQuarter);
        Location YeminMoshe = new Location ("Yemin Moshe");
        YeminMoshe.setLatitude(31.7721407);
        YeminMoshe.setLongitude(35.2249574);
        locations.put("Yemin Moshe", YeminMoshe);
        Location OldCity = new Location ("Old City");
        OldCity.setLatitude(31.7782872);
        OldCity.setLongitude(35.2319526);
        locations.put("Old City", OldCity);
        Location Rehavia = new Location ("Rehavia");
        Rehavia.setLatitude(31.7748);
        Rehavia.setLongitude(35.2120777);
        locations.put("Rehavia", Rehavia);
        Location Talpiot = new Location ("Talpiot");
        Talpiot.setLatitude(31.7496635);
        Talpiot.setLongitude(35.2337721);
        locations.put("Talpiot", Talpiot);
        Location Nayot = new Location ("Nayot");
        Nayot.setLatitude(31.7693712);
        Nayot.setLongitude(35.2024424);
        locations.put("Nayot", Nayot);
        Location Mamila = new Location ("Mamila");
        Mamila.setLatitude(31.77795945);
        Mamila.setLongitude(35.22092505);
        locations.put("Mamila", Mamila);
        Location MeaShearim = new Location ("Mea' She'arim");
        MeaShearim.setLatitude(31.7875129);
        MeaShearim.setLongitude(35.2222372);
        locations.put("Mea' She'arim", MeaShearim);
        Location Arnona = new Location ("Arnona");
        Arnona.setLatitude(31.750559);
        Arnona.setLongitude(35.2197245);
        locations.put("Arnona", Arnona);
        Location Holyland = new Location ("Holyland");
        Holyland.setLatitude(31.756516);
        Holyland.setLongitude(35.1887956);
        locations.put("Holyland", Holyland);
        Location Motza = new Location ("Motza");
        Motza.setLatitude(31.79279);
        Motza.setLongitude(35.168506);
        locations.put("Motza", Motza);
        Location Geula = new Location ("Geula");
        Geula.setLatitude(31.7883192);
        Geula.setLongitude(35.216052);
        locations.put("Geula", Geula);
        Location Katamon = new Location ("Katamon");
        Katamon.setLatitude(31.7660228);
        Katamon.setLongitude(35.2093317);
        locations.put("Katamon", Katamon);
        Location KiryathaYovel = new Location ("Kiryat haYovel");
        KiryathaYovel.setLatitude(31.7643601);
        KiryathaYovel.setLongitude(35.1750449);
        locations.put("Kiryat haYovel", KiryathaYovel);
        Location HarNof = new Location ("Har Nof");
        HarNof.setLatitude(31.7858115);
        HarNof.setLongitude(35.1741509);
        locations.put("Har Nof", HarNof);
        Location BaitvaGan = new Location ("Bait vaGan");
        BaitvaGan.setLatitude(31.7653824);
        BaitvaGan.setLongitude(35.1867841);
        locations.put("Bait vaGan", BaitvaGan);
        Location GivatShaul = new Location ("Givat Shaul");
        GivatShaul.setLatitude(31.7911185);
        GivatShaul.setLongitude(35.1931023);
        locations.put("Givat Shaul", GivatShaul);
        Location Malha = new Location ("Malha");
        Malha.setLatitude(31.7514658);
        Malha.setLongitude(35.1829753);
        locations.put("Malha", Malha);

        Location GeulimBaka = new Location ("Geulim Bak'a");
        GeulimBaka.setLatitude(31.7595534);
        GeulimBaka.setLongitude(35.2196723);
        locations.put("Geulim Bak'a", GeulimBaka);
        Location KiryatMenachem = new Location ("Kiryat Menachem");
        KiryatMenachem.setLatitude(31.7886126);
        KiryatMenachem.setLongitude(35.2176848);
        locations.put("Kiryat Menachem", KiryatMenachem);

        Location Romema = new Location ("Romema");
        Romema.setLatitude(31.7916556);
        Romema.setLongitude(35.2036799);
        locations.put("Romema", Romema);
        Location Pat = new Location ("Pat");
        Pat.setLatitude(31.7507047);
        Pat.setLongitude(35.2046175);
        locations.put("Pat", Pat);
        Location Sanhedriya = new Location ("Sanhedriya");
        Sanhedriya.setLatitude(31.79697265);
        Sanhedriya.setLongitude(35.2213482113587);
        locations.put("Sanhedriya", Sanhedriya);

        Location RamatBeitHakerem = new Location ("Ramat Beit Hakerem");
        RamatBeitHakerem.setLatitude(31.771929);
        RamatBeitHakerem.setLongitude(35.190955);
        locations.put("Ramat Beit Hakerem", RamatBeitHakerem);
        Location MakorHayim = new Location ("Makor Hayim");
        MakorHayim.setLatitude(31.7552876);
        MakorHayim.setLongitude(35.2119379);
        locations.put("Makor Hayim", MakorHayim);

    }




    /**
     * Edits a meal
     * @param old - old user to compare to current changes
     * @param disTitle - display name
     */
    public void editMeal(Meal old, String disTitle, String disDescription,
                         String disLocation, String disDate, int disMaxGuests,
                         Map disRestrictions, Map mNeeded)
    {
        DocumentReference busRef = db.collection(MEALS_DATA_STRING).document(old.getID());
        if (old.getTitle() != disTitle) {
            busRef.update("title", disTitle).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }

        if (old.getDescription() != disDescription) {
            busRef.update("description", disDescription).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }

        if (old.getLocation() != disLocation) {
            busRef.update("location", disLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }

        if (old.getTime() != disDate) {
            busRef.update("time", disDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }

        if (old.getDescription() != disDescription) {
            busRef.update("description", disDescription).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("User edit : success!");
                }
            });
        }


		if (old.getMaxGuests() != disMaxGuests) {
			busRef.update("maxGuests", disMaxGuests).addOnSuccessListener(new OnSuccessListener<Void>() {
				@Override
				public void onSuccess(Void aVoid) {
					System.out.println("User edit : success!");
				}
			});
		}

		busRef.update("restrictions", disRestrictions).addOnSuccessListener(new OnSuccessListener<Void>() {

			@Override
			public void onSuccess(Void aVoid) {
				System.out.println("User edit : success!");
			}
		});

		busRef.update("needed", mNeeded).addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				System.out.println("User edit : success!");
			}
		});


    }



    public void editMeal (Meal old, Map mNeeded)
    {
        DocumentReference busRef = db.collection(MEALS_DATA_STRING).document(old.getID());


        busRef.update("needed", mNeeded).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("User edit : success!");
            }
        });


    }


    /**
     * Iterates over users and adds field
     */
    public void patch() {
        CollectionReference ref = db.collection(USERS_DATA_STRING);
        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.iterator();
                Iterator<QueryDocumentSnapshot> it = queryDocumentSnapshots.iterator();
                int count = 0;
                while (it.hasNext()) {
                    DocumentReference doc = it.next().getReference();
                    doc.update("email", "email" + count + "@gm.com");
                    count++;
                }
            }
        });
    }

    /**
     * adds emails to meals
     */
    public void patch2() {
        CollectionReference ref = db.collection(MEALS_STRING);

        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.iterator();
                Iterator<QueryDocumentSnapshot> it = queryDocumentSnapshots.iterator();

                while (it.hasNext()) {
                    DocumentSnapshot snp = it.next();
                    final DocumentReference doc = snp.getReference();
                    doc.update("guestsMails", new ArrayList<String>());
                    Meal me = snp.toObject(Meal.class); // downloads personal interactions

                    for (String id : me.getGuests()){
                        final DocumentReference userref = db.collection(USERS_DATA_STRING).document(id);

                        userref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User usi = documentSnapshot.toObject(User.class);
                                doc.update("guestsMails", FieldValue.arrayUnion(usi.getEmail()));
                            }
                        });

                    }
                }
            }
        });
    }

    /**
     * adds emails to meals
     */
    public void patch3() {
        CollectionReference ref = db.collection(MEALS_STRING);

        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.iterator();
                Iterator<QueryDocumentSnapshot> it = queryDocumentSnapshots.iterator();

                while (it.hasNext()) {
                    DocumentSnapshot snp = it.next();
                    final DocumentReference doc = snp.getReference();
                    HashMap<String, String> empty = new HashMap<>();
                    empty.put("beer", "NEEDED");
                    empty.put("flowers", "NEEDED");
                    empty.put("dessert", "NEEDED");
                    empty.put("drinks", "NEEDED");


                    doc.update("needed", empty);

                }
            }
        });
    }

    /**
     * Sends a mail to all of the meal's guests
     * @param mails - mails list of all the guests' mails
     * @param cont - context to use
     */
    public void sendAllMail(ArrayList<String> mails, Context cont, String title, String date){

        final Intent emailLauncher = new Intent(Intent.ACTION_SEND_MULTIPLE);
        String[] mail_list = new String[mails.size()];
        int count = 0;
        for (String m : mails){
            mail_list[count] = m;
            count++;
        }
        emailLauncher.setType("message/rfc822");
        emailLauncher.putExtra(android.content.Intent.EXTRA_EMAIL, mail_list);
        emailLauncher.putExtra(Intent.EXTRA_SUBJECT, title + " " + date);
        try{
            cont.startActivity(emailLauncher);
        } catch (ActivityNotFoundException e){
            System.out.println("Error sending a mail");
        }
    }


    /**
     * @param neighname - neighborhood's name
     * @return neighborhood location if found. JLM's if not
     */
    public Location getLoc(String neighname){
        if (locations.containsKey(neighname)){
            return locations.get(neighname);
        }
        else{
            Location res = new Location("JLM");
            res.setLongitude(35.2250786);
            res.setLatitude(31.778345);
            return res;
        }
    }
}


