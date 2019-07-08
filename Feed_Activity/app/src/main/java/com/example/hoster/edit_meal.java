package com.example.hoster;

import android.app.Activity;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class edit_meal extends AppCompatActivity {

    private ImageButton accept;
    private ImageButton cancel;
    private StorageReference mSorageRef;
    EditText pickDate;
    String date;
    EditText title;
    EditText description;
    Calendar mCalendar;
    SimpleDateFormat mSimpleDateFormat;
    CheckBox kosher;
    CheckBox Halal;
    CheckBox Vegan;
    CheckBox Veggie;
    AutoCompleteTextView location;
    String loc;
    HashMap<String, Boolean> foodRests;
    int maxGuests;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meal);

        mSorageRef = Server.getInstance().storage.getReference();
        final String uId = MainActivity.userId;
        final User[] user = new User[1]; // server will insert our user here
        Bundle b = getIntent().getExtras();
        final Meal meal = (Meal) b.getSerializable("meal");
        final String mealId = (String) b.getSerializable("mealId");

        title = (EditText) findViewById(R.id.mealTitleAdd_edit);
        title.setHint(meal.getTitle());
        pickDate = (EditText) findViewById(R.id.mealDateAdd_edit);
        pickDate.setHint(meal.getTime());
        description = (EditText) findViewById(R.id.mealDescriptionAdd_edit);
        description.setHint(meal.getDescription());
        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.number_picker_edit);
        numberPicker.setValue(meal.getMaxGuests());
        numberPicker.setUnit(1);
        location = (AutoCompleteTextView) findViewById(R.id.location_edit);
        location.setHint(meal.getLocation());







    }

}
