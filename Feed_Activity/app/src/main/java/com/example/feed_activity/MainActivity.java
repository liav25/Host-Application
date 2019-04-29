package com.example.feed_activity;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addMealBut;
    static Server sev = Server.getInstance();
    ArrayList<Meal> meals;
    static int userId = sev.addUser("liavi", "mami", null,"hebrwe",
            new HashSet<String>(), new HashSet<String>());
    int s = sev.addUser("papa", "mami", null,"hebrwe",
            new HashSet<String>(), new HashSet<String>());
    private ListView cardlist;


    public static MealsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        meals = sev.getMeals();

        cardlist = (ListView)findViewById(R.id.mealCardList);

        adapter = new MealsListAdapter(this,  R.layout.feed_card
                , meals);

        cardlist.setAdapter(adapter);


        sev.addMeal(1, "Hi tom", new HashSet<String>(),
                new HashMap<String, Boolean>(), "this is tom", 6,"here",  "123");


        sev.addMeal(MainActivity.userId, "Hi tomisalz", new HashSet<String>(),
                new HashMap<String, Boolean>(), "this is tomas", 6,"here",  "123");



        addMealBut = findViewById(R.id.addMealButton);
        addMealBut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent newIntent = new Intent(getApplicationContext(), PopActivity.class);
                startActivity(newIntent);
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


        //The ERROR:
        //Make sure the content of your adapter is not modified from a background thread, but only from
        //the UI thread. Make sure your adapter calls notifyDataSetChanged() when its content changes.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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





}
