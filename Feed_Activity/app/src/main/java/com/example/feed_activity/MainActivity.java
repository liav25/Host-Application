package com.example.feed_activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addMealBut;
    Animation fromBottom;
    static Server sev = Server.getInstance();
    ArrayList<Meal> meals;

    static int userId = sev.addUser("Esti Levi", "password", null,"Hebrew University",
            new HashSet<String>(Arrays.asList("Hebrew", "French")), new HashSet<String>());



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorAccent));

            Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        //getSupportActionBar().setBackgroundDrawable(getDrawable(getResources().getDrawable(R.drawable.header)));


        meals = sev.getMeals();

        
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
                b.putInt("userId", userId); //Your id
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



}
