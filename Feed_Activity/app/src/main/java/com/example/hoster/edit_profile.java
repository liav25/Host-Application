package com.example.hoster;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class edit_profile extends AppCompatActivity {
    private TextView name;
    private TextView uni;
    private TextView langs;
    private TextView loc;
    private ImageButton accept;
    private ImageButton cancel;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(this.getResources().getColor(R.color.TextGrey));

        setContentView(R.layout.activity_edit_profile);

        final String uId = MainActivity.userId;

        final User[] user = new User[1]; // server will insert our user here

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.TextYellow));

        name = findViewById(R.id.editable_name_of_profile);
        name.setHint(MainActivity.user.getDisplayName());
        uni = findViewById(R.id.editable_uni_name);
        uni.setHint(MainActivity.user.getEmail());

        // TODO - ADD location to student, currently not in object
        //

        langs = findViewById(R.id.editable_profile_langs);
        Server.getInstance().getUser(uId, user, name, uni, langs);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //clicks
        accept = findViewById(R.id.accept);
        cancel = findViewById(R.id.cancel);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = getIntent().getExtras();
                Intent profileIntent = new Intent(getApplicationContext(), Profile.class);

                b.putString("userId", MainActivity.userId);
                profileIntent.putExtras(b);
                //Todo to get connection to sever and edit profile fields from there
                String newName = name.getText().toString();
                String newUni = uni.getText().toString();

                String[] stringLangs = langs.getText().toString().split(",");

                ArrayList<String> newLangs = new ArrayList<>(Arrays.asList(stringLangs));

                Server.getInstance().editUser(user[0], newName,null, newUni, newLangs );

                startActivity(profileIntent);
                finish();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("userId", MainActivity.userId);

                Intent profileIntent = new Intent(getApplicationContext(), Profile.class);
                profileIntent.putExtras(b); //Put your id to your next Intent
                startActivity(profileIntent);
            }
        });


    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
