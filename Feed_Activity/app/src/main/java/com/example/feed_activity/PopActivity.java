package com.example.feed_activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.HashSet;

import static java.security.AccessController.getContext;

public class PopActivity extends Activity {
    Button createMeal;
    EditText pickDate;
    Button exit;
    DatePickerDialog dpd;
    TimePickerDialog tmd;
    String date;
    String time;
    Calendar c;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.add_new_meal_popup);
        pickDate = (EditText) findViewById(R.id.mealDateAdd);
        c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        String curr_date = (year + "/" + month + "/" + day + " "+hour+":"+minute);
        pickDate.setText(curr_date);

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //to get today's date & time



                dpd = new DatePickerDialog(PopActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        //date = (mDay + "/" + (mMonth + 1) + "/" + mYear);
                        /*this is not wotking:
                            i am trying to make the "ok" button in datePicker
                            to move to another dialog of TimePicker
                            NEED FIX
                        */
                        /*
                        Button ok = (Button) dpd.getButton(dpd.BUTTON_POSITIVE);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TimePickerDialog tpd = new TimePickerDialog(getBaseContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        time = (hourOfDay+ ":" + minute);
                                    }
                                },1,2,true);
                                tpd.show();
                            }
                        });
                        */
                        date = (mDay + "/" + (mMonth + 1) + "/" + mYear + " "+ time);
                        pickDate.setText(date);
                    }
                }, 2019, 04, 26);
                dpd.show();

            }
                                });



                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                int width = dm.widthPixels;
                int height = dm.heightPixels;

                getWindow().setLayout((int) (width * .8), (int) (height * .7));

                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.gravity = Gravity.CENTER;
                params.x = 0;
                params.y = -20;

                getWindow().setAttributes(params);

                /* set create meal to add meal on server */
                createMeal = findViewById(R.id.addNewMeal);
                createMeal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText titleAdd = (EditText) findViewById(R.id.mealTitleAdd);
                        String title = titleAdd.getText().toString();
                        String description = getInfoFromTextbox(R.id.mealDescriptionAdd);

                        MainActivity.sev.addMeal(MainActivity.userId, title, new HashSet<String>(),
                                new HashMap<String, Boolean>(), description, 6, "here", date);

                        MainActivity.adapter.notifyDataSetChanged();
                        finish();
                    }

                });

                Button closePopUp;
                closePopUp = findViewById(R.id.closePopUpButton);
                closePopUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();

                    }
                });

            }


            /**
             * receives data from user by id
             * @param id - textbox ID
             * @return String representing that data
             */
            private String getInfoFromTextbox(int id) {
                EditText name = (EditText) findViewById(id);
                return name.getText().toString();
            }


    ;}


