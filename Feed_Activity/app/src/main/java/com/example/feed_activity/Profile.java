package com.example.feed_activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class Profile extends AppCompatActivity {
    private TextView name;
    private TextView uni;
    private TextView langs;
    private TextView loc;
    private ImageButton pen;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(this.getResources().getColor(R.color.TextYellow));
        setContentView(R.layout.activity_profile);
        Bundle b = getIntent().getExtras();
        final String uId = b.getString("userId");

        User[] user = new User[1]; // server will insert our user here
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.TextYellow));
        pen = findViewById(R.id.pencil);
        name = (TextView)findViewById(R.id.name_of_profile);

        uni = (TextView)findViewById(R.id.uni_name);

        // TODO - ADD location to student, currently not in object

        langs = (TextView)findViewById(R.id.profile_langs);
        Server.getInstance().getUser(uId, user, name, uni, langs);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if(!uId.equals(MainActivity.userId)){
            pen.setEnabled(false);
            pen.setVisibility(View.INVISIBLE);
        } else {
            pen.setVisibility(View.VISIBLE);
            pen.setEnabled(true);
        }

    }

    public static String getLangsString(ArrayList<String> langs){
        String lang = "";

        for (String language : langs){
            lang += " " + language + ",";
        }

        if(lang.endsWith(","))
        {
            lang = lang.substring(0,lang.length() - 1);
        }

        return lang;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
