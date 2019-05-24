package com.example.hoster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private TextView name;
    private TextView uni;
    private TextView langs;
    private TextView loc;
    private ImageButton pen;
    CircleImageView profile_image;
    Uri imageUri;

    private  final Integer[] IMAGES = {R.drawable.nilesh, R.drawable.nilesh, R.drawable.nilesh, R.drawable.nilesh,
            R.drawable.nilesh, R.drawable.nilesh, R.drawable.nilesh, R.drawable.nilesh};
    private ArrayList<Integer> arrayList = new ArrayList<Integer>();

    private final int PICK_IMAGE_REQUEST = 1;

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
        name = (TextView) findViewById(R.id.name_of_profile);
        profile_image = (CircleImageView) findViewById(R.id.profilepic);



        uni = (TextView) findViewById(R.id.uni_name);

        // TODO - ADD location to student, currently not in object

        langs = (TextView) findViewById(R.id.profile_langs);
        Server.getInstance().getUser(uId, user, name, uni, langs, profile_image);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (!uId.equals(MainActivity.userId)) {
            pen.setEnabled(false);
            pen.setVisibility(View.INVISIBLE);
        } else {
            pen.setVisibility(View.VISIBLE);
            pen.setEnabled(true);
        }

        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                Intent editProfileIntent = new Intent(getApplicationContext(), edit_profile.class);
                editProfileIntent.putExtras(b); //Put your id to your next Intent
                startActivity(editProfileIntent);
            }
        });
    }

    public static String getLangsString(ArrayList<String> langs) {
        String lang = "";

        for (String language : langs) {
            lang += " " + language + ",";
        }

        if (lang.endsWith(",")) {
            lang = lang.substring(0, lang.length() - 1);
        }

        return lang;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void uploadImage() {
        StorageReference storageReference = Server.getInstance().storageReference;
        if(imageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }



}
