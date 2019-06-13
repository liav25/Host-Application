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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class peopleListAdapter  extends ArrayAdapter<String>{

    private Context mContext;
    int mResource;
    private int lastPosition = -1;

    /**
     * @param context
     * @param resource
     * @param objects
     */
    public peopleListAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    /* holds variable in a View
     */

    private static class ViewHolder {
        TextView name;
        ImageView profile_pic;


        private void setImage(final String uId, final Context mContext){

            Server.getInstance().downloadProfilePic(profile_pic, uId);
            Server.getInstance().getUsername(uId, name);

            profile_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickImage(uId, mContext);
                }
            });

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickImage(uId, mContext);
                }
            });
        }

        private void onClickImage(String uId, Context mContext){

            Bundle b = new Bundle();
            b.putString("userId", uId); //Your id
            Intent profileIntent = new Intent(mContext, Profile.class);
            profileIntent.putExtras(b); //Put your id to your next Intent
            mContext.startActivity(profileIntent);

        }

    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {


        //create the view result for showing animation
        final View result;

        //viewholder object
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.profile_name);
            holder.profile_pic = (CircleImageView) convertView.findViewById(R.id.profile_pic_person);
            result = convertView;

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        holder.setImage(getItem(position), mContext);

        return result;
    }
}


