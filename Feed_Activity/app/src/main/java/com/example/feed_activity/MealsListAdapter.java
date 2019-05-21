package com.example.feed_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.validation.TypeInfoProvider;



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
        String host = Server.getInstance().getUser(getItem(position).getHostId()).getUsername();

        if (getItem(position).getHostId().equals(MainActivity.user.getUid())) {
            host = "You";
        }

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

        if (getItem(position).isMember(MainActivity.user.getUid())){
            holder.joinBU.setText("Leave");
            holder.joinBU.setBackgroundColor(Color.GRAY);
        } else if (getItem(position).isFull()){ // meal is full
            holder.joinBU.setText("Full");
            holder.joinBU.setBackgroundColor(Color.GRAY);
        } else {
            holder.joinBU.setText("Join Meal");
            holder.joinBU.setTextColor(Color.WHITE);
            holder.joinBU.setBackgroundResource(R.color.colorAccent);
        }

        holder.joinBU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).isMember(MainActivity.user.getUid())){

                    Server.getInstance().removeUserToMeal(MainActivity.user.getUid(), getItem(position).getID());

                }  else if (!getItem(position).isFull()) { // not in meal and meal not full

                    MainActivity.sev.addUserToMeal(MainActivity.user.getUid(), getItem(position).getID());
                }
                MainActivity.adapter.notifyDataSetChanged();
            }
        });

        holder.title.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MealActivity.class);
                Bundle b = new Bundle();
                b.putInt("mealId", getItem(position).getID()); //Your id
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

