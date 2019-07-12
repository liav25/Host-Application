package com.example.hoster;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText email;
    EditText pass;
    final static int MIN_PASS_CHAR = 6;
    final static int MIN_EMAIL_CHAR = 1;
    Button login;
    public static FirebaseAuth mAuth;
    TextView toRegister;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.TextYellow));
        login = (Button) findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();
        //TODO - this part is for staying logged in

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            MainActivity.userId = user.getUid();
            MainActivity.userMail = user.getEmail();
            MainActivity.user = user;
            Intent feed_intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(feed_intent);

            Log.d("win", "signInWithEmail:success");
            finish();

        }

        email = findViewById(R.id.userNameInputField);
        pass = findViewById(R.id.editText3);
        toRegister = findViewById(R.id.to_register);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String em = email.getText().toString();
                final String pas = pass.getText().toString();
                if(em.length() > MIN_EMAIL_CHAR && pas.length() >= MIN_PASS_CHAR) {
                    signIn(em, pas);
                }


            }
        });

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(getApplicationContext(), Register.class);
                startActivity(registerIntent);
                finish();
            }
        });
    }


    public void signIn(final String email, final String password){
        mAuth.signInWithEmailAndPassword( email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            MainActivity.userId = mAuth.getUid();
                            MainActivity.userMail = email;
                            MainActivity.user = mAuth.getCurrentUser();
                            Intent feed_intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(feed_intent);

                            Log.d("win", "signInWithEmail:success");
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("fail", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Wrong email or password",
                                    Toast.LENGTH_SHORT).show();
                            System.out.println(email +" " + password);

                        }


                    }
                });
        // [END sign_in_with_email]
    }
}