package com.example.hoster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;



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



        private void setButCol(Meal meal){
            if (meal.isMember(MainActivity.userId)){
                joinBU.setText("Leave");

                joinBU.setBackgroundColor(Color.GRAY);
            } else if (meal.isFull()){ // meal is full
                joinBU.setText("Full");
                joinBU.setBackgroundColor(Color.GRAY);
            } else {
                joinBU.setText("Join Meal");
                joinBU.setTextColor(Color.WHITE);
                joinBU.setBackgroundResource(R.drawable.rounded_button);
            }
        }
    }

    /**
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
        String date = getItem(position).getTime();
        String description = getItem(position).getDescription();
        String host = getItem(position).getHostId();



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

            result = convertView;

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.title.setText(title);
        holder.date.setText(date);
        holder.description.setText(description);
        holder.host.setText(host);

        if (getItem(position).getHostId().equals(MainActivity.userId)) { // todo - find another way to get your user id
            holder.host.setText("You");
        } else {
            Server.getInstance().getUsername(getItem(position).getHostId(), holder.host);
        }

        holder.setButCol(getItem(position));

        holder.joinBU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).isMember(MainActivity.userId)){

                    Server.getInstance().removeUserToMeal(MainActivity.userId, getItem(position).getID(),
                            getItem(position).getHostId());
                    getItem(position).removeGuest(MainActivity.userId);
                }  else if (!getItem(position).isFull()) { // not in meal and meal not full

                    MainActivity.sev.addUserToMeal(getContext(), MainActivity.userId, getItem(position));
                    getItem(position).addGuest(MainActivity.userId);
                }
                holder.setButCol(getItem(position));

                Server.getInstance().getMeals(MainActivity.meals, MainActivity.adapter);
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

