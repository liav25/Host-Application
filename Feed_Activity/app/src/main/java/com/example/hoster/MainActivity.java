package com.example.hoster;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    ImageButton addMealBut;
    static Server sev = Server.getInstance();

    public static CircleImageView profilePicture;
    public static ArrayList<Meal> meals;
    public static FirebaseUser user;
    public static String userId;
    public static String userMail;
    //**FOR POPUP**//
    private String[] sortby = {"None", "Date", "Location", "Alphabetically"};
    private String[] filterby = {"None", "My Meals", "I'm Hosting", "Neighborhood",
    "Time: No more than"};

    private ListView cardlist;

    public static MealsListAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(this.getResources().getColor(R.color.toolbarcolor));
        setContentView(R.layout.activity_main);

        meals = new ArrayList<>();
        adapter = new MealsListAdapter(this,  R.layout.feed_card
                , meals);

        sev.getMeals(meals, adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        cardlist = findViewById(R.id.mealCardList);

        cardlist.setAdapter(adapter);

        addMealBut = findViewById(R.id.addMealButton);
        Animation fromBottom2 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        toolbar.setAnimation(fromtop);
        profilePicture = findViewById(R.id.profile_picture);
        sev.downloadProfilePic(profilePicture, userId);
        profilePicture.setAnimation(fromtop);
//        addMealBut.setAnimation(fromBottom2);
        Bundle bun = new Bundle();



        addMealBut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent newIntent = new Intent(getApplicationContext(), PopActivity.class);
                startActivity(newIntent);
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("userId", userId); //Your id
                Intent profileIntent = new Intent(getApplicationContext(), Profile.class);
                profileIntent.putExtras(b); //Put your id to your next Intent
                startActivity(profileIntent);
            }
        });

        cardlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            Toast.makeText(getApplicationContext(),
                    "Click ListItem Number " + position, Toast.LENGTH_LONG)
                    .show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;  //for visible 3 dots change to true, hiding false
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /**
         *Look Here Tom
         * http://www.androidhiro.com/source/android/example/dialogplus/1092
         * This is dialog plus
         * continue with this, that what you wanted
         */

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {

            final DialogPlus dialog = DialogPlus.newDialog(this)
                    .setGravity(Gravity.TOP)
                    .setExpanded(true,300)
                    .setContentHolder(new ViewHolder(R.layout.top_dialog)).
                    setCancelable(true)
                    .create();

//            Button action = (Button) dialog.getHolderView().findViewById(R.id.my_button);
//            action.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getApplicationContext(), Splash.class);
//                    startActivity(intent);
//                }
//            });

            Spinner sort = (Spinner)dialog.findViewById(R.id.sortby);
            Spinner filter = (Spinner)dialog.findViewById(R.id.filterby);

            ArrayAdapter<String> sortAd =
                    new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, sortby);

            ArrayAdapter<String> filtAd =
                    new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, filterby);

            sortAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filtAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sort.setAdapter(sortAd);
            filter.setAdapter(filtAd);

            sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 1: sortByDate();

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            dialog.show();
            }


        return super.onOptionsItemSelected(item);
    }

    private void sortByDate(){
        Collections.sort(meals, new Comparator<Meal>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public int compare(Meal o1, Meal o2) {
                return compareByDate(o1, o2);
            }
        });

        adapter.notifyDataSetChanged();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private int compareByDate(Meal m1, Meal m2){


        if (m1.getTime() != null && m2.getTime() != null) { // time is not a mandatory info, hence

            String date1 = m1.getTime().replace("אחה״צ", "PM")
                    .replace("לפנה״צ", "AM");

            String date2 = m2.getTime().replace("אחה״צ", "PM")
                    .replace("לפנה״צ", "AM"); // adjust to hebrew strings

            System.out.println("in " + date1);
            Date d1 = StringToDate(date1);
            System.out.println("out " + d1);
            Date d2 = StringToDate(date2);

            if (d1 != null && d2 != null) {

                return d1.compareTo(d2);
            }
        } else if (m1.getTime() == null){
            return 1;
        } else {
            return -1;
        }

        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Date StringToDate(String d){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        try {
            return format.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


}
