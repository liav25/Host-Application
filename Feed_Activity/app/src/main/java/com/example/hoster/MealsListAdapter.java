package com.example.hoster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.rpc.Help;

import java.util.ArrayList;
import java.util.Map;

/**
 * Adapter for the meals list (in the feed)
 */
class MealsListAdapter extends ArrayAdapter<Meal> {

    private Context mContext;
    int mResource;
    private int lastPosition = -1;

    /* holds variable in a View
     */

    private static class ViewHolder {
        TextView title;
        TextView description;
        TextView date;
        TextView host;
        Button joinBU;
        ImageView img1;
        ImageView img2;
        TextView textView;
        ImageView img3;
        ImageView img4;
        ImageView img5;
        ImageView img6;
        ImageView kosherSymbol;
        ImageView halalSymbol;
        ImageView veggieSymbol;
        ImageView veganSymbol;
        LinearLayout kosherLayout;
        LinearLayout veggieLayout;
        LinearLayout veganLayout;
        LinearLayout halalLayout;
        LinearLayout seperator;
        CardView cardView;



        /* sets the join button's color according to eh user's status*/
        private void setButCol(Meal meal){
            if (meal.isMember(MainActivity.userId)){
                joinBU.setText("Leave");

                joinBU.setBackgroundResource(R.drawable.rounded_button_gray);
            } else if (meal.isFull()){ // meal is full
                joinBU.setText("Full");
                joinBU.setBackgroundResource(R.drawable.rounded_button_gray);
            } else {
                joinBU.setText("Join Meal");
                joinBU.setTextColor(Color.WHITE);
                joinBU.setBackgroundResource(R.drawable.rounded_button);
            }
        }
        /* sets the symbols of the resrtictions */
        private void setSymbols(final Map<String, Boolean> restrictions, final Context mContext){
            RelativeLayout.LayoutParams kosherParams = (RelativeLayout.LayoutParams)kosherLayout.getLayoutParams();
            RelativeLayout.LayoutParams vegParams = (RelativeLayout.LayoutParams)veggieLayout.getLayoutParams();
            RelativeLayout.LayoutParams veganParams = (RelativeLayout.LayoutParams)veganLayout.getLayoutParams();
            RelativeLayout.LayoutParams halalParams = (RelativeLayout.LayoutParams)halalLayout.getLayoutParams();
            RelativeLayout.LayoutParams seperatorParams = (RelativeLayout.LayoutParams)seperator.getLayoutParams();
            kosherParams.addRule(RelativeLayout.LEFT_OF, R.id.seperator);
            vegParams.addRule(RelativeLayout.LEFT_OF, R.id.kosher_layout);
            veganParams.addRule(RelativeLayout.LEFT_OF, R.id.veg_layout);
            halalParams.addRule(RelativeLayout.LEFT_OF, R.id.vegan_layout);

            try {
                if (restrictions.get("Kosher")) {
                    kosherSymbol.setVisibility(View.VISIBLE);}
                else {
                    kosherSymbol.setVisibility(View.INVISIBLE);
                }

                if (restrictions.get("Halal")) {
                    halalSymbol.setVisibility(View.VISIBLE);
                }
                else {halalSymbol.setVisibility(View.INVISIBLE);}

                if (restrictions.get("Vegetarian")) {
                    veggieSymbol.setVisibility(View.VISIBLE);
                }
                else {veggieSymbol.setVisibility(View.INVISIBLE);}

                if (restrictions.get("Vegan")) {
                    veganSymbol.setVisibility(View.VISIBLE);
                }
                else {veganSymbol.setVisibility(View.INVISIBLE);}
            }


            catch (NullPointerException e){}

            if(!restrictions.get("Vegan")){
                halalParams.addRule(RelativeLayout.LEFT_OF, R.id.veg_layout);
                if(!restrictions.get("Vegetarian")){
                    halalParams.addRule(RelativeLayout.LEFT_OF, R.id.kosher_layout);
                    if(!restrictions.get("Kosher")){
                        halalParams.addRule(RelativeLayout.LEFT_OF, R.id.seperator);
                    }
                }
            }
            if(!restrictions.get("Vegetarian")){
                veganParams.addRule(RelativeLayout.LEFT_OF, R.id.kosher_layout);
                if(!restrictions.get("Kosher")){
                    veganParams.addRule(RelativeLayout.LEFT_OF, R.id.seperator);
                }
            }
            if(!restrictions.get("Kosher")){
                vegParams.addRule(RelativeLayout.LEFT_OF, R.id.seperator);
            }
        }





        /* sets the profile pictures within the meal cards*/
        private void setImages(final ArrayList<String> guests, final Context mContext){
            if (guests.size() >= 6){
                img6.setVisibility(View.VISIBLE);
                img6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b = new Bundle();
                        b.putStringArrayList("user_ids", guests); //Your id
                        Intent more_profiles = new Intent(mContext, see_all_people_activity.class);
                        more_profiles.putExtras(b); //Put your id to your next Intent
                        mContext.startActivity(more_profiles);
                    }
                });
            } else {
                img6.setVisibility(View.INVISIBLE);
            }

            if (guests.size() >= 5){
                img5.setVisibility(View.VISIBLE);

                Server.getInstance().downloadProfilePic(img5, guests.get(4));
                img5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(guests.get(4), mContext);
                    }
                });
            }else {
                img5.setVisibility(View.INVISIBLE);
            }

            if (guests.size() >= 4){
                img4.setVisibility(View.VISIBLE);

                Server.getInstance().downloadProfilePic(img4, guests.get(3));
                img4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(guests.get(3), mContext);
                    }
                });
            }else {
                img4.setVisibility(View.INVISIBLE);
            }

            if (guests.size() >= 3){
                img3.setVisibility(View.VISIBLE);

                Server.getInstance().downloadProfilePic(img3, guests.get(2));
                img3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(guests.get(2), mContext);
                    }
                });
            }else {
                img3.setVisibility(View.INVISIBLE);
            }

            if (guests.size() >= 2){
                img2.setVisibility(View.VISIBLE);

                Server.getInstance().downloadProfilePic(img2, guests.get(1));
                img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(guests.get(1), mContext);
                    }
                });
            }else {
                img2.setVisibility(View.INVISIBLE);
            }

            if (guests.size() >= 1){
                Server.getInstance().downloadProfilePic(img1, guests.get(0));
                img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickImage(guests.get(0), mContext);
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
    }

    /**
     * cosntructor
     * @param context
     * @param resource
     * @param objects
     */
    public MealsListAdapter(Context context, int resource, ArrayList<Meal> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        String title = getItem(position).getTitle();
        final String date = getItem(position).getTime();
        String description = getItem(position).getDescription();
        String host = getItem(position).getHostId();
        ArrayList<String> guests = getItem(position).getGuests();
        Map<String, Boolean> restrictions = getItem(position).getRestrictions();


        //create the view result for showing animation
        final View result;

        //viewholder object
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.card_title);
            holder.date = (TextView) convertView.findViewById(R.id.card_date);
            holder.description = (TextView) convertView.findViewById(R.id.card_description);
            holder.host = (TextView) convertView.findViewById(R.id.author);
            holder.joinBU  = (Button)  convertView.findViewById(R.id.JoinButton);
            holder.img1 = convertView.findViewById(R.id.student1);
            holder.img2 = convertView.findViewById(R.id.student2);
            holder.img3 = convertView.findViewById(R.id.student3);
            holder.img4 = convertView.findViewById(R.id.student4);
            holder.img5 = convertView.findViewById(R.id.student5);
            holder.img6 = convertView.findViewById(R.id.see_more_in_card);
            holder.kosherSymbol = convertView.findViewById(R.id.kosher_symbol);
            holder.halalSymbol = convertView.findViewById(R.id.halal_symbol);
            holder.veganSymbol = convertView.findViewById(R.id.vegan_symbol);
            holder.veggieSymbol = convertView.findViewById(R.id.veg_symbol);
            holder.kosherLayout = (LinearLayout)convertView.findViewById(R.id.kosher_layout);
            holder.veganLayout = (LinearLayout)convertView.findViewById(R.id.vegan_layout);
            holder.veggieLayout = (LinearLayout)convertView.findViewById(R.id.veg_layout);
            holder.halalLayout = (LinearLayout)convertView.findViewById(R.id.halal_layout);
            holder.seperator = (LinearLayout) convertView.findViewById(R.id.seperator);
            holder.textView = (TextView) convertView.findViewById(R.id.helper);
            holder.cardView = (CardView) convertView.findViewById(R.id.cardview);


            result = convertView;

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.title.setText(title);
        holder.date.setText(date);
        holder.description.setText(description);
        //Tomi why this isnt working ?????
        //shows only host and not arranged by + host


        holder.setImages(guests, mContext);
        holder.setSymbols(restrictions, mContext);

        if (getItem(position).getHostId().equals(MainActivity.userId)) {
            holder.host.setText("Arranged by You");
        } else {
            Server.getInstance().getUsername(getItem(position).getHostId(), holder.host);
        }

        holder.setButCol(getItem(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MealActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("meal", getItem(position));
                b.putString("mealId", getItem(position).getID()); //Your id
                intent.putExtras(b); //Put your id to your next Intent

                mContext.startActivity(intent);
            }
        });

        holder.joinBU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).isMember(MainActivity.userId)){

                    Server.getInstance().removeUserToMeal(MainActivity.userId, getItem(position).getID(),
                            getItem(position).getHostId(), getContext(), MainActivity.userMail);
                    getItem(position).removeGuest(MainActivity.userId);
                }  else if (!getItem(position).isFull()) { // not in meal and meal not full

                    MainActivity.sev.addUserToMeal(getContext(), MainActivity.userId, getItem(position),
                            MainActivity.userMail);
                    getItem(position).addGuest(MainActivity.userId);
                }
                holder.setButCol(getItem(position));

                //TODO - needed pop
                if (getItem(position).getNeeded().containsValue("NEEDED") &&
                        getItem(position).isMember(MainActivity.userId)){
                    Intent neededPopup = new Intent(mContext, neededDialog.class);
                    Bundle b = new Bundle();
                    b.putSerializable("meal", getItem(position));
                    String uId = MainActivity.sev.mAuth.getCurrentUser().getUid();
                    b.putString("userId", uId);
                    neededPopup.putExtras(b);
                    mContext.startActivity(neededPopup);

                }

                Server.getInstance().getMeals(MainActivity.meals, MainActivity.adapter);
                holder.setImages(getItem(position).getGuests(), mContext);
                holder.setSymbols(getItem(position).getRestrictions(), mContext);
            }
        });

        holder.title.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MealActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("meal", getItem(position));
                b.putString("mealId", getItem(position).getID()); //Your id
                intent.putExtras(b); //Put your id to your next Intent

                mContext.startActivity(intent);
            }
        });


        holder.host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = new Bundle();
                b.putString("userId", getItem(position).getHostId()); //Your id
                Intent profileIntent = new Intent(mContext, Profile.class);
                profileIntent.putExtras(b); //Put your id to your next Intent
                mContext.startActivity(profileIntent);

            }
        });
        return result;
    }





}

