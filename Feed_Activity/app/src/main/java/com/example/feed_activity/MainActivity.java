package com.example.feed_activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addMealBut;
    Animation fromBottom;

    static Server sev = Server.getInstance();


    public static ArrayList<Meal> meals;
    public static FirebaseUser user;
    public static String userId;

    private ListView cardlist;


    public static MealsListAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorAccent));
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
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        Animation fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        toolbar.setAnimation(fromtop);
        CircleImageView profilePicture = findViewById(R.id.profile_picture);
        profilePicture.setAnimation(fromtop);
        addMealBut.setAnimation(fromBottom);
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
                b.putString("userId", user.getUid()); //Your id
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
        return false;  //for visible 3 dots change to true, hiding false
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void init(){
        sev.addUser("Daniel Carmi", "email901@gmail.com", "password", null,"Open University",
                new ArrayList<String>(Arrays.asList("Hebrew", "English", "Arabic")));

//        user = FirebaseAuth.getInstance().getCurrentUser();
//        sev.addMeal("WiAPbzpIkPdLYTfeMFUxKQoZXqk2", "Italian Dairy Meal", new ArrayList<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", true); put("Kosher", true);
//                    put("Vegan", false); put("Vegetarian", true);}}, "A fun Italian meal in the center of Jerusalem", 5,
//                "Nahlaot",  "05/06/2019  18:30 PM");
//        sev.addMeal(2, "Kosher Barbecue", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", false); put("Kosher", true);
//                    put("Vegan", false); put("Vegetarian", false);}}, "Outdoor barbecue with friendly people in the French Hill", 8,
//                "French Hill",  "20/06/2019  19:30 PM");
//
//
//        sev.addMeal(3, "Shabbat Meal in Rehavia", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", false); put("Kosher", true);
//                    put("Vegan", false); put("Vegetarian", true);}}, "Kosher, shomer shabbat meal on Friday night. Traditional style meal.", 6,
//                "Rehavia",  "10/05/2019  19:00 PM");
//
//        sev.addMeal(4, "Vegan Brunch", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", true); put("Kosher", true);
//                    put("Vegan", true); put("Vegetarian", true);}}, "A cool and casual spring brunch! Everything will be vegan and delicious.", 4,
//                "City Center",  "11/05/2019  10:30 AM");
//
//
//        sev.addMeal(5, "Lunch near Givat Ram Campus", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", true); put("Kosher", false);
//                    put("Vegan", false); put("Vegetarian", false);}}, "Lunch near campus for people who want a nice meal during the school day. Halal friendly.", 3,
//                "Neve Sha'anan",  "08/05/2019  13:00 PM");
//
//
//
//
//        sev.addMeal(6, "Fancy Dinner with French Cuisine", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", false); put("Kosher", false);
//                    put("Vegan", false); put("Vegetarian", false);}}, "We want to host a French cuisine dinner. It's going to be classy, so dress nicely!", 4,
//                "Rehavia",  "09/05/2019  19:30 PM");
//
//
//
//        sev.addMeal(7, "Business Lunch", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", false); put("Kosher", false);
//                    put("Vegan", false); put("Vegetarian", true);}}, "Looking to make connections with people in the art world. Lunch will be vegetarian.", 5,
//                "Talbiya",  "11/05/2019  12:30 PM");
//
//
//
//
//        sev.addMeal(6, "Chill Dinner", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", false); put("Kosher", false);
//                    put("Vegan", false); put("Vegetarian", false);}}, "New in town and looking for English-speaking friends. Join me for a casual dinner with good conversation.", 5,
//                "Nahlaot",  "24/05/2019  18:45 PM");
//
//
//
//        sev.addMeal(8, "Hot dogs and beer with the game", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", false); put("Kosher", false);
//                    put("Vegan", false); put("Vegetarian", false);}}, "If you're looking for somewhere to watch the game, join me! We're gonna have a casual non-vegetarian meal.", 7,
//                "City Center",  "16/05/2019  19:00 PM");
//
//
//
//        sev.addMeal(9, "Puzzles and Pizza", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", false); put("Kosher", true);
//                    put("Vegan", false); put("Vegetarian", true);}}, "Hosting a dinner for people who like games and puzzles! We're going to have pizza and have a good time.", 10,
//                "German Colony",  "15/06/2019  19:30 PM");
//
//
//
//
//        sev.addMeal(4, "Game of Thrones viewing dinner", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", true); put("Kosher", true);
//                    put("Vegan", false); put("Vegetarian", false);}}, "No spoilers! For people who don't want to get up at 4 a.m. to watch a TV show, we're going to screen the episode the next day. Not vegetarian-friendly! What does a dragon eat?", 7,
//                "Talbiya",  "07/05/2019  20:00 PM");
//
//
//
//
//        sev.addMeal(7, "Netflix and Grill", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", false); put("Kosher", true);
//                    put("Vegan", false); put("Vegetarian", false);}}, "Barbecue and episodes of Friends in the background. Perfect for people who don't want a lot of conversation.", 5,
//                "Nahlaot",  "30/05/2019  18:00 PM");
//
//
//
//
//        sev.addMeal(2, "Shabbat Dinner", new HashSet<String>(),
//                new HashMap<String, Boolean>(){{put("Halal", false); put("Kosher", true);
//                    put("Vegan", false); put("Vegetarian", false);}}, "Traditional Shabbat dinner with home-made food like Grandma makes. Shomer-Shabbat friendly, so leave your phone at home.", 6,
//                "French Hill",  "24/05/2019  19:30 PM");
//
//
//        sev.addUserToMeal(7,10);
//        sev.addUserToMeal(6,4);
//        sev.addUserToMeal(3,0);
//        sev.addUserToMeal(9,3);
//        sev.addUserToMeal(4,4);
//        sev.addUserToMeal(7,0);
//        sev.addUserToMeal(0,4);
//        sev.addUserToMeal(1,12);
//        sev.addUserToMeal(9,6);
//        sev.addUserToMeal(6,0);
//        sev.addUserToMeal(4,7);
//        sev.addUserToMeal(4,11);
//        sev.addUserToMeal(5,4);
//        sev.addUserToMeal(9,4);
//        sev.addUserToMeal(1,6);
//        sev.addUserToMeal(0,7);
//        sev.addUserToMeal(2,1);
//        sev.addUserToMeal(3,2);
//        sev.addUserToMeal(1,0);
//        sev.addUserToMeal(1,9);
//        sev.addUserToMeal(4,0);
//        sev.addUserToMeal(5,5);
//        sev.addUserToMeal(6,12);
//        sev.addUserToMeal(8,8);
//        sev.addUserToMeal(3,1);
//        sev.addUserToMeal(4,1);
//        sev.addUserToMeal(4,0);
//        sev.addUserToMeal(9,4);
//        sev.addUserToMeal(0,12);
//        sev.addUserToMeal(3,5);
//        sev.addUserToMeal(3,11);
//        sev.addUserToMeal(8,6);
//        sev.addUserToMeal(4,9);
//        sev.addUserToMeal(7,11);
//        sev.addUserToMeal(9,9);
//        sev.addUserToMeal(9,3);
//        sev.addUserToMeal(7,4);
//        sev.addUserToMeal(9,10);
//        sev.addUserToMeal(0,9);
//        sev.addUserToMeal(7,2);
//        sev.addUserToMeal(2,1);
//        sev.addUserToMeal(2,7);
//        sev.addUserToMeal(6,0);
//        sev.addUserToMeal(9,2);
//        sev.addUserToMeal(1,10);
//        sev.addUserToMeal(8,0);
//        sev.addUserToMeal(9,3);
//        sev.addUserToMeal(2,6);
//        sev.addUserToMeal(6,5);
//        sev.addUserToMeal(9,1);
//        sev.addUserToMeal(8,10);
//        sev.addUserToMeal(0,11);
//        sev.addUserToMeal(1,9);
//        sev.addUserToMeal(1,8);
//        sev.addUserToMeal(7,7);
//        sev.addUserToMeal(1,7);
//        sev.addUserToMeal(9,8);
//        sev.addUserToMeal(2,1);
//        sev.addUserToMeal(4,10);
//        sev.addUserToMeal(3,8);
//        sev.addUserToMeal(5,12);
//        sev.addUserToMeal(2,9);
//        sev.addUserToMeal(9,7);
//        sev.addUserToMeal(3,4);
//        sev.addUserToMeal(9,10);
//        sev.addUserToMeal(4,2);
//        sev.addUserToMeal(7,4);
//        sev.addUserToMeal(5,2);
//        sev.addUserToMeal(5,9);
//        sev.addUserToMeal(0,8);
//        sev.addUserToMeal(8,11);
//        sev.addUserToMeal(1,1);
//        sev.addUserToMeal(3,3);
//        sev.addUserToMeal(4,12);
//        sev.addUserToMeal(3,6);
//        sev.addUserToMeal(7,4);
//        sev.addUserToMeal(2,3);
//        sev.addUserToMeal(7,6);
//        sev.addUserToMeal(3,7);
//        sev.addUserToMeal(4,8);
//        sev.addUserToMeal(5,1);
//        sev.addUserToMeal(9,2);
//        sev.addUserToMeal(0,10);
//        sev.addUserToMeal(3,2);
//        sev.addUserToMeal(7,8);
//        sev.addUserToMeal(9,0);
//        sev.addUserToMeal(4,6);
//        sev.addUserToMeal(2,6);
//        sev.addUserToMeal(8,5);
//        sev.addUserToMeal(0,6);
//        sev.addUserToMeal(5,8);
//        sev.addUserToMeal(8,5);
//        sev.addUserToMeal(5,1);
//        sev.addUserToMeal(7,9);
//        sev.addUserToMeal(4,1);
//        sev.addUserToMeal(0,10);
//        sev.addUserToMeal(1,2);
//        sev.addUserToMeal(4,9);
//        sev.addUserToMeal(3,0);
//        sev.addUserToMeal(0,3);
    }



}
