package com.example.hoster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private CircleImageView img1;
    private CircleImageView img2;
    private CircleImageView img3;
    private CircleImageView img4;
    private CircleImageView img5;
    private CircleImageView more_profiles;
    private TextView mutual_title;
    private LinearLayout mutual_frame;
    private CircleImageView profile_image;
    private Uri imageUri;
    private ArrayList<String> mutuals;
    private final int PICK_IMAGE_REQUEST = 1;
    private String uId;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(this.getResources().getColor(R.color.TextYellow));
        setContentView(R.layout.activity_profile);
        Bundle b = getIntent().getExtras();
        uId = b.getString("userId");

        User[] user = new User[1]; // server will insert our user here
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.TextYellow));
        pen = findViewById(R.id.pencil);
        name = (TextView) findViewById(R.id.name_of_profile);
        profile_image = (CircleImageView) findViewById(R.id.profilepic);

        /* mutuals setup*/
        img1 = findViewById(R.id.profile_stud1);
        img2 = findViewById(R.id.profile_stud2);
        img3 = findViewById(R.id.profile_stud3);
        img4 = findViewById(R.id.profile_stud4);
        img5 = findViewById(R.id.profile_stud5);
        more_profiles = findViewById(R.id.see_more_mutual);
        mutual_title = findViewById(R.id.mutual_title);
        mutual_frame = findViewById(R.id.mutual_frame);
        mutuals = new ArrayList<>();


        uni = (TextView) findViewById(R.id.uni_name);

        // TODO - ADD location to student, currently not in object

        langs = (TextView) findViewById(R.id.profile_langs);
        Server.getInstance().getUser(uId, user, name, uni, langs, profile_image, mutuals, this);
        Server.getInstance().downloadProfilePic(profile_image, uId);
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
                finish();
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
    private void onClickImage(String uId, Context mContext){

        Bundle b = new Bundle();
        b.putString("userId", uId); //Your id
        Intent profileIntent = new Intent(mContext, Profile.class);
        profileIntent.putExtras(b); //Put your id to your next Intent
        mContext.startActivity(profileIntent);

    }

    /**
     * Sets the images for mutual connections with this user
     */
    public void setImages(){
        if (!uId.equals(MainActivity.userId) && mutuals.size() >= 1){
            if (mutuals.size() == 6){
                more_profiles.setVisibility(View.VISIBLE);

            more_profiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putStringArrayList("user_ids", mutuals); //Your id
                    Intent more_profiles = new Intent(Profile.this, see_all_people_activity.class);
                    more_profiles.putExtras(b); //Put your id to your next Intent
                    Profile.this.startActivity(more_profiles);
                }
            });
                // TODO - send to a different activity of array with all members of meal
            } else {
                more_profiles.setVisibility(View.INVISIBLE);
            }

            if (mutuals.size() >= 5){
                img5.setVisibility(View.VISIBLE);

                Server.getInstance().downloadProfilePic(img5, mutuals.get(4));
                img5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(mutuals.get(4), Profile.this);
                    }
                });
            }else {
                img5.setVisibility(View.INVISIBLE);
            }

            if (mutuals.size() >= 4){
                img4.setVisibility(View.VISIBLE);

                Server.getInstance().downloadProfilePic(img4, mutuals.get(3));
                img4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(mutuals.get(3), Profile.this);
                    }
                });
            }else {
                img4.setVisibility(View.INVISIBLE);
            }

            if (mutuals.size() >= 3){
                img3.setVisibility(View.VISIBLE);

                Server.getInstance().downloadProfilePic(img3, mutuals.get(2));
                img3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(mutuals.get(2), Profile.this);
                    }
                });
            }else {
                img3.setVisibility(View.INVISIBLE);
            }

            if (mutuals.size() >= 2){
                img2.setVisibility(View.VISIBLE);

                Server.getInstance().downloadProfilePic(img2, mutuals.get(1));
                img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(mutuals.get(1), Profile.this);
                    }
                });
            } else {
                img2.setVisibility(View.INVISIBLE);
            }

            if (mutuals.size() >= 1){
                Server.getInstance().downloadProfilePic(img1, mutuals.get(0));
                img1.setVisibility(View.VISIBLE);
                mutual_title.setVisibility(View.VISIBLE);
                mutual_frame.setVisibility(View.VISIBLE);
                img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(mutuals.get(0), Profile.this);
                    }
                });

            }

        } else {
            img1.setVisibility(View.INVISIBLE);
            mutual_title.setVisibility(View.INVISIBLE);
            mutual_frame.setVisibility(View.INVISIBLE);
        }
    }
}
