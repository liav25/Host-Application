package com.example.feed_activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Login extends AppCompatActivity {

    Button login;
    FirebaseAuth mAuth;
    public static Meal[] meal;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.TextYellow));
        login = (Button) findViewById(R.id.loginButton);
        meal = new Meal[1];

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feed_intent = new Intent(getApplicationContext(), MainActivity.class);

                stam();

                startActivity(feed_intent);
            }
        });
    }


    private void stam(){
        mAuth = FirebaseAuth.getInstance();
        final AtomicBoolean boo = new AtomicBoolean(false);
        final Meal[] mea = new Meal[1];
        mea[0] = Server.getInstance().getMeal(3);




        start();
    }

    private void start(){
        mAuth.signInWithEmailAndPassword( "email2@gmail.com", "password")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            MainActivity.userId = mAuth.getUid();
                            Log.d("win", "signInWithEmail:success");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("fail", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });
        // [END sign_in_with_email]
    }
}