package com.example.hoster;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Integer> arrayList = new ArrayList<Integer>();

    public DataAdapter(Context context, ArrayList<Integer> imagesArray) {
        this.context = context;
        arrayList = imagesArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imageLayout = LayoutInflater.from(context).inflate(R.layout.profile_layout, parent, false);
        return new ViewHolder(imageLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.imageView.setImageResource(arrayList.get(position));

        if (position == 3) {
            holder.relativeLayout.setVisibility(View.VISIBLE);
            holder.tvCount.setText(String.valueOf(arrayList.size()-4));
        }else {
            holder.relativeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        TextView tvCount;
        CircleImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relative);
            tvCount = itemView.findViewById(R.id.tvCount);
            imageView = itemView
                    .findViewById(R.id.profile_image);


        }
    }
}