package com.example.hoster;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoster.R;
import com.travijuu.numberpicker.library.NumberPicker;

public class HowWasItPop extends AppCompatActivity {
    // set default to 4 if the user didn't click but submitted
    final int[] curRank = new int[4];
    private ImageView profile;
    private TextView name;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_was_it_pop);

        // set view
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // percentage on screen the popup will take
        getWindow().setLayout((int) (width * .85), (int) (height * .4));

        Bundle b = getIntent().getExtras();
        final String userId = b.getString("userToRate");
        final int mealId = b.getInt("mealIdToRate");

        final ImageButton pepper1 = findViewById(R.id.pepper1);
        final ImageButton pepper2 = findViewById(R.id.pepper2);
        final ImageButton pepper3 = findViewById(R.id.pepper3);
        final ImageButton pepper4 = findViewById(R.id.pepper4);
        final ImageButton pepper5 = findViewById(R.id.pepper5);

        pepper1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curRank[0] = 1;
                pepper1.setAlpha((float)1);
                pepper2.setAlpha((float)0.3);
                pepper3.setAlpha((float)0.3);
                pepper4.setAlpha((float)0.3);
                pepper5.setAlpha((float)0.3);
            }
        });

        pepper2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curRank[0] = 2;
                pepper1.setAlpha((float)1);
                pepper2.setAlpha((float)1);
                pepper3.setAlpha((float)0.3);
                pepper4.setAlpha((float)0.3);
                pepper5.setAlpha((float)0.3);
            }
        });

        pepper3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curRank[0] = 3;
                pepper1.setAlpha((float)1);
                pepper2.setAlpha((float)1);
                pepper3.setAlpha((float)1);
                pepper4.setAlpha((float)0.3);
                pepper5.setAlpha((float)0.3);
            }
        });

        pepper4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curRank[0] = 4;
                pepper1.setAlpha((float)1);
                pepper2.setAlpha((float)1);
                pepper3.setAlpha((float)1);
                pepper4.setAlpha((float)1);
                pepper5.setAlpha((float)0.3);
            }
        });

        pepper5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curRank[0] = 5;
                pepper1.setAlpha((float)1);
                pepper2.setAlpha((float)1);
                pepper3.setAlpha((float)1);
                pepper4.setAlpha((float)1);
                pepper5.setAlpha((float)1);
            }
        });

        Button submitRank = findViewById(R.id.submitRankButton);
        submitRank.setOnClickListener(new View.OnClickListener() {
            /**
             * submit the ranking - update the server
             * leave the popup page and go to the Feed (default behavior)
             * @param v - submit button of ranking pop up clicked
             */
            @Override
            public void onClick(View v) {
                Intent ranking = new Intent(getApplicationContext(), MainActivity.class);
                // find out how to send the user id

                Server.getInstance().setRanking(curRank[0], userId, mealId, HowWasItPop.this);
                startActivity(ranking);
                finish();
            }
        });


        // close the popUp window for review
        Button closePopUp;
        closePopUp = findViewById(R.id.closeRevPopUpButton);
        closePopUp.setOnClickListener(new View.OnClickListener() {
            /**
             * the user exited the pop-up - send her to feed (default behavior)
             * @param view - x button on ranking pop up clicked
             */
            @Override
            public void onClick(View view) {
                Intent ranking = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(ranking);
                finish();
            }
        });

    }


}
