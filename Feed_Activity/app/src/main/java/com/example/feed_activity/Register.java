package com.example.feed_activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;

public class Register extends AppCompatActivity {

    String password;
    String userName;
    String emailAddress;
    Button createUser;
    TextView loginbut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        FirebaseAuth mAtuh = FirebaseAuth.getInstance();
        loginbut = findViewById(R.id.loginButtonInSignup);
        mAtuh.signOut();
        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(), Login.class);
                startActivity(login);
                finish();
            }
        });

        if (mAtuh.getCurrentUser() != null){

            Intent main = new Intent(getApplicationContext(), MainActivity.class);
            MainActivity.user = mAtuh.getCurrentUser();
            MainActivity.userId = mAtuh.getCurrentUser().getUid();
            startActivity(main);
            finish();
        }


        /* set create meal to add meal on server */
        createUser = findViewById(R.id.signUpButton);
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = getInfoFromTextbox(R.id.insertPasswordSU);

                emailAddress = getInfoFromTextbox(R.id.insertEmailSU);

                MainActivity.sev.signUp(emailAddress, password);

                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }

        });
    }

    /**
     * receives data from user by i"d
     *
     * @param id - textbox ID
     * @return String representing that data
     */
    private String getInfoFromTextbox(int id) {
        EditText name = (EditText) findViewById(id);
        return name.getText().toString();
    }
}