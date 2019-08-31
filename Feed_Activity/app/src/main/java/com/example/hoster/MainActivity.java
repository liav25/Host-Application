package com.example.hoster;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

/**
 * Main activity of this app. contains the feed
 */
public class MainActivity extends AppCompatActivity {
    ImageView addMealBut;
    static Server sev = Server.getInstance();

    public static CircleImageView profilePicture;
    public static ArrayList<Meal> meals;
    public static FirebaseUser user;
    public static String userId;
    public static String userMail;
    private LocationManager locationManager;
    private LocationListener sortLocListener;
    private LocationListener filterLocListener;
    private Location curLoc;
    //**FOR POPUP**//
    private String[] sortby = {"None", "Date", "Distance", "Alphabetically"};
    private String[] filterby = {"None", "All", "My Meals", "I'm Hosting"};
    private int distanceFilter = 0;
    private int timeFilter = 0;

    private boolean Halal = false;
    private boolean Kosher = false;
    private boolean vegan = false;
    private boolean veggie = false;


    private ListView cardlist;

    public static MealsListAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(this.getResources().getColor(R.color.toolbarcolor));
        setContentView(R.layout.activity_main);

        meals = new ArrayList<>();
        adapter = new MealsListAdapter(this,  R.layout.feed_card
                , meals);

        sev.getMeals(meals, adapter); // loads meals from server
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        cardlist = findViewById(R.id.mealCardList);

        cardlist.setAdapter(adapter);

        addMealBut = findViewById(R.id.addMealButton);
        Animation fromBottom2 = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        Animation fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        toolbar.setAnimation(fromtop);
        addMealBut.setAnimation(fromBottom2);

        /* handles the profile pictures on the left side of this feed*/
        profilePicture = findViewById(R.id.profile_picture);
        sev.downloadProfilePic(profilePicture, userId);
        profilePicture.setAnimation(fromtop);

        Bundle bun = new Bundle();



        addMealBut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent newIntent = new Intent(getApplicationContext(), PopActivity.class);
                startActivity(newIntent);
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("userId", userId); //Your id
                Intent profileIntent = new Intent(getApplicationContext(), Profile.class);
                profileIntent.putExtras(b); //Put your id to your next Intent
                startActivity(profileIntent);
            }
        });

        cardlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            Toast.makeText(getApplicationContext(),
                    "Click ListItem Number " + position, Toast.LENGTH_LONG)
                    .show();
            }
        });

        /* handles location for soriting and filtering */
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        sortLocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                curLoc = location;
                sortByDistance();
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                //
            }

            @Override
            public void onProviderEnabled(String s) {
                //
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        filterLocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                curLoc = location;
                filterByDistance();
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                //
            }

            @Override
            public void onProviderEnabled(String s) {
                //
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 10:
                getLocationForSort();
                break;
            default:
                break;
        }
    }
    /* gets the geo location for sorting */
    void getLocationForSort() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request_permission();
            }
        } else {
            // permission has been granted
            locationManager.requestLocationUpdates("gps", 5000, 0, sortLocListener);

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void request_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                ACCESS_COARSE_LOCATION)) {

            Toast.makeText(this, "Need permissions", Toast.LENGTH_SHORT).show();
        } else {
            // permission has not been granted yet. Request it directly.
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;  //for visible 3 dots change to true, hiding false
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /**
         *Look Here Tom
         * http://www.androidhiro.com/source/android/example/dialogplus/1092
         * This is dialog plus
         * continue with this, that what you wanted
         */

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {

            final DialogPlus dialog = DialogPlus.newDialog(this)
                    .setGravity(Gravity.TOP)
                    .setContentHolder(new ViewHolder(R.layout.top_dialog)).
                    setCancelable(true)
                    .create();


            Spinner sort = (Spinner)dialog.findViewById(R.id.sortby);
            Spinner seeOnly = (Spinner)dialog.findViewById(R.id.seeOnly);
            final SeekBar maxdays = (SeekBar)dialog.findViewById(R.id.maxdays);
            final SeekBar maxdist = (SeekBar) dialog.findViewById(R.id.maxdistance);
            final TextView maxDaysCount = (TextView)dialog.findViewById(R.id.max_days_text);
            final TextView maxDistShow = (TextView)dialog.findViewById(R.id.max_dist_show);
            maxdist.setProgress(distanceFilter);
            maxdays.setProgress(timeFilter);
            maxDaysCount.setText(String.valueOf(timeFilter));
            maxDistShow.setText(String.valueOf(distanceFilter));

            maxdays.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    maxDaysCount.setText(String.valueOf(progress));
                    timeFilter = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            maxdist.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    maxDistShow.setText(String.valueOf(progress));
                    distanceFilter = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            ArrayAdapter<String> sortAd =
                    new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, sortby);

            ArrayAdapter<String> filtAd =
                    new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, filterby);

            sortAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filtAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sort.setAdapter(sortAd);

            sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 1: sortByDate(); break;
                        case 2: getLocationForSort(); break;
                        case 3: sortAlphabetically(); break;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            seeOnly.setAdapter(filtAd);
            seeOnly.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 1: sev.getMeals(meals, adapter); setBackVals(); break;
                        case 2: filterByMyMeals(); break;
                        case 3: filterByHosting(); break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            final CheckBox halal = (CheckBox)dialog.findViewById(R.id.halal_filter);
            final CheckBox kosher = (CheckBox)dialog.findViewById(R.id.kosher_filter);
            final CheckBox vega = (CheckBox)dialog.findViewById(R.id.vegan_filter);
            final CheckBox veggi = (CheckBox)dialog.findViewById(R.id.vegetarian_filter);
            halal.setChecked(Halal);
            kosher.setChecked(Kosher);
            vega.setChecked(vegan);
            veggi.setChecked(veggie);


            Button filterBut = (Button)dialog.findViewById(R.id.filter_button);
            filterBut.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    Halal = halal.isChecked();
                    Kosher = kosher.isChecked();
                    vegan = vega.isChecked();
                    veggie = veggi.isChecked();
                    filter();
                    dialog.dismiss();
                }
            });


            dialog.show();
            }


        return super.onOptionsItemSelected(item);
    }


    /**
     * sorts the meals array alphabetically
     */
    private void sortAlphabetically(){
        Collections.sort(meals, new Comparator<Meal>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public int compare(Meal o1, Meal o2) {
                if (o1.getTitle() != null && o2.getTitle() != null){
                    return o1.getTitle().compareTo(o2.getTitle());
                } else if (o1.getTitle() == null){
                    return 1;
                } else {
                    return -1;
                }

            }
        });

        adapter.notifyDataSetChanged();
    }

    /**
     * Sorts the meals array by date
     */
    private void sortByDate(){
        Collections.sort(meals, new Comparator<Meal>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public int compare(Meal o1, Meal o2) {
                return compareByDate(o1, o2);
            }
        });

        adapter.notifyDataSetChanged();
    }

    /**
     * Date comparatore for two meals
     * @param m1 - meal 2
     * @param m2 - meal 2
     * @return time comparison result
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private int compareByDate(Meal m1, Meal m2){

        if (m1.getTime() != null && m2.getTime() != null) { // time is not a mandatory info, hence

            String date1 = m1.getTime().replace("אחה״צ", "PM")
                    .replace("לפנה״צ", "AM");

            String date2 = m2.getTime().replace("אחה״צ", "PM")
                    .replace("לפנה״צ", "AM"); // adjust to hebrew strings

            Date d1 = StringToDate(date1);

            Date d2 = StringToDate(date2);

            if (d1 != null && d2 != null) {

                return d1.compareTo(d2);
            }
        } else if (m1.getTime() == null){
            return 1;
        } else {
            return -1;
        }

        return 0;
    }


    /**
     * Converts string dates into date objects
     * @param d - string of date
     * @return - date object
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Date StringToDate(String d){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        try {
            return format.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sorts meals by distance to current location
     */
    private void sortByDistance(){

        Collections.sort(meals, new Comparator<Meal>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public int compare(Meal o1, Meal o2) {
                return compareByLoc(o1, o2);
            }
        });

        adapter.notifyDataSetChanged();

    }

    /**
     * Compares two meals for their proximity to current location
     * @param m1 - meal 1
     * @param m2 - meal 2
     * @return comparison's result
     */
    private int compareByLoc(Meal m1, Meal m2){
        if (m1.getLocation() != null && m2.getLocation() != null){
            Location l1 = sev.getLoc(m1.getLocation());
            Location l2 = sev.getLoc(m2.getLocation());

            return Float.compare(l1.distanceTo(curLoc), l2.distanceTo(curLoc));
        } else if (m1.getLocation() == null ){
            return 1;
        } else {
            return -1;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filterByDate(ArrayList<Meal> temp){
        ArrayList<Meal> newmeals = new ArrayList<>();
        if (timeFilter > 0){
            for (Meal meal : temp){
                if (meal.getTime() != null){
                    Date mDate = StringToDate(meal.getTime().replace("אחה״צ", "PM")
                            .replace("לפנה״צ", "AM"));
                    Date cur = Calendar.getInstance().getTime();
                    if (mDate != null) {
                        if (TimeUnit.MILLISECONDS.toDays(Math.abs(mDate.getTime() - cur.getTime())) <= timeFilter) {
                            newmeals.add(meal);
                        }
                    }
                }
            }
            temp.clear();
            temp.addAll(newmeals);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filterByHosting(){
        ArrayList<Meal> newmeals = new ArrayList<>();

        for (Meal meal : meals){
            if (meal.getHostId().equals(userId)){
                newmeals.add(meal);
            }
        }
        meals.clear();
        meals.addAll(newmeals);
        adapter.notifyDataSetChanged();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filterByMyMeals(){
        ArrayList<Meal> newmeals = new ArrayList<>();
        for (Meal meal : meals){
            for (String guest : meal.getGuests()){
                if (guest != null) {
                    if (guest.equals(userId)) {
                        newmeals.add(meal);
                    }
                }
            }
        }

        meals.clear();
        meals.addAll(newmeals);

        adapter.notifyDataSetChanged();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filterByRestrictions(ArrayList<Meal> temp){
        ArrayList<Meal> newmeals = new ArrayList<>();
        if (vegan || veggie || Halal || Kosher){
            for (Meal meal : temp){
                HashMap<String, Boolean> rests = new HashMap<>(meal.getRestrictions());
                if (rests.containsKey("Halal") && rests.containsKey("Kosher") &&
                        rests.containsKey("Vegan") && rests.containsKey("Vegetarian")  ){
                        if ((rests.get("Halal") == Halal) && rests.get("Kosher") == Kosher &&
                            rests.get("Vegan") == vegan && rests.get("Vegetarian") == veggie){
                            newmeals.add(meal);
                        }
                }
            }
            temp.clear();
            temp.addAll(newmeals);

        }
    }

    /* filters meals by distance */
    private void filterByDistance(){
        ArrayList<Meal> newmeals = new ArrayList<>();
        if (distanceFilter > 0){
            for (Meal meal : meals){
                if (meal.getLocation() != null){
                    Location loc = sev.getLoc(meal.getLocation());
                    if ((loc.distanceTo(curLoc) / 1000 )< distanceFilter){
                        newmeals.add(meal);
                    }
                }
            }
            meals.clear();
            meals.addAll(newmeals);
            adapter.notifyDataSetChanged();
        }
    }
    /* gets the devices location for filtering */
    void getLocationForFilter() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request_permission();
            }
        } else {
            // permission has been granted
            locationManager.requestLocationUpdates("gps", 5000, 0, filterLocListener);
        }
    }

    /* filters results */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void filter(){
        ArrayList<Meal> newmeals = new ArrayList<>(meals);

        filterByRestrictions(newmeals);
        filterByDate(newmeals);

        meals.clear();
        meals.addAll(newmeals);
        if(distanceFilter > 0) {
            getLocationForFilter();
        }

    }
    /* sets all filtering values back to default */
    private void setBackVals(){
        Kosher = false;
        Halal = false;
        veggie = false;
        vegan = false;
        distanceFilter = 0;
        timeFilter = 0;
    }
}
