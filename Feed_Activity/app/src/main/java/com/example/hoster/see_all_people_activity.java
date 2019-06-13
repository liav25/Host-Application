package com.example.hoster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class see_all_people_activity extends AppCompatActivity {
    ArrayList<String> userIds;
    private ListView cardlist;
    private peopleListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_people_activity);
        Bundle b = getIntent().getExtras();
        userIds = b.getStringArrayList("user_ids");
        adapter = new peopleListAdapter(this,  R.layout.people_interaction_card
                , userIds);

        cardlist = findViewById(R.id.people_list);



        cardlist.setAdapter(adapter);
    }
}
