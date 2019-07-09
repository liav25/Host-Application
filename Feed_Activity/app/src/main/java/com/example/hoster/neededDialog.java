package com.example.hoster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;

public class neededDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needed_dialog);



        final Bundle b = getIntent().getExtras();
        Intent popIntent = getIntent();

        //** xml things **//
        TextView thastOk;
        Button flowers;
        Button drinks;
        Button beer;
        Button dessert;
        flowers = (Button) findViewById(R.id.flowers_button);
        drinks = (Button) findViewById(R.id.drinks_button);
        beer = (Button) findViewById(R.id.beer_button);
        dessert = (Button) findViewById(R.id.dessert_button);
        thastOk = (TextView) findViewById(R.id.thats_ok);
        SpannableString content = new SpannableString("That's ok");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        thastOk.setText(content);

        final Meal meal = (Meal) b.getSerializable("meal");

        flowers.setVisibility(View.INVISIBLE);
        beer.setVisibility(View.INVISIBLE);
        drinks.setVisibility(View.INVISIBLE);
        dessert.setVisibility(View.INVISIBLE);

        try {
            if (meal.getNeeded().get("beer").equals(Meal.NEEDED)) {
                beer.setVisibility(View.VISIBLE);
            }
        }
        catch (NullPointerException e){

        }

        try {
            if (meal.getNeeded().get("drinks").equals(Meal.NEEDED)) {
                drinks.setVisibility(View.VISIBLE);
            }
        }
        catch (NullPointerException e){
        }
        try {
            if (meal.getNeeded().get("dessert").equals(Meal.NEEDED)) {
                dessert.setVisibility(View.VISIBLE);
        }
        }
        catch (NullPointerException e){

        }
        try {
            if (meal.getNeeded().get("flowers").equals(Meal.NEEDED)) {
                flowers.setVisibility(View.VISIBLE);
            }
        }
        catch (NullPointerException e){

        }

         RelativeLayout.LayoutParams dessertParams = (RelativeLayout.LayoutParams) dessert.getLayoutParams();
            RelativeLayout.LayoutParams beerParams = (RelativeLayout.LayoutParams) beer.getLayoutParams();
            RelativeLayout.LayoutParams flowersParams = (RelativeLayout.LayoutParams) flowers.getLayoutParams();
            RelativeLayout.LayoutParams drinksParams = (RelativeLayout.LayoutParams) drinks.getLayoutParams();

        dessertParams.addRule(RelativeLayout.BELOW, R.id.sep_needed);
        beerParams.addRule(RelativeLayout.BELOW, R.id.dessert_button);
        flowersParams.addRule(RelativeLayout.BELOW, R.id.beer_button);
        drinksParams.addRule(RelativeLayout.BELOW, R.id.flowers_button);

        if(meal.getNeeded().get("dessert")==null){
            beerParams.addRule(RelativeLayout.BELOW, R.id.sep_needed);
            if(meal.getNeeded().get("beer")==null){
                flowersParams.addRule(RelativeLayout.BELOW, R.id.sep_needed);
                if(meal.getNeeded().get("flowers")==null){
                    drinksParams.addRule(RelativeLayout.BELOW, R.id.sep_needed);
                }
            }
        }
        if(meal.getNeeded().get("beer")==null){
            flowersParams.addRule(RelativeLayout.BELOW, R.id.dessert_button);
            if(meal.getNeeded().get("flowers")==null){
                drinksParams.addRule(RelativeLayout.BELOW, R.id.dessert_button);
            }
        }
        if(meal.getNeeded().get("flowers")==null){
            drinksParams.addRule(RelativeLayout.BELOW, R.id.beer_button);
        }




        flowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = b.getString("userId");
                Map<String,String> needed = meal.getNeeded();
                needed.put("flowers", username);
                MainActivity.sev.editMeal(meal, needed);
                MainActivity.adapter.notifyDataSetChanged();
                finish();
            }
        });

        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = b.getString("userId");
                Map<String,String> needed = meal.getNeeded();
                needed.put("drinks", username);
                MainActivity.sev.editMeal(meal, needed);
                MainActivity.adapter.notifyDataSetChanged();

                finish();
            }
        });

        beer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = b.getString("userId");
                Map<String,String> needed = meal.getNeeded();
                needed.put("beer", username);
                MainActivity.sev.editMeal(meal, needed);
                MainActivity.adapter.notifyDataSetChanged();

                finish();
            }
        });

        dessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = b.getString("userId");
                Map<String,String> needed = meal.getNeeded();
                needed.put("dessert", username);
                MainActivity.sev.editMeal(meal, needed);
                MainActivity.adapter.notifyDataSetChanged();

                finish();
            }
        });

        thastOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
