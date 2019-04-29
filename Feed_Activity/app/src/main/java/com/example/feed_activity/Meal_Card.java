package com.example.feed_activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.HashSet;

public class Meal_Card extends AppCompatActivity {
    Button join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        join = (Button)findViewById(R.id.JoinButton);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("afs");
            }
        });
        setContentView(R.layout.feed_card);
    }


}
