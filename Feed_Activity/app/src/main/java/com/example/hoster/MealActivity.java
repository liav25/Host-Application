package com.example.hoster;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Map;
import java.util.Objects;

/**
 * Viewing a meal activity
 */
public class MealActivity extends AppCompatActivity {

    private Button joinmeal;
    private TextView title;
    private TextView host;
    private TextView desc;
    private TextView time;
    private TextView restrictions;
    private TextView neededString;
    private TextView bringString1;
    private TextView bringname1;
    private TextView bringString2;
    private TextView bringname2;
    private RecyclerView guestsRecycle;
    private TextView loc;
    private ImageButton pen;
    private ImageButton mail;


//    private ArrayList<String> guests;

//    private ArrayList<Integer> guests_pics;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.constraint_meal_page);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.TextYellow));




        Toolbar toolbar = (Toolbar) findViewById(R.id.meal_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.TextYellow));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle b = getIntent().getExtras();
        final String mealId = b.getString("mealId");

        final Meal meal = (Meal) b.getSerializable("meal");
        title = (TextView)findViewById(R.id.textView4);
        title.setText(meal.getTitle());

        host = (TextView)findViewById(R.id.MealHostName);
        final String hostId = meal.getHostId();

        if (hostId.equals(MainActivity.userId)){
            host.setText("Arrenged by You");
        } else {
            Server.getInstance().getUsername(hostId, host);
        }


        mail = findViewById(R.id.sendmail);

        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("userId", hostId); //Your id
                Intent profileIntent = new Intent(getApplicationContext(), Profile.class);
                profileIntent.putExtras(b); //Put your id to your next Intent
                startActivity(profileIntent);
            }
        });

        desc = (TextView)findViewById(R.id.mealFoodRestrictions7);
        desc.setText(meal.getDescription());

        time = (TextView)findViewById(R.id.mealPageDateTime);
        time.setText(meal.getTime());

        loc = (TextView)findViewById(R.id.mealFoodRestrictions2);
        loc.setText(meal.getLocation());

        joinmeal = (Button)findViewById(R.id.button);
        pen = findViewById(R.id.pencil_meal);

        try {
            if (!hostId.equals(MainActivity.userId)) {
                pen.setEnabled(false);
                pen.setVisibility(View.INVISIBLE);
                mail.setEnabled(false);
                mail.setVisibility(View.INVISIBLE);
            } else {
                pen.setVisibility(View.VISIBLE);
                pen.setEnabled(true);
                mail.setEnabled(true);
                mail.setVisibility(View.VISIBLE);
            }
        }

        catch (NullPointerException e){
            System.out.println("********** User Id"+MainActivity.userId);
        }

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Server.getInstance().sendAllMail(meal.getGuestsMails(), MealActivity.this,
                        meal.getTitle(), meal.getTime());
            }
        });

        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editMealIntent = new Intent(getApplicationContext(), edit_meal.class);
                Bundle b = new Bundle();
                b.putSerializable("meal", meal);
                b.putSerializable("mealId", mealId);
                editMealIntent.putExtras(b); //Put your id to your next Intent
                startActivity(editMealIntent);
                finish();
            }
        });

        setColorOfButton(meal);

        restrictions = (TextView)findViewById(R.id.mealFoodRestrictions);
        restrictions.setText(getRestrictionString(meal));

        neededString = (TextView) findViewById(R.id.still_needed);
        neededString.setText(getNeededString(meal));

        bringString1 = (TextView) findViewById(R.id.bringing1);
        bringString2 = (TextView) findViewById(R.id.bringing2);
        bringname1 = (TextView) findViewById(R.id.namebring1);
        bringname2 = (TextView) findViewById(R.id.namebring2);
        bringname1.setVisibility(View.INVISIBLE);
        bringname2.setVisibility(View.INVISIBLE);
        bringString2.setVisibility(View.INVISIBLE);
        bringString1.setVisibility(View.INVISIBLE);
        setBringing(meal);

        joinmeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBut(mealId, meal);
            }
        });

    }

    /*
    sets the requests for guest's participation
     */
    private void setBringing(Meal meal) {
        Map needed = meal.getNeeded();
        if(needed.get("beer")!=null) {
            if (!needed.get("beer").equals(Meal.NEEDED)) {
                bringString1.setVisibility(View.VISIBLE);
                bringString1.setText("Brings beer: ");
                bringname1.setVisibility(View.VISIBLE);
                MainActivity.sev.getUsername(needed.get("beer").toString(), bringname1);

                if (needed.get("drinks") != null) {
                    if (!needed.get("drinks").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings drinks: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("drinks").toString(), bringname2);
                    }
                }
                if (needed.get("flowers") != null) {
                    if (!needed.get("flowers").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings flowers: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("flowers").toString(), bringname2);
                    }
                }
                if (needed.get("dessert") != null) {
                    if (!needed.get("dessert").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings dessert: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("dessert").toString(), bringname2);
                    }
                }
            }
        }

        if(needed.get("drinks")!=null) {

            if (!needed.get("drinks").equals(Meal.NEEDED)) {
                bringString1.setVisibility(View.VISIBLE);
                bringString1.setText("Brings drinks: ");
                bringname1.setVisibility(View.VISIBLE);
                MainActivity.sev.getUsername(needed.get("drinks").toString(), bringname1);

                if (needed.get("beer") != null) {
                    if (!needed.get("beer").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings beer: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("beer").toString(), bringname2);
                    }
                }
                if (needed.get("flowers") != null) {
                    if (!needed.get("flowers").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings flowers: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("flowers").toString(), bringname2);
                    }
                }
                if (needed.get("dessert") != null) {
                    if (!needed.get("dessert").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings dessert: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("dessert").toString(), bringname2);
                    }
                }
            }
        }

        if(needed.get("flowers")!=null) {

            if (!needed.get("flowers").equals(Meal.NEEDED)) {
                bringString1.setVisibility(View.VISIBLE);
                bringString1.setText("Brings flowers: ");
                bringname1.setVisibility(View.VISIBLE);
                MainActivity.sev.getUsername(needed.get("flowers").toString(), bringname1);

                if (needed.get("drinks") != null) {
                    if (!needed.get("drinks").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings drinks: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("drinks").toString(), bringname2);
                    }
                }
                if (needed.get("beer") != null) {
                    if (!needed.get("beer").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings beer: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("beer").toString(), bringname2);
                    }
                }
                if (needed.get("dessert") != null) {
                    if (!needed.get("dessert").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings dessert: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("dessert").toString(), bringname2);
                    }
                }
            }
        }

        if(needed.get("dessert")!=null) {
            if (!needed.get("dessert").equals(Meal.NEEDED)) {
                bringString1.setVisibility(View.VISIBLE);
                bringString1.setText("Brings dessert: ");
                bringname1.setVisibility(View.VISIBLE);
                MainActivity.sev.getUsername(needed.get("dessert").toString(), bringname1);

                if (needed.get("drinks") != null) {
                    if (!needed.get("drinks").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings drinks: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("drinks").toString(), bringname2);
                    }
                }
                if (needed.get("flowers") != null) {
                    if (!needed.get("flowers").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings flowers: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("flowers").toString(), bringname2);
                    }
                }
                if (needed.get("beer") != null) {
                    if (!needed.get("beer").equals(Meal.NEEDED)) {
                        bringString2.setVisibility(View.VISIBLE);
                        bringString2.setText("Brings beer: ");
                        bringname2.setVisibility(View.VISIBLE);
                        MainActivity.sev.getUsername(needed.get("beer").toString(), bringname2);
                    }
                }
            }
        }
    }

    private void onClickBut(String mealId, Meal meal){
        if (meal.isMember(MainActivity.userId)){

            Boolean flag = meal.getHostId().equals(MainActivity.userId);

            Server.getInstance().removeUserToMeal(MainActivity.userId, mealId, meal.getHostId(), this,
                    MainActivity.userMail);
            meal.removeGuest(MainActivity.userId);
            if (flag) {
                finish();
                MainActivity.adapter.notifyDataSetChanged();
                return;
            }


        }  else if (!meal.isFull()) { // not in meal and meal not full
            MainActivity.sev.addUserToMeal(getApplicationContext(), MainActivity.userId, meal,
                    MainActivity.userMail);
            meal.addGuest(MainActivity.userId);
        }

        MainActivity.adapter.notifyDataSetChanged();
        setColorOfButton(meal);
    }

    /*
    sets the join meal button's color according to the guests' status
     */
    private void setColorOfButton(Meal meal){
        if (meal.isMember(MainActivity.userId)){
            joinmeal.setText("Leave");
            joinmeal.setBackgroundColor(Color.GRAY);
        } else if (meal.isFull()){ // meal is full
            joinmeal.setText("Full");
            joinmeal.setBackgroundColor(Color.GRAY);
        } else {
            joinmeal.setText("Join Meal");
            joinmeal.setBackgroundResource(R.color.TextYellow);
        }
    }

    private String setGuestsString(int mealId){
        String guestsString = "Guests:";


        if (MainActivity.sev.isUserInMeal(MainActivity.user.getUid(), mealId) &&
                (!MainActivity.user.getUid().equals(MainActivity.sev.getMeal(mealId).getHostId()))){
            // Our user is not the admin of the meal, but in the meal
            guestsString = guestsString + " You,";
        }

        for (String guestId : MainActivity.sev.getMeal(mealId).getGuests()) {
            if (!guestId.equals(MainActivity.sev.getMeal(mealId).getHostId()) && !guestId.equals(MainActivity.user.getUid())) {
                // last condition becaause we replaced it with "you"
                //guestsString = guestsString + " " + MainActivity.sev.getUser(guestId).getUsername() + ",";
                //TODO
            }

        }

        if(guestsString.endsWith(","))
        {
            guestsString = guestsString.substring(0,guestsString.length() - 1);
        }

        return guestsString;
    }


    private String getRestrictionString(Meal meal){
        String restri = "Restrictions:";

        if (meal.isRestricted("Kosher")) {
            restri += " Kosher,";
        }

        if (meal.isRestricted("Halal")) {
            restri += " Halal,";
        }

        if (meal.isRestricted("Vegan")) {
            restri += " Vegan,";
        }


        if (meal.isRestricted("Vegetarian")) {
            restri += " Vegetarian,";
        }

        if(restri.endsWith(","))
        {
            restri = restri.substring(0,restri.length() - 1);
        }

        return restri;
    }

    private String getNeededString(Meal meal){
        String nee = "you can also bring ";
        try {
            if (meal.getNeeded().get("beer").equals(Meal.NEEDED)) {
                nee += "beer, ";
            }
        }
        catch (NullPointerException e){        }
        try {
            if (meal.getNeeded().get("drinks").equals(Meal.NEEDED)) {
                nee += "drinks, ";
            }
        }
        catch (NullPointerException e){        }
        try {
            if (meal.getNeeded().get("dessert").equals(Meal.NEEDED)) {
                nee += "dessert, ";
            }
        }
        catch (NullPointerException e){        }
        try {
            if (meal.getNeeded().get("flowers").equals(Meal.NEEDED)) {
                nee += "or flowers";
            }
        }
        catch (NullPointerException e){
        }
        System.out.println("+++++++++" + meal.getNeeded().toString());
        if((meal.getNeeded().get("dessert")==null)&&(meal.getNeeded().get("drinks")==null)&&
                (meal.getNeeded().get("beer")==null)&&(meal.getNeeded().get("flowers")==null)){
            nee = "";
        }
        return nee+" !";

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public class OverlapDecoration extends RecyclerView.ItemDecoration {

        private final static int vertOverlap = -40;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            final int itemPosition = parent.getChildAdapterPosition(view);

            outRect.set(0, 0, vertOverlap, 0);


        }
    }
}



