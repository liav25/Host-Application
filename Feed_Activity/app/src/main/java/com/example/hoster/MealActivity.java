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
import android.widget.TextView;

public class MealActivity extends AppCompatActivity {
    public static DataAdapter guestsAdapter;

    private Button joinmeal;
    private TextView title;
    private TextView host;
    private TextView desc;
    private TextView time;
    private TextView restrictions;
    private RecyclerView guestsRecycle;
    private TextView loc;
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
            host.setText("Host: You");
        } else {
            Server.getInstance().getUsername(hostId, host);
        }



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

//        guests = MainActivity.sev.getMeal(mealId).getGuestsPictures(); // todo
//
//        guestsAdapter = new DataAdapter(this, guests_pics);
//
//        guestsRecycle = (RecyclerView)findViewById(R.id.mealFoodRestrictions6);
//        guestsRecycle.setLayoutManager(new LinearLayoutManager(this,
//                LinearLayoutManager.HORIZONTAL,false));
//        guestsRecycle.addItemDecoration(new OverlapDecoration());
//        guestsRecycle.setHasFixedSize(true);
//        guestsRecycle.setAdapter(guestsAdapter);

        loc = (TextView)findViewById(R.id.mealFoodRestrictions2);
        loc.setText(meal.getLocation());

        joinmeal = (Button)findViewById(R.id.button);

        setColorOfButton(meal);

        restrictions = (TextView)findViewById(R.id.mealFoodRestrictions);
        restrictions.setText(getRestrictionString(meal));

        joinmeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBut(mealId, meal);
            }
        });

    }

    private void onClickBut(String mealId, Meal meal){
        if (meal.isMember(MainActivity.userId)){

            Boolean flag = meal.getHostId().equals(MainActivity.userId);

            Server.getInstance().removeUserToMeal(MainActivity.userId, mealId, meal.getHostId());
            meal.removeGuest(MainActivity.userId);
            if (flag) {
                finish();
                MainActivity.adapter.notifyDataSetChanged();
                return;
            }


        }  else if (!meal.isFull()) { // not in meal and meal not full
            MainActivity.sev.addUserToMeal(getApplicationContext(), MainActivity.userId, meal);
            meal.addGuest(MainActivity.userId);
        }

        MainActivity.adapter.notifyDataSetChanged();
        setColorOfButton(meal);
    }

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



