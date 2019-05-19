package com.example.feed_activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;

public class Profile extends AppCompatActivity {
    TextView name;
    TextView uni;
    TextView langs;
    TextView loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_profile);
        Bundle b = getIntent().getExtras();
        final String uId = b.getString("userId");


        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.TextYellow));

        name = (TextView)findViewById(R.id.name_of_profile);
        name.setText(Server.getInstance().getUser(uId).getUsername());

        uni = (TextView)findViewById(R.id.uni_name);
        uni.setText(Server.getInstance().getUser(uId).getUniversity());

        // TODO - ADD location to student, currently not in object

        langs = (TextView)findViewById(R.id.profile_langs);
        langs.setText(getLangsString(uId));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    private String getLangsString(String uId){
        HashSet<String> langs = new HashSet<String>(Server.getInstance().getUser(uId).getLangs());
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
