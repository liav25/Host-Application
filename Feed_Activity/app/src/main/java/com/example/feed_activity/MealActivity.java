package com.example.feed_activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MealActivity extends AppCompatActivity {
    private Button joinmeal;
    private TextView title;
    private TextView host;
    private TextView desc;
    private TextView time;
    private TextView restrictions;
    private TextView guests;
    private TextView loc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.constraint_meal_page);


        Toolbar toolbar = (Toolbar) findViewById(R.id.meal_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.TextYellow));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle b = getIntent().getExtras();
        final int mealId = b.getInt("mealId");

        title = (TextView)findViewById(R.id.textView4);
        title.setText(MainActivity.sev.getMeal(mealId).getTitle());

        host = (TextView)findViewById(R.id.MealHostName);
        final int hostId = MainActivity.sev.getMeal(mealId).getHostId();

        if (hostId == MainActivity.userId){
            host.setText("Host: You");
        } else {
            host.setText("Host: " + MainActivity.sev.getUser(hostId).getUsername());
        }



        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putInt("userId", hostId); //Your id
                Intent profileIntent = new Intent(getApplicationContext(), Profile.class);
                profileIntent.putExtras(b); //Put your id to your next Intent
                startActivity(profileIntent);
            }
        });

        desc = (TextView)findViewById(R.id.mealFoodRestrictions7);
        desc.setText( MainActivity.sev.getMeal(mealId).getDescription());

        time = (TextView)findViewById(R.id.mealPageDateTime);
        time.setText( MainActivity.sev.getMeal(mealId).getTime());

        guests = (TextView)findViewById(R.id.mealFoodRestrictions6);
        guests.setText(setGuestsString(mealId));


        loc = (TextView)findViewById(R.id.mealFoodRestrictions2);
        loc.setText(MainActivity.sev.getMeal(mealId).getLocation());

        joinmeal = (Button)findViewById(R.id.button);

        setColorOfButton(mealId);

        restrictions = (TextView)findViewById(R.id.mealFoodRestrictions);
        restrictions.setText(getRestrictionString(mealId));

        joinmeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBut(mealId);
            }
        });

    }

    private void onClickBut(int mealId){
        if (MainActivity.sev.isUserInMeal(MainActivity.userId, mealId)){

            Boolean flag = Server.getInstance().getMeal(mealId).getHostId() == MainActivity.userId;

            Server.getInstance().removeUserToMeal(MainActivity.userId, MainActivity.sev.getMeal(mealId).getID());
            if (flag) {
                finish();
                MainActivity.adapter.notifyDataSetChanged();
                return;
            }


        }  else if (!MainActivity.sev.getMeal(mealId).isFull()) { // not in meal and meal not full
            MainActivity.sev.addUserToMeal(MainActivity.userId, mealId);
        }

        MainActivity.adapter.notifyDataSetChanged();
        setColorOfButton(mealId);
        guests.setText(setGuestsString(mealId));
    }

    private void setColorOfButton(int mealId){
        if (MainActivity.sev.isUserInMeal(MainActivity.userId, mealId)){
            joinmeal.setText("Leave");
            joinmeal.setBackgroundColor(Color.GRAY);
        } else if (MainActivity.sev.getMeal(mealId).isFull()){ // meal is full
            joinmeal.setText("Full");
            joinmeal.setBackgroundColor(Color.GRAY);
        } else {
            joinmeal.setText("Join Meal");
            joinmeal.setBackgroundResource(R.color.TextYellow);
        }
    }

    private String setGuestsString(int mealId){
        String guestsString = "Guests:";


        if (MainActivity.sev.isUserInMeal(MainActivity.userId, mealId) &&
                (MainActivity.userId != MainActivity.sev.getMeal(mealId).getHostId())){
            // Our user is not the admin of the meal, but in the meal
            guestsString = guestsString + " You,";
        }

        for (int guestId : MainActivity.sev.getMeal(mealId).getGuests()) {
            if (guestId != MainActivity.sev.getMeal(mealId).getHostId() && guestId != MainActivity.userId) {
                // last condition becaause we replaced it with "you"
                guestsString = guestsString + " " + MainActivity.sev.getUser(guestId).getUsername();
            }

        }

        if(guestsString.endsWith(","))
        {
            guestsString = guestsString.substring(0,guestsString.length() - 1);
        }

        return guestsString;
    }


    private String getRestrictionString(int mealId){
        String restri = "Restrictions:";

        if (Server.getInstance().isRestricted(mealId,"Kosher")) {
            restri += " Kosher,";
        }

        if (Server.getInstance().isRestricted(mealId,"Halal")) {
            restri += " Halal,";
        }

        if (Server.getInstance().isRestricted(mealId,"Vegan")) {
            restri += " Vegan,";
        }


        if (Server.getInstance().isRestricted(mealId,"Vegetarian")) {
            restri += " Vegetarian,";
        }

        if(restri.endsWith(","))
        {
            restri = restri.substring(0,restri.length() - 1);
        }

        return restri;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}
