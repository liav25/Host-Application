package com.example.hoster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    ImageButton addMealBut;
    Animation fromBottom;
    static Server sev = Server.getInstance();

    public static CircleImageView profilePicture;
    public static ArrayList<Meal> meals;
    public static FirebaseUser user;
    public static String userId;



    //**FOR POPUP**//


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
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        Animation fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        toolbar.setAnimation(fromtop);
        profilePicture = findViewById(R.id.profile_picture);
        sev.downloadProfilePic(profilePicture, userId);

        profilePicture.setAnimation(fromtop);
        addMealBut.setAnimation(fromBottom);
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

//
//    public void showPopup(){
//
//        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
//        View mView = getLayoutInflater().inflate(R.layout.needed_popup, null);
//        mBuilder.setView(mView);
//        final AlertDialog dialogPopup = mBuilder.create();
//
//                //** xml things **//
//        TextView thastOk;
//        Button flowers;
//        Button drinks;
//        Button beer;
//        Button dessert;
//        flowers = (Button) findViewById(R.id.flowers_button);
//        drinks = (Button) findViewById(R.id.drinks_button);
//        beer = (Button) findViewById(R.id.beer_button);
//        dessert = (Button) dialogPopup.findViewById(R.id.dessert_button);
//        thastOk = (TextView) dialogPopup.findViewById(R.id.thats_ok);
//        SpannableString content = new SpannableString("That's ok");
//        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
//        thastOk.setText(content);
//
//        flowers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogPopup.dismiss();
//
//            }
//        });
//
//        drinks.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogPopup.dismiss();
//
//            }
//        });
//
//        beer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogPopup.dismiss();
//
//            }
//        });
//
//        dessert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogPopup.dismiss();
//
//            }
//        });
//
//        thastOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogPopup.dismiss();
//            }
//        });
//        dialogPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogPopup.show();
//    }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }






}
