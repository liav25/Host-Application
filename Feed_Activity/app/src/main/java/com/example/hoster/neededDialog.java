package com.example.hoster;

import android.app.AlertDialog;
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
import android.widget.TextView;

public class neededDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needed_dialog);

        Bundle b = getIntent().getExtras();
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

        flowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        beer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
