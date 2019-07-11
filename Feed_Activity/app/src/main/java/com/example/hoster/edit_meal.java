package com.example.hoster;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.storage.StorageReference;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class edit_meal extends AppCompatActivity {

    private ImageButton accept;

    private Button cancel;
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
    Map<String, Boolean> foodRests;
    Map<String, String> needed;
    int maxGuests;
    private Activity mActivity;
    CheckBox beer;
    CheckBox drinks;
    CheckBox dessert;
    CheckBox flowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meal);
        mActivity = this;

        mSorageRef = Server.getInstance().storage.getReference();
        final String uId = MainActivity.userId;
        final User[] user = new User[1]; // server will insert our user here
        Bundle b = getIntent().getExtras();
        final Meal meal = (Meal) b.getSerializable("meal");
        final String mealId = (String) b.getSerializable("mealId");
        foodRests = meal.getRestrictions();
        needed = meal.getNeeded();
        cancel = findViewById(R.id.closePopUpButton_edit);


        title = (EditText) findViewById(R.id.mealTitleAdd_edit);
        title.setText(meal.getTitle());
        pickDate = (EditText) findViewById(R.id.mealDateAdd_edit);
        pickDate.setKeyListener(null);
        pickDate.setFocusable(false);
        pickDate.setText(meal.getTime());

        date = meal.getTime();
        description = (EditText) findViewById(R.id.mealDescriptionAdd_edit);
        description.setText(meal.getDescription());
        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.number_picker_edit);
        numberPicker.setValue(meal.getMaxGuests());
        numberPicker.setUnit(1);

        /* set location autocomplete */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, NEIGHBORHOODS_JLM);


        location = (AutoCompleteTextView) findViewById(R.id.location_edit);
        location.setText(meal.getLocation());
        location.setAdapter(adapter);

        kosher = (CheckBox)findViewById(R.id.Kosher_edit);
        Halal = (CheckBox)findViewById(R.id.Halal_edit);
        Vegan = (CheckBox)findViewById(R.id.vegan_edit);
        Veggie = (CheckBox)findViewById(R.id.Vegetarian_edit);

        beer = (CheckBox) findViewById(R.id.beer_edit);
        drinks = (CheckBox) findViewById(R.id.drink_edit);
        flowers = (CheckBox) findViewById(R.id.flower_edit);
        dessert = (CheckBox) findViewById(R.id.desert_edit);

        kosher.setChecked(foodRests.get("Kosher"));
        Halal.setChecked(foodRests.get("Kosher"));
        Vegan.setChecked(foodRests.get("Vegan"));
        Veggie.setChecked(foodRests.get("Vegetarian"));

        if(needed.get("beer")!=null){
            beer.setChecked(true);
        }
        if(needed.get("drinks")!=null){
            drinks.setChecked(true);
        }
        if(needed.get("dessert")!=null){
            dessert.setChecked(true);
        }
        if(needed.get("flowers")!=null){
            flowers.setChecked(true);
        }

        mSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());

        pickDate.setEnabled(true);
        pickDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();
                new DatePickerDialog(mActivity, mDateDataSet, mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        accept = findViewById(R.id.accept_edit);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = getIntent().getExtras();
                Intent mealIntent = new Intent(getApplicationContext(), Profile.class);
                b.putString("mealId", mealId);
                mealIntent.putExtras(b);

                String titleEdit = title.getText().toString();
                String descriptionEdit = description.getText().toString();
                loc = location.getText().toString();

                setFoodRests();
                maxGuests = numberPicker.getValue();

                if(beer.isChecked()){
                    needed.put("beer", Meal.NEEDED);
                }
                else{
                    needed.put("beer", null);
                }
                if(flowers.isChecked()){
                    needed.put("flowers", Meal.NEEDED);
                }
                else{
                    needed.put("flowers",null);
                }
                if(drinks.isChecked()){
                    needed.put("drinks", Meal.NEEDED);
                }
                else{
                    needed.put("drinks",null);
                }
                if(dessert.isChecked()){
                    needed.put("dessert", Meal.NEEDED);
                }
                else{
                    needed.put("dessert",null);
                }

                MainActivity.sev.editMeal(meal, titleEdit, descriptionEdit,
                        loc, date, maxGuests, foodRests, needed);

                Server.getInstance().getMeals(MainActivity.meals, MainActivity.adapter);

                finish();
            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });










    }

    private String getInfoFromTextbox(int id) {
        EditText name = (EditText) findViewById(id);
        return name.getText().toString();
    }


    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(mActivity, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    };

    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            String value = mSimpleDateFormat.format(mCalendar.getTime());
            pickDate.setText(value);
            date = value;
        }
    };

    private void setFoodRests(){

        if (kosher.isChecked()) {
            foodRests.put("Kosher", true);
        } else {
            foodRests.put("Kosher", false);
        }

        if (Halal.isChecked()) {
            foodRests.put("Halal", true);
        } else {
            foodRests.put("Halal", false);
        }

        if (Vegan.isChecked()) {
            foodRests.put("Vegan", true);
        } else {
            foodRests.put("Vegan", false);
        }

        if (Veggie.isChecked()) {
            foodRests.put("Vegetarian", true);
        } else {
            foodRests.put("Vegetarian", false);
        }
    }


    private static final String[] NEIGHBORHOODS_JLM = new String[] {
            "Musrara", "Ein Kerem", "Nachlaot", "Jewish Quarter", "Yemin Moshe",
            "Old City", "Machne Yehuda", "Emek Refaim", "Rehavia", "Rasko", "Talpiot",
            "Nayot", "Mamila", "Mea' She'arim", "Arnona", "Holyland", "Mishkanot She'ananim",
            "Motza", "Geula", "Katamon", "Kiryat haYovel", "Givat Ram", "Har Nof", "Migrash haRusim",
            "Bait vaGan", "Givat Shaul", "Malha", "Talbiya", "Makor Baruch", "Nachalat Shiv'a",
            "Geulim Bak'a", "Kerem Avraham", "Kiryat Menachem", "Givat Masuaa", "Givat Mordechai",
            "Kiryat haMemshala", "Hutzot haYotzer", "Ramat Sharet", "Romema", "Pat", "Sanhedriya",
            "Bucharim", "Mahane Israel", "She'arey Hesed", "Ir Ganim Gimel", "Hameshulash", "Ramat Beit Hakerem",
            "Makor Hayim", "Kiryat haLeom", "Ramat Deniya", "Tel Arza"
    };


}
