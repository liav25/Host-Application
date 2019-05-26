package com.example.hoster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ProfilePicAdapter extends ArrayAdapter<String> {

    private Context mContext;
    int mResource;


    /* holds variable in a View
     */

    private static class ViewHolder {
        ImageView img;


    }

    /**
     * @param context
     * @param resource
     * @param objects
     */
    public ProfilePicAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        String uId = getItem(position);

        //create the view result for showing animation
        final View result;

        //viewholder object
        final ProfilePicAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ProfilePicAdapter.ViewHolder();

            result = convertView;

            convertView.setTag(holder);

        } else {
            holder = (ProfilePicAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }
        holder.img = convertView.findViewById(R.id.profile_pic);

        Server.getInstance().downloadProfilePic(holder.img, uId);


        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = new Bundle();
                b.putString("userId", getItem(position)); //Your id
                Intent profileIntent = new Intent(mContext, Profile.class);
                profileIntent.putExtras(b); //Put your id to your next Intent
                mContext.startActivity(profileIntent);

            }
        });
        return result;
    }



}
